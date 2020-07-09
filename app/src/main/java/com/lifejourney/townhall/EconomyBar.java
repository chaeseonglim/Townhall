package com.lifejourney.townhall;

import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Widget;

public class EconomyBar extends Widget {

    private final String LOG_TAG = "EconomyBar";

    public EconomyBar() {

        super(new Rect(20, 0, 500, 64), 20, 0.0f);

        Sprite bg = new Sprite.Builder("economy_bar.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).depth(0.0f)
                .gridSize(1, 1)
                .layer(20).visible(false).build();
        addSprite(bg);

        Sprite happiness = new Sprite.Builder("economy_bar_happiness.png")
                .size(new SizeF(36, 36))
                .smooth(false).depth(0.1f)
                .gridSize(5, 1)
                .layer(20).visible(false).build();
        happiness.setPositionOffset(new PointF(40, 0));
        addSprite(happiness);
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

}
