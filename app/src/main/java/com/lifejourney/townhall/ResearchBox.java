package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

public class ResearchBox extends Widget implements Button.Event, MessageBox.Event {

    private final String LOG_TAG = "ResearchBox";

    public interface Event {

        void onResearchBoxSwitchToHomeBox(ResearchBox researchBox);

        void onResearchBoxClosed(ResearchBox researchBox);
    }

    public ResearchBox(Event eventHandler, Rect region, int layer, float depth) {

        super(region, layer, depth);

        this.eventHandler = eventHandler;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("research_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(layer).depth(depth)
                .gridSize(1, 1).visible(false).opaque(0.8f).build();
        backgroundSprite.setGridIndex(0, 0);
        addSprite(backgroundSprite);

        // Close button
        Rect closeButtonRegion = new Rect(region.right() - 155, region.bottom() - 65,
                150, 60);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message("닫기").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(closeButton);

        // Home button
        Rect toHomeButtonRegion = new Rect(region.right() - 310, region.bottom() - 65,
                150, 60);
        toHomeButton = new Button.Builder(this, toHomeButtonRegion)
                .message("홈 화면").imageSpriteAsset("")
                .fontSize(25).layer(layer + 1).textColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(toHomeButton);

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
            eventHandler.onResearchBoxClosed(this);
        } else if (button == toHomeButton) {
            // To home button
            setVisible(false);
            eventHandler.onResearchBoxSwitchToHomeBox(this);
        }
    }

    /**
     *
     * @param messageBox
     * @param buttonType
     */
    @Override
    public void onMessageBoxButtonPressed(MessageBox messageBox, MessageBox.ButtonType buttonType) {
    }

    private Event eventHandler;
    private Button closeButton;
    private Button toHomeButton;
}
