package com.lifejourney.townhall;

public class Bandit extends HostileTribe {

    private static final String LOG_TAG = "Bandit";

    public Bandit(Event eventHandler, GameMap map, Villager villager) {
        super(eventHandler, Faction.BANDIT, map, villager);

        /*
        spawnSquad(new OffsetCoord(9, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        spawnSquad(new OffsetCoord(10, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.CANNON);
         */
    }
}
