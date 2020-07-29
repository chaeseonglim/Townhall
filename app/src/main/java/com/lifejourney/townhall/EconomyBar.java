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

public class EconomyBar extends Widget {

    private final String LOG_TAG = "EconomyBar";

    public EconomyBar(Villager villager, Rect region, int layer, float depth) {

        super(region, layer, depth);
        this.villager = villager;

        Sprite bg = new Sprite.Builder("economy_bar.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.0f)
                .gridSize(1, 1)
                .layer(20).visible(false).build();
        addSprite(bg);

        happinessSprite = new Sprite.Builder("economy_bar_happiness.png")
                .size(new SizeF(42, 42))
                .positionOffset(new PointF(60, 0))
                .smooth(false).depth(0.1f)
                .gridSize(5, 1)
                .layer(20).visible(false).build();
        happinessSprite.setGridIndex(2, 0);
        addSprite(happinessSprite);

        goldTextSprite = new TextSprite.Builder("goldText", " ", 26)
                .fontColor(Color.argb(255, 255, 255, 0))
                .bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(Paint.Align.RIGHT)
                .fontName("NanumBarunGothic.ttf")
                .size(new SizeF(130, 36))
                .positionOffset(new PointF(-80, -3))
                .smooth(true).depth(0.1f)
                .layer(20).visible(false).build();
        addSprite(goldTextSprite);

        popTextSprite = new TextSprite.Builder("popText", " ", 26)
                .fontColor(Color.argb(255, 255, 255, 0))
                .bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(Paint.Align.RIGHT)
                .fontName("NanumBarunGothic.ttf")
                .size(new SizeF(130, 36))
                .positionOffset(new PointF(130, -3))
                .smooth(true).depth(0.1f)
                .layer(20).visible(false).build();
        addSprite(popTextSprite);
    }

    /**
     *
     */
    public void refresh() {

        // Set gold text
        if (villager.getGold() >= 0) {
            goldTextSprite.setFontColor(Color.rgb(255, 255, 0));
        } else {
            goldTextSprite.setFontColor(Color.rgb(255, 0, 0));
        }
        goldTextSprite.setText(NumberFormat.getNumberInstance(Locale.US).format(villager.getGold()));

        // Set population text
        int population = villager.getUsablePopulation();
        if (population >= 0) {
            popTextSprite.setFontColor(Color.rgb(255, 255, 0));
        } else {
            popTextSprite.setFontColor(Color.rgb(255, 0, 0));
        }
        popTextSprite.setText(((population >= 0)?"+":"")+ population);

        // Set happiness text
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
    }

    private Villager villager;
    private Sprite happinessSprite;
    private TextSprite goldTextSprite;
    private TextSprite popTextSprite;
}
