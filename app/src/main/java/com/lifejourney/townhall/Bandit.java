package com.lifejourney.townhall;

public class Bandit extends Tribe {

    public Bandit(Event eventHandler, GameMap map) {

        super(eventHandler, Tribe.Faction.BANDIT, map);

        spawnSquad(getHeadquarterPosition().toGameCoord(), getFaction(),
                Unit.UnitClass.SWORD_MAN, Unit.UnitClass.LONGBOW_ARCHER, Unit.UnitClass.LONGBOW_ARCHER);
    }

    /**
     *
     */
    @Override
    void update() {

    }
}
