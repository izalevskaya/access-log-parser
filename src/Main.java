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

            System.out.printf("Средний трафик в час: %.2f%n", stats.getTrafficRate());



        } catch (LongLineException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}