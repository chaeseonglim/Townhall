package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;

public class Bandit extends Tribe {

    public Bandit(Event eventHandler, GameMap map) {

        super(eventHandler, Faction.RAIDER, map);

        //spawnSquad(getHeadquarterPosition().toGameCoord(), getFaction(),
        //        Unit.UnitClass.SWORD_MAN, Unit.UnitClass.LONGBOW_ARCHER, Unit.UnitClass.LONGBOW_ARCHER);
        spawnSquad(new OffsetCoord(9, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        spawnSquad(new OffsetCoord(10, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.CANNON);
    }
}
