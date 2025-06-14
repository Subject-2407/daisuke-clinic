package implementation.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import implementation.model.enums.Gender;
import implementation.model.interfaces.Identifiable;
import shared.repository.AppointmentRepository;
import shared.repository.DoctorRepository;
import shared.repository.PatientRepository;
import utility.UserInterface;

// Medical record class

public class MedicalRecord implements Identifiable {
    private int id;
    private int patientId;
    private int doctorId;
    private int lastAppointmentId;
    private String presentingComplaints;
    private String diagnosis;
    private String treatment;
    private String prescription;
    private String additionalNote;
    private LocalDateTime recordLastUpdated;

    public MedicalRecord(int patientId) {
        this.id = patientId; // same as patient id
        this.patientId = patientId;
        this.doctorId = 0;
        this.lastAppointmentId = 0;
        this.presentingComplaints = "N/A";
        this.diagnosis = "N/A";
        this.treatment = "N/A";
        this.prescription = "N/A";
        this.additionalNote = "N/A";
        this.recordLastUpdated = LocalDateTime.now();
    }

    public MedicalRecord(int patientId, int doctorId, int lastAppointmentId, String presentingComplaints, String diagnosis, String treatment, String prescription, String additionalNote, LocalDateTime recordLastUpdated) {
        this.id = patientId; // same as patient id
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.lastAppointmentId = lastAppointmentId;
        this.presentingComplaints = presentingComplaints;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescription = prescription;
        this.additionalNote = additionalNote;
        this.recordLastUpdated = recordLastUpdated;
    }

    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public int getLastAppointmentId() { return lastAppointmentId; }
    public String getPresentingComplaints() { return presentingComplaints; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }
    public String getPrescription() { return prescription; }
    public String getAdditionalNote() { return additionalNote; }
    public LocalDateTime getRecordLastUpdated() { return recordLastUpdated; }

    public void setLastAppointmentId(int appointmentId) { this.lastAppointmentId = appointmentId; }
    public void setPresentingComplaints(String complaints) { this.presentingComplaints = complaints; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public void setAdditionalNote(String additionalNote) { this.additionalNote = additionalNote; }
    public void setRecordLastUpdated(LocalDateTime time) { this.recordLastUpdated = time; }
    
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String formattedDateTime = recordLastUpdated.format(formatter);

        return id + "|" + patientId + "|" + doctorId + "|"  + lastAppointmentId + "|" + presentingComplaints + "|" + diagnosis + "|" + treatment + "|" + prescription + "|" + additionalNote + "|" + formattedDateTime;
    }

    public static MedicalRecord fromFileString(String line) {
        String[] parts = line.split("\\|");

        return new MedicalRecord(
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2]),
            Integer.parseInt(parts[3]),
            parts[4],
            parts[5],
            parts[6],
            parts[7],
            parts[8],
            LocalDateTime.parse(parts[9])
        );
    }

    @Override
    public int getId() { return id; }
    @Override
    public String toString() {
        // Fungsi untuk mem-wrap teks panjang
        Function<String, String[]> wrapText = (text) -> {
            if (text == null) return new String[]{"N/A"};
            int maxLength = 50;
            if (text.length() <= maxLength) return new String[]{text};
            
            List<String> lines = new ArrayList<>();
            int index = 0;
            while (index < text.length()) {
                lines.add(text.substring(index, Math.min(index + maxLength, text.length())));
                index += maxLength;
            }
            return lines.toArray(new String[0]);
        };

        // Format untuk setiap baris dalam box
        String lineFormat = "║    %-56s ║%n";

        Patient patient = PatientRepository.findById(patientId);
        Doctor doctor = DoctorRepository.findById(doctorId);
        Appointment appointment = AppointmentRepository.findById(lastAppointmentId);
        LocalDateTime lastAppointmentTime = appointment != null ? appointment.getTime() : null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        String genderString = patient != null ? (patient.getGender() == Gender.MALE ? "M" : "F") : "";
        String patientString = patient != null ? (patient.getName() + " - " + patient.getAge() + "/" + genderString + " (" + UserInterface.colorize("#" + patientId, UserInterface.YELLOW) + ")") : UserInterface.colorize("N/A",UserInterface.YELLOW);
        String doctorString = doctor != null ? ("Dr. " + doctor.getName() + " (" + UserInterface.colorize("#" + doctorId, UserInterface.YELLOW) + ")") : UserInterface.colorize("N/A                                ",UserInterface.YELLOW);
        String appointmentSring = lastAppointmentTime != null ? UserInterface.colorize(lastAppointmentTime.format(formatter), UserInterface.BLUE) : UserInterface.colorize("N/A",UserInterface.BLUE);
    
        return String.format(
            "╔═════════════════════════════════════════════════════════════╗%n" +
            "║                       " + UserInterface.colorize("MEDICAL RECORD", UserInterface.GREEN) + "                        ║%n" +
            "╠═════════════════════════════════════════════════════════════╣%n" +
            "║ " + UserInterface.colorize("[>]",UserInterface.YELLOW) + " Record ID         : "+UserInterface.colorize("#%-34d",UserInterface.YELLOW)+" ║%n" +
            "║ " + UserInterface.colorize("[>]",UserInterface.YELLOW) + " Patient           : %-44s ║%n" +
            "║ " + UserInterface.colorize("[>]",UserInterface.YELLOW) + " Doctor            : %-44s ║%n" +
            "║ " + UserInterface.colorize("[>]",UserInterface.YELLOW) + " Last Appointment  : %-57s ║%n" +
            "╠─────────────────────────────────────────────────────────────╣%n" +
            "║ "+ UserInterface.colorize("[>]",UserInterface.YELLOW) +" Presenting Complaints:                                  ║%n",
            id, patientString, doctorString, appointmentSring
        ) +
        // Handle wrapping untuk teks panjang
        String.join("", Arrays.stream(wrapText.apply(presentingComplaints))
                .map(line -> String.format(lineFormat, line))
                .toArray(String[]::new)) +
        String.format(
            "║ "+ UserInterface.colorize("[>]",UserInterface.YELLOW) +" Diagnosis:                                              ║%n"
        ) +
        String.join("", Arrays.stream(wrapText.apply(diagnosis))
                .map(line -> String.format(lineFormat, line))
                .toArray(String[]::new)) +
        String.format(
            "║ "+ UserInterface.colorize("[>]",UserInterface.YELLOW) +" Treatment:                                              ║%n"
        ) +
        String.join("", Arrays.stream(wrapText.apply(treatment))
                .map(line -> String.format(lineFormat, line))
                .toArray(String[]::new)) +
        String.format(
            "╠─────────────────────────────────────────────────────────────╣%n" +
            "║ "+ UserInterface.colorize("[>]",UserInterface.YELLOW) +" Prescription:                                           ║%n"
        ) +
        String.join("", Arrays.stream(wrapText.apply(prescription))
                .map(line -> String.format(lineFormat, line))
                .toArray(String[]::new)) +
        String.format(
            "║ "+ UserInterface.colorize("[>]",UserInterface.YELLOW) +" Additional Notes:                                       ║%n"
        ) +
        String.join("", Arrays.stream(wrapText.apply(additionalNote))
                .map(line -> String.format(lineFormat, line))
                .toArray(String[]::new)) +
        String.format(
            "╠─────────────────────────────────────────────────────────────╣%n" +
            "║ "+ UserInterface.colorize("[>]",UserInterface.YELLOW) +" Last Updated      : %-44s ║%n" +
            "╚═════════════════════════════════════════════════════════════╝",
            UserInterface.colorize(recordLastUpdated.format(formatter),UserInterface.GREEN)
        );
    }

}