package implementation.view;

import java.util.Scanner;

import implementation.controller.AdminController;
import implementation.controller.DoctorController;
import implementation.model.Doctor;
import implementation.model.Specialty;
import implementation.view.doctor.DoctorAppointmentMenu;
import implementation.view.doctor.DoctorDoctorsMenu;
import implementation.view.doctor.DoctorPatientsMenu;
import shared.LoginState;
import shared.repository.DoctorRepository;
import shared.repository.SpecialtyRepository;
import utility.Input;
import utility.UserInterface;

public class DoctorMenu {
    private static Doctor profile;
    private static Specialty specialty;

    public static void show(Scanner scanner) {
        profile = DoctorRepository.findById(LoginState.getLoginId());
        specialty = SpecialtyRepository.findById(profile.getSpecialtyId());
        menuLoop: while (true) {
            UserInterface.update("Doctor Menu");
            System.out.println("Welcome, dr. " + profile.getName() + " (" + UserInterface.colorize("#" + profile.getId(), UserInterface.YELLOW) + ")");
            System.out.println("You currently have " + UserInterface.colorize("" + profile.getUpcomingAppointments().size(), UserInterface.YELLOW) + " upcoming appointment(s).\n");

            String[] options = {
                "Appointments",
                "My Patients",
                "View Doctors\n",
                "Edit Profile",
                "Contact Admin\n",
                "Log Out"
            };

            UserInterface.createOptions(options);

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    if (specialty != null) {
                        DoctorAppointmentMenu.show(scanner, profile, specialty);
                    } else {
                        UserInterface.warning("Your specialty data is not found, please contact the admin!");
                        UserInterface.enter(scanner);
                    }
                    break;
                case "2":
                    DoctorPatientsMenu.show(scanner, profile);
                    break;
                case "3":
                    if (specialty != null) {
                        DoctorDoctorsMenu.show(scanner, specialty);
                    } else {
                        UserInterface.warning("Your specialty data is not found, please contact the admin!");
                        UserInterface.enter(scanner);
                    }
                    break;
                case "4":
                    DoctorController.editProfile(scanner, profile);
                    break;
                case "5":
                    AdminController.viewAdmins(scanner);
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
