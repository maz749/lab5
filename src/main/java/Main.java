import server.Server;
import client.Client;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        String mode = args[0].toLowerCase();
        switch (mode) {
            case "server":
                if (args.length != 2) {
                    System.out.println("Ошибка: Для режима 'server' требуется имя файла данных.");
                    printUsage();
                    System.exit(1);
                }
                String filename = args[1];
                startServer(filename);
                break;

            case "client":
                if (args.length != 1) {
                    System.out.println("Ошибка: Режим 'client' не принимает аргументов.");
                    printUsage();
                    System.exit(1);
                }
                startClient();
                break;

            default:
                System.out.println("Ошибка: Неизвестный режим: " + mode);
                printUsage();
                System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Использование: java -jar music-band-manager.jar <режим> [аргументы]");
        System.out.println("Режимы:");
        System.out.println("  server <имя_файла> - Запустить сервер с указанным файлом данных");
        System.out.println("  client - Запустить клиент");
    }

    private static void startServer(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.canRead()) {
            System.out.println("Ошибка: Файл " + filename + " не существует или недоступен для чтения.");
            System.exit(1);
        }

        try {
            Server server = new Server(filename);
            server.start();
        } catch (Exception e) {
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void startClient() {
        try {
            Client client = new Client();
            client.start();
        } catch (Exception e) {
            System.out.println("Ошибка при запуске клиента: " + e.getMessage());
            System.exit(1);
        }
    }
}