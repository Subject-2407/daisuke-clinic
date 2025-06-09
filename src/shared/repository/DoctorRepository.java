package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import adt.BST;
import implementation.model.Doctor;

public class DoctorRepository {
    private static BST<Doctor> doctorTree = new BST<>();
    private static final String filePath = "src/saves/doctors.txt";

    public static void getAll() {
        doctorTree.inOrder();
    }

    public static void add(Doctor doctor) {
        doctorTree.insert(doctor);
        try {
            saveToFile(doctor);
        } catch (IOException e) {
            System.err.println("Failed to save doctor data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Doctor findById(int id) {
        return doctorTree.search(id);
    }

    public static void remove(int id) {
        doctorTree.remove(id);
    }

    private static void saveToFile(Doctor doctor) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(doctor.toFileString());
        writer.newLine();
        writer.close();
    }

    public static void load() throws IOException {
        doctorTree = new BST<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            Doctor doctor = Doctor.fromFileString(line);
            doctorTree.insert(doctor);
        }
        reader.close();
    }
}
