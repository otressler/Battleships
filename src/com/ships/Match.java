package com.ships;

import ai.AI;

public class Match {

    public Match(int numberOfGames, AI ai1, AI ai2){
        for (int i = 0; i < numberOfGames; i++) {
            new Game(ai1, ai2);
        }
    }

    public Match(int numberOfGames, AI ai){
        for (int i = 0; i < numberOfGames; i++) {
            new Game(ai);
        }
    }
}
