package implementation.controller;

import java.time.LocalDateTime;
import java.util.Scanner;

import implementation.model.MedicalRecord;
import shared.repository.MedicalRecordRepository;
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

    public static void updateMedicalRecord(Scanner scanner, int doctorId) {
        outerLoop: while (true) {
            UserInterface.update("Check Patient's Medical Record");
            System.out.println("*) You may only access the medical records of patients you are currently handling.");
            System.out.println("*) Enter 0 to exit\n");
            
            MedicalRecord medicalRecord = null;

            while (medicalRecord == null) {
                Input _medicalRecordId = new Input(scanner, "Enter patient's ID (in number): ")
                                            .isNotEmpty().isNumeric().validate();
                if (_medicalRecordId.isExit()) return;
                int medicalRecordId = _medicalRecordId.getInteger();

                medicalRecord = MedicalRecordRepository.findById(medicalRecordId);

                if (medicalRecord == null) {
                    UserInterface.warning("Can't find a patient with this ID!");
                    continue;
                }

                if (medicalRecord.getDoctorId() != doctorId) {
                    UserInterface.warning("You are not currently in charge of handling this patient!");
                    medicalRecord = null;
                    continue;
                }
            }

            System.out.println();
            UserInterface.info("Medical record summary: ");
            System.out.println(medicalRecord);

            System.out.println();
            String[] options = {"Update Medical Record"};
            UserInterface.createOptions(options);

            System.out.println();
            while (true) {
                Input _choice = new Input(scanner, "Enter choice: ").validate();
                if (_choice.isExit()) return;
                String choice = _choice.get();
                if (choice.equals("1")) {
                    System.out.println("\n*) Enter N in each field to skip");
                    String presentingComplaints = null, diagnosis = null, treatment = null, prescription = null, additionalNote = null;

                    // PC input
                    Input _presentingComplaints = new Input(scanner, "Enter presenting complaints: ")
                                                    .isNotEmpty().validate();
                    if (_presentingComplaints.isExit()) break outerLoop;
                    if (!_presentingComplaints.get().toString().toUpperCase().equals("N")) {
                        presentingComplaints = _presentingComplaints.get();
                    }

                    // diagnosis input
                    Input _diagnosis = new Input(scanner, "Enter diagnosis: ")
                                                    .isNotEmpty().validate();
                    if (_diagnosis.isExit()) break outerLoop;
                    if (!_diagnosis.get().toString().toUpperCase().equals("N")) {
                        diagnosis = _diagnosis.get();
                    }

                    // diagnosis input
                    Input _treatment = new Input(scanner, "Enter treatment: ")
                                                    .isNotEmpty().validate();
                    if (_treatment.isExit()) break outerLoop;
                    if (!_treatment.get().toString().toUpperCase().equals("N")) {
                        treatment = _treatment.get();
                    }

                    // prescription input
                    Input _prescription = new Input(scanner, "Enter prescription: ")
                                                    .isNotEmpty().validate();
                    if (_prescription.isExit()) break outerLoop;
                    if (!_prescription.get().toString().toUpperCase().equals("N")) {
                        prescription = _prescription.get();
                    }

                    // additional note input
                    Input _additionalNote = new Input(scanner, "Enter additional note: ")
                                                    .isNotEmpty().validate();
                    if (_additionalNote.isExit()) break outerLoop;
                    if (!_additionalNote.get().toString().toUpperCase().equals("N")) {
                        additionalNote = _additionalNote.get();
                    }

                    System.out.println();

                    if (presentingComplaints != null) {
                        final String patientComplaints = presentingComplaints;
                        medicalRecord.setPresentingComplaints(presentingComplaints);
                        MedicalRecordRepository.modifyFile(medicalRecord.getId(), m -> { m.setPresentingComplaints(patientComplaints); return m;}); // med record's id shares the same id with patient's id

                    }
                    if (diagnosis != null) {
                        final String patientDiagnosis = diagnosis;
                        medicalRecord.setDiagnosis(patientDiagnosis);
                        MedicalRecordRepository.modifyFile(medicalRecord.getId(), m -> { m.setDiagnosis(patientDiagnosis); return m;});
                    }
                    if (treatment != null) {
                        final String patientTreatment = treatment;
                        medicalRecord.setTreatment(patientTreatment);
                        MedicalRecordRepository.modifyFile(medicalRecord.getId(), m -> { m.setTreatment(patientTreatment); return m;});
                    }
                    if (prescription != null) {
                        final String doctorPrescription = prescription;
                        medicalRecord.setPrescription(doctorPrescription);
                        MedicalRecordRepository.modifyFile(medicalRecord.getId(), m -> { m.setPrescription(doctorPrescription); return m;});
                    }
                    if (additionalNote != null) {
                        final String doctorAdditionalNote = additionalNote;
                        medicalRecord.setAdditionalNote(doctorAdditionalNote);
                        MedicalRecordRepository.modifyFile(medicalRecord.getId(), m -> { m.setAdditionalNote(doctorAdditionalNote); return m;});
                    }
                    if (presentingComplaints != null || diagnosis != null || treatment != null || prescription != null || additionalNote != null) {
                        medicalRecord.setRecordLastUpdated(LocalDateTime.now());
                        MedicalRecordRepository.modifyFile(medicalRecord.getId(), m -> { m.setRecordLastUpdated(LocalDateTime.now()); return m;});
                        UserInterface.success("Successfully updated patient's medical record!");
                    } else {
                        UserInterface.info("It looks like you haven't updated anything yet..");
                    }
                    UserInterface.enter(scanner);
                    break;
                } else if (choice.equals("0")) {
                    break outerLoop;
                } else {
                    UserInterface.warning("Invalid choice!");
                }
            }
        }
    }
}
