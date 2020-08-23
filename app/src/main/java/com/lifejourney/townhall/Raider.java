package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;

public class Raider extends HostileTribe {

    private static final String LOG_TAG = "Raider";

    public Raider(Event eventHandler, GameMap map, Villager villager) {
        super(eventHandler, Faction.RAIDER, map, villager);

        /*
        spawnSquad(new OffsetCoord(9, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        spawnSquad(new OffsetCoord(10, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.CANNON);
         */
    }
}
