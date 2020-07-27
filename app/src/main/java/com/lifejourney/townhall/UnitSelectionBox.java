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

public class UnitSelectionBox extends Widget implements Button.Event{

    private final String LOG_TAG = "UnitSelectionBox";

    public interface Event {

        void onUnitBuilderBoxSelected(UnitSelectionBox infoBox, Unit.UnitClass unitClass);
    }

    public UnitSelectionBox(Event eventHandler, Villager villager, Unit.UnitClass replacementClass,
                            Rect region, int layer, float depth) {

        super(region, layer, depth);

        this.eventHandler = eventHandler;
        this.villager = villager;
        this.replacementUnitClass = replacementClass;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("unit_builder_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(layer).depth(depth)
                .gridSize(1, 1).visible(false).opaque(0.8f).build();
        addSprite(backgroundSprite);

        // Cancel button
        Rect cancelButtonRegion = new Rect(region.right() - 140, region.bottom() - 65,
                136, 60);
        cancelButton = new Button.Builder(this, cancelButtonRegion)
                .message("취소").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 255))
                .build();
        addWidget(cancelButton);

        // Select button
        Rect selectButtonRegion = new Rect(region.right() - 140, region.bottom() - 65,
                136, 60);
        selectButton = new Button.Builder(this, selectButtonRegion)
                .message("선택").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 255))
                .build();
        addWidget(selectButton);

        // Unit button
        Rect unitButtonRegion =
                new Rect(region.left() + 22, region.bottom() - 65,
                        56, 60);
        for (int i = 0; i < Unit.UnitClass.values().length; ++i) {
            unitButtons[i] =
                    new Button.Builder(this, unitButtonRegion.clone())
                            .imageSpriteAsset("unit_selection_btn.png")
                            .numImageSpriteSet(Unit.UnitClass.values().length * 4)
                            .layer(layer + 1).build();
            Unit.UnitClass unitClass = Unit.UnitClass.values()[i];
            if (villager.isAffordable(unitClass, replacementUnitClass)) {
                unitButtons[i].setImageSpriteSet(i * 4);
            } else {
                unitButtons[i].setImageSpriteSet(i * 4 + 2);
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
        if (selectedUnitClass == null || !villager.isAffordable(selectedUnitClass, replacementUnitClass)) {
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
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addText(String text, SizeF size, PointF position, int fontColor) {

        addSprite(new TextSprite.Builder("text"+textIndex, text, 25)
                .fontColor(fontColor).bgColor(Color.argb(0, 0, 0, 0))
                .fontName("NanumBarunGothic.ttf")
                .textAlign(Paint.Align.LEFT)
                .size(size).positionOffset(position)
                .smooth(true).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    private void updateUnitInfo() {

        // Remove all previous texts
        removeSprites("text"+textIndex++);

        if (selectedUnitClass != null) {
            // Unit Class
            PointF textPosition = new PointF(-250, -165);
            addText("클래스", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(selectedUnitClass.word(), new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));

            // Population
            textPosition.offset(150, -30);
            addText("인구", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(selectedUnitClass.population()+"", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));

            // Purchase gold
            textPosition.offset(-150, 30);
            addText("구매 비용", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(selectedUnitClass.costToPurchase()+"", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));

            // Upkeep gold
            textPosition.offset(150, -30);
            addText("유지 비용", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(selectedUnitClass.costUpkeep()+"", new SizeF(150, 40),
                    textPosition.clone(), Color.rgb(255, 255, 255));

            // Health
            textPosition.offset(-150, 30);
            addText("체력", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText((int)selectedUnitClass.health() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));

            // Velocity
            textPosition.offset(150, -30);
            addText("이동 속도", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText((int)(selectedUnitClass.maxVelocity() * 10) + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));


            if (selectedUnitClass.unitClassType() == Unit.UnitClassType.MELEE_HEALER ||
                selectedUnitClass.unitClassType() == Unit.UnitClassType.RANGED_HEALER) {
                // Heal power
                textPosition.offset(-150, 30);
                addText("치유력", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(0, 30);
                String healPowerString = (selectedUnitClass.healPower() == 0.0f) ?
                        "-" : (int) selectedUnitClass.healPower() + "";
                addText(healPowerString, new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));

                // Heal speed
                textPosition.offset(150, -30);
                addText("치유 속도", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(0, 30);
                String healSpeedString = (selectedUnitClass.healSpeed() == 0)?
                        "-" : 100 / selectedUnitClass.healSpeed() + "";
                addText(healSpeedString, new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));
            } else {
                // Attack damage
                textPosition.offset(-150, 30);
                addText("공격력", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(0, 30);
                String attackDamageString =
                        ((selectedUnitClass.meleeAttackDamage() == 0.0f) ?
                                "-" : (int) selectedUnitClass.meleeAttackDamage()) + "/" +
                                ((selectedUnitClass.rangedAttackDamage() == 0.0f) ?
                                        "-" : (int) selectedUnitClass.rangedAttackDamage());
                addText(attackDamageString, new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));

                // Attack speed
                textPosition.offset(150, -30);
                addText("공격 속도", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
                textPosition.offset(0, 30);
                String attackSpeedString =
                        ((selectedUnitClass.meleeAttackSpeed() == 0) ?
                                "-" : 100 / selectedUnitClass.meleeAttackSpeed()) + "/" +
                                ((selectedUnitClass.rangedAttackSpeed() == 0) ?
                                        "-" : 100 / selectedUnitClass.rangedAttackSpeed());
                addText(attackSpeedString, new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));
            }

            // Armor
            textPosition.offset(-150, 30);
            addText("방어도", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText((selectedUnitClass.armor() == 0.0f)?"-":(int)selectedUnitClass.armor() + "%",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));

            // Evasion
            textPosition.offset(150, -30);
            addText("회피", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            String evasionString =
                    ((selectedUnitClass.meleeEvasion() == 0) ?
                    "-":(int)selectedUnitClass.meleeEvasion()+"%") + "/" +
                    ((selectedUnitClass.rangedEvasion() == 0) ?
                    "-":(int)selectedUnitClass.rangedEvasion()+"%");
            addText(evasionString, new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));

            // Strong/Weakness
            textPosition.setTo(100, -165);
            addText("지원", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(selectedUnitClass.isSupportable()?"가능":"불가",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
            textPosition.offset(0, 30);
            addText("설명",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(75, 70);
            addText(selectedUnitClass.description(),
                    new SizeF(300, 120), textPosition.clone(),
                    Color.rgb(255, 255, 255));
        } else {
            PointF textPosition = new PointF(-250 + 75, -165);
            addText("클래스를 선택하세요.", new SizeF(300, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
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

    private Event eventHandler;
    private Villager villager;
    private Button cancelButton;
    private Button selectButton;
    private Button[] unitButtons = new Button[Unit.UnitClass.values().length];
    private Unit.UnitClass selectedUnitClass = null;
    private Unit.UnitClass replacementUnitClass;
    private int textIndex = 0;
}
