package com.lifejourney.townhall;

public class Villager extends Tribe {

    private static final String LOG_TAG = "Villager";

    public Villager(Tribe.Event eventHandler, GameMap map) {

        super(eventHandler, Faction.VILLAGER, map);

        spawnSquad(getHeadquarterPosition().toGameCoord(), getFaction(),
                Unit.UnitClass.HORSE_MAN, Unit.UnitClass.CANNON, Unit.UnitClass.HEALER);
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
        population = 0;
        happiness = 0;
        float tax = 0.0f;
        for (Town town : getTowns()) {
            tax += town.getTax() *
                    (1.0f + getGlobalFactor(GlobalBonusFactor.TOWN_GOLD_BOOST));
            population += town.getPopulation() *
                    (1.0f + getGlobalFactor(GlobalBonusFactor.TOWN_POPULATION_BOOST));
            happiness += town.getHappiness();
        }
        gold += tax;
        happiness /= getTowns().size();

        // Pay upkeep
        for (Squad squad : getSquads()) {
            gold -= squad.getUpkeepGold();
            population -= squad.getPopulation();
        }

        /*
        Log.i(LOG_TAG, "Villager " + getFaction().toString() + " gold: " + gold + "(" + tax + ")" +
                " population: " + population + " happiness: " + happiness);
        */

        getEventHandler().onTribeCollected(this);

        collectTimeLeft = COLLECT_UPDATE_TIME;
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
        this.population -= population;
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
                getPopulation() + replacementPopulation >= unitClass.population());
    }

    private final static int COLLECT_UPDATE_TIME = 60;
    private final static int STARTING_GOLD = 250;

    private int collectTimeLeft = 1;
    private int population = 0;
    private int gold = STARTING_GOLD;
    private int happiness = 50;
}
