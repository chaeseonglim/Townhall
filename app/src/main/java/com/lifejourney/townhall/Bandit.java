package com.lifejourney.townhall;

public class Bandit extends Tribe {

    public Bandit(Squad.Event squadListener, GameMap map) {

        super(Town.Faction.BANDIT, squadListener, map);

        spawnSquad(getHeadquarterCoord().toGameCoord(), getFaction(),
                Unit.UnitClass.SWORD, Unit.UnitClass.LONGBOW, Unit.UnitClass.LONGBOW);
    }

    /**
     *
     */
    @Override
    void update() {

    }
}
