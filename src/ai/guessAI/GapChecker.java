package ai.guessAI;

import com.ships.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

public class GapChecker {
    private ArrayList<Gap> horizontalGaps, verticalGaps;
    private int maxGapLength;

    public GapChecker() {
        horizontalGaps = new ArrayList<>();
        verticalGaps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            horizontalGaps.add(new Gap(new Coordinate(0, i), new Coordinate(9, i)));
            verticalGaps.add(new Gap(new Coordinate(i, 0), new Coordinate(i, 9)));
        }
    }

    public void splitGaps(Coordinate split) {
        Gap gapUp = new Gap();
        Gap gapDown = new Gap();
        Gap gapLeft = new Gap();
        Gap gapRight = new Gap();
        for (Iterator<Gap> i = horizontalGaps.iterator(); i.hasNext(); ) {
            Gap gap = i.next();
            if (gap.withinGap(split)) {
                gapLeft = new Gap(gap.start, split.delta(-1, 0));
                gapRight = new Gap(split.delta(1, 0), gap.end);
                i.remove();
                break;
            }
        }
        for (Iterator<Gap> i = verticalGaps.iterator(); i.hasNext(); ) {
            Gap gap = i.next();
            if (gap.withinGap(split)) {
                gapUp = new Gap(gap.start, split.delta(0, -1));
                gapDown = new Gap(split.delta(0, 1), gap.end);
                i.remove();
                break;
            }
        }
        if (Coordinate.validCoordinate(split.delta(-1, 0)) && !(gapLeft.start == null || gapLeft.length() <= 0))
            horizontalGaps.add(sortGap(gapLeft));
        if (Coordinate.validCoordinate(split.delta(1, 0)) && !(gapRight.start == null || gapRight.length() <= 0))
            horizontalGaps.add(sortGap(gapRight));
        horizontalGaps.sort(Comparator.reverseOrder());
        if (horizontalGaps.get(0).length() > maxGapLength)
            maxGapLength = horizontalGaps.get(0).length();

        if (Coordinate.validCoordinate(split.delta(0, -1)) && !(gapUp.start == null || gapUp.length() <= 0))
            verticalGaps.add(sortGap(gapUp));
        if (Coordinate.validCoordinate(split.delta(0, 1)) && !(gapDown.start == null || gapDown.length() <= 0))
            verticalGaps.add(sortGap(gapDown));
        verticalGaps.sort(Comparator.reverseOrder());
        if (verticalGaps.get(0).length() > maxGapLength)
            maxGapLength = verticalGaps.get(0).length();
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
        for(Coordinate cH : gapCoordinatesH){
            for(Coordinate cV : gapCoordinatesV){
                if(cH.equals(cV)){
                    duplicates.add(cH);
                }
            }
        }
        return duplicates;
    }

    public String toString() {
        String horizontal = horizontalGaps.stream().filter(gap -> gap.length() < 10).collect(Collectors.toList()).toString();
        String vertical = verticalGaps.stream().filter(gap -> gap.length() < 10).collect(Collectors.toList()).toString();
        return "H: " + horizontal + System.lineSeparator() + "V: " + vertical + " maxLength = " + maxGapLength;
    }

    private Gap sortGap(Gap gap) {
        if (gap.end.compareTo(gap.start) < 0) {
            return new Gap(gap.end, gap.start);
        } else {
            return gap;
        }
    }
}
