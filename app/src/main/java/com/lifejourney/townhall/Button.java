package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Widget;

import java.util.prefs.PreferencesFactory;

public class Button extends Widget {

    private final String LOG_TAG = "Button";

    interface Event {

        void onButtonPressed(Button button);

    }

    public static class Builder {

        private Button.Event eventHandler;
        private Rect region;
        private String message;

        private String bgAsset = "button_bg.png";
        private float fontSize = 35.0f;
        private int textColor = Color.argb(255, 255, 255, 255);
        private int layer = 0;
        private float depth = 0.0f;

        Builder(Button.Event eventHandler, Rect region, String message) {
            this.eventHandler = eventHandler;
            this.region = region;
            this.message = message;
        }
        Builder bgAsset(String bgAsset) {
            this.bgAsset = bgAsset;
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
        Button build() {
            return new Button(this);
        }
    }

    private Button(Builder builder) {

        super(builder.region, builder.layer, builder.depth);

        eventHandler = builder.eventHandler;

        bg = new Sprite.Builder(builder.bgAsset)
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.2f)
                .gridSize(2, 1)
                .layer(builder.layer).visible(false).build();
        shadow = new Sprite.Builder(builder.bgAsset)
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.1f).opaque(0.2f)
                .gridSize(2, 1)
                .layer(builder.layer).visible(false).build();

        msg = new Sprite.Builder("button"+uid++, builder.message, builder.fontSize,
                builder.textColor, Color.argb(0, 0, 0, 0), Paint.Align.CENTER)
                .size(new SizeF(builder.region.size()))
                .smooth(true).depth(0.3f)
                .layer(builder.layer).visible(false).build();
    }

    @Override
    public void close() {
        msg.close();
        shadow.close();
        bg.close();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible()) {
            return false;
        }

        int eventAction = event.getAction();
        boolean result;

        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:
                if (checkIfInputEventInRegion(event)) {
                    bg.setGridIndex(1, 0);
                    pressed = true;
                    result = true;
                }
                else {
                    result = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pressed) {
                    result = true;
                }
                else {
                    result = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (pressed) {
                    pressed = false;
                    bg.setGridIndex(0, 0);
                    result = true;
                }
                else {
                    result = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pressed) {
                    pressed = false;
                    bg.setGridIndex(0, 0);
                    if (checkIfInputEventInRegion(event) && eventHandler != null) {
                        eventHandler.onButtonPressed(this);
                    }
                    result = true;
                }
                else {
                    result = false;
                }
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    @Override
    public void commit() {

        super.commit();

        if (bg.getGridIndex().equals(new Point(1, 0))) {
            msg.setPosition(new PointF(getScreenRegion().center().offset(3, 3)));
            bg.setPosition(new PointF(getScreenRegion().center().offset(3, 3)));
        }
        else {
            msg.setPosition(new PointF(getScreenRegion().center()));
            bg.setPosition(new PointF(getScreenRegion().center()));
        }
        shadow.setPosition(new PointF(getScreenRegion().center().offset(3, 3)));
        msg.commit();
        bg.commit();
        shadow.commit();
    }

    @Override
    public void setVisible(boolean visible) {

        super.setVisible(visible);

        msg.setVisible(visible);
        bg.setVisible(visible);
        shadow.setVisible(visible);
    }

    private static int uid = 0;

    private Event eventHandler;
    private Sprite msg;
    private Sprite bg, shadow;
    private boolean pressed = false;
}
