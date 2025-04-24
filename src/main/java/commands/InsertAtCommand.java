package commands;

import manager.MusicBandManager;
import models.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class InsertAtCommand implements Command {
    private MusicBandManager manager;

    /**
     * Конструктор команды InsertAtCommand.
     *
     * @param manager менеджер музыкальных групп
     */
    public InsertAtCommand(MusicBandManager manager) {
        this.manager = manager;
    }

    /**
     * Выполняет команду добавления элемента по индексу.
     *
     * @param argument индекс, на который нужно добавить элемент
     */
    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.isEmpty()) {
                throw new IllegalArgumentException("Не указан индекс.");
            }
            int index = Integer.parseInt(argument);
            if (index < 0 || index > manager.getMusicBands().size()) {
                throw new IllegalArgumentException("Индекс вне допустимого диапазона.");
            }

            Scanner scanner = new Scanner(System.in);
            MusicBand newBand = createMusicBand(scanner);
            if (newBand != null) {
                manager.getMusicBands().add(index, newBand);
                System.out.println("Группа добавлена на позицию " + index + ": " + newBand);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: Индекс должен быть числом.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Создает объект MusicBand на основе введенных данных.
     *
     * @param scanner сканер для ввода данных
     * @return объект MusicBand или null при некорректных данных
     */
    private MusicBand createMusicBand(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Введите имя группы: ");
                String name = scanner.nextLine();
                if (name == null || name.trim().isEmpty()) {
                    throw new IllegalArgumentException("Имя группы не может быть пустым.");
                }

                System.out.print("Введите координаты (x): ");
                Double x = Double.parseDouble(scanner.nextLine());

                System.out.print("Введите координаты (y): ");
                Integer y = Integer.parseInt(scanner.nextLine());

                System.out.print("Введите количество участников: ");
                int numberOfParticipants = Integer.parseInt(scanner.nextLine());
                if (numberOfParticipants <= 0) {
                    throw new IllegalArgumentException("Количество участников должно быть больше 0.");
                }

                System.out.print("Введите описание (можно оставить пустым): ");
                String description = scanner.nextLine();
                if (description.isEmpty()) {
                    description = null;
                }

                System.out.print("Введите дату основания (yyyy-MM-dd): ");
                Date establishmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(scanner.nextLine());

                System.out.print("Введите жанр. Доступные жанры: " + Arrays.toString(MusicGenre.values()));
                MusicGenre genre = MusicGenre.valueOf(scanner.nextLine().toUpperCase());

                System.out.print("Введите название лучшего альбома: ");
                String albumName = scanner.nextLine();
                if (albumName.isEmpty()) {
                    throw new IllegalArgumentException("Название альбома не может быть пустым.");
                }

                System.out.print("Введите длину альбома: ");
                Integer albumLength = Integer.parseInt(scanner.nextLine());
                if (albumLength <= 0) {
                    throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                }

                Coordinates coordinates = new Coordinates(x, y);
                Album bestAlbum = new Album(albumName, albumLength);
                return new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);
            } catch (ParseException e) {
                System.out.println("Некорректный формат даты. Используйте yyyy-MM-dd. Попробуйте снова.");
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введено некорректное число. Попробуйте снова.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }
}