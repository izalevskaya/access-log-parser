import java.io.*;
import java.util.Scanner;

public class AccessLogParser {
    public static void main(String[] args) {
        String path = "C:\\Users\\ASUSVB\\IdeaProjects\\AccessLogParser\\access.log";
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Файл не найден.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int totalLines = 0;
            int maxLength = 0;
            int minLength = Integer.MAX_VALUE;

            while ((line = reader.readLine()) != null) {
                totalLines++;
                int length = line.length();
                if (length > maxLength) {
                    maxLength = length;
                }
                if (length < minLength) {
                    minLength = length;
                }
                if (length > 1024) {
                    throw new RuntimeException("Длина строки превышает 1024 символа.");
                }
            }

            System.out.println("Общее количество строк: " + totalLines);
            System.out.println("Длина самой длинной строки: " + maxLength);
            System.out.println("Длина самой короткой строки: " + minLength);

        } catch (IOException | RuntimeException ex) {
            ex.printStackTrace();
        }
    }
}

