package com.lifejourney.townhall;

import java.util.ArrayList;

public class Town {

    enum Side {
        TOWN,
        BANDIT,
        PIRATE,
        REBEL,
        NEUTRAL
    }

    public Town() {

    }

    public ArrayList<Squad> getSquads() {
        return squads;
    }

    public void setSquads(ArrayList<Squad> squads) {
        this.squads = squads;
    }

    public void addSquad(Squad squad) {
        this.squads.add(squad);
    }

    public void removeSquad(Squad squad) {
        this.squads.remove(squad);
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    private Battle battle;
    private ArrayList<Squad> squads = new ArrayList<>();
}
