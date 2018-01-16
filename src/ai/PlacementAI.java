package ai;

import com.ships.Battleground;
import com.ships.Game;
import com.ships.Ship;
import com.ships.ShipType;

import javax.swing.text.Position;
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
                for (int i = 0; i < shipList.length; i++) {

                    do {
                        newXPos = random.nextInt(battlegroundSize);
                        newYPos = random.nextInt(battlegroundSize);
                        newVerticalRotation = random.nextBoolean();
                        tempShip = new Ship(shipList[i], newXPos, newYPos, newVerticalRotation);
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

    /*private int[][] distanceMatrixPerShip(Ship ship) {

        int[][] distanceMatrix = new int[10][10];

        for (ship.getCoordinates()) {
            for (int yField = 0; yField < distanceMatrix.length; yField++) {
                for (int xField = 0; xField < distanceMatrix[yField].length; xField++) {

                }
            }
        }
        return distanceMatrix;
    }*/


}
