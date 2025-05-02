package models;

import java.io.Serializable;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Integer length;

    public Album(String name, Integer length) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Название альбома не может быть пустым или null.");
        }
        if (length == null || length <= 0) {
            throw new IllegalArgumentException("Длина альбома должна быть больше 0.");
        }
        this.name = name;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public Integer getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Album{name='" + name + "', length=" + length + '}';
    }
}