package com.lifejourney.townhall;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Tribe implements Squad.Event {

    private static final String LOG_TAG = "Tribe";

    enum Faction {
        NEUTRAL,
        VILLAGER,
        RAIDER,
        VIKING,
        REBEL;

        String toGameString() {
            switch(this) {
                case NEUTRAL:
                    return "중립";
                case VILLAGER:
                    return "원주민";
                case RAIDER:
                    return "정복자";
                case VIKING:
                    return "바이킹";
                case REBEL:
                    return "반란군";
                default:
                    return "";
            }
        }
    }

    enum ShrineBonus {
        UNIT_ATTACK_SPEED,
        UNIT_HEAL_POWER,
        TOWN_GOLD_BOOST,
        TOWN_POPULATION_BOOST;
    }

    public interface Event extends Squad.Event {

        void onTribeCollected(Tribe tribe);
    }


    public Tribe(Event eventHandler, Faction faction, GameMap map) {

        this.eventHandler = eventHandler;
        this.faction = faction;
        this.map = map;
        this.territories = map.getTownsBySide(faction);
        for (Territory territory : territories) {
            if (territory.getTerrain() == Territory.Terrain.HEADQUARTER) {
                this.headquarterPosition = territory.getMapPosition();
            } else if (territory.getTerrain() == Territory.Terrain.SHRINE_HEAL ||
                    territory.getTerrain() == Territory.Terrain.SHRINE_LOVE ||
                    territory.getTerrain() == Territory.Terrain.SHRINE_PROSPER ||
                    territory.getTerrain() == Territory.Terrain.SHRINE_WIND) {
                this.shrinePositions.add(territory.getMapPosition());
            }
        }
        Arrays.fill(this.globalFactors, 0.0f);
    }

    /**
     *
     */
    public void update() {

        // Set squad bonus
        for (Squad squad : squads) {
            squad.setShrineBonus(ShrineBonus.UNIT_ATTACK_SPEED,
                    getShrineBonus(ShrineBonus.UNIT_ATTACK_SPEED));
            squad.setShrineBonus(ShrineBonus.UNIT_HEAL_POWER,
                    getShrineBonus(ShrineBonus.UNIT_HEAL_POWER));
        }
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
     * @param faction
     * @param unitClass
     */
    public Squad spawnSquad(PointF position, Faction faction, Unit.UnitClass... unitClass) {

        Squad squad = new Squad.Builder(this, position, map, faction).build();
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
    public float getShrineBonus(ShrineBonus factor) {

        return globalFactors[factor.ordinal()];
    }

    /**
     *
     * @param factor
     * @param value
     */
    public void setGlobalFactor(ShrineBonus factor, float value) {

        globalFactors[factor.ordinal()] = value;
    }

    /**
     *
     * @param factor
     * @param value
     */
    public void addGlobalFactor(ShrineBonus factor, float value) {

        globalFactors[factor.ordinal()] += value;
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

    private Event eventHandler;
    private Faction faction;
    private GameMap map;
    private OffsetCoord headquarterPosition;
    private ArrayList<OffsetCoord> shrinePositions = new ArrayList<>();
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Territory> territories;
    private float[] globalFactors = new float[ShrineBonus.values().length];
}
