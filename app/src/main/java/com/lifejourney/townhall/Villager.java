package com.lifejourney.townhall;

public class Villager extends Tribe {

    private static final String LOG_TAG = "Villager";

    public Villager(Tribe.Event eventHandler, GameMap map) {

        super(eventHandler, Town.Faction.VILLAGER, map);
    }

    @Override
    public void update() {

        if (collectTimeLeft-- > 0) {
            return;
        }

        // Collect taxes
        population = 0;
        happiness = 0;
        for (Town town: getTowns()) {
            gold += town.getTax();
            population += town.getPopulation();
            happiness += town.getHappiness();
        }
        happiness /= getTowns().size();

        // Pay upkeep
        for (Squad squad: getSquads()) {
            gold -= squad.getUpkeepGold();
            population -= squad.getPopulation();
        }

        getEventHandler().onTribeCollected(this);

        //Log.i(LOG_TAG, "Villager " + getFaction().toString() + " gold: " + gold +
        //        " max population: " + maxPopulation + " happiness: " + happiness);

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
