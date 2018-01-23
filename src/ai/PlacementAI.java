package ai;

import com.ships.*;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Integer.min;

public class PlacementAI {
    ArrayList<Ship> ships;
    Battleground bg;
    PositionStrategy strategy;

    public PlacementAI(Game game, PositionStrategy strategy) {
        bg = new Battleground(game);
        ships = new ArrayList<>();
        this.strategy = strategy;
    }

    public Battleground placeShips(ShipType[] shipList) {
        switch (strategy) {
            case SPARSE: // high distance between ships
                return placeSparse(shipList);
            case DENSE: // low distance between ships
                return placeDense(shipList);
            case RANDOM: // random distance between ships
                return placeRandomly(shipList);
            default:
                return placeRandomly(shipList);
        }
    }

    private Battleground placeDense(ShipType[] shipList) {
        int[][] distanceMatrix;

        bg.placeShip(suggestRandomPlacement(shipList[0]));
        distanceMatrix = calculateDistanceMatrix(bg.ships.get(0));

        for (int i = 1; i < shipList.length; i++) {
            bg.placeShip(suggestDensePlacement(distanceMatrix, shipList[i], 100));

            // TODO : Have a look at mergeMatrices for refreshDistanceMatrix. Currently this does not come up with good results for dense placement
            //distanceMatrix = refreshDistanceMatrix(bg.ships.get(i), distanceMatrix);
        }

        return bg;
    }

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

    public Battleground placeRandomly(ShipType[] shipList) {
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

    public Battleground placeSparse(ShipType[] shipList) {
        int[][] distanceMatrix;

        bg.placeShip(suggestRandomPlacement(shipList[0]));
        distanceMatrix = calculateDistanceMatrix(bg.ships.get(0));

        for (int i = 1; i < shipList.length; i++) {
            bg.placeShip(suggestSparsePlacement(distanceMatrix, shipList[i], 100));

            distanceMatrix = refreshDistanceMatrix(bg.ships.get(i), distanceMatrix);
        }

        return bg;
    }

    public Ship suggestSparsePlacement(int[][] distanceMatrix, ShipType shipType, int tries) {
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

    public enum PositionStrategy {
        SPARSE, DENSE, RANDOM
    }

    private int[][] calculateDistanceMatrix(Ship s) {
        return distanceMatrixPerShip(s);
    }

    private int[][] refreshDistanceMatrix(Ship s, int[][] distanceMatrix) {
        return mergeMatrices(distanceMatrix, distanceMatrixPerShip(s));
    }

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

    private int[][] distanceMatrixPerShipPart(Coordinate shipPart) {
        int[][] singleDistanceMatrix = new int[10][10];

        for (int yField = 0; yField < singleDistanceMatrix.length; yField++) {
            for (int xField = 0; xField < singleDistanceMatrix[yField].length; xField++) {
                singleDistanceMatrix[yField][xField] = euclideanDistance(shipPart.getX(), shipPart.getY(), xField, yField);
            }
        }

        return singleDistanceMatrix;
    }

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

    private int euclideanDistance(int xShip, int yShip, int xField, int yField) {
        return (int) (Math.sqrt((xShip - xField) * (xShip - xField) + (yShip - yField) * (yShip - yField)));
    }

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
}
