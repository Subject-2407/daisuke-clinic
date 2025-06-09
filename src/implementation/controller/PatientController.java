package implementation.controller;

import java.util.Scanner;

import implementation.model.Patient;
import shared.LoginState;
import shared.enums.Role;
import shared.repository.PatientRepository;
import utility.Hasher;
import utility.Input;
import utility.UserInterface;

public class PatientController {
    public static void addPatient(Scanner scanner) {
        while (true) {
            String perspective = "";
            if (LoginState.getRole() == Role.NONE) {
                UserInterface.update("Register as Patient");
                perspective = "your";
            } else if (LoginState.getRole() == Role.ADMIN) {
                UserInterface.update("Add a Patient");
                perspective = "patient's";
            }
            System.out.println("*) Enter 0 to exit\n");

            int patientId = -1, patientAge = -1;
            String patientPassword, patientName, patientAddress, patientPhoneNumber;

            // patient's id input
            while (patientId == -1) {
                Input _patientId = new Input(scanner, "Enter " + perspective + " new ID (in number): ")
                                .isNotEmpty().isNumeric().isPositiveNumber().validate();
                if (_patientId.isExit()) return;
                patientId = _patientId.getInteger();

                // check whether patient's ID exists or not
                if (PatientRepository.findById(patientId) != null) {
                    UserInterface.warning("This patient ID is already used!");
                    patientId = -1;
                }
            }

            // patient's name input
            Input _patientName = new Input(scanner, "Enter " + perspective + " name: ")
                            .isNotEmpty().isAlphabetic().validate();
            if (_patientName.isExit()) return;
            patientName = _patientName.get();

            // patient's age input
            Input _patientAge = new Input(scanner, "Enter " + perspective + " age: ")
                            .isNotEmpty().isNumeric().isPositiveNumber().validate();
            if (_patientAge.isExit()) return;
            patientAge = _patientAge.getInteger();

            // patient's address input
            Input _patientAddress = new Input(scanner, "Enter " + perspective + " address: ")
                                        .isNotEmpty().validate();
            if (_patientAddress.isExit()) return;
            patientAddress = _patientAddress.get();

            // patient's phone number input
            Input _patientPhoneNumber = new Input(scanner, "Enter " + perspective + " phone number: ")
                                            .isNotEmpty().isValidPhoneNumber().validate();
            if (_patientPhoneNumber.isExit()) return;
            patientPhoneNumber = _patientPhoneNumber.get();

            // patient's new password input
            Input _patientPassword = new Input(scanner, "Enter " + perspective + " new password (alphanumeric): ")
                                        .isNotEmpty().isAlphanumeric().validate();
            if (_patientPassword.isExit()) return;
            patientPassword = Hasher.hash(_patientPassword.get());

            Patient newPatient = new Patient(patientId, patientPassword, patientName, patientAge, patientAddress, patientPhoneNumber);
            PatientRepository.add(newPatient);

            System.out.println();

            if (LoginState.getRole() == Role.NONE) {
                UserInterface.success("Register successful!");
                UserInterface.enter(scanner);
                return;
            } else if (LoginState.getRole() == Role.ADMIN) {
                String id = UserInterface.colorize("#" + patientId, UserInterface.YELLOW);
                UserInterface.info("Successfully added patient " + id + "!");
                UserInterface.enter(scanner);
            }
        }
    }
}
