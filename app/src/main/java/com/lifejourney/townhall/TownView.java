package com.lifejourney.townhall;

import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.View;

import java.util.ArrayList;

class TownView implements View {

    private String LOG_TAG = "TownView";

    TownView(GameWorld world) {

        this.world = world;
        this.map = world.getMap();
    }

    /**
     *
     */
    @Override
    public void close() {
    }

    /**
     *
     */
    @Override
    public void update() {

        if (!visible) {
            return;
        }

        map.update();
        updateViewport();
    }

    /**
     *
     */
    @Override
    public void commit() {

        if (!visible) {
            return;
        }

        map.commit();
    }

    /**
     *
     */
    @Override
    public void show() {

        setVisible(true);
    }

    /**
     *
     */
    @Override
    public void hide() {

        setVisible(false);
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle squad touch event first
        for (Squad squad: squads) {
            if (squad.onTouchEvent(event)) {
                return true;
            }
        }

        // Dragging map
        int eventAction = event.getAction();
        PointF touchingScreenCoord = new PointF(event.getX(), event.getY());

        if (eventAction == MotionEvent.ACTION_DOWN) {
            dragging = true;
            touchedPoint = touchingScreenCoord;
            return true;
        }
        else if (eventAction == MotionEvent.ACTION_MOVE && dragging) {
            Rect viewport = Engine2D.GetInstance().getViewport();
            PointF delta = new PointF(touchingScreenCoord);
            delta.subtract(touchedPoint).multiply(-1.0f);
            viewport.offset(new Point(delta));
            Engine2D.GetInstance().setViewport(viewport);

            touchedPoint = touchingScreenCoord;
            return true;
        }
        else if ((eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_CANCEL)
                && dragging) {
            dragging = false;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * @param visible
     */
    private void setVisible(boolean visible) {

        this.visible = visible;
        map.setVisible(visible);
    }

    /**
     *
     */
    private void updateViewport() {

        /*
        Rect viewport = Engine2D.GetInstance().getViewport();
        Engine2D.GetInstance().setViewport(viewport);
         */
    }

    private GameWorld world;
    private TownMap map;
    private boolean visible;
    private boolean dragging = false;
    private PointF touchedPoint;

    private ArrayList<Squad> squads = new ArrayList<>();
}
