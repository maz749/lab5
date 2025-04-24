package manager;

import models.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Класс для чтения и записи данных музыкальных групп в CSV-файл.
 */
public class FileStorage {
    private final MusicBandFactory factory;
    private String fileName;

    public FileStorage(MusicBandFactory factory) {
        this.factory = factory;
    }

    public void loadFromFile(String fileName, MusicBandCollection collection) {
        this.fileName = fileName;
        collection.getMusicBands().clear();
        Set<Long> usedIds = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                MusicBand band = factory.parseMusicBand(line);
                if (band != null && !usedIds.contains(band.getId())) {
                    collection.add(band);
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

    public void saveToFile(String fileName, MusicBandCollection collection) {
        if (fileName == null || fileName.isEmpty()) {
            fileName = this.fileName;
        }
        if (fileName == null) {
            System.out.println("Ошибка: Имя файла не указано.");
            return;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (MusicBand band : collection.getMusicBands()) {
                writer.println(factory.bandToString(band));
            }
            System.out.println("Данные успешно сохранены в файл: " + fileName);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных в файл: " + e.getMessage());
        }
    }
}