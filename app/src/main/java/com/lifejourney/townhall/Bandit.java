package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;

public class Bandit extends Tribe {

    public Bandit(Event eventHandler, GameMap map) {

        super(eventHandler, Tribe.Faction.BANDIT, map);

        //spawnSquad(getHeadquarterPosition().toGameCoord(), getFaction(),
        //        Unit.UnitClass.SWORD_MAN, Unit.UnitClass.LONGBOW_ARCHER, Unit.UnitClass.LONGBOW_ARCHER);
        spawnSquad(new OffsetCoord(9, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.SWORD_MAN, Unit.UnitClass.LONGBOW_ARCHER, Unit.UnitClass.LONGBOW_ARCHER);
        spawnSquad(new OffsetCoord(10, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.LONGBOW_ARCHER, Unit.UnitClass.LONGBOW_ARCHER, Unit.UnitClass.HEALER);
    }

    /**
     *
     */
    @Override
    void update() {

    }
}
