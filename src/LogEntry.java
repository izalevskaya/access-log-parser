import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LogEntry {

    private static final DateTimeFormatter LOG_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    private final String ipAddress;
    private final LocalDateTime dateTime;
    private final String path;
    private final int responseCode;
    private final long responseSize;
    private final String referer;           // НОВОЕ ПОЛЕ
    private final UserAgent userAgent;

    public LogEntry(String line) {
        // Разбиваем строку по пробелам
        String[] parts = line.split(" ");

        // 1. IP-адрес - первый элемент
        this.ipAddress = parts[0];

        // 2. Дата и время - находится в квадратных скобках
        String dateStr = parts[3] + " " + parts[4];
        dateStr = dateStr.substring(1, dateStr.length() - 1); // убираем [ и ]
        OffsetDateTime odt = OffsetDateTime.parse(dateStr, LOG_DATE_FORMAT);
        this.dateTime = odt.toLocalDateTime();

        // 3. Путь запроса
        String requestPath = parts[6];
        this.path = requestPath;

        // 4. Код ответа
        int tempResponseCode;
        try {
            tempResponseCode = Integer.parseInt(parts[8]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            tempResponseCode = 0;
        }
        this.responseCode = tempResponseCode;

        // 5. Размер ответа
        long tempResponseSize;
        try {
            String sizeStr = parts[9];
            tempResponseSize = "-".equals(sizeStr) ? 0 : Long.parseLong(sizeStr);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            tempResponseSize = 0;
        }
        this.responseSize = tempResponseSize;

        // 6. НОВОЕ: Парсинг referer (находится в parts[10])
        String tempReferer;
        try {
            tempReferer = parts[10];
            // Убираем кавычки, если они есть
            if (tempReferer.startsWith("\"") && tempReferer.endsWith("\"")) {
                tempReferer = tempReferer.substring(1, tempReferer.length() - 1);
            }
            // Если referer равен "-", считаем его пустым
            if ("-".equals(tempReferer)) {
                tempReferer = "";
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            tempReferer = "";
        }
        this.referer = tempReferer;

        // 7. User-Agent (начинается с parts[11])
        StringBuilder userAgentBuilder = new StringBuilder();
        for (int i = 11; i < parts.length; i++) {
            if (userAgentBuilder.length() > 0) {
                userAgentBuilder.append(" ");
            }
            userAgentBuilder.append(parts[i]);
        }

        String userAgentStr = userAgentBuilder.toString();
        if (userAgentStr.startsWith("\"") && userAgentStr.endsWith("\"")) {
            userAgentStr = userAgentStr.substring(1, userAgentStr.length() - 1);
        }

        this.userAgent = new UserAgent(userAgentStr);
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

    public int getResponseCode() {
        return responseCode;
    }

    public String getPath() {
        return path;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    // НОВЫЙ МЕТОД
    public String getReferer() {
        return referer;
    }
}