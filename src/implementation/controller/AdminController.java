package implementation.controller;

import java.util.Scanner;

import implementation.model.Admin;
import shared.LoginState;
import shared.repository.AdminRepository;
import utility.Hasher;
import utility.Input;
import utility.UserInterface;

public class AdminController {
    public static void addAdmin(Scanner scanner) {
        while(true) {
            UserInterface.update("Add an Admin");
            System.out.println("*) Enter 0 to exit\n");

            int adminId = -1;
            String adminPassword, adminName, adminPhoneNumber;

            // admin id input
            while (adminId == -1) {
                Input _adminId = new Input(scanner, "Enter new admin's ID (in number): ")
                                    .isNotEmpty().isNumeric().isPositiveNumber().validate();
                if (_adminId.isExit()) return;
                adminId = _adminId.getInteger();

                // check if admin already exists
                if (AdminRepository.findById(adminId) != null) {
                    UserInterface.warning("This admin ID is already used!");
                    adminId = -1;
                }
            }

            // admin name input
            Input _adminName = new Input(scanner, "Enter admin's name: ")
                                    .isNotEmpty().isAlphabetic().validate();
            if (_adminName.isExit()) return;
            adminName = _adminName.get();

            // admin phone number input
            Input _adminPhoneNumber = new Input(scanner, "Enter admin's phone number: ")
                                            .isNotEmpty().isValidPhoneNumber().validate();
            if (_adminPhoneNumber.isExit()) return;
            adminPhoneNumber = _adminPhoneNumber.get();

            // admin password input
            Input _adminPassword = new Input(scanner, "Enter admin's new password (alphanumeric): ")
                                        .isNotEmpty().isAlphanumeric().validate();
            if (_adminPassword.isExit()) return;
            adminPassword = Hasher.hash(_adminPassword.get());

            Admin newAdmin = new Admin(adminId, adminPassword, adminName, adminPhoneNumber);
            AdminRepository.add(newAdmin);

            System.out.println();

            UserInterface.success("Successfully added new admin!");
            UserInterface.enter(scanner);
        }
    }

    public static void findAdmin(Scanner scanner) {
        while (true) {
            UserInterface.update("Find an Admin by ID");
            System.out.println("*) Enter 0 to exit\n");

            Admin foundAdmin = null;

            // admin id input
            while (foundAdmin == null) {
                Input _adminId = new Input(scanner, "Enter admin's ID (in number): ")
                                    .isNotEmpty().isNumeric().validate();
                if (_adminId.isExit()) return;
                int adminId = _adminId.getInteger();
                foundAdmin = AdminRepository.findById(adminId);

                if (foundAdmin == null) {
                    UserInterface.warning("Can't find an admin with this ID!");
                }
            }

            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.println(foundAdmin);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void removeAdmin(Scanner scanner) {
        while (true) {
            UserInterface.update("Remove an Admin");
            System.out.println("*) Enter 0 to exit\n");

            int adminId = -1;
            Admin admin = null;

            // admin id input
            while (adminId == -1) {
                Input _adminId = new Input(scanner, "Enter admin's ID (in number): ")
                                    .isNotEmpty().isNumeric().validate();
                if (_adminId.isExit()) return;
                adminId = _adminId.getInteger();
                if (LoginState.getLoginId() == adminId) {
                    UserInterface.warning("You can't remove yourself!");
                    adminId = -1;
                    continue;
                }
                admin = AdminRepository.findById(adminId);

                if (admin == null) {
                    UserInterface.warning("Can't find an admin with this ID!");
                    adminId = -1;
                }
            }

            AdminRepository.remove(adminId);

            System.out.println();

            UserInterface.success("Successfully removed admin " + UserInterface.colorize("#" + adminId, UserInterface.YELLOW) + "!");
            UserInterface.info("Removed admin details: ");

            System.out.println("-------------------------------------------------");
            System.out.println(admin);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void viewAdmins(Scanner scanner) {
        UserInterface.update("View All Admins");

        System.out.println("-------------------------------------------------");
        AdminRepository.getAll();

        UserInterface.enter(scanner);
        return;
    }
}
