package com.lifejourney.townhall;

public class Bandit extends Tribe {

    public Bandit(Squad.Event squadListener, TownMap map) {

        super(Town.Side.BANDIT, squadListener, map);

        spawnSquad(getHeadquarterCoord().toGameCoord(), getSide(),
                Unit.UnitClass.SWORD, Unit.UnitClass.LONGBOW, Unit.UnitClass.LONGBOW);
    }

    /**
     *
     */
    @Override
    void update() {

    }
}
