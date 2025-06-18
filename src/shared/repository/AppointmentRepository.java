package shared.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Predicate;

import adt.BST;
import implementation.model.Appointment;
import implementation.model.Doctor;
import implementation.model.Patient;
import implementation.model.Specialty;
import implementation.model.enums.AppointmentStatus;

public class AppointmentRepository {
    private static BST<Appointment> appointmentTree = new BST<>();
    
    private static final String filePath = Paths.get("saves", "appointments.txt").toString();
    private static final String tempFilePath = Paths.get("saves", "temp_appointments.txt").toString();
    // private static final String filePath = "src/saves/appointments.txt";
    // private static final String tempFilePath = "src/saves/temp_appointments.txt"; // for modifying purposes

    public static int getAppointmentSize() { return appointmentTree.size(); }

    public static void add(Appointment appointment) {
        appointmentTree.insert(appointment);

        Specialty specialty = SpecialtyRepository.findById(appointment.getSpecialtyId());
        if (specialty != null) {
            specialty.enqueueAppointment(appointment);
        }

        Patient patient = PatientRepository.findById(appointment.getPatientId());
        if (patient != null) {
            patient.enqueueAppointment(appointment);
        }

        Doctor doctor = DoctorRepository.findById(appointment.getDoctorId());
        if (doctor != null) {
            doctor.enqueueAppointment(appointment);
        }

        try {
            saveToFile(appointment);
        } catch (IOException e) {
            System.err.println("Failed to save appointment data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Appointment findById(int id) {
        return appointmentTree.search(id);
    }

    public static Object[] findAll(Predicate<Appointment> predicate) {
        return appointmentTree.searchAll(predicate);
    }

    public static void updateStatusInFile(int id, AppointmentStatus status) {
        try {
            modifyLineInFile(id, a -> { a.setStatus(status); return a;} );
        } catch (IOException e) {
            System.err.println("Failed to update appointment data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveToFile(Appointment appointment) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(appointment.toFileString());
        writer.newLine();
        writer.close();
    }

    public static void load() throws IOException {
        appointmentTree = new BST<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            Appointment appointment = Appointment.fromFileString(line);
            appointmentTree.insert(appointment);

            Specialty specialty = SpecialtyRepository.findById(appointment.getSpecialtyId());
            Patient patient = PatientRepository.findById(appointment.getPatientId());
            Doctor doctor = DoctorRepository.findById(appointment.getDoctorId());

           
            if (appointment.getStatus() == AppointmentStatus.ONGOING || appointment.getStatus() == AppointmentStatus.SCHEDULED) {
                if (specialty != null) {
                    specialty.enqueueAppointment(appointment);
                }
                if (patient != null) {
                    patient.enqueueAppointment(appointment);
                }
                if (doctor != null) {
                    doctor.enqueueAppointment(appointment);
                }
            } else {
                if (patient != null) {
                    patient.addAppointmentHistory(appointment);
                }
                if (doctor != null) {
                    doctor.addAppointmentHistory(appointment);
                }
            }
        }
        reader.close();
    }

    private static void modifyLineInFile(int targetId, Function<Appointment, Appointment> modifier) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File(tempFilePath);

        try (
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                Appointment appointment = Appointment.fromFileString(line);
                if (appointment.getId() == targetId) {
                    appointment = modifier.apply(appointment); 
                }
                writer.write(appointment.toFileString());
                writer.newLine();
            }
        }

        if (!inputFile.delete()) {
            throw new IOException("Could not delete original file");
        }
        if (!tempFile.renameTo(inputFile)) {
            throw new IOException("Could not rename temporary file");
        }
    }
}
