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
import adt.LinkedList;
import implementation.model.Patient;

public class PatientRepository {
    private static LinkedList<Patient> patientList = new LinkedList<>();
    private static BST<Patient> patientTree = new BST<>(); // for faster data lookup (by id)
    private static final String filePath = Paths.get("saves", "patients.txt").toString();
    private static final String tempFilePath = Paths.get("saves", "temp_patients.txt").toString();
    // private static final String filePath = "src/saves/patients.txt";
    // private static final String tempFilePath = "src/saves/temp_patients.txt"; // for deleting purposes

    public static int getRepositorySize() { return patientTree.size(); }

    public static Object[] getAll() {
        return patientList.toArray();
    }

    public static void getAllInorder() {
        patientTree.inOrder();
    }

    public static void add(Patient patient) {
        patientList.insert(patient);
        patientTree.insert(patient);
        try {
            saveToFile(patient);
        } catch (IOException e) {
            System.err.println("Failed to save patient data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Patient find(Predicate<Patient> predicate) {
        return patientList.find(predicate);
    }

    public static Object[] findAll(Predicate<Patient> predicate) {
        return patientList.findAll(predicate);
    }

    public static Patient findById(int id) {
        return patientTree.search(id);
    }

    public static void modifyFile(int id, Function<Patient, Patient> modifier) {
        try {
            modifyLineInFile(id, modifier);
        } catch (IOException e) {
            System.err.println("Failed to update patient data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Patient remove(int id) {
        patientTree.remove(id);
        Patient removedPatient = patientList.removeIf(p -> p.getId() == id);
        try {
            removeFromFile(id);
        } catch (IOException e) {
            System.err.println("Failed to remove doctor data: " + e.getMessage());
            e.printStackTrace();
        }
        return removedPatient;
    }

    private static void saveToFile(Patient patient) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(patient.toFileString());
        writer.newLine();
        writer.close();
    }

    public static void load() throws IOException {
        patientList = new LinkedList<>();
        patientTree = new BST<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            Patient patient = Patient.fromFileString(line);
            patientList.insert(patient);
            patientTree.insert(patient);
        }
        reader.close();
    }

    private static void modifyLineInFile(int targetId, Function<Patient, Patient> modifier) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                Patient patient = Patient.fromFileString(line);
                if (patient.getId() == targetId) {
                    patient = modifier.apply(patient); 
                }
                writer.write(patient.toFileString());
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

    private static void removeFromFile(int targetId) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                Patient patient = Patient.fromFileString(line);
                if (patient.getId() != targetId) {
                    writer.write(patient.toFileString());
                    writer.newLine();
                }
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
