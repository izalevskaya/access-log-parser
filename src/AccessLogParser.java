import java.io.*;
import java.util.regex.*;

public class AccessLogParser {

    // Регулярное выражение для парсинга access.log
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+|\"-?\") \"([^\"]*)\" \"([^\"]*)\"$"
    );

    public static void main(String[] args) {
        String path = "C:\\Users\\ASUSVB\\IdeaProjects\\AccessLogParser\\access.log";
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("Файл не найден: " + path);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            parseLogFile(reader);
        } catch (IOException ex) {
            System.err.println("Ошибка при чтении файла: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void parseLogFile(BufferedReader reader) throws IOException {
        String line;
        int totalRequests = 0;
        int googlebotCount = 0;
        int yandexbotCount = 0;

        while ((line = reader.readLine()) != null) {
            // Проверка длины строки
            int length = line.length();
            if (length > 1024) {
                throw new RuntimeException("Длина строки превышает 1024 символа. Строка: " + length + " символов");
            }

            // Парсинг строки лога
            Matcher matcher = LOG_PATTERN.matcher(line);
            if (matcher.matches()) {
                totalRequests++;

                // Получаем User-Agent (последняя группа)
                String userAgent = matcher.group(9);

                // Обрабатываем User-Agent для определения бота
                String botName = extractBotName(userAgent);

                if ("Googlebot".equals(botName)) {
                    googlebotCount++;
                } else if ("YandexBot".equals(botName)) {
                    yandexbotCount++;
                }
            }
        }

        // Вывод результатов
        printStatistics(totalRequests, googlebotCount, yandexbotCount);
    }

    /**
     * Извлекает имя бота из User-Agent строки
     * Алгоритм:
     * 1. Выделить часть, которая находится в первых скобках
     * 2. Разделить по точке с запятой
     * 3. Взять второй фрагмент (индекс 1)
     * 4. Очистить от пробелов
     * 5. Отделить часть до слэша
     */
    private static String extractBotName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return null;
        }

        // Находим первую открывающую скобку
        int firstBracket = userAgent.indexOf('(');
        int lastBracket = userAgent.indexOf(')');

        if (firstBracket == -1 || lastBracket == -1 || firstBracket >= lastBracket) {
            return null;
        }

        // 1. Выделяем часть в первых скобках
        String bracketContent = userAgent.substring(firstBracket + 1, lastBracket);

        // 2. Разделяем по точке с запятой
        String[] parts = bracketContent.split(";");

        // 3. Берем второй фрагмент (индекс 1), если он существует
        if (parts.length >= 2) {
            // 4. Очищаем от пробелов
            String fragment = parts[1].trim();

            // 5. Отделяем часть до слэша
            int slashIndex = fragment.indexOf('/');
            if (slashIndex != -1) {
                return fragment.substring(0, slashIndex).trim();
            } else {
                return fragment;
            }
        }

        return null;
    }

    private static void printStatistics(int totalRequests, int googlebotCount, int yandexbotCount) {
        System.out.println("\n=== АНАЛИЗ ПОИСКОВЫХ БОТОВ ===");
        System.out.println("Всего обработано запросов: " + totalRequests);
        System.out.println("Запросов от Googlebot: " + googlebotCount);
        System.out.println("Запросов от YandexBot: " + yandexbotCount);

        // Вычисляем доли в процентах
        if (totalRequests > 0) {
            double googlebotShare = (double) googlebotCount / totalRequests * 100;
            double yandexbotShare = (double) yandexbotCount / totalRequests * 100;
            double totalBotsShare = (double) (googlebotCount + yandexbotCount) / totalRequests * 100;

            System.out.println("\n--- Доля запросов от ботов ---");
            System.out.printf("Googlebot: %.2f%%%n", googlebotShare);
            System.out.printf("YandexBot: %.2f%%%n", yandexbotShare);
            System.out.printf("Всего от поисковых ботов: %.2f%%%n", totalBotsShare);
        } else {
            System.out.println("Нет данных для анализа");
        }

        System.out.println("\n===============================");
    }
}