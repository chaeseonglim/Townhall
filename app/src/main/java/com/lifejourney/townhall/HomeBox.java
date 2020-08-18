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

public class HomeBox extends Widget implements Button.Event {

    private final String LOG_TAG = "HomeBox";

    public interface Event {

        void onHomeBoxSwitchToResearchBox(HomeBox homeBox);

        void onHomeBoxClosed(HomeBox homeBox);
    }

    public HomeBox(Event eventHandler, Villager villager, int layer, float depth) {

        super(null, layer, depth);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 700) / 2, (viewport.height - 400) / 2,
                700, 400);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;
        this.villager = villager;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("home_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(true).layer(layer).depth(depth)
                .gridSize(1, 1).visible(false).opaque(0.8f).build();
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

        // Upgradable button
        Rect toUpgradeButtonRegion = new Rect(getRegion().right() - 310, getRegion().bottom() - 65,
                150, 60);
        toUpgradeButton = new Button.Builder(this, toUpgradeButtonRegion)
                .message("강화하기").imageSpriteAsset("")
                .fontSize(25).layer(layer + 1).fontColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(toUpgradeButton);

        updateVillageInfo();

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
        } else if (button == toUpgradeButton) {
            // To upgrade button
            setVisible(false);
            eventHandler.onHomeBoxSwitchToResearchBox(this);
        }
    }

    /**
     *
     */
    private void updateVillageInfo() {

        removeSprites("text");
        removeSprites("icon");

        // Population
        PointF textPosition = new PointF(-250, -155);
        addText("전체 인구", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-65, 30);
        addIcon("people.png", new SizeF(30, 30), textPosition.clone());
        textPosition.offset(95, 0);
        addText(villager.getTotalPopulation()+"", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        textPosition.offset(120, -30);
        addText("인구 소모", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-65, 30);
        addIcon("people.png", new SizeF(30, 30), textPosition.clone());
        textPosition.offset(95, 0);
        addText((villager.getWorkingPopulation() == 0)? "-" : ("-" + villager.getWorkingPopulation()),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 0, 0));

        // Income
        textPosition.offset(-180, 30);
        addText("골드 수입", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-65, 30);
        addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
        textPosition.offset(95, 0);
        addText(villager.getIncome() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // Spend
        textPosition.offset(120, -30);
        addText("골드 소비", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-65, 30);
        addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
        textPosition.offset(95, 0);
        addText((villager.getSpend() == 0)? "-" : ("-" + villager.getSpend()),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 0, 0));

        // Happiness
        textPosition.offset(-180, 30);
        addText("평균 행복도", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-65, 30);
        if (villager.getHappiness() > 80) {
            addIcon("very_happy.png", new SizeF(25, 25), textPosition.clone());
        } else if (villager.getHappiness() > 60) {
            addIcon("happy.png", new SizeF(25, 25), textPosition.clone());
        } else if (villager.getHappiness() > 40) {
            addIcon("soso.png", new SizeF(25, 25), textPosition.clone());
        } else if (villager.getHappiness() > 20) {
            addIcon("bad.png", new SizeF(25, 25), textPosition.clone());
        } else {
            addIcon("very_bad.png", new SizeF(25, 25), textPosition.clone());
        }
        textPosition.offset(95, 0);
        addText(villager.getHappiness() + "", new SizeF(150, 40),
                textPosition.clone(), Color.rgb(230, 230, 230));

        // The number of towns
        textPosition.setTo(100, -155);
        addText("마을 수", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-65, 30);
        addIcon("territory.png", new SizeF(25, 25), textPosition.clone());
        textPosition.offset(95, 0);
        addText(villager.getTerritories().size() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // The number of squads
        textPosition.offset(120, -30);
        addText("부대 수", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-65, 30);
        addIcon("troop.png", new SizeF(25, 25), textPosition.clone());
        textPosition.offset(95, 0);
        addText(villager.getSquads().size() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // Occupying shrine
        textPosition.offset(-180, 30);
        addText("소유한 제단", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        if (villager.getShrineBonus(Tribe.ShrineBonus.UNIT_HEAL_POWER) != 0) {
            textPosition.offset(-65, 30);
            addIcon("heal.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(95, 0);
            addText("치유의 제단", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
            textPosition.offset(-30, 0);
        }
        if (villager.getShrineBonus(Tribe.ShrineBonus.UNIT_ATTACK_SPEED) != 0) {
            textPosition.offset(-65, 30);
            addIcon("wind.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(95, 0);
            addText("바람의 제단", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
            textPosition.offset(-30, 0);
        }
        if (villager.getShrineBonus(Tribe.ShrineBonus.TERRITORY_GOLD_BOOST) != 0) {
            textPosition.offset(-65, 30);
            addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(95, 0);
            addText("풍요의 제단", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
            textPosition.offset(-30, 0);
        }
        if (villager.getShrineBonus(Tribe.ShrineBonus.TERRITORY_POPULATION_BOOST) != 0) {
            textPosition.offset(-65, 30);
            addIcon("health.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(95, 0);
            addText("사랑의 제단", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
            textPosition.offset(-30, 0);
        }
        if (villager.getShrineBonus(Tribe.ShrineBonus.UNIT_HEAL_POWER) == 0 &&
                villager.getShrineBonus(Tribe.ShrineBonus.UNIT_ATTACK_SPEED) == 0 &&
                villager.getShrineBonus(Tribe.ShrineBonus.UNIT_ATTACK_SPEED) == 0 &&
                villager.getShrineBonus(Tribe.ShrineBonus.UNIT_ATTACK_SPEED) == 0) {
            textPosition.offset(0, 30);
            addText("없음", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
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
                .fontColor(fontColor).bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(Paint.Align.LEFT)
                .size(size).positionOffset(position)
                .smooth(true).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    /**
     *
     * @param asset
     * @param size
     * @param position
     */
    private void addIcon(String asset, SizeF size, PointF position) {

        addSprite(new Sprite.Builder("icon", asset)
                .size(size).positionOffset(position)
                .smooth(false).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    private Event eventHandler;
    private Villager villager;
    private Button closeButton;
    private Button toUpgradeButton;
}
