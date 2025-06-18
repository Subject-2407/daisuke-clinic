package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Predicate;

import adt.BST;
import implementation.model.MedicalRecord;
import implementation.model.Patient;

public class MedicalRecordRepository {
    private static BST<MedicalRecord> medicalRecordTree = new BST<>();

    private static final String filePath = Paths.get("saves", "medical_records.txt").toString();
    private static final String tempFilePath = Paths.get("saves", "temp_medical_records.txt").toString();
    // private static final String filePath = "src/saves/medical_records.txt";
    // private static final String tempFilePath = "src/saves/temp_medical_records.txt"; // for modifying purposes

    public static void add(MedicalRecord record) {
        medicalRecordTree.insert(record);
        try {
            saveToFile(record);
        } catch (IOException e) {
            System.err.println("Failed to save medical record data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object[] findAll(Predicate<MedicalRecord> predicate) {
        return medicalRecordTree.searchAll(predicate);
    }


    public static MedicalRecord findById(int id) {
        return medicalRecordTree.search(id);
    }

    private static void saveToFile(MedicalRecord record) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(record.toFileString());
        writer.newLine();
        writer.close();
    }

    public static void modifyFile(int id, Function<MedicalRecord, MedicalRecord> modifier) {
        try {
            modifyLineInFile(id, modifier);
        } catch (IOException e) {
            System.err.println("Failed to update medical record data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void load() throws IOException {
        medicalRecordTree = new BST<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            MedicalRecord record = MedicalRecord.fromFileString(line);
            Patient patient = PatientRepository.findById(record.getPatientId());
            medicalRecordTree.insert(record);
            if (patient != null) {
                patient.setMedicalRecord(record);
            }   
        }
        reader.close();
    }

    private static void modifyLineInFile(int targetId, Function<MedicalRecord, MedicalRecord> modifier) throws IOException {
         File inputFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                MedicalRecord record = MedicalRecord.fromFileString(line);
                if (record.getId() == targetId) {
                    record = modifier.apply(record); 
                }
                writer.write(record.toFileString());
                writer.newLine();
            }
        }

        if (!inputFile.delete()) {
            throw new IOException("Could not delete original file");
        }
        if (!tempFile.renameTo(inputFile)) {
            throw new IOException("Could not rename temporary file");
        }
    }
}
