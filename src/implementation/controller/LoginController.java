package implementation.controller;

import java.util.Scanner;

import implementation.model.Admin;
import implementation.model.Doctor;
import implementation.model.Patient;
import shared.LoginState;
import shared.enums.Role;
import shared.repository.AdminRepository;
import shared.repository.DoctorRepository;
import shared.repository.PatientRepository;
import utility.Input;
import utility.UserInterface;

public class LoginController {
    static class LoginInput {
        static int userId;
        static String password;
        static boolean isExit = false;

        static void show(Scanner scanner) {
            isExit = false;
            Input _userId = new Input(scanner, "Enter ID (in number): ")
                                    .isNotEmpty().isNumeric().validate();
            if (_userId.isExit()) {
                isExit = true; 
                return;
            }
            userId = _userId.getInteger();

            Input _password = new Input(scanner, "Enter password: ")
                                        .validate();
            if (_password.isExit()) {
                isExit = true; 
                return;
            }
            password = _password.get();
        }

        static int getId() { return userId; }
        static String getPassword() { return password; }
        static boolean isExit() { return isExit; }
    }
    public static void patientLogin(Scanner scanner) {
        while (true) {
            UserInterface.update("Log In as Patient");
            System.out.println("*) Enter 0 to exit\n");

            LoginInput.show(scanner);
            if (LoginInput.isExit()) return;

            System.out.println();
            
            // validate login
            Patient foundPatient = PatientRepository.findById(LoginInput.getId());
            if (foundPatient == null || !foundPatient.validatePassword(LoginInput.getPassword())) {
                UserInterface.warning("Invalid login!");
                UserInterface.enter(scanner);
            } else {
                LoginState.login(Role.PATIENT, LoginInput.getId());
                UserInterface.success("Login successful!");
                UserInterface.enter(scanner);
                return;
            }
        }
    }

    public static void doctorLogin(Scanner scanner) {
        while (true) {
            UserInterface.update("Log In as Doctor");
            System.out.println("*) Enter 0 to exit\n");

            LoginInput.show(scanner);
            if (LoginInput.isExit()) return;

            System.out.println();

            // validate login
            Doctor foundDoctor = DoctorRepository.findById(LoginInput.getId());
            if (foundDoctor == null || !foundDoctor.validatePassword(LoginInput.getPassword())) {
                UserInterface.warning("Invalid login!");
                UserInterface.enter(scanner);
            } else {
                LoginState.login(Role.DOCTOR, LoginInput.getId());
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

            LoginInput.show(scanner);
            if (LoginInput.isExit()) return;

            System.out.println();
            
            // validate login
            Admin foundAdmin = AdminRepository.findById(LoginInput.getId());
            if (foundAdmin == null || !foundAdmin.validatePassword(LoginInput.getPassword())) {
                UserInterface.warning("Invalid login!");
                UserInterface.enter(scanner);
            } else {
                LoginState.login(Role.ADMIN, LoginInput.getId());
                UserInterface.success("Login successful!");
                UserInterface.enter(scanner);
                return;
            }
        }
    }
}