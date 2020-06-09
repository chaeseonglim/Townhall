package com.lifejourney.townhall;

import android.graphics.Color;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.View;
import com.lifejourney.engine2d.World;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class TownView implements View {

    private String LOG_TAG = "TownView";

    TownView(World world, TownData data, float scale) {
        this.world = world;
        this.data = data;
        this.sprites = new HashMap<>();
        this.tileSize = new Size((int) (TILE_WIDTH * scale), (int) (TILE_HEIGHT * scale));

        MessageBox msgBox = new MessageBox(new Rect(30, 30, 300, 300), "test\ntest",
                35.0f, Color.rgb(255, 255, 255));
        msgBox.show();
        this.world.addWidget(msgBox);
    }

    /**
     *
     */
    @Override
    public void close() {
        data = null;
        for (Map.Entry<CoordKey, Sprite> entry: sprites.entrySet()) {
            entry.getValue().close();
        }
        sprites = new HashMap<>();
        tileSize = new Size();
    }

    /**
     *
     * @return
     */
    private Rect getRegionToCache() {
        Rect cachedRegion = new Rect(Engine2D.GetInstance().getViewport());

        // Adding gaps to viewport for caching more sprites around
        cachedRegion.x = Math.max(0, cachedRegion.x - tileSize.width *3);
        cachedRegion.width += tileSize.width * 6;
        cachedRegion.y = Math.max(0, cachedRegion.y - tileSize.height *3);
        cachedRegion.height += tileSize.height * 6;

        return cachedRegion;
    }

    /**
     *
     */
    private void removeUnusedSprites() {
        Rect regionToCache = getRegionToCache();

        Iterator<HashMap.Entry<CoordKey, Sprite>> iter = sprites.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry<CoordKey, Sprite> entry = iter.next();
            CoordKey key = entry.getKey();
            Rect spriteRegion = new Rect(key.getX()* tileSize.width, key.getY()* tileSize.height,
                    tileSize.width, tileSize.height);
            if (!Rect.intersects(regionToCache, spriteRegion)) {
                Sprite sprite = entry.getValue();
                sprite.close();
                iter.remove();
            }
        }
    }

    private Point getTextureGridForTile(Point mapCoord) {
        Point textureGrid;

        TownData.TileType tileType = data.getTileType(mapCoord);
        switch (tileType) {
            case GRASS:
                textureGrid = new Point(1, 0);
                break;

            case ROAD:
                textureGrid = new Point(0, 0);
                break;

            default:
                textureGrid = new Point(1, 0);
                break;
        }

        return textureGrid;
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return false;
    }

    /**
     *
     */
    @Override
    public void update() {
        if (!visible)
            return;

        updateTileSprites();
        updateViewport();
    }

    /**
     *
     */
    @Override
    public void commit() {
        if (!visible)
            return;

        commitTileSprites();
    }

    /**
     *
     */
    @Override
    public void show() {
        setVisible(true);
    }

    /**
     *
     */
    @Override
    public void hide() {
        setVisible(false);
    }

    /**
     *
     * @param visible
     */
    private void setVisible(boolean visible) {
        this.visible = visible;
        for (HashMap.Entry<CoordKey, Sprite> entry : sprites.entrySet()) {
            entry.getValue().setVisible(visible);
        }
    }

    /**
     *
     * @return
     */
    public Size getSize() {
        return new Size(data.getSize()).multiply(tileSize.width, tileSize.height);
    }

    /**
     *
     * @param pt
     * @return
     */
    RectF getScreenRegionfromTownCoord(Point pt) {
        return new RectF(pt.x*tileSize.width, pt.y*tileSize.height,
                tileSize.width, tileSize.height);
    }

    /**
     *
     * @param pt
     * @return
     */
    Point getTownCoordFromScreenCoord(PointF pt) {
        return new Point(pt).divide(new Point(tileSize.width, tileSize.height));
    }

    /**
     *
     * @return
     */
    Size getTileSize() {
        return tileSize;
    }

    /**
     *
     */
    private void updateViewport() {
        Rect viewport = Engine2D.GetInstance().getViewport();
        Engine2D.GetInstance().setViewport(viewport);
    }

    private void updateTileSprites() {
        // clean up unused spries
        removeUnusedSprites();

        // build up sprites
        Rect cachedRegion = getRegionToCache();
        Size trackDataSize = data.getSize();

        for (int y = cachedRegion.top() / tileSize.height;
             y < Math.min(cachedRegion.bottom() / tileSize.height, trackDataSize.height);
             ++y) {
            for (int x = cachedRegion.left() / tileSize.width;
                 x < Math.min(cachedRegion.right() / tileSize.width, trackDataSize.width);
                 ++x) {
                if (sprites.get(new CoordKey(x, y)) != null)
                    continue;

                Point textureGrid = getTextureGridForTile(new Point(x, y));

                Sprite.Builder spriteBuilder =
                        new Sprite.Builder("tiles.png")
                                .position(new Point(
                                        x * tileSize.width + tileSize.width /2,
                                        y * tileSize.height + tileSize.height /2))
                                .size(new Size(tileSize.width, tileSize.height))
                                .gridSize(new Size(4, 14)).smooth(false)
                                .layer(MAP_LAYER).visible(true);
                Sprite sprite = spriteBuilder.build();
                sprite.setGridIndex(textureGrid);
                sprites.put(new CoordKey(x, y), sprite);
            }
        }
    }

    /**
     *
     */
    private void commitTileSprites() {
        for (HashMap.Entry<CoordKey, Sprite> entry : sprites.entrySet()) {
            entry.getValue().commit();
        }
    }

    private final int MAP_LAYER = 0;
    private final int TILE_WIDTH = 32, TILE_HEIGHT = 32;

    private World world;
    private TownData data;
    private HashMap<CoordKey, Sprite> sprites;
    private Size tileSize;
    private boolean visible;
}
