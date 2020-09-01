package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;

public class Viking extends HostileTribe {

    public Viking(Event eventHandler, GameMap map, Villager villager, Mission mission) {
        super(eventHandler, Faction.VIKING, map, villager, mission);
    }
}
