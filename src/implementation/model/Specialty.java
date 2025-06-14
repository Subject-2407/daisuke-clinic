package implementation.model;

import adt.PriorityQueue;
import implementation.model.enums.AppointmentStatus;
import implementation.model.interfaces.Identifiable;
import utility.UserInterface;

// Class for specialties / departments

public class Specialty implements Identifiable {
    private int id;
    private String name;
    private int maxSlots; // maximum number of appointments allowed for this department
    private int availableDoctors;
    private PriorityQueue<Appointment> appointmentQueue;

    public Specialty(int id, String name, int maxSlots) {
        this.id = id;
        this.name = name;
        this.maxSlots = maxSlots;
        this.availableDoctors = 0;
        this.appointmentQueue = new PriorityQueue<>();
    }

    public String getName() { return name; }
    public int getMaxSlots() { return maxSlots; }
    public PriorityQueue<Appointment> getAppointmentQueue() { return appointmentQueue; }
    public int getAvailableDoctors() { return availableDoctors; }

    public Appointment peekAppointment() { return this.appointmentQueue.peek(); }
    public void enqueueAppointment(Appointment appointment) { this.appointmentQueue.enqueue(appointment, appointment.getTime()); }
    public void dequeueAppointment(AppointmentStatus status) { this.appointmentQueue.dequeue().setStatus(status); }
    public void setAvailableDoctors(int value) { this.availableDoctors = value; }

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
        return 
        "[" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + "] " + name +
        "\n  - Available Doctors: " + availableDoctors +
        "\n  - Appointment Slots: " + appointmentQueue.size() + " of " + maxSlots + " available.\n";
    }
}
