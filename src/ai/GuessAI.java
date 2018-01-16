package ai;

import com.ships.Battleground;
import com.ships.Coordinate;
import com.ships.Game;

import java.util.Collections;
import java.util.Stack;

public class GuessAI {
    Battleground aiMap;
    Stack<Coordinate> nextGuesses;
    AIMode state = AIMode.SCOUT;

    public GuessAI(Game game) {
        this.aiMap = new Battleground(game);
        nextGuesses = new Stack<>();
        initCheckerboard();

    }

    public Coordinate getNextGuess() {
        return nextGuesses.pop();
    }

    public void initCheckerboard() {
        for (int y = 0; y < 10; y++) {
            for (int x = y % 2; x < 10; x += 2) {
                nextGuesses.push(new Coordinate(x, y));
                aiMap.battleground[y][x] = Battleground.FieldState.IGNORE;
            }
        }
        Collections.shuffle(nextGuesses);
    }

    public void onHit() {
        state = AIMode.ATTACK;
        // Push coordinates next to hit on stack
        // Enter AttackMode
        // After hitting ship for the second time and !sunk
        // set orientation
        // calculate max enemy shiplength
        // try next field depending on orientation
        // on first miss try other direction if ship has not been sunk
        // fire as long as hit fields < max enemy shiplength || ship sunk
        // Mark everything around sunk ship as IGNORE
        // Remove ignored fields from checkerboard stack
    }

    public void onMiss() {

    }

    enum AIMode {
        SCOUT, ATTACK
    }
}
