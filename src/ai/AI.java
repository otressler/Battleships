package ai;

import com.ships.Game;

import java.util.ArrayList;

public class AI {
    public PlacementAI placementAI;
    public GuessAI guessAI;
    private Game game;

    public AI(Game game) {
        this.game = game;

        ArrayList<GuessAI.Module> guessModules = new ArrayList<>();
        guessModules.add(GuessAI.Module.CHECKERBOARD);
        guessModules.add(GuessAI.Module.HIT_REACTION);

        this.placementAI = new PlacementAI(game, PlacementAI.PositionStrategy.RANDOM);
        this.guessAI = new GuessAI(game, guessModules);
    }
}
