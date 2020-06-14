package com.lifejourney.townhall;

import android.util.Log;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Object;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;

public class Squad extends Object{

    private final static String LOG_TAG = "Squad";

    public static class Builder {

        private PointF position;
        private float scale;
        private TownMap map;

        public Builder(PointF position, float scale, TownMap map) {
            this.position = position;
            this.scale = scale;
            this.map = map;
        }
        public Squad build() {
            Sprite squadIcon = new Sprite.Builder("squad.png").layer(SPRITE_LAYER)
                    .size(SPRITE_BASE_SIZE.clone().multiply(scale)).smooth(false).visible(true)
                    .gridSize(new Size(2, 1))
                    .build();
            squadIcon.setPositionOffset(new Point(0, (int) (-10 * scale)));
            Sprite squadIconDragging = squadIcon.clone();
            squadIconDragging.setOpaque(0.0f);
            ArrayList<Sprite> sprites = new ArrayList<>();
            sprites.add(squadIcon);
            sprites.add(squadIconDragging);
            return (Squad) new PrivateBuilder<>(position, scale, map).priority(-1)
                    .sprite(squadIcon, true)
                    .sprite(squadIconDragging, false).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Squad.PrivateBuilder<T>> extends Object.Builder<T> {

        private float scale;
        private TownMap map;

        public PrivateBuilder(PointF position, float scale, TownMap map) {
            super(position);
            this.scale = scale;
            this.map = map;
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
    }

    /**
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        PointF touchingScreenCoord = new PointF(event.getX(), event.getY());
        PointF touchingGameCoord =
                Engine2D.GetInstance().translateScreenToGameCoord(touchingScreenCoord);
        RectF region = new RectF(getPosition(), spriteSize);
        region.offset(-(float)spriteSize.width/2, -(float)spriteSize.height/2);

        if (!region.includes(touchingGameCoord) && !dragging) {
            return false;
        }

        Sprite draggingSprite = getSprite(1);

        if (eventAction == MotionEvent.ACTION_DOWN) {
            draggingSprite.setOpaque(0.5f);
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
            if (map.getTileType(offsetCoord).movable()) {
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
        for (Sprite sprite: getSprites()) {
            sprite.setSize(spriteSize);
        }
    }

    private final static int SPRITE_LAYER = 5;
    private final static Size SPRITE_BASE_SIZE = new Size(32, 32);

    private TownMap map;
    private float scale;
    private Size spriteSize;
    private ArrayList<Unit> units = new ArrayList<>();
    private boolean dragging = false;
}
