import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

public class Statistics {

    private long totalTraffic = 0;
    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    // Множество для хранения уникальных страниц с кодом ответа 200
    private HashSet<String> existingPages = new HashSet<>();

    // HashMap для подсчета количества вхождений каждой операционной системы
    private HashMap<String, Integer> osStatistics = new HashMap<>();

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

        // 1. Добавляем адреса существующих страниц с кодом ответа 200
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        // 2. Подсчитываем статистику операционных систем
        String os = entry.getUserAgent().getOperatingSystem();

        // Проверяем, есть ли уже такая ОС в HashMap
        if (osStatistics.containsKey(os)) {
            // Если есть, увеличиваем значение на 1
            int currentCount = osStatistics.get(os);
            osStatistics.put(os, currentCount + 1);
        } else {
            // Если нет, добавляем запись со значением 1
            osStatistics.put(os, 1);
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
     * Возвращает список всех существующих страниц сайта (с кодом ответа 200)
     */
    public HashSet<String> getExistingPages() {
        return new HashSet<>(existingPages); // Возвращаем копию для защиты данных
    }

    /**
     * Возвращает статистику операционных систем пользователей сайта
     * в виде долей от 0 до 1 для каждой операционной системы
     */
    public HashMap<String, Double> getOsStatistics() {
        HashMap<String, Double> osShares = new HashMap<>();

        // Вычисляем общее количество запросов
        int totalCount = 0;
        for (int count : osStatistics.values()) {
            totalCount += count;
        }

        // Если нет данных, возвращаем пустой HashMap
        if (totalCount == 0) {
            return osShares;
        }

        // Рассчитываем долю для каждой операционной системы
        for (String os : osStatistics.keySet()) {
            int count = osStatistics.get(os);
            double share = (double) count / totalCount;
            osShares.put(os, share);
        }

        return osShares;
    }
}