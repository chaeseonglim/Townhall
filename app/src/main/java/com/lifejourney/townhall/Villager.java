package com.lifejourney.townhall;

import android.util.Log;

public class Villager extends Tribe {

    private static final String LOG_TAG = "Villager";

    public Villager(Squad.Event squadListener, GameMap map) {

        super(Town.Faction.VILLAGER, squadListener, map);
    }

    @Override
    public void update() {

        if (collectUpdateTimeLeft-- > 0) {
            return;
        }

        maxPopulation = 0;
        happiness = 0;
        for (Town town: getTowns()) {
            gold += town.collectTax();
            maxPopulation += town.collectPopulation();
            happiness += town.getHappiness();
        }
        happiness /= getTowns().size();

        //Log.i(LOG_TAG, "Villager " + getFaction().toString() + " gold: " + gold +
        //        " max population: " + maxPopulation + " happiness: " + happiness);

        collectUpdateTimeLeft = COLLECT_UPDATE_TIME;
    }

    /**
     *
     * @return
     */
    public int getMaxPopulation() {
        return maxPopulation;
    }

    /**
     *
     * @return
     */
    public int getPopulation() {
        return population;
    }

    /**
     *
     * @return
     */
    public int getGold() {
        return gold;
    }

    /**
     *
     * @return
     */
    public int getHappiness() {
        return happiness;
    }


    private final static int COLLECT_UPDATE_TIME = 60;

    private int collectUpdateTimeLeft = COLLECT_UPDATE_TIME;
    private int maxPopulation = 0;
    private int population = 0;
    private int gold = 0;
    private int happiness = 50;
}
