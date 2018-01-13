package com.ships;

public class Util {
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static int parseXPosition(String coordinates) {
        return Character.getNumericValue(Character.toUpperCase(coordinates.charAt(0))) - 10;
    }

    public static int parseYPosition(String coordinates) {
        return Character.getNumericValue(coordinates.charAt(1));
    }

    public static boolean parseOrientation(String coordinates) {
        return Character.toLowerCase(coordinates.charAt(2)) == 'v';
    }
}
