package implementation.view;

import java.util.Scanner;

import implementation.controller.DoctorController;
import implementation.controller.SpecialtyController;
import implementation.model.Patient;
import implementation.view.patient.PatientAppointmentMenu;
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
                "Appointments",
                "Medical Records\n",
                "View Available Specialties",
                "View Doctor(s) by Specialty",
                "Find Doctor(s) by Name\n",
                "Edit Profile",
                "Contact Admin\n",
                "Log Out"
            };

            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    PatientAppointmentMenu.show(scanner, profile);
                    break;
                case "2":
                    break;
                case "3":
                    SpecialtyController.viewSpecialties(scanner);
                    break;
                case "4":
                    DoctorController.findDoctorsBySpecialty(scanner);
                    break;
                case "5":
                    DoctorController.findDoctorsByName(scanner);
                    break;
                case "6":
                    break;
                case "7":
                    break;
                case "8":
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
