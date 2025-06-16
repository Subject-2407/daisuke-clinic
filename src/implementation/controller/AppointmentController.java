package implementation.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import adt.BST;
import adt.LinkedList;
import adt.PriorityQueue;
import implementation.model.Appointment;
import implementation.model.Doctor;
import implementation.model.Patient;
import implementation.model.Specialty;
import implementation.model.enums.AppointmentStatus;
import shared.LoginState;
import shared.repository.AppointmentRepository;
import shared.repository.DoctorRepository;
import shared.repository.MedicalRecordRepository;
import shared.repository.PatientRepository;
import shared.repository.SpecialtyRepository;
import utility.Input;
import utility.UserInterface;

public class AppointmentController {
    public static void addAppointment(Scanner scanner) {
        while (true) {
            UserInterface.update("Book an Appointment");
            System.out.println("*) Enter 0 to exit");
            System.out.println("*) Only one appointment per specialty allowed.\n");

            Doctor doctor = null;
            LocalDateTime time;
            int patientId = LoginState.getLoginId();

            // doctor id input
            while (doctor == null) {
                Input _doctorId = new Input(scanner, "Enter doctor's ID (in number): ")
                                        .isNotEmpty().isNumeric().validate();
                if (_doctorId.isExit()) return;
                int doctorId = _doctorId.getInteger();
                doctor = DoctorRepository.findById(doctorId);

                if (doctor == null) {
                    UserInterface.warning("Can't find a doctor with this ID!");
                    doctorId = -1;
                }
            }

            // show doctor details and schedule

            System.out.println();
            UserInterface.info("Doctor details: ");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(doctor);

            // validate specialty
            Specialty specialty = SpecialtyRepository.findById(doctor.getSpecialtyId());

            if (specialty == null) {
                System.out.println();
                UserInterface.warning("Could not proceed with appointment booking because doctor's specialty is unknown. Please contact the admin for support.");
                UserInterface.enter(scanner);
                continue;
            } else {
                // check if patient already have one appointment in the specialty
                Object patientAppointments = specialty.getAppointmentQueue().find(a -> a.getPatientId() == patientId);
                if (patientAppointments != null) {
                    System.out.println();
                    UserInterface.warning("You already have a scheduled appointment in the " + UserInterface.colorize(specialty.getName() + "!", UserInterface.YELLOW));
                    UserInterface.enter(scanner);
                    continue;
                }

                // check if specialty appointment queue is full
                if (specialty.getAppointmentQueue().size() == specialty.getMaxSlots()) {
                    System.out.println();
                    UserInterface.warning("The appointment queue in this specialty is currently full!");
                    UserInterface.enter(scanner);
                    continue;
                }
            }

            // get doctor's taken slots
            LinkedList<LocalDateTime> doctorTakenSlots = new LinkedList<>();
            for (Object obj : doctor.getUpcomingAppointments().toArray()) {
                Appointment appointment = (Appointment) obj;
                doctorTakenSlots.insert(appointment.getTime());
            }

            if (!doctorTakenSlots.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
                System.out.println("║ > Doctor's booked slot(s): ");
                int i = 0;
                for (Object obj : doctorTakenSlots.toArray()) {
                    i += 1;
                    LocalDateTime takenSlot = (LocalDateTime) obj;
                    System.out.println("║   " + i + ". " + UserInterface.colorize(takenSlot.format(formatter), UserInterface.ORANGE));
                }
                System.out.println("╚════════════════════════════════════════════════");
            }
            

            System.out.println();
            if (!doctorTakenSlots.isEmpty()) {
                System.out.println("*) The chosen time must be within at least 15 minutes before or after a taken slot.\n");
            }
            // appointment time input
            while (true) {
                Input _appointmentTime = new Input(scanner, "Schedule your appointment (must be within the doctor's schedule; format: DD-MM-YYYY HH:MM): ")
                                            .isNotEmpty().validate();
                if (_appointmentTime.isExit()) return;
                String appointmentTime = _appointmentTime.getString();

                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    LocalDateTime parsedTime = LocalDateTime.parse(appointmentTime, formatter);
                    if (parsedTime.isBefore(LocalDateTime.now())) {
                        UserInterface.warning("What are you, a time traveller?");
                    } else if (doctor.getWorkSchedule().containsKey(parsedTime.getDayOfWeek())) {
                        LocalTime startTime = doctor.getWorkSchedule().get(parsedTime.getDayOfWeek()).getStartTime();
                        LocalTime endTime = doctor.getWorkSchedule().get(parsedTime.getDayOfWeek()).getEndTime();
                        LocalTime targetTime = parsedTime.toLocalTime();

                        if (!targetTime.isBefore(startTime) && !targetTime.isAfter(endTime)) {
                            if (!doctorTakenSlots.isEmpty()) {
                                boolean isProperSlot = true;
                                for (Object obj : doctorTakenSlots.toArray()) {
                                    LocalDateTime takenSlot = (LocalDateTime) obj;

                                    long minutesDiff = Math.abs(Duration.between(takenSlot, parsedTime).toMinutes());
                                    if (minutesDiff <= 15) {
                                        isProperSlot = false;
                                        break;
                                    }
                                }
                                if (!isProperSlot) {
                                    UserInterface.warning("You must choose a time within at least 15 minutes before or after an already booked slot!");
                                } else {
                                    time = parsedTime;
                                    break;
                                }
                            } else {
                                time = parsedTime;
                                break;
                            }
                        } else {
                            UserInterface.warning("Selected time must be within the doctor's working hours!");
                        }
                    } else {
                        UserInterface.warning("Selected time must be within the doctor's schedule!");
                    }
                } catch (DateTimeParseException e) {
                    UserInterface.warning("Invalid format! Example: 07-12-2015 11:00");
                }
            }

            int appointmentNumber = AppointmentRepository.getAppointmentSize() + 1;
            int specialtyId = doctor.getSpecialtyId();

            Appointment newAppointment = new Appointment(appointmentNumber, specialtyId, patientId, doctor.getId(), time);
            AppointmentRepository.add(newAppointment);

            System.out.println();

            UserInterface.success("Successfully booked an appointment!");
            UserInterface.enter(scanner);
        }
    }

    public static void cancelAppointment(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments, BST<Appointment> profileAppointmentHistory) {
        while (true) {
            UserInterface.update("Cancel an Appointment");
            System.out.println("*) Enter 0 to exit\n");

            Appointment appointment = null;

            while (appointment == null) {
                Input _appointmentId = new Input(scanner, "Enter your appointment ID (in number): ")
                                            .isNotEmpty().isNumeric().validate();
                if (_appointmentId.isExit()) return;
                int appointmentId = _appointmentId.getInteger();
                appointment = (Appointment) profileUpcomingAppointments.find(a -> a.getId() == appointmentId);

                if (appointment == null) {
                    UserInterface.warning("Can't find an appointment with this ID in your upcoming appointments!");
                }
            }

            final int appointmentId = appointment.getId();

            System.out.println();
            UserInterface.info("Appointment details:");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(appointment);

            System.out.println();
            while (true) {
                Input confirmationInput = new Input(scanner, "Are you sure you want to cancel the appointment? (Y/N): ")
                                    .isNotEmpty().validate();
                String confirmation = confirmationInput.get();
                if (confirmation.toUpperCase().equals("Y")) {
                    if (profileUpcomingAppointments.removeIf(a -> a.getId() == appointmentId) != null) {
                        appointment.setStatus(AppointmentStatus.CANCELLED);
                        AppointmentRepository.updateStatusInFile(appointmentId, AppointmentStatus.CANCELLED);

                        Specialty specialty = SpecialtyRepository.findById(appointment.getSpecialtyId());
                        if (specialty != null) {
                            specialty.getAppointmentQueue().removeIf(a -> a.getId() == appointmentId);
                        }

                        Doctor doctor = DoctorRepository.findById(appointment.getDoctorId());
                        if (doctor != null) {
                            doctor.getUpcomingAppointments().removeIf(a -> a.getId() == appointmentId);
                            doctor.addAppointmentHistory(appointment);
                        }

                        profileAppointmentHistory.insert(appointment);
                        
                        System.out.println();
                        UserInterface.info("Succesfully cancelled appointment " + UserInterface.colorize("#" + appointmentId, UserInterface.YELLOW));
                        UserInterface.enter(scanner);
                        break;
                    } else {
                        UserInterface.warning("System error! Please contact the admin for support.");
                        UserInterface.enter(scanner);
                        break;
                    }
                } else if (confirmation.toUpperCase().equals("N")) {
                    break;
                } else {
                    UserInterface.warning("Invalid input!");
                }
            }
        }
    }

    public static void getPatientNextAppointment(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments) {
        UserInterface.update("View My Next Appointment");
        if (profileUpcomingAppointments.isEmpty()) {
            System.out.println("You currently have no upcoming appointment.");
        } else {
            System.out.println("Your next appointment is: ");

            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(profileUpcomingAppointments.peek());
        }

        System.out.println();
        UserInterface.enter(scanner);
    }

    public static void getAppointmentById(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments, BST<Appointment> appointmentHistory) {
        while (true) {
            UserInterface.update("Find My Appointment by ID");
            System.out.println("*) Enter 0 to exit\n");

            Appointment appointment = null;

            while (appointment == null) {
                Input _appointmentId = new Input(scanner, "Enter your appointment ID (in number): ")
                                            .isNotEmpty().isNumeric().validate();
                if (_appointmentId.isExit()) return;
                int appointmentId = _appointmentId.getInteger();

                // check the upcoming first
                appointment = profileUpcomingAppointments.find(a -> a.getId() == appointmentId);

                // then check the history
                if (appointment == null) {
                    appointment = appointmentHistory.search(appointmentId);
                }

                if (appointment == null) {
                    UserInterface.warning("Can't find an appointment with this ID!");
                }
            }

            System.out.println();
            UserInterface.info("Appointment details: ");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(appointment);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void getAppointmentsByPatientName(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments, BST<Appointment> appointmentHistory) {
        while (true) {
            UserInterface.update("Find My Appointment(s) by Patient's Name");
            System.out.println("*) Enter 0 to exit\n");

            LinkedList<Appointment> appointments = new LinkedList<>();

            Input _patientName = new Input(scanner, "Enter patient's name: ")
                                        .isNotEmpty().isAlphabetic().validate();
            if (_patientName.isExit()) return;
            String patientName = _patientName.get();

            // check the upcoming first
            Object[] fromUpcomingAppointments = profileUpcomingAppointments.findAll(a -> {
                Patient patient = PatientRepository.findById(a.getPatientId());
                return patient.getName().toLowerCase().contains(patientName.toLowerCase());
            });

            // then check the history
            Object[] fromAppointmentHistory = appointmentHistory.searchAll(a -> {
                Patient patient = PatientRepository.findById(a.getPatientId());
                return patient.getName().toLowerCase().contains(patientName.toLowerCase());
            });
         
            // populate appointments linked list
            for (Object obj : fromUpcomingAppointments) {
                Appointment appointment = (Appointment) obj;
                appointments.insert(appointment);
            }
            for (Object obj : fromAppointmentHistory) {
                Appointment appointment = (Appointment) obj;
                appointments.insert(appointment);
            }

            Object[] foundAppointments = appointments.toArray();

            System.out.println();
            UserInterface.info("Result: ");
            if (foundAppointments.length == 0) {
                System.out.println("No appointment found with the specified patient.");
            } else {
                System.out.println("╔════════════════════════════════════════════════");
                for (Object obj : foundAppointments) {
                    Appointment appointment = (Appointment) obj;
                    System.out.println(appointment);
                }
            }

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void getUpcomingAppointments(Scanner scanner, PriorityQueue<Appointment> profileUpcomingAppointments) {
        UserInterface.update("View My Upcoming Appointments");
        
        Object[] appointments = profileUpcomingAppointments.toArray();

        if (appointments.length == 0) {
            System.out.println("You currently have no upcoming appointments.");
        } else {
            UserInterface.info("Your upcoming appointments: ");
            System.out.println("╔════════════════════════════════════════════════");
            for (Object obj : appointments) {
                Appointment appointment = (Appointment) obj;
                System.out.println(appointment);
            }
        }

        System.out.println();
        UserInterface.enter(scanner);
    }

    public static void getAppointmentHistory(Scanner scanner, BST<Appointment> appointmentHistory) {
        UserInterface.update("View My Appointment History");

        if (appointmentHistory.size() == 0) {
            System.out.println("No appointments have taken place.");
        } else {
            System.out.println("╔════════════════════════════════════════════════");
            appointmentHistory.inOrder();
        }

        System.out.println();
        UserInterface.enter(scanner);
    }

    public static void findClinicAppointmentById(Scanner scanner) {
        while (true) {
            UserInterface.update("Find an Appointment by ID");
            System.out.println("*) Enter 0 to exit\n");
            
            Appointment appointment = null;

            while (appointment == null) {
                Input _appointmentId = new Input(scanner, "Enter appointment ID (in number): ")
                                            .isNotEmpty().isNumeric().validate();
                if (_appointmentId.isExit()) return;
                int appointmentId = _appointmentId.getInteger();

                appointment = AppointmentRepository.findById(appointmentId);

                if (appointment == null) {
                    UserInterface.warning("Can't find an appointment with this ID!");
                }
            }

            System.out.println();
            UserInterface.info("Appointment details: ");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(appointment);

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void viewAppointmentsBySpecialty(Scanner scanner) {
        while (true) {
            UserInterface.update("View Upcoming Appointment(s) by Specialty");
            System.out.println("*) Enter 0 to exit\n");

            Specialty specialty = null;

            while (specialty == null) {
                Input _specialtyId = new Input(scanner, "Enter specialty ID (in number): ")
                                        .isNotEmpty().isNumeric().validate();
                if (_specialtyId.isExit()) return;
                int specialtyId = _specialtyId.getInteger();

                specialty = SpecialtyRepository.findById(specialtyId);

                if (specialty == null) {
                    UserInterface.warning("Can't find a specialty with this ID!");
                }
            }

            Object[] appointments = specialty.getAppointmentQueue().toArray();

            System.out.println();
            if (appointments.length == 0) {
                System.out.println("No upcoming appointments.");
            } else {
                UserInterface.info("Appointment(s) in " + UserInterface.colorize(specialty.getName(), UserInterface.YELLOW) + ": ");
                System.out.println("╔════════════════════════════════════════════════");
                for (Object obj : appointments) {
                    Appointment appointment = (Appointment) obj;
                    System.out.println(appointment);
                }
            }

            System.out.println();
            UserInterface.enter(scanner);
        }
    }

    public static void processDoctorNextAppointment(Scanner scanner, Doctor doctor, Specialty specialty) {
        outerLoop: while (true) {
            UserInterface.update("Check Next Appointment");
            if (specialty.getAppointmentQueue().isEmpty()) {
                System.out.println("No appointments are available in " + UserInterface.colorize(specialty.getName(),UserInterface.YELLOW) + " at the moment.");
                System.out.println();
                UserInterface.enter(scanner);
                return;
            }
            System.out.println("*) You can only handle your own appointment\n");

            Appointment nextAppointment = specialty.getAppointmentQueue().peek();

            UserInterface.info("Next appointment in queue:");
            System.out.println("╔════════════════════════════════════════════════");
            System.out.println(nextAppointment);

            if (doctor.getId() != nextAppointment.getDoctorId()) {
                System.out.println();
                UserInterface.enter(scanner);
                return;
            }

            System.out.println();
            String[] options = {
                "Process Appointment"
            };
            UserInterface.createOptions(options);

            System.out.println();
            while (true) {
                Input _choice = new Input(scanner, "Enter choice: ").validate();
                if (_choice.isExit()) return;
                String choice = _choice.get();
                if (choice.equals("1")) {
                    Patient patient = PatientRepository.findById(nextAppointment.getPatientId());
                    while (true) {
                        Input _updateMedicalRecord = new Input(scanner, "Do you also wish to update the patient's medical record? (Y/N): ").isNotEmpty().validate();
                        String updateMedicalRecord = _updateMedicalRecord.get();

                        if (updateMedicalRecord.toUpperCase().equals("Y")) {
                            System.out.println("\n*) Enter N in each field to skip");
                            String presentingComplaints = null, diagnosis = null, treatment = null, prescription = null, additionalNote = null;

                            // PC input
                            Input _presentingComplaints = new Input(scanner, "Enter presenting complaints: ")
                                                            .isNotEmpty().validate();
                            if (_presentingComplaints.isExit()) break outerLoop;
                            if (!_presentingComplaints.get().toString().toUpperCase().equals("N")) {
                                presentingComplaints = _presentingComplaints.get();
                            }

                            // diagnosis input
                            Input _diagnosis = new Input(scanner, "Enter diagnosis: ")
                                                            .isNotEmpty().validate();
                            if (_diagnosis.isExit()) break outerLoop;
                            if (!_diagnosis.get().toString().toUpperCase().equals("N")) {
                                diagnosis = _diagnosis.get();
                            }

                            // diagnosis input
                            Input _treatment = new Input(scanner, "Enter treatment: ")
                                                            .isNotEmpty().validate();
                            if (_treatment.isExit()) break outerLoop;
                            if (!_treatment.get().toString().toUpperCase().equals("N")) {
                                treatment = _treatment.get();
                            }

                            // prescription input
                            Input _prescription = new Input(scanner, "Enter prescription: ")
                                                            .isNotEmpty().validate();
                            if (_prescription.isExit()) break outerLoop;
                            if (!_prescription.get().toString().toUpperCase().equals("N")) {
                                prescription = _prescription.get();
                            }

                            // additional note input
                            Input _additionalNote = new Input(scanner, "Enter additional note: ")
                                                            .isNotEmpty().validate();
                            if (_additionalNote.isExit()) break outerLoop;
                            if (!_additionalNote.get().toString().toUpperCase().equals("N")) {
                                additionalNote = _additionalNote.get();
                            }

                            System.out.println();

                            if (presentingComplaints != null) {
                                final String patientComplaints = presentingComplaints;
                                patient.getMedicalRecord().setPresentingComplaints(presentingComplaints);
                                MedicalRecordRepository.modifyFile(patient.getId(), m -> { m.setPresentingComplaints(patientComplaints); return m;}); // med record's id shares the same id with patient's id

                            }
                            if (diagnosis != null) {
                                final String patientDiagnosis = diagnosis;
                                patient.getMedicalRecord().setDiagnosis(patientDiagnosis);
                                MedicalRecordRepository.modifyFile(patient.getId(), m -> { m.setDiagnosis(patientDiagnosis); return m;});
                            }
                            if (treatment != null) {
                                final String patientTreatment = treatment;
                                patient.getMedicalRecord().setTreatment(patientTreatment);
                                MedicalRecordRepository.modifyFile(patient.getId(), m -> { m.setTreatment(patientTreatment); return m;});
                            }
                            if (prescription != null) {
                                final String doctorPrescription = prescription;
                                patient.getMedicalRecord().setPrescription(doctorPrescription);
                                MedicalRecordRepository.modifyFile(patient.getId(), m -> { m.setPrescription(doctorPrescription); return m;});
                            }
                            if (additionalNote != null) {
                                final String doctorAdditionalNote = additionalNote;
                                patient.getMedicalRecord().setAdditionalNote(doctorAdditionalNote);
                                MedicalRecordRepository.modifyFile(patient.getId(), m -> { m.setAdditionalNote(doctorAdditionalNote); return m;});
                            }
                            if (presentingComplaints != null || diagnosis != null || treatment != null || prescription != null || additionalNote != null) {
                                patient.getMedicalRecord().setRecordLastUpdated(LocalDateTime.now());
                                MedicalRecordRepository.modifyFile(patient.getId(), m -> { m.setRecordLastUpdated(LocalDateTime.now()); return m;});
                                UserInterface.success("Successfully updated patient's medical record!");
                            }
                            break;
                        } else if (updateMedicalRecord.toUpperCase().equals("N")) {
                            System.out.println();
                            break;
                        } else {
                            UserInterface.warning("Invalid choice!");
                            UserInterface.enter(scanner);
                        }
                    }
                    
                    patient.getMedicalRecord().setDoctorId(doctor.getId()); // also implicitly sets patient's their current doctor (the doctor will be allowed to update their medical record)
                    patient.getMedicalRecord().setLastAppointmentId(nextAppointment.getId());
                    MedicalRecordRepository.modifyFile(patient.getId(), m -> { m.setDoctorId(doctor.getId()); return m;});
                    MedicalRecordRepository.modifyFile(patient.getId(), m -> { m.setLastAppointmentId(nextAppointment.getId()); return m;});

                    // dequeue appointment from the objects referenced by it
                    specialty.dequeueAppointment(AppointmentStatus.PROCESSED);
                    AppointmentRepository.updateStatusInFile(nextAppointment.getId(), AppointmentStatus.PROCESSED);

                    doctor.dequeueAppointment();
                    doctor.addAppointmentHistory(nextAppointment);

                    patient.dequeueAppointment();
                    patient.addAppointmentHistory(nextAppointment);
                    
                    UserInterface.success("Successfully processed appointment " + UserInterface.colorize("#" + nextAppointment.getId() + "!", UserInterface.YELLOW));
                    UserInterface.enter(scanner);
                    break;
                } else if (choice.equals("0")) {
                    break outerLoop;
                } else {
                    UserInterface.warning("Invalid choice!");
                    UserInterface.enter(scanner);
                    continue outerLoop;
                }
            }
        }
    }

    public static void viewSpecialtyUpcomingAppointments(Scanner scanner, Specialty specialty) {
        UserInterface.update("View Specialty Upcoming Appointments");

        if (specialty.getAppointmentQueue().isEmpty()) {
            System.out.println("No appointments are available in " + UserInterface.colorize(specialty.getName(),UserInterface.YELLOW) + " at the moment.");
        } else {
            UserInterface.info("Upcoming appointments in " + UserInterface.colorize(specialty.getName(),UserInterface.YELLOW) + ": ");

            Object[] appointments = specialty.getAppointmentQueue().toArray();
            System.out.println("╔════════════════════════════════════════════════");
            for (Object obj : appointments) {
                Appointment appointment = (Appointment) obj;
                System.out.println(appointment);
            }
        }

        System.out.println();
        UserInterface.enter(scanner);
        return;
    }
}
