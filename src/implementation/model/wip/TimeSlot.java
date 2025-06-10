package implementation.model.wip;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeSlot {
    private DayOfWeek dayOfWeek;
    private LocalTime startingTime;
    private LocalTime finishingTime;

    public TimeSlot(DayOfWeek dayOfWeek, LocalTime startingTime, LocalTime finishingTime) {
        this.dayOfWeek = dayOfWeek;
        this.startingTime = startingTime;
        this.finishingTime = finishingTime;
    }

    public boolean isWithinSlot(LocalDateTime dateTime) {
        // Check if the day matches
        if (dateTime.getDayOfWeek() != this.dayOfWeek) {
            return false;
        }

        // Get the time from the LocalDateTime
        LocalTime time = dateTime.toLocalTime();

        // Check if the time is within the range [startingTime, finishingTime)
        return !time.isBefore(startingTime) && time.isBefore(finishingTime);
    }

    // Getters (optional)...
}
