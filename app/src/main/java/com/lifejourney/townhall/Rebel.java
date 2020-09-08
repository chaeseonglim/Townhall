package com.lifejourney.townhall;

public class Rebel extends HostileTribe {

    public Rebel(Event eventHandler, GameMap map, Villager villager, Mission mission) {
        super(eventHandler, Faction.REBEL, map, villager, mission);

        policy = Policy.ASSAULT;
        strategicTarget = villager.getHeadquarterPosition();
        retreatable = false;
    }

    @Override
    protected void decidePolicy() {
    }
}
