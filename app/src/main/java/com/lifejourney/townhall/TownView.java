package com.lifejourney.townhall;

import android.graphics.Color;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.View;
import com.lifejourney.engine2d.World;

import java.util.HashMap;

class TownView implements View, MessageBox.Event, Button.Event {

    private String LOG_TAG = "TownView";

    TownView(World world, TownData townData) {
        this.world = world;
        this.townData = townData;
        this.hexTileMap = new HexTileMap(townData, 4.0f);

        messageBox =
                new MessageBox.Builder(this, new Rect(100, 100, 500, 400),
                        "한글은?\ntest\ntest").fontSize(35.0f).layer(9)
                        .textColor(Color.rgb(0, 0, 0))
                        .build();
        world.addWidget(messageBox);

        Button okButton =
                new Button.Builder(this, new Rect(400, 380, 150, 80),
                        "확인").fontSize(35.0f).layer(10)
                        .textColor(Color.rgb(0, 0, 0)).build();
        world.addWidget(okButton);
    }

    /**
     *
     */
    @Override
    public void close() {
        townData = null;
        hexTileMap.close();
    }

    /**
     *
     */
    @Override
    public void update() {
        if (!visible)
            return;

        hexTileMap.update();
        updateViewport();
    }

    /**
     *
     */
    @Override
    public void commit() {
        if (!visible)
            return;

        hexTileMap.commit();
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
    public boolean onTouchEvent(MotionEvent event)
    {
        return false;
    }

    /**
     *
     * @param visible
     */
    private void setVisible(boolean visible) {
        this.visible = visible;
        hexTileMap.setVisible(visible);
    }

    /**
     *
     */
    private void updateViewport() {
        Rect viewport = Engine2D.GetInstance().getViewport();
        Engine2D.GetInstance().setViewport(viewport);
    }

    private World world;
    private TownData townData;
    private HexTileMap hexTileMap;
    private MessageBox messageBox;
    private boolean visible;
}
