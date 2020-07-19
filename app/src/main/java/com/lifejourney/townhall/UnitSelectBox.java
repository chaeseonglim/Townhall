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

public class UnitSelectBox extends Widget implements Button.Event{

    private final String LOG_TAG = "UnitSelectBox";

    public interface Event {

        void onUnitBuilderBoxSelected(UnitSelectBox infoBox, Unit.UnitClass unitClass);
    }

    public UnitSelectBox(Event eventHandler, Villager villager, Unit.UnitClass replacementClass,
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
            PointF textPosition = new PointF(-250, -155);
            addText("클래스", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(selectedUnitClass.toGameString(), new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));

            // Strong/Weakness
            textPosition.setTo(100, -155);
            addText("강점",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText("없음",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
            textPosition.offset(0, 30);
            addText("약점",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText("없음",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
        } else {
            PointF textPosition = new PointF(-250 + 75, -155);
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
