package implementation.model;

import implementation.model.interfaces.Identifiable;
import utility.Hasher;
import utility.UserInterface;

public class Admin implements Identifiable {
    private int id;
    private String password; // should be hashed
    private String name;
    private String phoneNumber;

    public Admin(int id, String password, String name, String phoneNumber) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public boolean validatePassword(String password) { return Hasher.hash(password).equals(this.password); }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPassword(String password) { this.password = Hasher.hash(password); }

    public String toFileString() {
        return id + "|" + password + "|" + name + "|" + phoneNumber;
    }

    public static Admin fromFileString(String line) {
        String[] parts = line.split("\\|");

        return new Admin(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            parts[3]
        );
    }

    @Override
    public int getId() { return id; }

    @Override
    public String toString() {
        return
        "║ [" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + "] "  + name +
        "\n║ > Phone Number: " + phoneNumber +
        "\n╠════════════════════════════════════════════════";
    }
}
