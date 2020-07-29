package com.lifejourney.townhall;

public class Villager extends Tribe {

    private static final String LOG_TAG = "Villager";

    public Villager(Tribe.Event eventHandler, GameMap map) {

        super(eventHandler, Faction.VILLAGER, map);

        spawnSquad(getHeadquarterPosition().toGameCoord(), getFaction(),
                Unit.UnitClass.HORSE_MAN, Unit.UnitClass.CANNON, Unit.UnitClass.PALADIN);
    }

    @Override
    public void update() {

        super.update();

        collect();
    }

    /**
     *
     */
    private void collect() {

        if (collectTimeLeft-- > 0) {
            return;
        }

        // Collect resources
        income = 0;
        totalPopulation = 0;
        happiness = 0;
        for (Town town : getTowns()) {
            income += (float)town.getTax() *
                    (1.0f + getGlobalFactor(GlobalBonusFactor.TOWN_GOLD_BOOST));
            totalPopulation += town.getPopulation() *
                    (1.0f + getGlobalFactor(GlobalBonusFactor.TOWN_POPULATION_BOOST));
            happiness += town.getHappiness();
        }
        happiness /= getTowns().size();

        // Pay upkeep
        spend = 0;
        usingPopulation = 0;
        for (Squad squad : getSquads()) {
            spend += squad.getUpkeep();
            usingPopulation += squad.getPopulation();
        }
        usablePopulation = totalPopulation - usingPopulation;
        gold += (income - spend);

        /*
        Log.i(LOG_TAG, "Villager " + getFaction().toString() + " gold: " + gold + "(" + tax + ")" +
                " population: " + population + " happiness: " + happiness);
        */

        getEventHandler().onTribeCollected(this);

        collectTimeLeft = COLLECT_UPDATE_TIME;
    }

    /**
     *
     * @param gold
     */
    public void pay(int gold) {

        this.gold -= gold;
    }

    /**
     *
     * @param gold
     * @param population
     */
    public void pay(int gold, int population) {

        this.gold -= gold;
        this.usablePopulation -= population;
    }

    /**
     *
     * @param unitClass
     * @return
     */
    public boolean isAffordable(Unit.UnitClass unitClass, Unit.UnitClass replacementClass) {

        int replacementPopulation = 0;
        if (replacementClass != null) {
            replacementPopulation = replacementClass.population();
        }
        return (getGold() >= unitClass.costToPurchase() &&
                getUsablePopulation() + replacementPopulation >= unitClass.population());
    }

    /**
     *
     * @return
     */
    public int getTotalPopulation() {

        return totalPopulation;
    }

    /**
     *
     * @return
     */
    public int getUsingPopulation() {

        return usingPopulation;
    }

    /**
     *
     * @return
     */
    public int getUsablePopulation() {

        return usablePopulation;
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
    public int getIncome() {

        return income;
    }

    /**
     *
     * @return
     */
    public int getSpend() {

        return spend;
    }

    /**
     *
     * @return
     */
    public int getHappiness() {

        return happiness;
    }


    private final static int COLLECT_UPDATE_TIME = 60;
    private final static int STARTING_GOLD = 250;

    private int collectTimeLeft = 1;
    private int totalPopulation = 0;
    private int usingPopulation = 0;
    private int usablePopulation = 0;
    private int gold = STARTING_GOLD;
    private int income = 0;
    private int spend = 0;
    private int happiness = 50;
}
