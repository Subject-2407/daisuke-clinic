package implementation.model;

import adt.LinkedList;
import adt.PriorityQueue;
import implementation.model.interfaces.Identifiable;
import shared.repository.SpecialtyRepository;
import utility.Hasher;
import utility.UserInterface;

public class Doctor implements Identifiable {
    private int id;
    private String password; // must be hashed
    private String name;
    private Specialty specialty;
    private String phoneNumber;
    private PriorityQueue<Appointment> upcomingAppointments;
    private LinkedList<Appointment> appointmentHistory;

    public Doctor(int id, String password, String name, Specialty specialty, String phoneNumber) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.specialty = specialty;
        this.phoneNumber = phoneNumber;
        this.upcomingAppointments = new PriorityQueue<>();
        this.appointmentHistory = new LinkedList<>();
    }

    public boolean validatePassword(String password) { return Hasher.hash(password).equals(this.password); }
    public String getName() { return name; }
    public Specialty getSpecialty() { return specialty; }
    public String getPhoneNumber() { return phoneNumber; }
    public PriorityQueue<Appointment> getUpcomingAppointments() { return upcomingAppointments; }
    public LinkedList<Appointment> getAppointmentHistory() { return appointmentHistory; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPassword(String password) { this.password = Hasher.hash(password); }

    public String toFileString() { // wip
        return id + "|" + password + "|" + name + "|" + specialty.getId() + "|" + phoneNumber;
    }

    public static Doctor fromFileString(String line) { // wip
        String[] parts = line.split("\\|");

        int specialtyId = Integer.parseInt(parts[3]);
        Specialty specialty = SpecialtyRepository.findById(specialtyId);

        return new Doctor(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            specialty,
            parts[4]
        );
    }

    @Override
    public int getId() { return id; }

    @Override
    public String toString() { 
        return "Doctor " + UserInterface.colorize("#" + id, UserInterface.YELLOW) +
        "\n > Name: " + name +
        "\n > Specialty: " + (specialty == null ? "Unknown" : (specialty.getName() + " (" + UserInterface.colorize("#" + specialty.getId(), UserInterface.YELLOW) + ")")) +
        "\n > Phone Number: " + phoneNumber + "\n";
    }
}
