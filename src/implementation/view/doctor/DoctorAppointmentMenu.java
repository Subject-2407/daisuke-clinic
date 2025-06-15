package implementation.view.doctor;

import java.util.Scanner;

import implementation.controller.AppointmentController;
import implementation.model.Doctor;
import implementation.model.Specialty;
import utility.Input;
import utility.UserInterface;

public class DoctorAppointmentMenu {
    public static void show(Scanner scanner, Doctor doctorProfile, Specialty specialty) {
        while (true) {
            UserInterface.update("Appointments");
            System.out.println("Specialty: " + UserInterface.colorize(specialty.getName(), UserInterface.YELLOW) + "\n");
            String[] options = {
                "Process Next Appointment",
                "View Specialty Upcoming Appointments (" + specialty.getAppointmentQueue().size() + ")",
                "View My Upcoming Appointments (" + doctorProfile.getUpcomingAppointments().size() + ")",
                "View My Appointment History",
                "Find My Appointment by ID",
                "Find My Appointment(s) by Patient's Name\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    AppointmentController.processDoctorNextAppointment(scanner, doctorProfile, specialty);
                    break;
                case "2":
                    AppointmentController.viewSpecialtyUpcomingAppointments(scanner, specialty);
                    break;
                case "3":
                    AppointmentController.getUpcomingAppointments(scanner, doctorProfile.getUpcomingAppointments());
                    break;
                case "4":
                    AppointmentController.getAppointmentHistory(scanner, doctorProfile.getAppointmentHistory());
                    break;
                case "5":
                    AppointmentController.getAppointmentById(scanner, doctorProfile.getUpcomingAppointments(), doctorProfile.getAppointmentHistory());
                    break;
                case "6":
                    AppointmentController.getAppointmentsByPatientName(scanner, doctorProfile.getUpcomingAppointments(), doctorProfile.getAppointmentHistory());
                    break;
                case "7":
                    return;
                case "0":
                    System.exit(0);
                default:
                    UserInterface.warning("Invalid choice!");
                    UserInterface.enter(scanner);
            }
        }
    }
}
