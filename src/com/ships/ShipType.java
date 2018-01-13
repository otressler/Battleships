package com.ships;

public enum ShipType {
    BATTLESHIP("Battleship"),
    CRUISER("Cruiser"),
    FRIGATE("Frigate"),
    MINESWEEPER("Minesweeper");

    private String className;

    ShipType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
