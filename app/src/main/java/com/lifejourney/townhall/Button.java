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
        private String fontName = null;
        private float fontSize = 35.0f;
        private int fontColor = Color.argb(255, 255, 255, 255);
        private int layer = 0;
        private float depth = 0.0f;
        private int shadowColor = Color.rgb(0, 0, 0);
        private float shadowDepth = 0.0f;

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
        Builder fontName(String fontName) {
            this.fontName = fontName;
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
        Builder fontShadow(int shadowColor, float shadowDepth) {
            this.shadowColor = shadowColor;
            this.shadowDepth = shadowDepth;
            return this;
        }
        Button build() {
            return new Button(this);
        }
    }

    private Button(Builder builder) {

        super(builder.region, builder.layer, builder.depth);

        eventHandler = builder.eventHandler;
        name = builder.name;
        fontSize = builder.fontSize;
        fontColor = builder.fontColor;
        fontName = builder.fontName;
        layer = builder.layer;
        fontShadowColor = builder.shadowColor;
        fontShadowDepth = builder.shadowDepth;
        message = builder.message;

        if (!builder.imageSpriteAsset.equals("")) {
            imageSprite = new Sprite.Builder(builder.imageSpriteAsset)
                    .size(new SizeF(getRegion().size()))
                    .smooth(true).depth(0.2f)
                    .gridSize(3, builder.numImageSpriteSet)
                    .layer(builder.layer).visible(false).build();
            imageSprite.setGridIndex(0, imageSpriteSet);
            addSprite(imageSprite);
        }

        if (builder.message != null) {
            setMessage(message);
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
                    Engine2D.GetInstance().playSoundEffect("click3", 1.0f);
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
                    .fontColor(fontColor).fontName(fontName)
                    .shadow(fontShadowColor, fontShadowDepth)
                    .horizontalAlign(Layout.Alignment.ALIGN_CENTER)
                    .verticalAlign(Layout.Alignment.ALIGN_CENTER)
                    .size(new SizeF(getRegion().size()))
                    .smooth(true).depth(0.3f)
                    .layer(layer).visible(false).build();
            addSprite(messageSprite);
        } else {
            messageSprite.setFontColor(fontColor);
            messageSprite.setShadow(fontShadowColor, fontShadowDepth);
            messageSprite.setText(message);
        }
    }

    /**
     *
     * @param fontColor
     */
    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    /**
     *
     * @param fontShadowColor
     */
    public void setFontShadow(int fontShadowColor, float fontShadowDepth) {
        this.fontShadowColor = fontShadowColor;
        this.fontShadowDepth = fontShadowDepth;
    }

    /**
     *
     */
    public void redraw() {
        if (message != null) {
            setMessage(message);
        }
    }

    private static int UID = 0;

    private String name;
    private String message;
    private Event eventHandler;
    private TextSprite messageSprite = null;
    private float fontSize;
    private int fontColor;
    private String fontName;
    private int layer;
    private Sprite imageSprite = null;
    private int imageSpriteSet = 0;
    private boolean pressed = false;
    private boolean disabled = false;
    private int fontShadowColor;
    private float fontShadowDepth;
}
