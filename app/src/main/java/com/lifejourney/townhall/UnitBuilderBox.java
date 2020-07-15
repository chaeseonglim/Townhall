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

import java.text.NumberFormat;
import java.util.Locale;

public class UnitBuilderBox extends Widget implements Button.Event{

    private final String LOG_TAG = "UnitBuilderBox";

    public interface Event {

        void onUnitBuilderBoxSelected(UnitBuilderBox infoBox, Unit.UnitClass unitClass);
    }

    public UnitBuilderBox(Event listener, Rect region, int layer, float depth) {

        super(region, layer, depth);

        this.listener = listener;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("unit_builder_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(layer).depth(depth)
                .gridSize(1, 1).visible(false).opaque(0.8f).build();
        addSprite(backgroundSprite);

        // Cancel button
        Rect cancelButtonRegion = new Rect(region.right() - 155, region.bottom() - 67,
                150, 60);
        cancelButton = new Button.Builder(this, cancelButtonRegion)
                .message("취소").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 0))
                .build();
        addWidget(cancelButton);

        // Select button
        Rect selectButtonRegion = new Rect(region.right() - 155, region.bottom() - 67,
                150, 60);
        selectButton = new Button.Builder(this, selectButtonRegion)
                .message("선택").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 0))
                .build();
        addWidget(selectButton);

        // Tile type
        PointF textPosition = new PointF(-250, -155);
        addText("지형", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText("-", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));

        // Population
        textPosition.setTo(100, -155);
        addText("인구 / 행복도",
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));
        textPosition.offset(0, 30);
        addText("없음 / 없음",
                new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));
    }

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        selectButton.hide();
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
            setVisible(false);
            listener.onUnitBuilderBoxSelected(this, null);
        } else if (button == selectButton) {
            // Select button
            setVisible(false);
            listener.onUnitBuilderBoxSelected(this, null);
        }
    }

    private Event listener;
    private Button cancelButton;
    private Button selectButton;
}
