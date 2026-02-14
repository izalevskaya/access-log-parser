import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Scanner;

public class Main {  // <- Класс начинается здесь

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Введите путь к файлу: ");
        String path = sc.nextLine();

        Statistics stats = new Statistics();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.length() > 1024) {
                    throw new LongLineException(
                            "Строка длиннее 1024 символов (номер строки: " + lineNumber + ", длина: " + line.length() + ")"
                    );
                }

                LogEntry entry = new LogEntry(line);
                stats.addEntry(entry);
            }

            // Вывод среднего трафика
            System.out.printf("Средний трафик в час: %.2f%n", stats.getTrafficRate());

            // Вывод списка несуществующих страниц (код 404)
            System.out.println("\n=== Несуществующие страницы сайта (код 404) ===");
            HashSet<String> nonExistingPages = stats.getNonExistingPages();
            if (nonExistingPages.isEmpty()) {
                System.out.println("Страницы с кодом 404 не найдены");
            } else {
                for (String page : nonExistingPages) {
                    System.out.println(page);
                }
                System.out.println("Всего несуществующих страниц: " + nonExistingPages.size());
            }

            // Вывод статистики браузеров
            System.out.println("\n=== Статистика браузеров ===");
            HashMap<String, Double> browserStats = stats.getBrowserStatistics();
            if (browserStats.isEmpty()) {
                System.out.println("Данные о браузерах не найдены");
            } else {
                for (String browser : browserStats.keySet()) {
                    double share = browserStats.get(browser);
                    System.out.printf("%s: %.2f%%%n", browser, share * 100);
                }
            }

            // Дополнительная статистика
            System.out.println("\n=== Дополнительная статистика ===");
            System.out.printf("Среднее количество посещений в час (реальные пользователи): %.2f%n",
                    stats.getAverageVisitsPerHour());
            System.out.printf("Среднее количество ошибочных запросов в час: %.2f%n",
                    stats.getAverageErrorsPerHour());
            System.out.printf("Средняя посещаемость одним пользователем: %.2f%n",
                    stats.getAverageVisitsPerUser());

            System.out.println("\n=== Детали ===");
            System.out.println("Всего записей: " + stats.getTotalVisits());
            System.out.println("Запросов от ботов: " + stats.getTotalBotVisits());
            System.out.println("Ошибочных запросов (4xx, 5xx): " + stats.getTotalErrorRequests());
            System.out.println("Уникальных пользователей (не боты): " + stats.getUniqueUsers());

        } catch (LongLineException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        System.out.println("\n=== Новая статистика (Задание 2) ===");
        System.out.println("Пиковая посещаемость в секунду: " + stats.getPeakVisitsPerSecond() + " посещений");
        System.out.println("Максимальная посещаемость одним пользователем: " + stats.getMaxVisitsPerUser() + " посещений");

        System.out.println("\n=== Сайты, с которых есть ссылки (referer domains) ===");
        HashSet<String> refererDomains = stats.getRefererDomains();
        if (refererDomains.isEmpty()) {
            System.out.println("Нет данных о referer-ах");
        } else {
            for (String domain : refererDomains) {
                System.out.println(domain);
            }
            System.out.println("Всего уникальных доменов: " + refererDomains.size());
        }
    }

}