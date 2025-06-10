package implementation.model;

import adt.BST;
import adt.PriorityQueue;
import implementation.model.enums.Gender;
import implementation.model.interfaces.Identifiable;
import utility.Hasher;
import utility.UserInterface;

public class Patient implements Identifiable {
    private int id;
    private String password; // should be hashed
    private String name;
    private Gender gender;
    private int age;
    private String address;
    private String phoneNumber;
    private PriorityQueue<Appointment> upcomingAppointments;
    private BST<Appointment> appointmentHistory;
    private BST<MedicalRecord> medicalRecords;

    public Patient(int id, String password, String name, Gender gender, int age, String address, String phoneNumber) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.upcomingAppointments = new PriorityQueue<>();
        this.appointmentHistory = new BST<>();
        this.medicalRecords = new BST<>();
    }

    public boolean validatePassword(String password) { return Hasher.hash(password).equals(this.password); }
    public String getName() { return name; }
    public Gender getGender() { return gender; }
    public int getAge() { return age; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public PriorityQueue<Appointment> getUpcomingAppointments() { return upcomingAppointments; }
    public BST<Appointment> getAppointmentHistory() { return appointmentHistory; }
    public BST<MedicalRecord> getMedicalRecords() { return medicalRecords; }

    public void setName(String name) { this.name = name; }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setAge(int age) { this.age = age; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void enqueueAppointment(Appointment appointment) { this.upcomingAppointments.enqueue(appointment, appointment.getTime()); }
    public void dequeueAppointment() { this.upcomingAppointments.dequeue(); }
    public void addAppointmentHistory(Appointment appointment) { this.appointmentHistory.insert(appointment); }
    public void addMedicalRecord(MedicalRecord record) { this.medicalRecords.insert(record); }
    public void setPassword(String password) { this.password = Hasher.hash(password); }

    public String toFileString() {
        int genderStatus = gender == Gender.MALE ? 0 : 1;
        return id + "|" + password + "|" + name + "|" + genderStatus + "|" + age + "|" + address + "|" + phoneNumber;
    }

    public static Patient fromFileString(String line) {
        String[] parts = line.split("\\|");
        Gender gender = Integer.parseInt(parts[3]) == 0 ? Gender.MALE : Gender.FEMALE;
        return new Patient(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            gender,
            Integer.parseInt(parts[4]),
            parts[5],
            parts[6]
        );
    }

    @Override
    public int getId() { return id; }

    @Override
    public String toString() {
        String genderString = gender.toString().substring(0, 1).toUpperCase() + gender.toString().substring(1).toLowerCase();
        return
        "[" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + "] " + name +
        "\n > Gender: " + genderString + 
        "\n > Age: " + age + 
        "\n > Address: " + address + 
        "\n > Phone Number: " + phoneNumber + 
        "\n-------------------------------------------------";
    }
}
