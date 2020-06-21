package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;

import java.util.ArrayList;

public class Town {

    enum Side {
        TOWN,
        BANDIT,
        PIRATE,
        REBEL,
        NEUTRAL
    }

    public Town(OffsetCoord mapCoord) {
        this.mapCoord = mapCoord;
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
     * @param squads
     */
    public void setSquads(ArrayList<Squad> squads) {
        this.squads = squads;
    }

    /**
     *
     * @param squad
     */
    public void addSquad(Squad squad) {
        this.squads.add(squad);
    }

    /**
     *
     * @param squad
     */
    public void removeSquad(Squad squad) {
        this.squads.remove(squad);
    }

    /**
     *
     * @return
     */
    public Battle getBattle() {
        return battle;
    }

    /**
     *
     * @param battle
     */
    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getMapCoord() {
        return mapCoord;
    }

    /**
     *
     * @param mapCoord
     */
    public void setMapCoord(OffsetCoord mapCoord) {
        this.mapCoord = mapCoord;
    }

    private Battle battle;
    private ArrayList<Squad> squads = new ArrayList<>();
    private OffsetCoord mapCoord;
}
