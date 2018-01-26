package ai;

import java.util.ArrayList;

public class Metrics {
    private double hitMissRatio, winLoseRation;
    private int gamesPlayed, overallHits, overallMisses, overallGamesWon, overallGamesLost;

    public Metrics(int gamesToBePlayed){
        gamesPlayed = gamesToBePlayed;

        hitMissRatio = 0;
        winLoseRation= 0;

        overallGamesLost = 0;
        overallGamesWon  = 0;

        overallHits  = 0;
        overallMisses= 0;
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
        return ((float)overallHits/(float)gamesPlayed)/((float)overallMisses/(float)gamesPlayed)*100f;
    }

    public void updateHitsMisses(AI ai){
        overallHits+=ai.guessAI.getHits();
        overallMisses+=ai.guessAI.getMisses();
    }

    public double getWinLoseRation() {
        return (float)overallGamesWon/(float)overallGamesLost;
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
