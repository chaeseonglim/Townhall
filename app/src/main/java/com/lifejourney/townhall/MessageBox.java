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

import java.util.ArrayList;

public class MessageBox extends Widget {

    private final String LOG_TAG = "MessageBox";

    public interface Event {

        void onMessageBoxTouched(MessageBox messageBox);
    }

    public static class Builder {

        private Event listener;
        private Rect region;
        private ArrayList<String> messages;

        private String imageSpriteAsset = "messagebox_bg.png";
        private float fontSize = 35.0f;
        private int textColor = Color.argb(255, 255, 255, 255);
        private int layer = 0;
        private float depth = 0.0f;

        Builder(Event listener, Rect region, String message) {
            this.listener = listener;
            this.region = region;
            this.messages = new ArrayList<>();
            messages.add(message);
        }
        Builder(Event listener, Rect region, ArrayList<String> messages) {
            this.listener = listener;
            this.region = region;
            this.messages = messages;
        }
        Builder imageSpriteAsset(String imageSpriteAsset) {
            this.imageSpriteAsset = imageSpriteAsset;
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
        MessageBox build() {
            return new MessageBox(this);
        }
    }

    private MessageBox(Builder builder) {

        super(builder.region, builder.layer, builder.depth);
        eventHandler = builder.listener;

        Sprite imageSprite = new Sprite.Builder(builder.imageSpriteAsset)
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(builder.layer).depth(0.2f)
                .gridSize(2, 1).visible(false).build();
        addSprite(imageSprite);
        Sprite shadowSprite = new Sprite.Builder(builder.imageSpriteAsset)
                .size(new SizeF(getRegion().size()))
                .positionOffset(new PointF(5, 5))
                .smooth(false).layer(builder.layer).depth(0.1f).opaque(0.2f)
                .gridSize(2, 1).visible(false).build();
        shadowSprite.setGridIndex(1, 0);
        addSprite(shadowSprite);

        pages = new ArrayList<>();
        for (int i = 0; i < builder.messages.size(); ++i) {
            Sprite textSprite =
                new TextSprite.Builder("messagebox"+i, builder.messages.get(i), builder.fontSize)
                    .fontColor(builder.textColor)
                    .bgColor(Color.argb(0, 0, 0, 0))
                    .textAlign(Paint.Align.LEFT)
                    .size(new SizeF(getRegion().size().add(-TEXT_MARGIN*2, -TEXT_MARGIN*2)))
                    .positionOffset(new PointF(TEXT_MARGIN, TEXT_MARGIN))
                    .smooth(true).depth(0.3f)
                    .layer(builder.layer).visible(false).build();
            pages.add(textSprite);
            addSprite(textSprite);
        }
    }

    /**
     *
     */
    @Override
    public void close() {

        super.close();
        pages = null;
    }

    /**
     *
     */
    @Override
    public void commit() {

        super.commit();
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

        int eventAction = event.getAction();

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                if (checkIfInputEventInRegion(event)) {
                    touched = true;
                    currentPage++;
                    currentPage %= pages.size();
                    for (Sprite sprite: pages) {
                        sprite.hide();
                    }
                    pages.get(currentPage).show();

                    if (eventHandler != null) {
                        eventHandler.onMessageBoxTouched(this);
                    }
                    return true;
                }
                else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                return touched;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (touched) {
                    touched = false;
                    return true;
                }
                else {
                    return false;
                }
            default:
                return false;
        }
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {

        super.setVisible(visible);

        if (currentPage < pages.size()) {
            pages.get(currentPage).setVisible(visible);
        }
    }

    /**
     *
     * @return
     */
    public int getTotalPage() {

        return pages.size();
    }

    /**
     *
     * @return
     */
    public int getCurrentPage() {

        return currentPage;
    }

    private final int TEXT_MARGIN = 12;

    private Event eventHandler;
    private ArrayList<Sprite> pages;
    private int currentPage = 0;
    private boolean touched = false;
}
