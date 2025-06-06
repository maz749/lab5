//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package lab6.lab.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import lab6.lab.common.CommandRequest;
import lab6.lab.common.models.Album;
import lab6.lab.common.models.Coordinates;
import lab6.lab.common.models.MusicBand;
import lab6.lab.common.models.MusicGenre;

public class CommandParser {
    private final Scanner scanner;
    private static final int MAX_NAME_LENGTH = 1000;

    public CommandParser() {
        this.scanner = new Scanner(System.in);
    }

    public CommandRequest parseCommand(String input, BufferedReader scriptReader) throws IOException {
        String[] parts = input.trim().split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : null;
        if (command.equals("save")) {
            System.out.println("Команда 'save' недоступна на клиенте.");
            return null;
        } else if (this.requiresObject(command)) {
            MusicBand band = scriptReader != null ? this.createMusicBandFromScript(scriptReader) : this.createMusicBand((BufferedReader)null);
            return band == null ? null : new CommandRequest(command, argument, band);
        } else {
            return new CommandRequest(command, argument, (Object)null);
        }
    }

    private boolean requiresObject(String command) {
        return command.equals("add") || command.equals("add_if_max") || command.equals("update") || command.equals("remove_lower") || command.equals("insert_at");
    }

    private MusicBand createMusicBand(BufferedReader scriptReader) throws IOException {
        try {
            System.out.println("Введите данные музыкальной группы:");
            String name = (String)this.readField("Имя: ", scriptReader, (input) -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Имя не может быть пустым.");
                } else if (input.length() > 1000) {
                    throw new IllegalArgumentException("Имя слишком длинное. Максимальная длина: 1000");
                } else {
                    return input;
                }
            });
            if (name == null) {
                return null;
            } else {
                Double x = (Double)this.readField("Координата X: ", scriptReader, (input) -> {
                    double value = Double.parseDouble(input);
                    if (Math.abs(value) > 1000000.0) {
                        throw new IllegalArgumentException("Координата X слишком большая.");
                    } else {
                        return value;
                    }
                });
                if (x == null) {
                    return null;
                } else {
                    Integer y = (Integer)this.readField("Координата Y: ", scriptReader, (input) -> {
                        int value = Integer.parseInt(input);
                        if (Math.abs(value) > 1000000) {
                            throw new IllegalArgumentException("Координата Y слишком большая.");
                        } else {
                            return value;
                        }
                    });
                    if (y == null) {
                        return null;
                    } else {
                        Integer participants = (Integer)this.readField("Количество участников: ", scriptReader, (input) -> {
                            int value = Integer.parseInt(input);
                            if (value < 0) {
                                throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
                            } else if (value > 1000000) {
                                throw new IllegalArgumentException("Количество участников слишком большое.");
                            } else {
                                return value;
                            }
                        });
                        if (participants == null) {
                            return null;
                        } else {
                            String description = (String)this.readField("Описание (опционально): ", scriptReader, (input) -> {
                                return input.isEmpty() ? null : input;
                            });
                            Date establishmentDate = (Date)this.readField("Дата основания (гггг-мм-дд): ", scriptReader, (input) -> {
                                return (new SimpleDateFormat("yyyy-MM-dd")).parse(input);
                            });
                            if (establishmentDate == null) {
                                return null;
                            } else {
                                MusicGenre genre = (MusicGenre)this.readField("Жанр (" + Arrays.toString(MusicGenre.values()) + "): ", scriptReader, (input) -> {
                                    return MusicGenre.valueOf(input.toUpperCase());
                                });
                                if (genre == null) {
                                    return null;
                                } else {
                                    String albumName = (String)this.readField("Название лучшего альбома: ", scriptReader, (input) -> {
                                        if (input.isEmpty()) {
                                            throw new IllegalArgumentException("Название альбома не может быть пустым.");
                                        } else {
                                            return input;
                                        }
                                    });
                                    if (albumName == null) {
                                        return null;
                                    } else {
                                        Integer albumLength = (Integer)this.readField("Длина альбома (в секундах): ", scriptReader, (input) -> {
                                            int value = Integer.parseInt(input);
                                            if (value <= 0) {
                                                throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                                            } else if (value > 1000000) {
                                                throw new IllegalArgumentException("Длина альбома слишком большая.");
                                            } else {
                                                return value;
                                            }
                                        });
                                        if (albumLength == null) {
                                            return null;
                                        } else {
                                            Coordinates coordinates = new Coordinates(x, y);
                                            Album bestAlbum = new Album(albumName, albumLength);
                                            return new MusicBand(name, coordinates, participants, description, establishmentDate, genre, bestAlbum);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception var13) {
            Exception e = var13;
            System.out.println("Ошибка создания музыкальной группы: " + e.getMessage());
            if (scriptReader != null) {
                throw new IOException("Некорректные данные в скрипте: " + e.getMessage());
            } else {
                return null;
            }
        }
    }

    private MusicBand createMusicBandFromScript(BufferedReader scriptReader) throws IOException {
        try {
            String name = (String)this.readField("Имя: ", scriptReader, (input) -> {
                if (input.isEmpty()) {
                    throw new IllegalArgumentException("Имя не может быть пустым.");
                } else if (input.length() > 1000) {
                    throw new IllegalArgumentException("Имя слишком длинное. Максимальная длина: 1000");
                } else {
                    return input;
                }
            });
            if (name == null) {
                throw new IOException("Недостаточно данных для имени группы.");
            } else {
                Double x = (Double)this.readField("Координата X: ", scriptReader, (input) -> {
                    double value = Double.parseDouble(input);
                    if (Math.abs(value) > 1000000.0) {
                        throw new IllegalArgumentException("Координата X слишком большая.");
                    } else {
                        return value;
                    }
                });
                if (x == null) {
                    throw new IOException("Недостаточно данных для координаты X.");
                } else {
                    Integer y = (Integer)this.readField("Координата Y: ", scriptReader, (input) -> {
                        int value = Integer.parseInt(input);
                        if (Math.abs(value) > 1000000) {
                            throw new IllegalArgumentException("Координата Y слишком большая.");
                        } else {
                            return value;
                        }
                    });
                    if (y == null) {
                        throw new IOException("Недостаточно данных для координаты Y.");
                    } else {
                        Integer participants = (Integer)this.readField("Количество участников: ", scriptReader, (input) -> {
                            int value = Integer.parseInt(input);
                            if (value < 0) {
                                throw new IllegalArgumentException("Количество участников не может быть отрицательным.");
                            } else if (value > 1000000) {
                                throw new IllegalArgumentException("Количество участников слишком большое.");
                            } else {
                                return value;
                            }
                        });
                        if (participants == null) {
                            throw new IOException("Недостаточно данных для количества участников.");
                        } else {
                            String description = (String)this.readField("Описание (опционально): ", scriptReader, (input) -> {
                                return input.isEmpty() ? null : input;
                            });
                            Date establishmentDate = (Date)this.readField("Дата основания (гггг-мм-дд): ", scriptReader, (input) -> {
                                return (new SimpleDateFormat("yyyy-MM-dd")).parse(input);
                            });
                            if (establishmentDate == null) {
                                throw new IOException("Недостаточно данных для даты основания.");
                            } else {
                                MusicGenre genre = (MusicGenre)this.readField("Жанр (" + Arrays.toString(MusicGenre.values()) + "): ", scriptReader, (input) -> {
                                    return MusicGenre.valueOf(input.toUpperCase());
                                });
                                if (genre == null) {
                                    throw new IOException("Недостаточно данных для жанра.");
                                } else {
                                    String albumName = (String)this.readField("Название лучшего альбома: ", scriptReader, (input) -> {
                                        if (input.isEmpty()) {
                                            throw new IllegalArgumentException("Название альбома не может быть пустым.");
                                        } else {
                                            return input;
                                        }
                                    });
                                    if (albumName == null) {
                                        throw new IOException("Недостаточно данных для названия альбома.");
                                    } else {
                                        Integer albumLength = (Integer)this.readField("Длина альбома (в секундах): ", scriptReader, (input) -> {
                                            int value = Integer.parseInt(input);
                                            if (value <= 0) {
                                                throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
                                            } else if (value > 1000000) {
                                                throw new IllegalArgumentException("Длина альбома слишком большая.");
                                            } else {
                                                return value;
                                            }
                                        });
                                        if (albumLength == null) {
                                            throw new IOException("Недостаточно данных для длины альбома.");
                                        } else {
                                            Coordinates coordinates = new Coordinates(x, y);
                                            Album bestAlbum = new Album(albumName, albumLength);
                                            return new MusicBand(name, coordinates, participants, description, establishmentDate, genre, bestAlbum);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception var13) {
            Exception e = var13;
            System.out.println("Ошибка создания музыкальной группы из скрипта: " + e.getMessage());
            throw new IOException("Некорректные данные в скрипте: " + e.getMessage());
        }
    }

    private <T> T readField(String prompt, BufferedReader scriptReader, Validator<T> validator) throws IOException {
        while(true) {
            try {
                String input = this.readLine(prompt, scriptReader);
                if (input == null && scriptReader == null) {
                    System.out.println("Ввод прерван (Ctrl+D).");
                    return null;
                }

                return validator.validate(input);
            } catch (IllegalArgumentException var5) {
                IllegalArgumentException e = var5;
                System.out.println("Ошибка: " + e.getMessage());
                if (scriptReader != null) {
                    throw new IOException("Некорректный ввод в скрипте: " + e.getMessage());
                }
            } catch (Exception var6) {
                Exception e = var6;
                System.out.println("Ошибка ввода: " + e.getMessage());
                if (scriptReader != null) {
                    throw new IOException("Некорректный ввод в скрипте: " + e.getMessage());
                }
            }
        }
    }

    private String readLine(String prompt, BufferedReader scriptReader) throws IOException {
        System.out.print(prompt);
        if (scriptReader != null) {
            String line = scriptReader.readLine();
            if (line == null) {
                throw new IOException("Достигнут конец файла скрипта.");
            } else {
                System.out.println(line);
                return line.trim();
            }
        } else {
            return !this.scanner.hasNextLine() ? null : this.scanner.nextLine().trim();
        }
    }

    @FunctionalInterface
    private interface Validator<T> {
        T validate(String var1) throws Exception;
    }
}
