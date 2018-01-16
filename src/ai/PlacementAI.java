package ai;

import com.ships.*;

import java.util.ArrayList;
import java.util.Random;

public class PlacementAI {
    ArrayList<Ship> ships;
    Battleground bg;

    public PlacementAI(Game game) {
        bg = new Battleground(game);
        ships = new ArrayList<>();
    }

    public Battleground placeShips(ShipType[] shipList, PositionStrategy strategy) {

        int newXPos, newYPos;
        boolean newVerticalRotation;
        int battlegroundSize = bg.battleground.length - 1;
        int[][] distanceBattlefield = new int[battlegroundSize][battlegroundSize];
        Ship tempShip;
        Random random = new Random();

        switch (strategy) {
            case SPARSE: // high distance between ships
                break;
            case DENSE: // low distance between ships
                break;
            case RANDOM: // random distance between ships
                for (ShipType type : shipList) {

                    do {
                        newVerticalRotation = random.nextBoolean();
                        if(newVerticalRotation) {
                            newXPos = random.nextInt(battlegroundSize);
                            newYPos = random.nextInt(battlegroundSize - type.getLength());
                        } else{
                            newXPos = random.nextInt(battlegroundSize - type.getLength());
                            newYPos = random.nextInt(battlegroundSize);
                        }
                        tempShip = new Ship(type, newXPos, newYPos, newVerticalRotation);
                    } while (bg.checkForBlockedFields(tempShip));

                    Ship s = tempShip;
                    ships.add(s);
                    bg.placeShip(s);
                }
                break;
        }



        return bg;
    }

    public enum PositionStrategy {
        SPARSE, DENSE, RANDOM
    }

    private int[][] distanceMatrixPerShip(Ship ship) {

        ArrayList<int[][]> distanceMatrices = new ArrayList<>();
        int[][] fullDistanceMatrix = new int[10][10];

        // generate euclidean distance matrix for each coordinate of a ship
        for (Coordinate coordinates: ship.getCoordinates()) {

            int xShip = coordinates.getX();
            int yShip = coordinates.getY();
            int[][] singleDistanceMatrix = new int[10][10];

            for (int yField = 0; yField < singleDistanceMatrix.length; yField++) {
                for (int xField = 0; xField < singleDistanceMatrix[yField].length; xField++) {
                    singleDistanceMatrix[yField][xField] = (int)(Math.sqrt((xShip - xField) * (xShip - xField) + (yShip - yField) * (yShip - yField)));
                }
            }

            distanceMatrices.add(singleDistanceMatrix);

        }

        // generate basic fullDistanceMatrix with high values in every field
        for (int yField = 0; yField < fullDistanceMatrix.length; yField++) {
            for (int xField = 0; xField < fullDistanceMatrix[yField].length; xField++) {
                fullDistanceMatrix[yField][xField] = 100;
            }
        }

        // set fields to min value of all matrices
        for (int[][] distanceMatrix: distanceMatrices) {
            for (int yField = 0; yField < distanceMatrix.length; yField++) {
                for (int xField = 0; xField < distanceMatrix[yField].length; xField++) {
                    fullDistanceMatrix[yField][xField] = Math.min(distanceMatrix[yField][xField], fullDistanceMatrix[yField][xField]);
                }
            }
        }

        return fullDistanceMatrix;
    }


}
