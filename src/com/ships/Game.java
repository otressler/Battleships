package com.ships;

import ai.AI;
import ai.GuessAI;
import ai.PlacementAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Game {
    public final ShipType[] shipList = {ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.FRIGATE, ShipType.FRIGATE, ShipType.MINESWEEPER};
    BufferedReader br;
    int player;
    Battleground[] battlegrounds;
    int counter = 0;
    AI ai1;
    AI ai2;

    public Game() {
        player = 0;
        battlegrounds = new Battleground[2];
        battlegrounds[0] = new Battleground(this);
        battlegrounds[1] = new Battleground(this);
        br = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<GuessAI.Module> modulesAI1 = new ArrayList<>();
        ArrayList<GuessAI.Module> modulesAI2 = new ArrayList<>();
        modulesAI1.add(GuessAI.Module.CHECKERBOARD);
        modulesAI1.add(GuessAI.Module.HIT_REACTION);
        ai1 = new AI(this, PlacementAI.PositionStrategy.RANDOM, modulesAI1, 250);
        ai2 = new AI(this, PlacementAI.PositionStrategy.RANDOM, modulesAI2, 250);

        placementPhase(0);
        placementPhase(1);
        System.out.println("Now entering game phase: ");
        while (!winCondition(0) && !winCondition(1)) {
            System.out.println("Round " + counter);
            round();
        }
    }

    private boolean winCondition(int player) {
        for (Ship s : battlegrounds[(player+1) %2].ships) {
            if (!s.sunk)
                return false;
        }
        System.out.println("Finished Game. Winner is "+player);
        return true;
    }

    public void aiGuess(AI ai){
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
        System.out.println(ai.guessAI.toString());
    }

    public void humanGuess(){
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
    }

    public void guess(int player) {
        if (player == 0) {
            System.out.println("Turn player " + player);
            //humanGuess()
            aiGuess(ai1);

            battlegrounds[(player + 1) % 2].printBattleground(Battleground.BattlegroundMode.ENEMY);
            System.out.println();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println();
        } else {
            aiGuess(ai2);
        }
    }

    public void round() {
        guess(0);
        if(winCondition(0))
            return;
        player++;
        guess(1);
        if(winCondition(1))
            return;
        player--;
        counter++;
    }

    public void placementPhase(int player) {
        if (player == 0) {
            /*for (ShipType type : shipList)
                requestHumanPlacement(type);*/
            battlegrounds[player] = ai1.placementAI.placeShips(shipList);
        } else {
            battlegrounds[player] = ai2.placementAI.placeShips(shipList);
        }
    }

    private void requestHumanPlacement(ShipType type) {
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
