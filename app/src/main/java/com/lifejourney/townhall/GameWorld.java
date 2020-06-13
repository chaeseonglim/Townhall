package com.lifejourney.townhall;

import com.lifejourney.engine2d.World;

public class GameWorld extends World {

    static final String LOG_TAG = "GameWorld";

    GameWorld() {
        super();
        setDesiredFPS(20.0f);

        TownView view = new TownView(this, "map.png");
        view.show();

        addView(view);
    }

    @Override
    protected void close() {
    }
}
