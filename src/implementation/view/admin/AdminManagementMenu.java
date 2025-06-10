package implementation.view.admin;

import java.util.Scanner;

import implementation.controller.AdminController;
import utility.Input;
import utility.UserInterface;

public class AdminManagementMenu {
    public static void show(Scanner scanner) {
        while (true) {
            UserInterface.update("Admin Management");
            String[] options = {
                "Add an Admin",
                "Find an Admin by ID",
                "Remove an Admin",
                "View All Admins\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    AdminController.addAdmin(scanner);
                    break;
                case "2":
                    AdminController.findAdmin(scanner);
                    break;
                case "3":
                    AdminController.removeAdmin(scanner);
                    break;
                case "4":
                    AdminController.viewAdmins(scanner);
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
