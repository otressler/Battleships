package ai;

import com.ships.Battleground;
import com.ships.Game;
import com.ships.Ship;
import com.ships.ShipType;

import java.util.ArrayList;
import java.util.Random;

public class PlacementAI {
    ArrayList<Ship> ships;
    Battleground bg;

    public PlacementAI(Game game) {
        bg = new Battleground(game);
        ships = new ArrayList<>();
    }

    public Battleground placeShips(ShipType[] shipList, String positionType) {

        int newXPos, newYPos;
        int battlegroundSize = bg.battleground.length;
        Ship tempShip;
        Random random = new Random();

        switch (positionType) {
            case "sparse": // high distance between ships
                break;
            case "dense": // low distance between ships
                break;
            default: // random distance between ships
                for (int i = 0; i < shipList.length; i++) {

                    do {
                        newXPos = random.nextInt(battlegroundSize);
                        newYPos = random.nextInt(battlegroundSize);
                        tempShip = new Ship(shipList[i], newXPos, newYPos, true);
                    } while (bg.checkForBlockedFields(tempShip));

                    Ship s = tempShip;
                    ships.add(s);
                    bg.placeShip(s);
                }
                break;
        }



        return bg;
    }


}
