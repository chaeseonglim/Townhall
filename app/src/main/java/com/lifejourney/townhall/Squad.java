package com.lifejourney.townhall;

import android.view.MotionEvent;

import androidx.core.util.Pair;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.CollisionDetector;
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
import java.util.Arrays;
import java.util.ListIterator;

public class Squad extends Object implements Controllable {

    private final static String LOG_TAG = "Squad";

    public interface Event {

        void onSquadCreated(Squad squad);

        void onSquadDestroyed(Squad squad);

        void onSquadFocused(Squad squad);

        void onSquadMoved(Squad squad, OffsetCoord prevMapPosition, OffsetCoord newMapPosition);

        void onSquadUnitAdded(Squad squad, Unit unit);

        void onSquadUnitRemoved(Squad squad, Unit unit);
    }

    public static class Builder {

        private Event listener;
        private PointF position;
        private GameMap map;
        private Tribe.Faction faction;

        public Builder(Event listener, PointF position, GameMap map, Tribe.Faction faction) {
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
                    .gridSize(3, 5).opaque(ICON_SPRITE_OPAQUE_NORMAL)
                    .build();
            currentStick.setGridIndex(0, faction.ordinal());
            Sprite targetStick =
                new Sprite.Builder("SquadTarget", "squad.png").layer(SPRITE_LAYER)
                    .size(ICON_SPRITE_SIZE).positionOffset(ICON_SPRITE_HOTSPOT_OFFSET)
                    .smooth(false).visible(false).depth(-0.5f)
                    .gridSize(3, 5).opaque(TARGET_SPRITE_OPAQUE_NORMAL)
                    .build();
            targetStick.setGridIndex(0, faction.ordinal());
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

        private Event eventHandler;
        private GameMap map;
        private Tribe.Faction faction;

        public PrivateBuilder(Event eventHandler, PointF position, GameMap map, Tribe.Faction faction) {
            super(position);
            this.eventHandler = eventHandler;
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
        eventHandler = builder.eventHandler;
        faction = builder.faction;
        map = builder.map;
        Arrays.fill(shrineBonus, 0);

        eventHandler.onSquadCreated(this);
    }

    /**
     *
     */
    @Override
    public void close() {

        for (Unit unit: units) {
            eventHandler.onSquadUnitRemoved(this, unit);
            unit.close();
        }
        eventHandler.onSquadDestroyed(this);

        super.close();
    }

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        // Movement on tiled view
        OffsetCoord currentMapOffset = getMapPosition();
        if (targetMapPositionToMove != null && !targetMapPositionToMove.equals(currentMapOffset)) {
            if (map.isTerritoryMovable(nextMapPositionToMove, this)) {
                // If it's moving, send unit to next offset
                for (Unit unit : units) {
                    unit.setTargetMapOffset(nextMapPositionToMove);
                }

                // First check if all units are arrived
                boolean allUnitArrived = true;
                for (Unit unit : units) {
                    if (!new OffsetCoord(unit.getPosition()).equals(nextMapPositionToMove)) {
                        allUnitArrived = false;
                        break;
                    }
                }

                // If all units are arrived,  move to next
                if (allUnitArrived) {
                    moveTo(nextMapPositionToMove);
                    seekTo(targetMapPositionToMove, false);
                }
            } else {
                seekTo(targetMapPositionToMove, false);
            }
        } else {
            // If it's not moving, send unit to current offset
            for (Unit unit: units) {
                unit.setTargetMapOffset(currentMapOffset);
            }

            // If it's not fighting
            if (!isFighting()) {
                // First checking that if it's occupying territory
                Territory territory = map.getTerritory(getMapPosition());
                if (territory.isOccupying()) {
                    occupy();
                } else {
                    // And check if it's supporting other squad
                    boolean isSupporting = false;
                    if (isSupportable()) {
                        ArrayList<Territory> neighborTerritories =
                                map.getNeighborTerritories(getMapPosition(), 1, false);
                        for (Territory neighborTerritory : neighborTerritories) {
                            if (neighborTerritory.getBattle() != null &&
                                    neighborTerritory.isFactionSquadExist(getFaction())) {
                                neighborTerritory.getBattle().addSupporter(this);
                                isSupporting = true;
                                setSpritesToSupport();
                                break;
                            }
                        }
                    }

                    // Check if it's working
                    if (isWorking() && territory.getTerrain().facilitySlots() > 0) {
                        setSpritesToWork();
                    }

                    // Reset squad state at peace
                    if (!isSupporting && !isWorking() && !isDragging()) {
                        resetSprites();
                    }

                    // Heal units
                    if (!isSupporting) {
                        restUnits();
                    }
                }
            }
        }

        if (isDragging()) {
            // Scroll map if dragging is going on boundary
            Point scrollOffset = new Point();
            PointF touchedWidgetPosition =
                    Engine2D.GetInstance().translateScreenToWidgetPosition(lastTouchedScreenPosition);
            if (touchedWidgetPosition.x < 70) {
                scrollOffset.x = -30;
            }
            if (touchedWidgetPosition.y < 70) {
                scrollOffset.y = -30;
            }
            if (touchedWidgetPosition.x > Engine2D.GetInstance().getViewport().width - 70) {
                scrollOffset.x = 30;
            }
            if (touchedWidgetPosition.y > Engine2D.GetInstance().getViewport().height - 70) {
                scrollOffset.y = 30;
            }
            map.scroll(scrollOffset);

            if (firstDraggingGamePosition != null) {
                if (isFocused()) {
                    // Show glowing line while focused
                    showGlowingTilesToTarget(targetMapPositionToMove, true);
                }
            } else {
                // Show glowing line while dragging
                PointF lastTouchedGamePosition =
                        Engine2D.GetInstance().translateScreenToGamePosition(lastTouchedScreenPosition);
                OffsetCoord lastDraggingMapPosition = new OffsetCoord(lastTouchedGamePosition);
                showGlowingTilesToTarget(lastDraggingMapPosition, false);
            }
        } else if (isFocused()) {
            // Show glowing line while focused
            showGlowingTilesToTarget(targetMapPositionToMove, true);
        }

        // Set unit to bonus
        for (Unit unit: units) {
            unit.setAttackDamageBonus(UNIT_BONUS_DELTA * getOffensiveBonusFromTerritory());
            unit.setArmorBonus(UNIT_BONUS_DELTA * getDefensiveBonusFromTerritory());
            unit.setAttackSpeedBonus(UNIT_BONUS_DELTA * shrineBonus[Tribe.ShrineBonus.UNIT_ATTACK_SPEED.ordinal()]);
            unit.setHealPowerBonus(UNIT_BONUS_DELTA * shrineBonus[Tribe.ShrineBonus.UNIT_HEAL_POWER.ordinal()]);
        }

        if (!isFighting()) {
            // Collision detection
            CollisionDetector collisionDetector = Engine2D.GetInstance().getCollisionDetector();
            for (CollidableObject refUnit : units) {
                if (!refUnit.isCollisionEnabled())
                    continue;

                for (CollidableObject candidateUnit : units) {
                    if (refUnit == candidateUnit || candidateUnit.isCollisionChecked() ||
                            !candidateUnit.isCollisionEnabled()) {
                        continue;
                    }

                    collisionDetector.checkAndReponseCollision(refUnit, candidateUnit, false);
                }

                refUnit.setCollisionChecked(true);
            }
            for (CollidableObject unit : units) {
                unit.setCollisionChecked(false);
            }
        }

        // Hide if it's in fog
        if (map.getTerritory(getMapPosition()).getFogState() != Territory.FogState.CLEAR) {
            for (Unit unit: units) {
                unit.setInvisible(true);
            }
            for (Sprite sprite: getSprites()) {
                sprite.setOpaque(0.0f);
            }
        } else {
            for (Unit unit: units) {
                unit.setInvisible(false);
            }
        }
    }

    /**
     *
     * @return
     */
    public int getOffensiveBonusFromTerritory() {

        Territory territory = map.getTerritory(getMapPosition());
        if (territory.getFaction() == faction) {
            return map.getTerritory(getMapPosition()).getDelta(Territory.DeltaAttribute.OFFENSIVE);
        } else {
            return 0;
        }
    }

    /**
     *
     * @return
     */
    public int getDefensiveBonusFromTerritory() {

        Territory territory = map.getTerritory(getMapPosition());
        if (territory.getFaction() == faction) {
            return map.getTerritory(getMapPosition()).getDelta(Territory.DeltaAttribute.DEFENSIVE);
        } else {
            return 0;
        }
    }

    /**
     *
     * @return
     */
    public int[] collectDevelopmentBonus() {

        int workerCount = 0;

        if (!isMoving() && !isFighting() && !isOccupying() && !isSupporting()) {
            for (Unit unit : units) {
                if (!unit.isRecruiting() && unit.getUnitClass() == Unit.UnitClass.WORKER) {
                    workerCount++;
                }
            }
        }

        int[] deltas = new int[Territory.Facility.values().length];
        Arrays.fill(deltas, workerCount);

        if (workerCount > 0) {
            deltas[Territory.Facility.FARM.ordinal()] +=
                    Upgradable.WORKER_FARM_DEVELOPMENT_SPEED.getLevel(faction) * workerCount;
            deltas[Territory.Facility.MARKET.ordinal()] +=
                    Upgradable.WORKER_MARKET_DEVELOPMENT_SPEED.getLevel(faction) * workerCount;
            deltas[Territory.Facility.DOWNTOWN.ordinal()] +=
                    Upgradable.WORKER_DOWNTOWN_DEVELOPMENT_SPEED.getLevel(faction) * workerCount;
            deltas[Territory.Facility.FORTRESS.ordinal()] +=
                    Upgradable.WORKER_FORTRESS_DEVELOPMENT_SPEED.getLevel(faction) * workerCount;
        }
        return deltas;
    }

    /**
     *
     * @return
     */
    public int collectGoldBonus() {

        int workerCount = 0;

        if (!isMoving() && !isFighting() && !isOccupying() && !isSupporting()) {
            for (Unit unit : units) {
                if (!unit.isRecruiting() && unit.getUnitClass() == Unit.UnitClass.WORKER) {
                    workerCount++;
                }
            }
        }

        return workerCount;
    }

    /**
     *
     * @return
     */
    public int collectHappinessBonus() {

        int workerCount = 0;

        if (!isMoving() && !isFighting() && !isOccupying() && !isSupporting()) {
            for (Unit unit : units) {
                if (!unit.isRecruiting() && unit.getUnitClass() == Unit.UnitClass.WORKER) {
                    workerCount++;
                }
            }
        }

        if (workerCount > 0) {
            workerCount += Upgradable.WORKER_HAPPINESS.getLevel(faction) * workerCount;
        }

        return workerCount;
    }

    /**
     *
     * @return
     */
    public int collectDefensiveBonus() {

        int workerCount = 0;

        if (!isMoving() && !isFighting() && !isOccupying() && !isSupporting()) {
            for (Unit unit : units) {
                if (!unit.isRecruiting() && unit.getUnitClass() == Unit.UnitClass.WORKER) {
                    workerCount++;
                }
            }
        }

        if (workerCount > 0) {
            workerCount = Upgradable.WORKER_DEFENSE.getLevel(faction) * workerCount;
        }

        return workerCount;
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
        PointF touchedScreenPosition = new PointF(event.getX(), event.getY());
        PointF touchedGamePosition =
                Engine2D.GetInstance().translateScreenToGamePosition(touchedScreenPosition);

        // If it goes out of region without dragging, ignore touch
        RectF region = new RectF(getPosition(), spriteSize);
        region.offset(-spriteSize.width /2, -spriteSize.height /2);
        if (!region.includes(touchedGamePosition) && !isDragging()) {
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
                        setDragging(false, touchedGamePosition);
                    }
                } else if (isFocused() && getFaction() == Tribe.Faction.VILLAGER) {
                    // Keep dragging
                    setDragging(true, touchedGamePosition);
                }
                result = true;
            } else if (eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_CANCEL) {
                if (!isFocused()) {
                    // If it's not set to focused, set focus first
                    setFocus(true);
                } else if (isDragging()) {
                    OffsetCoord targetMapPosition = new OffsetCoord(touchedGamePosition);
                    // If it's dragged to the same tile or it's not movable tile
                    if (eventAction == MotionEvent.ACTION_CANCEL ||
                            targetMapPosition.equals(getMapPosition()) ||
                            !map.isTerritoryMovable(targetMapPosition, this)) {
                        if (targetMapPosition.equals(getMapPosition()) && firstDraggingGamePosition == null) {
                            // cancel dragging and moving
                            setDragging(false, touchedGamePosition);
                            stopMoving();
                        } else {
                            // cancel dragging
                            setDragging(false, touchedGamePosition);
                            if (isMoving()) {
                                Sprite targetStick = getSprite("SquadTarget");
                                targetStick.setPosition(targetMapPositionToMove.toGameCoord());
                                targetStick.setPositionOffset(ICON_SPRITE_HOTSPOT_OFFSET);
                                targetStick.setVisible(true);
                            }
                        }
                    } else {
                        // Stop dragging and seek to target
                        setDragging(false, touchedGamePosition);
                        seekTo(targetMapPosition, false);
                    }
                }
                setTouching(false);
                result = true;
            }
        }

        lastTouchedScreenPosition = touchedScreenPosition;
        return result;
    }


    /**
     *
     * @param targetMapCoord
     */
    public void moveTo(OffsetCoord targetMapCoord) {

        prevMapPosition = getMapPosition().clone();
        setPosition(targetMapCoord.toGameCoord());
        eventHandler.onSquadMoved(this, prevMapPosition, targetMapCoord);
    }

    /**
     *
     */
    public void stopMoving() {

        Sprite currentStick = getSprite("SquadStick");
        Sprite targetStick = getSprite("SquadTarget");
        Sprite squadIcon = getSprite("SquadIcon");
        Sprite movingArrow = getSprite("SquadMovingArrow");
        currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        targetStick.setVisible(false);
        squadIcon.setGridIndex(0, 0);
        movingArrow.setVisible(false);
        targetMapPositionToMove = null;
        nextMapPositionToMove = null;
    }

    /**
     *
     * @return
     */
    public boolean isMoving() {

        return targetMapPositionToMove != null;
    }

    /**
     *
     * @param targetMapPosition
     * @return
     */
    public boolean seekTo(OffsetCoord targetMapPosition, boolean alternativeTarget) {

        Sprite currentStick = getSprite("SquadStick");
        Sprite targetStick = getSprite("SquadTarget");
        Sprite squadIcon = getSprite("SquadIcon");
        Sprite movingArrow = getSprite("SquadMovingArrow");

        currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
        targetStick.setPosition(targetMapPosition.toGameCoord());
        targetStick.setPositionOffset(ICON_SPRITE_HOTSPOT_OFFSET);
        if (isFocused()) {
            targetStick.setVisible(true);
        }
        squadIcon.setAnimationWrap(true);
        squadIcon.clearAnimation();
        squadIcon.addAnimationFrame(1, 0, 40);
        squadIcon.addAnimationFrame(2, 0, 40);
        dragging = false;
        targetMapPositionToMove = targetMapPosition;

        OffsetCoord currentMapPosition = getMapPosition();
        if (currentMapPosition.equals(targetMapPosition)) {
            // If it reached to target offset, done moving
            stopMoving();
            return true;
        }

        // Path finding
        GamePathFinder pathFinder =
                new GamePathFinder(this, targetMapPosition, false);
        ArrayList<Waypoint> optimalPath = pathFinder.findOptimalPath();

        if (optimalPath == null) {
            if (alternativeTarget) {
                // Try alternative path
                GamePathFinder alternativePathFinder =
                        new GamePathFinder(getMapPosition(), targetMapPosition, getMap(),
                                getFaction());
                ArrayList<Waypoint> alternativePath = alternativePathFinder.findOptimalPath();
                if (alternativePath != null && alternativePath.size() > 1) {
                    OffsetCoord nextCandidate = new OffsetCoord(
                            alternativePath.get(1).getPosition().x, alternativePath.get(1).getPosition().y);
                    if (getMap().isTerritoryMovable(nextCandidate, this)) {
                        targetMapPositionToMove = nextCandidate;
                        optimalPath = alternativePath;
                    } else {
                        // If there's no path to target, cancel moving
                        stopMoving();
                        return false;
                    }
                }
            } else {
                // If there's no path to target, cancel moving
                stopMoving();
                return false;
            }
        }

        // Set next tile
        nextMapPositionToMove = new OffsetCoord(
                optimalPath.get(1).getPosition().x, optimalPath.get(1).getPosition().y);

        // Set moving arrow
        PointF currentGamePosition = getMapPosition().toGameCoord();
        PointF nextGamePositionToMove = nextMapPositionToMove.toGameCoord();
        currentGamePosition.add(nextGamePositionToMove).divide(2);
        CubeCoord.Direction tileDirection = getMapPosition().getDirection(nextMapPositionToMove);
        movingArrow.setPosition(currentGamePosition);
        if (!movingArrow.isVisible() || movingArrow.getGridIndex().y != tileDirection.ordinal()) {
            movingArrow.setAnimationWrap(true);
            movingArrow.clearAnimation();
            movingArrow.addAnimationFrame(0, tileDirection.ordinal(), 15);
            movingArrow.addAnimationFrame(1, tileDirection.ordinal(), 15);
            movingArrow.addAnimationFrame(2, tileDirection.ordinal(), 15);
            movingArrow.addAnimationFrame(3, tileDirection.ordinal(), 15);
            movingArrow.setVisible(true);
        }

        return true;
    }


    /**
     *
     * @param unit
     */
    private void addUnit(Unit unit) {

        units.add(unit);

        eventHandler.onSquadUnitAdded(this, unit);
    }

    /**
     *
     * @param unitClass
     */
    public void spawnUnit(Unit.UnitClass unitClass) {

        Unit unit = new Unit.Builder(unitClass, faction).position(getPosition().clone()).build();
        unit.setVisible(isVisible());
        unit.setCompanions(units);
        unit.setFocused(focused);
        addUnit(unit);
    }

    /**
     *
     * @param unit
     */
    public void removeUnit(Unit unit) {

        units.remove(unit);
        eventHandler.onSquadUnitRemoved(this, unit);
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
        eventHandler.onSquadUnitRemoved(this, unit);
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
     * @param index
     * @return
     */
    Unit getUnit(int index) {

        if (units.size() <= index) {
            return null;
        }

        return units.get(index);
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
            unit.endFight();
        }

        setPosition(getMapPosition().toGameCoord());
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

        // Set companions and opponents to all units
        ArrayList<Unit> companionUnits = new ArrayList<>();
        companionUnits.addAll(companion.getUnits());
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
    private void resetSprites() {

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
                expEarned += unit.getUnitClass().bountyExp(unit.getLevel());
                iter.remove();
                eventHandler.onSquadUnitRemoved(this, unit);
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

        boolean aggressiveness = false;
        int totalUnitHealth = 0;
        for (Unit unit: units) {
            totalUnitHealth += unit.getHealth();
            if (unit.getUnitClass().isAggressive()) {
                aggressiveness = true;
            }
        }

        return  ((float) totalUnitHealth / totalHealthAtBeginningOfFight < RETREAT_THRESHOLD) ||
                !aggressiveness;
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
    public Tribe.Faction getFaction() {

        return faction;
    }

    /**
     *
     * @param faction
     */
    public void setFaction(Tribe.Faction faction) {

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
    public OffsetCoord getMapPosition() {

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
            currentStick.setGridIndex(2, faction.ordinal());
            if (isMoving()) {
                targetStick.setVisible(true);
            }
            for (Unit unit: units) {
                unit.setFocused(true);
            }
            eventHandler.onSquadFocused(this);
        }
        else {
            for (Unit unit: units) {
                unit.setFocused(false);
            }
            currentStick.setGridIndex(0, faction.ordinal());
            targetStick.setVisible(false);
            map.setGlowingTilePositions(null);
        }
    }

    /**
     *
     * @param dragging
     * @param touchedGamePosition
     */
    public void setDragging(boolean dragging, PointF touchedGamePosition) {

        Sprite currentStick = getSprite("SquadStick");
        Sprite targetStick = getSprite("SquadTarget");

        if (!this.dragging && dragging) {
            firstDraggingGamePosition = touchedGamePosition.clone();
        }

        this.dragging = dragging;

        if (this.dragging) {
            if (firstDraggingGamePosition == null ||
                    firstDraggingGamePosition.distance(touchedGamePosition) > MIN_DISTANCE_START_DRAGGING) {
                currentStick.setOpaque(ICON_SPRITE_OPAQUE_DRAGGING);
                targetStick.setOpaque(TARGET_SPRITE_OPAQUE_DRAGGING);
                targetStick.setVisible(true);
                targetStick.setPosition(touchedGamePosition);
                targetStick.setPositionOffset(TARGET_SPRITE_HOTSPOT_OFFSET);
                firstDraggingGamePosition = null;
            }
        }
        else {
            currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
            targetStick.setVisible(false);
            firstDraggingGamePosition = null;
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
     * @param targetMapPosition
     * @param useNextPosition
     */
    private void showGlowingTilesToTarget(OffsetCoord targetMapPosition, boolean useNextPosition) {

        Sprite targetStick = getSprite("SquadTarget");

        if (targetMapPosition == null) {
            map.setGlowingTilePositions(null);
        }
        else if (!targetMapPosition.equals(getMapPosition()) &&
                map.isTerritoryMovable(targetMapPosition, this)) {
            GamePathFinder pathFinder =
                    new GamePathFinder(this, targetMapPosition, useNextPosition);
            ArrayList<Waypoint> optimalPath = pathFinder.findOptimalPath();

            if (optimalPath == null) {
                targetStick.setGridIndex(faction.ordinal(), 1);
            } else {
                ArrayList<OffsetCoord> glowingLine = new ArrayList<>();
                for (Waypoint waypoint : optimalPath) {
                    glowingLine.add(new OffsetCoord(waypoint.getPosition().x, waypoint.getPosition().y));
                }
                map.setGlowingTilePositions(glowingLine);
                targetStick.setGridIndex(0, faction.ordinal());
            }
        } else {
            map.setGlowingTilePositions(null);
            targetStick.setGridIndex(1, faction.ordinal());
        }
    }

    /**
     *
     * @return
     */
    public OffsetCoord getPrevMapPosition() {

        return prevMapPosition;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getNextMapPositionToMove() {

        return nextMapPositionToMove;
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

        return !isFighting() && map.getTerritory(getMapPosition()).isOccupying();
    }

    /**
     *
     * @return
     */
    public boolean isSupportable() {

        for (Unit unit: units) {
            if (!unit.isRecruiting() && unit.getUnitClass().isSupportable()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isSupporting() {

        if (isMoving() || isFighting() || isOccupying() || !isSupportable()) {
            return false;
        }

        ArrayList<Territory> neighborTerritories =
                map.getNeighborTerritories(getMapPosition(), 1, false);
        for (Territory neighborTerritory : neighborTerritories) {
            if (neighborTerritory.getBattle() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isWorking() {

        if (isMoving() || isFighting() || isOccupying() || isSupporting()) {
            return false;
        }

        for (Unit unit: units) {
            if (!unit.isRecruiting() && unit.getUnitClass() == Unit.UnitClass.WORKER) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public int getUpkeep() {
        int goldUpkeep = 0;
        for (Unit unit: units) {
            goldUpkeep += unit.getGoldUpkeep();
        }
        return goldUpkeep;
    }

    /**
     *
     * @return
     */
    public int getPopulation() {
        int populationUpkeep = 0;
        for (Unit unit: units) {
            populationUpkeep += unit.getPopulationUpkeep();
        }
        return populationUpkeep;
    }

    /**
     *
     * @return
     */
    public int getVision() {
        int vision = 0;
        for (Unit unit: units) {
            if (!unit.isRecruiting()) {
                vision = Math.max(vision, unit.getUnitClass().vision());
            }
        }

        return vision;
    }

    /**
     *
     * @param factor
     * @return
     */
    public int getShrineBonus(Tribe.ShrineBonus factor) {

        return shrineBonus[factor.ordinal()];
    }

    /**
     *
     * @param factor
     * @param value
     */
    public void setShrineBonus(Tribe.ShrineBonus factor, int value) {

        shrineBonus[factor.ordinal()] = value;
    }

    /**
     *
     */
    private void setSpritesToSupport() {

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
    }

    /**
     *
     */
    private void setSpritesToWork() {

        Sprite currentStick = getSprite("SquadStick");
        Sprite squadIcon = getSprite("SquadIcon");
        ArrayList<Pair<Point, Integer>> iconAnimation = squadIcon.getAnimation();
        if (iconAnimation.size() == 0 || !iconAnimation.get(0).first.equals(new Point(7, 0))) {
            currentStick.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);
            squadIcon.setAnimationWrap(true);
            squadIcon.clearAnimation();
            squadIcon.addAnimationFrame(7, 0, 8);
            squadIcon.addAnimationFrame(0, 0, 8);
            squadIcon.addAnimationFrame(7, 0, 200);
        }
    }

    /**
     *
     */
    private void restUnits() {

        for (Unit unit: units) {
            unit.rest(REST_PERCENTAGE *
                    (1 + map.getTerritory(getMapPosition()).getDelta(Territory.DeltaAttribute.DEFENSIVE)));
        }
    }

    /**
     *
     * @return
     */
    public boolean isRecruiting() {

        for (Unit unit: units) {
            if (unit.isRecruiting()) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @return
     */
    public float getHealthPercentage() {

        float maxHealth = 0.0f, currentHealth = 0.0f;
        for (Unit unit: units) {
            if (!unit.isRecruiting()) {
                maxHealth += unit.getMaxHealth();
                currentHealth += unit.getHealth();
            }
        }

        if (maxHealth == 0.0f) {
            return 0.0f;
        } else {
            return currentHealth / maxHealth;
        }
    }

    /**
     *
     * @return
     */
    public float getBattleMetric() {

        float battleMetric = 0.0f;
        for (Unit unit: units) {
            if (!unit.isRecruiting()) {
                battleMetric += unit.getUnitClass().battleMetric();
            }
        }
        return battleMetric;
    }

    /**
     *
     * @return
     */
    public float getSupportMetric() {

        float supportMetric = 0.0f;
        for (Unit unit: units) {
            if (!unit.isRecruiting()) {
                supportMetric += unit.getUnitClass().supportMetric();
            }
        }
        return supportMetric;
    }

    /**
     *
     */
    public void berserk() {

       for (Unit unit: units) {
           unit.setLevel(10);
       }
    }

    /**
     *
     */
    public void eliminate() {

        for (Unit unit: units) {
            unit.setHealth(0);
        }
    }

    private final static int SPRITE_LAYER = 5;
    private final static float MIN_DISTANCE_START_DRAGGING = 30;
    private final static SizeF ICON_SPRITE_SIZE = new SizeF(80, 80);
    private final static SizeF MOVING_ARROW_SIZE = new SizeF(32, 32);
    private final static PointF ICON_SPRITE_HOTSPOT_OFFSET = new PointF(0, -25);
    private final static PointF TARGET_SPRITE_HOTSPOT_OFFSET = new PointF(0, -80);
    private final static float ICON_SPRITE_OPAQUE_NORMAL = 1.0f;
    private final static float ICON_SPRITE_OPAQUE_BATTLE = 0.5f;
    private final static float ICON_SPRITE_OPAQUE_DRAGGING = 0.2f;
    private final static float TARGET_SPRITE_OPAQUE_NORMAL = 0.0f;
    private final static float TARGET_SPRITE_OPAQUE_DRAGGING = 0.8f;
    private final static float MOVING_ARROW_SPRITE_OPAQUE_NORMAL = 0.7f;
    private final static float RETREAT_THRESHOLD = 0.3f;
    private final static float UNIT_BONUS_DELTA = 0.05f;
    private final static float REST_PERCENTAGE = 0.01f;

    private Event eventHandler;
    private GameMap map;
    private Tribe.Faction faction;
    private SizeF spriteSize;
    private ArrayList<Unit> units = new ArrayList<>();
    private boolean focused = false;
    private boolean touching = false;
    private boolean dragging = false;
    private boolean fighting = false;
    private OffsetCoord targetMapPositionToMove;
    private OffsetCoord nextMapPositionToMove;
    private OffsetCoord prevMapPosition;
    private int totalHealthAtBeginningOfFight;
    private PointF lastTouchedScreenPosition = null;
    private PointF firstDraggingGamePosition = null;
    private int[] shrineBonus = new int[Tribe.ShrineBonus.values().length];
}
