package models;

public class Album {
    private String name; //Поле не может быть null
    private Integer length; //Поле не может быть null, Значение поля должно быть больше 0

    public Album(String name, Integer length) {
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