package ai;

import com.ships.Battleground;
import com.ships.Coordinate;
import com.ships.Game;

import java.util.*;

public class GuessAI {
    Battleground aiMap;
    Stack<Coordinate> nextGuesses;
    AIMode state = AIMode.SCOUT;


    int maxEnemyShipLength, minEnemyShipLength;

    // The following fields are used to determine the orientation of the enemy ship during ATTACK_ADJACENT
    Coordinate initialHit;
    ArrayList<Direction> possibleDirections = new ArrayList<>();
    Direction currentDirection;
    int hits = 0;
    int miss = 0;

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
            possibleDirections.add(Direction.UP);
            possibleDirections.add(Direction.DOWN);
            possibleDirections.add(Direction.LEFT);
            possibleDirections.add(Direction.RIGHT);
        } else if (state.equals(AIMode.ATTACK_ADJACENT)) {
            missDuringAttackAdjacent(x, y);
        } else if (state.equals(AIMode.ATTACK)) {
            missDuringAttack(x, y);
        }
        // TODO: Check if further fields have become irrelevant
    }

    public void hitDuringScout(int x, int y) {
        state = AIMode.ATTACK_ADJACENT;
        Random r = new Random();
        // TODO: Check for irrelevant fields in switch statement
        for (int i = 0; i < 4; i++) {
            Integer[] options = {0, 1, 2, 3};
            List<Integer> choices = Arrays.asList(options);
            Collections.shuffle(choices);
            initialHit = new Coordinate(x, y);
            for (Integer choice : choices) {
                switch (choice) {
                    case 0:
                        nextGuesses.push(new Coordinate(x + 1, y));
                        break;
                    case 1:
                        nextGuesses.push(new Coordinate(x - 1, y));
                        break;
                    case 2:
                        nextGuesses.push(new Coordinate(x, y + 1));
                        break;
                    case 3:
                        nextGuesses.push(new Coordinate(x, y - 1));
                        break;
                    default:
                        System.out.println("Illegal number generated");
                }
            }
        }
    }

    public void missDuringScout(int x, int y) {
        // TODO: Remove possible direction
        // TODO: Remove possible direction based on max enemy shipLength

        // Miss right from initialHit
        if (x > initialHit.getX()) {
            possibleDirections.remove(Direction.RIGHT);
        }
        // Miss left from initialHit
        else if (x < initialHit.getX()) {
            possibleDirections.remove(Direction.LEFT);
        }
        // Miss above initialHit
        else if (y > initialHit.getY()) {
            possibleDirections.remove(Direction.UP);
        }
        // Miss below initialHit
        else if (y < initialHit.getY()) {
            possibleDirections.remove(Direction.DOWN);
        }
    }

    public void hitDuringAttackAdjacent(int x, int y) {
        // Hit right from initialHit
        if (x > initialHit.getX()) {
            // Remove vertical directions
            possibleDirections.remove(Direction.DOWN);
            possibleDirections.remove(Direction.UP);

            // Remove adjacentFieldsFromStack
            for (int i = 0; i < 3 - miss; i++)
                nextGuesses.pop();

            // Try fields to the right from current hit
            currentDirection = Direction.RIGHT;
            nextGuesses.push(new Coordinate(x + 1, y));
            hits++;
            state = AIMode.ATTACK;
        }
        // Hit left from initialHit
        else if (x < initialHit.getX()) {
            // Remove vertical directions
            possibleDirections.remove(Direction.DOWN);
            possibleDirections.remove(Direction.UP);

            // Remove adjacentFieldsFromStack
            for (int i = 0; i < 3 - miss; i++)
                nextGuesses.pop();


            // Try fields to the left from current hit
            currentDirection = Direction.LEFT;
            nextGuesses.push(new Coordinate(x - 1, y));
            hits++;
            state = AIMode.ATTACK;
        }
        // Hit above initialHit
        else if (y > initialHit.getY()) {
            // Remove horizontal directions
            possibleDirections.remove(Direction.LEFT);
            possibleDirections.remove(Direction.RIGHT);

            // Remove adjacentFieldsFromStack
            for (int i = 0; i < 3 - miss; i++)
                nextGuesses.pop();

            currentDirection = Direction.DOWN;
            nextGuesses.push(new Coordinate(x, y + 1));
            hits++;
            state = AIMode.ATTACK;
        }
        // Hit below initialHit
        else if (y < initialHit.getY()) {
            // Remove horizontal directions
            possibleDirections.remove(Direction.LEFT);
            possibleDirections.remove(Direction.RIGHT);

            // Remove adjacentFieldsFromStack
            for (int i = 0; i < 3 - miss; i++)
                nextGuesses.pop();

            currentDirection = Direction.UP;
            nextGuesses.push(new Coordinate(x, y - 1));
            hits++;
            state = AIMode.ATTACK;
        }
    }

    public void missDuringAttackAdjacent(int x, int y) {
        miss++;
    }

    public void hitDuringAttack(int x, int y) {
        switch (currentDirection) {
            case UP:
                nextGuesses.push(new Coordinate(x, y - 1));
                break;
            case DOWN:
                nextGuesses.push(new Coordinate(x, y + 1));
                break;
            case LEFT:
                nextGuesses.push(new Coordinate(x - 1, y));
                break;
            case RIGHT:
                nextGuesses.push(new Coordinate(x + 1, y));
                break;
            default:
                System.out.println("BÖÖÖP");
        }
    }

    public void missDuringAttack(int x, int y) {
        possibleDirections.remove(currentDirection);
        currentDirection = possibleDirections.get(0);

        switch (currentDirection) {
            case UP:
                nextGuesses.push(new Coordinate(x, y - 1));
                break;
            case DOWN:
                nextGuesses.push(new Coordinate(x, y + 1));
                break;
            case LEFT:
                nextGuesses.push(new Coordinate(x - 1, y));
                break;
            case RIGHT:
                nextGuesses.push(new Coordinate(x + 1, y));
                break;
            default:
                System.out.println("MÖÖÖP");
        }
    }

    public void onSunk(int x, int y) {
        hits = 0;
    }

    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    enum AIMode {
        SCOUT, ATTACK, ATTACK_ADJACENT
    }
}
