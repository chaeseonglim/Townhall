package com.lifejourney.townhall;

import android.graphics.Color;
import android.util.Log;
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

class TownView implements View, MessageBox.Event, Button.Event {

    private String LOG_TAG = "TownView";

    TownView(World world, TownData townData, float scale) {
        this.world = world;
        this.townData = townData;
        this.sprites = new HashMap<>();
        this.tileSize = new Size((int) (TILE_WIDTH * scale), (int) (TILE_HEIGHT * scale));

        messageBox =
                new MessageBox.Builder(this, new Rect(100, 100, 500, 400),
                        "한글은?\ntest\ntest").fontSize(35.0f).layer(9)
                        .textColor(Color.rgb(0, 0, 0))
                        .build();
        messageBox.show();
        world.addWidget(messageBox);

        Button okButton =
                new Button.Builder(this, new Rect(380, 380, 150, 80),
                        "확인").fontSize(35.0f).layer(10)
                        .textColor(Color.rgb(0, 0, 0)).build();
        okButton.show();
        world.addWidget(okButton);
    }

    /**
     *
     */
    @Override
    public void close() {
        townData = null;
        for (Map.Entry<CoordKey, Sprite> entry: sprites.entrySet()) {
            entry.getValue().close();
        }
        sprites = new HashMap<>();
        tileSize = new Size();
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
     * @param messageBox
     */
    @Override
    public void onMessageBoxTouched(MessageBox messageBox) {
        messageBox.hide();
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {
        messageBox.show();
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
     * @return
     */
    private Rect getRegionToCache() {
        Rect cachedRegion = new Rect(Engine2D.GetInstance().getViewport());

        // Adding gaps to viewport for caching more sprites around
        cachedRegion.x = Math.max(0, cachedRegion.x - tileSize.width * 2);
        cachedRegion.width += tileSize.width * 4;
        cachedRegion.y = Math.max(0, cachedRegion.y - tileSize.height * 2);
        cachedRegion.height += tileSize.height * 4;

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

        TownData.TileType tileType = townData.getTileType(mapCoord);
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
        return new Size(townData.getSize()).multiply(tileSize.width, tileSize.height);
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
        Size trackDataSize = townData.getSize();

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
    private TownData townData;
    private MessageBox messageBox;
    private HashMap<CoordKey, Sprite> sprites;
    private Size tileSize;
    private boolean visible;
}
