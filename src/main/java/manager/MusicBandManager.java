package manager;

import commands.*;
import models.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Менеджер для управления коллекцией музыкальных групп.
 */
public class MusicBandManager {
    private List<MusicBand> musicBands;
    private String fileName;
    private final Map<String, Command> commands;

    /**
     * Конструктор класса MusicBandManager.
     */
    public MusicBandManager() {
        this.musicBands = new ArrayList<>();
        this.commands = new HashMap<>();
        registerCommands();
    }

    /**
     * Регистрирует все доступные команды в карте.
     */
    private void registerCommands() {
        commands.put("add", new AddCommand(this));
        commands.put("add_if_max", new AddIfMaxCommand(this));
        commands.put("remove_by_id", new RemoveByIdCommand(this));
        commands.put("clear", new ClearCommand(this));
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("filter_by_number_of_participants", new FilterByNumberOfParticipantsCommand(this));
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand(this));
        commands.put("max_by_name", new MaxByNameCommand(this));
        commands.put("remove_any_by_description", new RemoveAnyByDescriptionCommand(this));
        commands.put("remove_head", new RemoveHeadCommand(this));
        commands.put("remove_lower", new RemoveLowerCommand(this));
        commands.put("save", new SaveCommand(this));
        commands.put("show", new ShowCommand(this));
        commands.put("update", new UpdateCommand(this));
        commands.put("insert_at", new InsertAtCommand(this));
        commands.put("sort", new SortCommand(this));
        commands.put("history", new HistoryCommand());
        commands.put("average_of_number_of_participants", new AverageOfNumberOfParticipantsCommand(this));
        commands.put("filter_greater_than_number_of_participants", new FilterGreaterThanNumberOfParticipantsCommand(this));
    }

    /**
     * Возвращает карту зарегистрированных команд.
     *
     * @return карта команд
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    /**
     * Загружает данные из указанного файла.
     *
     * @param fileName имя файла для загрузки
     */
    public void loadFromFile(String fileName) {
        this.fileName = fileName;
        musicBands.clear();
        Set<Long> usedIds = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                MusicBand band = parseMusicBand(line);
                if (band != null && !usedIds.contains(band.getId())) {
                    musicBands.add(band);
                    usedIds.add(band.getId());
                    count++;
                } else if (band != null) {
                    System.out.println("Пропущен дубликат ID: " + band.getId());
                }
            }
            System.out.println("Загружено " + count + " музыкальных групп из файла " + fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + fileName + ". Будет создан новый файл при сохранении.");
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке данных из файла: " + e.getMessage());
        }
    }

    /**
     * Парсит строку в объект MusicBand.
     *
     * @param line строка с данными в формате CSV
     * @return объект MusicBand или null при ошибке парсинга
     */
    private MusicBand parseMusicBand(String line) {
        line = line.trim().replaceAll("\\s+", " ");
        if (line.isEmpty()) {
            return null;
        }
        String[] parts = line.split(",", -1); // -1 to include empty fields

        if (parts.length != 9) {
            System.out.println("Некорректная строка (неверное количество полей): " + line);
            return null;
        }

        String name = parts[0].trim();
        Double x;
        Integer y;
        int numberOfParticipants;
        String description = parts[4].trim();
        Date establishmentDate;
        MusicGenre genre;
        String bestAlbumName = parts[7].trim();
        Integer bestAlbumLength;

        try {
            if (name.isEmpty()) {
                System.out.println("Ошибка: Пустое имя группы в строке: " + line);
                return null;
            }
            x = NumberFormat.getInstance(Locale.US).parse(parts[1].trim()).doubleValue();
            y = Integer.parseInt(parts[2].trim());
            numberOfParticipants = Integer.parseInt(parts[3].trim());
            if (numberOfParticipants < 0) {
                System.out.println("Ошибка: Отрицательное количество участников в строке: " + line);
                return null;
            }
            if (parts[5].trim().isEmpty()) {
                System.out.println("Ошибка: Пустая дата основания в строке: " + line);
                return null;
            }
            establishmentDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[5].trim());
            if (parts[6].trim().isEmpty()) {
                System.out.println("Ошибка: Пустой жанр в строке: " + line);
                return null;
            }
            genre = MusicGenre.valueOf(parts[6].trim().toUpperCase());
            if (bestAlbumName.isEmpty()) {
                System.out.println("Ошибка: Пустое название альбома в строке: " + line);
                return null;
            }
            bestAlbumLength = Integer.parseInt(parts[8].trim());
            if (bestAlbumLength <= 0) {
                System.out.println("Ошибка: Некорректная длина альбома в строке: " + line);
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Ошибка парсинга числовых данных в строке: " + line);
            return null;
        } catch (ParseException e) {
            System.out.println("Ошибка парсинга даты в строке: " + line);
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка парсинга жанра в строке: " + line);
            return null;
        }

        try {
            Coordinates coordinates = new Coordinates(x, y);
            Album bestAlbum = new Album(bestAlbumName, bestAlbumLength);
            if (description.isEmpty()) {
                description = null;
            }
            return new MusicBand(name, coordinates, numberOfParticipants, description, establishmentDate, genre, bestAlbum);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка создания MusicBand: " + e.getMessage() + " в строке: " + line);
            return null;
        }
    }

    /**
     * Добавляет группу в коллекцию, если она максимальна по критерию сравнения.
     *
     * @param newBand новая музыкальная группа
     * @return true, если группа добавлена, false в противном случае
     */
    public boolean addIfMax(MusicBand newBand) {
        if (musicBands.isEmpty() || newBand.compareTo(Collections.max(musicBands)) > 0) {
            musicBands.add(newBand);
            System.out.println("Музыкальная группа добавлена: " + newBand);
            return true;
        } else {
            System.out.println("Музыкальная группа не добавлена, так как она не максимальна");
            return false;
        }
    }

    /**
     * Удаляет группу из коллекции по её ID.
     *
     * @param id ID группы
     * @return true, если группа была удалена, false в противном случае
     */
    public boolean removeById(int id) {
        return musicBands.removeIf(band -> band.getId() == id);
    }

    /**
     * Очищает коллекцию музыкальных групп.
     */
    public void clear() {
        musicBands.clear();
    }

    /**
     * Сохраняет коллекцию в CSV-файл.
     *
     * @param fileName имя файла для сохранения; если null, используется имя файла, указанное при загрузке
     */
    public void saveToFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            fileName = this.fileName;
        }
        if (fileName == null) {
            System.out.println("Ошибка: Имя файла не указано.");
            return;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (MusicBand band : musicBands) {
                writer.println(bandToString(band));
            }
            System.out.println("Данные успешно сохранены в файл: " + fileName);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных в файл: " + e.getMessage());
        }
    }

    /**
     * Преобразует объект MusicBand в строку формата CSV.
     *
     * @param band музыкальная группа
     * @return строка в формате CSV
     */
    private String bandToString(MusicBand band) {
        return String.join(",",
                band.getName(),
                String.valueOf(band.getCoordinates().getX()),
                String.valueOf(band.getCoordinates().getY()),
                String.valueOf(band.getNumberOfParticipants()),
                band.getDescription() != null ? band.getDescription() : "",
                new SimpleDateFormat("yyyy-MM-dd").format(band.getEstablishmentDate()),
                band.getGenre().name(),
                band.getBestAlbum().getName(),
                String.valueOf(band.getBestAlbum().getLength())
        );
    }

    /**
     * Возвращает музыкальную группу по её ID.
     *
     * @param id ID группы
     * @return объект MusicBand или null, если группа не найдена
     */
    public MusicBand getMusicBandById(int id) {
        return musicBands.stream().filter(band -> band.getId() == id).findFirst().orElse(null);
    }

    /**
     * Возвращает коллекцию музыкальных групп.
     *
     * @return список музыкальных групп
     */
    public List<MusicBand> getMusicBands() {
        return musicBands;
    }

    /**
     * Фильтрует группы по количеству участников.
     *
     * @param numberOfParticipants количество участников для фильтрации
     * @return список групп с указанным количеством участников
     */
    public List<MusicBand> filterByNumberOfParticipants(int numberOfParticipants) {
        return musicBands.stream()
                .filter(band -> band.getNumberOfParticipants() == numberOfParticipants)
                .toList();
    }

    /**
     * Удаляет одну группу по описанию.
     *
     * @param description описание группы
     * @return true, если группа была удалена, false в противном случае
     */
    public boolean removeAnyByDescription(String description) {
        return musicBands.removeIf(band -> band.getDescription() != null && band.getDescription().equalsIgnoreCase(description));
    }

    /**
     * Находит группу с максимальным именем (по алфавитному порядку).
     *
     * @return группа с максимальным именем или null, если коллекция пуста
     */
    public MusicBand getMaxByName() {
        return musicBands.stream().max(Comparator.comparing(MusicBand::getName)).orElse(null);
    }

    /**
     * Удаляет первый элемент коллекции.
     *
     * @return удалённый элемент или null, если коллекция пуста
     */
    public MusicBand removeHead() {
        if (!musicBands.isEmpty()) {
            return musicBands.remove(0);
        }
        return null;
    }

    /**
     * Удаляет группы с количеством участников меньше, чем у заданной группы.
     *
     * @param lowerBand эталонная группа для сравнения
     */
    public void removeLower(MusicBand lowerBand) {
        musicBands.removeIf(band -> band.getNumberOfParticipants() < lowerBand.getNumberOfParticipants());
    }

    /**
     * Выводит группу с максимальным именем.
     */
    public void maxByName() {
        MusicBand maxBand = getMaxByName();
        if (maxBand != null) {
            System.out.println("Группа с максимальным именем: " + maxBand);
        } else {
            System.out.println("Коллекция пуста.");
        }
    }

    /**
     * Обновляет группу по её ID.
     *
     * @param id ID группы
     * @param updatedBand обновлённые данные группы
     */
    public void update(int id, MusicBand updatedBand) {
        for (int i = 0; i < musicBands.size(); i++) {
            if (musicBands.get(i).getId() == id) {
                updatedBand.setId(id);
                musicBands.set(i, updatedBand);
                return;
            }
        }
        System.out.println("Группа с ID " + id + " не найдена.");
    }

    /**
     * Выполняет команду по строке.
     *
     * @param commandLine строка с командой
     */
    public void executeCommand(String commandLine) {
        executeCommand(commandLine, null);
    }

    /**
     * Выполняет команду по строке, используя BufferedReader для ввода данных.
     *
     * @param commandLine строка с командой
     * @param reader источник ввода данных (может быть null)
     */
    public void executeCommand(String commandLine, BufferedReader reader) {
        String[] parts = commandLine.trim().split(" ", 2);
        String cmd = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;

        Command command = commands.get(cmd);
        if (command == null) {
            System.out.println("Неизвестная команда: " + cmd);
            return;
        }

        HistoryCommand.addToHistory(cmd);
        if (reader != null) {
            command.execute(argument, reader);
        } else {
            command.execute(argument);
        }
    }
}