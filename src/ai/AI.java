package ai;

import ai.guessAI.GuessAI;
import com.ships.Game;

import java.util.ArrayList;

public class AI {
    public PlacementAI placementAI;
    public GuessAI guessAI;

    private Game game;

    public AI(PlacementAI.PositionStrategy placementStrategy, ArrayList<GuessAI.Module> guessModules, int decisionDelay) {
        this.placementAI = new PlacementAI(placementStrategy, 10);
        this.guessAI = new GuessAI(guessModules, decisionDelay);
    }

    //default AI
    public AI(Game game) {
        ArrayList<GuessAI.Module> guessModules = new ArrayList<>();
        guessModules.add(GuessAI.Module.CHECKERBOARD);
        guessModules.add(GuessAI.Module.HIT_REACTION);

        this.placementAI = new PlacementAI(PlacementAI.PositionStrategy.RANDOM, 10);
        this.guessAI = new GuessAI(guessModules, 0);
    }

    public void nextMatch() {
        placementAI.resetState();
        guessAI.resetState();
    }
}
