package com.lifejourney.townhall;

import android.util.Log;

import com.lifejourney.engine2d.OffsetCoord;

public class Villager extends Tribe {

    private static final String LOG_TAG = "Villager";

    public Villager(Squad.Event squadListener, GameMap map) {

        super(Town.Side.VILLAGER, squadListener, map);

        this.maxPopulation = 0;
        this.population = 0;
        this.gold = 0;

        spawnSquad(getHeadquarterCoord().toGameCoord(), getSide(),
                Unit.UnitClass.SWORD, Unit.UnitClass.LONGBOW, Unit.UnitClass.LONGBOW);

        OffsetCoord coord = getHeadquarterCoord().clone();
        coord.offset(1, 0);
        spawnSquad(coord.toGameCoord(), getSide(),
                Unit.UnitClass.SWORD, Unit.UnitClass.LONGBOW, Unit.UnitClass.LONGBOW);
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
            happiness += town.collectHappiness();
        }
        happiness /= getTowns().size();

        Log.i(LOG_TAG, "Villager " + getSide().toString() + " gold: " + gold +
                " max population: " + maxPopulation + " happiness: " + happiness);

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
    private int maxPopulation;
    private int population;
    private int gold;
    private int happiness;
}
