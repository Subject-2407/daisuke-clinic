package implementation.controller;

import java.util.Scanner;

import implementation.model.MedicalRecord;
import utility.UserInterface;

public class MedicalRecordController {
    public static void viewMedicalRecord(Scanner scanner, MedicalRecord profileMedicalRecord) {
        UserInterface.update("My Medical Record");

        System.out.println(profileMedicalRecord);

        System.out.println();
        UserInterface.enter(scanner);
        return;
    }
}
