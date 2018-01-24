package ai;

import com.ships.Coordinate;

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
        if(this.length()<o.length())
            return -1;
        else if(this.length()==o.length())
            return 0;
        else
            return 1;
    }

    public Coordinate middle(){
        if(start.getY() == end.getY())
            return new Coordinate(end.getX()-(length()/2), start.getY());
        else
            return new Coordinate(start.getX(), (end.getY()-length()/2));
    }
}
