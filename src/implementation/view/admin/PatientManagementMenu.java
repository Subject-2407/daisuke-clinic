package implementation.view.admin;

import java.util.Scanner;

import implementation.controller.PatientController;
import utility.Input;
import utility.UserInterface;

public class PatientManagementMenu {
    public static void show(Scanner scanner) {
        while (true) {
            UserInterface.update("Patient Management");
            String[] options = {
                "Add a Patient",
                "Find a Patient by ID",
                "Find Patient(s) by Name",
                "Remove a Patient",
                "View All Patients\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    PatientController.addPatient(scanner);
                    break;
                case "2":
                    PatientController.findPatient(scanner);
                    break;
                case "3":
                    PatientController.findPatientsByName(scanner);
                    break;
                case "4":
                    PatientController.removePatient(scanner);
                    break;
                case "5":
                    PatientController.viewPatients(scanner);
                    break;
                case "6":
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
