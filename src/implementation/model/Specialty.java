package implementation.model;

import adt.PriorityQueue;
import implementation.model.interfaces.Identifiable;
import utility.UserInterface;

// Class for specialties / departments

public class Specialty implements Identifiable {
    private int id;
    private String name;
    private int maxSlots; // maximum number of appointments allowed for this department
    private PriorityQueue<Appointment> queue;

    public Specialty(int id, String name, int maxSlots) {
        this.id = id;
        this.name = name;
        this.maxSlots = maxSlots;
        this.queue = new PriorityQueue<>();
    }

    public String getName() { return name; }
    public int getMaxSlots() { return maxSlots; }
    public PriorityQueue<Appointment> getQueue() { return queue; }

    public String toFileString() {
        return id + "|" + name + "|" + maxSlots;
    }

    public static Specialty fromFileString(String line) {
        String[] parts = line.split("\\|");

        return new Specialty(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]));
    }

    @Override
    public int getId() { return id; }

    @Override
    public String toString() {
        return "[" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + "] " + name;
    }
}
