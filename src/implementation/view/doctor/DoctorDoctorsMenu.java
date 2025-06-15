package implementation.view.doctor;

import java.util.Scanner;

import utility.UserInterface;

public class DoctorDoctorsMenu {
    public static void show(Scanner scanner) {
        while (true) {
            UserInterface.update("View Doctors");
            String[] options = {
                "View All Doctors",
                "View Doctors in My Specialty",
                "View Doctors by Specialty",
                "Find a Doctor by ID",
                "Find Doctor(s) by Name\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);


        }
    }
}
