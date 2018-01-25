package ai.guessAI;

import com.ships.*;

import java.util.*;


// TODO: Checkerboard only results in empty stack
public class GuessAI {
    private ArrayList<Module> modules;
    private ArrayList<ShipType> enemyShips;

    private long decisionDelay;

    private Battleground aiMap;
    private Stack<Coordinate> nextGuesses;
    private AIMode state = AIMode.SCOUT;

    private GapChecker gapChecker = new GapChecker();
    private boolean checkerBoardShift = false;

    private int maxEnemyShipLength = 5;
    private int minEnemyShipLength = 2;

    // The following fields are used to determine the orientation of the enemy ship during ATTACK_ADJACENT
    private Coordinate initialHit;
    private Direction currentDirection = Direction.UNKNOWN;

    private int hits = 0;
    private int miss = 0;

    int[][] placementMemory = new int[10][10]; // remember shots of enemy

    public GuessAI(ArrayList<Module> modules, long decisionDelay) {
        this.decisionDelay = decisionDelay;
        this.aiMap = new Battleground();
        this.modules = modules;

        initGuessStack();

        enemyShips = Util.convertShipList(Util.getDefaultShipTypes());
    }

    private void initGuessStack(){
        nextGuesses = new Stack<>();
        if(modules.contains(Module.CHECKERBOARD) && !checkerBoardShift) {
            initCheckerboard(false);
            checkerBoardShift = true;
        }
        else
            initAllFields();
    }

    public void resetState(){
        this.aiMap = new Battleground();
        initGuessStack();
    }

    public Coordinate getNextGuess() {
        // Delay next guess for better visibility
        try {
            Thread.sleep(decisionDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(modules.contains(Module.CHECKERBOARD) && nextGuesses.empty() & checkerBoardShift){
            initCheckerboard(true);
        }

        if(modules.contains(Module.SPACE_ANALYSIS)&&minEnemyShipLength>2){
            return retrieveGapSuggestion();
        }

        if(!Coordinate.validCoordinate(nextGuesses.peek()))
            nextGuesses.pop();

        while(aiMap.battleground[nextGuesses.peek().getY()][nextGuesses.peek().getX()].equals(Battleground.FieldState.IGNORE))
            nextGuesses.pop();
        aiMap.battleground[nextGuesses.peek().getY()][nextGuesses.peek().getX()] = Battleground.FieldState.IGNORE;
        return nextGuesses.pop();
    }

    private Coordinate retrieveGapSuggestion(){
        Gap gap = gapChecker.suggest(minEnemyShipLength);
        Iterator<Coordinate> i = gap.getCoordinates().iterator();
        Coordinate gapGuess = new Coordinate();
        boolean foundCoordinate = false;
        while(i.hasNext() && !foundCoordinate){
            Coordinate temp = i.next();
            if(!getFieldState(temp).equals(Battleground.FieldState.IGNORE)){
                gapGuess = temp;
                foundCoordinate = true;
            }
        }
        return gapGuess;
    }

    private void initAllFields(){
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x ++) {
                nextGuesses.push(new Coordinate(x, y));
                aiMap.battleground[y][x] = Battleground.FieldState.POTENTIAL;
            }
        }
        Collections.shuffle(nextGuesses);
    }

    private void initCheckerboard(boolean shift) {
        for (int y = 0; y < 10; y++) {
            for (int x = (y + Boolean.compare(shift, false)) % 2; x < 10; x += 2) {
                nextGuesses.push(new Coordinate(x, y));
                aiMap.battleground[y][x] = Battleground.FieldState.POTENTIAL;
            }
        }
        Collections.shuffle(nextGuesses);
    }

    private void updateActiveShips(Ship ship){
        boolean removed = false;
        for(Iterator<ShipType> i = enemyShips.iterator(); i.hasNext() && !removed;){
            if(i.next().getLength() == hits){
                i.remove();
                removed = true;
            }
        }

        //ignoreAdjacentBlockedFields(ship);

        calculateMinEnemyShipLength();
        calculateMaxEnemyShipLength();
    }

    public void onHit(int x, int y) {
        if(modules.contains(Module.HIT_REACTION)) {
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
        // on first miss try other direction if ship has not been sunk
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
        // TODO: Check if further fields have become irrelevant
    }

    public void onSunk(Ship s) {
        updateActiveShips(s);

        System.out.println("AI has been noticed that the ship has been sunk");
        currentDirection = Direction.UNKNOWN;
        stateChange(AIMode.SCOUT);

        hits = 0;
    }

    private void hitDuringScout(int x, int y) {
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

    private void missDuringScout(int x, int y) {
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
    private void hitDuringAttackAdjacent(int x, int y) {
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

    private void missDuringAttackAdjacent(int x, int y) {
        miss++;
    }

    /**
     * Follow up hits after direction is determined
     *
     * @param x X-Coord of attacked field
     * @param y Y-Coord of attacked field
     */
    private void hitDuringAttack(int x, int y) {
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

    private void missDuringAttack(int x, int y) {
        aiMap.battleground[y][x] = Battleground.FieldState.IGNORE;
        directionSwitch();
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

    private void stateChange(AIMode destinationState) {
        System.out.println("AI Mode set to " + destinationState.name());
        state = destinationState;
    }

    private void ignoreAdjacentBlockedFields(Ship ship){
        if(modules.contains(Module.IGNORE_BLOCKED)) {
            System.out.println(currentDirection.name());
            if (currentDirection.equals(Direction.UP) || currentDirection.equals(Direction.DOWN)) {
                // Block field over the ship
                if (ship.yPos > 0) {
                    setIgnore(new Coordinate(ship.xPos, ship.yPos -1));
                }
                // Block fields below the ship
                if (ship.yPos + ship.length <= 9) {
                    setIgnore(new Coordinate(ship.xPos, ship.yPos+ship.length));
                }
                // Block fields covered by the ship
                for (int i = 0; i < ship.length; i++) {
                    setIgnore(new Coordinate(ship.xPos, ship.yPos+i));
                }
                // Block fields left from the ship
                if (ship.xPos > 0) {
                    for (int i = 0; i < ship.length; i++) {
                        setIgnore(new Coordinate(ship.xPos-1, ship.yPos+i));
                    }
                }
                // Block fields right from the ship
                if (ship.xPos < 9) {
                    for (int i = 0; i < ship.length; i++) {
                        setIgnore(new Coordinate(ship.xPos+1, ship.yPos+i));
                    }
                }
            } else {
                // Block fields left from the ship
                if (ship.xPos > 0) {
                    setIgnore(new Coordinate(ship.xPos -1, ship.yPos));
                }
                // Block fields right from the ship
                if (ship.xPos + ship.length <= 9) {
                    setIgnore(new Coordinate(ship.xPos+ship.length, ship.yPos));
                }
                // Block fields covered by the ship
                for (int i = 0; i < ship.length; i++) {
                    setIgnore(new Coordinate(ship.xPos+i, ship.yPos));
                }
                // Block field over the ship
                if (ship.yPos > 0) {
                    for (int i = 0; i < ship.length; i++) {
                        setIgnore(new Coordinate(ship.xPos+i, ship.yPos-1));
                    }
                }
                // Block fields below the ship
                if (ship.yPos < 9) {
                    for (int i = 0; i < ship.length; i++) {
                        setIgnore(new Coordinate(ship.xPos+i,ship.yPos+1));
                    }
                }
            }
        }
    }

    private void calculateMaxEnemyShipLength(){
        int tempMax = 0;
        for(ShipType type : enemyShips){
            if(type.getLength() > tempMax){
                tempMax = type.getLength();
            }
        }
        if(tempMax<maxEnemyShipLength)
            maxEnemyShipLength = tempMax;
    }

    private void calculateMinEnemyShipLength() {
        int tempMin = 5;
        for(ShipType type : enemyShips){
            if(type.getLength() < tempMin){
                tempMin = type.getLength();
            }
        }
        if(tempMin>minEnemyShipLength)
            minEnemyShipLength = tempMin;
    }

    private void setIgnore(Coordinate c){
        aiMap.battleground[c.getY()][c.getX()] = Battleground.FieldState.IGNORE;
        gapChecker.splitGaps(c);
    }

    private Battleground.FieldState getFieldState(Coordinate c){
        return aiMap.battleground[c.getY()][c.getX()];
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

    public String toString(){
        String output="AI MAP";
        output+=System.lineSeparator();
        output+=("    A  B  C  D  E  F  G  H  I  J       ");
        output+=System.lineSeparator();
        for (int y = 0; y < aiMap.battleground.length; y++) {
            output+=(Util.padRight(Integer.toString(y), 3));
            for (int x = 0; x < aiMap.battleground[y].length; x++) {
                if (/*!battleground[y][x].equals(FieldState.SHIP) && !aiMap.battleground[y][x].equals(Battleground.FieldState.BLOCKED)*/ true)
                    output+=("[" + aiMap.battleground[y][x].getSymbol() + "]");
                else
                    output+=("[ ]");
            }
            output+=System.lineSeparator();
        }
        return output;
    }


    public void updatePlacementMemory(Coordinate coordinate) {
        placementMemory[coordinate.getY()][coordinate.getX()] += 1;
    }
}
