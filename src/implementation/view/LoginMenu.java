package implementation.view;

import java.util.Scanner;

import implementation.controller.LoginController;
import implementation.controller.PatientController;
import shared.LoginState;
import shared.enums.Role;
import utility.Input;
import utility.UserInterface;

public class LoginMenu {
    public static void show(Scanner scanner) {
        menuLoop: while (true) {
            UserInterface.update("Login");
            String[] options = {
                "Log In as Patient",
                "Log In as Doctor (W.I.P.)",
                "Log In as Admin\n",
                "Register as Patient\n"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    LoginController.patientLogin(scanner);
                    if (LoginState.isLoggedIn() && LoginState.getRole() == Role.PATIENT) {
                        PatientMenu.show(scanner);
                    }
                    break;
                case "2":
                    break;
                case "3":
                    LoginController.adminLogin(scanner);
                    if (LoginState.isLoggedIn() && LoginState.getRole() == Role.ADMIN) {
                        AdminMenu.show(scanner);
                    };
                    break;
                case "4":
                    PatientController.addPatient(scanner);
                    break;
                case "0":
                    break menuLoop;
                default:
                    UserInterface.warning("Invalid choice!");
                    UserInterface.enter(scanner);
            }
        }
    }
}
