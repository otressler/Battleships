package com.ships;

import ai.AI;
import ai.Metrics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EmptyStackException;

/**
 * Plays a series of games and collects metrics
 */
public class Match {

    /**
     * Play a series of games with AI vs AI
     * @param numberOfGames How many games?
     * @param ai1
     * @param ai2
     * @param verbose Should fields be printed? false increases speed significantly.
     */
    public Match(int numberOfGames, AI ai1, AI ai2, boolean verbose) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Metrics ai1Metrics = new Metrics(numberOfGames);
        Metrics ai2Metrics = new Metrics(numberOfGames);
        int abortedGames = 0;
        for (int i = 0; i < numberOfGames; i++) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Game g = new Game(ai1, ai2, verbose);
            try {
                g.init();
            } catch (EmptyStackException e) {
                ai1.nextMatch();
                ai2.nextMatch();
                i--;
                e.printStackTrace();
                abortedGames++;
                continue;
            }
            if (g.winner == 0) {
                ai1Metrics.increaseGamesWon();
                ai2Metrics.increaseGamesLost();
                ai1.guessAI.updateOpponentStrategy();
            } else {
                ai2Metrics.increaseGamesWon();
                ai1Metrics.increaseGamesLost();
                ai2.guessAI.updateOpponentStrategy();
            }

            ai1Metrics.updateAverageRoundsPlayed(g.getRounds());
            ai2Metrics.updateAverageRoundsPlayed(g.getRounds());

            ai1Metrics.updateHitsMisses(ai1);
            ai2Metrics.updateHitsMisses(ai2);

            ai1.nextMatch();
            ai2.nextMatch();

            System.out.println(ai1.guessAI.getOpponentStrategyAnalyzer().guessPlacementStrategy());
            System.out.println(ai2.guessAI.getOpponentStrategyAnalyzer().guessPlacementStrategy());

            System.out.println("abortedGames = " + abortedGames);
        }
        System.out.println(ai1Metrics.toString());
        System.out.println(ai2Metrics.toString());
    }

    /**
     * Play a series of games against the AI
     * @param numberOfGames How many games?
     * @param ai
     */
    public Match(int numberOfGames, AI ai) {
        for (int i = 0; i < numberOfGames; i++) {
            new Game(ai).init();
            ai.nextMatch();
        }
    }
}
