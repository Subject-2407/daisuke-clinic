package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Predicate;

import adt.BST;
import adt.LinkedList;
import implementation.model.Patient;

public class PatientRepository {
    private static LinkedList<Patient> patientList = new LinkedList<>();
    private static BST<Patient> patientTree = new BST<>(); // for faster data lookup (by id)
    private static final String filePath = "src/saves/patients.txt";

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

    public static Patient remove(int id) {
        patientTree.remove(id);
        return patientList.removeIf(p -> p.getId() == id);
    }

    private static void saveToFile(Patient patient) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(patient.toFileString());
        writer.newLine();
        writer.close();
    }

    public static void load() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            Patient patient = Patient.fromFileString(line);
            patientList.insert(patient);
            patientTree.insert(patient);
        }
        reader.close();
    }
}
