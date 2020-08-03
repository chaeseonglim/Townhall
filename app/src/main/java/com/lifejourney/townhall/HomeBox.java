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

public class HomeBox extends Widget implements Button.Event {

    private final String LOG_TAG = "HomeBox";

    public interface Event {

        void onHomeBoxSwitchToResearchBox(HomeBox homeBox);

        void onHomeBoxClosed(HomeBox homeBox);
    }

    public HomeBox(Event eventHandler, Rect region, int layer, float depth, Villager villager) {

        super(region, layer, depth);

        this.eventHandler = eventHandler;
        this.villager = villager;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("home_box.png")
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
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(closeButton);

        // Upgradable button
        Rect toResearchButtonRegion = new Rect(region.right() - 310, region.bottom() - 65,
                150, 60);
        toPolicyButton = new Button.Builder(this, toResearchButtonRegion)
                .message("강화하기").imageSpriteAsset("")
                .fontSize(25).layer(layer + 1).fontColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(toPolicyButton);

        updateVillageInfo();

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
            eventHandler.onHomeBoxClosed(this);
        } else if (button == toPolicyButton) {
            // To town button
            setVisible(false);
            eventHandler.onHomeBoxSwitchToResearchBox(this);
        }
    }

    /**
     *
     */
    private void updateVillageInfo() {

        removeSprites("text");

        // The number of towns
        PointF textPosition = new PointF(-250, -155);
        addText("전체 마을 수", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(villager.getTowns().size() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // The number of squads
        textPosition.offset(150, -30);
        addText("전체 부대 수", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(villager.getSquads().size() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // Population
        textPosition.offset(-150, 30);
        addText("전체 인구", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(villager.getTotalPopulation()+"", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        textPosition.offset(150, -30);
        addText("인구 소모", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText("-" + villager.getWorkingPopulation(), new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // Income
        textPosition.offset(-150, 30);
        addText("골드 수입", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(villager.getIncome() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // Spend
        textPosition.offset(150, -30);
        addText("골드 소비", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText("-" + villager.getSpend(), new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // Happiness
        textPosition.offset(-150, 30);
        addText("평균 행복도", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(villager.getHappiness() + "", new SizeF(150, 40),
                textPosition.clone(), Color.rgb(230, 230, 230));

    }

    private Event eventHandler;
    private Villager villager;
    private Button closeButton;
    private Button toPolicyButton;
}