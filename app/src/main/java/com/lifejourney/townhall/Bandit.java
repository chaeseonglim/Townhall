package com.lifejourney.townhall;

public class Bandit extends Tribe {

    public Bandit(Event eventHandler, GameMap map) {

        super(eventHandler, Town.Faction.BANDIT, map);

        spawnSquad(getHeadquarterPosition().toGameCoord(), getFaction(),
                Unit.UnitClass.SWORDMAN, Unit.UnitClass.LONGBOWMAN, Unit.UnitClass.LONGBOWMAN);
    }

    /**
     *
     */
    @Override
    void update() {

    }
}
