package implementation.view.admin;

import java.util.Scanner;

import implementation.model.Admin;
import shared.LoginState;
import shared.repository.AdminRepository;
import utility.Input;
import utility.UserInterface;

public class AdminMenu {
    private static Admin profile;

    public static void show(Scanner scanner) {
        profile = AdminRepository.findById(LoginState.getLoginId());
        menuLoop: while (true) {
            UserInterface.update("Admin Panel");
            System.out.println("Welcome, " + profile.getName() + " (" + UserInterface.colorize("#" + profile.getId(), UserInterface.YELLOW) + ")\n");

            String[] options = {
                "Specialty Management",
                "Doctor Management",
                "View Appointments", // based on specialty
                "View Medical Records",
                "Edit Profile\n",
                "Log Out"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    SpecialtyManagementMenu.show(scanner);
                    break;
                case "2":
                    DoctorManagementMenu.show(scanner);
                    break;
                case "3":
                    break;
                case "4":
                    break;
                case "5":
                    break;
                case "6":
                    LoginState.logout();
                    break menuLoop;
                case "0":
                    System.exit(0);
                default:
                    UserInterface.warning("Invalid choice!");
                    UserInterface.enter(scanner);
            }
        }
    }
}
