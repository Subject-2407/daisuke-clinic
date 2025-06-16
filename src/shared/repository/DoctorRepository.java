package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

import adt.BST;
import implementation.model.Doctor;
import implementation.model.Specialty;

public class DoctorRepository {
    private static BST<Doctor> doctorTree = new BST<>();
    private static final String filePath = "src/saves/doctors.txt";
    private static final String tempFilePath = "src/saves/temp_doctors.txt"; // for deleting purposes

    public static int getRepositorySize() { return doctorTree.size(); }
    
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

    public static Object[] findAll(Predicate<Doctor> predicate) {
        return doctorTree.searchAll(predicate);
    }

    public static Doctor findById(int id) {
        return doctorTree.search(id);
    }

    public static void modifyFile(int id, Function<Doctor, Doctor> modifier) {
        try {
            modifyLineInFile(id, modifier);
        } catch (IOException e) {
            System.err.println("Failed to update doctor data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void remove(int id) {
        doctorTree.remove(id);
        try {
            removeFromFile(id);
        } catch (IOException e) {
            System.err.println("Failed to remove doctor data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveToFile(Doctor doctor) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(doctor.toFileString());
        writer.newLine();
        writer.close();
    }

    public static void load() throws IOException {
        SpecialtyRepository.resetSpecialtiesAvailableDoctors(); // do not remove
        doctorTree = new BST<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            Doctor doctor = Doctor.fromFileString(line);
            doctorTree.insert(doctor);
        }
        reader.close();
    }

    private static void modifyLineInFile(int targetId, Function<Doctor, Doctor> modifier) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                Doctor doctor = Doctor.fromFileString(line);
                if (doctor.getId() == targetId) {
                    doctor = modifier.apply(doctor); 
                }
                writer.write(doctor.toFileString());
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
            SpecialtyRepository.resetSpecialtiesAvailableDoctors();
            while ((line = reader.readLine()) != null) {
                Doctor doctor = Doctor.fromFileString(line);
                if (doctor.getId() != targetId) {
                    writer.write(doctor.toFileString());
                    writer.newLine();
                } else {
                    Specialty removedDoctorSpecialty = SpecialtyRepository.findById(doctor.getSpecialtyId());
                    removedDoctorSpecialty.setAvailableDoctors(removedDoctorSpecialty.getAvailableDoctors() - 1);
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
