package com.lifejourney.townhall;

import android.util.Log;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.HexTileMap;
import com.lifejourney.engine2d.InfoBitmap;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.ResourceManager;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;

class TownMap extends HexTileMap {

    private static final String LOG_TAG = "TownMap";

    enum TileType {
        GRASS((byte)0xe0, true),
        SOIL((byte)0xd0, true),
        WATER((byte)0x00, false),
        CAPITAL((byte)0xb0, true),
        UNKNOWN((byte)0xff, false);

        TileType(byte code, boolean movable) {
            this.code = code;
            this.movable = movable;
        }

        byte code() {
            return code;
        }
        boolean movable() {
            return movable;
        }

        private final byte code;
        private final boolean movable;
    }

    /**
     *
     * @param mapBitmap
     */
    TownMap(String mapBitmap, float scale) {

        super((int) (HEX_SIZE * scale));

        setCacheMargin(4);
        this.scale = scale;

        // Load map data from bitmap (grayscale png)
        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        InfoBitmap bitmap = resourceManager.loadGrayscaleBitmap(mapBitmap);
        setMapData(bitmap.get2DByteArray());
        setMapSize(new Size(bitmap.getWidth(), bitmap.getHeight()));

        // Check where capital city is
        Size mapSize = getMapSize();
        for (int y = 0; y < mapSize.height; ++y) {
            for (int x = 0; x < mapSize.width; ++x) {
                OffsetCoord offsetCoord = new OffsetCoord(x, y);
                TileType tileType = getTileType(offsetCoord);
                if (tileType == TileType.CAPITAL) {
                    capitalOffset = offsetCoord;
                }
            }
        }
    }

    private Point getTextureGridForTile(OffsetCoord offsetCoord) {

        TownMap.TileType tileType = getTileType(offsetCoord);
        switch (tileType) {
            case GRASS:
                return new Point(0, 0);
            case SOIL:
                return new Point(0, 1);
            case WATER:
                return new Point(0, 2);
            case CAPITAL:
                return new Point(0, 3);
            default:
                return new Point(0, 0);
        }
    }

    @Override
    protected Sprite getTileSprite(OffsetCoord offsetCoord) {

        Sprite.Builder spriteBuilder =
            new Sprite.Builder("tiles.png")
                .position(offsetCoord.toScreenCoord())
                .size(getTileSize())
                .gridSize(new Size(2, 4)).smooth(false)
                .layer(MAP_LAYER).visible(true);
        Sprite sprite = spriteBuilder.build();
        sprite.setGridIndex(getTextureGridForTile(offsetCoord));
        return sprite;
    }

    /**
     *
     * @param offsetCoord
     * @return
     */
    public TileType getTileType(OffsetCoord offsetCoord) {

        if (offsetCoord.getX() >= getMapSize().width || offsetCoord.getY() >= getMapSize().height) {
            return TileType.UNKNOWN;
        }

        byte code = getMapData(offsetCoord);
        for (TileType type : TileType.values()) {
            if (type.code() == code) {
                return type;
            }
        }

        return TileType.UNKNOWN;
    }

    /**
     *
     * @param offsetCoord
     * @return
     */
    public boolean isMovable(OffsetCoord offsetCoord) {

        if (offsetCoord.getX() >= getMapSize().width || offsetCoord.getY() >= getMapSize().height) {
            return false;
        }

        return getTileType(offsetCoord).movable();
    }

    /**
     *
     * @param offsetCoord
     * @return
     */
    public boolean isSearchable(OffsetCoord offsetCoord) {

        if (offsetCoord.getX() >= getMapSize().width || offsetCoord.getY() >= getMapSize().height) {
            return false;
        }

        return getTileType(offsetCoord).movable();
    }

    /**
     *
     * @return
     */
    public OffsetCoord getCapitalOffset() {
        return capitalOffset;
    }

    private final static int MAP_LAYER = 0;
    private final static int HEX_SIZE = 16;

    private float scale;
    private OffsetCoord capitalOffset;
}
