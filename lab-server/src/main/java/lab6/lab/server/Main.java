package lab6.lab.server;

public class Main {
    public static void main(String[] args) {
        int port = 5001; // Порт по умолчанию, соответствует клиенту
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Порт должен быть числом.");
                System.exit(1);
            }
        } else if (args.length > 1) {
            System.out.println("Ошибка: Укажите только порт.");
            printUsage();
            System.exit(1);
        }

        try {
            Server server = new Server(port);
            server.start();
        } catch (Exception e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Использование: java -jar lab-server.jar [порт]");
        System.out.println("  порт - Номер порта для сервера (по умолчанию 5001)");
    }
}