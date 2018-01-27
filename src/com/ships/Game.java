package com.ships;

import ai.AI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EmptyStackException;

public class Game {
    private final ShipType[] shipList = {ShipType.BATTLESHIP, ShipType.CRUISER, ShipType.FRIGATE, ShipType.FRIGATE, ShipType.MINESWEEPER};
    int winner;
    private BufferedReader br;
    private boolean humanEnemy = false;
    private int player;
    private Battleground[] battlegrounds;
    private int counter = 0;
    private AI ai1;
    private AI ai2;

    public Game(AI ai) {
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
    }

    public void init() throws EmptyStackException {
        placementPhase(0);
        placementPhase(1);
        System.out.println("Now entering game phase: ");
        while (!winCondition(0) && !winCondition(1)) {
            System.out.println("Round " + counter);
            round();
        }
    }

    private boolean winCondition(int player) {
        if (player == 1) {
            for (Ship s : battlegrounds[0].ships) {
                if (!s.sunk)
                    return false;
            }
        } else {
            for (Ship s : battlegrounds[1].ships) {
                if (!s.sunk)
                    return false;
            }
        }
        System.out.println("Finished Game. Winner is " + player);
        winner = player;
        return true;
    }


    private void aiGuess() {
        AI activeAI;
        AI passiveAI;
        if (humanEnemy) {
            activeAI = getCurrentPlayer() == 0 ? null : ai2;
            passiveAI = getCurrentPlayer() == 0 ? ai2 : null;
        } else {
            activeAI = player == 0 ? ai1 : ai2;
            passiveAI = player == 0 ? ai2 : ai1;
        }
        System.out.println("#############################  Turn player " + player + "  ###########################");
        printBattlegrounds(Battleground.BattlegroundMode.ENEMY);
        System.out.println();

        //TODO: Fix active AI wrong
        Coordinate aiGuess = activeAI.guessAI.getNextGuess();
        System.out.println("AI" + player + " guessed " + Util.parseCharacterFromInt(aiGuess.x) + "" + aiGuess.y);
        if (passiveAI != null) {
            passiveAI.placementAI.updateGuessMemory(aiGuess);
        }
        if (battlegrounds[(player + 1) % 2].hitEvaluation(aiGuess)) {
            System.out.println("AI" + player + " HIT");
            activeAI.guessAI.updatePlacementMemory(aiGuess);
            if (battlegrounds[(player + 1) % 2].battleground[aiGuess.y][aiGuess.x].equals(Battleground.FieldState.SUNK))
                activeAI.guessAI.onSunk(battlegrounds[(player + 1) % 2].findShipByCoordinate(aiGuess));
            else
                activeAI.guessAI.onHit(aiGuess.x, aiGuess.y);
            guess(getCurrentPlayer());
        } else {
            activeAI.guessAI.onMiss(aiGuess.x, aiGuess.y);
        }
    }

    private void humanGuess() {
        System.out.println("#############################  Turn player " + player + "  ###########################");
        printBattlegrounds(Battleground.BattlegroundMode.ENEMY);
        System.out.println();
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String coordinates = br.readLine();
            int x = Util.parseXPosition(coordinates);
            int y = Util.parseYPosition(coordinates);
            Coordinate guess = new Coordinate(x, y);
            ai2.placementAI.updateGuessMemory(guess);
            if (battlegrounds[(player + 1) % 2].hitEvaluation(guess))
                guess(player);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void guess(int player) {
        if (!winCondition(player)) {
            if (player == 0 && !humanEnemy) {
                aiGuess();
            } else if (player == 0 && humanEnemy) {
                humanGuess();
            } else {
                aiGuess();
            }
        }
    }

    private void round() {
        guess(0);
        if (winCondition(0))
            return;
        player++;
        guess(1);
        if (winCondition(1))
            return;
        player--;
        counter++;
    }

    public void placementPhase(int player) {
        if (player == 0 && !humanEnemy) {
            battlegrounds[player] = ai1.placementAI.placeShips(shipList);
        } else if (player == 0 && humanEnemy) {
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
        printBattlegrounds(Battleground.BattlegroundMode.PLACEMENT);
    }

    private void printBattlegrounds(Battleground.BattlegroundMode mode) {

        AI activeAI;
        if (humanEnemy) {
            activeAI = ai2;
        } else {
            activeAI = getCurrentPlayer() == 0 ? ai1 : ai2;
        }
        // rightPlayer : player
        // leftPlayer : enemy

        if (mode.equals(Battleground.BattlegroundMode.PLACEMENT)) {
            System.out.println("Placement");
            System.out.println();
            System.out.println("    A  B  C  D  E  F  G  H  I  J");
            for (int y = 0; y < battlegrounds[getCurrentPlayer()].battleground.length; y++) {
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < battlegrounds[getCurrentPlayer()].battleground[y].length; x++) {
                    System.out.print("[" + battlegrounds[getCurrentPlayer()].battleground[y][x].getSymbol() + "]");
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
            for (int y = 0; y < battlegrounds[getCurrentEnemy()].battleground.length; y++) {
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < battlegrounds[getCurrentEnemy()].battleground[y].length; x++) {
                    // TODO: uncomment this part
                    if (!battlegrounds[getCurrentEnemy()].battleground[y][x].equals(Battleground.FieldState.SHIP) && !battlegrounds[getCurrentEnemy()].battleground[y][x].equals(Battleground.FieldState.BLOCKED))
                        System.out.print("[" + battlegrounds[getCurrentEnemy()].battleground[y][x].getSymbol() + "]");
                    else
                        System.out.print("[ ]");
                }


                System.out.print("      ");
                System.out.print(Util.padRight(Integer.toString(y), 3));
                for (int x = 0; x < battlegrounds[getCurrentPlayer()].battleground[y].length; x++) {
                    if (!getBattlegrounds()[getCurrentPlayer()].battleground[y][x].equals(Battleground.FieldState.BLOCKED))
                        System.out.print("[" + getBattlegrounds()[getCurrentPlayer()].battleground[y][x].getSymbol() + "]");
                    else
                        System.out.print("[ ]");
                }

                System.out.print("      ||      ");
                System.out.print(Util.padRight(Integer.toString(y), 3));
                if (activeAI != null) {
                    for (int x = 0; x < activeAI.guessAI.getAiMap().battleground[y].length; x++) {
                        System.out.print(" " + activeAI.guessAI.getAiMap().battleground[y][x].getSymbol() + " ");
                    }
                }
                System.out.println();
            }
        }

    }

    private int getCurrentPlayer() {
        return player;
    }

    public int getRounds() {
        return counter;
    }

    private int getCurrentEnemy() {
        return (player + 1) % 2;
    }

    private Battleground[] getBattlegrounds() {
        return battlegrounds;
    }
}
