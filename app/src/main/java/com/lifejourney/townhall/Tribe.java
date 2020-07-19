package com.lifejourney.townhall;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;

import java.util.ArrayList;

public abstract class Tribe implements Squad.Event {

    private static final String LOG_TAG = "Tribe";

    public interface Event extends Squad.Event {

        void onTribeCollected(Tribe tribe);
    }


    public Tribe(Event eventHandler, Town.Faction faction, GameMap map) {

        this.eventHandler = eventHandler;
        this.faction = faction;
        this.map = map;
        this.towns = map.getTownsBySide(faction);
        for (Town town: towns) {
            if (town.getTerrain() == Town.Terrain.HEADQUARTER_GRASS) {
                this.headquarterPosition = town.getMapCoord();
            }
        }
    }

    /**
     *
     */
    abstract void update();

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
     * @param oldMapCoord
     * @param newMapCoord
     */
    @Override
    public void onSquadMoved(Squad squad, OffsetCoord oldMapCoord, OffsetCoord newMapCoord) {

        eventHandler.onSquadMoved(squad, oldMapCoord, newMapCoord);
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
    public Squad spawnSquad(PointF position, Town.Faction faction, Unit.UnitClass... unitClass) {

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
    public Town.Faction getFaction() {

        return faction;
    }

    /**
     *
     * @return
     */
    public ArrayList<Town> getTowns() {

        return towns;
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

    private Event eventHandler;
    private Town.Faction faction;
    private GameMap map;
    private OffsetCoord headquarterPosition;
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Town> towns;
}
