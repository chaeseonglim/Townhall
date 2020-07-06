package com.lifejourney.townhall;

import android.util.Log;

import com.lifejourney.engine2d.OffsetCoord;

import java.util.ArrayList;

public class Towner extends Tribe {

    private static final String LOG_TAG = "Towner";

    public Towner(Squad.Event squadListener, TownMap map) {

        super(Town.Side.TOWNER, squadListener, map);

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

        ArrayList<Town> towns = getMap().getTowns();

        maxPopulation = 0;
        for (Town town: towns) {
            if (town.getSide() == getSide()) {
                gold += town.collectTax();
                maxPopulation += town.collectPopulation();
            }
        }

        Log.i(LOG_TAG, "Towner " + getSide().toString() + " gold: " + gold +
                " max population: " + maxPopulation);

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


    private final static int COLLECT_UPDATE_TIME = 60;

    private int collectUpdateTimeLeft = COLLECT_UPDATE_TIME;
    private int maxPopulation;
    private int population;
    private int gold;
}
