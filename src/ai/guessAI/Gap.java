package ai.guessAI;

import com.ships.Coordinate;

import java.util.ArrayList;

/**
 * Used by the GapChecker. Gaps are horizontal or vertical sequences of fields, that do not contain fields set to IGNORE
 * or HIT.
 */
public class Gap implements Comparable<Gap> {
    Coordinate start, end;

    /**
     * Defines a Gap
     * @param start left- and uppermost point
     * @param end lower- and rightmost point
     */
    public Gap(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Default constructor
     */
    public Gap() {

    }


    /**
     * Util method to check whether the given coordinate is at the end of a gap.
     * @param c Coordinate
     * @return true, if it is the endpoint, false if it is not
     */
    public boolean isEndpoint(Coordinate c) {
        return this.end.equals(c);
    }

    /**
     * Util method to check whether the given coordinate is at the beginning of a gap.
     * @param c Coordinate
     * @return true, if it is the startpoint, false if it is not
     */
    public boolean isStartpoint(Coordinate c) {
        return this.start.equals(c);
    }

    /**
     * Used for printing gaps (debug purpose)
     * @return Coordinte -> Coordinate
     */
    public String toString() {
        return (start.toString() + " -> " + end.toString());
    }

    /**
     * Checks if a given coordinate is within the borders of a gap.
     * @param coordinate Coordinate
     * @return true if it is within the borders, false if it is not.
     */
    public boolean withinGap(Coordinate coordinate) {
        return coordinate.getX() >= start.getX()
                && coordinate.getX() <= end.getX()
                && coordinate.getY() >= start.getY()
                && coordinate.getY() <= end.getY();
    }

    /**
     * Returns starting point of the gap
     * @return Coordinate
     */
    public Coordinate getStart() {
        return start;
    }

    /**
     * Returns endpoint of the gap
     * @return Coordinate
     */
    public Coordinate getEnd() {
        return end;
    }

    /**
     * Returns the length of a gap
     * @return length
     */
    public int length() {
        return Math.max(end.getX() - start.getX() + 1, end.getY() - start.getY() + 1);
    }

    /**
     * Used to compare to gaps regarding their size
     * @param o other gap
     * @return positive if this is greater than o, negative if not, 0 if equal
     */
    @Override
    public int compareTo(Gap o) {
        return Integer.compare(this.length(), o.length());
    }

    /**
     * Returns the middle point of the gap
     * @return Coordinate
     */
    public Coordinate middle() {
        if (start.getY() == end.getY())
            return new Coordinate(end.getX() - (length() / 2), start.getY());
        else
            return new Coordinate(start.getX(), (end.getY() - length() / 2));
    }

    /**
     * Returns all coordinates within the borders of this gap
     * @return ArrayList of Coordinates
     */
    public ArrayList<Coordinate> getCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        if (start.getY() == end.getY()) {
            for (int i = 0; i < length(); i++) {
                coordinates.add(start.delta(i, 0));
            }
        } else {
            for (int i = 0; i < length(); i++) {
                coordinates.add(start.delta(0, i));
            }
        }
        return coordinates;
    }

    /**
     * Checks whether this gap is null
     * @return true if start and end Coordinates are null
     */
    public boolean isNullGap() {
        return start == null || end == null;
    }
}
