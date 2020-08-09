package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;

public class Viking extends HostileTribe {

    public Viking(Event eventHandler, GameMap map, Villager villager) {

        super(eventHandler, Faction.VIKING, map, villager);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean checkDefeated() {
        if (!destroyed &&
                getMap().getTerritory(getHeadquarterPosition()).getFaction() != getFaction()) {
            destroyed = true;
            for (Squad squad: getSquads()) {
                if (squad.isFighting()) {
                    squad.eliminate();
                } else {
                    squad.close();
                }
            }
            getEventHandler().onTribeDestroyed(this);
        }

        return destroyed;
    }
}
