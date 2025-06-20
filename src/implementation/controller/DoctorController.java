package implementation.controller;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Scanner;

import adt.Map;
import implementation.model.Doctor;
import implementation.model.Specialty;
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
                Input _doctorSpecialtyId = new Input(scanner, "Enter doctor's department/specialty (in ID): ")
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
            UserInterface.info("Doctor details: ");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(foundDoctor);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void findDoctorsByName(Scanner scanner) {
        while (true) {
            UserInterface.update("Find Doctor(s) by Name");
            System.out.println("*) Enter 0 to exit\n");


            Input _doctorName = new Input(scanner, "Enter doctor name: ")
                                    .isNotEmpty().isAlphabetic().validate();
            if (_doctorName.isExit()) return;

            Object[] doctors = DoctorRepository.findAll(d -> d.getName().toLowerCase().contains(_doctorName.get().toString().toLowerCase()));

            System.out.println();
            UserInterface.info("Result: ");

            if (doctors.length == 0) {
                System.out.println("No doctor found with that name.");
            } else {
                System.out.println("╔════════════════════════════════════════════════");
                for (Object obj : doctors) {
                    Doctor doctor = (Doctor) obj;
                    System.out.println(doctor);
                }
            }

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void findDoctorsInCurrentSpecialty(Scanner scanner, int specialtyId) {
        UserInterface.update("View Doctors in My Specialty");

        Object[] doctors = DoctorRepository.findAll(d -> d.getSpecialtyId() == specialtyId);

        if (doctors.length == 0) {
            System.out.println("No doctor available.");
        } else {
            System.out.println("╔════════════════════════════════════════════════");
            for (Object obj : doctors) {
                Doctor doctor = (Doctor) obj;
                System.out.println(doctor);
            }
        }

        System.out.println();
        UserInterface.enter(scanner);
    }

    public static void findDoctorsBySpecialty(Scanner scanner) {
        while (true) {
            UserInterface.update("Find Doctor(s) by Specialty");
            System.out.println("*) Enter 0 to exit\n");

            Specialty specialty = null;
            while (specialty == null) {
                Input _specialtyId = new Input(scanner, "Enter specialty ID (in number): ")
                                        .isNotEmpty().isNumeric().validate();
                if (_specialtyId.isExit()) return;
                int specialtyId = _specialtyId.getInteger();
                specialty = SpecialtyRepository.findById(specialtyId);

                if (specialty == null) {
                    UserInterface.warning("Can't find a specialty with this ID!");
                }
            }
            final int getSpecialtyId = specialty.getId();

            Object[] doctors = DoctorRepository.findAll(d -> d.getSpecialtyId() == getSpecialtyId);

            System.out.println();
            UserInterface.info("Doctor(s) in " + UserInterface.colorize(specialty.getName(),UserInterface.YELLOW) + ": ");

            if (doctors.length == 0) {
                System.out.println("No doctor available.");
            } else {
                System.out.println("╔════════════════════════════════════════════════");
                for (Object obj : doctors) {
                    Doctor doctor = (Doctor) obj;
                    System.out.println(doctor);
                }
            }

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void removeDoctor(Scanner scanner) {
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

            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(doctor);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void viewDoctors(Scanner scanner) {
        UserInterface.update("View All Doctors");

        if (DoctorRepository.getRepositorySize() == 0) {
            System.out.println("No doctors available");
        } else {
            System.out.println("╔════════════════════════════════════════════════");
            DoctorRepository.getAll();
        }

        UserInterface.enter(scanner);
        return;
    }

    public static void editProfile(Scanner scanner, Doctor doctorProfile) {
        while (true) {
            UserInterface.update("Edit Profile");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(doctorProfile);
            System.out.println();
            String[] options = {
                "Edit Name",
                "Edit Phone Number",
                "Change Password\n",
            };
            UserInterface.createOptions(options);

            System.out.println();
            Input _editProfileChoice = new Input(scanner, "Enter choice: ")
                                            .isNotEmpty().validate();
            if (_editProfileChoice.isExit()) return;
            String editProfileChoice = _editProfileChoice.get();

            switchLoop: switch (editProfileChoice) {
                case "1":
                    Input _editName = new Input(scanner, "Enter new name: ")
                                            .isNotEmpty().isAlphabetic().validate();
                    if (_editName.isExit()) break;
                    String newName = _editName.get();
                    doctorProfile.setName(newName);
                    DoctorRepository.modifyFile(doctorProfile.getId(), d -> { d.setName(newName); return d;});
                    UserInterface.success("Name successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "2":
                    Input _editPhoneNumber = new Input(scanner, "Enter new phone number: ")
                                            .isNotEmpty().isValidPhoneNumber().validate();
                    if (_editPhoneNumber.isExit()) break;
                    String newPhoneNumber = _editPhoneNumber.get();
                    doctorProfile.setPhoneNumber(newPhoneNumber);
                    DoctorRepository.modifyFile(doctorProfile.getId(), d -> { d.setPhoneNumber(newPhoneNumber); return d;});
                    UserInterface.success("Phone number successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "3":
                    while (true) {
                        Input _currentPassword = new Input(scanner, "Enter your current password: ")
                                                .isNotEmpty().validate();
                        if (_currentPassword.isExit()) break switchLoop;
                        if (!doctorProfile.validatePassword(_currentPassword.get())) {
                            UserInterface.warning("Invalid password!");
                        } else break;
                    }
                    Input _changePassword = new Input(scanner, "Enter new password (must be alphanumeric): ")
                                                .isNotEmpty().isAlphanumeric().validate();
                    if (_changePassword.isExit()) break;
                    String newPassword = _changePassword.get();
                    doctorProfile.setPassword(newPassword);
                    DoctorRepository.modifyFile(doctorProfile.getId(), d -> { d.setPassword(newPassword); return d;});
                    UserInterface.success("Password successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                default:
                    UserInterface.warning("Invalid choice!");
                    UserInterface.enter(scanner);
            }
        }
    }
}
