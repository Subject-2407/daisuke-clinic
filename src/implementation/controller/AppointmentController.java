package implementation.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import adt.BST;
import adt.PriorityQueue;
import implementation.model.Appointment;
import implementation.model.Doctor;
import implementation.model.Specialty;
import implementation.model.enums.AppointmentStatus;
import shared.LoginState;
import shared.repository.AppointmentRepository;
import shared.repository.DoctorRepository;
import shared.repository.SpecialtyRepository;
import utility.Input;
import utility.UserInterface;

public class AppointmentController {
    public static void addAppointment(Scanner scanner) {
        while (true) {
            UserInterface.update("Book an Appointment");
            System.out.println("*) Enter 0 to exit");
            System.out.println("*) Only one appointment per specialty allowed.\n");

            Doctor doctor = null;
            LocalDateTime time;
            int patientId = LoginState.getLoginId();

            // doctor id input
            while (doctor == null) {
                Input _doctorId = new Input(scanner, "Enter doctor's ID (in number): ")
                                        .isNotEmpty().isNumeric().validate();
                if (_doctorId.isExit()) return;
                int doctorId = _doctorId.getInteger();
                doctor = DoctorRepository.findById(doctorId);

                if (doctor == null) {
                    UserInterface.warning("Can't find a doctor with this ID!");
                    doctorId = -1;
                }
            }

            // show doctor details and schedule

            System.out.println("╔════════════════════════════════════════════════");
            UserInterface.info("Doctor details: ");
            System.out.println(doctor);

            // validate specialty
            Specialty specialty = SpecialtyRepository.findById(doctor.getSpecialtyId());

            if (specialty == null) {
                UserInterface.warning("Could not proceed with appointment booking because doctor's specialty is unknown. Please contact the admin for support.");
                UserInterface.enter(scanner);
                continue;
            } else {
                // check if patient already have one appointment in the specialty
                Object patientAppointments = specialty.getAppointmentQueue().find(a -> a.getPatientId() == patientId);
                if (patientAppointments != null) {
                    UserInterface.warning("You already have a scheduled appointment in the " + UserInterface.colorize(specialty.getName() + "!", UserInterface.YELLOW));
                    UserInterface.enter(scanner);
                    continue;
                }

                // check if specialty appointment queue is full
                if (specialty.getAppointmentQueue().size() == specialty.getMaxSlots()) {
                    UserInterface.warning("The appointment queue in this specialty is currently full!");
                    UserInterface.enter(scanner);
                    continue;
                }
            }

            // appointment time input
            while (true) {
                Input _appointmentTime = new Input(scanner, "Schedule your appointment (must be within the doctor's schedule; format: DD-MM-YYYY HH:MM): ")
                                            .isNotEmpty().validate();
                if (_appointmentTime.isExit()) return;
                String appointmentTime = _appointmentTime.getString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

                try {
                    LocalDateTime parsedTime = LocalDateTime.parse(appointmentTime, formatter);
                    if (parsedTime.isBefore(LocalDateTime.now())) {
                        UserInterface.warning("What are you, a time traveller?");
                    } else if (doctor.getWorkSchedule().containsKey(parsedTime.getDayOfWeek())) {
                        LocalTime startTime = doctor.getWorkSchedule().get(parsedTime.getDayOfWeek()).getStartTime();
                        LocalTime endTime = doctor.getWorkSchedule().get(parsedTime.getDayOfWeek()).getEndTime();
                        LocalTime targetTime = parsedTime.toLocalTime();

                        if (!targetTime.isBefore(startTime) && !targetTime.isAfter(endTime)) {
                            time = parsedTime;
                            break;
                        } else {
                            UserInterface.warning("Selected time must be within the doctor's working hours!");
                        }
                    } else {
                        UserInterface.warning("Selected time must be within the doctor's schedule!");
                    }
                } catch (DateTimeParseException e) {
                    UserInterface.warning("Invalid format! Example: 07-12-2015 11:00");
                }
            }

            int appointmentNumber = AppointmentRepository.getAppointmentSize() + 1;
            int specialtyId = doctor.getSpecialtyId();

            Appointment newAppointment = new Appointment(appointmentNumber, specialtyId, patientId, doctor.getId(), time);
            AppointmentRepository.add(newAppointment);

            System.out.println();

            UserInterface.success("Successfully booked an appointment!");
            UserInterface.enter(scanner);
        }
    }

    public static void cancelAppointment(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments, BST<Appointment> profileAppointmentHistory) {
        while (true) {
            UserInterface.update("Cancel an Appointment");
            System.out.println("*) Enter 0 to exit\n");

            Appointment appointment = null;

            while (appointment == null) {
                Input _appointmentId = new Input(scanner, "Enter your appointment ID (in number): ")
                                            .isNotEmpty().isNumeric().validate();
                if (_appointmentId.isExit()) return;
                int appointmentId = _appointmentId.getInteger();
                appointment = (Appointment) profileUpcomingAppointments.find(a -> a.getId() == appointmentId);

                if (appointment == null) {
                    UserInterface.warning("Can't find an appointment with this ID in your upcoming appointments!");
                }
            }

            final int appointmentId = appointment.getId();

            System.out.println();
            UserInterface.info("Appointment details:");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(appointment);

            System.out.println();
            while (true) {
                Input confirmationInput = new Input(scanner, "Are you sure you want to cancel the appointment? (Y/N): ")
                                    .isNotEmpty().validate();
                String confirmation = confirmationInput.get();
                if (confirmation.toUpperCase().equals("Y")) {
                    if (profileUpcomingAppointments.removeIf(a -> a.getId() == appointmentId) != null) {
                        appointment.setStatus(AppointmentStatus.CANCELLED);
                        AppointmentRepository.updateStatusInFile(appointmentId, AppointmentStatus.CANCELLED);

                        Specialty specialty = SpecialtyRepository.findById(appointment.getSpecialtyId());
                        if (specialty != null) {
                            specialty.getAppointmentQueue().removeIf(a -> a.getId() == appointmentId);
                        }

                        Doctor doctor = DoctorRepository.findById(appointment.getDoctorId());
                        if (doctor != null) {
                            doctor.getUpcomingAppointments().removeIf(a -> a.getId() == appointmentId);
                            doctor.addAppointmentHistory(appointment);
                        }

                        profileAppointmentHistory.insert(appointment);
                        
                        System.out.println();
                        UserInterface.info("Succesfully cancelled appointment " + UserInterface.colorize("#" + appointmentId, UserInterface.YELLOW));
                        UserInterface.enter(scanner);
                        break;
                    } else {
                        UserInterface.warning("System error! Please contact the admin for support.");
                        UserInterface.enter(scanner);
                        break;
                    }
                } else if (confirmation.toUpperCase().equals("N")) {
                    break;
                } else {
                    UserInterface.warning("Invalid input!");
                }
            }
        }
    }

    public static void getPatientNextAppointment(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments) {
        UserInterface.update("View Next Appointment");
        if (profileUpcomingAppointments.isEmpty()) {
            System.out.println("You currently have no upcoming appointment.");
        } else {
            System.out.println("Your next appointment is: ");

            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(profileUpcomingAppointments.peek());
        }

        System.out.println();
        UserInterface.enter(scanner);
    }

    public static void getPatientAppointmentById(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments, BST<Appointment> appointmentHistory) {
        while (true) {
            UserInterface.update("Find an Appointment by ID");
            System.out.println("*) Enter 0 to exit\n");

            Appointment appointment = null;

            while (appointment == null) {
                Input _appointmentId = new Input(scanner, "Enter your appointment ID (in number): ")
                                            .isNotEmpty().isNumeric().validate();
                if (_appointmentId.isExit()) return;
                int appointmentId = _appointmentId.getInteger();

                // check the upcoming first
                appointment = profileUpcomingAppointments.find(a -> a.getId() == appointmentId);

                // then check the history
                if (appointment == null) {
                    appointment = appointmentHistory.search(appointmentId);
                }

                if (appointment == null) {
                    UserInterface.warning("Can't find an appointment with this ID!");
                }
            }

            System.out.println();
            UserInterface.info("Appointment details: ");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(appointment);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void getUpcomingAppointments(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments) {
        UserInterface.update("Upcoming Appointments");
        
        Object[] appointments = profileUpcomingAppointments.toArray();

        if (appointments.length == 0) {
            System.out.println("No upcoming appointments.");
        } else {
            System.out.println("╔════════════════════════════════════════════════");
            for (Object obj : appointments) {
                Appointment appointment = (Appointment) obj;
                System.out.println(appointment);
            }
        }

        System.out.println();
        UserInterface.enter(scanner);
    }

    public static void getAppointmentHistory(Scanner scanner, BST<Appointment> appointmentHistory) {
        UserInterface.update("Appointment History");

        if (appointmentHistory.size() == 0) {
            System.out.println("No appointments have taken place.");
        } else {
            System.out.println("╔════════════════════════════════════════════════");
            appointmentHistory.inOrder();
        }

        System.out.println();
        UserInterface.enter(scanner);
    }

    public static void findAppointmentById(Scanner scanner) {
        while (true) {
            UserInterface.update("Find an Appointment by ID");
            System.out.println("*) Enter 0 to exit\n");
            
            Appointment appointment = null;

            while (appointment == null) {
                Input _appointmentId = new Input(scanner, "Enter appointment ID (in number): ")
                                            .isNotEmpty().isNumeric().validate();
                if (_appointmentId.isExit()) return;
                int appointmentId = _appointmentId.getInteger();

                appointment = AppointmentRepository.findById(appointmentId);

                if (appointment == null) {
                    UserInterface.warning("Can't find an appointment with this ID!");
                }
            }

            System.out.println();
            UserInterface.info("Appointment details: ");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(appointment);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void viewAppointmentsBySpecialty(Scanner scanner) {
        while (true) {
            UserInterface.update("View Upcoming Appointment(s) by Specialty");
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

            Object[] appointments = specialty.getAppointmentQueue().toArray();

            System.out.println();
            if (appointments.length == 0) {
                System.out.println("No upcoming appointments.");
            } else {
                UserInterface.info("Appointment(s) in " + UserInterface.colorize(specialty.getName(), UserInterface.YELLOW) + ": ");
                System.out.println("╔════════════════════════════════════════════════");
                for (Object obj : appointments) {
                    Appointment appointment = (Appointment) obj;
                    System.out.println(appointment);
                }
            }

            System.out.println();
            UserInterface.enter(scanner);
        }
    }
}
