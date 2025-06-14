package implementation.controller;

import java.util.Scanner;

import implementation.model.Admin;
import shared.LoginState;
import shared.enums.Role;
import shared.repository.AdminRepository;
import shared.repository.PatientRepository;
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
            System.out.println("╔════════════════════════════════════════════════");
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

            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(admin);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void viewAdmins(Scanner scanner) {
        String header = "";
        if (LoginState.getRole() == Role.ADMIN) {
            header = "View All Admins";
        } else if (LoginState.getRole() == Role.PATIENT) {
            header = "Contact Admin";
        }

        UserInterface.update(header);

        if (PatientRepository.getRepositorySize() == 0) {
            System.out.println("No admins available.");
        } else {
            if (LoginState.getRole() == Role.PATIENT) {
                System.out.println("You can contact the admins below in case you have a problem with the system.\n");
            }
            System.out.println("╔════════════════════════════════════════════════");
            AdminRepository.getAll();
        }

        UserInterface.enter(scanner);
        return;
    }

    public static void editProfile(Scanner scanner, Admin profile) {
        while (true) {
            UserInterface.update("Edit Profile");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(profile);
            System.out.println();
            String[] options = {
                "Edit Name",
                "Edit Phone Number",
                "Change Password\n",
            };
            UserInterface.createOptions(options);

            System.out.println();
            Input _editProfileChoice = new Input(scanner, "Enter choice: ")
                                            .isNotEmpty().validate();
            if (_editProfileChoice.isExit()) return;
            String editProfileChoice = _editProfileChoice.get();

            switchLoop: switch (editProfileChoice) {
                case "1":
                    Input _editName = new Input(scanner, "Enter new name: ")
                                            .isNotEmpty().isAlphabetic().validate();
                    if (_editName.isExit()) break;
                    String newName = _editName.get();
                    profile.setName(newName);
                    AdminRepository.modifyFile(LoginState.getLoginId(), a -> { a.setName(newName); return a;});
                    UserInterface.success("Name successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "2":
                    Input _editPhoneNumber = new Input(scanner, "Enter new phone number: ")
                                            .isNotEmpty().isValidPhoneNumber().validate();
                    if (_editPhoneNumber.isExit()) break;
                    String newPhoneNumber = _editPhoneNumber.get();
                    profile.setPhoneNumber(newPhoneNumber);
                    AdminRepository.modifyFile(LoginState.getLoginId(), a -> { a.setPhoneNumber(newPhoneNumber); return a;});
                    UserInterface.success("Phone number successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "3":
                    while (true) {
                        Input _currentPassword = new Input(scanner, "Enter your current password: ")
                                                .isNotEmpty().validate();
                        if (_currentPassword.isExit()) break switchLoop;
                        if (!profile.validatePassword(_currentPassword.get())) {
                            UserInterface.warning("Invalid password!");
                        } else break;
                    }
                    Input _changePassword = new Input(scanner, "Enter new password (must be alphanumeric): ")
                                                .isNotEmpty().isAlphanumeric().validate();
                    if (_changePassword.isExit()) break;
                    String newPassword = _changePassword.get();
                    profile.setPassword(newPassword);
                    AdminRepository.modifyFile(LoginState.getLoginId(), a -> { a.setPassword(newPassword); return a;});
                    UserInterface.success("Password successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "0":
                    
                default:
                    UserInterface.warning("Invalid choice!");
                    UserInterface.enter(scanner);
            }
        }
    }
}
