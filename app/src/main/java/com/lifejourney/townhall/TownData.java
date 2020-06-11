package com.lifejourney.townhall;

import android.util.Log;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.InfoBitmap;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.ResourceManager;
import com.lifejourney.engine2d.Size;

import java.util.ArrayList;

class TownData {

    private static final String LOG_TAG = "TownData";

    enum TileType {
        GRASS((byte)0xe0, true),
        SOIL((byte)0xd0, true),
        WATER((byte)0x00, false),
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
    TownData(String mapBitmap) {
        // Load map data from bitmap (grayscale png)
        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        InfoBitmap bitmap = resourceManager.loadGrayscaleBitmap(mapBitmap);
        grid = bitmap.get2DByteArray();
        size = new Size(bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     *
     * @param pt
     * @return
     */
    TileType getTileType(Point pt) {
        Rect townRegion = new Rect(new Point(), getSize());
        if (!townRegion.includes(pt)) {
            return TileType.UNKNOWN;
        }

        byte code = grid[pt.y][pt.x];
        for (TileType type : TileType.values()) {
            if (type.code() == code) {
                return type;
            }
        }

        Log.e(LOG_TAG, "Unknown tile type!!! " + code);
        return TileType.UNKNOWN;
    }

    /**
     *
     * @param pt
     * @return
     */
    boolean isMovable(Point pt) {
        Rect townRegion = new Rect(new Point(), getSize());
        if (!townRegion.includes(pt)) {
            return false;
        }

        return getTileType(pt).movable();
    }

    /**
     *
     * @param pt
     * @return
     */
    boolean isSearchable(Point pt) {
        Rect townRegion = new Rect(new Point(), getSize());
        if (!townRegion.includes(pt)) {
            return false;
        }

        TileType type = getTileType(pt);
        return type.movable();
    }

    /**
     *
     * @return
     */
    Size getSize() {
        return size;
    }

    private byte[][] grid;
    private Size size;
}
