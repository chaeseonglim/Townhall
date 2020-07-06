package com.lifejourney.townhall;

public class Rebel extends Tribe {

    public Rebel(Squad.Event squadListener, TownMap map) {

        super(Town.Side.TOWNER, squadListener, map);
    }

    /**
     *
     */
    @Override
    void update() {

    }
}
