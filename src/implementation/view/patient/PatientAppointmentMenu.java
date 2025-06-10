package implementation.view.patient;

import java.util.Scanner;

import implementation.controller.AppointmentController;
import implementation.model.Patient;
import utility.Input;
import utility.UserInterface;

public class PatientAppointmentMenu {
    public static void show(Scanner scanner, Patient profile) {
        while (true) {
            UserInterface.update("Appointments");
            String[] options = {
                "Book an Appointment",
                "View Upcoming Appointments (" + profile.getUpcomingAppointments().size() + ")",
                "View Appointment History",
                "Find an Appointment by ID\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    AppointmentController.addAppointment(scanner);
                    break;
                case "2":
                    AppointmentController.getUpcomingAppointments(scanner, profile.getUpcomingAppointments());
                    break;
                case "3":
                    AppointmentController.getAppointmentHistory(scanner, profile.getAppointmentHistory());
                    break;
                case "4":
                    break;
                case "5":
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
