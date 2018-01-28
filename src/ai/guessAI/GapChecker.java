package ai.guessAI;

import com.ships.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

public class GapChecker {
    private ArrayList<Gap> horizontalGaps, verticalGaps;
    private int maxGapLength;
    private ArrayList<Coordinate> splits;

    public GapChecker() {
        horizontalGaps = new ArrayList<>();
        verticalGaps = new ArrayList<>();
        splits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            horizontalGaps.add(new Gap(new Coordinate(0, i), new Coordinate(9, i)));
            verticalGaps.add(new Gap(new Coordinate(i, 0), new Coordinate(i, 9)));
        }
    }

    public void splitGaps(Coordinate split) {
        splitGapsInternal(split);
    }

    public void splitGapsInternal(Coordinate split) {
        splits.add(split);
        Gap gapUp = new Gap();
        Gap gapDown = new Gap();
        Gap gapLeft = new Gap();
        Gap gapRight = new Gap();
        for (Iterator<Gap> i = horizontalGaps.iterator(); i.hasNext(); ) {
            Gap gap = i.next();
            if (gap.withinGap(split)) {
                if (!gap.isStartpoint(split))
                    gapLeft = new Gap(gap.start, split.delta(-1, 0));
                if (!gap.isEndpoint(split))
                    gapRight = new Gap(split.delta(1, 0), gap.end);
                i.remove();
            }
        }
        for (Iterator<Gap> i = verticalGaps.iterator(); i.hasNext(); ) {
            Gap gap = i.next();
            if (gap.withinGap(split)) {
                if (!gap.isStartpoint(split))
                    gapUp = new Gap(gap.start, split.delta(0, -1));
                if (!gap.isEndpoint(split))
                    gapDown = new Gap(split.delta(0, 1), gap.end);
                i.remove();
            }
        }
        if (Coordinate.validCoordinate(split.delta(-1, 0)) && !(gapLeft.start == null || gapLeft.length() <= 0))
            horizontalGaps.add(sortGap(gapLeft));
        if (Coordinate.validCoordinate(split.delta(1, 0)) && !(gapRight.start == null || gapRight.length() <= 0))
            horizontalGaps.add(sortGap(gapRight));
        horizontalGaps.sort(Comparator.reverseOrder());
        //if (horizontalGaps.get(0).length() > maxGapLength)
        //maxGapLength = horizontalGaps.get(0).length();

        if (Coordinate.validCoordinate(split.delta(0, -1)) && !(gapUp.start == null || gapUp.length() <= 0))
            verticalGaps.add(sortGap(gapUp));
        if (Coordinate.validCoordinate(split.delta(0, 1)) && !(gapDown.start == null || gapDown.length() <= 0))
            verticalGaps.add(sortGap(gapDown));
        verticalGaps.sort(Comparator.reverseOrder());
        //if (verticalGaps.get(0).length() > maxGapLength)
        //maxGapLength = verticalGaps.get(0).length();
    }

    private void updateExistingGaps() {
        for (Coordinate c : getAllOverlappingCoordinates()) {
            splitGaps(c);
        }
    }

    private Coordinate getOverlap(Gap g1, Gap g2) {
        for (Coordinate c1 : g1.getCoordinates()) {
            for (Coordinate c2 : g2.getCoordinates()) {
                if (c1.equals(c2)) {
                    return c1;
                }
            }
        }
        return null;
    }

    public ArrayList<Coordinate> getAllOverlappingCoordinates() {
        ArrayList<Coordinate> overlappingCoordinates = new ArrayList<>();
        List<Gap> horizontal = horizontalGaps.stream().filter(gap -> gap.length() < 10).collect(Collectors.toList());
        List<Gap> vertical = verticalGaps.stream().filter(gap -> gap.length() < 10).collect(Collectors.toList());
        for (Gap hG : horizontal) {
            for (Gap vG : vertical) {
                Coordinate overlap = getOverlap(hG, vG);
                if (overlap != null) {
                    overlappingCoordinates.add(overlap);
                }
            }
        }
        return overlappingCoordinates;
    }

    public Gap suggest(int minShipSize) {
        List<Gap> optionsH = horizontalGaps.stream().filter(gap -> gap.length() >= minShipSize).collect(Collectors.toList());
        List<Gap> optionsV = verticalGaps.stream().filter(gap -> gap.length() >= minShipSize).collect(Collectors.toList());
        Iterator<Gap> i;
        if (new Random().nextBoolean())
            i = optionsH.iterator();
        else
            i = optionsV.iterator();

        if (i.hasNext()) {
            return i.next();
        }
        throw new NoSuchElementException();
    }

    public List<Coordinate> ignoreTooShortGaps(int minShipSize) {
        List<Gap> optionsH = horizontalGaps.stream().filter(gap -> gap.length() < minShipSize).collect(Collectors.toList());
        List<Gap> optionsV = verticalGaps.stream().filter(gap -> gap.length() < minShipSize).collect(Collectors.toList());

        ArrayList<Coordinate> gapCoordinatesH = new ArrayList<>();
        optionsH.forEach(gap -> gapCoordinatesH.addAll(gap.getCoordinates()));
        gapCoordinatesH.sort(Comparator.naturalOrder());
        ArrayList<Coordinate> gapCoordinatesV = new ArrayList<>();
        optionsV.forEach(gap -> gapCoordinatesV.addAll(gap.getCoordinates()));
        gapCoordinatesV.sort(Comparator.naturalOrder());

        ArrayList<Coordinate> duplicates = new ArrayList<>();
        for (Coordinate cH : gapCoordinatesH) {
            for (Coordinate cV : gapCoordinatesV) {
                if (cH.equals(cV)) {
                    duplicates.add(cH);
                }
            }
        }

        duplicates.forEach(coordinate -> splitGaps(coordinate));
        return duplicates;
    }

    public String toString() {
        List<Gap> horizontal = horizontalGaps.stream().filter(gap -> gap.length() < 10).collect(Collectors.toList());
        List<Gap> vertical = verticalGaps.stream().filter(gap -> gap.length() < 10).collect(Collectors.toList());
        String[][] output = new String[10][10];
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                output[y][x] = "[ ]";
            }
        }
        for (Gap hG : horizontal) {
            for (Coordinate hC : hG.getCoordinates()) {
                output[hC.getY()][hC.getX()] = "[-]";
            }
        }
        for (Gap vG : vertical) {
            for (Coordinate vC : vG.getCoordinates()) {
                output[vC.getY()][vC.getX()] = "[I]";
            }
        }

        String out = "H: " + horizontal + System.lineSeparator() + "V: " + vertical + " maxLength = " + maxGapLength + System.lineSeparator();
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                out += output[y][x];
            }
            out += System.lineSeparator();
        }
        return out;
    }

    private Gap sortGap(Gap gap) {
        if (gap.end.compareTo(gap.start) < 0) {
            return new Gap(gap.end, gap.start);
        } else {
            return gap;
        }
    }
}
