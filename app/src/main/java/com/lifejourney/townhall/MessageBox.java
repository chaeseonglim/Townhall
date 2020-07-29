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

public class MessageBox extends Widget implements Button.Event {

    private final String LOG_TAG = "MessageBox";

    enum Type {
        CLOSE,
        YES_OR_NO,
        OK_OR_CANCEL
    }

    enum ButtonType {
        CLOSE,
        YES,
        NO,
        OK,
        CANCEL
    }

    public interface Event {

        void onMessageBoxButtonPressed(MessageBox messageBox, ButtonType buttonType);
    }

    public static class Builder {

        private Event listener;
        private Rect region;
        private Type type;
        private String message;

        private String imageSpriteAsset = "messagebox_bg.png";
        private float fontSize = 35.0f;
        private int textColor = Color.rgb(255, 255, 255);
        private int layer = 0;
        private float depth = 0.0f;

        Builder(Event listener, Type type, Rect region, String message) {
            this.listener = listener;
            this.type = type;
            this.region = region;
            this.message = message;
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
        MessageBox build() {
            return new MessageBox(this);
        }
    }

    private MessageBox(Builder builder) {

        super(builder.region, builder.layer, builder.depth);
        eventHandler = builder.listener;
        type = builder.type;

        Sprite bgSprite = new Sprite.Builder(builder.imageSpriteAsset)
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(builder.layer).depth(0.2f)
                .gridSize(2, 1).visible(false).build();
        addSprite(bgSprite);

        if (type == Type.CLOSE) {
            bgSprite.setGridIndex(0, 0);

            // Close button
            Rect closeButtonRegion = new Rect(getRegion().right() - 310, getRegion().bottom() - 65,
                    136, 60);
            closeButton = new Button.Builder(this, closeButtonRegion)
                    .message("닫기").imageSpriteAsset("")
                    .fontSize(25).layer(getLayer()+1).textColor(Color.rgb(230, 230, 230))
                    .build();
            addWidget(closeButton);
        } else if (type == Type.YES_OR_NO) {
            bgSprite.setGridIndex(1, 0);

            // Yes button
            Rect yesButtonRegion = new Rect(getRegion().right() - 283, getRegion().bottom() - 65,
                    136, 60);
            yesButton = new Button.Builder(this, yesButtonRegion)
                    .message("예").imageSpriteAsset("")
                    .fontSize(25).layer(getLayer()+1).textColor(Color.rgb(230, 230, 230))
                    .build();
            addWidget(yesButton);

            // No button
            Rect noButtonRegion = new Rect(getRegion().right() - 143, getRegion().bottom() - 65,
                    136, 60);
            noButton = new Button.Builder(this, noButtonRegion)
                    .message("아니오").imageSpriteAsset("")
                    .fontSize(25).layer(getLayer()+1).textColor(Color.rgb(230, 230, 230))
                    .build();
            addWidget(noButton);
        } else if (type == Type.OK_OR_CANCEL) {
            bgSprite.setGridIndex(1, 0);

            // OK button
            Rect okButtonRegion = new Rect(getRegion().right() - 283, getRegion().bottom() - 65,
                    136, 60);
            okButton = new Button.Builder(this, okButtonRegion)
                    .message("확인").imageSpriteAsset("")
                    .fontSize(25).layer(getLayer()+1).textColor(Color.rgb(230, 230, 230))
                    .build();
            addWidget(okButton);

            // Cancel button
            Rect cancelButtonRegion = new Rect(getRegion().right() - 143, getRegion().bottom() - 65,
                    136, 60);
            cancelButton = new Button.Builder(this, cancelButtonRegion)
                    .message("취소").imageSpriteAsset("")
                    .fontSize(25).layer(getLayer()+1).textColor(Color.rgb(230, 230, 230))
                    .build();
            addWidget(cancelButton);
        }

        textSprite =
            new TextSprite.Builder("messagebox", builder.message, builder.fontSize)
                .fontColor(builder.textColor)
                .bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(Paint.Align.LEFT)
                .size(new SizeF(getRegion().size().add(-TEXT_MARGIN*2, -TEXT_MARGIN*2)))
                .positionOffset(new PointF(TEXT_MARGIN, TEXT_MARGIN))
                .smooth(true).depth(0.3f)
                .layer(builder.layer).visible(false).build();
        addSprite(textSprite);
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {

        if (button == yesButton) {
            eventHandler.onMessageBoxButtonPressed(this, ButtonType.YES);
        } else if (button == noButton) {
            eventHandler.onMessageBoxButtonPressed(this, ButtonType.NO);
        } else if (button == okButton) {
            eventHandler.onMessageBoxButtonPressed(this, ButtonType.OK);
        } else if (button == cancelButton) {
            eventHandler.onMessageBoxButtonPressed(this, ButtonType.CANCEL);
        } else if (button == closeButton) {
            eventHandler.onMessageBoxButtonPressed(this, ButtonType.CLOSE);
        }
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

    private final int TEXT_MARGIN = 12;

    private Event eventHandler;
    private Sprite textSprite;
    private Type type;
    private Button yesButton;
    private Button noButton;
    private Button okButton;
    private Button cancelButton;
    private Button closeButton;
}
