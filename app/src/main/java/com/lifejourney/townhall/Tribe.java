package com.lifejourney.townhall;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;

import java.util.ArrayList;

public abstract class Tribe implements Squad.Event {

    private static final String LOG_TAG = "Tribe";

    public Tribe(Town.Faction faction, Squad.Event squadListener, GameMap map) {

        this.faction = faction;
        this.map = map;
        this.squadListener = squadListener;
        this.towns = map.getTownsBySide(faction);
        for (Town town: towns) {
            if (town.getTerrain() == Town.Terrain.HEADQUARTER_GRASS) {
                this.headquarterCoord = town.getMapCoord();
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

        squadListener.onSquadCreated(squad);
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadDestroyed(Squad squad) {

        squadListener.onSquadDestroyed(squad);
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadFocused(Squad squad) {

        squadListener.onSquadFocused(squad);
    }

    /**
     *
     * @param squad
     * @param oldMapCoord
     * @param newMapCoord
     */
    @Override
    public void onSquadMoved(Squad squad, OffsetCoord oldMapCoord, OffsetCoord newMapCoord) {

        squadListener.onSquadMoved(squad, oldMapCoord, newMapCoord);
    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitAdded(Squad squad, Unit unit) {

        squadListener.onSquadUnitAdded(squad, unit);

    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitRemoved(Squad squad, Unit unit) {

        squadListener.onSquadUnitRemoved(squad, unit);
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
    public OffsetCoord getHeadquarterCoord() {

        return headquarterCoord;
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

    private Town.Faction faction;
    private Squad.Event squadListener;
    private GameMap map;
    private OffsetCoord headquarterCoord;
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Town> towns = new ArrayList<>();
}
