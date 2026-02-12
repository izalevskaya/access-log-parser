import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {

    private static final DateTimeFormatter LOG_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    private final String ipAddress;
    private final LocalDateTime dateTime;
    private final long responseSize;
    private final UserAgent userAgent;

    public LogEntry(String line) {


        this.ipAddress = line.substring(0, line.indexOf(" "));


        int lb = line.indexOf('[');
        int rb = line.indexOf(']');
        String dateStr = line.substring(lb + 1, rb);
        OffsetDateTime odt = OffsetDateTime.parse(dateStr, LOG_DATE_FORMAT);
        this.dateTime = odt.toLocalDateTime();


        int requestEnd = line.indexOf("\"", rb) + 1;
        int secondQuote = line.indexOf("\"", requestEnd);
        String afterRequest = line.substring(secondQuote + 2); // пропускаем " и пробел

        String[] parts = afterRequest.split(" ");


        String sizeStr = parts[1];
        this.responseSize = "-".equals(sizeStr) ? 0 : Long.parseLong(sizeStr);


        int lastQuote = line.lastIndexOf('"');
        int prevQuote = line.lastIndexOf('"', lastQuote - 1);
        String agent = line.substring(prevQuote + 1, lastQuote);
        this.userAgent = new UserAgent(agent);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public long getResponseSize() {
        return responseSize;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}