package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

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

        Sprite background = new Sprite.Builder("news_bar.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.0f)
                .gridSize(1, 1)
                .layer(20).visible(false).build();
        addSprite(background);

        textSprite = new TextSprite.Builder("dateText", "안녕하세요. 오늘도 평화로운 하루가 시작됩니다!", 26)
                .fontColor(Color.rgb(230, 230, 230))
                .bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(Paint.Align.LEFT)
                .fontName("NanumBarunGothic.ttf")
                .size(new SizeF(700, 36))
                .positionOffset(new PointF(40, -3))
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
    private static final int EMPTY_HOLD_COUNT = 3;
    private static final int NEWS_LIST_MAX_SIZE = 5;

    private int updateTimeLeft = NEWS_UPDATE_PERIOD;
    private int emptyHoldCount = 0;
    private TextSprite textSprite;
    private Queue<String> newsList = new LinkedList<>();
}