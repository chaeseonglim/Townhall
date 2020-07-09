package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Widget;

import java.util.ArrayList;

public class MessageBox extends Widget {

    private final String LOG_TAG = "MessageBox";

    public interface Event {

        void onMessageBoxTouched(MessageBox messageBox);
    }

    public static class Builder {

        private Event eventHandler;
        private Rect region;
        private ArrayList<String> messages;

        private String bgAsset = "messagebox_bg.png";
        private float fontSize = 35.0f;
        private int textColor = Color.argb(255, 255, 255, 255);
        private int layer = 0;
        private float depth = 0.0f;

        Builder(Event eventHandler, Rect region, String message) {
            this.eventHandler = eventHandler;
            this.region = region;
            this.messages = new ArrayList<>();
            messages.add(message);
        }
        Builder(Event eventHandler, Rect region, ArrayList<String> messages) {
            this.eventHandler = eventHandler;
            this.region = region;
            this.messages = messages;
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
        MessageBox build() {
            return new MessageBox(this);
        }
    }

    private MessageBox(Builder builder) {

        super(builder.region, builder.layer, builder.depth);

        eventHandler = builder.eventHandler;

        Sprite bg = new Sprite.Builder(builder.bgAsset)
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(builder.layer).depth(0.2f)
                .gridSize(2, 1).visible(false).build();
        addSprite(bg);
        Sprite shadow = new Sprite.Builder(builder.bgAsset)
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(builder.layer).depth(0.1f).opaque(0.2f)
                .gridSize(2, 1).visible(false).build();
        shadow.setGridIndex(1, 0);
        shadow.setPositionOffset(new PointF(5, 5));
        addSprite(shadow);

        pages = new ArrayList<>();
        for (int i = 0; i < builder.messages.size(); ++i) {
            Sprite sprite =
                    new Sprite.Builder("messagebox"+i, builder.messages.get(i), builder.fontSize,
                            builder.textColor, Color.argb(0, 0, 0, 0), Paint.Align.LEFT)
                            .size(new SizeF(getRegion().size().add(-TEXT_MARGIN*2, -TEXT_MARGIN*2)))
                            .smooth(true).depth(0.3f)
                            .layer(builder.layer).visible(false).build();
            sprite.setPositionOffset(new PointF(TEXT_MARGIN, TEXT_MARGIN));
            pages.add(sprite);
            addSprite(sprite);
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
