//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.server;

import java.io.File;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Ошибка: Для режима 'server' требуется имя файла данных.");
            printUsage();
            System.exit(1);
        }

        String filename = args[0];
        File file = new File(filename);
        if (!file.exists() || !file.canRead()) {
            System.out.println("Ошибка: Файл " + filename + " не существует или недоступен для чтения.");
            System.exit(1);
        }

        try {
            Server server = new Server(filename);
            server.start();
        } catch (Exception var4) {
            Exception e = var4;
            System.out.println("Ошибка при запуске сервера: " + e.getMessage());
            System.exit(1);
        }

    }

    private static void printUsage() {
        System.out.println("Использование: java -jar lab-server.jar <имя_файла>");
        System.out.println("  <имя_файла> - Файл для хранения данных");
    }
}
