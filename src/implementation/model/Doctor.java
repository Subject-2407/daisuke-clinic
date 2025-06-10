package implementation.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.StringJoiner;

import adt.LinkedList;
import adt.Map;
import adt.PriorityQueue;
import implementation.model.interfaces.Identifiable;
import shared.repository.SpecialtyRepository;
import utility.Hasher;
import utility.UserInterface;

public class Doctor implements Identifiable {
    private int id;
    private String password; // should be hashed
    private String name;
    private int specialtyId;
    private String phoneNumber;
    private Map<DayOfWeek, WorkingHours> workSchedule;
    private PriorityQueue<Appointment> upcomingAppointments;
    private LinkedList<Appointment> appointmentHistory;

    public Doctor(int id, String password, String name, int specialtyId, String phoneNumber) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.specialtyId = specialtyId;
        this.phoneNumber = phoneNumber;
        this.workSchedule = new Map<>();
        this.upcomingAppointments = new PriorityQueue<>();
        this.appointmentHistory = new LinkedList<>();
    }

    public boolean validatePassword(String password) { return Hasher.hash(password).equals(this.password); }
    public String getName() { return name; }
    public int getSpecialtyId() { return specialtyId; }
    public String getPhoneNumber() { return phoneNumber; }
    public Map<DayOfWeek, WorkingHours> getWorkSchedule() { return workSchedule; }
    public PriorityQueue<Appointment> getUpcomingAppointments() { return upcomingAppointments; }
    public LinkedList<Appointment> getAppointmentHistory() { return appointmentHistory; }

    public void setWorkSchedule(Map<DayOfWeek, WorkingHours> schedule) { this.workSchedule = schedule; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPassword(String password) { this.password = Hasher.hash(password); }

    public String toFileString() {
        Object[] scheduleKeys = workSchedule.keySet();
        StringJoiner joiner = new StringJoiner(",");

        for (Object obj : scheduleKeys) {
            DayOfWeek day = (DayOfWeek) obj;
            String scheduleString = day + "=" + workSchedule.get(day).getStartTime() + "-" + workSchedule.get(day).getEndTime();
            joiner.add(scheduleString);
        }

        String workScheduleString = joiner.toString();

        return id + "|" + password + "|" + name + "|" + specialtyId + "|" + phoneNumber + "|" + workScheduleString;
    }

    public static Doctor fromFileString(String line) { 
        String[] parts = line.split("\\|");

        String[] dayHoursPairs = parts[5].split(",");
        Map<DayOfWeek, WorkingHours> schedule = new Map<>();
        for (String pair : dayHoursPairs) {
            String[] dayAndHours = pair.split("=");
            DayOfWeek day = DayOfWeek.valueOf(dayAndHours[0]); // work day

            String[] hours = dayAndHours[1].split("-");
            LocalTime startTime = LocalTime.parse(hours[0]); // start time
            LocalTime endTime = LocalTime.parse(hours[1]); // end time

            schedule.put(day, new WorkingHours(startTime, endTime));
        }

        Doctor doctor = new Doctor(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            Integer.parseInt(parts[3]),
            parts[4]
        );

        doctor.setWorkSchedule(schedule);

        return doctor;
    }

    @Override
    public int getId() { return id; }

    @Override
    public String toString() { 
        Specialty specialty = SpecialtyRepository.findById(specialtyId);
        String workScheduleString = "";
        DayOfWeek[] daysInOrder = {
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        };

        for (DayOfWeek day : daysInOrder) {
            if (workSchedule.containsKey(day)) {
                workScheduleString += String.format("\n   - %s: %s - %s", 
                    day.toString().substring(0, 1).toUpperCase() + day.toString().substring(1).toLowerCase(), 
                    workSchedule.get(day).getStartTime(), 
                    workSchedule.get(day).getEndTime());
            }
        }

        return "-------------------------------------------------\n" +
        "Dr. " + name + " (" + UserInterface.colorize("#" + id, UserInterface.YELLOW) + ")" +
        "\n > Specialty: " + (specialty == null ? "Unknown" : (specialty.getName() + " (" + UserInterface.colorize("#" + specialtyId, UserInterface.YELLOW) + ")")) +
        "\n > Phone Number: " + phoneNumber + 
        "\n > Work Schedule: " + workScheduleString + 
        "\n-------------------------------------------------";
    }
}
