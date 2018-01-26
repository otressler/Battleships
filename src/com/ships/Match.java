package com.ships;

import ai.AI;
import ai.Metrics;

import java.util.EmptyStackException;

public class Match {

    public Match(int numberOfGames, AI ai1, AI ai2){
        Metrics ai1Metrics = new Metrics(numberOfGames);
        Metrics ai2Metrics = new Metrics(numberOfGames);
        for (int i = 0; i < numberOfGames; i++) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Game g = new Game(ai1, ai2);
            try{
                g.init();
            }catch (EmptyStackException e){
                ai1.nextMatch();
                ai2.nextMatch();
                continue;
            }
            if(g.winner==0){
                ai1Metrics.increaseGamesWon();
                ai2Metrics.increaseGamesLost();
            } else{
                ai2Metrics.increaseGamesWon();
                ai1Metrics.increaseGamesLost();
            }

            ai1Metrics.updateAverageRoundsPlayed(g.getRounds());
            ai2Metrics.updateAverageRoundsPlayed(g.getRounds());

            ai1Metrics.updateHitsMisses(ai1);
            ai2Metrics.updateHitsMisses(ai2);

            ai1.nextMatch();
            ai2.nextMatch();
        }
        System.out.println(ai1Metrics.toString());
        System.out.println(ai2Metrics.toString());
    }

    public Match(int numberOfGames, AI ai){
        for (int i = 0; i < numberOfGames; i++) {
            new Game(ai);
            ai.nextMatch();
        }
    }
}
