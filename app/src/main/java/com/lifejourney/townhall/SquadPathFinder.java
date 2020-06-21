package com.lifejourney.townhall;

import android.util.Log;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PathFinder;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;

public class SquadPathFinder extends PathFinder {

    private static final String LOG_TAG = "SquadPathFinder";

    public SquadPathFinder(Squad squad, OffsetCoord targetOffset) {

        OffsetCoord startOffset = new OffsetCoord(squad.getPosition());
        Point start = new Point(startOffset.getX(), startOffset.getY());
        Point target = new Point(targetOffset.getX(), targetOffset.getY());
        set(start, target);
        this.squad = squad;
    }

    /**
     *
     * @param waypoint
     * @return
     */
    @Override
    protected ArrayList<Waypoint> getNeighborWaypoints(Waypoint waypoint) {
        ArrayList<Waypoint> neighbors = new ArrayList<>();
        ArrayList<OffsetCoord> neighborCoords =
                new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y).getNeighbors();
        for (OffsetCoord coord: neighborCoords) {
            neighbors.add(new Waypoint(new Point(coord.getX(), coord.getY()), waypoint,
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

        OffsetCoord targetOffset = new OffsetCoord(target.x, target.y);
        return squad.getMap().isMovable(targetOffset, squad);
    }

    /**
     *
     * @param waypoint
     * @param target
     * @return
     */
    @Override
    protected float calculateCostToTarget(Waypoint waypoint, Point target) {

        OffsetCoord currentOffset =
                new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y);
        OffsetCoord targetOffset = new OffsetCoord(target.x, target.y);
        return currentOffset.getDistance(targetOffset);
    }

    private Squad squad;
}
