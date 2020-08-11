package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

public class SettingBox extends Widget implements Button.Event {

    private final String LOG_TAG = "SettingBox";

    public interface Event {

        void onSettingBoxClosed(SettingBox settingBox);
    }

    public SettingBox(Event eventHandler, int layer, float depth) {

        super(null, layer, depth);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 350) / 2, (viewport.height - 302) / 2,
                350, 302);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("setting_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(layer).depth(depth)
                .gridSize(1, 1).visible(false).opaque(1.0f).build();
        backgroundSprite.setGridIndex(0, 0);
        addSprite(backgroundSprite);

        // Close button
        Rect closeButtonRegion = new Rect(getRegion().right() - 155, getRegion().bottom() - 65,
                150, 60);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message("닫기").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(closeButton);

        // Music button
        Rect musicButtonRegion = new Rect(getRegion().left() + 40, getRegion().top() + 52,
                56, 60);
        musicButton = new Button.Builder(this, musicButtonRegion)
                .imageSpriteAsset("music_setting_btn.png").numImageSpriteSet(4)
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        if (Engine2D.GetInstance().isMusicEnabled()) {
            musicButton.setImageSpriteSet(0);
        } else {
            musicButton.setImageSpriteSet(1);
        }
        addWidget(musicButton);

        PointF textPosition = new PointF(15, -70);
        addText("음악 On/Off", new SizeF(160, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // Sound Effect button
        Rect soundEffectButtonRegion = new Rect(getRegion().left() + 40, getRegion().top() + 125,
                56, 60);
        soundEffectButton = new Button.Builder(this, soundEffectButtonRegion)
                .imageSpriteAsset("music_setting_btn.png").numImageSpriteSet(4)
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        if (Engine2D.GetInstance().isSoundEffectEnabled()) {
            soundEffectButton.setImageSpriteSet(2);
        } else {
            soundEffectButton.setImageSpriteSet(3);
        }
        addWidget(soundEffectButton);

        textPosition = new PointF(15, 3);
        addText("효과음 On/Off", new SizeF(160, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));
    }

    /**
     *
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addText(String text, SizeF size, PointF position, int fontColor) {

        addSprite(new TextSprite.Builder("text", text, 24)
                .fontColor(fontColor).bgColor(Color.argb(0, 0, 0, 0))
                .fontName("NanumBarunGothic.ttf")
                .textAlign(Paint.Align.LEFT)
                .size(size).positionOffset(position)
                .smooth(true).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // It consumes all input
        super.onTouchEvent(event);
        return true;
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {

        if (button == closeButton) {
            // Close button
            setVisible(false);
            eventHandler.onSettingBoxClosed(this);
        } else if (button == musicButton) {
            // Music button
            Engine2D engine2D = Engine2D.GetInstance();
            engine2D.enableMusic(!engine2D.isMusicEnabled());
            engine2D.savePreference(engine2D.getString(R.string.music_enable),
                    engine2D.isMusicEnabled()?1:0);
            musicButton.setImageSpriteSet(engine2D.isMusicEnabled()?0:1);
        } else if (button == soundEffectButton) {
            // Sound effect button
            Engine2D engine2D = Engine2D.GetInstance();
            engine2D.enableSoundEffect(!engine2D.isSoundEffectEnabled());
            engine2D.savePreference(engine2D.getString(R.string.sound_effect_enable),
                    engine2D.isSoundEffectEnabled()?1:0);
            soundEffectButton.setImageSpriteSet(engine2D.isSoundEffectEnabled()?2:3);
        }
    }

    private Event eventHandler;
    private Button closeButton;
    private Button musicButton;
    private Button soundEffectButton;
}
