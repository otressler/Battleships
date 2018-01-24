package ai;

import com.ships.Battleground;
import com.ships.Coordinate;
import com.ships.Game;

import java.util.*;

// TODO:

public class GuessAI {
    ArrayList<Module> modules;

    Battleground aiMap;
    Stack<Coordinate> nextGuesses;
    AIMode state = AIMode.SCOUT;


    int maxEnemyShipLength, minEnemyShipLength;

    // The following fields are used to determine the orientation of the enemy ship during ATTACK_ADJACENT
    Coordinate initialHit;
    Direction currentDirection;
    boolean sunk;

    int hits = 0;
    int miss = 0;

    public void hitDuringScout(int x, int y) {
        stateChange(AIMode.ATTACK_ADJACENT);
        // TODO: Check for irrelevant fields in switch statement
        for (int i = 0; i < 4; i++) {
            Integer[] options = {0, 1, 2, 3};
            List<Integer> choices = Arrays.asList(options);
            Collections.shuffle(choices);
            initialHit = new Coordinate(x, y);
            for (Integer choice : choices) {
                switch (choice) {
                    case 0:
                        if (x + 1 < 10) {
                            nextGuesses.push(new Coordinate(x + 1, y));
                        }
                        break;
                    case 1:
                        if (x - 1 >= 0) {
                            nextGuesses.push(new Coordinate(x - 1, y));
                        }
                        break;
                    case 2:
                        if (y + 1 < 10) {
                            nextGuesses.push(new Coordinate(x, y + 1));
                        }
                        break;
                    case 3:
                        if (y - 1 >= 0) {
                            nextGuesses.push(new Coordinate(x, y - 1));
                        }
                        break;
                    default:
                        System.out.println("Illegal number generated");
                }
            }
        }
    }

    public void missDuringScout(int x, int y) {
        aiMap.battleground[y][x] = Battleground.FieldState.IGNORE;
    }

    /**
     * Determine the attack vector.
     * If the hit after the initial hit is shifted on the y-axis, the ship is placed vertically, else it is placed
     * horizontally. If the ship that has been hit was placed at a border (e.g. X = 0, Y = 4, orient. = horizontal)
     * this is taken into account for the attack vector.
     * A call of this method sets the AI into ATTACK mode.
     *
     * @param x X position of the follow up hit
     * @param y Y position of the follow up hit
     */
    public void hitDuringAttackAdjacent(int x, int y) {
        // Hit right from initialHit
        if (x > initialHit.getX()) {
            currentDirection = Direction.RIGHT;

            // Remove adjacentFieldsFromStack
            for (int i = 0; i < 3 - miss; i++)
                nextGuesses.pop();

            // Check if already at border
            if (x + 1 > 9) {
                currentDirection = Direction.LEFT;
                nextGuesses.push(new Coordinate(initialHit.getX() - 1, initialHit.getY()));
            } else
                // Try fields to the right from current hit
                nextGuesses.push(new Coordinate(x + 1, y));

        }
        // Hit left from initialHit
        else if (x < initialHit.getX()) {
            currentDirection = Direction.LEFT;

            // Remove adjacentFieldsFromStack
            for (int i = 0; i < 3 - miss; i++)
                nextGuesses.pop();

            // Check if already at border
            if (x - 1 < 0) {
                currentDirection = Direction.RIGHT;
                nextGuesses.push(new Coordinate(initialHit.getX() + 1, initialHit.getY()));
            } else
                // Try fields to the left from current hit
                nextGuesses.push(new Coordinate(x - 1, y));
        }
        // Hit below initialHit
        else if (y > initialHit.getY()) {
            currentDirection = Direction.DOWN;

            // Remove adjacentFieldsFromStack
            for (int i = 0; i < 3 - miss; i++)
                nextGuesses.pop();

            // Check if already at border
            if (y + 1 > 9) {
                currentDirection = Direction.UP;
                nextGuesses.push(new Coordinate(initialHit.getX(), initialHit.getY() - 1));
            } else
                nextGuesses.push(new Coordinate(x, y + 1));
        }
        // Hit above initialHit
        else if (y < initialHit.getY()) {
            currentDirection = Direction.UP;
            // Remove adjacentFieldsFromStack
            for (int i = 0; i < 3 - miss; i++)
                nextGuesses.pop();

            // Check if already at border
            if (y - 1 < 0) {
                currentDirection = Direction.DOWN;
                nextGuesses.push(new Coordinate(initialHit.getX(), initialHit.getY() + 1));
            } else
                nextGuesses.push(new Coordinate(x, y - 1));
        }

        hits++;
        stateChange(AIMode.ATTACK);
    }

    public GuessAI(Game game) {
        this.aiMap = new Battleground(game);
        nextGuesses = new Stack<>();
        initCheckerboard();
        maxEnemyShipLength = 5;
        minEnemyShipLength = 2;
    }

    public void onHit(int x, int y) {
        if (state.equals(AIMode.SCOUT)) {
            hitDuringScout(x, y);
        } else if (state.equals(AIMode.ATTACK_ADJACENT)) {
            hitDuringAttackAdjacent(x, y);
        } else if (state.equals(AIMode.ATTACK)) {
            hitDuringAttack(x, y);
        }

        // TODO: Check if further fields have become irrelevant
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

    public void onMiss(int x, int y) {
        if (state.equals(AIMode.SCOUT)) {
            missDuringScout(x, y);
        } else if (state.equals(AIMode.ATTACK_ADJACENT)) {
            missDuringAttackAdjacent(x, y);
        } else if (state.equals(AIMode.ATTACK)) {
            missDuringAttack(x, y);
        }
        // TODO: Check if further fields have become irrelevant
    }

    /**
     * Follow up hits after direction is determined
     *
     * @param x X-Coord of attacked field
     * @param y Y-Coord of attacked field
     */
    public void hitDuringAttack(int x, int y) {
        aiMap.battleground[y][x] = Battleground.FieldState.IGNORE;
        switch (currentDirection) {
            case UP:
                if(Coordinate.validCoordinate(x, y))
                    nextGuesses.push(new Coordinate(x, y - 1));
                else
                    directionSwitch();
                break;
            case DOWN:
                if(Coordinate.validCoordinate(x, y))
                    nextGuesses.push(new Coordinate(x, y + 1));
                else
                    directionSwitch();
                break;
            case LEFT:
                if(Coordinate.validCoordinate(x, y))
                    nextGuesses.push(new Coordinate(x - 1, y));
                else
                    directionSwitch();
                break;
            case RIGHT:
                if(Coordinate.validCoordinate(x, y))
                    nextGuesses.push(new Coordinate(x + 1, y));
                else
                    directionSwitch();
                break;
            default:
                System.out.println("Default case during hitDuringAttack. This should not happen!");
        }
    }

    public void missDuringAttack(int x, int y) {
        aiMap.battleground[y][x] = Battleground.FieldState.IGNORE;
        directionSwitch();
    }

    public void onSunk(int x, int y) {
        System.out.println("AI has been noticed that the ship has been sunk");
        currentDirection = Direction.UNKNOWN;
        stateChange(AIMode.SCOUT);
    }

    public void missDuringAttackAdjacent(int x, int y) {
        miss++;
    }

    private void directionSwitch(){
        switch (currentDirection) {
            case UP:
                currentDirection = Direction.DOWN;
                nextGuesses.push(initialHit.delta(0, 1));
                break;
            case DOWN:
                currentDirection = Direction.UP;
                nextGuesses.push(initialHit.delta(0, -1));
                break;
            case LEFT:
                currentDirection = Direction.RIGHT;
                nextGuesses.push(initialHit.delta(1, 0));
                break;
            case RIGHT:
                currentDirection = Direction.LEFT;
                nextGuesses.push(initialHit.delta(-1, 0));
                break;
            default:
                System.out.println("Default case during missDuringAttack. This should not happen");
        }
    }

    public void stateChange(AIMode destinationState) {
        System.out.println("AI Mode set to " + destinationState.name());
        state = destinationState;
    }

    public enum Module {
        CHECKERBOARD,
        HIT_REACTION,
        SPACE_ANALYSIS,
        MEMORY
    }

    public enum Direction {
        LEFT, RIGHT, UP, DOWN, UNKNOWN
    }

    public enum AIMode {
        SCOUT, ATTACK, ATTACK_ADJACENT
    }
}
