package ai;

import com.ships.Battleground;
import com.ships.Game;
import com.ships.Ship;
import com.ships.ShipType;

import java.util.ArrayList;

public class PlacementAI {
    ArrayList<Ship> ships;
    Battleground bg;

    public PlacementAI(Game game) {
        bg = new Battleground(game);
        ships = new ArrayList<>();
    }

    public Battleground placeShips(ShipType[] shipList) {
        for (int i = 0; i < shipList.length; i++) {
            Ship s = new Ship(shipList[i], i * 2, 0, true);
            ships.add(s);
            bg.placeShip(s);
        }
        return bg;
    }


}
