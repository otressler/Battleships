package ai.guessAI;

import com.ships.Coordinate;

import java.util.*;
import java.util.stream.Collectors;

public class GapChecker {
    private ArrayList<Gap> horizontalGaps, verticalGaps;
    private int maxGapLength;

    public GapChecker(){
        horizontalGaps = new ArrayList<>();
        verticalGaps = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            horizontalGaps.add(new Gap(new Coordinate(0, i), new Coordinate(9,i)));
            verticalGaps.add(new Gap(new Coordinate(i,0), new Coordinate(i,9)));
        }
    }

    public void splitGaps(Coordinate split){
        Gap gapUp = new Gap();
        Gap gapDown = new Gap();
        Gap gapLeft = new Gap();
        Gap gapRight = new Gap();
        for(Iterator<Gap> i = horizontalGaps.iterator(); i.hasNext();){
            Gap gap = i.next();
            if(gap.withinGap(split)){
                gapLeft = new Gap(gap.start, split.delta(-1,0));
                gapRight = new Gap(split.delta(1, 0), gap.end);
                i.remove();
            }
        }
        for(Iterator<Gap> i = verticalGaps.iterator(); i.hasNext();){
            Gap gap = i.next();
            if(gap.withinGap(split)){
                gapUp = new Gap(gap.start, split.delta(0,-1));
                gapDown = new Gap(split.delta(0, 1), gap.end);
                i.remove();
            }
        }

        horizontalGaps.add(gapRight);
        horizontalGaps.add(gapLeft);
        Collections.sort(horizontalGaps, Comparator.reverseOrder());
        if(horizontalGaps.get(0).length()<maxGapLength)
            maxGapLength = horizontalGaps.get(0).length();
        verticalGaps.add(gapDown);
        verticalGaps.add(gapUp);
        Collections.sort(verticalGaps, Comparator.reverseOrder());
        if(verticalGaps.get(0).length()<maxGapLength)
            maxGapLength = verticalGaps.get(0).length();
    }

    public Gap suggest(int minShipSize){
        List<Gap> optionsH = horizontalGaps.stream().filter(gap -> gap.length() >= minShipSize).collect(Collectors.toList());
        List<Gap> optionsV = verticalGaps.stream().filter(gap -> gap.length() >= minShipSize).collect(Collectors.toList());
        Iterator<Gap> i;
        if(new Random().nextBoolean())
            i = optionsH.iterator();
        else
            i = optionsV.iterator();

        if(i.hasNext()){
            return i.next();
        }
        throw new NoSuchElementException();
    }
}
