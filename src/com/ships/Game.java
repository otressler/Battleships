package com.ships;

import ai.AI;
import ai.guessAI.GuessAI;
import ai.PlacementAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Game {
    public final ShipType[] shipList = {ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.FRIGATE, ShipType.FRIGATE, ShipType.MINESWEEPER};
    BufferedReader br;
    boolean humanEnemy = false;
    int player;
    int winner;
    Battleground[] battlegrounds;
    int counter = 0;
    AI ai1;
    AI ai2;

    public Game(AI ai){
        humanEnemy = true;
        player = 0;
        battlegrounds = new Battleground[2];
        battlegrounds[0] = new Battleground(this);
        battlegrounds[1] = new Battleground(this);
        this.ai2 = ai;

        placementPhase(0);
        placementPhase(1);
        System.out.println("Now entering game phase: ");
        while (!winCondition(0) && !winCondition(1)) {
            System.out.println("Round " + counter);
            round();
        }
    }

    public Game(AI ai1, AI ai2) {
        player = 0;
        battlegrounds = new Battleground[2];
        battlegrounds[0] = new Battleground(this);
        battlegrounds[1] = new Battleground(this);
        this.ai1 = ai1;
        this.ai2 = ai2;

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
        winner = player;
        return true;
    }

    public void aiGuess(AI activeAI, AI passiveAI){
        Coordinate aiGuess = activeAI.guessAI.getNextGuess();
        System.out.println("AI"+player+" guessed " + Util.parseCharacterFromInt(aiGuess.x) + "" + aiGuess.y);
        passiveAI.placementAI.updateGuessMemory(aiGuess);
        if (battlegrounds[(player + 1) % 2].hitEvaluation(aiGuess)) {
            System.out.println("AI"+player+" HIT");
            activeAI.guessAI.updatePlacementMemory(aiGuess);
            if (battlegrounds[(player + 1) % 2].battleground[aiGuess.y][aiGuess.x].equals(Battleground.FieldState.SUNK))
                activeAI.guessAI.onSunk(battlegrounds[(player + 1) % 2].findShipByCoordinate(aiGuess));
            else
                activeAI.guessAI.onHit(aiGuess.x, aiGuess.y);
            guess(1);
        } else {
            activeAI.guessAI.onMiss(aiGuess.x, aiGuess.y);
        }
    }

    public void humanGuess(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String coordinates = br.readLine();
            int x = Util.parseXPosition(coordinates);
            int y = Util.parseYPosition(coordinates);
            Coordinate guess = new Coordinate(x, y);
            ai1.placementAI.updateGuessMemory(guess);
            if (battlegrounds[(player + 1) % 2].hitEvaluation(guess))
                guess(player);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void guess(int player) {
        if (player == 0 && !humanEnemy) {
            System.out.println("#############################  Turn player " + player+"  ###########################");
            printBattlegrounds(Battleground.BattlegroundMode.ENEMY, ai1);
            System.out.println();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println();
            aiGuess(ai1, ai2);
        }

        else if(player == 0 && humanEnemy){
            System.out.println("#############################  Turn player " + player+"  ###########################");
            printBattlegrounds(Battleground.BattlegroundMode.ENEMY, ai1);
            System.out.println();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println();
            humanGuess();
        } else {
            aiGuess(ai2, ai1);
            printBattlegrounds(Battleground.BattlegroundMode.ENEMY, ai2);
            System.out.println();
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println();
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
        if (player == 0 && !humanEnemy) {
            battlegrounds[player] = ai1.placementAI.placeShips(shipList);
        } else if(player == 0 && humanEnemy){
            for (ShipType type : shipList)
                requestHumanPlacement(type);
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
        //battlegrounds[player].printBattleground(Battleground.BattlegroundMode.PLACEMENT);
        printBattlegrounds(Battleground.BattlegroundMode.PLACEMENT, ai1);
    }

    public void printBattlegrounds(Battleground.BattlegroundMode mode, AI activeAI) {

        Battleground leftPlayer = battlegrounds[0];
        Battleground rightPlayer = battlegrounds[1];

        if (mode.equals(Battleground.BattlegroundMode.PLACEMENT)) {
            System.out.println("Placement");
            System.out.println();
            System.out.println("    A  B  C  D  E  F  G  H  I  J");
            for (int y = 0; y < rightPlayer.battleground.length; y++) {
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < rightPlayer.battleground[y].length; x++) {
                    System.out.print("[" + rightPlayer.battleground[y][x].getSymbol() + "]");
                }
                System.out.println();
            }
        } else if (mode.equals(Battleground.BattlegroundMode.ENEMY)) {
            System.out.print("Your guesses on the enemies board      ");
            System.out.print("Enemies guesses on your board          ");
            System.out.print("        Active AI map                          ");
            System.out.println();
            System.out.print("    A  B  C  D  E  F  G  H  I  J       ");
            System.out.print("    A  B  C  D  E  F  G  H  I  J       ");
            System.out.print("            A  B  C  D  E  F  G  H  I  J       ");
            System.out.println();
            for (int y = 0; y < leftPlayer.battleground.length; y++) {
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < rightPlayer.battleground[y].length; x++) {
                    // TODO: uncomment this part
                    if (!rightPlayer.battleground[y][x].equals(Battleground.FieldState.SHIP) && !rightPlayer.battleground[y][x].equals(Battleground.FieldState.BLOCKED))
                        System.out.print("[" + rightPlayer.battleground[y][x].getSymbol() + "]");
                    else
                        System.out.print("[ ]");
                }


                System.out.print("      ");
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < rightPlayer.battleground[y].length; x++) {
                    if (!getBattlegrounds()[getCurrentPlayer()].battleground[y][x].equals(Battleground.FieldState.BLOCKED))
                        System.out.print("[" + getBattlegrounds()[getCurrentPlayer()].battleground[y][x].getSymbol() + "]");
                    else
                        System.out.print("[ ]");
                }

                System.out.print("      ||      ");
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < activeAI.guessAI.getAiMap().battleground[y].length; x++) {
                    if (/*!battleground[y][x].equals(FieldState.SHIP) && !aiMap.battleground[y][x].equals(Battleground.FieldState.BLOCKED)*/ true)
                        System.out.print("[" + activeAI.guessAI.getAiMap().battleground[y][x].getSymbol() + "]");
                    else
                        System.out.print("[ ]");
                }


                System.out.println();


            }
        }
    }

    public int getCurrentPlayer() {
        return player;
    }

    public Battleground[] getBattlegrounds() {
        return battlegrounds;
    }
}
