package models;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    private final double x;
    private final int y;

    public Coordinates(double x, int y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + '}';
    }
}