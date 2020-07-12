package com.lifejourney.townhall;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;

import java.util.ArrayList;

public abstract class Tribe implements Squad.Event {

    private static final String LOG_TAG = "Tribe";

    public Tribe(Town.Side side, Squad.Event squadListener, GameMap map) {

        this.side = side;
        this.map = map;
        this.squadListener = squadListener;
        this.towns = map.getTownsBySide(side);
        for (Town town: towns) {
            if (town.getType() == Town.Type.HEADQUARTER) {
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
    public void onSquadUnitSpawned(Squad squad, Unit unit) {

        squadListener.onSquadUnitSpawned(squad, unit);

    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitKilled(Squad squad, Unit unit) {

        squadListener.onSquadUnitKilled(squad, unit);
    }

    /**
     *
     * @param position
     * @param side
     * @param unitClass
     */
    public void spawnSquad(PointF position, Town.Side side, Unit.UnitClass... unitClass) {

        Squad squad = new Squad.Builder(this, position, map, side).build();
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
    public Town.Side getSide() {

        return side;
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
    public ArrayList<Town> getTowns() {

        return towns;
    }

    private Town.Side side;
    private Squad.Event squadListener;
    private GameMap map;
    private OffsetCoord headquarterCoord;
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Town> towns = new ArrayList<>();
}
