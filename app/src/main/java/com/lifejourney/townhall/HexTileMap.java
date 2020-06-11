package com.lifejourney.townhall;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HexTileMap {

    HexTileMap(TownData data, float scale) {
        this.townData = data;
        this.sprites = new HashMap<>();
        this.tileSize = new Size((int) (TILE_WIDTH * scale), (int) (TILE_HEIGHT * scale));
    }

    /**
     *
     */
    public void close() {
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
                textureGrid = new Point(0, 0);
                break;

            case SOIL:
                textureGrid = new Point(0, 1);
                break;

            case WATER:
                textureGrid = new Point(0, 2);
                break;

            default:
                textureGrid = new Point(0, 0);
                break;
        }

        return textureGrid;
    }


    public void update() {
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
                int xOffset = (y % 2==1)?tileSize.width/2:0;

                Sprite.Builder spriteBuilder =
                        new Sprite.Builder("tiles.png")
                                .position(new Point(
                                        x * tileSize.width + tileSize.width /2 + xOffset,
                                        y * (tileSize.height*3/4) + tileSize.height /2))
                                .size(new Size(tileSize.width, tileSize.height))
                                .gridSize(new Size(2, 3)).smooth(false)
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
    public void commit() {
        for (HashMap.Entry<CoordKey, Sprite> entry : sprites.entrySet()) {
            entry.getValue().commit();
        }
    }

    /**
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
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

    private final int MAP_LAYER = 0;
    private final int TILE_WIDTH = 32, TILE_HEIGHT = 32;

    private TownData townData;
    private HashMap<CoordKey, Sprite> sprites;
    private Size tileSize;
}
