package ai;

/**
 * Used for tracking performance. Variables speak for themselves
 */
public class Metrics {
    private int gamesPlayed, overallHits, overallMisses, overallGamesWon, overallGamesLost, minRoundsPlayed, maxRoundsPlayed, totalRoundsPlayed;

    public Metrics(int gamesToBePlayed) {
        gamesPlayed = gamesToBePlayed;

        overallGamesLost = 0;
        overallGamesWon = 0;

        overallHits = 0;
        overallMisses = 0;
    }

    public Metrics(int overallHits, int overallMisses, int overallGamesWon, int overallGamesLost) {
        this.overallHits = overallHits;
        this.overallMisses = overallMisses;
        this.overallGamesWon = overallGamesWon;
        this.overallGamesLost = overallGamesLost;
    }

    private double getHitMissRatio() {
        return ((float) overallHits / (float) gamesPlayed) / ((float) overallMisses / (float) gamesPlayed) * 100f;
    }

    public void updateHitsMisses(AI ai) {
        overallHits += ai.guessAI.getHits();
        overallMisses += ai.guessAI.getMisses();
    }

    private double getWinLoseRatio() {
        return (float) overallGamesWon / (float) overallGamesLost;
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

    public void increaseGamesLost() {
        overallGamesLost++;
    }

    public void increaseGamesWon() {
        overallGamesWon++;
    }

    public void increaseOverallHits() {
        overallHits++;
    }

    public void increaseOverallMisses() {
        overallMisses++;
    }

    public void updateAverageRoundsPlayed(int roundsPlayed) {
        totalRoundsPlayed += roundsPlayed;
        setMaxRoundsPlayed(roundsPlayed);
        setMinRoundsPlayed(roundsPlayed);
    }

    private int getMinRoundsPlayed() {
        return minRoundsPlayed;
    }

    private void setMinRoundsPlayed(int roundsPlayed) {
        if (roundsPlayed < minRoundsPlayed || minRoundsPlayed == 0)
            minRoundsPlayed = roundsPlayed;
    }

    private int getMaxRoundsPlayed() {
        return maxRoundsPlayed;
    }

    private void setMaxRoundsPlayed(int roundsPlayed) {
        if (roundsPlayed > maxRoundsPlayed)
            maxRoundsPlayed = roundsPlayed;
    }

    private float getAvgRoundsPlayed() {
        return totalRoundsPlayed / gamesPlayed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Metrics" + System.lineSeparator());
        sb.append("Win/Lose-Ratio: " + getWinLoseRatio() + System.lineSeparator());
        sb.append("Hit/Miss-Ratio: " + getHitMissRatio() + System.lineSeparator());
        sb.append("Avg Rounds    : " + getAvgRoundsPlayed() + System.lineSeparator());
        sb.append("Max Rounds    : " + getMaxRoundsPlayed() + System.lineSeparator());
        sb.append("Min Rounds    : " + getMinRoundsPlayed() + System.lineSeparator());
        return sb.toString();
    }
}
