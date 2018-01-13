package com.ships;

import ai.AI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Game {
    final ShipType[] shipList = {ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.FRIGATE, ShipType.FRIGATE, ShipType.MINESWEEPER};
    BufferedReader br;
    int player;
    Battleground[] battlegrounds;
    int counter = 0;
    AI ai;

    public Game() {
        player = 0;
        battlegrounds = new Battleground[2];
        battlegrounds[0] = new Battleground(this);
        battlegrounds[1] = new Battleground(this);
        br = new BufferedReader(new InputStreamReader(System.in));
        ai = new AI(this);

        placementPhase(0);
        placementPhase(1);
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
        System.out.println();
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println();
    }

    public void round() {
        guess(0);
        player++;
        guess(1);
        player--;
        counter++;
    }

    public void placementPhase(int player) {
        if (player == 0) {
            for (ShipType type : shipList)
                requestPlacement(type);
        } else {
            battlegrounds[player] = ai.placementAI.placeShips(shipList);
        }
    }

    private void requestPlacement(ShipType type) {
        System.out.println("@Player " + player + ": You may now place your " + type.getClassName() + ". Please enter the coordinates:");
        br = new BufferedReader(new InputStreamReader(System.in));
        String coordinates = "";
        do {
            try {
                coordinates = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (
                !battlegrounds[player].placeShip(
                        new Ship(
                                type,
                                Util.parseXPosition(coordinates),
                                Util.parseYPosition(coordinates),
                                Util.parseOrientation(coordinates)
                        )
                ));
        battlegrounds[player].printBattleground(Battleground.BattlegroundMode.PLACEMENT);
    }

    public int getCurrentPlayer() {
        return player;
    }

    public Battleground[] getBattlegrounds() {
        return battlegrounds;
    }
}
