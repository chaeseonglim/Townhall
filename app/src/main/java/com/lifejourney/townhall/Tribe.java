package com.lifejourney.townhall;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Tribe implements Squad.Event {

    private static final String LOG_TAG = "Tribe";

    enum Faction {
        NEUTRAL,
        VILLAGER,
        BANDIT,
        VIKING,
        REBEL;

        String toGameString() {
            switch(this) {
                case NEUTRAL:
                    return Engine2D.GetInstance().getString(R.string.neutral);
                case VILLAGER:
                    return Engine2D.GetInstance().getString(R.string.villager);
                case BANDIT:
                    return Engine2D.GetInstance().getString(R.string.bandit);
                case VIKING:
                    return Engine2D.GetInstance().getString(R.string.viking);
                case REBEL:
                    return Engine2D.GetInstance().getString(R.string.rebel);
                default:
                    return "";
            }
        }
    }

    enum ShrineBonus {
        WIND,
        HEAL,
        PROSPERITY,
        LOVE;
    }

    public interface Event extends Squad.Event {
        void onTribeCollected(Tribe tribe);
        void onTribeUpgraded(Tribe tribe, Upgradable upgradable);
        void onTribeDefeated(Tribe tribe);
    }


    public Tribe(Event eventHandler, Faction faction, GameMap map) {
        this.eventHandler = eventHandler;
        this.faction = faction;
        this.map = map;
        this.territories = map.getTerritoriesBySide(faction);
        for (Territory territory : territories) {
            if (territory.getTerrain() == Territory.Terrain.HEADQUARTER) {
                this.headquarterPosition = territory.getMapPosition();
            }
        }
        for (Territory territory : map.getTerritories()) {
            if (territory.getTerrain() == Territory.Terrain.SHRINE_HEAL ||
                    territory.getTerrain() == Territory.Terrain.SHRINE_LOVE ||
                    territory.getTerrain() == Territory.Terrain.SHRINE_PROSPER ||
                    territory.getTerrain() == Territory.Terrain.SHRINE_WIND) {
                this.shrinePositions.add(territory.getMapPosition());
            }
        }
        Arrays.fill(this.shrineBonuses, 0);
    }

    /**
     *
     */
    public void update() {
        // Check win/defeat condition
        if (checkDefeated())
            return;

        // Set squad bonus
        for (Squad squad : squads) {
            squad.setShrineBonus(ShrineBonus.WIND, getShrineBonus(ShrineBonus.WIND));
            squad.setShrineBonus(ShrineBonus.HEAL, getShrineBonus(ShrineBonus.HEAL));
            squad.setShrineBonus(ShrineBonus.LOVE, getShrineBonus(ShrineBonus.LOVE));
            squad.setShrineBonus(ShrineBonus.PROSPERITY, getShrineBonus(ShrineBonus.PROSPERITY));
        }
    }

    /**
     *
     * @return
     */
    public boolean checkDefeated() {
        if (!isDefeated() && getSquads().size() == 0 &&
                (getHeadquarterPosition() == null ||
                (getHeadquarterPosition() != null &&
                getMap().getTerritory(getHeadquarterPosition()).getFaction() != getFaction()))) {
            defeated = true;

            // Remove territories
            ArrayList<Territory> territoriesCopy = new ArrayList<>(territories);
            for (Territory territory: territoriesCopy) {
                territory.setFaction(Faction.NEUTRAL);
            }

            getEventHandler().onTribeDefeated(this);
        }

        return defeated;
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadCreated(Squad squad) {
        eventHandler.onSquadCreated(squad);
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadDestroyed(Squad squad) {
        eventHandler.onSquadDestroyed(squad);
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadFocused(Squad squad) {
        eventHandler.onSquadFocused(squad);
    }

    /**
     *
     * @param squad
     * @param prevMapPosition
     * @param newMapPosition
     */
    @Override
    public void onSquadMoved(Squad squad, OffsetCoord prevMapPosition, OffsetCoord newMapPosition) {
        eventHandler.onSquadMoved(squad, prevMapPosition, newMapPosition);
    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitAdded(Squad squad, Unit unit) {
        eventHandler.onSquadUnitAdded(squad, unit);

    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitRemoved(Squad squad, Unit unit) {
        eventHandler.onSquadUnitRemoved(squad, unit);
    }

    /**
     *
     * @param position
     * @param unitClass
     */
    public Squad spawnSquad(PointF position, Unit.UnitClass... unitClass) {
        Squad squad = new Squad.Builder(this, position, map, getFaction()).build();
        squad.setRetretable(retreatable);
        if (unitClass.length >= 1) {
            squad.spawnUnit(unitClass[0]);
        }
        if (unitClass.length >= 2) {
            squad.spawnUnit(unitClass[1]);
        }
        if (unitClass.length >= 3) {
            squad.spawnUnit(unitClass[2]);
        }
        squad.show();
        squads.add(squad);

        return squad;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getHeadquarterPosition() {
        return headquarterPosition;
    }

    /**
     *
     * @return
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     *
     * @return
     */
    public ArrayList<Territory> getTerritories() {
        return territories;
    }

    /**
     *
     * @return
     */
    public ArrayList<Squad> getSquads() {
        return squads;
    }

    /**
     *
     * @return
     */
    protected Event getEventHandler() {
        return eventHandler;
    }

    /**
     *
     * @param factor
     * @return
     */
    public int getShrineBonus(ShrineBonus factor) {
        return shrineBonuses[factor.ordinal()];
    }

    /**
     *
     * @param factor
     * @param value
     */
    public void setShrineBonus(ShrineBonus factor, int value) {
        shrineBonuses[factor.ordinal()] = value;
    }

    /**
     *
     * @param factor
     * @param value
     */
    public void addShrineBonus(ShrineBonus factor, int value) {
        shrineBonuses[factor.ordinal()] += value;
    }

    /**
     *
     * @return
     */
    public GameMap getMap() {
        return map;
    }

    /**
     *
     * @return
     */
    public ArrayList<OffsetCoord> getShrinePositions() {
        return shrinePositions;
    }

    /**
     *
     * @return
     */
    public float getTotalBattleMetric() {
        float totalBattleMetric = 0.0f;
        for (Squad squad: getSquads()) {
            totalBattleMetric += squad.getBattleMetric();
        }
        return totalBattleMetric;
    }

    /**
     *
     * @return
     */
    public boolean isDefeated() {
        return defeated;
    }

    /**
     *
     * @return
     */
    public boolean isRetreatable() {
        return retreatable;
    }

    /**
     *
     */
    public void setRetretable(boolean retreatable) {
        this.retreatable = retreatable;

        for (Squad squad: getSquads()) {
            squad.setRetretable(retreatable);
        }
    }
    private Event eventHandler;
    private Faction faction;
    private GameMap map;
    private OffsetCoord headquarterPosition;
    private ArrayList<OffsetCoord> shrinePositions = new ArrayList<>();
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Territory> territories;
    private int[] shrineBonuses = new int[ShrineBonus.values().length];
    protected boolean defeated = false;
    protected boolean retreatable = true;
}
