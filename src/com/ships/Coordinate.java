package com.ships;

/**
 * Facilitates working with coordinates
 */
public class Coordinate implements Comparable<Coordinate> {
    int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Checks if a coordinate is within a field.
     * @param c Coordinate
     * @return true if within field, false if not
     */
    public static boolean validCoordinate(Coordinate c) {
        return c.getX() >= 0 && c.getX() <= 9 && c.getY() >= 0 && c.getY() <= 9;
    }

    /**
     * Param overload
     */
    public static boolean validCoordinate(int x, int y) {
        return x >= 0 && x <= 9 && y >= 0 && y <= 9;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Shift a coordinate based on x and y values
     * @param deltaX x shift
     * @param deltaY y shift
     * @return shifted coordinate
     */
    public Coordinate delta(int deltaX, int deltaY) {
        return new Coordinate(x + deltaX, y + deltaY);
    }

    /**
     * Stringifier for output and debug purposes
     * @return X|Y
     */
    public String toString() {
        return x + "|" + y;
    }

    /**
     * Used for comparing equality of coordinates
     * @param c Coordinate this one is compared to
     * @return true if equal, false if not
     */
    public boolean equals(Coordinate c) {
        return c.x == this.x && c.y == this.y;
    }

    /**
     * Calculates the euclidean distance between this coordinate and another
     * @param c another coordinate
     * @return distance
     */
    public double euclideanDistance(Coordinate c) {
        int deltaX = (this.x - c.getX()) * (this.x - c.getX());
        int deltaY = (this.y - c.getY()) * (this.x - c.getX());
        return Math.sqrt(deltaX + deltaY);
    }

    /**
     * Used for sorting coordinates first row than columnwise.
     * @param o another coordinate
     * @return 1 if this is greater, 0 if equal, -1 if smaller
     */
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

    /**
     * Hash value of coordinate.
     */
    public int hashCode() {
        return x + 10 * y;
    }
}
