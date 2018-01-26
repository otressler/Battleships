package com.ships;

public enum ShipType {
    BATTLESHIP("Battleship", 5),
    CRUISER("Cruiser", 4),
    FRIGATE("Frigate", 3),
    MINESWEEPER("Minesweeper", 2);

    private String className;
    private int length;

    ShipType(String className, int length) {
        this.className = className;
        this.length = length;
    }

    public String getClassName() {
        return className;
    }

    public int getLength() {
        return length;
    }
}
