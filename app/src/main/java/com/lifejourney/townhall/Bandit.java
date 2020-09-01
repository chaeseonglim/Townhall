package com.lifejourney.townhall;

public class Bandit extends HostileTribe {

    private static final String LOG_TAG = "Bandit";

    public Bandit(Event eventHandler, GameMap map, Villager villager, Mission mission) {
        super(eventHandler, Faction.BANDIT, map, villager, mission);
    }
}
