package implementation.view.doctor;

import java.util.Scanner;

import implementation.controller.DoctorController;
import implementation.model.Specialty;
import utility.Input;
import utility.UserInterface;

public class DoctorDoctorsMenu {
    public static void show(Scanner scanner, Specialty specialty) {
        while (true) {
            UserInterface.update("View Doctors");
            String[] options = {
                "View All Doctors",
                "View Doctors in My Specialty",
                "View Doctors by Specialty",
                "Find a Doctor by ID",
                "Find Doctor(s) by Name\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    DoctorController.viewDoctors(scanner);
                    break;
                case "2":
                    DoctorController.findDoctorsInCurrentSpecialty(scanner, specialty.getId());
                    break;
                case "3":
                    DoctorController.findDoctorsBySpecialty(scanner);
                    break;
                case "4":
                    DoctorController.findDoctor(scanner);
                    break;
                case "5":
                    DoctorController.findDoctorsByName(scanner);
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
