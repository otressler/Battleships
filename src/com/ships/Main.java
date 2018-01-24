package com.ships;

import ai.GapChecker;

public class Main {

    public static void main(String[] args) {
        //new Game();
        GapChecker gapChecker = new GapChecker();
        gapChecker.splitGaps(new Coordinate(4,4));
        System.out.println(gapChecker.suggest(5).toString());
        gapChecker.suggest(3);
    }
}
