package ai;

import com.ships.Battleground;
import com.ships.Coordinate;
import com.ships.Ship;
import com.ships.ShipType;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Integer.min;

/**
 * Suggests placement of ships
 */
public class PlacementAI {
    int tries;
    private ArrayList<Ship> ships;
    private Battleground bg;
    private PositionStrategy strategy;
    private int[][] guessMemory = new int[10][10]; // remember shots of enemy

    /**
     * Constructor for defining Placement strategy
     * @param strategy DENSE; SPARSE; RANDOM; MEMORY
     * @param patience How many tries before choosing best strategy? Suggested value is around 100.
     */
    public PlacementAI(PositionStrategy strategy, int patience) {
        bg = new Battleground();
        ships = new ArrayList<>();
        this.strategy = strategy;
        this.tries = patience;
    }

    /**
     * Resets variables that are not supposed to last for longer than the duration of a game
     */
    public void resetState() {
        bg = new Battleground();
        ships = new ArrayList<>();
    }

    /**
     * Places ships depending on defined strategy
     * @param shipList What ships are in play?
     * @return Field with ships placed
     */
    public Battleground placeShips(ShipType[] shipList) {
        switch (strategy) {
            case SPARSE: // high distance between ships
                return placeSparse(shipList);
            case DENSE: // low distance between ships
                return placeDense(shipList);
            case RANDOM: // random distance between ships
                return placeRandomly(shipList);
            case MEMORY:
                return placeMemory(shipList);
            default:
                return placeRandomly(shipList);
        }
    }

    /**
     * Suggests a dense placement
     * @param shipList What ships are in play?
     * @return Field with ships placed
     */
    private Battleground placeDense(ShipType[] shipList) {
        int[][] distanceMatrix;

        bg.placeShip(suggestRandomPlacement(shipList[0]));
        distanceMatrix = calculateDistanceMatrix(bg.ships.get(0));

        for (int i = 1; i < shipList.length; i++) {
            bg.placeShip(suggestDensePlacement(distanceMatrix, shipList[i], tries));
        }

        return bg;
    }

    /**
     * Suggests a placement for one ship that corresponds to the dense tactic
     * @param distanceMatrix current distanceMatrix for dense value calculations
     * @param shipType ship to be placed
     * @param tries patience
     * @return Ship with x and y coordinates that can be used for placement
     */
    private Ship suggestDensePlacement(int[][] distanceMatrix, ShipType shipType, int tries) {
        int bestScore = -1;
        Ship bestShip = new Ship(ShipType.MINESWEEPER, -1, -1, true);
        for (int i = 0; i < tries; i++) {
            Ship temp;
            do {
                temp = suggestRandomPlacement(shipType);
            } while (bg.checkForBlockedFields(temp));

            int tempScore = 0;
            for (Coordinate coordinate : temp.getCoordinates()) {
                tempScore += distanceMatrix[coordinate.getY()][coordinate.getX()];
            }

            if (bestScore < 0) {
                bestShip = temp;
                bestScore = tempScore;
            } else {
                if (tempScore < bestScore) {
                    bestShip = temp;
                    bestScore = tempScore;
                }
            }
        }

        return bestShip;
    }

    /**
     * Suggests random placement
     * @param shipList Ships in play
     * @return Field with ships placed
     */
    private Battleground placeRandomly(ShipType[] shipList) {
        Ship tempShip;
        for (ShipType type : shipList) {
            do {
                tempShip = suggestRandomPlacement(type);
            } while (bg.checkForBlockedFields(tempShip));

            Ship s = tempShip;
            ships.add(s);
            bg.placeShip(s);
        }
        return bg;
    }

    /**
     * Suggests a sparse placement
     * @param shipList Ships in play
     * @return Field with ships placed
     */
    private Battleground placeSparse(ShipType[] shipList) {
        int[][] distanceMatrix;

        bg.placeShip(suggestRandomPlacement(shipList[0]));
        distanceMatrix = calculateDistanceMatrix(bg.ships.get(0));

        for (int i = 1; i < shipList.length; i++) {
            bg.placeShip(suggestSparsePlacement(distanceMatrix, shipList[i], tries));

            distanceMatrix = refreshDistanceMatrix(bg.ships.get(i), distanceMatrix);
        }

        return bg;
    }

    /**
     * Suggests a placement for one ship that corresponds to the sparse tactic
     * @param distanceMatrix current distanceMatrix for dense value calculations
     * @param shipType ship to be placed
     * @param tries patience
     * @return Ship with x and y coordinates that can be used for placement
     */
    private Ship suggestSparsePlacement(int[][] distanceMatrix, ShipType shipType, int tries) {
        int bestScore = -1;
        Ship bestShip = new Ship(ShipType.MINESWEEPER, -1, -1, true);
        for (int i = 0; i < tries; i++) {
            Ship temp;
            do {
                temp = suggestRandomPlacement(shipType);
            } while (bg.checkForBlockedFields(temp));

            int tempScore = 0;
            for (Coordinate coordinate : temp.getCoordinates()) {
                tempScore += distanceMatrix[coordinate.getY()][coordinate.getX()];
            }

            if (bestScore < 0) {
                bestShip = temp;
                bestScore = tempScore;
            } else {
                if (tempScore > bestScore) {
                    bestShip = temp;
                    bestScore = tempScore;
                }
            }
        }

        return bestShip;
    }

    /**
     * Suggests placement based on memory
     * @param shipList Ships to be placed
     * @return Field with ships placed
     */
    private Battleground placeMemory(ShipType[] shipList) {

        bg.placeShip(suggestRandomPlacement(shipList[0]));

        for (int i = 1; i < shipList.length; i++) {
            bg.placeShip(suggestMemoryPlacement(shipList[i], tries));
        }

        return bg;
    }

    /**
     * Suggest a placement for one ship based on memory
     * @param shipType ship to be placed
     * @param tries patience
     * @return Ship with x and y coordinates that can be used for placement
     */
    private Ship suggestMemoryPlacement(ShipType shipType, int tries) {
        int bestScore = -1;
        Ship bestShip = new Ship(ShipType.MINESWEEPER, -1, -1, true);
        for (int i = 0; i < tries; i++) {
            Ship temp;
            do {
                temp = suggestRandomPlacement(shipType);
            } while (bg.checkForBlockedFields(temp));

            int tempScore = 0;
            for (Coordinate coordinate : temp.getCoordinates()) {
                tempScore += guessMemory[coordinate.getY()][coordinate.getX()];
            }

            if (bestScore < 0) {
                bestShip = temp;
                bestScore = tempScore;
            } else {
                if (tempScore < bestScore) {
                    bestShip = temp;
                    bestScore = tempScore;
                }
            }
        }

        return bestShip;
    }

    /**
     * Used for calculating the complete distanceMatrix after first ship has been placed
     * @param s
     * @return
     */
    private int[][] calculateDistanceMatrix(Ship s) {
        return distanceMatrixPerShip(s);
    }

    /**
     * Updates an existing distanceMatrix. Call when placing second ship and after.
     * @param s Ship to be placed. Calculations will be done for this ship.
     * @param distanceMatrix Existing distanceMatrix
     * @return Updated distanceMatrix that includes ship s
     */
    private int[][] refreshDistanceMatrix(Ship s, int[][] distanceMatrix) {
        return mergeMatrices(distanceMatrix, distanceMatrixPerShip(s));
    }

    /**
     * Util method for merging two distance matrices
     * @param m1 Matrix 1
     * @param m2 Matrix 2
     * @return DistanceMatrix with min values for each field
     */
    private int[][] mergeMatrices(int[][] m1, int[][] m2) {
        int[][] temp = new int[10][10];
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                temp[y][x] = min(m1[y][x], m2[y][x]);
                //temp[y][x] = (m1[y][x] - m2[y][x]);
            }
        }
        return temp;
    }

    /**
     * Calculates the distance matrix for one ship
     * @param ship Ship
     * @return DistanceMatrix
     */
    private int[][] distanceMatrixPerShip(Ship ship) {
        ArrayList<int[][]> distanceMatrices = new ArrayList<>();
        int[][] distanceMatrix = new int[10][10];

        // generate euclidean distance matrix for each coordinate of a ship
        for (Coordinate shipPart : ship.getCoordinates()) {
            distanceMatrices.add(distanceMatrixPerShipPart(shipPart));
        }

        // set fields to max value of all matrices
        for (int yField = 0; yField < distanceMatrix.length; yField++) {
            for (int xField = 0; xField < distanceMatrix[yField].length; xField++) {
                distanceMatrix[yField][xField] = findMinimumDistanceAmongMatrices(xField, yField, distanceMatrices);
            }
        }

        /*
        // set fields to min value of all matrices
        for (int[][] distanceMatrix: distanceMatrices) {
            for (int yField = 0; yField < distanceMatrix.length; yField++) {
                for (int xField = 0; xField < distanceMatrix[yField].length; xField++) {
                    distanceMatrix[yField][xField] = Math.min(distanceMatrix[yField][xField], distanceMatrix[yField][xField]);
                }
            }
        }
        */
        return distanceMatrix;
    }

    /**
     * Calculates the distanceMatrix for each ship part
     * @param shipPart Coordinate of a ship
     * @return DistanceMatrix
     */
    private int[][] distanceMatrixPerShipPart(Coordinate shipPart) {
        int[][] singleDistanceMatrix = new int[10][10];

        for (int yField = 0; yField < singleDistanceMatrix.length; yField++) {
            for (int xField = 0; xField < singleDistanceMatrix[yField].length; xField++) {
                singleDistanceMatrix[yField][xField] = euclideanDistance(shipPart.getX(), shipPart.getY(), xField, yField);
            }
        }

        return singleDistanceMatrix;
    }

    /**
     * Util method for merging on field of multiple matrices
     * @param x x-Coordinate
     * @param y y-Coordinate
     * @param matrices List of matrices
     * @return minimum value of that field
     */
    private int findMinimumDistanceAmongMatrices(int x, int y, ArrayList<int[][]> matrices) {
        int min = -1;
        for (int[][] matrix : matrices) {
            if (min >= 0)
                min = min(matrix[y][x], min);
            else
                min = matrix[y][x];
        }
        return min;
    }

    /**
     * Calculates the euclidean distance between a ship coordinate and a field coordinate
     * @param xShip
     * @param yShip
     * @param xField
     * @param yField
     * @return distance
     */
    private int euclideanDistance(int xShip, int yShip, int xField, int yField) {
        return (int) (Math.sqrt((xShip - xField) * (xShip - xField) + (yShip - yField) * (yShip - yField)));
    }

    /**
     * Suggests a random placement for one ship
     * @param type Ship to be placed
     * @return Ship with x and y coordinate ready for placement
     */
    private Ship suggestRandomPlacement(ShipType type) {
        int newXPos, newYPos;
        boolean newVerticalRotation;
        Random random = new Random();

        // Start with random placement of battleship
        newVerticalRotation = random.nextBoolean();
        if (newVerticalRotation) {
            newXPos = random.nextInt(10);
            newYPos = random.nextInt(10 - type.getLength() + 1);
        } else {
            newXPos = random.nextInt(10 - type.getLength() + 1);
            newYPos = random.nextInt(10);
        }
        return new Ship(type, newXPos, newYPos, newVerticalRotation);
    }

    /**
     * Updates on what fields the enemy shoots most.
     * @param coordinate Field that has been targeted by the enemy
     */
    public void updateGuessMemory(Coordinate coordinate) {
        guessMemory[coordinate.getY()][coordinate.getX()] += 1;
    }

    /**
     * Defines the possible position strategies
     */
    public enum PositionStrategy {
        SPARSE, DENSE, RANDOM, MEMORY
    }
}
