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

public class UnitSelectionBox extends Widget implements Button.Event{

    private final String LOG_TAG = "UnitSelectionBox";

    public interface Event {
        void onUnitBuilderBoxSelected(UnitSelectionBox infoBox, Unit.UnitClass unitClass);
    }

    public UnitSelectionBox(Event eventHandler, Mission mission, Villager villager,
                            Unit.UnitClass replacementClass) {
        super(null, 40, 0.0f);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 732) / 2, (viewport.height - 437) / 2,
                732, 437);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;
        this.mission = mission;
        this.villager = villager;
        this.replacementUnitClass = replacementClass;

        // Background sprite
        backgroundSprite = new Sprite.Builder("unit_selection_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(getLayer()).depth(getDepth())
                .gridSize(2, 1).visible(false).opaque(1.0f).build();
        addSprite(backgroundSprite);

        // Cancel button
        Rect cancelButtonRegion = new Rect(getRegion().right() - 165, getRegion().bottom() - 80,
                138, 64);
        cancelButton = new Button.Builder(this, cancelButtonRegion)
                .message("취소").imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(35, 35, 35))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(getLayer() + 1).build();
        addWidget(cancelButton);

        // Select button
        Rect selectButtonRegion = new Rect(getRegion().right() - 165, getRegion().bottom() - 80,
                138, 64);
        selectButton = new Button.Builder(this, selectButtonRegion)
                .message("선택").imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(35, 35, 35))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(getLayer() + 1).build();
        addWidget(selectButton);

        // Unit button
        Rect unitButtonRegion =
                new Rect(getRegion().left() + 27, getRegion().bottom() - 78,
                        56, 60);
        for (int i = 0; i < Unit.UnitClass.values().length; ++i) {
            unitButtons[i] =
                    new Button.Builder(this, unitButtonRegion.clone())
                            .imageSpriteAsset("unit_selection_btn.png")
                            .numImageSpriteSet(Unit.UnitClass.values().length * 4)
                            .layer(getLayer() + 1).build();
            Unit.UnitClass unitClass = Unit.UnitClass.values()[i];
            if (villager.isAffordable(unitClass, replacementUnitClass)) {
                unitButtons[i].setImageSpriteSet(i * 4);
            } else {
                unitButtons[i].setImageSpriteSet(i * 4 + 2);
            }
            if (!mission.getRecruitAvailable()[i]) {
                unitButtons[i].disable();
            }
            addWidget(unitButtons[i]);

            unitButtonRegion.offset(62, 0);
        }

        updateUnitInfo();
    }

    /**
     *
     */
    @Override
    public void update() {
        // Do this here for preventing auto show/hide affect to the button status
        if (selectedUnitClass == null ||
                !villager.isAffordable(selectedUnitClass, replacementUnitClass)) {
            selectButton.hide();
            cancelButton.show();
        } else {
            selectButton.show();
            cancelButton.hide();
        }

        super.update();
    }

    /**
     *
     */
    private void updateUnitInfo() {
        // Remove all previous texts
        removeSprites("text"+textIndex++);
        removeSprites("icon");

        if (selectedUnitClass != null) {
            backgroundSprite.setGridIndex(1, 0);

            // Unit Class
            PointF textPosition = new PointF(-245, -168);
            addText("클래스", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(selectedUnitClass.word(), new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Population
            textPosition.offset(160, -30);
            addText("소모 인구", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("people.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(95, 0);
            addText(selectedUnitClass.population()+"", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Purchase gold
            textPosition.offset(-190, 30);
            addText("구매 비용", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(95, 0);
            addText(selectedUnitClass.costToPurchase()+"", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Upkeep gold
            textPosition.offset(130, -30);
            addText("유지 비용", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(95, 0);
            addText(selectedUnitClass.costUpkeep()+"", new SizeF(150, 40),
                    textPosition.clone(), Color.rgb(230, 230, 230));

            // Health
            textPosition.offset(-190, 30);
            addText("체력", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("health.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(95, 0);
            addText((int)selectedUnitClass.health() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Velocity
            textPosition.offset(130, -30);
            addText("이동 속도", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("speed.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(95, 0);
            addText((int)(selectedUnitClass.maxVelocity() * 10) + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            if (selectedUnitClass.unitClassType() == Unit.UnitClassType.MELEE_HEALER ||
                selectedUnitClass.unitClassType() == Unit.UnitClassType.RANGED_HEALER) {
                // Heal power
                textPosition.offset(-190, 30);
                addText("치유력", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(-65, 30);
                addIcon("heal.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(95, 0);
                String healPowerString = (selectedUnitClass.healPower() == 0.0f) ?
                        "-" : (int) selectedUnitClass.healPower() + "";
                addText(healPowerString, new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));

                // Heal speed
                textPosition.offset(130, -30);
                addText("치유 속도", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(-65, 30);
                addIcon("heal_speed.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(95, 0);
                String healSpeedString = (selectedUnitClass.healSpeed() == 0)?
                        "-" : 100 / selectedUnitClass.healSpeed() + "";
                addText(healSpeedString, new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            } else {
                // Attack damage
                textPosition.offset(-190, 30);
                addText("공격력", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(-65, 30);
                addIcon("attack.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(35, 0);
                addIcon("ranged_attack.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(53, 0);
                addSymbolText("/", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
                textPosition.offset(42, 0);
                String meleeAttackDamageString =
                        ((selectedUnitClass.meleeAttackDamage() == 0.0f) ?
                                "-" : (int) selectedUnitClass.meleeAttackDamage()) + "/" +
                                ((selectedUnitClass.rangedAttackDamage() == 0.0f) ?
                                        "-" : (int) selectedUnitClass.rangedAttackDamage());
                addText(meleeAttackDamageString, new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));

                // Attack speed
                textPosition.offset(95, -30);
                addText("공격 속도", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(-65, 30);
                addIcon("attack.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(35, 0);
                addIcon("ranged_attack.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(53, 0);
                addSymbolText("/", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
                textPosition.offset(42, 0);
                String attackSpeedString =
                        ((selectedUnitClass.meleeAttackSpeed() == 0) ?
                                "-" : 100 / selectedUnitClass.meleeAttackSpeed()) + "/" +
                                ((selectedUnitClass.rangedAttackSpeed() == 0) ?
                                        "-" : 100 / selectedUnitClass.rangedAttackSpeed());
                addText(attackSpeedString, new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
                textPosition.offset(-35, 0);
            }

            // Armor
            textPosition.offset(-190, 30);
            addText("방어도", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("armor.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(95, 0);
            addText((selectedUnitClass.armor() == 0.0f)?"-":(int)selectedUnitClass.armor() + "%",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Evasion
            textPosition.offset(130, -30);
            addText("회피", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("evade.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(95, 0);
            String evasionString =
                    ((selectedUnitClass.meleeEvasion() == 0) ?
                    "-":(int)selectedUnitClass.meleeEvasion()) + "/" +
                    ((selectedUnitClass.rangedEvasion() == 0) ?
                    "-":(int)selectedUnitClass.rangedEvasion()) + "%";
            addText(evasionString, new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Strong/Weakness
            textPosition.setTo(115, -168);
            addText("지원", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(selectedUnitClass.isSupportable()?"가능":"불가",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
            textPosition.offset(0, 30);
            addText("설명",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(70, 75);
            addLongText(selectedUnitClass.description(),
                    new SizeF(290, 120), textPosition.clone(),
                    Color.rgb(230, 230, 230));
        } else {
            backgroundSprite.setGridIndex(0, 0);

            PointF textPosition = new PointF(-245 + 75, -168);
            addText("클래스를 선택하세요.", new SizeF(300, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
        }
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isVisible()) {
            return false;
        }

        // It consumes all input when activated
        super.onTouchEvent(event);

        return true;
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {

        if (button == cancelButton) {
            // Cancel button
            eventHandler.onUnitBuilderBoxSelected(this, null);
        } else if (button == selectButton) {
            // Select button
            eventHandler.onUnitBuilderBoxSelected(this, selectedUnitClass);
            Engine2D.GetInstance().playSoundEffect("coin1", 1.0f);
        } else {
            // Unit selection buttons
            Unit.UnitClass pressedUnitClass = null;
            for (int i = 0; i < unitButtons.length; ++i) {
                if (button == unitButtons[i]) {
                    pressedUnitClass = Unit.UnitClass.values()[i];
                    break;
                }
            }

            // Reset prev button
            if (selectedUnitClass != null) {
                int unitClassIndex = selectedUnitClass.ordinal();
                if (villager.isAffordable(selectedUnitClass, replacementUnitClass)) {
                    unitButtons[unitClassIndex].setImageSpriteSet(unitClassIndex * 4);
                } else {
                    unitButtons[unitClassIndex].setImageSpriteSet(unitClassIndex * 4 + 2);
                }
            }

            // Set selectedUnitClass
            int unitClassIndex = pressedUnitClass.ordinal();
            if (selectedUnitClass != null && selectedUnitClass == pressedUnitClass) {
                if (villager.isAffordable(selectedUnitClass, replacementUnitClass)) {
                    button.setImageSpriteSet(unitClassIndex * 4);
                } else {
                    button.setImageSpriteSet(unitClassIndex * 4 + 2);
                }

                selectedUnitClass = null;
            } else {
                selectedUnitClass = pressedUnitClass;

                if (villager.isAffordable(selectedUnitClass, replacementUnitClass)) {
                    button.setImageSpriteSet(unitClassIndex * 4 + 1);
                } else {
                    button.setImageSpriteSet(unitClassIndex * 4 + 3);
                }
            }

            updateUnitInfo();
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
        addSprite(new TextSprite.Builder("text"+textIndex, text, 23)
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
    private void addSymbolText(String text, SizeF size, PointF position, int fontColor) {
        addSprite(new TextSprite.Builder("text"+textIndex, text, 23)
                .fontColor(fontColor)
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
    private void addLongText(String text, SizeF size, PointF position, int fontColor) {
        addSprite(new TextSprite.Builder("text"+textIndex, text, 24)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(61, 61, 61), 2.0f)
                .horizontalAlign(Layout.Alignment.ALIGN_NORMAL)
                .verticalAlign(Layout.Alignment.ALIGN_NORMAL)
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
    private Button cancelButton;
    private Button selectButton;
    private Button[] unitButtons = new Button[Unit.UnitClass.values().length];
    private Unit.UnitClass selectedUnitClass = null;
    private Unit.UnitClass replacementUnitClass;
    private int textIndex = 0;
}
