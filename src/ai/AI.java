package ai;

import com.ships.Game;

public class AI {
    public PlacementAI placementAI;
    public GuessAI guessAI;
    private Game game;

    public AI(Game game) {
        this.game = game;
        this.placementAI = new PlacementAI(game);
        this.guessAI = new GuessAI(game);
    }
}
