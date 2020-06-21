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
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.ResourceManager;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.View;
import com.lifejourney.engine2d.Waypoint;

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
                OffsetCoord mapCoord = new OffsetCoord(x, y);
                TileType tileType = getTileType(mapCoord);
                if (tileType == TileType.CAPITAL) {
                    capitalOffset = mapCoord;
                }

                Town town = new Town(mapCoord);
                towns.put(mapCoord, town);
            }
        }

        // Calculate viewport clipping area
        OffsetCoord bottomRightMapCoord = new OffsetCoord(mapSize.width-1, mapSize.height-1);
        Point bottomRightGameCoord = bottomRightMapCoord.toGameCoord();
        clippedViewport = new Rect(-getTileSize().width, -getTileSize().height,
                bottomRightGameCoord.x + getTileSize().width*2,
                bottomRightGameCoord.y + getTileSize().height*2);
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
            PointF delta = new PointF(touchingScreenCoord);
            delta.subtract(touchedPoint).multiply(-1.0f);
            scroll(new Point(delta));
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


    private Point getTextureGridForTile(OffsetCoord mapCoord) {

        TownMap.TileType tileType = getTileType(mapCoord);
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
    protected ArrayList<Sprite> getTileSprite(OffsetCoord mapCoord) {

        ArrayList<Sprite> sprites = new ArrayList<>();

        Sprite.Builder spriteBuilder =
            new Sprite.Builder("tiles.png")
                .position(mapCoord.toGameCoord())
                .size(getTileSize())
                .gridSize(new Size(2, 4)).smooth(false)
                .layer(MAP_LAYER).visible(true);
        Sprite sprite = spriteBuilder.build();
        sprite.setGridIndex(getTextureGridForTile(mapCoord));

        sprites.add(sprite);
        return sprites;
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    public TileType getTileType(OffsetCoord mapCoord) {

        if (mapCoord.getX() >= getMapSize().width || mapCoord.getY() >= getMapSize().height) {
            return TileType.UNKNOWN;
        }

        byte code = getMapData(mapCoord);
        for (TileType type : TileType.values()) {
            if (type.code() == code) {
                return type;
            }
        }

        return TileType.UNKNOWN;
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    public boolean isMovable(OffsetCoord mapCoord) {

        if (mapCoord.getX() < 0 || mapCoord.getY() < 0 ||
                mapCoord.getX() >= getMapSize().width || mapCoord.getY() >= getMapSize().height) {
            return false;
        }

        return getTileType(mapCoord).movable();
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    public boolean isMovable(OffsetCoord mapCoord, Squad squad) {

        if (!isMovable(mapCoord)) {
            return false;
        }

        ArrayList<Squad> squads = towns.get(mapCoord).getSquads();
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
     * @param mapCoord
     * @return
     */
    public Battle getBattle(OffsetCoord mapCoord) {

        Town town = towns.get(mapCoord);
        assert town != null;
        return town.getBattle();
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    public Town getTown(OffsetCoord mapCoord) {

        return towns.get(mapCoord);
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    protected ArrayList<Town> getNeighborTowns(OffsetCoord mapCoord) {

        ArrayList<Town> neighborTowns = new ArrayList<>();
        ArrayList<OffsetCoord> neighborCoords = mapCoord.getNeighbors();
        for (OffsetCoord coord: neighborCoords) {
            if (towns.get(coord) != null) {
                neighborTowns.add(towns.get(coord));
            }
        }
        return neighborTowns;
    }

    /**
     *
     * @return
     */
    public OffsetCoord findRetreatableMapCoord(OffsetCoord mapCoord) {

        ArrayList<Town> neighborTowns = getNeighborTowns(mapCoord);
        for (Town town: neighborTowns) {
            if (isMovable(town.getMapCoord()) && town.getSquads().size() == 0) {
                return town.getMapCoord();
            }
        }

        return null;
    }

    /**
     *
     * @param offset
     */
    public void scroll(Point offset) {
        Rect viewport = Engine2D.GetInstance().getViewport();
        viewport.offset(offset);
        if (viewport.x < clippedViewport.x) {
            viewport.x = clippedViewport.x;
        }
        if (viewport.y < clippedViewport.y) {
            viewport.y = clippedViewport.y;
        }
        if (viewport.bottomRight().x > clippedViewport.bottomRight().x) {
            viewport.x = clippedViewport.bottomRight().x - viewport.width;
        }
        if (viewport.bottomRight().y > clippedViewport.bottomRight().y) {
            viewport.y = clippedViewport.bottomRight().y - viewport.height;
        }
        Engine2D.GetInstance().setViewport(viewport);
    }

    private final static int MAP_LAYER = 0;
    private final static int HEX_SIZE = 64;

    private float scale;
    private OffsetCoord capitalOffset;
    private HashMap<OffsetCoord, Town> towns = new HashMap<>();
    private boolean dragging = false;
    private PointF touchedPoint;
    private Rect clippedViewport;
}
