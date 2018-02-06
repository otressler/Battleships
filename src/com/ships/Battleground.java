package com.ships;

import java.util.ArrayList;

/**
 * Used for storing information about ships and the enemies guesses
 */
public class Battleground {
    //      y x
    public FieldState[][] battleground;
    public ArrayList<Ship> ships;
    Game game;


    /**
     * ONLY USE FOR AI
     */
    public Battleground() {
        this.battleground = new FieldState[10][10];
        for (int y = 0; y < battleground.length; y++) {
            for (int x = 0; x < battleground[y].length; x++) {
                battleground[y][x] = FieldState.NA;
            }
        }
        ships = new ArrayList<>(5);

    }

    /**
     * @param game that this battleground is created for
     */
    public Battleground(Game game) {
        this.battleground = new FieldState[10][10];
        this.game = game;
        for (int y = 0; y < battleground.length; y++) {
            for (int x = 0; x < battleground[y].length; x++) {
                battleground[y][x] = FieldState.NA;
            }
        }
        ships = new ArrayList<>(5);
    }

    /**
     * Place a ship on the field
     * @param ship Ship to be placed
     * @return Placement valid?
     */
    public boolean placeShip(Ship ship) {
        if (ship.verticalRotation) {
            if (ship.xPos >= 0 && ship.xPos < 10 && ship.yPos >= 0 && ship.yPos + ship.length - 1 < 10 && !checkForBlockedFields(ship)) {
                for (int i = 0; i < ship.length; i++) {
                    battleground[ship.yPos + i][ship.xPos] = FieldState.SHIP;
                    blockFieldsForPlacement(ship);
                }
                ships.add(ship);
                return true;
            }
        } else {
            if (ship.xPos >= 0 && ship.xPos + ship.length - 1 < 10 && ship.yPos >= 0 && ship.yPos < 10 && !checkForBlockedFields(ship)) {
                for (int i = 0; i < ship.length; i++) {
                    battleground[ship.yPos][ship.xPos + i] = FieldState.SHIP;
                    blockFieldsForPlacement(ship);
                }
                ships.add(ship);
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the ship at the given coordinate
     * @param coordinate Coordinate
     * @return Ship at this coordinate. Null if there is none.
     */
    public Ship findShipByCoordinate(Coordinate coordinate) {
        for (Ship s : ships) {
            for (Coordinate c : s.getCoordinates()) {
                if (c.equals(coordinate)) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Checks if a shot at Coordinate c was a hit.
     * @param c Guess coordinate
     * @return true if hit, false if miss
     */
    public boolean hitEvaluation(Coordinate c) {
        for (Ship s : ships) {
            if (s.hitScan(c.x, c.y)) {
                battleground[c.y][c.x] = Battleground.FieldState.HIT;
                if (s.sunk) {
                    if (s.verticalRotation) {
                        for (int i = 0; i < s.length; i++) {
                            battleground[s.yPos + i][s.xPos] = Battleground.FieldState.SUNK;
                        }
                    } else {
                        for (int i = 0; i < s.length; i++) {
                            battleground[s.yPos][s.xPos + i] = Battleground.FieldState.SUNK;
                        }
                    }
                }
                return true;
            }
        }
        battleground[c.y][c.x] = FieldState.MISS;
        return false;
    }

    /**
     * Check if any fields are blocked for ship placement
     *
     * @param ship The ship that is supposed to be placed
     * @return true if fields are blocked, false if they arent
     */
    public boolean checkForBlockedFields(Ship ship) {
        if (ship.verticalRotation) {
            for (int i = 0; i < ship.length; i++) {
                if (battleground[ship.yPos + i][ship.xPos].equals(FieldState.BLOCKED) || battleground[ship.yPos + i][ship.xPos].equals(FieldState.SHIP)) {
                    //System.out.println(ship.xPos + " " + (ship.yPos + i) + " blocked");
                    return true;
                }
            }
        } else {
            for (int i = 0; i < ship.length; i++) {
                if (battleground[ship.yPos][ship.xPos + i].equals(FieldState.BLOCKED) || battleground[ship.yPos][ship.xPos + i].equals(FieldState.SHIP)) {
                    //System.out.println(ship.xPos + " " + (ship.yPos + i) + " blocked");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Blocks field around a ship for further placement.
     * @param ship Ship to be placed.
     */
    private void blockFieldsForPlacement(Ship ship) {
        if (ship.verticalRotation) {
            // Block field over the ship
            if (ship.yPos > 0) {
                battleground[ship.yPos - 1][ship.xPos] = FieldState.BLOCKED;
            }
            // Block fields below the ship
            if (ship.yPos + ship.length <= 9) {
                battleground[ship.yPos + ship.length][ship.xPos] = FieldState.BLOCKED;
            }
            // Block fields covered by the ship
            for (int i = 0; i < ship.length; i++) {
                battleground[ship.yPos + i][ship.xPos] = FieldState.SHIP;
            }
            // Block fields left from the ship
            if (ship.xPos > 0) {
                for (int i = 0; i < ship.length; i++) {
                    battleground[ship.yPos + i][ship.xPos - 1] = FieldState.BLOCKED;
                }
            }
            // Block fields right from the ship
            if (ship.xPos < 9) {
                for (int i = 0; i < ship.length; i++) {
                    battleground[ship.yPos + i][ship.xPos + 1] = FieldState.BLOCKED;
                }
            }
        } else {
            // Block fields left from the ship
            if (ship.xPos > 0) {
                battleground[ship.yPos][ship.xPos - 1] = FieldState.BLOCKED;
            }
            // Block fields right from the ship
            if (ship.xPos + ship.length <= 9) {
                battleground[ship.yPos][ship.xPos + ship.length] = FieldState.BLOCKED;
            }
            // Block fields covered by the ship
            for (int i = 0; i < ship.length; i++) {
                battleground[ship.yPos][ship.xPos + i] = FieldState.SHIP;
            }
            // Block field over the ship
            if (ship.yPos > 0) {
                for (int i = 0; i < ship.length; i++) {
                    battleground[ship.yPos - 1][ship.xPos + i] = FieldState.BLOCKED;
                }
            }
            // Block fields below the ship
            if (ship.yPos < 9) {
                for (int i = 0; i < ship.length; i++) {
                    battleground[ship.yPos + 1][ship.xPos + i] = FieldState.BLOCKED;
                }
            }
        }
    }

    /**
     * All states that the battleground can have. Placement for Placement phase, enemy for enemy phase. Mostly used for
     * printing.
     */
    enum BattlegroundMode {
        ENEMY, PLACEMENT;
    }

    /**
     * All states a field can have. Ignore and Potential are only used by AI, Ignore blocked after placement phase.
     */
    public enum FieldState {
        IGNORE("I"),
        POTENTIAL(" "),
        HIT("X"),
        MISS("-"),
        SUNK("#"),
        BLOCKED("B"),
        SHIP("O"),
        NA(" ");

        private final String symbol;

        FieldState(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
