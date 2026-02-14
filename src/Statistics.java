import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;

public class Statistics {

    private long totalTraffic = 0;
    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    // Множество для хранения уникальных страниц с кодом ответа 404 (несуществующие)
    private HashSet<String> nonExistingPages = new HashSet<>();

    // HashMap для подсчета количества вхождений каждого браузера
    private HashMap<String, Integer> browserStatistics = new HashMap<>();

    // Поля для дополнительной статистики
    private int totalVisits = 0;                    // Общее количество посещений
    private int totalBotVisits = 0;                  // Количество посещений ботами
    private int totalErrorRequests = 0;               // Количество ошибочных запросов (4xx, 5xx)
    private HashMap<String, Integer> userVisits = new HashMap<>(); // IP -> количество посещений

    // НОВЫЕ ПОЛЯ
    private HashMap<Integer, Integer> visitsPerSecond = new HashMap<>(); // Секунда -> количество посещений
    private HashSet<String> refererDomains = new HashSet<>(); // Домены referer-ов

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

        // 1. Добавляем адреса несуществующих страниц с кодом ответа 404
        if (entry.getResponseCode() == 404) {
            nonExistingPages.add(entry.getPath());
        }

        // 2. Подсчитываем статистику браузеров
        String browser = entry.getUserAgent().getBrowser();
        if (browserStatistics.containsKey(browser)) {
            int currentCount = browserStatistics.get(browser);
            browserStatistics.put(browser, currentCount + 1);
        } else {
            browserStatistics.put(browser, 1);
        }

        // 3. Проверяем, является ли запрос от бота
        boolean isBot = entry.getUserAgent().isBot();

        // 4. Увеличиваем общее количество посещений
        totalVisits++;

        // 5. Если это бот, увеличиваем счетчик ботов
        if (isBot) {
            totalBotVisits++;
        }

        // 6. Проверяем, является ли запрос ошибочным (код 4xx или 5xx)
        int responseCode = entry.getResponseCode();
        if (responseCode >= 400 && responseCode < 600) {
            totalErrorRequests++;
        }

        // 7. Учитываем посещения по IP-адресам для реальных пользователей (не ботов)
        if (!isBot) {
            String ip = entry.getIpAddress();
            if (userVisits.containsKey(ip)) {
                int visits = userVisits.get(ip);
                userVisits.put(ip, visits + 1);
            } else {
                userVisits.put(ip, 1);
            }
        }

        // 8. НОВОЕ: Учитываем посещения по секундам (только для реальных пользователей)
        if (!isBot) {
            // Получаем количество секунд от начала суток
            LocalDateTime dateTime = entry.getDateTime();
            int secondOfDay = dateTime.toLocalTime().toSecondOfDay();

            if (visitsPerSecond.containsKey(secondOfDay)) {
                int count = visitsPerSecond.get(secondOfDay);
                visitsPerSecond.put(secondOfDay, count + 1);
            } else {
                visitsPerSecond.put(secondOfDay, 1);
            }
        }

        // 9. НОВОЕ: Собираем домены referer-ов
        String referer = entry.getReferer();
        if (referer != null && !referer.isEmpty() && !referer.equals("-")) {
            String domain = extractDomain(referer);
            if (domain != null && !domain.isEmpty()) {
                refererDomains.add(domain);
            }
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

    /**
     * Возвращает список всех несуществующих страниц сайта (с кодом ответа 404)
     */
    public HashSet<String> getNonExistingPages() {
        return new HashSet<>(nonExistingPages);
    }

    /**
     * Возвращает статистику браузеров пользователей сайта
     * в виде долей от 0 до 1 для каждого браузера
     */
    public HashMap<String, Double> getBrowserStatistics() {
        HashMap<String, Double> browserShares = new HashMap<>();

        int totalCount = 0;
        for (int count : browserStatistics.values()) {
            totalCount += count;
        }

        if (totalCount == 0) {
            return browserShares;
        }

        for (String browser : browserStatistics.keySet()) {
            int count = browserStatistics.get(browser);
            double share = (double) count / totalCount;
            browserShares.put(browser, share);
        }

        return browserShares;
    }

    /**
     * Метод подсчёта среднего количества посещений сайта за час
     * (только реальные пользователи, не боты)
     */
    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null) {
            return 0;
        }

        long hours = Duration.between(minTime, maxTime).toHours();
        if (hours <= 0) {
            hours = 1;
        }

        int realUserVisits = totalVisits - totalBotVisits;
        return (double) realUserVisits / hours;
    }

    /**
     * Метод подсчёта среднего количества ошибочных запросов в час
     */
    public double getAverageErrorsPerHour() {
        if (minTime == null || maxTime == null) {
            return 0;
        }

        long hours = Duration.between(minTime, maxTime).toHours();
        if (hours <= 0) {
            hours = 1;
        }

        return (double) totalErrorRequests / hours;
    }

    /**
     * Метод расчёта средней посещаемости одним пользователем
     * (только реальные пользователи, не боты)
     */
    public double getAverageVisitsPerUser() {
        // Общее количество посещений реальными пользователями
        int realUserVisits = totalVisits - totalBotVisits;

        // Количество уникальных IP-адресов реальных пользователей
        int uniqueUsers = userVisits.size();

        if (uniqueUsers == 0) {
            return 0;
        }

        return (double) realUserVisits / uniqueUsers;
    }

    /**
     * НОВЫЙ МЕТОД: Расчёт пиковой посещаемости сайта (в секунду)
     * Возвращает максимальное количество посещений за одну секунду
     * (только реальные пользователи, не боты)
     */
    public int getPeakVisitsPerSecond() {
        int maxVisits = 0;

        for (int visits : visitsPerSecond.values()) {
            if (visits > maxVisits) {
                maxVisits = visits;
            }
        }

        return maxVisits;
    }

    /**
     * НОВЫЙ МЕТОД: Возвращает список сайтов, со страниц которых есть ссылки на текущий сайт
     */
    public HashSet<String> getRefererDomains() {
        return new HashSet<>(refererDomains);
    }

    /**
     * НОВЫЙ МЕТОД: Расчёт максимальной посещаемости одним пользователем
     * (только реальные пользователи, не боты)
     */
    public int getMaxVisitsPerUser() {
        int maxVisits = 0;

        for (int visits : userVisits.values()) {
            if (visits > maxVisits) {
                maxVisits = visits;
            }
        }

        return maxVisits;
    }

    /**
     * Вспомогательный метод для извлечения домена из URL
     */
    private String extractDomain(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            // Убираем протокол (http://, https://)
            String domain = url.toLowerCase();
            if (domain.startsWith("http://")) {
                domain = domain.substring(7);
            } else if (domain.startsWith("https://")) {
                domain = domain.substring(8);
            }

            // Убираем путь и параметры после домена
            int slashIndex = domain.indexOf('/');
            if (slashIndex > 0) {
                domain = domain.substring(0, slashIndex);
            }

            // Убираем порт, если есть
            int portIndex = domain.indexOf(':');
            if (portIndex > 0) {
                domain = domain.substring(0, portIndex);
            }

            // Убираем www
            if (domain.startsWith("www.")) {
                domain = domain.substring(4);
            }

            return domain;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Дополнительные геттеры для отладки
     */
    public int getTotalVisits() {
        return totalVisits;
    }

    public int getTotalBotVisits() {
        return totalBotVisits;
    }

    public int getTotalErrorRequests() {
        return totalErrorRequests;
    }

    public int getUniqueUsers() {
        return userVisits.size();
    }

    /**
     * Дополнительный метод для получения детальной информации о посещаемости по секундам
     */
    public HashMap<Integer, Integer> getVisitsPerSecond() {
        return new HashMap<>(visitsPerSecond);
    }
}