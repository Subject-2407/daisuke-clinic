package implementation.view.doctor;

import java.util.Scanner;

import utility.UserInterface;

public class DoctorPatientsMenu {
    public static void show(Scanner scanner) {
        while (true) {
            UserInterface.update("My Patients");
            String[] options = {
                "Check Patient's Medical Record",
                "View My Current Patients",
                "Find My Patient by ID",
                "Find My Patient(s) by Name\n",
                "Return to Main Menu"
            };
            UserInterface.createOptions(options);


        }
    }
}