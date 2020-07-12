package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;

import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

import java.text.NumberFormat;
import java.util.Locale;

public class SpeedControl extends Widget implements Button.Event {

    private final String LOG_TAG = "SpeedControl";

    interface Event {

        void onSpeedControlUpdate(SpeedControl speedControl);
    };

    public SpeedControl(Event listener) {

        super(new Rect(1080, 0, 174, 64), 20, 0.0f);
        this.listener = listener;

        Sprite bg = new Sprite.Builder("speed_control.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.0f)
                .gridSize(1, 1)
                .layer(20).visible(false).build();
        addSprite(bg);

        pauseButton = new Button.Builder(this,
                new Rect(1080, 0, 87, 64))
                .imageSpriteAsset("speed_control_btn1.png").numImageSpriteSet(2).layer(21).build();
        pauseButton.setImageSpriteSet(1);
        pauseButton.show();
        addWidget(pauseButton);

        playButton = new Button.Builder(this,
                new Rect(1167, 0, 87, 64))
                .imageSpriteAsset("speed_control_btn2.png").numImageSpriteSet(4).layer(21).build();
        playButton.setImageSpriteSet(playSpeed);
        playButton.show();
        addWidget(playButton);
    }

    /**
     *
     */
    @Override
    public void close() {

        super.close();
    }

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
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {

        if (button == pauseButton) {
            playSpeed = 0;
            pauseButton.setImageSpriteSet(0);
            playButton.setImageSpriteSet(0);
        } else {
            if (playSpeed++ == 0) {
                pauseButton.setImageSpriteSet(1);
            }
            if (playSpeed > 3) {
                playSpeed = 1;
            }
            playButton.setImageSpriteSet(playSpeed);
        }

        listener.onSpeedControlUpdate(this);
    }

    /**
     *
     * @return
     */
    int getPlaySpeed() {
        return playSpeed;
    }

    private Button pauseButton;
    private Button playButton;
    private Event listener;
    private int playSpeed = 1;  /* 0: paused, 1: play 1x, 2: play 2x, 3: play 3x */
}
