package com.ships;

import java.util.ArrayList;

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

    public static ArrayList<ShipType> convertShipList(ShipType[] array){
        ArrayList<ShipType> temp = new ArrayList<>();
        for(ShipType st : array){
            temp.add(st);
        }
        return temp;
    }

    public static ShipType[] getDefaultShipTypes(){
        ShipType[] defaultTypes = {ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.FRIGATE, ShipType.FRIGATE, ShipType.MINESWEEPER};
        return defaultTypes;
    }

    public static char parseCharacterFromInt(int value) {
        return (char) (value + 65);
    }
}
