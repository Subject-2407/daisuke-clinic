package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import adt.BST;
import implementation.model.Specialty;

public class SpecialtyRepository {
    private static BST<Specialty> specialtyTree = new BST<>();
    private static final String filePath = Paths.get("saves", "specialties.txt").toString();
    private static final String tempFilePath = Paths.get("saves", "temp_specialties.txt").toString();
    // private static final String filePath = "src/saves/specialties.txt";
    // private static final String tempFilePath = "src/saves/temp_specialties.txt"; // for deleting purposes

    public static Object[] getAll() {
        return specialtyTree.toArray();
    }

    public static void viewAll() {
        specialtyTree.inOrder();
    }

    public static void add(Specialty specialty) {
        specialtyTree.insert(specialty);
        try {
            saveToFile(specialty);
        } catch (IOException e) {
            System.err.println("Failed to save specialty data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Specialty findById(int id) {
        return specialtyTree.search(id);
    }

    public static void remove(int id) {
        specialtyTree.remove(id);
        try {
            removeFromFile(id);
        } catch (IOException e) {
            System.err.println("Failed to remove specialty data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected static void resetSpecialtiesAvailableDoctors() {
        Object[] specialties = specialtyTree.toArray();
        for (Object obj : specialties) {
            Specialty specialty = (Specialty) obj;
            specialty.setAvailableDoctors(0);
        }
    }

    private static void saveToFile(Specialty specialty) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(specialty.toFileString());
        writer.newLine();
        writer.close();
    }

    public static void load() throws IOException {
        specialtyTree = new BST<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            Specialty specialty = Specialty.fromFileString(line);
            specialtyTree.insert(specialty);
        }
        reader.close();
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
                Specialty specialty = Specialty.fromFileString(line);
                if (specialty.getId() != targetId) {
                    writer.write(specialty.toFileString());
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
