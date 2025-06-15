package implementation.view;

import java.util.Scanner;

import implementation.controller.AdminController;
import implementation.controller.AppointmentController;
import implementation.model.Admin;
import implementation.view.admin.AdminManagementMenu;
import implementation.view.admin.DoctorManagementMenu;
import implementation.view.admin.PatientManagementMenu;
import implementation.view.admin.SpecialtyManagementMenu;
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
                "Patient Management",
                "Admin Management",
                "Find an Appointment by ID", // all clinic appointments
                "View Appointment(s) by Specialty\n",
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
                    PatientManagementMenu.show(scanner);
                    break;
                case "4":
                    AdminManagementMenu.show(scanner);
                    break;
                case "5":
                    AppointmentController.findClinicAppointmentById(scanner);
                    break;
                case "6":
                    AppointmentController.viewAppointmentsBySpecialty(scanner);
                    break;
                case "7":
                    AdminController.editProfile(scanner, profile);
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
