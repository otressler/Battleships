package com.ships;

import ai.AI;
import ai.PlacementAI;
import ai.guessAI.GuessAI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int numberOfGames = 20;//Integer.parseInt(br.readLine());
        ArrayList<GuessAI.Module> modules1 = new ArrayList<>();
        modules1.add(GuessAI.Module.CHECKERBOARD);
        modules1.add(GuessAI.Module.HIT_REACTION);
        //modules1.add(GuessAI.Module.SPACE_ANALYSIS);


        ArrayList<GuessAI.Module> modules2 = new ArrayList<>();
        modules2.add(GuessAI.Module.CHECKERBOARD);
        modules2.add(GuessAI.Module.HIT_REACTION);
        new Match(numberOfGames,
                new AI(PlacementAI.PositionStrategy.RANDOM, modules1, 0),
                new AI(PlacementAI.PositionStrategy.RANDOM, modules2, 0)
        );

    }
}