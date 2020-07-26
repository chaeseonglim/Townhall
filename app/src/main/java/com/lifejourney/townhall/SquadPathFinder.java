package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PathFinder;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;

public class SquadPathFinder extends PathFinder {

    private static final String LOG_TAG = "SquadPathFinder";

    public SquadPathFinder(Squad squad, OffsetCoord targetMapCoord, boolean useNextCoord) {

        OffsetCoord startMapCoord = (useNextCoord)?squad.getNextMapPositionToMove():
                squad.getMapPosition();
        Point start = new Point(startMapCoord.getX(), startMapCoord.getY());
        Point target = new Point(targetMapCoord.getX(), targetMapCoord.getY());
        set(start, target);
        this.squad = squad;
        this.useNextCoord = useNextCoord;
    }

    /**
     *
     * @return
     */
    @Override
    public ArrayList<Waypoint> findOptimalPath() {
        ArrayList<Waypoint> optimalPath = super.findOptimalPath();
        if (useNextCoord) {
            OffsetCoord squadMapCoord = squad.getMapPosition();
            optimalPath.add(0,
                    new Waypoint(new Point(squadMapCoord.getX(), squadMapCoord.getY()),
                            null, 0.0f));
        }

        return optimalPath;
    }

    /**
     *
     * @param waypoint
     * @return
     */
    @Override
    protected ArrayList<Waypoint> getNeighborWaypoints(Waypoint waypoint) {

        ArrayList<Waypoint> neighbors = new ArrayList<>();
        ArrayList<OffsetCoord> neighborMapCoord =
                new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y).getNeighbors();
        for (OffsetCoord mapCoord: neighborMapCoord) {
            neighbors.add(new Waypoint(new Point(mapCoord.getX(), mapCoord.getY()), waypoint,
                    waypoint.getCostFromStart() + 1));
        }
        return neighbors;
    }

    /**
     *
     * @param current
     * @param target
     * @return
     */
    @Override
    protected boolean isMovable(Point current, Point target) {

        OffsetCoord targetMapCoord = new OffsetCoord(target.x, target.y);
        return squad.getMap().isMovable(targetMapCoord, squad);
    }

    /**
     *
     * @param waypoint
     * @param target
     * @return
     */
    @Override
    protected float calculateCostToTarget(Waypoint waypoint, Point target) {

        OffsetCoord currentMapCoord =
                new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y);
        OffsetCoord targetMapCoord = new OffsetCoord(target.x, target.y);
        return currentMapCoord.getDistance(targetMapCoord);
    }

    private Squad squad;
    private boolean useNextCoord;
}
