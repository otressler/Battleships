package com.ships;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Game {

    int player;
    Battleground[] battlegrounds;
    int counter = 0;

    public Game() {
        player = 0;
        battlegrounds = new Battleground[2];
        battlegrounds[0] = new Battleground();
        battlegrounds[1] = new Battleground();
        placementPhase(nextPlayer());
        placementPhase(nextPlayer());
        battlegrounds[0].clearBlocked();
        battlegrounds[1].clearBlocked();
        System.out.println("Now entering game phase: ");
        while (!winCondition()) {
            System.out.println("Round " + counter);
            round();
        }
    }

    private boolean winCondition() {
        for (Battleground b : battlegrounds) {
            for (Ship s : b.ships) {
                if (!s.sunk)
                    return false;
            }
            return true;
        }
        return true;
    }

    public void guess(int player) {
        System.out.println("Turn player " + player);
        battlegrounds[(player + 1) % 2].printBattleground(Battleground.BattlegroundMode.ENEMY);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String coordinates = br.readLine();
            int x = Util.parseXPosition(coordinates);
            int y = Util.parseYPosition(coordinates);

            boolean hit = false;
            for (Ship s : battlegrounds[(player + 1) % 2].ships) {
                if (s.hitScan(x, y)) {
                    battlegrounds[(player + 1) % 2].battleground[y][x] = Battleground.FieldState.HIT;
                    if (s.sunk) {
                        if (s.verticalRotation) {
                            for (int i = 0; i < s.length; i++) {
                                battlegrounds[(player + 1) % 2].battleground[s.yPos + i][s.xPos] = Battleground.FieldState.SUNK;
                            }
                        }
                    }
                    hit = true;
                }
            }
            if (!hit) {
                battlegrounds[(player + 1) % 2].battleground[y][x] = Battleground.FieldState.MISS;
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        battlegrounds[(player + 1) % 2].printBattleground(Battleground.BattlegroundMode.ENEMY);
        battlegrounds[player].printBattleground(Battleground.BattlegroundMode.FRIENDLY);

    }

    public void round() {
        guess(nextPlayer());
        guess(nextPlayer());
        counter++;
    }

    public void placementPhase(int player) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String coordinates = "";
        try {
            System.out.println(player + "You may now place your battleship. Please enter the coordinates:");
            do {
                coordinates = br.readLine();
            } while (
                    !battlegrounds[player].placeShip(
                            new Ship(
                                    ShipType.BATTLESHIP,
                                    Util.parseXPosition(coordinates),
                                    Util.parseYPosition(coordinates),
                                    Util.parseOrientation(coordinates)
                            )
                    ));
            battlegrounds[player].printBattleground(Battleground.BattlegroundMode.PLACEMENT);

            System.out.print(player + "You may now place your cruiser. Please enter the coordinates:");
            do {
                coordinates = br.readLine();
            } while (
                    !battlegrounds[player].placeShip(
                            new Ship(
                                    ShipType.CRUISER,
                                    Util.parseXPosition(coordinates),
                                    Util.parseYPosition(coordinates),
                                    Util.parseOrientation(coordinates)
                            )
                    ));
            battlegrounds[player].printBattleground(Battleground.BattlegroundMode.PLACEMENT);

            System.out.print(player + "You may now place your frigate. Please enter the coordinates:");
            do {
                coordinates = br.readLine();
            } while (
                    !battlegrounds[player].placeShip(
                            new Ship(
                                    ShipType.FRIGATE,
                                    Util.parseXPosition(coordinates),
                                    Util.parseYPosition(coordinates),
                                    Util.parseOrientation(coordinates)
                            )
                    ));
            battlegrounds[player].printBattleground(Battleground.BattlegroundMode.PLACEMENT);

            System.out.print(player + "You may now place your second frigate. Please enter the coordinates;");
            do {
                coordinates = br.readLine();
            } while (
                    !battlegrounds[player].placeShip(
                            new Ship(
                                    ShipType.FRIGATE,
                                    Util.parseXPosition(coordinates),
                                    Util.parseYPosition(coordinates),
                                    Util.parseOrientation(coordinates)
                            )
                    ));
            battlegrounds[player].printBattleground(Battleground.BattlegroundMode.PLACEMENT);

            System.out.print(player + "You may now place your minesweeper. Please enter the coordinates:");
            do {
                coordinates = br.readLine();
            } while (
                    !battlegrounds[player].placeShip(
                            new Ship(
                                    ShipType.MINESWEEPER,
                                    Util.parseXPosition(coordinates),
                                    Util.parseYPosition(coordinates),
                                    Util.parseOrientation(coordinates)
                            )
                    ));
            battlegrounds[player].printBattleground(Battleground.BattlegroundMode.PLACEMENT);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private int nextPlayer() {
        player = (player + 1) % 2;
        return player;
    }
}
