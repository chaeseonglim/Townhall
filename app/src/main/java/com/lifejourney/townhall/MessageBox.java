package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Widget;

import java.util.ArrayList;

public class MessageBox extends Widget {

    private final String LOG_TAG = "MessageBox";

    interface Event {

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
        MessageBox build() {
            return new MessageBox(this);
        }
    }

    private MessageBox(Builder builder) {
        super(builder.region, builder.layer);

        eventHandler = builder.eventHandler;

        bg = new Sprite.Builder(builder.bgAsset)
                .size(getRegion().size())
                .smooth(false).depth(0.2f)
                .layer(builder.layer).visible(false).build();
        shadow = new Sprite.Builder(builder.bgAsset)
                .size(getRegion().size())
                .smooth(false).depth(0.1f).opaque(0.2f)
                .layer(builder.layer).visible(false).build();

        pages = new ArrayList<>();
        for (int i = 0; i < builder.messages.size(); ++i) {
            Sprite sprite =
                    new Sprite.Builder("messagebox"+i, builder.messages.get(i), builder.fontSize,
                            builder.textColor, Color.argb(0, 0, 0, 0), Paint.Align.LEFT)
                            .size(getRegion().size().add(-TEXT_MARGIN*2, -TEXT_MARGIN*2))
                            .smooth(true).depth(0.3f)
                            .layer(builder.layer).visible(false).build();
            pages.add(sprite);
        }
    }

    /**
     *
     */
    @Override
    public void close() {

        for (Sprite sprite: pages) {
            sprite.close();
        }
        pages.clear();
        bg.close();
        shadow.close();
    }

    /**
     *
     */
    @Override
    public void commit() {

        super.commit();

        if (currentPage < pages.size()) {
            pages.get(currentPage).setPos(new Point(getScreenRegion().center().offset(TEXT_MARGIN, TEXT_MARGIN)));
            pages.get(currentPage).commit();
        }

        bg.setPos(new Point(getScreenRegion().center()));
        bg.commit();

        shadow.setPos(new Point(getScreenRegion().center()).offset(5, 5));
        shadow.commit();
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!super.onTouchEvent(event)) {
            return false;
        }

        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_DOWN) {
            currentPage++;
            currentPage %= pages.size();
            if (eventHandler != null) {
                eventHandler.onMessageBoxTouched(this);
            }
        }

        return true;
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
        bg.setVisible(visible);
        shadow.setVisible(visible);
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

    private final int TEXT_MARGIN = 15;

    private Event eventHandler;
    private Sprite bg, shadow;
    private ArrayList<Sprite> pages;
    private int currentPage = 0;
}
