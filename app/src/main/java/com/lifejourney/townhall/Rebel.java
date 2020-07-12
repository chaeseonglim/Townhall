package com.lifejourney.townhall;

public class Rebel extends Tribe {

    public Rebel(Squad.Event squadListener, GameMap map) {

        super(Town.Side.VILLAGER, squadListener, map);
    }

    /**
     *
     */
    @Override
    void update() {

    }
}
