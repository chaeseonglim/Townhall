package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

import java.util.LinkedList;
import java.util.Queue;

public class NewsBar extends Widget {

    private final String LOG_TAG = "NewsBar";

    public NewsBar(Rect region, int layer, float depth) {

        super(region, layer, depth);

        backgroundSprite = new Sprite.Builder("news_bar.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.0f)
                .gridSize(2, 1)
                .layer(20).visible(false).build();
        addSprite(backgroundSprite);

        textSprite = new TextSprite.Builder("dateText",
                Engine2D.GetInstance().getString(R.string.say_hi), 24)
                .fontColor(Color.rgb(230, 230, 230))
                .bgColor(Color.argb(0, 0, 0, 0))
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(61, 61, 61), 2.0f)
                .horizontalAlign(Layout.Alignment.ALIGN_NORMAL)
                .verticalAlign(Layout.Alignment.ALIGN_CENTER)
                .size(new SizeF(700, 36))
                .positionOffset(new PointF(40, 0))
                .smooth(true).depth(0.1f)
                .layer(20).visible(false).build();
        addSprite(textSprite);
    }

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        // Update date
        if (--updateTimeLeft == 0) {
            if (newsList.isEmpty()) {
                emptyHoldCount++;
                if (emptyHoldCount == EMPTY_HOLD_COUNT) {
                    hide();
                }
            } else {
                String news = newsList.poll();
                textSprite.setText(news);
                emptyHoldCount = 0;
                show();

                Engine2D.GetInstance().playSoundEffect("news", 1.0f);

                backgroundSprite.setAnimationWrap(false);
                backgroundSprite.clearAnimation();
                backgroundSprite.addAnimationFrame(1, 0, 5);
                backgroundSprite.addAnimationFrame(0, 0, 5);
                backgroundSprite.addAnimationFrame(1, 0, 5);
                backgroundSprite.addAnimationFrame(0, 0, 5);
            }
            updateTimeLeft = NEWS_UPDATE_PERIOD;
        }
    }

    /**
     *
     * @param news
     */
    public void addNews(String news) {

        while (newsList.size() > NEWS_LIST_MAX_SIZE - 1) {
            newsList.poll();
        }
        newsList.offer(news);

        if (newsList.size() == 1) {
            updateTimeLeft = 1;
        }
    }

    private static final int NEWS_UPDATE_PERIOD = 60;
    private static final int EMPTY_HOLD_COUNT = 2;
    private static final int NEWS_LIST_MAX_SIZE = 5;

    private int updateTimeLeft = NEWS_UPDATE_PERIOD;
    private int emptyHoldCount = 0;
    private Sprite backgroundSprite;
    private TextSprite textSprite;
    private Queue<String> newsList = new LinkedList<>();
}
