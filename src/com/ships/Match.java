package com.ships;

import ai.AI;
import ai.Metrics;

public class Match {

    public Match(int numberOfGames, AI ai1, AI ai2){
        Metrics ai1Metrics = new Metrics();
        Metrics ai2Metrics = new Metrics();
        for (int i = 0; i < numberOfGames; i++) {
            Game g = new Game(ai1, ai2);
            if(g.winner==0){
                ai1Metrics.increaseGamesWon();
                ai2Metrics.increaseGamesLost();
            } else{
                ai2Metrics.increaseGamesWon();
                ai1Metrics.increaseGamesLost();
            }
            ai1.nextMatch();
            ai2.nextMatch();
        }
    }

    public Match(int numberOfGames, AI ai){
        for (int i = 0; i < numberOfGames; i++) {
            new Game(ai);
            ai.nextMatch();
        }
    }
}
