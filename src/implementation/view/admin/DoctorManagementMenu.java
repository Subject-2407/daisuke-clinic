package implementation.view.admin;

import java.util.Scanner;

import implementation.controller.DoctorController;
import utility.Input;
import utility.UserInterface;

public class DoctorManagementMenu {
    public static void show(Scanner scanner) {
        while (true) {
            UserInterface.update("Doctor Management");
            String[] options = {
                "Add a Doctor",
                "Find a Doctor by ID",
                "Find Doctor(s) by Name",
                "Find Doctor(s) by Specialty",
                "Remove a Doctor",
                "View All Doctors\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    DoctorController.addDoctor(scanner);
                    break;
                case "2":
                    DoctorController.findDoctor(scanner);
                    break;
                case "3":
                    DoctorController.findDoctorsByName(scanner);
                    break;
                case "4":
                    DoctorController.findDoctorsBySpecialty(scanner);
                    break;
                case "5":
                    DoctorController.removeDoctor(scanner);
                    break;
                case "6":
                    DoctorController.viewDoctors(scanner);
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
