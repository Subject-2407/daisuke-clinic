package implementation.view.doctor;

import java.util.Scanner;

import implementation.controller.MedicalRecordController;
import implementation.controller.PatientController;
import implementation.model.Doctor;
import utility.Input;
import utility.UserInterface;

public class DoctorPatientsMenu {
    public static void show(Scanner scanner, Doctor doctorProfile) {
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

            System.out.println();
            String input = new Input(scanner, "Enter choice: ").validate().get();
            switch (input == null ? "0" : input) {
                case "1":
                    MedicalRecordController.updateMedicalRecord(scanner, doctorProfile.getId());
                    break;
                case "2":
                    PatientController.getDoctorCurrentPatients(scanner, doctorProfile.getId());
                    break;
                case "3":
                    PatientController.getDoctorCurrentPatientById(scanner, doctorProfile.getId());
                    break;
                case "4":
                    PatientController.getDoctorCurrentPatientsByName(scanner, doctorProfile.getId());
                    break;
                case "5":
                    return;
                case "0":
                    System.exit(0);
                default:
                    UserInterface.warning("Invalid choice!");
                    UserInterface.enter(scanner);
            }
        }
    }
}