package com.ships;

import java.util.ArrayList;

public class Ship {
    ShipType type;
    int xPos;
    int yPos;
    boolean verticalRotation;
    int hits;
    int length;
    boolean sunk;

    public Ship(ShipType type, int xPos, int yPos, boolean verticalRotation) {
        this.type = type;
        this.xPos = xPos;
        this.yPos = yPos;
        this.verticalRotation = verticalRotation;
        switch (type) {
            case BATTLESHIP:
                length = 5;
                break;
            case CRUISER:
                length = 4;
                break;
            case FRIGATE:
                length = 3;
                break;
            case MINESWEEPER:
                length = 2;
                break;
            default:
                length = 0;
        }
    }

    public boolean hitScan(int x, int y) {
        if (verticalRotation) {
            if (x == xPos && y >= yPos && y <= yPos + length - 1) {
                hits++;
                if (hits == length)
                    sunk = true;
                System.out.println("HIT");
                return true;
            }
        } else {
            if (y == yPos && x >= xPos && y <= xPos + length - 1) {
                hits++;
                if (hits == length)
                    sunk = true;
                System.out.println("HIT");
                return true;
            }
        }

        return false;
    }

    public ArrayList<Coordinate> getCoordinates() {

        ArrayList<Coordinate> coordinates = new ArrayList<>();

        if (verticalRotation) {
            for (int i = 0; i <= type.getLength(); i++) {
                coordinates.add(new Coordinate(xPos, yPos + i));
            }
        } else {
            for (int i = 0; i <= type.getLength(); i++) {
                coordinates.add(new Coordinate(xPos + i, yPos));
            }
        }

        return coordinates;
    }

}
