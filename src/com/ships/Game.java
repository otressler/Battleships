package com.ships;

import ai.AI;
import ai.PlacementAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Game {
    public final ShipType[] shipList = {ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.FRIGATE, ShipType.FRIGATE, ShipType.MINESWEEPER};
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
        for (int i = 0; i < 2; i++) {
            for (Ship s : battlegrounds[i].ships) {
                if (!s.sunk)
                    return false;
            }
            return true;
        }
        return true;
    }

    public void guess(int player) {
        if (player == 0) {
            System.out.println("Turn player " + player);
            battlegrounds[(player + 1) % 2].printBattleground(Battleground.BattlegroundMode.ENEMY);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            try {
                String coordinates = br.readLine();
                int x = Util.parseXPosition(coordinates);
                int y = Util.parseYPosition(coordinates);
                if (battlegrounds[(player + 1) % 2].hitEvaluation(new Coordinate(x, y)))
                    guess(player);
            } catch (IOException io) {
                io.printStackTrace();
            }
            battlegrounds[(player + 1) % 2].printBattleground(Battleground.BattlegroundMode.ENEMY);
            System.out.println();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println();
        } else {
            Coordinate aiGuess = ai.guessAI.getNextGuess();
            System.out.println("AI guessed " + Util.parseCharacterFromInt(aiGuess.x) + "" + aiGuess.y);
            if (battlegrounds[(player + 1) % 2].hitEvaluation(aiGuess)) {
                System.out.println("AI HIT");
                if (battlegrounds[(player + 1) % 2].battleground[aiGuess.y][aiGuess.x].equals(Battleground.FieldState.SUNK))
                    ai.guessAI.onSunk(battlegrounds[(player + 1) % 2].findShipByCoordinate(aiGuess));
                else
                    ai.guessAI.onHit(aiGuess.x, aiGuess.y);
                guess(1);
            } else {
                ai.guessAI.onMiss(aiGuess.x, aiGuess.y);
            }
        }
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
            //battlegrounds[player] = new PlacementAI(this, PlacementAI.PositionStrategy.RANDOM).placeShips(shipList);
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
