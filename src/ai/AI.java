package ai;

import ai.guessAI.GuessAI;
import com.ships.Game;

import java.util.ArrayList;

public class AI {
    private String name;
    public PlacementAI placementAI;
    public GuessAI guessAI;

    private Game game;

    public AI(PlacementAI.PositionStrategy placementStrategy, ArrayList<GuessAI.Module> guessModules, int decisionDelay, String name) {
        this.name = name;
        this.placementAI = new PlacementAI(placementStrategy, 10);
        this.guessAI = new GuessAI(guessModules, decisionDelay,name);
    }

    //default AI
    public AI(Game game) {
        ArrayList<GuessAI.Module> guessModules = new ArrayList<>();
        this.name = "default";
        guessModules.add(GuessAI.Module.CHECKERBOARD);
        guessModules.add(GuessAI.Module.HIT_REACTION);

        this.placementAI = new PlacementAI(PlacementAI.PositionStrategy.RANDOM, 10);
        this.guessAI = new GuessAI(guessModules, 0, name);
    }

    public void nextMatch() {
        placementAI.resetState();
        guessAI.resetState();
    }
}
