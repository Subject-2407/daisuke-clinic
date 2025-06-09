package implementation.model;

import implementation.model.interfaces.Identifiable;
import utility.Hasher;

public class Admin implements Identifiable {
    private int id;
    private String password;
    private String name;

    public Admin(int id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public boolean validatePassword(String password) { return Hasher.hash(password).equals(this.password); }
    public String getName() { return name; }

    public void setPassword(String password) { this.password = Hasher.hash(password); }

    public String toFileString() {
        return id + "|" + password + "|" + name;
    }

    public static Admin fromFileString(String line) {
        String[] parts = line.split("\\|");

        return new Admin(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2]
        );
    }

    @Override
    public int getId() { return id; }
}
