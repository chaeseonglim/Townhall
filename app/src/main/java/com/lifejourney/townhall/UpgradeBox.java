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

public class UpgradeBox extends Widget implements Button.Event, MessageBox.Event {

    private final String LOG_TAG = "UpgradeBox";

    public interface Event {

        void onUpgradeBoxSwitchToHomeBox(UpgradeBox upgradeBox);

        void onUpgradeBoxUpgraded(UpgradeBox upgradeBox, Upgradable upgradable);

        void onUpgradeBoxClosed(UpgradeBox upgradeBox);
    }

    public UpgradeBox(Event eventHandler, Villager villager, Mission mission) {
        super(null, 30, 0.0f);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 802) / 2, (viewport.height - 457) / 2,
                802,457);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;
        this.villager = villager;
        this.mission = mission;

        // Background sprite
        backgroundSprite = new Sprite.Builder("upgrade_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(getLayer()).depth(getDepth())
                .gridSize(3, 1).visible(false).opaque(1.0f).build();
        backgroundSprite.setGridIndex(0, 0);
        addSprite(backgroundSprite);

        // Close button
        Rect closeButtonRegion = new Rect(getRegion().right() - 166, getRegion().bottom() - 80,
                138, 64);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message(Engine2D.GetInstance().getString(R.string.close)).imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(getLayer() + 1).build();
        addWidget(closeButton);

        // Home button
        Rect toHomeButtonRegion = new Rect(getRegion().right() - 310, getRegion().bottom() - 80,
                138, 64);
        toHomeButton = new Button.Builder(this, toHomeButtonRegion)
                .message(Engine2D.GetInstance().getString(R.string.home)).imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(getLayer() + 1).build();
        addWidget(toHomeButton);

        // Unit buttons
        Rect unitButtonRegion =
                new Rect(getRegion().left() + 27, getRegion().bottom() - 78,
                        56, 60);
        for (int i = 0; i < Unit.UnitClass.values().length; ++i) {
            unitButtons[i] =
                    new Button.Builder(this, unitButtonRegion.clone())
                            .name("UnitBtn" + i)
                            .imageSpriteAsset("unit_selection_btn.png")
                            .numImageSpriteSet(Unit.UnitClass.values().length * 4)
                            .layer(getLayer() + 1).build();
            unitButtons[i].setImageSpriteSet(i * 4);
            if (!mission.getRecruitAvailable()[i]) {
                unitButtons[i].disable();
            }
            addWidget(unitButtons[i]);

            unitButtonRegion.offset(62, 0);
        }

        // Upgradable buttons
        Rect[] upgradableButtonRegions = {
                new Rect(getRegion().left() + 45, getRegion().top() + 90,150, 65),
                new Rect(getRegion().left() + 45, getRegion().top() + 180,150, 65),
                new Rect(getRegion().left() + 45, getRegion().top() + 270,150, 65),
                new Rect(getRegion().left() + 205, getRegion().top() + 90,150, 65),
                new Rect(getRegion().left() + 205, getRegion().top() + 180,150, 65),
                new Rect(getRegion().left() + 205, getRegion().top() + 270,150, 65),
        };
        for (int i = 0; i < 6; ++ i) {
            upgradableButtons[i] =
                    new Button.Builder(this, upgradableButtonRegions[i])
                            .name("UpgradeBtn" + i)
                            .imageSpriteAsset("unit_upgrade_btn.png")
                            .numImageSpriteSet(4)
                            .message(" ").fontSize(18)
                            .fontName("neodgm.ttf")
                            .fontColor(Color.rgb(205, 205, 205))
                            .fontShadow(Color.rgb(35, 35, 35), 1.0f)
                            .layer(getLayer() + 1).build();
            upgradableButtons[i].hide();
            upgradableButtons[i].setFollowParentVisibility(false);
            addWidget(upgradableButtons[i]);
        }

        updateInfo();
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
            eventHandler.onUpgradeBoxClosed(this);
        } else if (button == toHomeButton) { // To home button
            setVisible(false);
            eventHandler.onUpgradeBoxSwitchToHomeBox(this);
        } else if (button.getName().startsWith("UnitBtn")) { // Unit class selection buttons
            Unit.UnitClass pressedUnitClass =
                    Unit.UnitClass.values()[Integer.parseInt(button.getName().substring(7))];

            // Reset current selected button
            if (selectedUnitClass != null) {
                int unitClassIndex = selectedUnitClass.ordinal();
                unitButtons[unitClassIndex].setImageSpriteSet(unitClassIndex * 4);
            }

            // Select new unit class
            int unitClassIndex = pressedUnitClass.ordinal();
            if (selectedUnitClass != null && selectedUnitClass == pressedUnitClass) {
                button.setImageSpriteSet(unitClassIndex * 4);
                selectedUnitClass = null;
            } else {
                button.setImageSpriteSet(unitClassIndex * 4 + 1);
                selectedUnitClass = pressedUnitClass;
            }
            selectedUpgradable = null;

            updateInfo();
        } else if (button.getName().startsWith("UpgradeBtn")) { // Upgradable buttons
            Upgradable pressedUpgradable =
                    Upgradable.values()[selectedUnitClass.ordinal()*6 +
                            Integer.parseInt(button.getName().substring(10))];
            if (pressedUpgradable == selectedUpgradable) {
                Rect viewport = Engine2D.GetInstance().getViewport();
                if ((selectedUpgradable.getParent() == null ||
                        selectedUpgradable.getParent().getLevel(Tribe.Faction.VILLAGER) > 0) &&
                        selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) < 3) {
                    if (villager.isAffordable(selectedUpgradable)) {
                        upgradeConfirmBox = new MessageBox.Builder(this, MessageBox.Type.YES_OR_NO,
                                new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                                        353, 275),
                                Engine2D.GetInstance().getString(R.string.confirm_upgrade))
                                .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 230, 230))
                                .fontName("neodgm.ttf")
                                .build();
                    } else {
                        upgradeConfirmBox = new MessageBox.Builder(this, MessageBox.Type.CLOSE,
                                new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                                        353, 275),
                                Engine2D.GetInstance().getString(R.string.no_money_for_upgrade))
                                .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 230, 230))
                                .fontName("neodgm.ttf")
                                .build();
                    }
                    upgradeConfirmBox.show();
                    addWidget(upgradeConfirmBox);
                }
            } else {
                selectedUpgradable = pressedUpgradable;
            }

            updateInfo();
        }
    }

    /**
     *
     * @param messageBox
     * @param buttonType
     */
    @Override
    public void onMessageBoxButtonPressed(MessageBox messageBox, MessageBox.ButtonType buttonType) {
        if (buttonType == MessageBox.ButtonType.YES) {
            villager.pay(selectedUpgradable.getPurchaseCost());
            selectedUpgradable.setLevel(Tribe.Faction.VILLAGER,
                    selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) + 1);
            updateInfo();

            eventHandler.onUpgradeBoxUpgraded(this, selectedUpgradable);
        }

        messageBox.close();
        removeWidget(messageBox);
        upgradeConfirmBox = null;
    }

    /**
     *
     */
    private void updateInfo() {
        // Remove all previous texts
        removeSprites("text");
        removeSprites("icon");

        if (selectedUnitClass != null) {
            backgroundSprite.setGridIndex(1, 0);

            for (int i = 0; i < 6; ++ i) {
                Upgradable upgradable = Upgradable.values()[i+selectedUnitClass.ordinal()*6];
                int alpha = (upgradable == selectedUpgradable)? 1 : 0;
                if (upgradable.getParent() == null ||
                        upgradable.getParent().getLevel(Tribe.Faction.VILLAGER) > 0) {
                    upgradableButtons[i].setImageSpriteSet(0 + alpha);
                    upgradableButtons[i].setFontColor(Color.rgb(61, 61, 61));
                    upgradableButtons[i].setFontShadow(Color.rgb(235, 235, 235), 1.0f);
                } else {
                    upgradableButtons[i].setImageSpriteSet(2 + alpha);
                }
                upgradableButtons[i].setMessage(upgradable.getTitle()+"\nLv."+
                        upgradable.getLevel(Tribe.Faction.VILLAGER));
                upgradableButtons[i].show();
            }

            PointF textPosition = new PointF(-173, -165);
            addTitleText(selectedUnitClass.word() + " " + Engine2D.GetInstance().getString(R.string.upgrade),
                    new SizeF(350, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));

            if (selectedUpgradable != null) {
                backgroundSprite.setGridIndex(2, 0);

                textPosition.setTo(217, -167);
                addText(Engine2D.GetInstance().getString(R.string.purchase_cost),
                        new SizeF(350, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(-160, 30);
                addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
                textPosition.offset(200, 0);
                addText(selectedUpgradable.getPurchaseCost() + "", new SizeF(350, 40),
                        textPosition.clone(), Color.rgb(230, 230, 230));

                textPosition.offset(-40, 30);
                addText(Engine2D.GetInstance().getString(R.string.upkeep),
                        new SizeF(350, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(-160, 30);
                addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
                textPosition.offset(200, 0);
                addText((selectedUpgradable.getUpkeepCost() *
                                selectedUpgradable.getLevel(Tribe.Faction.VILLAGER)) + " (" +
                                selectedUpgradable.getUpkeepCost() + " x Lv)",
                        new SizeF(350, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));

                textPosition.offset(-40, 30);
                addText(Engine2D.GetInstance().getString(R.string.level) + " 1 "+
                                ((selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) == 1) ? "(*)":""),
                        new SizeF(350, 40), textPosition.clone(),
                        (selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) < 1)?
                                Color.rgb(160, 160, 0):
                                Color.rgb(255, 255, 0)
                );
                textPosition.offset(0, 30);
                addText(selectedUpgradable.getDescriptionLv1(),
                        new SizeF(350, 40), textPosition.clone(),
                        (selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) < 1)?
                                Color.rgb(160, 160, 160):
                                Color.rgb(230, 230, 230)
                );

                textPosition.offset(0, 30);
                addText(Engine2D.GetInstance().getString(R.string.level) + " 2 "+
                                ((selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) == 2) ? "(*)":""),
                        new SizeF(350, 40), textPosition.clone(),
                        (selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) < 2)?
                                Color.rgb(160, 160, 0):
                                Color.rgb(255, 255, 0)
                );
                textPosition.offset(0, 30);
                addText(selectedUpgradable.getDescriptionLv2(),
                        new SizeF(350, 40), textPosition.clone(),
                        (selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) < 2)?
                                Color.rgb(160, 160, 160):
                                Color.rgb(230, 230, 230)
                );

                textPosition.offset(0, 30);
                addText(Engine2D.GetInstance().getString(R.string.level) + " 3 " +
                                ((selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) == 3) ? "(*)":""),
                        new SizeF(350, 40), textPosition.clone(),
                        (selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) < 3)?
                                Color.rgb(160, 160, 0):
                                Color.rgb(255, 255, 0)
                );
                textPosition.offset(0, 30);
                addText(selectedUpgradable.getDescriptionLv3(),
                        new SizeF(350, 40), textPosition.clone(),
                        (selectedUpgradable.getLevel(Tribe.Faction.VILLAGER) < 3)?
                                Color.rgb(160, 160, 160):
                                Color.rgb(230, 230, 230)
                );
            } else {
                textPosition.setTo(217, -165);
                addText(Engine2D.GetInstance().getString(R.string.choose_upgrade_lv),
                        new SizeF(350, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
        } else {
            backgroundSprite.setGridIndex(0, 0);
            for (int i = 0; i < 6; ++ i) {
                upgradableButtons[i].hide();
            }

            PointF textPosition = new PointF(-185, -165);
            addText(Engine2D.GetInstance().getString(R.string.choose_upgrade_class),
                    new SizeF(350, 40), textPosition.clone(),
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
    private void addTitleText(String text, SizeF size, PointF position, int fontColor) {
        addSprite(new TextSprite.Builder("text", text, 27)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(61, 61, 61), 2.0f)
                .horizontalAlign(Layout.Alignment.ALIGN_NORMAL)
                .verticalAlign(Layout.Alignment.ALIGN_CENTER)
                .size(size).positionOffset(position)
                .smooth(true).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    /**
     *
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addText(String text, SizeF size, PointF position, int fontColor) {
        addSprite(new TextSprite.Builder("text", text, 23)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(61, 61, 61), 1.0f)
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
    private Mission mission;
    private Sprite backgroundSprite;
    private Button closeButton;
    private Button toHomeButton;
    private Button[] unitButtons = new Button[Unit.UnitClass.values().length];
    private Button[] upgradableButtons = new Button[6];
    private Unit.UnitClass selectedUnitClass = null;
    private Upgradable selectedUpgradable = null;
    private MessageBox upgradeConfirmBox;
}
