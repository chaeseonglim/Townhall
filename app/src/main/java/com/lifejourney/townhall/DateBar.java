package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;

import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

import java.text.NumberFormat;
import java.util.Locale;

public class DateBar extends Widget {

    private final String LOG_TAG = "DateBar";

    public DateBar(Rect region, int layer, float depth) {

        super(region, layer, depth);

        Sprite background = new Sprite.Builder("date_bar.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.0f)
                .gridSize(1, 1)
                .layer(20).visible(false).build();
        addSprite(background);

        dateTextSprite = new TextSprite.Builder("dateText", "0 days", 26)
                .fontColor(Color.argb(255, 255, 255, 0))
                .bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(Paint.Align.RIGHT)
                .fontName("NanumBarunGothic.ttf")
                .size(new SizeF(130, 36))
                .positionOffset(new PointF(20, -3))
                .smooth(true).depth(0.1f)
                .layer(20).visible(false).build();
        addSprite(dateTextSprite);
    }

    /**
     *
     */
    @Override
    public void close() {

        super.close();
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
     * @param day
     */
    public void setDay(int day) {

        dateTextSprite.setText(day + " days");
    }

    private TextSprite dateTextSprite;
}
