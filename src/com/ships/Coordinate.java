package com.ships;

public class Coordinate {
    int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinate delta(int deltaX, int deltaY) {
        return new Coordinate(x + deltaX, y + deltaY);
    }
}
