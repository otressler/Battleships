package ai.guessAI;

import com.ships.*;

import java.util.*;


// TODO: Checkerboard only results in empty stack
public class GuessAI {
    int[][] placementMemory = new int[10][10]; // remember shots of enemy
    private ArrayList<Module> modules;
    private ArrayList<ShipType> enemyShips;
    private long decisionDelay;
    private Battleground aiMap;
    private Stack<Coordinate> nextGuesses;
    private AIMode state = AIMode.SCOUT;
    private GapChecker gapChecker = new GapChecker();
    private int maxEnemyShipLength = 5;
    private int minEnemyShipLength = 2;
    // The following fields are used to determine the orientation of the enemy ship during ATTACK_ADJACENT
    private Coordinate initialHit;
    private Direction currentDirection = Direction.UNKNOWN;
    private int hits = 0;
    private int misses;
    private int adjacentMisses = 0;

    public GuessAI(ArrayList<Module> modules, long decisionDelay) {
        this.decisionDelay = decisionDelay;
        this.aiMap = new Battleground();
        this.modules = modules;

        initGuessStack();

        enemyShips = Util.convertShipList(Util.getDefaultShipTypes());
    }

    private void initGuessStack() {
        nextGuesses = new Stack<>();
        if (modules.contains(Module.CHECKERBOARD)) {
            initCheckerboard();
        } else
            initAllFields();
        // If modules contains memory -> put matrix on stock sorted top to bottom
    }

    public void resetState() {
        this.aiMap = new Battleground();
        this.nextGuesses = new Stack<>();
        this.state = AIMode.SCOUT;
        adjacentMisses = 0;
        hits = 0;
        maxEnemyShipLength = 5;
        minEnemyShipLength = 2;

        currentDirection = Direction.UNKNOWN;

        gapChecker = new GapChecker();

        initGuessStack();
    }

    public Coordinate getNextGuess() {
        // Delay next guess for better visibility
        try {
            Thread.sleep(decisionDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (modules.contains(Module.CHECKERBOARD) && nextGuesses.empty()) {
            //initCheckerboard(true);
        }

        if (!Coordinate.validCoordinate(nextGuesses.peek())) {
            System.out.println("Popping due to invalid coordinate " + nextGuesses.peek().toString());
            nextGuesses.pop();
        }

        while (aiMap.battleground[nextGuesses.peek().getY()][nextGuesses.peek().getX()].equals(Battleground.FieldState.IGNORE)) {
            System.out.println("Popping due to IGNORE coordinate " + nextGuesses.peek().toString());
            nextGuesses.pop();
        }
        setIgnore(nextGuesses.peek());
        System.out.println("Popping due to AI Guess");
        return nextGuesses.pop();
    }

    private void initAllFields() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                nextGuesses.push(new Coordinate(x, y));
                aiMap.battleground[y][x] = Battleground.FieldState.POTENTIAL;
            }
        }
        Collections.shuffle(nextGuesses);
    }

    private void initCheckerboard() {
        for (int y = 0; y < 10; y++) {
            for (int x = (y) % 2; x < 10; x += 2) {
                nextGuesses.push(new Coordinate(x, y));
                aiMap.battleground[y][x] = Battleground.FieldState.POTENTIAL;
            }
        }
        Collections.shuffle(nextGuesses);
    }

    private void updateActiveShips(Ship ship) {
        for (Iterator<ShipType> i = enemyShips.iterator(); i.hasNext(); ) {
            if (i.next().getLength() == ship.length) {
                i.remove();
                return;
            }
        }
    }

    public void onHit(int x, int y) {
        if (modules.contains(Module.HIT_REACTION)) {
            if (state.equals(AIMode.SCOUT)) {
                hitDuringScout(x, y);
            } else if (state.equals(AIMode.ATTACK_ADJACENT)) {
                hitDuringAttackAdjacent(x, y);
            } else if (state.equals(AIMode.ATTACK)) {
                hitDuringAttack(x, y);
            }

            hits++;
        }
        // TODO: Check if further fields have become irrelevant
        // Push coordinates next to hit on stack
        // Enter AttackMode
        // After hitting ship for the second time and !sunk
        // set orientation
        // calculate max enemy shiplength
        // try next field depending on orientation
        // on first adjacentMisses try other direction if ship has not been sunk
        // fire as long as hit fields < max enemy shiplength || ship sunk
        // Mark everything around sunk ship as IGNORE
        // Remove ignored fields from checkerboard stack
    }

    public void onMiss(int x, int y) {
        if (state.equals(AIMode.SCOUT)) {
            missDuringScout(x, y);
        } else if (state.equals(AIMode.ATTACK_ADJACENT)) {
            missDuringAttackAdjacent(x, y);
        } else if (state.equals(AIMode.ATTACK)) {
            missDuringAttack(x, y);
        }
        misses++;
        // TODO: Check if further fields have become irrelevant
    }

    public void onSunk(Ship s) {
        updateActiveShips(s);
        ignoreAdjacentBlockedFields(s);

        calculateMinEnemyShipLength();
        calculateMaxEnemyShipLength();

        System.out.println("AI has been noticed that the ship has been sunk at " + s.getCoordinates().toString());
        currentDirection = Direction.UNKNOWN;
        stateChange(AIMode.SCOUT);
    }

    private void hitDuringScout(int x, int y) {
        stateChange(AIMode.ATTACK_ADJACENT);
        // TODO: Check for irrelevant fields in switch statement
        Integer[] options = {0, 1, 2, 3};
        List<Integer> choices = Arrays.asList(options);
        Collections.shuffle(choices);
        initialHit = new Coordinate(x, y);
        for (Integer choice : choices) {
            switch (choice) {
                case 0:
                    // TODO: Replace with valid Coordinate
                    if (x + 1 < 10) {
                        nextGuesses.push(new Coordinate(x + 1, y));
                    } else
                        adjacentMisses++;
                    break;
                case 1:
                    if (x - 1 >= 0) {
                        nextGuesses.push(new Coordinate(x - 1, y));
                    } else
                        adjacentMisses++;
                    break;
                case 2:
                    if (y + 1 < 10) {
                        nextGuesses.push(new Coordinate(x, y + 1));
                    } else
                        adjacentMisses++;
                    break;
                case 3:
                    if (y - 1 >= 0) {
                        nextGuesses.push(new Coordinate(x, y - 1));
                    } else
                        adjacentMisses++;
                    break;
                default:
                    System.out.println("Illegal number generated");
            }
        }
    }

    private void missDuringScout(int x, int y) {

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
    private void hitDuringAttackAdjacent(int x, int y) {
        // Remove adjacentFieldsFromStack
        // TODO: Install options counter
        for (int i = 0; i < 3 - adjacentMisses; i++) {
            System.out.println("Popping adjacentField " + nextGuesses.peek().toString());
            nextGuesses.pop();
        }

        // Hit right from initialHit
        if (x > initialHit.getX()) {
            currentDirection = Direction.RIGHT;

            // Check if already at border
            if (x + 1 > 9 || (Coordinate.validCoordinate(x + 1, y) && getFieldState(new Coordinate(x + 1, y)).equals(Battleground.FieldState.IGNORE))) {
                currentDirection = Direction.LEFT;
                nextGuesses.push(new Coordinate(initialHit.getX() - 1, initialHit.getY()));
            } else
                // Try fields to the right from current hit
                nextGuesses.push(new Coordinate(x + 1, y));
        }
        // Hit left from initialHit
        else if (x < initialHit.getX()) {
            currentDirection = Direction.LEFT;
            // Check if already at border
            if (x - 1 < 0 || (Coordinate.validCoordinate(x - 1, y) && getFieldState(new Coordinate(x - 1, y)).equals(Battleground.FieldState.IGNORE))) {
                currentDirection = Direction.RIGHT;
                nextGuesses.push(new Coordinate(initialHit.getX() + 1, initialHit.getY()));
            } else
                // Try fields to the left from current hit
                nextGuesses.push(new Coordinate(x - 1, y));
        }
        // Hit below initialHit
        else if (y > initialHit.getY()) {
            currentDirection = Direction.DOWN;

            // Check if already at border
            if (y + 1 > 9 || (Coordinate.validCoordinate(x, y + 1) && getFieldState(new Coordinate(x, y + 1)).equals(Battleground.FieldState.IGNORE))) {
                currentDirection = Direction.UP;
                nextGuesses.push(new Coordinate(initialHit.getX(), initialHit.getY() - 1));
            } else
                nextGuesses.push(new Coordinate(x, y + 1));
        }
        // Hit above initialHit
        else if (y < initialHit.getY()) {
            currentDirection = Direction.UP;

            // Check if already at border
            if (y - 1 < 0 || (Coordinate.validCoordinate(x, y - 1) && getFieldState(new Coordinate(x, y - 1)).equals(Battleground.FieldState.IGNORE))) {
                currentDirection = Direction.DOWN;
                nextGuesses.push(new Coordinate(initialHit.getX(), initialHit.getY() + 1));
            } else
                nextGuesses.push(new Coordinate(x, y - 1));
        }
        adjacentMisses = 0;
        stateChange(AIMode.ATTACK);
    }

    private void missDuringAttackAdjacent(int x, int y) {
        adjacentMisses++;
    }

    /**
     * Follow up hits after direction is determined
     *
     * @param x X-Coord of attacked field
     * @param y Y-Coord of attacked field
     */
    private void hitDuringAttack(int x, int y) {
        switch (currentDirection) {
            case UP:
                if (Coordinate.validCoordinate(x, y - 1) && !getFieldState(new Coordinate(x, y - 1)).equals(Battleground.FieldState.IGNORE))
                    nextGuesses.push(new Coordinate(x, y - 1));
                else
                    directionSwitch();
                break;
            case DOWN:
                if (Coordinate.validCoordinate(x, y + 1) && !getFieldState(new Coordinate(x, y + 1)).equals(Battleground.FieldState.IGNORE))
                    nextGuesses.push(new Coordinate(x, y + 1));
                else
                    directionSwitch();
                break;
            case LEFT:
                if (Coordinate.validCoordinate(x - 1, y) && !getFieldState(new Coordinate(x - 1, y)).equals(Battleground.FieldState.IGNORE))
                    nextGuesses.push(new Coordinate(x - 1, y));
                else
                    directionSwitch();
                break;
            case RIGHT:
                if (Coordinate.validCoordinate(x + 1, y) && !getFieldState(new Coordinate(x + 1, y)).equals(Battleground.FieldState.IGNORE))
                    nextGuesses.push(new Coordinate(x + 1, y));
                else
                    directionSwitch();
                break;
            default:
                System.out.println("Default case during hitDuringAttack. This should not happen!");
        }
    }

    private void missDuringAttack(int x, int y) {
        directionSwitch();
    }

    private void directionSwitch() {
        System.out.println("Switching direction");
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

    private void stateChange(AIMode destinationState) {
        System.out.print("AI Mode set to " + destinationState.name());
        if (destinationState.equals(AIMode.ATTACK))
            System.out.println(" " + currentDirection.name());
        state = destinationState;
    }

    private void ignoreAdjacentBlockedFields(Ship ship) {
        if (modules.contains(Module.IGNORE_BLOCKED)) {

            if (currentDirection.equals(Direction.UP) || currentDirection.equals(Direction.DOWN)) {
                // Block field over the ship
                if (Coordinate.validCoordinate(new Coordinate(ship.xPos, ship.yPos - 1))) {
                    setIgnore(new Coordinate(ship.xPos, ship.yPos - 1));
                }
                // Block fields below the ship
                if (Coordinate.validCoordinate(new Coordinate(ship.xPos, ship.yPos + ship.length))) {
                    setIgnore(new Coordinate(ship.xPos, ship.yPos + ship.length));
                }
                // Block fields left from the ship
                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos - 1, ship.yPos + i))) {
                        setIgnore(new Coordinate(ship.xPos - 1, ship.yPos + i));
                    }
                }

                // Block fields right from the ship
                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos + 1, ship.yPos + i))) {
                        setIgnore(new Coordinate(ship.xPos + 1, ship.yPos + i));
                    }
                }

            } else {
                // Block fields left from the ship
                if (Coordinate.validCoordinate(new Coordinate(ship.xPos - 1, ship.yPos))) {
                    setIgnore(new Coordinate(ship.xPos - 1, ship.yPos));
                }
                // Block fields right from the ship
                if (Coordinate.validCoordinate(new Coordinate(ship.xPos + ship.length, ship.yPos))) {
                    setIgnore(new Coordinate(ship.xPos + ship.length, ship.yPos));
                }
                // Block field over the ship
                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos + i, ship.yPos - 1))) {
                        setIgnore(new Coordinate(ship.xPos + i, ship.yPos - 1));
                    }
                }

                // Block fields below the ship

                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos + i, ship.yPos + 1))) {
                        setIgnore(new Coordinate(ship.xPos + i, ship.yPos + 1));
                    }
                }
            }
        }
    }

    private void calculateMaxEnemyShipLength() {
        int tempMax = 0;
        for (ShipType type : enemyShips) {
            if (type.getLength() > tempMax) {
                tempMax = type.getLength();
            }
        }
        if (tempMax < maxEnemyShipLength)
            maxEnemyShipLength = tempMax;
    }

    private void calculateMinEnemyShipLength() {
        int tempMin = 5;
        for (ShipType type : enemyShips) {
            if (type.getLength() < tempMin) {
                tempMin = type.getLength();
            }
        }
        if (tempMin > minEnemyShipLength)
            minEnemyShipLength = tempMin;
    }

    private void setIgnore(Coordinate c) {
        aiMap.battleground[c.getY()][c.getX()] = Battleground.FieldState.IGNORE;
        if (modules.contains(Module.SPACE_ANALYSIS)) {
            gapChecker.splitGaps(c);
            if(minEnemyShipLength>2)
                ignoreTooShortGaps();
        }
    }

    private void ignoreTooShortGaps() {
        List<Coordinate> gaps = gapChecker.ignoreTooShortGaps(minEnemyShipLength);
        gaps.forEach(coordinate -> aiMap.battleground[coordinate.getY()][coordinate.getX()] = Battleground.FieldState.IGNORE);
    }

    private Battleground.FieldState getFieldState(Coordinate c) {
        return aiMap.battleground[c.getY()][c.getX()];
    }

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public String toString() {
        String output = "AI MAP";
        output += System.lineSeparator();
        output += ("    A  B  C  D  E  F  G  H  I  J       ");
        output += System.lineSeparator();
        for (int y = 0; y < aiMap.battleground.length; y++) {
            output += (Util.padRight(Integer.toString(y), 3));
            for (int x = 0; x < aiMap.battleground[y].length; x++) {
                if (/*!battleground[y][x].equals(FieldState.SHIP) && !aiMap.battleground[y][x].equals(Battleground.FieldState.BLOCKED)*/ true)
                    output += ("[" + aiMap.battleground[y][x].getSymbol() + "]");
                else
                    output += ("[ ]");
            }
            output += System.lineSeparator();
        }
        return output;
    }

    public Battleground getAiMap() {
        return aiMap;
    }

    public void updatePlacementMemory(Coordinate coordinate) {
        placementMemory[coordinate.getY()][coordinate.getX()] += 1;
    }

    public enum Module {
        CHECKERBOARD,
        HIT_REACTION,
        IGNORE_BLOCKED,
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
