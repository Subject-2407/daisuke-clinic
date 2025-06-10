package implementation.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Scanner;

import adt.Map;
import implementation.model.Doctor;
import implementation.model.WorkingHours;
import shared.repository.DoctorRepository;
import shared.repository.SpecialtyRepository;
import utility.Hasher;
import utility.Input;
import utility.UserInterface;

public class DoctorController {
    public static void addDoctor(Scanner scanner) {
        while (true) {
            UserInterface.update("Add a Doctor");
            System.out.println("*) Enter 0 to exit\n");

            int doctorId = -1, doctorSpecialtyId = -1;
            String doctorPassword, doctorName, doctorPhoneNumber;
            Map<DayOfWeek, WorkingHours> doctorWorkingSchedule = new Map<>();

            // doctor id input
            while (doctorId == -1) {
                Input _doctorId = new Input(scanner, "Enter new doctor's ID (in number): ")
                                    .isNotEmpty().isNumeric().isPositiveNumber().validate();
                if (_doctorId.isExit()) return;
                doctorId = _doctorId.getInteger();

                // check if doctor id already exists
                if (DoctorRepository.findById(doctorId) != null) {
                    UserInterface.warning("This doctor ID is already used!");
                    doctorId = -1;
                }
            }

            // doctor name input
            Input _doctorName = new Input(scanner, "Enter doctor's name: ")
                                    .isNotEmpty().isAlphabetic().validate();
            if (_doctorName.isExit()) return;
            doctorName = _doctorName.get();

            // doctor specialty input
            while (doctorSpecialtyId == -1) {
                Input _doctorSpecialtyId = new Input(scanner, "Enter doctor's specialty (in ID): ")
                                                .isNotEmpty().isNumeric().validate();
                if (_doctorSpecialtyId.isExit()) return;
                doctorSpecialtyId = _doctorSpecialtyId.getInteger();

                // if specialty does not exist
                if (SpecialtyRepository.findById(doctorSpecialtyId) == null) {
                    UserInterface.warning("Specialty not found!");
                }
            }

            // doctor phone number input
            Input _doctorPhoneNumber = new Input(scanner, "Enter doctor's phone number: ")
                                            .isNotEmpty().isValidPhoneNumber().validate();
            if (_doctorPhoneNumber.isExit()) return;
            doctorPhoneNumber = _doctorPhoneNumber.get();

            // doctor password input
            Input _doctorPassword = new Input(scanner, "Enter doctor's new password (alphanumeric): ")
                                        .isNotEmpty().isAlphanumeric().validate();
            if (_doctorPassword.isExit()) return;
            doctorPassword = Hasher.hash(_doctorPassword.get());

            System.out.println();

            // doctor work schedule
            System.out.println("Choose schedule type for doctor: ");
            System.out.println("1. 5-day workweek (Mon-Fri 08:00 - 17:00)");
            System.out.println("2. Custom working days and hours");
            scheduleLoop: while (true) {
                Input scheduleType = new Input(scanner, "Enter choice: ")
                                        .isNotEmpty().isNumeric().validate();
                if (scheduleType.isExit()) return;
                int type = scheduleType.getInteger();

                if (type == 1) {
                    doctorWorkingSchedule.put(DayOfWeek.MONDAY, new WorkingHours(LocalTime.of(8, 0), LocalTime.of(17, 0)));
                    doctorWorkingSchedule.put(DayOfWeek.TUESDAY, new WorkingHours(LocalTime.of(8, 0), LocalTime.of(17, 0)));
                    doctorWorkingSchedule.put(DayOfWeek.WEDNESDAY, new WorkingHours(LocalTime.of(8, 0), LocalTime.of(17, 0)));
                    doctorWorkingSchedule.put(DayOfWeek.THURSDAY, new WorkingHours(LocalTime.of(8, 0), LocalTime.of(17, 0)));
                    doctorWorkingSchedule.put(DayOfWeek.FRIDAY, new WorkingHours(LocalTime.of(8, 0), LocalTime.of(17, 0)));
                    break;
                } else if (type == 2) {
                    System.out.println();
                    boolean hasAtleastOneWorkingDay = false;
                    while (!hasAtleastOneWorkingDay) {
                        for (DayOfWeek day : DayOfWeek.values()) {
                            while (true) {
                                String dayString = day.toString();
                                String capitalizedDay = dayString.substring(0, 1).toUpperCase() + dayString.substring(1).toLowerCase();
                                Input hours = new Input(scanner, "Enter working hours for " + capitalizedDay + " (format: HH:MM-HH:MM, type 'n' for off-day): ").isNotEmpty().validate();
                                if (hours.isExit()) return;
                                String hoursInput = hours.get();
                                if (hoursInput.toLowerCase().equals("n")) {
                                    break;
                                } else {
                                    try {
                                        String[] times = hoursInput.split("-");
                                        LocalTime startTime = LocalTime.parse(times[0]);
                                        LocalTime endTime = LocalTime.parse(times[1]);
                                        doctorWorkingSchedule.put(day, new WorkingHours(startTime, endTime));
                                        hasAtleastOneWorkingDay = true;
                                        break;
                                    } catch (Exception e) {
                                        UserInterface.warning("Invalid format! Example: 08:00-17:00");
                                    }
                                }
                            }
                            
                        }

                        if (!hasAtleastOneWorkingDay) {
                            UserInterface.warning("You must enter working hours for at least one day!");
                        } else break scheduleLoop;
                    }
                } else {
                    UserInterface.warning("Invalid choice!");
                }
            }

            Doctor newDoctor = new Doctor(doctorId, doctorPassword, doctorName, doctorSpecialtyId, doctorPhoneNumber);
            newDoctor.setWorkSchedule(doctorWorkingSchedule);
            DoctorRepository.add(newDoctor);

            System.out.println();

            UserInterface.success("Successfully added new doctor!");
            UserInterface.enter(scanner);
        }
    }

    public static void findDoctor(Scanner scanner) {
        reloadRepository();
        while (true) {
            UserInterface.update("Find a Doctor by ID");
            System.out.println("*) Enter 0 to exit\n");

            Doctor foundDoctor = null;

            // doctor id input
            while (foundDoctor == null) {
                Input _doctorId = new Input(scanner, "Enter doctor's ID (in number): ")
                                    .isNotEmpty().isNumeric().validate();
                if (_doctorId.isExit()) return;
                int doctorId = _doctorId.getInteger();
                foundDoctor = DoctorRepository.findById(doctorId);

                if (foundDoctor == null) {
                    UserInterface.warning("Can't find a doctor with this ID!");
                }
            }

            System.out.println();
            System.out.println(foundDoctor);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void removeDoctor(Scanner scanner) {
        reloadRepository();
        while (true) {
            UserInterface.update("Remove a Doctor");
            System.out.println("*) Enter 0 to exit\n");

            int doctorId = -1;
            Doctor doctor = null;

            // doctor id input
            while (doctorId == -1) {
                Input _doctorId = new Input(scanner, "Enter doctor's ID (in number): ")
                                    .isNotEmpty().isNumeric().validate();
                if (_doctorId.isExit()) return;
                doctorId = _doctorId.getInteger();
                doctor = DoctorRepository.findById(doctorId);

                if (doctor == null) {
                    UserInterface.warning("Can't find a doctor with this ID!");
                    doctorId = -1;
                }
            }

            DoctorRepository.remove(doctorId);

            System.out.println();

            UserInterface.success("Successfully removed doctor " + UserInterface.colorize("#" + doctorId, UserInterface.YELLOW) + "!");
            UserInterface.info("Removed doctor details: ");

            System.out.println(doctor);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void viewDoctors(Scanner scanner) {
        reloadRepository();
        UserInterface.update("View All Doctors");

        DoctorRepository.getAll();

        UserInterface.enter(scanner);
        return;
    }

    private static void reloadRepository() {
        try {
            DoctorRepository.load(); // reload repository
        } catch (IOException e) {
            System.err.println("Failed to load doctor data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
