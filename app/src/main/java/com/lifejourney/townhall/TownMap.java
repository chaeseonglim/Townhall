package com.lifejourney.townhall;

import android.util.Log;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.HexTileMap;
import com.lifejourney.engine2d.InfoBitmap;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.ResourceManager;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

class TownMap extends HexTileMap implements View {

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

        this.scale = scale;
        setCacheMargin(4);

        // Load map data from bitmap (grayscale png)
        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        InfoBitmap bitmap = resourceManager.loadGrayscaleBitmap(mapBitmap);
        setMapData(bitmap.get2DByteArray());
        setMapSize(new Size(bitmap.getWidth(), bitmap.getHeight()));

        // Add town information
        Size mapSize = getMapSize();
        for (int y = 0; y < mapSize.height; ++y) {
            for (int x = 0; x < mapSize.width; ++x) {
                OffsetCoord offsetCoord = new OffsetCoord(x, y);
                TileType tileType = getTileType(offsetCoord);
                if (tileType == TileType.CAPITAL) {
                    capitalOffset = offsetCoord;
                }

                Town town = new Town();
                towns.put(offsetCoord, town);
            }
        }
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Dragging map
        int eventAction = event.getAction();
        PointF touchingScreenCoord = new PointF(event.getX(), event.getY());

        if (eventAction == MotionEvent.ACTION_DOWN) {
            dragging = true;
            touchedPoint = touchingScreenCoord;
            return true;
        }
        else if (eventAction == MotionEvent.ACTION_MOVE && dragging) {
            Rect viewport = Engine2D.GetInstance().getViewport();
            PointF delta = new PointF(touchingScreenCoord);
            delta.subtract(touchedPoint).multiply(-1.0f);
            viewport.offset(new Point(delta));
            Engine2D.GetInstance().setViewport(viewport);

            touchedPoint = touchingScreenCoord;
            return true;
        }
        else if ((eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_CANCEL)
                && dragging) {
            dragging = false;
            return true;
        }
        else {
            return false;
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
    protected ArrayList<Sprite> getTileSprite(OffsetCoord offsetCoord) {

        ArrayList<Sprite> sprites = new ArrayList<>();

        Sprite.Builder spriteBuilder =
            new Sprite.Builder("tiles.png")
                .position(offsetCoord.toGameCoord())
                .size(getTileSize())
                .gridSize(new Size(2, 4)).smooth(false)
                .layer(MAP_LAYER).visible(true);
        Sprite sprite = spriteBuilder.build();
        sprite.setGridIndex(getTextureGridForTile(offsetCoord));

        sprites.add(sprite);
        return sprites;
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
    public boolean isMovable(OffsetCoord offsetCoord, Squad squad) {

        if (!isMovable(offsetCoord)) {
            return false;
        }

        ArrayList<Squad> squads = towns.get(offsetCoord).getSquads();
        for (Squad localSquad: squads) {
            if (squad != localSquad && squad.getSide() == localSquad.getSide()) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getCapitalOffset() {
        return capitalOffset;
    }

    /**
     *
     * @param offsetCoord
     * @param squad
     */
    public void addSquad(OffsetCoord offsetCoord, Squad squad) {

        Town town = towns.get(offsetCoord);
        assert town != null;
        town.addSquad(squad);
    }

    /**
     *
     * @param offsetCoord
     * @param squad
     */
    public void removeSquad(OffsetCoord offsetCoord, Squad squad) {

        Town town = towns.get(offsetCoord);
        assert town != null;
        town.removeSquad(squad);
    }

    /**
     *
     * @param offsetCoord
     * @return
     */
    public ArrayList<Squad> getSquads(OffsetCoord offsetCoord) {

        return Objects.requireNonNull(towns.get(offsetCoord)).getSquads();
    }


    private final static int MAP_LAYER = 0;
    private final static int HEX_SIZE = 64;

    private float scale;
    private OffsetCoord capitalOffset;
    private HashMap<OffsetCoord, Town> towns = new HashMap<>();
    private boolean dragging = false;
    private PointF touchedPoint;
}
