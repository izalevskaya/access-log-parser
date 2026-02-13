import java.util.HashSet;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

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

            // ДОБАВЛЯЕМ ВЫВОД СПИСКА СТРАНИЦ
            System.out.println("\n=== Существующие страницы сайта (код 200) ===");
            HashSet<String> pages = stats.getExistingPages();
            if (pages.isEmpty()) {
                System.out.println("Страницы с кодом 200 не найдены");
            } else {
                for (String page : pages) {
                    System.out.println(page);
                }
                System.out.println("Всего страниц: " + pages.size());
            }

            // ДОБАВЛЯЕМ ВЫВОД СТАТИСТИКИ ОС
            System.out.println("\n=== Статистика операционных систем ===");
            HashMap<String, Double> osStats = stats.getOsStatistics();
            if (osStats.isEmpty()) {
                System.out.println("Данные об ОС не найдены");
            } else {
                for (String os : osStats.keySet()) {
                    double share = osStats.get(os);
                    System.out.printf("%s: %.2f%%%n", os, share * 100);
                }
            }

        } catch (LongLineException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}