package ai;

import ai.guessAI.GuessAI;
import com.ships.Game;

import java.util.ArrayList;

public class AI {
    public PlacementAI placementAI;
    public GuessAI guessAI;

    private Game game;

    public AI(PlacementAI.PositionStrategy placementStrategy, ArrayList<GuessAI.Module> guessModules, int decisionDelay) {
        this.placementAI = new PlacementAI(placementStrategy);
        this.guessAI = new GuessAI(guessModules, decisionDelay);
    }

    public void nextMatch(){
        placementAI.resetState();
        guessAI.resetState();
    }

    //default AI
    public AI(Game game) {
        ArrayList<GuessAI.Module> guessModules = new ArrayList<>();
        guessModules.add(GuessAI.Module.CHECKERBOARD);
        guessModules.add(GuessAI.Module.HIT_REACTION);

        this.placementAI = new PlacementAI(PlacementAI.PositionStrategy.RANDOM);
        this.guessAI = new GuessAI(guessModules, 0);
    }
}
