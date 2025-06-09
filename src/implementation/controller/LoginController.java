package implementation.controller;

import java.util.Scanner;

import implementation.model.Admin;
import implementation.model.Patient;
import shared.LoginState;
import shared.enums.Role;
import shared.repository.AdminRepository;
import shared.repository.PatientRepository;
import utility.Input;
import utility.UserInterface;

public class LoginController {
    public static void patientLogin(Scanner scanner) {
        while (true) {
            UserInterface.update("Log In as Patient");
            System.out.println("*) Enter 0 to exit\n");

            // id input
            Input _userId = new Input(scanner, "Enter ID (in number): ")
                                    .isNumeric().validate();
            if (_userId.isExit()) return;
            int userId = _userId.getInteger();

            // password input
            Input _password = new Input(scanner, "Enter password: ")
                                        .validate();
            if (_password.isExit()) return;
            String password = _password.get();

            System.out.println();
            
            // validate login
            Patient foundPatient = PatientRepository.findById(userId);
            if (foundPatient == null || !foundPatient.validatePassword(password)) {
                UserInterface.warning("Invalid login!");
                UserInterface.enter(scanner);
            } else {
                LoginState.login(Role.PATIENT, userId);
                UserInterface.success("Login successful!");
                UserInterface.enter(scanner);
                return;
            }
        }
    }

    public static void adminLogin(Scanner scanner) {
        while (true) {
            UserInterface.update("Log In as Admin");
            System.out.println("*) Enter 0 to exit\n");

            // id input
            Input _userId = new Input(scanner, "Enter ID (in number): ")
                                    .isNumeric().validate();
            if (_userId.isExit()) return;
            int userId = _userId.getInteger();

            // password input
            Input _password = new Input(scanner, "Enter password: ")
                                        .validate();
            if (_password.isExit()) return;
            String password = _password.get();

            System.out.println();
            
            // validate login
            Admin foundAdmin = AdminRepository.findById(userId);
            if (foundAdmin == null || !foundAdmin.validatePassword(password)) {
                UserInterface.warning("Invalid login!");
                UserInterface.enter(scanner);
            } else {
                LoginState.login(Role.ADMIN, userId);
                UserInterface.success("Login successful!");
                UserInterface.enter(scanner);
                return;
            }
        }
    }
}