package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

public class SettingBox extends Widget implements Button.Event, MessageBox.Event {

    private final String LOG_TAG = "SettingBox";

    public interface Event {

        void onSettingBoxClosed(SettingBox settingBox);
        void onSettingBoxExitPressed(SettingBox settingBox);
    }

    public SettingBox(Event eventHandler, int layer, float depth) {

        super(null, layer, depth);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 322) / 2, (viewport.height - 302) / 2,
                322, 302);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("setting_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(true).layer(layer).depth(depth)
                .gridSize(1, 1).visible(false).opaque(1.0f).build();
        backgroundSprite.setGridIndex(0, 0);
        addSprite(backgroundSprite);

        // Music button
        Rect musicButtonRegion = new Rect(getRegion().left() + 40, getRegion().top() + 28,
                56, 60);
        musicButton = new Button.Builder(this, musicButtonRegion)
                .imageSpriteAsset("setting_icon_btns.png").numImageSpriteSet(6)
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        if (Engine2D.GetInstance().isMusicEnabled()) {
            musicButton.setImageSpriteSet(0);
        } else {
            musicButton.setImageSpriteSet(1);
        }
        addWidget(musicButton);

        PointF textPosition = new PointF(50, -94);
        addText("음악 On/Off", new SizeF(200, 40), textPosition.clone(),
                Color.rgb(35, 35, 35));

        // Sound Effect button
        Rect soundEffectButtonRegion = new Rect(getRegion().left() + 40, getRegion().top() + 91,
                56, 60);
        soundEffectButton = new Button.Builder(this, soundEffectButtonRegion)
                .imageSpriteAsset("setting_icon_btns.png").numImageSpriteSet(6)
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        if (Engine2D.GetInstance().isSoundEffectEnabled()) {
            soundEffectButton.setImageSpriteSet(2);
        } else {
            soundEffectButton.setImageSpriteSet(3);
        }
        addWidget(soundEffectButton);

        textPosition = new PointF(50, -31);
        addText("효과음 On/Off", new SizeF(200, 40), textPosition.clone(),
                Color.rgb(35, 35, 35));

        // Exit button
        Rect exitButtonRegion = new Rect(getRegion().left() + 40, getRegion().top() + 154,
                56, 60);
        exitButton = new Button.Builder(this, exitButtonRegion)
                .imageSpriteAsset("setting_icon_btns.png").numImageSpriteSet(6)
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        exitButton.setImageSpriteSet(4);
        addWidget(exitButton);

        textPosition = new PointF(50, 32);
        addText("종료하기", new SizeF(200, 40), textPosition.clone(),
                Color.rgb(35, 35, 35));

        // Close button
        Rect closeButtonRegion = new Rect(getRegion().left() + 40, getRegion().top() + 217,
                56, 60);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .imageSpriteAsset("setting_icon_btns.png").numImageSpriteSet(6)
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        closeButton.setImageSpriteSet(5);
        addWidget(closeButton);

        textPosition = new PointF(50, 95);
        addText("돌아가기", new SizeF(200, 40), textPosition.clone(),
                Color.rgb(35, 35, 35));
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
        if (button == closeButton) { // Close button
            setVisible(false);
            eventHandler.onSettingBoxClosed(this);
        } else if (button == musicButton) { // Music button
            Engine2D engine2D = Engine2D.GetInstance();
            engine2D.enableMusic(!engine2D.isMusicEnabled());
            engine2D.savePreference(engine2D.getString(R.string.music_enable),
                    engine2D.isMusicEnabled()?1:0);
            musicButton.setImageSpriteSet(engine2D.isMusicEnabled()?0:1);
        } else if (button == soundEffectButton) { // Sound effect button
            Engine2D engine2D = Engine2D.GetInstance();
            engine2D.enableSoundEffect(!engine2D.isSoundEffectEnabled());
            engine2D.savePreference(engine2D.getString(R.string.sound_effect_enable),
                    engine2D.isSoundEffectEnabled()?1:0);
            soundEffectButton.setImageSpriteSet(engine2D.isSoundEffectEnabled()?2:3);
        } else if (button == exitButton) { // Exit button
            Rect viewport = Engine2D.GetInstance().getViewport();
            MessageBox messageBox = new MessageBox.Builder(this, MessageBox.Type.YES_OR_NO,
                    new Rect((viewport.width - 355) / 2, (viewport.height - 277) / 2,
                            355, 277), "정말 나가시겠습니까?")
                    .fontSize(25.0f).layer(50).textColor(Color.rgb(210, 210, 210))
                    .build();
            messageBox.setFollowParentVisibility(false);
            messageBox.show();
            addWidget(messageBox);

            hide();
        }
    }

    /**
     *
     * @param messageBox
     * @param buttonType
     */
    @Override
    public void onMessageBoxButtonPressed(MessageBox messageBox, MessageBox.ButtonType buttonType) {
        messageBox.close();
        removeWidget(messageBox);

        if (buttonType == MessageBox.ButtonType.YES) {
            eventHandler.onSettingBoxExitPressed(this);
        } else {
            show();
        }
    }

    /**
     *
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addText(String text, SizeF size, PointF position, int fontColor) {
        addSprite(new TextSprite.Builder("text", text, 25)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(235, 235, 235), 2.0f)
                .horizontalAlign(Layout.Alignment.ALIGN_NORMAL)
                .verticalAlign(Layout.Alignment.ALIGN_CENTER)
                .size(size).positionOffset(position)
                .smooth(true).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }


    private Event eventHandler;
    private Button closeButton;
    private Button exitButton;
    private Button musicButton;
    private Button soundEffectButton;
}
