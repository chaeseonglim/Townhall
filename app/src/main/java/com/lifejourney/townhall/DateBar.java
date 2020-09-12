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

public class DateBar extends Widget {

    private final String LOG_TAG = "DateBar";

    interface Event {
        void onDateBarPassed(int days);
    }

    public DateBar(Event eventHandler, Rect region, int layer, float depth) {

        super(region, layer, depth);

        this.eventHandler = eventHandler;

        Sprite background = new Sprite.Builder("date_bar.png")
                .size(new SizeF(getRegion().size()))
                .smooth(true).depth(0.0f)
                .gridSize(1, 1)
                .layer(20).visible(false).build();
        addSprite(background);

        dateTextSprite = new TextSprite.Builder("dateText",
                "0 " + Engine2D.GetInstance().getString(R.string.day), 27)
                .fontColor(Color.rgb(235, 235, 235))
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(61, 61, 61), 2.0f)
                .horizontalAlign(Layout.Alignment.ALIGN_OPPOSITE)
                .verticalAlign(Layout.Alignment.ALIGN_CENTER)
                .size(new SizeF(130, 30))
                .positionOffset(new PointF(20, 0))
                .smooth(true).depth(0.1f)
                .layer(20).visible(false).build();
        addSprite(dateTextSprite);
    }

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        // Update date
        if (!paused && --updateTimeLeft == 0) {
            days++;
            dateTextSprite.setText(days + " " + Engine2D.GetInstance().getString(R.string.day));
            eventHandler.onDateBarPassed(days);
            updateTimeLeft = DATE_UPDATE_PERIOD;
        }
    }

    /**
     *
     * @return
     */
    public int getDays() {
        return days;
    }

    /**
     *
     */
    public void pause() {
        paused = true;
    }

    /**
     *
     */
    public void resume() {
        paused = false;
    }

    private static final int DATE_UPDATE_PERIOD = 80;

    private Event eventHandler;
    private boolean paused = false;
    private int days = 0;
    private int updateTimeLeft = DATE_UPDATE_PERIOD;
    private TextSprite dateTextSprite;
}
