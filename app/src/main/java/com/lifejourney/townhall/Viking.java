package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;

public class Viking extends HostileTribe {

    public Viking(Event eventHandler, GameMap map, Villager villager, Mission mission) {
        super(eventHandler, Faction.VIKING, map, villager, mission);
    }

    /**
     *
     * @return
     */
    public boolean checkDefeated() {
        if (!isDefeated() &&
                (getHeadquarterPosition() == null ||
                        (getHeadquarterPosition() != null &&
                                getMap().getTerritory(getHeadquarterPosition()).getFaction() != getFaction()))) {
            defeated = true;

            // Remove territories
            ArrayList<Territory> territoriesCopy = new ArrayList<>(getTerritories());
            for (Territory territory: territoriesCopy) {
                territory.setFaction(Faction.NEUTRAL);
            }

            getEventHandler().onTribeDefeated(this);
        }

        return defeated;
    }
}
