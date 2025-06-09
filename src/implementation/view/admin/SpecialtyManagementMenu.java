package implementation.view.admin;

import java.util.Scanner;

import implementation.controller.SpecialtyController;
import utility.Input;
import utility.UserInterface;

public class SpecialtyManagementMenu {
    public static void show(Scanner scanner) {
        while (true) {
            UserInterface.update("Specialty Management");
            String[] options = {
                "Add a Specialty",
                "Find a Specialty by ID",
                "Remove a Specialty",
                "View All Specialties\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    SpecialtyController.addSpecialty(scanner);
                    break;
                case "2":
                    SpecialtyController.findSpecialty(scanner);
                    break;
                case "3":
                    SpecialtyController.removeSpecialty(scanner);
                    break;
                case "4":
                    SpecialtyController.viewSpecialties(scanner);
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
