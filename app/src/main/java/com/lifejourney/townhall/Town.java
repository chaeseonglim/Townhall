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

    private ArrayList<Squad> squads = new ArrayList<>();
}
