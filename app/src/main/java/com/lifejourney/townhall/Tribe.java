package com.lifejourney.townhall;

public class Tribe {

    public Tribe(Town.Side side, TownMap map) {
        this.side = side;
        this.map = map;
        this.maxPopulation = 0;
        this.population = 0;
        this.gold = 0;
    }

    /**
     *
     */
    void update() {

    }

    public int getMaxPopulation() {
        return maxPopulation;
    }

    public int getPopulation() {
        return population;
    }

    public int getGold() {
        return gold;
    }

    Town.Side side;
    TownMap map;
    int maxPopulation;
    int population;
    int gold;
}
