package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

public class Button extends Widget {

    private final String LOG_TAG = "Button";

    public interface Event {

        void onButtonPressed(Button button);
    }

    public static class Builder {

        private Button.Event eventHandler;
        private Rect region;

        private String message;
        private String imageSpriteAsset = "button_bg.png";
        private int numImageSpriteSet = 1;
        private float fontSize = 35.0f;
        private int textColor = Color.argb(255, 255, 255, 255);
        private int layer = 0;
        private float depth = 0.0f;
        private boolean shadow = false;

        Builder(Button.Event eventHandler, Rect region) {
            this.eventHandler = eventHandler;
            this.region = region;
        }
        Builder message(String message) {
            this.message = message;
            return this;
        }
        Builder imageSpriteAsset(String imageSpriteAsset) {
            this.imageSpriteAsset = imageSpriteAsset;
            return this;
        }
        Builder numImageSpriteSet(int numImageSpriteSet) {
            this.numImageSpriteSet = numImageSpriteSet;
            return this;
        }
        Builder fontSize(float fontSize) {
            this.fontSize = fontSize;
            return this;
        }
        Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }
        Builder layer(int layer) {
            this.layer = layer;
            return this;
        }
        Builder depth(float depth) {
            this.depth = depth;
            return this;
        }
        Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }
        Button build() {
            return new Button(this);
        }
    }

    private Button(Builder builder) {

        super(builder.region, builder.layer, builder.depth);

        listener = builder.eventHandler;
        shadow = builder.shadow;

        imageSprite = new Sprite.Builder(builder.imageSpriteAsset)
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.2f)
                .gridSize(2, builder.numImageSpriteSet)
                .layer(builder.layer).visible(false).build();
        imageSprite.setGridIndex(0, imageSpriteSet);
        addSprite(imageSprite);

        if (shadow) {
            Sprite shadowSprite = new Sprite.Builder("shadow", builder.imageSpriteAsset)
                    .size(new SizeF(getRegion().size()))
                    .positionOffset(new PointF(3, 3))
                    .smooth(false).depth(0.1f).opaque(0.2f)
                    .gridSize(2, 1)
                    .layer(builder.layer).visible(false).build();
            addSprite(shadowSprite);
        }

        if (builder.message != null) {
            messageSprite = new TextSprite.Builder("button" + UID++, builder.message, builder.fontSize)
                    .fontColor(builder.textColor)
                    .bgColor(Color.argb(0, 0, 0, 0))
                    .textAlign(Paint.Align.CENTER)
                    .size(new SizeF(builder.region.size()))
                    .smooth(true).depth(0.3f)
                    .layer(builder.layer).visible(false).build();
            addSprite(messageSprite);
        }
    }

    /**
     *
     */
    @Override
    public void close() {

        super.close();
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

        int eventAction = event.getAction();
        boolean handledResult;

        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:
                if (checkIfInputEventInRegion(event)) {
                    imageSprite.setGridIndex(1, imageSpriteSet);
                    pressed = true;
                    handledResult = true;
                } else {
                    handledResult = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                handledResult = pressed;
                break;
            case MotionEvent.ACTION_CANCEL:
                if (pressed) {
                    pressed = false;
                    imageSprite.setGridIndex(0, imageSpriteSet);
                    handledResult = true;
                } else {
                    handledResult = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pressed) {
                    pressed = false;
                    imageSprite.setGridIndex(0, imageSpriteSet);
                    if (checkIfInputEventInRegion(event) && listener != null) {
                        listener.onButtonPressed(this);
                    }
                    handledResult = true;
                } else {
                    handledResult = false;
                }
                break;
            default:
                handledResult = false;
                break;
        }

        return handledResult;
    }

    /**
     *
     */
    @Override
    public void commit() {

        if (imageSprite.getGridIndex().x == 1) {
            if (messageSprite != null) {
                messageSprite.setPositionOffset(new PointF(3, 3));
            }
            imageSprite.setPositionOffset(new PointF(3, 3));
        } else {
            if (messageSprite != null) {
                messageSprite.setPositionOffset(new PointF(0, 0));
            }
            imageSprite.setPositionOffset(new PointF(0, 0));
        }

        super.commit();
    }

    /**
     *
     * @param imageSpriteSet
     */
    public void setImageSpriteSet(int imageSpriteSet) {

        this.imageSpriteSet = imageSpriteSet;
        imageSprite.setGridIndex(imageSprite.getGridIndex().x, imageSpriteSet);
    }

    private static int UID = 0;

    private Event listener;
    private Sprite messageSprite = null;
    private Sprite imageSprite;
    private int imageSpriteSet = 0;
    private boolean shadow;
    private boolean pressed = false;
}
