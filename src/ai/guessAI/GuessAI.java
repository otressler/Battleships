package ai.guessAI;

import ai.PlacementAI;
import ai.PlacementAnalysis;
import com.ships.*;

import java.util.*;

/**
 * Used for retrieving optimal guess coordinates
 */
public class GuessAI {
    String name;
    private int[][] placementMemory = new int[10][10]; // remember shots of enemy
    private ArrayList<Module> modules;
    private ArrayList<ShipType> enemyShips;
    private ArrayList<Ship> destroyedEnemyShips;
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
    private PlacementAnalysis opponentStrategyAnalyzer;

    /**
     * Constructor
     * @param modules List of Modules (See enum MODULE)
     * @param decisionDelay Sets a timedelay before a guess for visibility purposes
     * @param name Used to detect AI in console output
     */
    public GuessAI(ArrayList<Module> modules, long decisionDelay, String name) {
        destroyedEnemyShips = new ArrayList<>();
        opponentStrategyAnalyzer = new PlacementAnalysis(20);
        this.decisionDelay = decisionDelay;
        this.aiMap = new Battleground();
        this.modules = modules;
        this.name = name;
        initGuessStack();

        enemyShips = Util.convertShipList(Util.getDefaultShipTypes());
    }

    /**
     * Returns the analyzer
     * @return PlacementAnalysis object
     */
    public PlacementAnalysis getOpponentStrategyAnalyzer() {
        return opponentStrategyAnalyzer;
    }

    /**
     * Initializes the stack of guess coordinates
     */
    private void initGuessStack() {
        nextGuesses = new Stack<>();
        if (modules.contains(Module.CHECKERBOARD)) {
            initCheckerboard();
        } else
            initAllFields();

        /*if(modules.contains(Module.MEMORY)) {
            int[][] tempPlacementMemory = placementMemory;
            Arrays.sort(tempPlacementMemory,
                    new Comparator<int[]>() {
                        public int compare(int[] a1, int[] a2) {
                            //return Integer.compare(a1[1], a2[1]); // Ascending
                            return Integer.compare(a2[1], a1[1]); // Descending
                        }
                    }
            );
        }*/
    }

    /**
     * Can update the opponents strategy (Recommended: Use after win)
     */
    public void updateOpponentStrategy() {
        if (modules.contains(Module.MEMORY))
            opponentStrategyAnalyzer.calculateDenseValue(destroyedEnemyShips);
    }

    /**
     * Call after every game. Resets variables that should only exist for the duration of one game
     */
    public void resetState() {
        destroyedEnemyShips = new ArrayList<>();
        this.aiMap = new Battleground();
        this.nextGuesses = new Stack<>();
        this.state = AIMode.SCOUT;
        this.enemyShips = Util.convertShipList(Util.getDefaultShipTypes());
        adjacentMisses = 0;
        hits = 0;
        maxEnemyShipLength = 5;
        minEnemyShipLength = 2;

        currentDirection = Direction.UNKNOWN;

        gapChecker = new GapChecker();

        initGuessStack();
    }

    /**
     * Returns the next coordinate that should be shot at. Result depending on module list.
     * @return Coordinate
     */
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
            System.out.println(name + "Popping due to invalid coordinate " + nextGuesses.peek().toString());
            nextGuesses.pop();
        }

        while (aiMap.battleground[nextGuesses.peek().getY()][nextGuesses.peek().getX()].equals(Battleground.FieldState.IGNORE)) {
            System.out.println("Popping due to IGNORE coordinate " + nextGuesses.peek().toString());
            nextGuesses.pop();
        }

        System.out.println("Popping due to AI Guess");
        return nextGuesses.pop();
    }

    /**
     * Pushes all fields on the guess stack
     */
    private void initAllFields() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                nextGuesses.push(new Coordinate(x, y));
                aiMap.battleground[y][x] = Battleground.FieldState.POTENTIAL;
            }
        }
        Collections.shuffle(nextGuesses);
    }

    /**
     * Pushes every second field on the guess stack. WARNING: ONLY USE WITH HITREACT, or the ai will run out of fields
     */
    private void initCheckerboard() {
        for (int y = 0; y < 10; y++) {
            for (int x = (y) % 2; x < 10; x += 2) {
                nextGuesses.push(new Coordinate(x, y));
                aiMap.battleground[y][x] = Battleground.FieldState.POTENTIAL;
            }
        }
        Collections.shuffle(nextGuesses);
    }

    /**
     * Keeps track of enemy ships
     * @param ship destroyed ship
     */
    private void updateActiveShips(Ship ship) {
        for (Iterator<ShipType> i = enemyShips.iterator(); i.hasNext(); ) {
            if (i.next().getLength() == ship.length) {
                i.remove();
                return;
            }
        }
    }

    /**
     * Defines behaviour after hit
     * @param x x-Coordinate
     * @param y y-Coordinate
     */
    public void onHit(int x, int y) {
        if (modules.contains(Module.HIT_REACTION)) {
            if (state.equals(AIMode.SCOUT)) {
                hitDuringScout(x, y);
            } else if (state.equals(AIMode.ATTACK_ADJACENT)) {
                hitDuringAttackAdjacent(x, y);
            } else if (state.equals(AIMode.ATTACK)) {
                hitDuringAttack(x, y);
            }

        }
        hits++;
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

    /**
     * Defines behaviour after miss
     * @param x x-Coordinate
     * @param y y-Coordinate
     */
    public void onMiss(int x, int y) {
        setIgnore(new Coordinate(x, y));
        System.out.println(name + "Setting " + new Coordinate(x, y) + "to ignore in onMiss");
        if (state.equals(AIMode.SCOUT)) {
            missDuringScout(x, y);
        } else if (state.equals(AIMode.ATTACK_ADJACENT)) {
            missDuringAttackAdjacent(x, y);
        } else if (state.equals(AIMode.ATTACK)) {
            missDuringAttack(x, y);
        }
        misses++;
    }

    /**
     * Defines behaviour after destroying an enemy ship
     * @param s Destroyed ship
     */
    public void onSunk(Ship s) {
        updateActiveShips(s);
        destroyedEnemyShips.add(s);
        ignoreAdjacentBlockedFields(s);

        updateMinEnemyShipLength();
        updateMaxEnemyShipLength();
        if (destroyedEnemyShips.size() == 1) {
            if (modules.contains(Module.MEMORY) && opponentStrategyAnalyzer.validSuggestion() && opponentStrategyAnalyzer.guessPlacementStrategy().equals(PlacementAI.PositionStrategy.DENSE)) {
                nextGuesses.sort(Comparator.comparingDouble(value -> value.euclideanDistance(initialHit)));
            } else if (modules.contains(Module.MEMORY) && opponentStrategyAnalyzer.validSuggestion() && opponentStrategyAnalyzer.guessPlacementStrategy().equals(PlacementAI.PositionStrategy.SPARSE)) {
                nextGuesses.sort(Comparator.comparingDouble(value -> value.euclideanDistance(initialHit) * -1));
            }
        }
        System.out.println("AI has been noticed that the ship has been sunk at " + s.getCoordinates().toString());
        currentDirection = Direction.UNKNOWN;
        stateChange(AIMode.SCOUT);
    }

    /**
     * Defines behaviour after hit in scout mode. Actions depend on installed modules.
     * @param x x-Coordinate
     * @param y y-Coordinate
     */
    private void hitDuringScout(int x, int y) {
        Coordinate hit = new Coordinate(x, y);
        stateChange(AIMode.ATTACK_ADJACENT);
        Integer[] options = {0, 1, 2, 3};
        List<Integer> choices = Arrays.asList(options);
        Collections.shuffle(choices);
        initialHit = new Coordinate(x, y);
        for (Integer choice : choices) {
            switch (choice) {
                case 0:
                    if (Coordinate.validCoordinate(hit.delta(1, 0)) && !getFieldState(hit).equals(Battleground.FieldState.IGNORE)) {
                        System.out.println("Adding to stack " + hit.delta(1, 0));
                        nextGuesses.push(hit.delta(1, 0));
                    } else
                        adjacentMisses++;
                    break;
                case 1:
                    if (Coordinate.validCoordinate(hit.delta(-1, 0)) && !getFieldState(hit).equals(Battleground.FieldState.IGNORE)) {
                        System.out.println("Adding to stack " + hit.delta(-1, 0));
                        nextGuesses.push(hit.delta(-1, 0));
                    } else
                        adjacentMisses++;
                    break;
                case 2:
                    if (Coordinate.validCoordinate(hit.delta(0, 1)) && !getFieldState(hit).equals(Battleground.FieldState.IGNORE)) {
                        System.out.println(hit.delta(0, 1));
                        nextGuesses.push(hit.delta(0, 1));
                    } else
                        adjacentMisses++;
                    break;
                case 3:
                    if (Coordinate.validCoordinate(hit.delta(0, -1)) && !getFieldState(hit).equals(Battleground.FieldState.IGNORE)) {
                        System.out.println("Adding to stack " + hit.delta(0, -1));
                        nextGuesses.push(hit.delta(0, -1));
                    } else
                        adjacentMisses++;
                    break;
                default:
                    System.out.println("Illegal number generated");
            }
        }
    }
    /**
     * Defines behaviour after miss in scout mode.
     * @param x x-Coordinate
     * @param y y-Coordinate
     */
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
        for (int i = 0; i < 2 - adjacentMisses; i++) {
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

    /**
     * Defines behaviour after miss in scout mode.
     * @param x x-Coordinate
     * @param y y-Coordinate
     */
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

    /**
     * Defines behaviour after hit in attack mode. Switches attack vector.
     * @param x x-Coordinate
     * @param y y-Coordinate
     */
    private void missDuringAttack(int x, int y) {
        directionSwitch();
    }

    /**
     * Switches attack vector, and pushes coordinate of opposite direction on stack
     */
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

    /**
     * Changes state to one of the AIModes
     * @param destinationState AIMode
     */
    private void stateChange(AIMode destinationState) {
        System.out.print("AI Mode set to " + destinationState.name());
        if (destinationState.equals(AIMode.ATTACK))
            System.out.println(" " + currentDirection.name());
        state = destinationState;
    }

    /**
     * Used if PLACEMENT AWARENESS is activated. Ignores fields around sunk ships.
     * @param ship
     */
    private void ignoreAdjacentBlockedFields(Ship ship) {
        if (modules.contains(Module.IGNORE_BLOCKED)) {

            if (currentDirection.equals(Direction.UP) || currentDirection.equals(Direction.DOWN)) {
                // Block field over the ship
                if (Coordinate.validCoordinate(new Coordinate(ship.xPos, ship.yPos - 1))) {
                    setIgnore(new Coordinate(ship.xPos, ship.yPos - 1));
                    System.out.println(name + "Setting " + new Coordinate(ship.xPos, ship.yPos - 1) + "to ignore in ignoreAdjacentBlockedFields");

                }
                // Block fields below the ship
                if (Coordinate.validCoordinate(new Coordinate(ship.xPos, ship.yPos + ship.length))) {
                    setIgnore(new Coordinate(ship.xPos, ship.yPos + ship.length));
                    System.out.println(name + "Setting " + new Coordinate(ship.xPos, ship.yPos + ship.length) + "to ignore in ignoreAdjacentBlockedFields");
                }
                // Block fields left from the ship
                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos - 1, ship.yPos + i))) {
                        setIgnore(new Coordinate(ship.xPos - 1, ship.yPos + i));
                        System.out.println(name + "Setting " + new Coordinate(ship.xPos - 1, ship.yPos + i) + "to ignore in ignoreAdjacentBlockedFields");
                    }
                }

                // Block fields occupied by the ship
                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos, ship.yPos + i))) {
                        setIgnore(new Coordinate(ship.xPos, ship.yPos + i));
                        System.out.println(name + "Setting " + new Coordinate(ship.xPos, ship.yPos + i) + "to ignore in ignoreAdjacentBlockedFields");
                    }
                }

                // Block fields right from the ship
                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos + 1, ship.yPos + i))) {
                        setIgnore(new Coordinate(ship.xPos + 1, ship.yPos + i));
                        System.out.println(name + "Setting " + new Coordinate(ship.xPos + 1, ship.yPos + i) + "to ignore in ignoreAdjacentBlockedFields");
                    }
                }

            } else if (currentDirection.equals(Direction.LEFT) || currentDirection.equals(Direction.RIGHT)) {
                // Block fields left from the ship
                if (Coordinate.validCoordinate(new Coordinate(ship.xPos - 1, ship.yPos))) {
                    setIgnore(new Coordinate(ship.xPos - 1, ship.yPos));
                    System.out.println(name + "Setting " + new Coordinate(ship.xPos - 1, ship.yPos) + "to ignore in ignoreAdjacentBlockedFields");
                }
                // Block fields right from the ship
                if (Coordinate.validCoordinate(new Coordinate(ship.xPos + ship.length, ship.yPos))) {
                    setIgnore(new Coordinate(ship.xPos + ship.length, ship.yPos));
                    System.out.println(name + "Setting " + new Coordinate(ship.xPos + ship.length, ship.yPos) + "to ignore in ignoreAdjacentBlockedFields");
                }
                // Block field over the ship
                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos + i, ship.yPos - 1))) {
                        setIgnore(new Coordinate(ship.xPos + i, ship.yPos - 1));
                        System.out.println(name + "Setting " + new Coordinate(ship.xPos + i, ship.yPos - 1) + "to ignore in ignoreAdjacentBlockedFields");
                    }
                }

                // Block fields occupied by the ship
                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos + i, ship.yPos))) {
                        setIgnore(new Coordinate(ship.xPos + i, ship.yPos));
                        System.out.println(name + "Setting " + new Coordinate(ship.xPos + i, ship.yPos) + "to ignore in ignoreAdjacentBlockedFields");
                    }
                }

                // Block fields below the ship

                for (int i = 0; i < ship.length; i++) {
                    if (Coordinate.validCoordinate(new Coordinate(ship.xPos + i, ship.yPos + 1))) {
                        setIgnore(new Coordinate(ship.xPos + i, ship.yPos + 1));
                        System.out.println(name + "Setting " + new Coordinate(ship.xPos + i, ship.yPos + 1) + "to ignore in ignoreAdjacentBlockedFields");
                    }
                }
            } else {
                if (ship.getCoordinates().get(0).getY() != ship.getCoordinates().get(1).getY()) {
                    currentDirection = Direction.DOWN;
                } else {
                    currentDirection = Direction.RIGHT;
                }
                ignoreAdjacentBlockedFields(ship);
            }
        }
    }

    /**
     * Keeps track of the length of enemies remaining ships
     */
    private void updateMaxEnemyShipLength() {
        int tempMax = 0;
        for (ShipType type : enemyShips) {
            if (type.getLength() > tempMax) {
                tempMax = type.getLength();
            }
        }
        if (tempMax < maxEnemyShipLength)
            maxEnemyShipLength = tempMax;
    }
    /**
     * Keeps track of the length of enemies remaining ships
     */
    private void updateMinEnemyShipLength() {
        int tempMin = 5;
        for (ShipType type : enemyShips) {
            if (type.getLength() < tempMin) {
                tempMin = type.getLength();
            }
        }
        if (tempMin > minEnemyShipLength)
            minEnemyShipLength = tempMin;
        if (minEnemyShipLength > 2)
            ignoreTooShortGaps();
    }

    /**
     * Ignores a coordinate
     * @param c Coordinate
     */
    private void setIgnore(Coordinate c) {
        aiMap.battleground[c.getY()][c.getX()] = Battleground.FieldState.IGNORE;
        if (modules.contains(Module.SPACE_ANALYSIS)) {
            gapChecker.splitGaps(c);
            if (minEnemyShipLength > 2)
                ignoreTooShortGaps();
        }
    }

    /**
     * See GapChecker
     */
    private void ignoreTooShortGaps() {
        List<Coordinate> gaps = gapChecker.ignoreTooShortGaps(minEnemyShipLength);
        gaps.forEach(coordinate -> aiMap.battleground[coordinate.getY()][coordinate.getX()] = Battleground.FieldState.IGNORE);
    }

    /**
     * Returns field state of the ai map
     * @param c Coordinate of the desired field
     * @return FieldState
     */
    private Battleground.FieldState getFieldState(Coordinate c) {
        return aiMap.battleground[c.getY()][c.getX()];
    }

    /**
     * @return Total hits
     */
    public int getHits() {
        return hits;
    }

    /**
     * @return Total misses
     */
    public int getMisses() {
        return misses;
    }

    /**
     * Stringifier for debug and output purposes.
     * @return String of the aimap
     */
    public String toString() {
        String output = "AI MAP";
        output += System.lineSeparator();
        output += ("    A  B  C  D  E  F  G  H  I  J       ");
        output += System.lineSeparator();
        for (int y = 0; y < aiMap.battleground.length; y++) {
            output += (Util.padRight(Integer.toString(y), 3));
            for (int x = 0; x < aiMap.battleground[y].length; x++) {
                output += ("[" + aiMap.battleground[y][x].getSymbol() + "]");
            }
            output += System.lineSeparator();
        }
        return output;
    }

    /**
     * @return FieldState matrix representing the aimap
     */
    public Battleground getAiMap() {
        return aiMap;
    }

    /**
     * Updates the memory of the enemies placement if the ai hits
     * @param coordinate
     */
    public void updatePlacementMemory(Coordinate coordinate) {
        placementMemory[coordinate.getY()][coordinate.getX()] += 1;
    }

    /**
     * @return Returns all of the destroyed enemy ships
     */
    public ArrayList<Ship> getDestroyedEnemyShips() {
        return destroyedEnemyShips;
    }

    /**
     * AIModules
     */
    public enum Module {
        CHECKERBOARD,
        HIT_REACTION,
        IGNORE_BLOCKED,
        SPACE_ANALYSIS,
        MEMORY
    }

    /**
     * Directions UP, DOWN, ...
     */
    public enum Direction {
        LEFT, RIGHT, UP, DOWN, UNKNOWN
    }

    /**
     * Different AIStates
     */
    public enum AIMode {
        SCOUT, ATTACK, ATTACK_ADJACENT

    }
}
