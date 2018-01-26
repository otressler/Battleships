package com.ships;

public class Coordinate implements Comparable<Coordinate> {
    int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate() {
    }

    public static boolean validCoordinate(Coordinate c) {
        return c.getX() >= 0 && c.getX() <= 9 && c.getY() >= 0 && c.getY() <= 9;
    }

    public static boolean validCoordinate(int x, int y) {
        return x >= 0 && x <= 9 && y >= 0 && y <= 9;
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

    public String toString() {
        return x + "|" + y;
    }

    public boolean equals(Coordinate c) {
        return c.x == this.x && c.y == this.y;
    }

    @Override
    public int compareTo(Coordinate o) {
        if (this.y < o.y) {
            return -1;
        } else if (this.y > o.y) {
            return 1;
        } else {
            if (this.x < o.x) {
                return -1;
            } else if (this.x > o.x) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public int hashCode(){
        return x + 10*y;
    }
}
