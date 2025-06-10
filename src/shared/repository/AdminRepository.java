package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import adt.BST;
import implementation.model.Admin;

public class AdminRepository {
    private static BST<Admin> adminTree = new BST<>();
    private static final String filePath = "src/saves/admins.txt";
    private static final String tempFilePath = "src/saves/temp_admins.txt"; // for deleting purposes

    public static void getAll() {
        adminTree.inOrder();
    }

    public static void add(Admin admin) {
        adminTree.insert(admin);
        try {
            saveToFile(admin);
        } catch (IOException e) {
            System.err.println("Failed to save admin data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Admin findById(int id) {
        return adminTree.search(id);
    }

    public static void remove(int id) {
        adminTree.remove(id);
        try {
            removeFromFile(id);
        } catch (IOException e) {
            System.err.println("Failed to remove admin data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveToFile(Admin admin) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(admin.toFileString());
        writer.newLine();
        writer.close();
    }

    public static void load() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            Admin admin = Admin.fromFileString(line);
            adminTree.insert(admin);
        }
        reader.close();
    }

    public static void removeFromFile(int targetId) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                Admin admin = Admin.fromFileString(line);
                if (admin.getId() != targetId) {
                    writer.write(admin.toFileString());
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
