package com.lifejourney.townhall;

import android.graphics.Color;

import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.World;

import java.util.ArrayList;

public class GameWorld extends World implements Button.Event, MessageBox.Event {

    static final String LOG_TAG = "GameWorld";

    GameWorld() {
        super();
        setDesiredFPS(20.0f);

        map = new TownMap("map.png", scale);
        addView(map);
        map.show();

        messageBox = new MessageBox.Builder(this,
                new Rect(100, 100, 500, 400),"한글은?\ntest\ntest")
                .fontSize(35.0f).layer(9).textColor(Color.rgb(0, 0, 0))
                .build();
        messageBox.show();
        addWidget(messageBox);

        Button okButton = new Button.Builder(this,
                new Rect(400, 380, 150, 80), "확인")
                .fontSize(35.0f).layer(10).textColor(Color.rgb(0, 0, 0))
                .build();
        okButton.show();
        addWidget(okButton);

        Squad squad =
                new Squad.Builder(new PointF(map.getCapitalOffset().toScreenCoord()), scale, map)
                        .build();
        squad.show();
        squads.add(squad);
        addObject(squad);
    }

    /**
     *
     */
    @Override
    public void close() {
        map.close();
        map = null;
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
     * @param map
     */
    public void setMap(TownMap map) {
        this.map = map;
    }

    private float scale = 4.0f;
    private TownMap map;
    private MessageBox messageBox;
    private ArrayList<Squad> squads = new ArrayList<>();
}
