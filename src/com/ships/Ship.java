package com.ships;

import java.util.ArrayList;

public class Ship {
    ShipType type;
    public int xPos;
    public int yPos;
    boolean verticalRotation;
    int hits;
    public int length;
    boolean sunk;

    public Ship(ShipType type, int xPos, int yPos, boolean verticalRotation) {
        this.type = type;
        this.xPos = xPos;
        this.yPos = yPos;
        this.verticalRotation = verticalRotation;
        this.length = type.getLength();
    }

    public boolean hitScan(int x, int y) {
        if (verticalRotation) {
            if (x == xPos && y >= yPos && y <= yPos + length - 1) {
                hits++;
                if (hits == length) {
                    sunk = true;
                    System.out.println("SHIP SUNK " + hits + " " + length);
                } else {
                    //System.out.println("HIT " + hits + " " + length);
                }
                return true;
            }
        } else {
            if (y == yPos && x >= xPos && x <= xPos + length - 1) {
                hits++;
                if (hits == length) {
                    sunk = true;
                    System.out.println("SHIP SUNK " + hits + " " + length);
                } else {
                    //System.out.println("HIT " + hits + " " + length);
                }
                return true;
            }
        }

        return false;
    }

    public ArrayList<Coordinate> getCoordinates() {

        ArrayList<Coordinate> coordinates = new ArrayList<>();

        if (verticalRotation) {
            for (int i = 0; i < type.getLength(); i++) {
                coordinates.add(new Coordinate(xPos, yPos + i));
            }
        } else {
            for (int i = 0; i < type.getLength(); i++) {
                coordinates.add(new Coordinate(xPos + i, yPos));
            }
        }

        return coordinates;
    }
}
