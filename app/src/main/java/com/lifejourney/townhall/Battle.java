package com.lifejourney.townhall;

import java.util.ArrayList;

public class Battle {

    public Battle(Squad a, Squad b) {

        a.enterBattle(b);
        b.enterBattle(a);
        fighters.add(a);
        fighters.add(b);
    }

    public void update() {

    }

    private ArrayList<Squad> fighters = new ArrayList<>();
}
