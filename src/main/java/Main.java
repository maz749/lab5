import commands.*;
import manager.MusicBandManager;
import models.MusicBand;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Ошибка: Укажите имя файла как аргумент командной строки.");
            return;
        }
        String fileName = args[0];
        MusicBandManager manager = new MusicBandManager();
        manager.loadFromFile(fileName);

        System.out.println("Данные о музыкальных группах успешно загружены:");
        for (MusicBand band : manager.getMusicBands()) {
            System.out.println(band);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите команду (help для списка команд):");

        while (true) {
            String command = scanner.nextLine();
            HistoryCommand.addToHistory(command.split(" ")[0]);
            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Выход из программы.");
                break;
            }
            manager.executeCommand(command);
        }

        scanner.close();
    }
}