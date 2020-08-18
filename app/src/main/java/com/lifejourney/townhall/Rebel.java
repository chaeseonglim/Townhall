package com.lifejourney.townhall;

public class Rebel extends HostileTribe {

    public Rebel(Event eventHandler, GameMap map, Villager villager) {

        super(eventHandler, Faction.REBEL, map, villager);

        policy = Policy.ASSAULT;
        strategicTarget = villager.getHeadquarterPosition();
    }

    @Override
    protected Unit.UnitClass selectUnitToSpawn(UnitSpawnType spawnType) {
        return null;
    }

    @Override
    protected void decidePolicy() {
    }
}
