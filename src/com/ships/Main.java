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
        modules1.add(GuessAI.Module.IGNORE_BLOCKED);
        modules1.add(GuessAI.Module.SPACE_ANALYSIS);

        ArrayList<GuessAI.Module> modules2 = new ArrayList<>();
        modules2.add(GuessAI.Module.CHECKERBOARD);
        modules2.add(GuessAI.Module.HIT_REACTION);
        modules2.add(GuessAI.Module.IGNORE_BLOCKED);
        modules2.add(GuessAI.Module.SPACE_ANALYSIS);
        new Match(numberOfGames,
                new AI(PlacementAI.PositionStrategy.MEMORY, modules1, 0, "AI ONE: ")
        );
    }
}