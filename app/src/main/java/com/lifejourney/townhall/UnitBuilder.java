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

public class UnitBuilder extends Widget {

    private final String LOG_TAG = "EconomyBar";

    public UnitBuilder(Villager villager) {

        super(new Rect(20, 0, 500, 64), 20, 0.0f);

        this.villager = villager;

        Sprite bg = new Sprite.Builder("economy_bar.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.0f)
                .gridSize(1, 1)
                .layer(20).visible(false).build();
        addSprite(bg);

        happinessSprite = new Sprite.Builder("economy_bar_happiness.png")
                .size(new SizeF(36, 36))
                .positionOffset(new PointF(40, 0))
                .smooth(false).depth(0.1f)
                .gridSize(5, 1)
                .layer(20).visible(false).build();
        addSprite(happinessSprite);

        goldTextSprite = new TextSprite.Builder("goldText", "0", 26)
                .fontColor(Color.argb(255, 255, 255, 0))
                .bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(Paint.Align.RIGHT)
                .fontName("NanumBarunGothic.ttf")
                .size(new SizeF(130, 36))
                .positionOffset(new PointF(-105, -3))
                .smooth(true).depth(0.1f)
                .layer(20).visible(false).build();
        addSprite(goldTextSprite);

        popTextSprite = new TextSprite.Builder("popText", "+0", 26)
                .fontColor(Color.argb(255, 255, 255, 0))
                .bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(Paint.Align.RIGHT)
                .fontName("NanumBarunGothic.ttf")
                .size(new SizeF(130, 36))
                .positionOffset(new PointF(150, -3))
                .smooth(true).depth(0.1f)
                .layer(20).visible(false).build();
        addSprite(popTextSprite);
    }

    /**
     *
     */
    @Override
    public void close() {

        super.close();
    }

    @Override
    public void update() {

        super.update();

        if (--updateTimeLeft == 0) {
            goldTextSprite.setText(NumberFormat.getNumberInstance(Locale.US).format(villager.getGold()));
            int popDiff = villager.getMaxPopulation() - villager.getPopulation();
            popTextSprite.setText(((popDiff >= 0)?"+":"-")+ popDiff);
            if (villager.getHappiness() > 80) {
                happinessSprite.setGridIndex(0, 0);
            } else if (villager.getHappiness() > 60) {
                happinessSprite.setGridIndex(1, 0);
            } else if (villager.getHappiness() > 40) {
                happinessSprite.setGridIndex(2, 0);
            } else if (villager.getHappiness() > 20) {
                happinessSprite.setGridIndex(3, 0);
            } else  {
                happinessSprite.setGridIndex(4, 0);
            }
            updateTimeLeft = UPDATE_PERIOD;
        }

    }

    /**
     *
     */
    @Override
    public void commit() {

        super.commit();
    }

    private static final int UPDATE_PERIOD = 30;

    private Villager villager;
    private Sprite happinessSprite;
    private TextSprite goldTextSprite;
    private TextSprite popTextSprite;
    private int updateTimeLeft = UPDATE_PERIOD;
}
