package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Function;

import adt.BST;
import implementation.model.Admin;

public class AdminRepository {
    private static BST<Admin> adminTree = new BST<>();
    private static final String filePath = Paths.get("saves", "admins.txt").toString();
    private static final String tempFilePath = Paths.get("saves", "temp_admins.txt").toString();
    // private static final String filePath = "src/saves/admins.txt";
    // private static final String tempFilePath = "src/saves/temp_admins.txt"; // for deleting purposes

    public static int getRepositorySize() { return adminTree.size(); }

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

    public static void modifyFile(int id, Function<Admin, Admin> modifier) {
        try {
            modifyLineInFile(id, modifier);
        } catch (IOException e) {
            System.err.println("Failed to update admin data: " + e.getMessage());
            e.printStackTrace();
        }
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
        adminTree = new BST<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            Admin admin = Admin.fromFileString(line);
            adminTree.insert(admin);
        }
        reader.close();
    }

     private static void modifyLineInFile(int targetId, Function<Admin, Admin> modifier) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                Admin admin = Admin.fromFileString(line);
                if (admin.getId() == targetId) {
                    admin = modifier.apply(admin); 
                }
                writer.write(admin.toFileString());
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
