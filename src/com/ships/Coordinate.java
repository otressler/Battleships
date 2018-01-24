package com.ships;

public class Coordinate {
    int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinate delta(int deltaX, int deltaY) {
        return new Coordinate(x + deltaX, y + deltaY);
    }

    public static boolean validCoordinate(Coordinate c){
        if(c.getX() < 0 || c.getX() > 9 || c.getY() < 0 || c.getY() > 9){
            return false;
        }
        else{
            return true;
        }
    }

    public String toString(){
        return x+"|"+y;
    }

    public boolean equals(Coordinate c){
        if(c.x == this.x && c.y == this.y)
            return true;
        else
            return false;
    }

    public static boolean validCoordinate(int x, int y){
        if(x < 0 || x > 9 || y < 0 || y > 9){
            return false;
        }
        else{
            return true;
        }
    }
}
