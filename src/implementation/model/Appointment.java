package implementation.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import implementation.model.enums.AppointmentStatus;
import implementation.model.interfaces.Identifiable;
import shared.LoginState;
import shared.enums.Role;
import shared.repository.DoctorRepository;
import shared.repository.PatientRepository;
import shared.repository.SpecialtyRepository;
import utility.UserInterface;

public class Appointment implements Identifiable {
    private int id;
    private int specialtyId;
    private int patientId;
    private int doctorId;
    private LocalDateTime time;
    private AppointmentStatus status;

    public Appointment(int id, int specialtyId, int patientId, int doctorId, LocalDateTime time) {
        this.id = id;
        this.specialtyId = specialtyId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.time = time;
        this.status = AppointmentStatus.SCHEDULED;
    }

    public Appointment(int id, int specialtyId, int patientId, int doctorId, LocalDateTime time, AppointmentStatus status) {
        this.id = id;
        this.specialtyId = specialtyId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.time = time;
        this.status = status;
    }

    public int getSpecialtyId() { return specialtyId; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public LocalDateTime getTime() { return time; }
    public AppointmentStatus getStatus() { return status; }

    public void setStatus(AppointmentStatus status) { this.status = status; }

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

        return id + "|" + specialtyId + "|" + patientId + "|" + doctorId + "|" + formattedDateTime + "|" + statusNumber;
    }

    public static Appointment fromFileString(String line) {
        String[] parts = line.split("\\|");
        int statusNumber = Integer.parseInt(parts[5]);
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
            LocalDateTime.parse(parts[4]),
            appointmentStatus
        );
    }

    @Override
    public int getId() { return id; }

    @Override
    public String toString() {
        Patient patient = PatientRepository.findById(patientId);
        Doctor doctor = DoctorRepository.findById(doctorId);
        Specialty specialty = SpecialtyRepository.findById(specialtyId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        if (status == AppointmentStatus.SCHEDULED) {
            if (time.equals(LocalDateTime.now()) || LocalDateTime.now().isAfter(time)) {
                this.setStatus(AppointmentStatus.ONGOING);
            }
        }

        String statusString = "";
        switch (status) {
            case SCHEDULED:
                String today = "";
                if (time.getDayOfWeek().equals(LocalDateTime.now().getDayOfWeek())) today = " (Today)";
                statusString = UserInterface.colorize("SCHEDULED", UserInterface.YELLOW) + today;
                break;
            case CANCELLED:
                statusString = UserInterface.colorize("CANCELLED", UserInterface.RED);
                break;
            case ONGOING:
                if (!time.getDayOfWeek().equals(LocalDateTime.now().getDayOfWeek()) && LocalDateTime.now().isAfter(time)) {
                    statusString = UserInterface.colorize("ONGOING", UserInterface.BLUE) + " - " + UserInterface.colorize("Not Processed Yet", UserInterface.ORANGE);
                } else {
                    statusString = UserInterface.colorize("ONGOING", UserInterface.BLUE);
                }
                break;
            case PROCESSED:
                statusString = UserInterface.colorize("PROCESSED", UserInterface.GREEN);
                break;
        }

        String patientInfo = "\n║ > Patient: " + (patient == null ? "Unknown" : (patient.getName() + " (" + UserInterface.colorize("#" + patientId, UserInterface.YELLOW) + ")"));
 
        return "║ Appointment " + "[" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + "]" + (LoginState.getRole() == Role.DOCTOR && LoginState.getLoginId() == doctorId ? " - Your Appointment" : "") + 
        (LoginState.getRole() == Role.PATIENT && LoginState.getLoginId() == patientId ? "" : patientInfo) + 
        "\n║ > Doctor: " + (doctor == null ? "Unknown" : ("dr. " + doctor.getName() + " (" + UserInterface.colorize("#" + doctorId, UserInterface.YELLOW) + ")")) + 
        "\n║ > Specialty: " + (specialty == null ? "Unknown" : (specialty.getName() + " (" + UserInterface.colorize("#" + specialtyId, UserInterface.YELLOW) + ")")) + 
        "\n║ > Time: " + UserInterface.colorize(time.format(formatter), UserInterface.GREEN) +
        "\n║ > Status: " + statusString +
        "\n╠════════════════════════════════════════════════";
    }
}
