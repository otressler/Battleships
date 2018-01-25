package ai.guessAI;

import com.ships.Coordinate;

import java.util.ArrayList;

public class Gap implements Comparable<Gap>{
    Coordinate start, end;

    public Gap(Coordinate start, Coordinate end){
        this.start = start;
        this.end = end;
    }

    public Gap(){

    }

    public String toString(){
        return (start.toString()+" -> "+end.toString());
    }

    public boolean withinGap(Coordinate coordinate){
        return coordinate.getX() >= start.getX()
                && coordinate.getX() <= end.getX()
                && coordinate.getY() >= start.getY()
                && coordinate.getY() <= end.getY();
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }

    public int length(){
        return Math.max(end.getX()-start.getX(), end.getY()-start.getY());
    }

    @Override
    public int compareTo(Gap o) {
        return Integer.compare(this.length(), o.length());
    }

    public Coordinate middle(){
        if(start.getY() == end.getY())
            return new Coordinate(end.getX()-(length()/2), start.getY());
        else
            return new Coordinate(start.getX(), (end.getY()-length()/2));
    }

    public ArrayList<Coordinate> getCoordinates(){
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        if(start.getY()==end.getY()){
            for(int i = 0; i < length(); i++){
                coordinates.add(start.delta(i, 0));
            }
        } else{
            for(int i = 0; i < length(); i++){
                coordinates.add(start.delta(0, i));
            }
        }
        return coordinates;
    }
}
