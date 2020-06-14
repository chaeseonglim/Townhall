package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Widget;

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
        Button build() {
            return new Button(this);
        }
    }

    private Button(Builder builder) {

        super(builder.region, builder.layer);

        eventHandler = builder.eventHandler;

        bg = new Sprite.Builder(builder.bgAsset)
                .size(getRegion().size())
                .smooth(false).depth(0.2f)
                .gridSize(new Size(2, 1))
                .layer(builder.layer).visible(false).build();
        shadow = new Sprite.Builder(builder.bgAsset)
                .size(getRegion().size())
                .smooth(false).depth(0.1f).opaque(0.2f)
                .gridSize(new Size(2, 1))
                .layer(builder.layer).visible(false).build();

        msg = new Sprite.Builder("button"+uid++, builder.message, builder.fontSize,
                builder.textColor, Color.argb(0, 0, 0, 0), Paint.Align.CENTER)
                .size(builder.region.size())
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

        if (!super.onTouchEvent(event)) {
            return false;
        }

        int eventAction = event.getAction();

        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:
                setCaptureInput(true);
                bg.setGridIndex(new Point(1, 0));
                break;
            case MotionEvent.ACTION_CANCEL:
                setCaptureInput(false);
                bg.setGridIndex(new Point(0, 0));
                break;
            case MotionEvent.ACTION_UP:
                setCaptureInput(false);
                bg.setGridIndex(new Point(0, 0));
                if (checkIfInputEventInRegion(event) && eventHandler != null) {
                    eventHandler.onButtonPressed(this);
                }
                break;
        }


        return true;
    }

    @Override
    public void commit() {

        super.commit();

        if (bg.getGridIndex().equals(new Point(1, 0))) {
            msg.setPosition(new Point(getScreenRegion().center().offset(3, 3)));
            bg.setPosition(new Point(getScreenRegion().center().offset(3, 3)));
        }
        else {
            msg.setPosition(new Point(getScreenRegion().center()));
            bg.setPosition(new Point(getScreenRegion().center()));
        }
        shadow.setPosition(new Point(getScreenRegion().center().offset(3, 3)));
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
}
