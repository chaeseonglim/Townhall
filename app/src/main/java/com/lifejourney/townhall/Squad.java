package com.lifejourney.townhall;

import android.view.MotionEvent;

import com.lifejourney.engine2d.Controllable;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Object;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;
import java.util.ListIterator;

public class Squad extends Object implements Controllable {

    private final static String LOG_TAG = "Squad";

    public static class Builder {

        private PointF position;
        private TownMap map;
        private Town.Side side;

        public Builder(PointF position, TownMap map, Town.Side side) {
            this.position = position;
            this.map = map;
            this.side = side;
        }
        public Squad build() {
            Sprite currentIcon = new Sprite.Builder("squad.png").layer(SPRITE_LAYER)
                    .size(SPRITE_BASE_SIZE).smooth(false).visible(true)
                    .gridSize(new Size(4, 2)).opaque(ICON_SPRITE_OPAQUE_NORMAL)
                    .build();
            currentIcon.setGridIndex(new Point(side.ordinal(), 0));
            currentIcon.setPositionOffset(SPRITE_HOTSPOT_OFFSET);
            Sprite targetIcon = currentIcon.clone();
            targetIcon.setOpaque(DRAGGING_SPRITE_OPAQUE_NORMAL);
            targetIcon.setVisible(false);
            return (Squad) new PrivateBuilder<>(position, map, side).priority(-1)
                    .sprite(currentIcon, true)
                    .sprite(targetIcon, false).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Squad.PrivateBuilder<T>> extends Object.Builder<T> {

        private TownMap map;
        private Town.Side side;

        public PrivateBuilder(PointF position, TownMap map, Town.Side side) {
            super(position);
            this.map = map;
            this.side = side;
        }
        public Squad build() {
            return new Squad(this);
        }
    }

    public Squad(PrivateBuilder builder) {

        super(builder);
        spriteSize = SPRITE_BASE_SIZE;
        side = builder.side;
        map = builder.map;
        map.getTown(getMapCoord()).addSquad(this);
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isVisible()) {
            return false;
        }

        int eventAction = event.getAction();
        lastTouchedScreenCoord = new PointF(event.getX(), event.getY());
        PointF touchedGameCoord =
                Engine2D.GetInstance().translateScreenToGameCoord(lastTouchedScreenCoord);
        RectF region = new RectF(getPosition(), spriteSize);
        region.offset(-(float)spriteSize.width/2, -(float)spriteSize.height/2);

        if (!region.includes(touchedGameCoord) && !dragging) {
            return false;
        }

        Sprite currentSprite = getSprite(0);
        Sprite targetSprite = getSprite(1);

        if (eventAction == MotionEvent.ACTION_DOWN) {
            if (!fighting) {
                // Start dragging action
                currentSprite.setOpaque(CURRENT_SPRITE_OPAQUE_DRAGGING);
                targetSprite.setOpaque(TARGET_SPRITE_OPAQUE_DRAGGING);
                targetSprite.setPosition(new Point(touchedGameCoord));
                targetSprite.setVisible(true);
                targetSprite.setGridIndex(new Point(side.ordinal(), 0));
                dragging = true;
            }
            return true;
        }
        else if (eventAction == MotionEvent.ACTION_MOVE && dragging) {
            if (fighting) {
                // It battle begins, cancel dragging icon
                finishMove();
            }
            else {
                // Move dragging icon
                Point draggingPoint = new Point(touchedGameCoord);
                targetSprite.setPosition(draggingPoint);
                OffsetCoord mapCoord = new OffsetCoord(draggingPoint);
                if (map.isMovable(mapCoord, this)) {
                    targetSprite.setGridIndex(new Point(side.ordinal(), 0));
                }
                else {
                    targetSprite.setGridIndex(new Point(side.ordinal(), 1));
                }

                /*
                // Scroll view if touch is going to boundary
                Point scrollOffset = new Point();
                PointF touchedWidgetCoord =
                        Engine2D.GetInstance().translateScreenToWidgetCoord(touchedScreenCoord);
                if (touchedWidgetCoord.x < 50) {
                    scrollOffset.x = -15;
                }
                if (touchedWidgetCoord.y < 50) {
                    scrollOffset.y = -15;
                }
                if (touchedWidgetCoord.x > Engine2D.GetInstance().getViewport().width - 50) {
                    scrollOffset.x = 15;
                }
                if (touchedWidgetCoord.y > Engine2D.GetInstance().getViewport().height - 50) {
                    scrollOffset.y = 15;
                }
                map.scroll(scrollOffset);
                 */
            }
            return true;
        }
        else if ((eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_CANCEL)
                && dragging) {
            Point targetGameCoord = new Point(touchedGameCoord);
            OffsetCoord targetOffset = new OffsetCoord(targetGameCoord);
            if (eventAction == MotionEvent.ACTION_UP && map.isMovable(targetOffset, this)) {
                // If movable tile is target, go there
                finishMove();
                seek(targetOffset);
            }
            else {
                // Cancel move
                finishMove();
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     */
    @Override
    public void close() {

        map.getTown(getMapCoord()).removeSquad(this);
        super.close();
    }

    /**
     *
     * @param targetOffset
     */
    public void move(OffsetCoord targetOffset) {

        map.getTown(getMapCoord()).removeSquad(this);
        setPosition(new PointF(targetOffset.toGameCoord()));
        map.getTown(targetOffset).addSquad(this);
    }

    /**
     *
     */
    private void finishMove() {

        Sprite currentSprite = getSprite(0);
        Sprite targetSprite = getSprite(1);
        currentSprite.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        targetSprite.setVisible(false);
        dragging = false;
        if (nextOffsetToMove != null && !nextOffsetToMove.equals(getMapCoord())) {
            map.getTown(nextOffsetToMove).removeSquad(this);
        }
        targetOffsetToMove = null;
        nextOffsetToMove = null;
    }

    /**
     *
     * @param targetOffset
     * @return
     */
    private boolean seek(OffsetCoord targetOffset) {

        Sprite currentSprite = getSprite(0);
        Sprite targetSprite = getSprite(1);
        currentSprite.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        targetSprite.setPosition(targetOffset.toGameCoord());
        targetSprite.setVisible(true);
        dragging = false;
        targetOffsetToMove = targetOffset;

        OffsetCoord currentMapCoord = getMapCoord();
        if (currentMapCoord.equals(targetOffset)) {
            // If it reached to target offset, done moving
            finishMove();
            return true;
        }
        else if (nextOffsetToMove == null || currentMapCoord.equals(nextOffsetToMove)) {
            // Path finding
            SquadPathFinder pathFinder = new SquadPathFinder(this, targetOffset);
            ArrayList<Waypoint> optimalPath = pathFinder.findOptimalPath();

            if (optimalPath == null) {
                // if there's no path to target, cancel moving
                finishMove();
                return false;
            }
            else {
                nextOffsetToMove = new OffsetCoord(
                        optimalPath.get(1).getPosition().x, optimalPath.get(1).getPosition().y);
                // pre-occupy map before moving to the tile
                map.getTown(nextOffsetToMove).addSquad(this);
                return true;
            }

        }
        else {
            return true;
        }
    }

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        // Movement on tiled view
        OffsetCoord currentMapOffset = getMapCoord();
        if (targetOffsetToMove!= null && !targetOffsetToMove.equals(currentMapOffset)) {
            // If it's moving, send unit to next offset
            for(Unit unit: units) {
                unit.setTargetMapOffset(nextOffsetToMove);
            }

            // First check if all units are arrived
            boolean allUnitArrived = true;
            for (Unit unit: units) {
                if (!new OffsetCoord(unit.getPosition()).equals(nextOffsetToMove)) {
                    allUnitArrived = false;
                    break;
                }
            }

            // Then move again
            if (allUnitArrived) {
                move(nextOffsetToMove);
                seek(targetOffsetToMove);
            }
        }
        else {
            // If it's not moving, send unit to current offset
            for(Unit unit: units) {
                unit.setTargetMapOffset(currentMapOffset);
            }
        }

        // Scroll view if touch is going to boundary during dragging
        if (dragging) {
            Point scrollOffset = new Point();
            PointF touchedWidgetCoord =
                    Engine2D.GetInstance().translateScreenToWidgetCoord(lastTouchedScreenCoord);
            if (touchedWidgetCoord.x < 50) {
                scrollOffset.x = -40;
            }
            if (touchedWidgetCoord.y < 50) {
                scrollOffset.y = -40;
            }
            if (touchedWidgetCoord.x > Engine2D.GetInstance().getViewport().width - 50) {
                scrollOffset.x = 40;
            }
            if (touchedWidgetCoord.y > Engine2D.GetInstance().getViewport().height - 50) {
                scrollOffset.y = 40;
            }
            map.scroll(scrollOffset);
        }
    }

    /**
     *
     */
    @Override
    public void commit() {

        super.commit();
    }

    /**
     *
     * @param unit
     */
    void addUnit(Unit unit) {

        units.add(unit);
    }

    /**
     *
     * @param unitClass
     */
    Unit spawnUnit(Unit.UnitClass unitClass) {

        Unit unit = new Unit.Builder(unitClass, side).position(getPosition().clone()).build();
        unit.setVisible(isVisible());
        unit.setCompanions(units);
        addUnit(unit);
        return unit;
    }

    /**
     *
     * @return
     */
    public ArrayList<Unit> getUnits() {
        return units;
    }

    /**
     *
     * @param opponent
     */
    public void beginFight(Squad opponent) {

        // Finish any moving action
        finishMove();

        this.fighting = true;
        this.opponent = opponent;

        // Set opponents to each units
        totalHealthWhenEnteringBattle = 0;
        for (Unit unit: units) {
            unit.setOpponents(opponent.getUnits());
            totalHealthWhenEnteringBattle = unit.getHealth();
        }

        // Adjust squad icon to be battle position
        setPosition(getPosition().clone().offset(
                ((opponent.getSide().ordinal() > getSide().ordinal())? -1.0f : 1.0f) *
                        map.getTileSize().width / 3.0f, 0));
    }

    /**
     *
     */
    public void endFight() {

        this.fighting = false;
        this.opponent = null;
        for (Unit unit: units) {
            unit.setOpponents(null);
        }

        setPosition(new PointF(getMapCoord().toGameCoord()));
    }

    /**
     *
     */
    void fight() {

        // Fight
        for (Unit unit: units) {
            unit.fight();
        }
    }

    /**
     *
     */
    int countFightResult() {

        int expEarned = 0;

        // Remove killed units
        ListIterator<Unit> iter = units.listIterator();
        while (iter.hasNext()) {
            Unit unit = iter.next();
            if (unit.isKilled()) {
                expEarned += unit.getUnitClass().expEarned(unit.getLevel());
                iter.remove();
            }
        }

        return expEarned;
    }

    /**
     *
     * @return
     */
    public boolean isWillingToRetreat() {

        int totalUnitHealth = 0;
        for (Unit unit: units) {
            totalUnitHealth += unit.getHealth();
        }
        return (float) totalUnitHealth / totalHealthWhenEnteringBattle < RETREAT_THRESHOLD;
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {

        super.setVisible(visible);
        for (Unit unit: units) {
            unit.setVisible(visible);
        }
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
     * @param side
     */
    public void setSide(Town.Side side) {

        this.side = side;
    }

    /**
     *
     * @return
     */
    public TownMap getMap() {

        return map;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getMapCoord() {

        return new OffsetCoord(getPosition());
    }

    /**
     *
     * @return
     */
    public boolean isFighting() {

        return fighting;
    }

    /**
     *
     * @return
     */
    public boolean isEliminated() {

        return units.size() == 0;
    }

    public void addExp(int expEarned) {

        for (Unit unit: units) {
            unit.addExp(expEarned);
        }
    }

    private final static int SPRITE_LAYER = 5;
    private final static Size SPRITE_BASE_SIZE = new Size(80, 80);
    private final static Point SPRITE_HOTSPOT_OFFSET = new Point(0, -25);
    private final static float ICON_SPRITE_OPAQUE_NORMAL = 0.8f;
    private final static float CURRENT_SPRITE_OPAQUE_DRAGGING = 0.2f;
    private final static float DRAGGING_SPRITE_OPAQUE_NORMAL = 0.0f;
    private final static float TARGET_SPRITE_OPAQUE_DRAGGING = 0.5f;
    private final static float RETREAT_THRESHOLD = 0.3f;

    private TownMap map;
    private Town.Side side;
    private Size spriteSize;
    private ArrayList<Unit> units = new ArrayList<>();
    private boolean dragging = false;
    private OffsetCoord targetOffsetToMove;
    private OffsetCoord nextOffsetToMove;
    private boolean fighting = false;
    private Squad opponent;
    private int totalHealthWhenEnteringBattle;
    private PointF lastTouchedScreenCoord = null;
}
