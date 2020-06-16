package com.lifejourney.townhall;

import android.util.Log;
import android.view.MotionEvent;

import androidx.core.util.Pair;

import com.lifejourney.engine2d.Controllable;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Object;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;

public class Squad extends Object implements Controllable {

    private final static String LOG_TAG = "Squad";

    public static class Builder {

        private PointF position;
        private float scale;
        private TownMap map;
        private Town.Side side;

        public Builder(PointF position, float scale, TownMap map, Town.Side side) {
            this.position = position;
            this.scale = scale;
            this.map = map;
            this.side = side;
        }
        public Squad build() {
            Sprite squadIcon = new Sprite.Builder("squad.png").layer(SPRITE_LAYER)
                    .size(SPRITE_BASE_SIZE.clone().multiply(scale)).smooth(false).visible(true)
                    .gridSize(new Size(2, 1)).opaque(ICON_SPRITE_OPAQUE_NORMAL)
                    .build();
            squadIcon.setPositionOffset(SPRITE_HOTSPOT_OFFSET.clone().multiply(scale));
            Sprite squadIconDragging = squadIcon.clone();
            squadIconDragging.setOpaque(DRAGGING_SPRITE_OPAQUE_NORMAL);
            squadIconDragging.setVisible(false);
            return (Squad) new PrivateBuilder<>(position, scale, map, side).priority(-1)
                    .sprite(squadIcon, true)
                    .sprite(squadIconDragging, false).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Squad.PrivateBuilder<T>> extends Object.Builder<T> {

        private float scale;
        private TownMap map;
        private Town.Side side;

        public PrivateBuilder(PointF position, float scale, TownMap map, Town.Side side) {
            super(position);
            this.scale = scale;
            this.map = map;
            this.side = side;
        }
        public Squad build() {
            return new Squad(this);
        }
    }

    public Squad(PrivateBuilder builder) {

        super(builder);
        scale = builder.scale;
        spriteSize = SPRITE_BASE_SIZE.clone().multiply(scale);
        map = builder.map;
        side = builder.side;

        OffsetCoord offsetCoord = new OffsetCoord(getPosition());
        map.addSquad(offsetCoord, this);
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
        PointF touchingScreenCoord = new PointF(event.getX(), event.getY());
        PointF touchingGameCoord =
                Engine2D.GetInstance().translateScreenToGameCoord(touchingScreenCoord);
        RectF region = new RectF(getPosition(), spriteSize);
        region.offset(-(float)spriteSize.width/2, -(float)spriteSize.height/2);

        if (!region.includes(touchingGameCoord) && !dragging) {
            return false;
        }

        Sprite iconSprite = getSprite(0);
        Sprite draggingSprite = getSprite(1);

        if (eventAction == MotionEvent.ACTION_DOWN) {
            iconSprite.setOpaque(ICON_SPRITE_OPAQUE_DRAGGING);
            draggingSprite.setOpaque(DRAGGING_SPRITE_OPAQUE_DRAGGING);
            draggingSprite.setPosition(new Point(touchingGameCoord));
            draggingSprite.setVisible(true);
            draggingSprite.setGridIndex(new Point(0, 0));
            dragging = true;
            return true;
        }
        else if (eventAction == MotionEvent.ACTION_MOVE && dragging) {
            Point draggingPoint = new Point(touchingGameCoord);
            draggingSprite.setPosition(draggingPoint);
            OffsetCoord offsetCoord = new OffsetCoord(draggingPoint);
            if (map.isMovable(offsetCoord, this)) {
                draggingSprite.setGridIndex(new Point(0, 0));
            }
            else {
                draggingSprite.setGridIndex(new Point(1, 0));
            }
            return true;
        }
        else if ((eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_CANCEL)
                && dragging) {
            draggingSprite.setVisible(false);
            dragging = false;
            iconSprite.setOpaque(ICON_SPRITE_OPAQUE_NORMAL);

            Point draggingPoint = new Point(touchingGameCoord);
            OffsetCoord offsetCoord = new OffsetCoord(draggingPoint);
            if (eventAction == MotionEvent.ACTION_UP && map.isMovable(offsetCoord, this)) {
                setPosition(new PointF(offsetCoord.toScreenCoord()));
                map.removeSquad(offsetCoord, this);
                map.addSquad(offsetCoord, this);
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
    public void update() {

        super.update();

        OffsetCoord currentMapCoord = new OffsetCoord(new Point(getPosition()));
        for(Unit unit: units) {
            unit.setTargetMapPosition(currentMapCoord);
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
    Unit spawnUnit(Unit.Class unitClass) {

        Unit unit = new Unit.Builder(unitClass, scale, side).position(getPosition().clone()).build();
        unit.setVisible(isVisible());
        unit.setSquadMembers(units);
        addUnit(unit);
        return unit;
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
    public float getScale() {

        return scale;
    }

    /**
     *
     * @param scale
     */
    public void setScale(float scale) {

        this.scale = scale;
        this.spriteSize = SPRITE_BASE_SIZE.clone().multiply(scale);
        for (Pair<Sprite, Boolean> pair: getSprites()) {
            pair.first.setSize(spriteSize);
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

    private final static int SPRITE_LAYER = 5;
    private final static Size SPRITE_BASE_SIZE = new Size(80, 80);
    private final static Point SPRITE_HOTSPOT_OFFSET = new Point(0, -25);
    private final static float ICON_SPRITE_OPAQUE_NORMAL = 0.8f;
    private final static float ICON_SPRITE_OPAQUE_DRAGGING = 0.2f;
    private final static float DRAGGING_SPRITE_OPAQUE_NORMAL = 0.0f;
    private final static float DRAGGING_SPRITE_OPAQUE_DRAGGING = 0.5f;

    private TownMap map;
    private Town.Side side;
    private float scale;
    private Size spriteSize;
    private ArrayList<Unit> units = new ArrayList<>();
    private boolean dragging = false;
    private boolean moving = false;
}
