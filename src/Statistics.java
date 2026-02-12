import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {

    private long totalTraffic = 0;
    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    public Statistics() {
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();

        if (minTime == null || entry.getDateTime().isBefore(minTime)) {
            minTime = entry.getDateTime();
        }
        if (maxTime == null || entry.getDateTime().isAfter(maxTime)) {
            maxTime = entry.getDateTime();
        }
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null) {
            return 0;
        }

        long hours = Duration.between(minTime, maxTime).toHours();
        if (hours <= 0) {
            hours = 1;
        }

        return (double) totalTraffic / hours;
    }
}