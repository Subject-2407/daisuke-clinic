package implementation.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import implementation.model.enums.AppointmentStatus;
import implementation.model.interfaces.Identifiable;
import shared.repository.PatientRepository;
import utility.UserInterface;

public class Appointment implements Identifiable {
    private int id;
    private int specialtyId;
    private int patientId;
    private int doctorId;
    private String presentingComplaints;
    private LocalDateTime time;
    private AppointmentStatus status;

    public Appointment(int id, int patientId, int specialtyId, int doctorId, String presentingComplaints, LocalDateTime time) {
        this.id = id;
        this.specialtyId = specialtyId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.presentingComplaints = presentingComplaints;
        this.time = time;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public Appointment(int id, int patientId, int specialtyId, int doctorId, String presentingComplaints, LocalDateTime time, AppointmentStatus status) {
        this.id = id;
        this.specialtyId = specialtyId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.presentingComplaints = presentingComplaints;
        this.time = time;
        this.status = status;
    }

    public int getSpecialtyId() { return specialtyId; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public String getPresentingComplaints() { return presentingComplaints; }
    public LocalDateTime getTime() { return time; }
    public AppointmentStatus getStatus() { return status; }

    public String toFileString() {
        int statusNumber = -1;
        switch (status) {
            case SCHEDULED:
                statusNumber = 1;
                break;
            case CANCELLED:
                statusNumber = 2;
                break;
            case ONGOING:
                statusNumber = 3;
                break;
            case PROCESSED:
                statusNumber = 4;
                break;
            default:
                statusNumber = -1;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String formattedDateTime = time.format(formatter);

        return id + "|" + specialtyId + "|" + patientId + "|" + doctorId + "|" + presentingComplaints + "|" + formattedDateTime + "|" + statusNumber;
    }

    public static Appointment fromFileString(String line) {
        String[] parts = line.split("\\|");
        int statusNumber = Integer.parseInt(parts[6]);
        AppointmentStatus appointmentStatus;
        switch (statusNumber) {
            case 1:
                appointmentStatus = AppointmentStatus.SCHEDULED;
                break;
            case 2:
                appointmentStatus = AppointmentStatus.CANCELLED;
                break;
            case 3:
                appointmentStatus = AppointmentStatus.ONGOING;
                break;
            case 4:
                appointmentStatus = AppointmentStatus.PROCESSED;
                break;
            default:
                appointmentStatus = AppointmentStatus.CANCELLED;
        }

        return new Appointment(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2]),
            Integer.parseInt(parts[3]),
            parts[4],
            LocalDateTime.parse(parts[5]),
            appointmentStatus
        );
    }

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
