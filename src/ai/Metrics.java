package ai;

import java.util.ArrayList;

public class Metrics {
    private double hitMissRatio, winLoseRation;
    private int overallHits, overallMisses, overallGamesWon, overallGamesLost;

    public Metrics(){
        hitMissRatio = 0;
        winLoseRation= 0;

        overallGamesLost = 0;
        overallGamesWon  = 0;

        overallHits  = 0;
        overallMisses= 0;
    }

    public void updateMetrics(AI ai){

    }

    public Metrics(int overallHits, int overallMisses, int overallGamesWon, int overallGamesLost) {
        this.hitMissRatio = overallHits/overallMisses;
        this.overallHits = overallHits;
        this.overallMisses = overallMisses;
        this.winLoseRation = overallGamesWon/overallGamesLost;
        this.overallGamesWon = overallGamesWon;
        this.overallGamesLost = overallGamesLost;
    }

    public double getHitMissRatio() {
        return overallHits/overallMisses;
    }

    public double getWinLoseRation() {
        return overallGamesWon/overallGamesLost;
    }

    public int getOverallHits() {
        return overallHits;
    }

    public int getOverallMisses() {
        return overallMisses;
    }

    public int getOverallGamesWon() {
        return overallGamesWon;
    }

    public int getOverallGamesLost() {
        return overallGamesLost;
    }

    public void increaseGamesLost(){
        overallGamesLost++;
    }

    public void increaseGamesWon(){
        overallGamesWon++;
    }

    public void increaseOverallHits(){
        overallHits++;
    }

    public void increaseOverallMisses(){
        overallMisses++;
    }
}
