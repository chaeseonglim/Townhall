package com.lifejourney.townhall;

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

import java.util.ArrayList;
import java.util.HashMap;

class TownMap extends HexTileMap implements View {

    private static final String LOG_TAG = "TownMap";

    public interface Event {

        void onMapCreated();

        void onMapDestroyed();

        void onMapFocused(Town town);
    }

    enum TileType {
        GRASS((byte)0xe0, true),
        BADLAND((byte)0xd0, true),
        WATER((byte)0x00, false),
        TOWNHALL((byte)0xb0, true),
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
    TownMap(Event listener, String mapBitmap, float scale) {

        super((int) (HEX_SIZE * scale));

        this.listener = listener;
        this.scale = scale;
        setCacheMargin(4);

        // Load map data from bitmap (grayscale png)
        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        InfoBitmap bitmap = resourceManager.loadGrayscaleBitmap(mapBitmap);
        setMapData(bitmap.get2DByteArray());
        setMapSize(new Size(bitmap.getWidth(), bitmap.getHeight()));

        // Add town information
        Town.SetTileSize(getTileSize());
        Size mapSize = getMapSize();
        for (int y = 0; y < mapSize.height; ++y) {
            for (int x = 0; x < mapSize.width; ++x) {
                OffsetCoord mapCoord = new OffsetCoord(x, y);
                TileType tileType = getTileType(mapCoord);
                if (tileType == TileType.TOWNHALL) {
                    capitalOffset = mapCoord;
                }

                Town town = new Town(mapCoord, tileType);
                towns.put(mapCoord, town);
            }
        }

        // Calculate viewport clipping area
        OffsetCoord bottomRightMapCoord = new OffsetCoord(mapSize.width-1, mapSize.height-1);
        PointF bottomRightGameCoord = bottomRightMapCoord.toGameCoord();
        clippedViewport = new RectF(-getTileSize().width, -getTileSize().height,
                bottomRightGameCoord.x + getTileSize().width*2,
                bottomRightGameCoord.y + getTileSize().height*2);

        listener.onMapCreated();
    }

    /**
     *
     */
    @Override
    public void close() {

        super.close();

        listener.onMapDestroyed();
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
        PointF touchedScreenCoord = new PointF(event.getX(), event.getY());
        PointF touchedGameCoord =
                Engine2D.GetInstance().translateScreenToGameCoord(touchedScreenCoord);

        if (eventAction == MotionEvent.ACTION_DOWN) {
            setDragging(true);
            lastTouchedScreenCoord = lastDraggingScreenCoord = touchedScreenCoord;
            return true;
        } else if (isDragging()) {
            if (eventAction == MotionEvent.ACTION_MOVE) {
                PointF delta = new PointF(touchedScreenCoord);
                delta.subtract(lastDraggingScreenCoord).multiply(-1.0f);
                scroll(new Point(delta));
                lastDraggingScreenCoord = touchedScreenCoord;
            } else if (eventAction == MotionEvent.ACTION_UP) {
                setDragging(false);

                PointF lastTouchedWidgetCoord =
                        Engine2D.GetInstance().translateScreenToWidgetCoord(lastTouchedScreenCoord);
                PointF touchedWidgetCoord =
                        Engine2D.GetInstance().translateScreenToWidgetCoord(touchedScreenCoord);
                if (lastTouchedWidgetCoord.distance(touchedWidgetCoord) < 60.0f) {
                    OffsetCoord touchedMapCoord = new OffsetCoord(touchedGameCoord);
                    listener.onMapFocused(getTown(touchedMapCoord));
                }
            } else if (eventAction == MotionEvent.ACTION_CANCEL) {
                setDragging(false);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected ArrayList<Sprite> getTileSprite(OffsetCoord mapCoord) {

        ArrayList<Sprite> sprites = getTown(mapCoord).getTileSprite();

        if (glowingTiles != null && glowingTiles.contains(mapCoord)) {
            Sprite glowingSprite =
                    new Sprite.Builder("GlowingLine", "tiles.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(getTileSize()).gridSize(2, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.1f).visible(true).build();
            glowingSprite.setGridIndex(0, 4);
            sprites.add(glowingSprite);
        }

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
    public OffsetCoord getTownhallMapCoord() {

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
            viewport.x = (int) clippedViewport.x;
        }
        if (viewport.y < clippedViewport.y) {
            viewport.y = (int) clippedViewport.y;
        }
        if (viewport.bottomRight().x > clippedViewport.bottomRight().x) {
            viewport.x = (int) (clippedViewport.bottomRight().x - viewport.width);
        }
        if (viewport.bottomRight().y > clippedViewport.bottomRight().y) {
            viewport.y = (int) (clippedViewport.bottomRight().y - viewport.height);
        }
        Engine2D.GetInstance().setViewport(viewport);
    }

    /**
     *
     * @param glowingTiles
     */
    public void setGlowingTiles(ArrayList<OffsetCoord> glowingTiles) {

        if (this.glowingTiles != null) {
            for (OffsetCoord tileOffset: this.glowingTiles) {
                if (glowingTiles == null || !glowingTiles.contains(tileOffset)) {
                    flushTileSprite(tileOffset);
                }
            }
        }
        if (glowingTiles != null) {
            for (OffsetCoord tileOffset: glowingTiles) {
                if (this.glowingTiles == null || !this.glowingTiles.contains(tileOffset)) {
                    flushTileSprite(tileOffset);
                }
            }
        }
        this.glowingTiles = glowingTiles;
    }

    /**
     *
     * @return
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     *
     * @param dragging
     */
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    private final static int SPRITE_LAYER = 0;
    private final static int HEX_SIZE = 64;

    private Event listener;
    private float scale;
    private boolean dragging = false;
    private OffsetCoord capitalOffset;
    private HashMap<OffsetCoord, Town> towns = new HashMap<>();
    private ArrayList<OffsetCoord> glowingTiles = null;
    private PointF lastTouchedScreenCoord;
    private PointF lastDraggingScreenCoord;
    private RectF clippedViewport;
}
