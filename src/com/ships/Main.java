package com.ships;

import ai.AI;
import ai.PlacementAI;
import ai.guessAI.GuessAI;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        int numberOfGames = 100;//Integer.parseInt(br.readLine());
        ArrayList<GuessAI.Module> modules1 = new ArrayList<>();
        modules1.add(GuessAI.Module.CHECKERBOARD);
        modules1.add(GuessAI.Module.HIT_REACTION);


        ArrayList<GuessAI.Module> modules2 = new ArrayList<>();
        new Match(numberOfGames,
                new AI(PlacementAI.PositionStrategy.DENSE, modules1, 0, "AI ONE: "),
                new AI(PlacementAI.PositionStrategy.SPARSE, modules2, 0, "AI TWO: "),
                false
        );
    }
}