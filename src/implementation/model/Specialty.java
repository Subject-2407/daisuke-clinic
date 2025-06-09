package implementation.model;

import implementation.model.interfaces.Identifiable;
import utility.UserInterface;

public class Specialty implements Identifiable {
    private int id;
    private String name;

    public Specialty(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() { return name; }

    public String toFileString() {
        return id + "|" + name;
    }

    public static Specialty fromFileString(String line) {
        String[] parts = line.split("\\|");

        return new Specialty(Integer.parseInt(parts[0]), parts[1]);
    }

    @Override
    public int getId() { return id; }

    @Override
    public String toString() {
        return "[" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + "] " + name;
    }
}
