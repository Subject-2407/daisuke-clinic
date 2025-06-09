package implementation.view;

import java.util.Scanner;

import implementation.model.Patient;
import shared.LoginState;
import shared.repository.PatientRepository;
import utility.Input;
import utility.UserInterface;

public class PatientMenu {
    private static Patient profile;

    public static void show(Scanner scanner) {    
        profile = PatientRepository.findById(LoginState.getLoginId());
        menuLoop: while (true) {
            UserInterface.update();
            System.out.println("Welcome, " + profile.getName() + " (" + UserInterface.colorize("#" + profile.getId(), UserInterface.YELLOW) + ")\n");

            String[] options = {
                "View Available Doctors",
                "Book an Appointment",
                "View My Appointments",
                "View My Medical Records",
                "Edit Profile\n",
                "Log Out"
            };
            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    break;
                case "2":
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
