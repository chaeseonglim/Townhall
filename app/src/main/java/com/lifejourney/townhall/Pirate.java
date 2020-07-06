package com.lifejourney.townhall;

public class Pirate extends Tribe {

    public Pirate(Squad.Event squadListener, TownMap map) {

        super(Town.Side.PIRATE, squadListener, map);
    }

    /**
     *
     */
    @Override
    void update() {

    }
}
