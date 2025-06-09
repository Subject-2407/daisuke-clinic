package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import adt.BST;
import implementation.model.Admin;

public class AdminRepository {
    private static BST<Admin> adminTree = new BST<>();
    private static final String filePath = "src/saves/admins.txt";

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
}
