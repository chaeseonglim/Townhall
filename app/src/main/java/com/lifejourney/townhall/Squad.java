package com.lifejourney.townhall;

import android.view.MotionEvent;

import com.lifejourney.engine2d.Controllable;
import com.lifejourney.engine2d.CubeCoord;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Object;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;
import java.util.ListIterator;

public class Squad extends Object implements Controllable {

    private final static String LOG_TAG = "Squad";

    public interface Event {

        void onSquadCreated(Squad squad);

        void onSquadDestroyed(Squad squad);

        void onSquadFocused(Squad squad);

        void onSquadMoved(Squad squad, OffsetCoord prevMapCoord, OffsetCoord newMapCoord);
    }

    public static class Builder {

        private Event listener;
        private PointF position;
        private TownMap map;
        private Town.Side side;

        public Builder(Event listener, PointF position, TownMap map, Town.Side side) {
            this.listener = listener;
            this.position = position;
            this.map = map;
            this.side = side;
        }
        public Squad build() {
            Sprite currentIcon =
                new Sprite.Builder("SquadIcon", "squad.png").layer(SPRITE_LAYER)
                    .size(ICON_SPRITE_SIZE).smooth(false).visible(true)
                    .gridSize(4, 3).opaque(ICON_SPRITE_OPAQUE_NORMAL)
                    .build();
            currentIcon.setGridIndex(side.ordinal(), 0);
            currentIcon.setPositionOffset(ICON_SPRITE_HOTSPOT_OFFSET);
            Sprite targetIcon =
                new Sprite.Builder("SquadTarget", "squad.png").layer(SPRITE_LAYER)
                    .size(ICON_SPRITE_SIZE).smooth(false).visible(false).depth(-0.5f)
                    .gridSize(4, 3).opaque(TARGET_SPRITE_OPAQUE_NORMAL)
                    .build();
            targetIcon.setPositionOffset(TARGET_SPRITE_HOTSPOT_OFFSET);
            Sprite movingArrow =
                new Sprite.Builder("SquadMovingArrow", "squad_moving_arrow.png")
                    .layer(SPRITE_LAYER - 1)
                    .size(MOVING_ARROW_SIZE).smooth(true).visible(false)
                    .gridSize(4, 6).opaque(MOVING_ARROW_SPRITE_OPAQUE_NORMAL)
                    .build();
            return (Squad)new PrivateBuilder<>(listener, position, map, side).priority(-1).layer(SPRITE_LAYER)
                    .sprite(currentIcon, true)
                    .sprite(targetIcon, false)
                    .sprite(movingArrow, false)
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Squad.PrivateBuilder<T>> extends Object.Builder<T> {

        private Event listener;
        private TownMap map;
        private Town.Side side;

        public PrivateBuilder(Event listener, PointF position, TownMap map, Town.Side side) {
            super(position);
            this.listener = listener;
            this.map = map;
            this.side = side;
        }
        public Squad build() {
            return new Squad(this);
        }
    }

    public Squad(PrivateBuilder builder) {

        super(builder);
        spriteSize = ICON_SPRITE_SIZE;
        listener = builder.listener;
        side = builder.side;
        map = builder.map;

        listener.onSquadCreated(this);
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean result = false;

        if (!isVisible()) {
            return false;
        }

        // Get current touched coord
        int eventAction = event.getAction();
        PointF touchedScreenCoord = new PointF(event.getX(), event.getY());
        PointF touchedGameCoord =
                Engine2D.GetInstance().translateScreenToGameCoord(touchedScreenCoord);

        // If it goes out of region without dragging, ignore touch
        RectF region = new RectF(getPosition(), spriteSize);
        region.offset(-spriteSize.width /2, -spriteSize.height /2);
        if (!region.includes(touchedGameCoord) && !isDragging()) {
            return false;
        }

        if (eventAction == MotionEvent.ACTION_DOWN) {
            setTouching(true);
            result = true;
        }
        else if (isTouching()) {
            if (eventAction == MotionEvent.ACTION_MOVE) {
                if (isDragging() && isFighting()) {
                    // If the battle begins during dragging, cancel dragging
                    setDragging(false, touchedGameCoord);
                } else if (isFocused()) {
                    // Keep dragging
                    setDragging(true, touchedGameCoord);
                }
                result = true;
            } else if (eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_CANCEL) {
                if (!isFocused()) {
                    // If it's not set to focused, set focus first
                    setFocus(true);
                } else if (isDragging()) {
                    OffsetCoord targetMapCoord = new OffsetCoord(touchedGameCoord);
                    if (eventAction == MotionEvent.ACTION_CANCEL ||
                            targetMapCoord.equals(getMapCoord()) ||
                            !map.isMovable(targetMapCoord, this)) {
                        // It it's dragged to the same tile, the cation is canceled or it's not movable tile,
                        // stop dragging and cancel moving
                        setDragging(false, touchedGameCoord);
                        stopMoving();
                    } else {
                        // Stop dragging and seek to target
                        setDragging(false, touchedGameCoord);
                        seekTo(targetMapCoord);
                    }
                }
                setTouching(false);
                result = true;
            }
        }

        lastTouchedScreenCoord = touchedScreenCoord;
        return result;
    }

    /**
     *
     */
    @Override
    public void close() {

        listener.onSquadDestroyed(this);

        super.close();
    }

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        // Movement on tiled view
        OffsetCoord currentMapOffset = getMapCoord();
        if (targetMapCoordToMove != null && !targetMapCoordToMove.equals(currentMapOffset)) {
            if (map.isMovable(nextMapCoordToMove, this)) {
                // If it's moving, send unit to next offset
                for (Unit unit : units) {
                    unit.setTargetMapOffset(nextMapCoordToMove);
                }

                // First check if all units are arrived
                boolean allUnitArrived = true;
                for (Unit unit : units) {
                    if (!new OffsetCoord(unit.getPosition()).equals(nextMapCoordToMove)) {
                        allUnitArrived = false;
                        break;
                    }
                }

                // If all units are arrived,  move to next
                if (allUnitArrived) {
                    moveTo(nextMapCoordToMove);
                    seekTo(targetMapCoordToMove);
                }
            } else {
                seekTo(targetMapCoordToMove);
            }
        } else {
            // If it's not moving, send unit to current offset
            for(Unit unit: units) {
                unit.setTargetMapOffset(currentMapOffset);
            }
        }

        if (isDragging()) {
            // Scroll map if dragging is going on boundary
            Point scrollOffset = new Point();
            PointF touchedWidgetCoord =
                    Engine2D.GetInstance().translateScreenToWidgetCoord(lastTouchedScreenCoord);
            if (touchedWidgetCoord.x < 70) {
                scrollOffset.x = -30;
            }
            if (touchedWidgetCoord.y < 70) {
                scrollOffset.y = -30;
            }
            if (touchedWidgetCoord.x > Engine2D.GetInstance().getViewport().width - 70) {
                scrollOffset.x = 30;
            }
            if (touchedWidgetCoord.y > Engine2D.GetInstance().getViewport().height - 70) {
                scrollOffset.y = 30;
            }
            map.scroll(scrollOffset);

            // Show glowing line while dragging
            PointF lastTouchedGameCoord =
                    Engine2D.GetInstance().translateScreenToGameCoord(lastTouchedScreenCoord);
            OffsetCoord lastDraggingMapCoord = new OffsetCoord(lastTouchedGameCoord);
            showGlowingTilesToTarget(lastDraggingMapCoord);
        } else if (isFocused()) {
            // Show glowing line while focused
            showGlowingTilesToTarget(targetMapCoordToMove);
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
     * @param targetMapCoord
     */
    public void moveTo(OffsetCoord targetMapCoord) {

        prevMapCoord = getMapCoord().clone();
        setPosition(targetMapCoord.toGameCoord());
        listener.onSquadMoved(this, prevMapCoord, targetMapCoord);
    }

    /**
     *
     */
    private void stopMoving() {

        Sprite currentIcon = getSprite(0);
        Sprite targetIcon = getSprite(1);
        Sprite movingArrow = getSprite(2);
        currentIcon.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        targetIcon.setVisible(false);
        movingArrow.setVisible(false);
        targetMapCoordToMove = null;
        nextMapCoordToMove = null;
    }

    /**
     *
     * @return
     */
    public boolean isMoving() {
        return targetMapCoordToMove != null;
    }

    /**
     *
     * @param targetOffset
     * @return
     */
    private void seekTo(OffsetCoord targetOffset) {

        Sprite currentIcon = getSprite(0);
        Sprite targetIcon = getSprite(1);
        Sprite movingArrow = getSprite(2);

        currentIcon.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        targetIcon.setPosition(targetOffset.toGameCoord());
        if (isFocused()) {
            targetIcon.setVisible(true);
        }
        dragging = false;
        targetMapCoordToMove = targetOffset;

        OffsetCoord currentMapCoord = getMapCoord();
        if (currentMapCoord.equals(targetOffset)) {
            // If it reached to target offset, done moving
            stopMoving();
            return;
        }

        // Path finding
        SquadPathFinder pathFinder = new SquadPathFinder(this, targetOffset);
        ArrayList<Waypoint> optimalPath = pathFinder.findOptimalPath();

        if (optimalPath == null) {
            // If there's no path to target, cancel moving
            stopMoving();
        } else {
            // Set next tile
            nextMapCoordToMove = new OffsetCoord(
                    optimalPath.get(1).getPosition().x, optimalPath.get(1).getPosition().y);

            // Set moving arrow
            PointF currentGameCoord = getMapCoord().toGameCoord();
            PointF nextGameCoordToMove = nextMapCoordToMove.toGameCoord();
            currentGameCoord.add(nextGameCoordToMove).divide(2);
            movingArrow.setPosition(currentGameCoord);
            movingArrow.setAnimationWrap(true);
            movingArrow.clearAnimation();
            CubeCoord.Direction tileDirection = getMapCoord().getDirection(nextMapCoordToMove);
            movingArrow.addAnimationFrame(0, tileDirection.ordinal(), 15);
            movingArrow.addAnimationFrame(1, tileDirection.ordinal(), 15);
            movingArrow.addAnimationFrame(2, tileDirection.ordinal(), 15);
            movingArrow.addAnimationFrame(3, tileDirection.ordinal(), 15);
            movingArrow.setVisible(true);
        }
    }


    /**
     *
     * @param unit
     */
    private void addUnit(Unit unit) {

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
    ArrayList<Unit> getUnits() {
        return units;
    }

    /**
     *
     * @param opponent
     */
    void startFight(Squad opponent) {

        // Finish any moving action
        stopMoving();

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
    void finishFight() {

        this.fighting = false;
        this.opponent = null;
        for (Unit unit: units) {
            unit.setOpponents(null);
        }

        setPosition(getMapCoord().toGameCoord());
    }

    /**
     *
     */
    void fight() {

        // Let all unit fight
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
                expEarned += unit.getUnitClass().earnedExp(unit.getLevel());
                iter.remove();
            }
        }

        // Return exp
        return expEarned;
    }

    /**
     *
     * @return
     */
    boolean isWillingToRetreat() {

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

    /**
     *
     * @param expEarned
     */
    public void addExp(int expEarned) {

        for (Unit unit: units) {
            unit.addExp(expEarned);
        }
    }

    /**
     *
     * @return
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     *
     * @param focused
     */
    public void setFocus(boolean focused) {

        Sprite currentIcon = getSprite(0);
        Sprite targetIcon = getSprite(1);

        this.focused = focused;
        if (this.focused) {
            currentIcon.setGridIndex(side.ordinal(), 2);
            if (isMoving()) {
                targetIcon.setVisible(true);
            }
            listener.onSquadFocused(this);
        }
        else {
            currentIcon.setGridIndex(side.ordinal(), 0);
            targetIcon.setVisible(false);
            map.setGlowingTiles(null);
        }
    }

    /**
     *
     * @return
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     *
     * @param dragging
     * @param touchedGameCoord
     */
    public void setDragging(boolean dragging, PointF touchedGameCoord) {

        Sprite currentIcon = getSprite(0);
        Sprite targetIcon = getSprite(1);
        Sprite movingArrow = getSprite(2);

        this.dragging = dragging;

        if (this.dragging) {
            currentIcon.setOpaque(ICON_SPRITE_OPAQUE_DRAGGING);
            targetIcon.setOpaque(TARGET_SPRITE_OPAQUE_DRAGGING);
            targetIcon.setVisible(true);
            targetIcon.setPosition(touchedGameCoord);
        }
        else {
            currentIcon.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
            targetIcon.setVisible(false);
            movingArrow.setVisible(false);
        }
    }

    /**
     *
     * @return
     */
    public boolean isTouching() {
        return touching;
    }

    /**
     *
     * @param touching
     */
    public void setTouching(boolean touching) {
        this.touching = touching;
    }

    /**
     *
     * @param targetMapCoord
     */
    private void showGlowingTilesToTarget(OffsetCoord targetMapCoord) {
        Sprite targetIcon = getSprite(1);

        if (targetMapCoord == null) {
            map.setGlowingTiles(null);
        }
        else if (!targetMapCoord.equals(getMapCoord()) && map.isMovable(targetMapCoord, this)) {
            SquadPathFinder pathFinder = new SquadPathFinder(this, targetMapCoord);
            ArrayList<Waypoint> optimalPath = pathFinder.findOptimalPath();

            if (optimalPath == null) {
                targetIcon.setGridIndex(side.ordinal(), 1);
            } else {
                ArrayList<OffsetCoord> glowingLine = new ArrayList<>();
                for (Waypoint waypoint : optimalPath) {
                    glowingLine.add(new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y));
                }
                map.setGlowingTiles(glowingLine);
                targetIcon.setGridIndex(side.ordinal(), 0);
            }
        } else {
            map.setGlowingTiles(null);
            targetIcon.setGridIndex(side.ordinal(), 1);
        }
    }

    /**
     *
     * @return
     */
    public OffsetCoord getPrevMapCoord() {
        return prevMapCoord;
    }

    private final static int SPRITE_LAYER = 5;
    private final static SizeF ICON_SPRITE_SIZE = new SizeF(80, 80);
    private final static SizeF MOVING_ARROW_SIZE = new SizeF(32, 32);
    private final static PointF ICON_SPRITE_HOTSPOT_OFFSET = new PointF(0, -25);
    private final static PointF TARGET_SPRITE_HOTSPOT_OFFSET = new PointF(0, -25);
    private final static float ICON_SPRITE_OPAQUE_NORMAL = 1.0f;
    private final static float ICON_SPRITE_OPAQUE_DRAGGING = 0.2f;
    private final static float TARGET_SPRITE_OPAQUE_NORMAL = 0.0f;
    private final static float TARGET_SPRITE_OPAQUE_DRAGGING = 0.5f;
    private final static float MOVING_ARROW_SPRITE_OPAQUE_NORMAL = 0.7f;
    private final static float RETREAT_THRESHOLD = 0.3f;

    private Event listener;
    private TownMap map;
    private Town.Side side;
    private SizeF spriteSize;
    private ArrayList<Unit> units = new ArrayList<>();
    private boolean focused = false;
    private boolean touching = false;
    private boolean dragging = false;
    private OffsetCoord targetMapCoordToMove;
    private OffsetCoord nextMapCoordToMove;
    private OffsetCoord prevMapCoord;
    private boolean fighting = false;
    private Squad opponent;
    private int totalHealthWhenEnteringBattle;
    private PointF lastTouchedScreenCoord = null;
}
