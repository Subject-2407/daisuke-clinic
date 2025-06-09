package implementation.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import implementation.model.enums.AppointmentStatus;
import implementation.model.interfaces.Identifiable;
import shared.repository.PatientRepository;
import utility.UserInterface;

public class Appointment implements Identifiable {
    private int id;
    private int patientId;
    private int doctorId;
    private String presentingComplaints;
    private LocalDateTime time;
    private AppointmentStatus status;

    public Appointment(int id, int patientId, int doctorId, String presentingComplaints, LocalDateTime time) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.presentingComplaints = presentingComplaints;
        this.time = time;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public String getPresentingComplaints() { return presentingComplaints; }
    public LocalDateTime getTime() { return time; }
    public AppointmentStatus getStatus() { return status; }

    @Override
    public int getId() { return id; }
    @Override
    public String toString() {
        Patient patient = PatientRepository.findById(patientId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        return "Appointment " + UserInterface.colorize("#" + id, UserInterface.YELLOW) + 
        "\n > Patient : " + patient.getName() + " (" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + ")" + 
        "\n > Doctor : " + patient.getName() + " (" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + ")" + 
        "\n > Time: " + time.format(formatter) + "\n";
    }
}
