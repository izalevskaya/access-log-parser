import java.time.Duration;
import java.time.LocalDateTime;
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

        // Проверяем, есть ли уже такой браузер в HashMap
        if (browserStatistics.containsKey(browser)) {
            // Если есть, увеличиваем значение на 1
            int currentCount = browserStatistics.get(browser);
            browserStatistics.put(browser, currentCount + 1);
        } else {
            // Если нет, добавляем запись со значением 1
            browserStatistics.put(browser, 1);
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
        return new HashSet<>(nonExistingPages); // Возвращаем копию для защиты данных
    }

    /**
     * Возвращает статистику браузеров пользователей сайта
     * в виде долей от 0 до 1 для каждого браузера
     */
    public HashMap<String, Double> getBrowserStatistics() {
        HashMap<String, Double> browserShares = new HashMap<>();

        // Вычисляем общее количество запросов
        int totalCount = 0;
        for (int count : browserStatistics.values()) {
            totalCount += count;
        }

        // Если нет данных, возвращаем пустой HashMap
        if (totalCount == 0) {
            return browserShares;
        }

        // Рассчитываем долю для каждого браузера
        for (String browser : browserStatistics.keySet()) {
            int count = browserStatistics.get(browser);
            double share = (double) count / totalCount;
            browserShares.put(browser, share);
        }

        return browserShares;
    }
}