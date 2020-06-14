package com.lifejourney.townhall;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.View;
import com.lifejourney.engine2d.World;

import java.util.ArrayList;

class TownView implements View, MessageBox.Event, Button.Event {

    private String LOG_TAG = "TownView";

    TownView(World world, String mapAsset) {

        this.world = world;
        this.scale = 4.0f;
        this.townMap = new TownMap(mapAsset, scale);

        messageBox = new MessageBox.Builder(this,
                new Rect(100, 100, 500, 400),"한글은?\ntest\ntest")
                .fontSize(35.0f).layer(9).textColor(Color.rgb(0, 0, 0))
                .build();
        messageBox.show();
        world.addWidget(messageBox);

        Button okButton = new Button.Builder(this,
                new Rect(400, 380, 150, 80), "확인")
                .fontSize(35.0f).layer(10).textColor(Color.rgb(0, 0, 0))
                .build();
        okButton.show();
        world.addWidget(okButton);

        Squad squad =
                new Squad.Builder(new PointF(townMap.getCapitalOffset().toScreenCoord()), scale, townMap)
                        .build();
        squad.show();
        squads.add(squad);
        world.addObject(squad);
    }

    /**
     *
     */
    @Override
    public void close() {

        townMap.close();
        townMap = null;
    }

    /**
     *
     */
    @Override
    public void update() {

        if (!visible) {
            return;
        }

        townMap.update();
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

        townMap.commit();
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
     * @param messageBox
     */
    @Override
    public void onMessageBoxTouched(MessageBox messageBox) {

        messageBox.hide();
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {

        messageBox.show();
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
        townMap.setVisible(visible);
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

    private World world;
    private TownMap townMap;
    private MessageBox messageBox;
    private boolean visible;
    private float scale;
    private boolean dragging = false;
    private PointF touchedPoint;

    private ArrayList<Squad> squads = new ArrayList<>();
}
