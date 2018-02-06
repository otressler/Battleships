package ai;

import ai.guessAI.GuessAI;
import com.ships.Game;

import java.util.ArrayList;

/**
 * Used for AI configuration
 */
public class AI {
    public PlacementAI placementAI;
    public GuessAI guessAI;
    private String name;
    private Game game;

    /**
     * Configures AI
     * @param placementStrategy AIs Placement Strategy
     * @param guessModules AIs Modules
     * @param decisionDelay Delay before guess is published
     * @param name Name for detecting the ai in console output
     */
    public AI(PlacementAI.PositionStrategy placementStrategy, ArrayList<GuessAI.Module> guessModules, int decisionDelay, String name) {
        this.name = name;
        this.placementAI = new PlacementAI(placementStrategy, 10);
        this.guessAI = new GuessAI(guessModules, decisionDelay, name);
    }

    /**
     * Default constructor
     */
    public AI() {
        ArrayList<GuessAI.Module> guessModules = new ArrayList<>();
        this.name = "default";
        guessModules.add(GuessAI.Module.CHECKERBOARD);
        guessModules.add(GuessAI.Module.HIT_REACTION);

        this.placementAI = new PlacementAI(PlacementAI.PositionStrategy.RANDOM, 10);
        this.guessAI = new GuessAI(guessModules, 0, name);
    }

    /**
     * Call after game finishes, resets fields that are not supposed to last longer than one match
     */
    public void nextMatch() {
        placementAI.resetState();
        guessAI.resetState();
    }
}
