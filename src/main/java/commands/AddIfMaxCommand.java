package commands;

import manager.MusicBandManager;
import models.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class AddIfMaxCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды AddIfMaxCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public AddIfMaxCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду добавления новой музыкальной группы, если она максимальна.
     *
     * @param argument аргумент команды (не используется)
     */
    @Override
    public void execute(String argument) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("Введите название группы:");
                String name = scanner.nextLine();
                if (name == null || name.trim().isEmpty()) {
                    throw new IllegalArgumentException("Название группы не может быть пустым.");
                }

                System.out.println("Введите координаты X:");
                Double x = Double.parseDouble(scanner.nextLine());

                System.out.println("Введите координаты Y:");
                Integer y = Integer.parseInt(scanner.nextLine());

                System.out.println("Введите количество участников:");
                int numberOfParticipants = Integer.parseInt(scanner.nextLine());
                if (numberOfParticipants <= 0) {
                    throw new IllegalArgumentException("Количество участников должно быть больше 0.");
                }

                System.out.println("Введите описание группы (можно оставить пустым):");
                String description = scanner.nextLine();
                if (description.isEmpty()) {
                    description = null;
                }

                System.out.println("Введите дату основания (yyyy-MM-dd):");
                String dateStr = scanner.nextLine();
                Date establishmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

                System.out.println("Введите жанр. Доступные жанры: " + Arrays.toString(MusicGenre.values()));
                MusicGenre genre = MusicGenre.valueOf(scanner.nextLine().toUpperCase());

                System.out.println("Введите название лучшего альбома:");
                String bestAlbumName = scanner.nextLine();
                if (bestAlbumName.isEmpty()) {
                    throw new IllegalArgumentException("Название альбома не может быть пустым.");
                }

                System.out.println("Введите длину лучшего альбома:");
                Integer bestAlbumLength = Integer.parseInt(scanner.nextLine());
                if (bestAlbumLength <= 0) {
                    throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                }

                Coordinates coordinates = new Coordinates(x, y);
                Album bestAlbum = new Album(bestAlbumName, bestAlbumLength);
                MusicBand newBand = new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);

                if (manager.addIfMax(newBand)) {
                    System.out.println("Группа добавлена, так как она имеет максимальное количество участников.");
                } else {
                    System.out.println("Группа не добавлена, так как она не имеет максимального количества участников.");
                }
                break;
            } catch (ParseException e) {
                System.out.println("Ошибка при вводе даты. Пожалуйста, используйте формат yyyy-MM-dd. Попробуйте снова.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введено некорректное число. Попробуйте снова.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }
}