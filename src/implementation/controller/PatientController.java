package implementation.controller;

import java.util.Scanner;

import implementation.model.Patient;
import implementation.model.enums.Gender;
import shared.LoginState;
import shared.enums.Role;
import shared.repository.MedicalRecordRepository;
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
            Gender patientGender;

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

            // patient's gender input
            while (true) {
                Input _patientGender = new Input(scanner, "Enter " + perspective + " gender (M/F): ")
                                        .isNotEmpty().validate();
                if (_patientGender.isExit()) return;
                String patientGenderType = _patientGender.get();
                if (patientGenderType.toUpperCase().equals("M")) {
                    patientGender = Gender.MALE;
                    break;
                } else if (patientGenderType.toUpperCase().equals("F")) {
                    patientGender = Gender.FEMALE;
                    break;
                } else {
                    UserInterface.warning("Invalid input!");
                }
            }
            

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

            Patient newPatient = new Patient(patientId, patientPassword, patientName, patientGender, patientAge, patientAddress, patientPhoneNumber);
            PatientRepository.add(newPatient);
            MedicalRecordRepository.add(newPatient.getMedicalRecord());

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

    public static void findPatient(Scanner scanner) {
        while (true) {
            UserInterface.update("Find a Patient by ID");
            System.out.println("*) Enter 0 to exit\n");

            Patient foundPatient = null;

            // patient id input
            while (foundPatient == null) {
                Input _patientId = new Input(scanner, "Enter patient's ID (in number): ")
                                        .isNotEmpty().isNumeric().validate();
                if (_patientId.isExit()) return;
                int patientId = _patientId.getInteger();
                foundPatient = PatientRepository.findById(patientId);

                if (foundPatient == null) {
                    UserInterface.warning("Can't find a patient with this ID!");
                }
            } 

            System.out.println();
            UserInterface.info("Patient details: ");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(foundPatient);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void findPatientsByName(Scanner scanner) {
        while (true) {
            UserInterface.update("Find Patient(s) by Name");
            System.out.println("*) Enter 0 to exit\n");

            // patient name input
            Input _patientName = new Input(scanner, "Enter patient's name: ")
                                    .isNotEmpty().isAlphabetic().validate();
            if (_patientName.isExit()) return;

            Object[] patients = PatientRepository.findAll(p -> p.getName().toLowerCase().contains(_patientName.get().toString().toLowerCase()));

            System.out.println();
            UserInterface.info("Result: ");
            if (patients.length == 0) {
                System.out.println("No patient with that name.");
            } else {
                System.out.println("╔════════════════════════════════════════════════");
                for (Object obj : patients) {
                    Patient patient = (Patient) obj;
                    System.out.println(patient);
                }
            }

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void removePatient(Scanner scanner) {
        while (true) {
            UserInterface.update("Remove a Patient");
            System.out.println("*) Enter 0 to exit\n");

            int patientId = -1;
            Patient patient = null;

            // patient id input
            while (patientId == -1) {
                Input _patientId = new Input(scanner, "Enter patient's ID (in number): ")
                                    .isNotEmpty().isNumeric().validate();
                if (_patientId.isExit()) return;
                patientId = _patientId.getInteger();
                patient = PatientRepository.findById(patientId);

                if (patient == null) {
                    UserInterface.warning("Can't find a patient with this ID!");
                    patientId = -1;
                }
            }

            PatientRepository.remove(patientId);

            System.out.println();

            UserInterface.success("Successfully removed patient " + UserInterface.colorize("#" + patientId, UserInterface.YELLOW) + "!");
            UserInterface.info("Removed patient details: ");

            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(patient);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void viewPatients(Scanner scanner) {
        UserInterface.update("View All Patients");

        if (PatientRepository.getRepositorySize() == 0) {
            System.out.println("No patients available.");
        } else {
            System.out.println("╔════════════════════════════════════════════════");
            PatientRepository.getAllInorder();
        }
        

        UserInterface.enter(scanner);
        return;
    }

    public static void editProfile(Scanner scanner, Patient profile) {
        while (true) {
            UserInterface.update("Edit Profile");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(profile);
            System.out.println();
            String[] options = {
                "Edit Name",
                "Edit Age",
                "Edit Address",
                "Edit Phone Number",
                "Change Password\n",
            };
            UserInterface.createOptions(options);

            System.out.println();
            Input _editProfileChoice = new Input(scanner, "Enter choice: ")
                                            .isNotEmpty().validate();
            if (_editProfileChoice.isExit()) return;
            String editProfileChoice = _editProfileChoice.get();

            switchLoop: switch (editProfileChoice) {
                case "1":
                    Input _editName = new Input(scanner, "Enter new name: ")
                                            .isNotEmpty().isAlphabetic().validate();
                    if (_editName.isExit()) break;
                    String newName = _editName.get();
                    profile.setName(newName);
                    PatientRepository.modifyFile(LoginState.getLoginId(), p -> { p.setName(newName); return p;});
                    UserInterface.success("Name successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "2":
                    Input _editAge = new Input(scanner, "Enter new age: ")
                                            .isNotEmpty().isNumeric().validate();
                    if (_editAge.isExit()) break;
                    int newAge = _editAge.getInteger();
                    profile.setAge(newAge);
                    PatientRepository.modifyFile(LoginState.getLoginId(), p -> { p.setAge(newAge); return p;});
                    UserInterface.success("Age successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "3":
                    Input _editAddress = new Input(scanner, "Enter new address: ")
                                            .isNotEmpty().validate();
                    if (_editAddress.isExit()) break;
                    String newAddress = _editAddress.get();
                    profile.setAddress(newAddress);
                    PatientRepository.modifyFile(LoginState.getLoginId(), p -> { p.setAddress(newAddress); return p;});
                    UserInterface.success("Address successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "4":
                    Input _editPhoneNumber = new Input(scanner, "Enter new phone number: ")
                                            .isNotEmpty().isValidPhoneNumber().validate();
                    if (_editPhoneNumber.isExit()) break;
                    String newPhoneNumber = _editPhoneNumber.get();
                    profile.setPhoneNumber(newPhoneNumber);
                    PatientRepository.modifyFile(LoginState.getLoginId(), p -> { p.setPhoneNumber(newPhoneNumber); return p;});
                    UserInterface.success("Phone number successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                case "5":
                    while (true) {
                        Input _currentPassword = new Input(scanner, "Enter your current password: ")
                                                .isNotEmpty().validate();
                        if (_currentPassword.isExit()) break switchLoop;
                        if (!profile.validatePassword(_currentPassword.get())) {
                            UserInterface.warning("Invalid password!");
                        } else break;
                    }
                    Input _changePassword = new Input(scanner, "Enter new password (must be alphanumeric): ")
                                                .isNotEmpty().isAlphanumeric().validate();
                    if (_changePassword.isExit()) break;
                    String newPassword = _changePassword.get();
                    profile.setPassword(newPassword);
                    PatientRepository.modifyFile(LoginState.getLoginId(), p -> { p.setPassword(newPassword); return p;});
                    UserInterface.success("Password successfully updated!");
                    UserInterface.enter(scanner);
                    break;
                default:
                    UserInterface.warning("Invalid choice!");
                    UserInterface.enter(scanner);
            }
        }
    }
}
