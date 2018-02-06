package com.ships;

import java.util.ArrayList;

public class Util {
    /**
     * Beautifier for console output. Inserts space
     * @param s
     * @param n
     * @return
     */
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
    /**
     * Beautifier for console output. Inserts space
     * @param s
     * @param n
     * @return
     */
    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    /**
     * Retrieves x coordinate from coordinate string.
     * Maps Characters to integers. A -> 0
     * @param coordinates eg A0V or A0
     * @return
     */
    public static int parseXPosition(String coordinates) {
        return Character.getNumericValue(Character.toUpperCase(coordinates.charAt(0))) - 10;
    }

    /**
     * Retrieves y coordinate from coordinate string.
     * @param coordinates eg A0V or A0
     * @return
     */
    public static int parseYPosition(String coordinates) {
        return Character.getNumericValue(coordinates.charAt(1));
    }

    /**
     * If the ship will be placed vertically or horizontally.
     * @param coordinates eg A0V
     * @return
     */
    public static boolean parseOrientation(String coordinates) {
        return Character.toLowerCase(coordinates.charAt(2)) == 'v';
    }

    /**
     * Converts the shiplist to an arraylist.
     * @param array
     * @return
     */
    public static ArrayList<ShipType> convertShipList(ShipType[] array) {
        ArrayList<ShipType> temp = new ArrayList<>();
        for (ShipType st : array) {
            temp.add(st);
        }
        return temp;
    }

    /**
     * Used to retrieve ships in play
     * @return
     */
    public static ShipType[] getDefaultShipTypes() {
        return new ShipType[]{ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.FRIGATE, ShipType.FRIGATE, ShipType.MINESWEEPER};
    }

    /**
     * Converts integer to character. e.g 0 -> A
     * @param value
     * @return
     */
    public static char parseCharacterFromInt(int value) {
        return (char) (value + 65);
    }
}
