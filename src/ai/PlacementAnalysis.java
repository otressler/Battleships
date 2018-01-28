package ai;

import com.ships.Coordinate;
import com.ships.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.min;

public class PlacementAnalysis {
    private int[][] distanceMatrix;
    private int denseValue, slidingWindow;
    private ArrayList<Integer> oldDenseValues;

    public PlacementAnalysis(int slidingWindow) {
        oldDenseValues = new ArrayList<>();

        this.slidingWindow = slidingWindow;
    }

    public PlacementAI.PositionStrategy guessPlacementStrategy() {
        int avgValue = 0;
        for (int i = oldDenseValues.size() - 1; i >= 0 & i > oldDenseValues.size() - slidingWindow; i--) {
            avgValue += (oldDenseValues.get(i));
        }
        if(Math.min(oldDenseValues.size(), slidingWindow)>0)
            avgValue /= Math.min(oldDenseValues.size(), slidingWindow);
        else
            avgValue /= 1;
        if (avgValue < 30) {
            return PlacementAI.PositionStrategy.DENSE;
        } else if (avgValue > 45) {
            return PlacementAI.PositionStrategy.SPARSE;
        } else {
            return PlacementAI.PositionStrategy.RANDOM;
        }
    }

    public int calculateDenseValue(ArrayList<Ship> s) {
        denseValue = 0;
        List<Ship> ships = s.stream().distinct().collect(Collectors.toList());
        distanceMatrix = calculateDistanceMatrix(ships.get(0));
        for (int i = 1; i < ships.size(); i++) {
            ships.get(i).getCoordinates().forEach(coordinate -> denseValue += distanceMatrix[coordinate.getY()][coordinate.getX()]);
            distanceMatrix = refreshDistanceMatrix(ships.get(i), distanceMatrix);
        }
        oldDenseValues.add(denseValue);
        return denseValue;
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
}
