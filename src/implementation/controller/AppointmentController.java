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
import shared.LoginState;
import shared.repository.AppointmentRepository;
import shared.repository.DoctorRepository;
import utility.Input;
import utility.UserInterface;

public class AppointmentController {
    public static void addAppointment(Scanner scanner) {
        while (true) {
            UserInterface.update("Book an Appointment");
            System.out.println("*) Enter 0 to exit\n");

            Doctor doctor = null;
            String presentingComplaints;
            LocalDateTime time;

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

            System.out.println("-------------------------------------------------");
            UserInterface.info("Doctor details: ");
            System.out.println(doctor);

            // presenting complaints input
            Input _presentingComplaints = new Input(scanner, "Enter your presenting complaints: ")
                                            .isNotEmpty().validate();
            if (_presentingComplaints.isExit()) return;
            presentingComplaints = _presentingComplaints.get();

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
            int patientId = LoginState.getLoginId();
            int specialtyId = doctor.getSpecialtyId();

            Appointment newAppointment = new Appointment(appointmentNumber, specialtyId, patientId, doctor.getId(), presentingComplaints, time);
            AppointmentRepository.add(newAppointment);

            System.out.println();

            UserInterface.success("Successfully booked an appointment!");
            UserInterface.enter(scanner);
        }
    }

    public static void getUpcomingAppointments(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments) {
        UserInterface.update("Upcoming Appointments");
        
        Object[] appointments = profileUpcomingAppointments.toArray();

        if (appointments.length == 0) {
            System.out.println("No upcoming appointments.");
        } else {
            System.out.println("-------------------------------------------------");
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
            System.out.println("-------------------------------------------------");
            appointmentHistory.inOrder();
        }

        System.out.println();
        UserInterface.enter(scanner);
    }
}
