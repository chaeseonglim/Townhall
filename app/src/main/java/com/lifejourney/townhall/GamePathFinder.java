package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PathFinder;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;

public class GamePathFinder extends PathFinder {

    private static final String LOG_TAG = "GamePathFinder";

    public GamePathFinder(Squad squad, OffsetCoord targetMapPosition, boolean useNextMapPositionOfSquad) {

        this.map = squad.getMap();
        this.squad = squad;
        this.useNextMapPositionOfSquad = useNextMapPositionOfSquad;

        OffsetCoord startMapPosition = (useNextMapPositionOfSquad) ?
                squad.getNextMapPositionToMove() : squad.getMapPosition();
        Point start = new Point(startMapPosition.getX(), startMapPosition.getY());
        Point target = new Point(targetMapPosition.getX(), targetMapPosition.getY());
        set(start, target);
    }

    public GamePathFinder(OffsetCoord startMapPosition, OffsetCoord targetMapPosition, GameMap map, Tribe.Faction faction) {

        this.map = map;
        this.faction = faction;

        Point start = new Point(startMapPosition.getX(), startMapPosition.getY());
        Point target = new Point(targetMapPosition.getX(), targetMapPosition.getY());
        set(start, target);
    }

    /**
     *
     * @return
     */
    @Override
    public ArrayList<Waypoint> findOptimalPath() {
        ArrayList<Waypoint> optimalPath = super.findOptimalPath();
        if (optimalPath != null && useNextMapPositionOfSquad) {
            OffsetCoord squadMapPosition = squad.getMapPosition();
            optimalPath.add(0,
                    new Waypoint(new Point(squadMapPosition.getX(), squadMapPosition.getY()),
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
        ArrayList<OffsetCoord> neighborMapPositions =
                new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y).getNeighbors(1);
        for (OffsetCoord neighborMapPosition: neighborMapPositions) {
            neighbors.add(new Waypoint(new Point(neighborMapPosition.getX(), neighborMapPosition.getY()),
                    waypoint,waypoint.getCostFromStart() + 1));
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

        OffsetCoord targetMapPosition = new OffsetCoord(target.x, target.y);
        if (squad == null) {
            return map.isTerrainMovable(targetMapPosition, faction);
        } else {
            return map.isTerritoryMovable(targetMapPosition, squad);
        }
    }

    /**
     *
     * @param waypoint
     * @param target
     * @return
     */
    @Override
    protected float calculateCostToTarget(Waypoint waypoint, Point target) {

        OffsetCoord currentMapPosition =
                new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y);
        OffsetCoord targetMapPosition = new OffsetCoord(target.x, target.y);
        return currentMapPosition.getDistance(targetMapPosition);
    }

    private GameMap map;
    private Tribe.Faction faction = null;
    private Squad squad = null;
    private boolean useNextMapPositionOfSquad = false;
}
