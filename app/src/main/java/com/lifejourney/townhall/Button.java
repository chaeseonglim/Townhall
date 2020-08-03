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

public class Button extends Widget {

    private final String LOG_TAG = "Button";

    public interface Event {

        void onButtonPressed(Button button);
    }

    public static class Builder {

        private Button.Event eventHandler;
        private Rect region;

        private String name;
        private String message = null;
        private String imageSpriteAsset = "button_bg.png";
        private int numImageSpriteSet = 1;
        private float fontSize = 35.0f;
        private int fontColor = Color.argb(255, 255, 255, 255);
        private int layer = 0;
        private float depth = 0.0f;
        private boolean shadow = false;

        Builder(Button.Event eventHandler, Rect region) {
            this.eventHandler = eventHandler;
            this.region = region;
        }
        Builder name(String name) {
            this.name = name;
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
        Builder message(String message) {
            this.message = message;
            return this;
        }
        Builder fontSize(float fontSize) {
            this.fontSize = fontSize;
            return this;
        }
        Builder fontColor(int textColor) {
            this.fontColor = textColor;
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

        eventHandler = builder.eventHandler;
        shadow = builder.shadow;
        name = builder.name;
        fontSize = builder.fontSize;
        fontColor = builder.fontColor;
        layer = builder.layer;

        if (!builder.imageSpriteAsset.equals("")) {
            imageSprite = new Sprite.Builder(builder.imageSpriteAsset)
                    .size(new SizeF(getRegion().size()))
                    .smooth(false).depth(0.2f)
                    .gridSize(3, builder.numImageSpriteSet)
                    .layer(builder.layer).visible(false).build();
            imageSprite.setGridIndex(0, imageSpriteSet);
            addSprite(imageSprite);
        }

        if (shadow) {
            Sprite shadowSprite = new Sprite.Builder("shadow", builder.imageSpriteAsset)
                    .size(new SizeF(getRegion().size()))
                    .positionOffset(new PointF(3, 3))
                    .smooth(false).depth(0.1f).opaque(0.2f)
                    .gridSize(3, 1)
                    .layer(builder.layer).visible(false).build();
            addSprite(shadowSprite);
        }

        if (builder.message != null) {
            messageSprite = new TextSprite.Builder("button" + UID++, builder.message, builder.fontSize)
                    .fontColor(builder.fontColor)
                    .bgColor(Color.argb(0, 0, 0, 0))
                    .textAlign(Paint.Align.CENTER)
                    .size(new SizeF(getRegion().size()))
                    .smooth(true).depth(0.3f)
                    .layer(builder.layer).visible(false).build();
            addSprite(messageSprite);
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

        if (super.onTouchEvent(event)) {
            return true;
        }

        if (disabled && checkIfInputEventInRegion(event)) {
            return true;
        }

        boolean handledResult;
        int eventAction = event.getAction();

        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:
                if (checkIfInputEventInRegion(event)) {
                    if (imageSprite != null) {
                        imageSprite.setGridIndex(1, imageSpriteSet);
                        imageSprite.setPositionOffset(new PointF(3, 3));
                    }
                    if (messageSprite != null) {
                        messageSprite.setPositionOffset(new PointF(3, 3));
                    }
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
                    if (imageSprite != null) {
                        imageSprite.setGridIndex(0, imageSpriteSet);
                        imageSprite.setPositionOffset(new PointF(0, 0));
                    }
                    if (messageSprite != null) {
                        messageSprite.setPositionOffset(new PointF(0, 0));
                    }
                    handledResult = true;
                } else {
                    handledResult = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pressed) {
                    pressed = false;
                    if (imageSprite != null) {
                        imageSprite.setGridIndex(0, imageSpriteSet);
                        imageSprite.setPositionOffset(new PointF(0, 0));
                    }
                    if (messageSprite != null) {
                        messageSprite.setPositionOffset(new PointF(0, 0));
                    }
                    if (checkIfInputEventInRegion(event) && eventHandler != null) {
                        eventHandler.onButtonPressed(this);
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
     * @param imageSpriteSet
     */
    public void setImageSpriteSet(int imageSpriteSet) {

        if (imageSprite != null) {
            this.imageSpriteSet = imageSpriteSet;
            imageSprite.setGridIndex(imageSprite.getGridIndex().x, imageSpriteSet);
        }
    }

    /**
     *
     */
    public void enable() {
        disabled = false;
        if (imageSprite != null) {
            imageSprite.setGridIndex(0, imageSpriteSet);
        }
    }

    /**
     *
     */
    public void disable() {
        disabled = true;
        if (imageSprite != null) {
            imageSprite.setGridIndex(2, imageSpriteSet);
        }
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param message
     */
    public void setMessage(String message) {

        if (messageSprite == null) {
            messageSprite = new TextSprite.Builder("button" + UID++, message, fontSize)
                    .fontColor(fontColor)
                    .bgColor(Color.argb(0, 0, 0, 0))
                    .textAlign(Paint.Align.CENTER)
                    .size(new SizeF(getRegion().size()))
                    .smooth(true).depth(0.3f)
                    .layer(layer).visible(false).build();
            addSprite(messageSprite);
        } else {
            messageSprite.setText(message);
        }
    }

    private static int UID = 0;

    private String name;
    private Event eventHandler;
    private TextSprite messageSprite = null;
    private float fontSize;
    private int fontColor;
    private int layer;
    private Sprite imageSprite = null;
    private int imageSpriteSet = 0;
    private boolean shadow;
    private boolean pressed = false;
    private boolean disabled = false;
}
