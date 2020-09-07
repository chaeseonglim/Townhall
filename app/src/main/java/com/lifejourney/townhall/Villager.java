package com.lifejourney.townhall;

public class Villager extends Tribe {

    private static final String LOG_TAG = "Villager";

    public Villager(Tribe.Event eventHandler, GameMap map, int startingGold) {
        super(eventHandler, Faction.VILLAGER, map);

        this.gold = startingGold;
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
        if (getTerritories().size() > 0) {
            for (Territory territory : getTerritories()) {
                income += (float) territory.getTax() *
                        (1.0f + SHRINE_FACTOR * getShrineBonus(ShrineBonus.PROSPERITY));
                totalPopulation += territory.getPopulation() *
                        (1.0f + SHRINE_FACTOR * getShrineBonus(ShrineBonus.LOVE));
                happiness += territory.getHappiness();
            }
            happiness /= getTerritories().size();
        }

        // Pay upkeep
        spend = 0;
        workingPopulation = 0;
        for (Squad squad : getSquads()) {
            spend += squad.getUpkeep();
            workingPopulation += squad.getPopulation();
        }
        spend += Upgradable.getTotalUpkeep(getFaction());
        idlePopulation = totalPopulation - workingPopulation;
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
        this.idlePopulation -= population;
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
                getIdlePopulation() + replacementPopulation >= unitClass.population());
    }

    /**
     *
     * @param upgradable
     * @return
     */
    public boolean isAffordable(Upgradable upgradable) {

        return (getGold() >= upgradable.getPurchaseCost());
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
    public int getWorkingPopulation() {

        return workingPopulation;
    }

    /**
     *
     * @return
     */
    public int getIdlePopulation() {

        return idlePopulation;
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
    private final static float SHRINE_FACTOR = 0.2f;

    private int collectTimeLeft = 1;
    private int totalPopulation = 0;
    private int workingPopulation = 0;
    private int idlePopulation = 0;
    private int gold;
    private int income = 0;
    private int spend = 0;
    private int happiness = 50;
}
