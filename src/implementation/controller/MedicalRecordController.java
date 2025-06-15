package implementation.controller;

import java.util.Scanner;

import implementation.model.MedicalRecord;
import utility.Input;
import utility.UserInterface;

public class MedicalRecordController {
    public static void viewMedicalRecord(Scanner scanner, MedicalRecord profileMedicalRecord) {
        UserInterface.update("My Medical Record");

        System.out.println(profileMedicalRecord);

        System.out.println();
        UserInterface.enter(scanner);
        return;
    }

    public static void updateMedicalRecord(Scanner scanner) {
        while (true) {
            UserInterface.update("Check Patient's Medical Record");
            System.out.println("*) Enter 0 to exit\n");
            
            MedicalRecord medicalRecord = null;

            while (medicalRecord == null) {
                Input _medicalRecordId = new Input(scanner, "Enter patient's ID (in number): ")
                                            .isNotEmpty().isNumeric().validate();
                if (_medicalRecordId.isExit()) return;
                int medicalRecordId = _medicalRecordId.getInteger();
            }
        }
    }
}
