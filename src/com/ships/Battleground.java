package com.ships;

import java.util.ArrayList;

public class Battleground {
    //      y x
    public FieldState[][] battleground;
    public ArrayList<Ship> ships;
    Game game;

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

    public void printBattleground(BattlegroundMode mode) {
        if (mode.equals(BattlegroundMode.PLACEMENT)) {
            System.out.println("Placement");
            System.out.println();
            System.out.println("    A  B  C  D  E  F  G  H  I  J");
            for (int y = 0; y < battleground.length; y++) {
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < battleground[y].length; x++) {
                    System.out.print("[" + battleground[y][x].getSymbol() + "]");
                }
                System.out.println();
            }
        } else if (mode.equals(BattlegroundMode.ENEMY)) {
            System.out.print("Your guesses on the enemies board      ");
            System.out.print("Enemies guesses on your board          ");
            System.out.println();
            System.out.print("    A  B  C  D  E  F  G  H  I  J       ");
            System.out.print("    A  B  C  D  E  F  G  H  I  J       ");
            System.out.println();
            for (int y = 0; y < battleground.length; y++) {
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < battleground[y].length; x++) {
                    // TODO: uncomment this part
                    if (!battleground[y][x].equals(FieldState.SHIP) && !battleground[y][x].equals(FieldState.BLOCKED))
                        System.out.print("[" + battleground[y][x].getSymbol() + "]");
                    else
                        System.out.print("[ ]");
                }


                System.out.print("      ");
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < battleground[y].length; x++) {
                    if (!game.getBattlegrounds()[game.getCurrentPlayer()].battleground[y][x].equals(FieldState.BLOCKED))
                        System.out.print("[" + game.getBattlegrounds()[game.getCurrentPlayer()].battleground[y][x].getSymbol() + "]");
                    else
                        System.out.print("[ ]");
                }
                System.out.println();
            }
        }
    }

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

    public Ship findShipByCoordinate(Coordinate coordinate){
        for(Ship s : ships){
            for(Coordinate c : s.getCoordinates()){
                System.out.println(c.x+" "+c.y+"|"+coordinate.x+" "+coordinate.y);
                if(c.equals(coordinate)){
                    return s;
                }
            }
        }
        return null;
    }

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
                    System.out.println(ship.xPos + " " + (ship.yPos + i) + " blocked");
                    return true;
                }
            }
        } else {
            for (int i = 0; i < ship.length; i++) {
                if (battleground[ship.yPos][ship.xPos + i].equals(FieldState.BLOCKED) || battleground[ship.yPos][ship.xPos + i].equals(FieldState.SHIP)) {
                    System.out.println(ship.xPos + " " + (ship.yPos + i) + " blocked");
                    return true;
                }
            }
        }
        return false;
    }

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

    enum BattlegroundMode {
        ENEMY, PLACEMENT, FRIENDLY
    }

    public enum FieldState {
        IGNORE("I"),
        POTENTIAL("P"),
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
