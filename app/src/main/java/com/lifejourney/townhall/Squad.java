package com.lifejourney.townhall;

import android.view.MotionEvent;

import androidx.core.util.Pair;

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

        void onSquadUnitAdded(Squad squad, Unit unit);

        void onSquadUnitRemoved(Squad squad, Unit unit);
    }

    public static class Builder {

        private Event listener;
        private PointF position;
        private GameMap map;
        private Town.Faction faction;

        public Builder(Event listener, PointF position, GameMap map, Town.Faction faction) {
            this.listener = listener;
            this.position = position;
            this.map = map;
            this.faction = faction;
        }
        public Squad build() {
            Sprite currentStick =
                new Sprite.Builder("SquadStick", "squad.png").layer(SPRITE_LAYER)
                    .size(ICON_SPRITE_SIZE).positionOffset(ICON_SPRITE_HOTSPOT_OFFSET)
                    .smooth(false).visible(true)
                    .gridSize(5, 3).opaque(ICON_SPRITE_OPAQUE_NORMAL)
                    .build();
            currentStick.setGridIndex(faction.ordinal(), 0);
            Sprite targetStick =
                new Sprite.Builder("SquadTarget", "squad.png").layer(SPRITE_LAYER)
                    .size(ICON_SPRITE_SIZE).positionOffset(TARGET_SPRITE_HOTSPOT_OFFSET)
                    .smooth(false).visible(false).depth(-0.5f)
                    .gridSize(5, 3).opaque(TARGET_SPRITE_OPAQUE_NORMAL)
                    .build();
            targetStick.setGridIndex(faction.ordinal(), 0);
            Sprite squadIcon =
                new Sprite.Builder("SquadIcon", "squad_icon.png").layer(SPRITE_LAYER)
                    .size(ICON_SPRITE_SIZE).positionOffset(ICON_SPRITE_HOTSPOT_OFFSET)
                    .smooth(false).visible(false).depth(0.1f)
                    .gridSize(8, 1).opaque(ICON_SPRITE_OPAQUE_NORMAL)
                    .build();
            Sprite movingArrow =
                new Sprite.Builder("SquadMovingArrow", "squad_moving_arrow.png")
                    .layer(SPRITE_LAYER - 1)
                    .size(MOVING_ARROW_SIZE).smooth(true).visible(false)
                    .gridSize(4, 6).opaque(MOVING_ARROW_SPRITE_OPAQUE_NORMAL)
                    .build();
            return (Squad)new PrivateBuilder<>(listener, position, map, faction).priority(-1).layer(SPRITE_LAYER)
                    .sprite(currentStick, true)
                    .sprite(targetStick, false)
                    .sprite(squadIcon, true)
                    .sprite(movingArrow, false)
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Squad.PrivateBuilder<T>> extends Object.Builder<T> {

        private Event listener;
        private GameMap map;
        private Town.Faction faction;

        public PrivateBuilder(Event listener, PointF position, GameMap map, Town.Faction faction) {
            super(position);
            this.listener = listener;
            this.map = map;
            this.faction = faction;
        }
        public Squad build() {
            return new Squad(this);
        }
    }

    public Squad(PrivateBuilder builder) {

        super(builder);
        spriteSize = ICON_SPRITE_SIZE;
        listener = builder.listener;
        faction = builder.faction;
        map = builder.map;

        listener.onSquadCreated(this);
    }

    /**
     *
     */
    @Override
    public void close() {

        for (Unit unit: units) {
            listener.onSquadUnitRemoved(this, unit);
            unit.close();
        }
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
            for (Unit unit: units) {
                unit.setTargetMapOffset(currentMapOffset);
            }

            // If it's not fighting
            if (!isFighting()) {
                // First checking that if it's occupying town
                Town town = map.getTown(getMapCoord());
                if (town.isOccupying()) {
                    occupy();
                } else {
                    // And check if it's supporting others
                    boolean isSupporting = false;
                    ArrayList<Town> neighborTowns = map.getNeighborTowns(getMapCoord(), false);
                    for (Town neighborTown: neighborTowns) {
                        if (neighborTown.getBattle() != null) {
                            neighborTown.getBattle().addSupporter(this);
                            isSupporting = true;
                            break;
                        }
                    }

                    // Reset squad state at peace
                    if (!isSupporting && !isDragging()) {
                        peace();
                    }
                }
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

            if (firstDraggingGameCoord != null) {
                if (isFocused()) {
                    // Show glowing line while focused
                    showGlowingTilesToTarget(targetMapCoordToMove, true);
                }
            } else {
                // Show glowing line while dragging
                PointF lastTouchedGameCoord =
                        Engine2D.GetInstance().translateScreenToGameCoord(lastTouchedScreenCoord);
                OffsetCoord lastDraggingMapCoord = new OffsetCoord(lastTouchedGameCoord);
                showGlowingTilesToTarget(lastDraggingMapCoord, false);
            }
        } else if (isFocused()) {
            // Show glowing line while focused
            showGlowingTilesToTarget(targetMapCoordToMove, true);
        }
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
                if (isFighting()) {
                    if (isDragging()) {
                        // If the battle begins during dragging, cancel dragging
                        setDragging(false, touchedGameCoord);
                    }
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
                    // If it's dragged to the same tile or it's not movable tile
                    if (eventAction == MotionEvent.ACTION_CANCEL ||
                            targetMapCoord.equals(getMapCoord()) ||
                            !map.isMovable(targetMapCoord, this)) {
                        if (targetMapCoord.equals(getMapCoord()) && firstDraggingGameCoord == null) {
                            // cancel dragging and moving
                            setDragging(false, touchedGameCoord);
                            stopMoving();
                        } else {
                            // cancel dragging
                            setDragging(false, touchedGameCoord);
                            if (isMoving()) {
                                Sprite targetStick = getSprite("SquadTarget");
                                targetStick.setPosition(targetMapCoordToMove.toGameCoord());
                                targetStick.setVisible(true);
                            }
                        }
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

        Sprite currentStick = getSprite("SquadStick");
        Sprite targetStick = getSprite("SquadTarget");
        Sprite squadIcon = getSprite("SquadIcon");
        Sprite movingArrow = getSprite("SquadMovingArrow");
        currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        targetStick.setVisible(false);
        squadIcon.setGridIndex(0, 0);
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
     * @param targetMapCoord
     * @return
     */
    private void seekTo(OffsetCoord targetMapCoord) {

        Sprite currentStick = getSprite("SquadStick");
        Sprite targetStick = getSprite("SquadTarget");
        Sprite squadIcon = getSprite("SquadIcon");
        Sprite movingArrow = getSprite("SquadMovingArrow");

        currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        targetStick.setPosition(targetMapCoord.toGameCoord());
        if (isFocused()) {
            targetStick.setVisible(true);
        }
        squadIcon.setAnimationWrap(true);
        squadIcon.clearAnimation();
        squadIcon.addAnimationFrame(1, 0, 40);
        squadIcon.addAnimationFrame(2, 0, 40);
        dragging = false;
        targetMapCoordToMove = targetMapCoord;

        OffsetCoord currentMapCoord = getMapCoord();
        if (currentMapCoord.equals(targetMapCoord)) {
            // If it reached to target offset, done moving
            stopMoving();
            return;
        }

        // Path finding
        SquadPathFinder pathFinder = new SquadPathFinder(this, targetMapCoord, false);
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

        listener.onSquadUnitAdded(this, unit);
    }

    /**
     *
     * @param unitClass
     */
    public void spawnUnit(Unit.UnitClass unitClass) {

        Unit unit = new Unit.Builder(unitClass, faction).position(getPosition().clone()).build();
        unit.setVisible(isVisible());
        unit.setCompanions(units);
        addUnit(unit);
    }

    /**
     *
     * @param unit
     */
    public void removeUnit(Unit unit) {

        units.remove(unit);
        listener.onSquadUnitRemoved(this, unit);
        unit.close();
    }

    /**
     *
     * @param index
     */
    public void removeUnit(int index) {

        if (units.size() <= index) {
            return;
        }

        Unit unit = units.get(index);
        units.remove(index);
        listener.onSquadUnitRemoved(this, unit);
        unit.close();
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
    void beginFight(Squad opponent) {

        // Finish any moving action
        stopMoving();

        // Set squad Icon
        Sprite currentStick = getSprite("SquadStick");
        Sprite squadIcon = getSprite("SquadIcon");
        currentStick.setOpaque(ICON_SPRITE_OPAQUE_BATTLE);
        squadIcon.setAnimationWrap(true);
        squadIcon.clearAnimation();
        squadIcon.addAnimationFrame(3, 0, 8);
        squadIcon.addAnimationFrame(0, 0, 8);
        squadIcon.addAnimationFrame(3, 0, 200);

        this.fighting = true;

        // Set opponents to all units
        totalHealthAtBeginningOfFight = 0;
        for (Unit unit: units) {
            unit.setOpponents(opponent.getUnits());
            totalHealthAtBeginningOfFight += unit.getHealth();
        }

        // Adjust squad icon to be battle position
        setPosition(getPosition().clone().offset(
                ((opponent.getFaction().ordinal() > getFaction().ordinal())? -1.0f : 1.0f) *
                        map.getTileSize().width / 4.0f, 0));
    }

    /**
     *
     */
    void endFight() {

        Sprite currentStick = getSprite("SquadStick");
        Sprite squadIcon = getSprite("SquadIcon");
        currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        squadIcon.setGridIndex(0, 0);

        this.fighting = false;
        for (Unit unit: units) {
            unit.setOpponents(null);
        }

        setPosition(getMapCoord().toGameCoord());
    }

    /**
     *
     */
    void fight() {

        // Let all units fight
        for (Unit unit: units) {
            unit.fight();
        }
    }

    /**
     *
     */
    void support(Squad companion, Squad opponent) {

        // Set squad Icon
        Sprite currentStick = getSprite("SquadStick");
        Sprite squadIcon = getSprite("SquadIcon");
        ArrayList<Pair<Point, Integer>> iconAnimation = squadIcon.getAnimation();
        if (iconAnimation.size() == 0 || !iconAnimation.get(0).first.equals(new Point(4, 0))) {
            currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
            squadIcon.setAnimationWrap(true);
            squadIcon.clearAnimation();
            squadIcon.addAnimationFrame(4, 0, 8);
            squadIcon.addAnimationFrame(0, 0, 8);
            squadIcon.addAnimationFrame(4, 0, 200);
        }

        // Set companions and opponents to all units
        ArrayList<Unit> companionUnits = new ArrayList<>();
        companionUnits.addAll(companion.getUnits());
        companionUnits.addAll(getUnits());
        for (Unit unit: units) {
            unit.setCompanions(companionUnits);
            unit.setOpponents(opponent.getUnits());
        }

        // Let all units support
        for (Unit unit: units) {
            unit.support();
        }

        // Remove fight status
        for (Unit unit: units) {
            unit.setCompanions(getUnits());
            unit.setOpponents(null);
        }
    }

    /**
     *
     */
    private void occupy() {

        Sprite currentStick = getSprite("SquadStick");
        Sprite squadIcon = getSprite("SquadIcon");
        ArrayList<Pair<Point, Integer>> iconAnimation = squadIcon.getAnimation();
        if (iconAnimation.size() == 0 || !iconAnimation.get(0).first.equals(new Point(5, 0))) {
            currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
            squadIcon.setAnimationWrap(true);
            squadIcon.clearAnimation();
            squadIcon.addAnimationFrame(5, 0, 40);
            squadIcon.addAnimationFrame(6, 0, 40);
        }
    }

    /**
     *
     */
    private void peace() {

        Sprite currentStick = getSprite("SquadStick");
        Sprite squadIcon = getSprite("SquadIcon");
        squadIcon.setGridIndex(0, 0);
        currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
    }

    /**
     *
     */
    int handleFightResult() {

        int expEarned = 0;

        // Remove killed units
        ListIterator<Unit> iter = units.listIterator();
        while (iter.hasNext()) {
            Unit unit = iter.next();
            if (unit.isKilled()) {
                expEarned += unit.getUnitClass().earnedExp(unit.getLevel());
                iter.remove();
                listener.onSquadUnitRemoved(this, unit);
                unit.close();
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
        return (float) totalUnitHealth / totalHealthAtBeginningOfFight < RETREAT_THRESHOLD;
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
    public Town.Faction getFaction() {

        return faction;
    }

    /**
     *
     * @param faction
     */
    public void setFaction(Town.Faction faction) {

        this.faction = faction;
    }

    /**
     *
     * @return
     */
    public GameMap getMap() {

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
     * @param expEarned
     */
    public void addExp(int expEarned) {

        for (Unit unit: units) {
            unit.addExp(expEarned);
        }
    }

    /**
     *
     * @param focused
     */
    public void setFocus(boolean focused) {

        Sprite currentStick = getSprite("SquadStick");
        Sprite targetStick = getSprite("SquadTarget");

        this.focused = focused;
        if (this.focused) {
            currentStick.setGridIndex(faction.ordinal(), 2);
            if (isMoving()) {
                targetStick.setVisible(true);
            }
            listener.onSquadFocused(this);
        }
        else {
            currentStick.setGridIndex(faction.ordinal(), 0);
            targetStick.setVisible(false);
            map.setGlowingTiles(null);
        }
    }

    /**
     *
     * @param dragging
     * @param touchedGameCoord
     */
    public void setDragging(boolean dragging, PointF touchedGameCoord) {

        Sprite currentStick = getSprite("SquadStick");
        Sprite targetStick = getSprite("SquadTarget");

        if (!this.dragging && dragging) {
            firstDraggingGameCoord = touchedGameCoord.clone();
        }

        this.dragging = dragging;

        if (this.dragging) {
            if (firstDraggingGameCoord == null ||
                    firstDraggingGameCoord.distance(touchedGameCoord) > MIN_DISTANCE_START_DRAGGING) {
                currentStick.setOpaque(ICON_SPRITE_OPAQUE_DRAGGING);
                targetStick.setOpaque(TARGET_SPRITE_OPAQUE_DRAGGING);
                targetStick.setVisible(true);
                targetStick.setPosition(touchedGameCoord);
                firstDraggingGameCoord = null;
            }
        }
        else {
            currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
            targetStick.setVisible(false);
            firstDraggingGameCoord = null;
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
    private void showGlowingTilesToTarget(OffsetCoord targetMapCoord, boolean useNextCoord) {

        Sprite targetStick = getSprite("SquadTarget");

        if (targetMapCoord == null) {
            map.setGlowingTiles(null);
        }
        else if (!targetMapCoord.equals(getMapCoord()) &&
                map.isMovable(targetMapCoord, this)) {
            SquadPathFinder pathFinder =
                    new SquadPathFinder(this, targetMapCoord, useNextCoord);
            ArrayList<Waypoint> optimalPath = pathFinder.findOptimalPath();

            if (optimalPath == null) {
                targetStick.setGridIndex(faction.ordinal(), 1);
            } else {
                ArrayList<OffsetCoord> glowingLine = new ArrayList<>();
                for (Waypoint waypoint : optimalPath) {
                    glowingLine.add(new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y));
                }
                map.setGlowingTiles(glowingLine);
                targetStick.setGridIndex(faction.ordinal(), 0);
            }
        } else {
            map.setGlowingTiles(null);
            targetStick.setGridIndex(faction.ordinal(), 1);
        }
    }

    /**
     *
     * @return
     */
    public OffsetCoord getPrevMapCoord() {

        return prevMapCoord;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getNextMapCoordToMove() {

        return nextMapCoordToMove;
    }

    /**
     *
     */
    @Override
    public void show() {

        super.show();
        for (Unit unit: units) {
            unit.show();
        }
    }

    /**
     *
     */
    @Override
    public void hide() {

        super.hide();
        for (Unit unit: units) {
            unit.hide();
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
     * @return
     */
    public boolean isDragging() {

        return dragging;
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

        int activeUnitCount = 0;
        for (Unit unit: units) {
            if (unit.isRecruiting()) {
                continue;
            }
            activeUnitCount++;
        }

        return activeUnitCount == 0;
    }

    /**
     *
     * @return
     */
    public boolean isOccupying() {

        return !isFighting() && map.getTown(getMapCoord()).isOccupying();
    }

    /**
     *
     * @return
     */
    public boolean isSupporting() {

        if (isFighting() || isOccupying()) {
            return false;
        }

        ArrayList<Town> neighborTowns = map.getNeighborTowns(getMapCoord(), false);
        for (Town neighborTown: neighborTowns) {
            if (neighborTown.getBattle() != null) {
                return true;
            }
        }
        return false;
    }

    private final static int SPRITE_LAYER = 5;
    private final static float MIN_DISTANCE_START_DRAGGING = 30;
    private final static SizeF ICON_SPRITE_SIZE = new SizeF(80, 80);
    private final static SizeF MOVING_ARROW_SIZE = new SizeF(32, 32);
    private final static PointF ICON_SPRITE_HOTSPOT_OFFSET = new PointF(0, -25);
    private final static PointF TARGET_SPRITE_HOTSPOT_OFFSET = new PointF(0, -25);
    private final static float ICON_SPRITE_OPAQUE_NORMAL = 1.0f;
    private final static float ICON_SPRITE_OPAQUE_BATTLE = 0.5f;
    private final static float ICON_SPRITE_OPAQUE_DRAGGING = 0.2f;
    private final static float TARGET_SPRITE_OPAQUE_NORMAL = 0.0f;
    private final static float TARGET_SPRITE_OPAQUE_DRAGGING = 0.8f;
    private final static float MOVING_ARROW_SPRITE_OPAQUE_NORMAL = 0.7f;
    private final static float RETREAT_THRESHOLD = 0.3f;

    private Event listener;
    private GameMap map;
    private Town.Faction faction;
    private SizeF spriteSize;
    private ArrayList<Unit> units = new ArrayList<>();
    private boolean focused = false;
    private boolean touching = false;
    private boolean dragging = false;
    private boolean fighting = false;
    private OffsetCoord targetMapCoordToMove;
    private OffsetCoord nextMapCoordToMove;
    private OffsetCoord prevMapCoord;
    private int totalHealthAtBeginningOfFight;
    private PointF lastTouchedScreenCoord = null;
    private PointF firstDraggingGameCoord = null;
}
