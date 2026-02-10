import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Получаем первое число
        System.out.println("Введите первое число: ");
        int number1 = scanner.nextInt();

        // Получаем второе число
        System.out.println("Введите второе число: ");
        int number2 = scanner.nextInt();

        // Вычисляем сумму, разность и произведение (используем int)
        int sum = number1 + number2;
        int difference = number1 - number2;
        int product = number1 * number2;

        // Вычисляем частное (используем double для точности)
        double quotient = (double) number1 / number2;

        // Выводим результаты
        System.out.println("Сумма: " + sum);
        System.out.println("Разность: " + difference);
        System.out.println("Произведение: " + product);
        System.out.println("Частное: " + quotient);

        scanner.close();
    }
}
