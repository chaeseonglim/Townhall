package com.lifejourney.townhall;

import android.graphics.Color;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Widget;

import java.util.ArrayList;

public class MessageBox extends Widget {

    public MessageBox(Rect region, String message, float fontSize, int textColor) {
        super(region);

        Sprite sprite =
                new Sprite.Builder("messagebox", message, fontSize,
                        textColor, Color.argb(255, 255, 0, 0))
                        .size(new Size(region.width, region.height))
                        .smooth(true)
                        .layer(MESSAGEBOX_LAYER).visible(false).build();
        pages = new ArrayList<>();
        pages.add(sprite);
    }

    public MessageBox(Rect region, ArrayList<String> messages, float fontSize, int textColor) {
        super(region);

        pages = new ArrayList<>();
        for (int i = 0; i < messages.size(); ++i) {
            Sprite sprite =
                    new Sprite.Builder("messagebox"+i, messages.get(i), fontSize,
                            textColor, Color.argb(0, 0, 0, 0))
                            .size(new Size(region.width, region.height))
                            .smooth(true)
                            .layer(MESSAGEBOX_LAYER).visible(false).build();

            pages.add(sprite);
        }
    }

    @Override
    public void close() {
        for (Sprite sprite: pages) {
            sprite.close();
        }
        pages.clear();
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
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                currentPage++;
                currentPage %= pages.size();
                break;
        }

        return true;
    }

    @Override
    public void commit() {
        super.commit();

        if (currentPage < pages.size()) {
            pages.get(currentPage).setPos(new Point(getScreenRegion().center()));
            pages.get(currentPage).commit();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (currentPage < pages.size()) {
            pages.get(currentPage).setVisible(visible);
        }
    }

    private final int MESSAGEBOX_LAYER = 10;

    private ArrayList<Sprite> pages;
    private int currentPage = 0;
}
