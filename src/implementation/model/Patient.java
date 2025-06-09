package implementation.model;

import adt.LinkedList;
import implementation.model.interfaces.Identifiable;
import utility.Hasher;
import utility.UserInterface;

public class Patient implements Identifiable {
    private int id;
    private String password; // must be hashed
    private String name;
    private int age;
    private String address;
    private String phoneNumber;
    private LinkedList<MedicalRecord> medicalRecords;

    public Patient(int id, String password, String name, int age, String address, String phoneNumber) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.medicalRecords = new LinkedList<>();
    }

    public boolean validatePassword(String password) { return Hasher.hash(password).equals(this.password); }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public LinkedList<MedicalRecord> getMedicalRecords() { return medicalRecords; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void addMedicalRecord(MedicalRecord record) { this.medicalRecords.insert(record); }
    public void setPassword(String password) { this.password = Hasher.hash(password); }

    public String toFileString() { // wip
        return id + "|" + password + "|" + name + "|" + age + "|" + address + "|" + phoneNumber;
    }

    public static Patient fromFileString(String line) { // wip
        String[] parts = line.split("\\|");
        return new Patient(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            Integer.parseInt(parts[3]),
            parts[4],
            parts[5]
        );
    }

    @Override
    public int getId() { return id; }

    @Override
    public String toString() {
        return "Patient " + UserInterface.colorize("#" + id, UserInterface.YELLOW) + 
        "\n > Name: " + name + 
        "\n > Age: " + age + 
        "\n > Address: " + address + 
        "\n > Phone Number: " + phoneNumber + "\n";
    }
}
