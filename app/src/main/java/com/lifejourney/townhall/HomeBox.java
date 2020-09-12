package com.lifejourney.townhall;

import android.graphics.Color;
import android.text.Layout;
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
        Rect boxRegion = new Rect((viewport.width - 702) / 2, (viewport.height - 402) / 2,
                702, 402);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;
        this.villager = villager;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("home_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(true).layer(layer).depth(depth)
                .gridSize(1, 1).visible(false).opaque(1.0f).build();
        backgroundSprite.setGridIndex(0, 0);
        addSprite(backgroundSprite);

        // Close button
        Rect closeButtonRegion = new Rect(getRegion().right() - 166, getRegion().bottom() - 81,
                138, 64);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message(Engine2D.GetInstance().getString(R.string.close))
                .imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(layer + 1).build();
        addWidget(closeButton);

        // Upgradable button
        Rect toUpgradeButtonRegion = new Rect(getRegion().right() - 310, getRegion().bottom() - 81,
                138, 64);
        toUpgradeButton = new Button.Builder(this, toUpgradeButtonRegion)
                .message(Engine2D.GetInstance().getString(R.string.upgrade))
                .imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(layer + 1).build();
        addWidget(toUpgradeButton);

        updateTribeInfo();

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
            eventHandler.onHomeBoxClosed(this);
        } else if (button == toUpgradeButton) {  // To upgrade button
            setVisible(false);
            eventHandler.onHomeBoxSwitchToResearchBox(this);
        }
    }

    /**
     *
     */
    private void updateTribeInfo() {
        removeSprites("text");
        removeSprites("icon");

        // Population
        PointF textPosition = new PointF(-231, -145);
        addText(Engine2D.GetInstance().getString(R.string.total_population),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(235, 235, 0),
                Color.rgb(31, 31, 0), 2.0f);

        textPosition.offset(-65, 30);
        addIcon("people.png", new SizeF(30, 30), textPosition.clone());
        textPosition.offset(100, 0);
        addText(villager.getTotalPopulation()+"", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230),
                Color.rgb(35, 35, 35), 1.0f);

        textPosition.offset(125, -30);
        addText(Engine2D.GetInstance().getString(R.string.used_population),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(235, 235, 0),
                Color.rgb(31, 31, 0), 2.0f);

        textPosition.offset(-65, 30);
        addIcon("people.png", new SizeF(30, 30), textPosition.clone());
        textPosition.offset(100, 0);
        addText((villager.getWorkingPopulation() == 0)? "-" : ("-" + villager.getWorkingPopulation()),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 0, 0),
                Color.rgb(35, 35, 35), 1.0f);

        // Income
        textPosition.offset(-195, 30);
        addText(Engine2D.GetInstance().getString(R.string.total_income),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(235, 235, 0),
                Color.rgb(35, 35, 35), 2.0f);

        textPosition.offset(-65, 30);
        addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
        textPosition.offset(100, 0);
        addText(villager.getIncome() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230),
                Color.rgb(35, 35, 35), 1.0f);

        // Spend
        textPosition.offset(125, -30);
        addText(Engine2D.GetInstance().getString(R.string.total_spend),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(235, 235, 0),
                Color.rgb(35, 35, 35), 2.0f);

        textPosition.offset(-65, 30);
        addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
        textPosition.offset(100, 0);
        addText((villager.getSpend() == 0)? "-" : ("-" + villager.getSpend()),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 0, 0),
                Color.rgb(35, 35, 35), 1.0f);

        // Happiness
        textPosition.offset(-195, 30);
        addText(Engine2D.GetInstance().getString(R.string.happiness),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(235, 235, 0),
                Color.rgb(35, 35, 35), 2.0f);

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
        textPosition.offset(100, 0);
        addText(villager.getHappiness() + "", new SizeF(150, 40),
                textPosition.clone(), Color.rgb(230, 230, 230),
                Color.rgb(35, 35, 35), 1.0f);

        // The number of towns
        textPosition.setTo(115, -145);
        addText(Engine2D.GetInstance().getString(R.string.number_of_territory),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(235, 235, 0),
                Color.rgb(35, 35, 35), 2.0f);

        textPosition.offset(-65, 30);
        addIcon("territory.png", new SizeF(25, 25), textPosition.clone());
        textPosition.offset(100, 0);
        addText(villager.getTerritories().size() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230),
                Color.rgb(35, 35, 35), 1.0f);

        // The number of squads
        textPosition.offset(125, -30);
        addText(Engine2D.GetInstance().getString(R.string.number_of_squad),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(235, 235, 0),
                Color.rgb(35, 35, 35), 2.0f);

        textPosition.offset(-65, 30);
        addIcon("troop.png", new SizeF(25, 25), textPosition.clone());
        textPosition.offset(100, 0);
        addText(villager.getSquads().size() + "", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230),
                Color.rgb(35, 35, 35), 1.0f);

        // Occupying shrine
        textPosition.offset(-195, 30);
        addText(Engine2D.GetInstance().getString(R.string.occupied_shrine),
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(235, 235, 0),
                Color.rgb(35, 35, 35), 2.0f);

        if (villager.getShrineBonus(Tribe.ShrineBonus.HEAL) != 0) {
            textPosition.offset(-65, 30);
            addIcon("heal.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(100, 0);
            addText(Engine2D.GetInstance().getString(R.string.healing_shrine),
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230),
                    Color.rgb(35, 35, 35), 1.0f);
            textPosition.offset(-35, 0);
        }
        if (villager.getShrineBonus(Tribe.ShrineBonus.WIND) != 0) {
            textPosition.offset(-65, 30);
            addIcon("wind.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(100, 0);
            addText(Engine2D.GetInstance().getString(R.string.wind_shrine),
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230),
                    Color.rgb(35, 35, 35), 1.0f);
            textPosition.offset(-35, 0);
        }
        if (villager.getShrineBonus(Tribe.ShrineBonus.PROSPERITY) != 0) {
            textPosition.offset(-65, 30);
            addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(100, 0);
            addText(Engine2D.GetInstance().getString(R.string.prosper_shrine),
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230),
                    Color.rgb(35, 35, 35), 1.0f);
            textPosition.offset(-35, 0);
        }
        if (villager.getShrineBonus(Tribe.ShrineBonus.LOVE) != 0) {
            textPosition.offset(-65, 30);
            addIcon("health.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(100, 0);
            addText(Engine2D.GetInstance().getString(R.string.love_shrine),
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230),
                    Color.rgb(35, 35, 35), 1.0f);
            textPosition.offset(-35, 0);
        }
        if (villager.getShrineBonus(Tribe.ShrineBonus.HEAL) == 0 &&
                villager.getShrineBonus(Tribe.ShrineBonus.WIND) == 0 &&
                villager.getShrineBonus(Tribe.ShrineBonus.PROSPERITY) == 0 &&
                villager.getShrineBonus(Tribe.ShrineBonus.LOVE) == 0) {
            textPosition.offset(0, 30);
            addText(Engine2D.GetInstance().getString(R.string.none),
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230),
                    Color.rgb(35, 35, 35), 1.0f);
        }
    }

    /**
     *
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addText(String text, SizeF size, PointF position, int fontColor, int shadowColor,
                         float shadowDepth) {
        addSprite(new TextSprite.Builder("text", text, 23)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(shadowColor, shadowDepth)
                .horizontalAlign(Layout.Alignment.ALIGN_NORMAL)
                .verticalAlign(Layout.Alignment.ALIGN_CENTER)
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
