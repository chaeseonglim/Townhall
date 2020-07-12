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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

class GameMap extends HexTileMap implements View, Town.Event {

    private static final String LOG_TAG = "GameMap";

    public interface Event {

        void onMapCreated();

        void onMapDestroyed();

        void onMapFocused(Town town);
    }

    /**
     *
     * @param mapBitmap
     */
    GameMap(Event listener, String mapBitmap) {

        super(HEX_SIZE);

        this.listener = listener;
        setCacheMargin(4);

        // Load map data from bitmap file
        ResourceManager resourceManager = Engine2D.GetInstance().getResourceManager();
        InfoBitmap bitmap = resourceManager.loadGrayscaleBitmap(mapBitmap);
        setMapData(bitmap.getInfoArray());
        setMapSize(new Size(bitmap.getWidth(), bitmap.getHeight()));

        // Init town list
        for (int i = 0; i < Town.Side.values().length; ++i) {
            townsBySide.add(new ArrayList<Town>());
        }

        // Add town information
        Town.SetTileSize(getTileSize());
        Size mapSize = getMapSize();
        for (int y = 0; y < mapSize.height; ++y) {
            for (int x = 0; x < mapSize.width; ++x) {
                OffsetCoord mapCoord = new OffsetCoord(x, y);

                // base type
                int ordinal = (getMapData(mapCoord) & 0x00F00000) >> 20;
                Town.Type type = Town.Type.values()[ordinal];

                // side
                ordinal = (getMapData(mapCoord) & 0x000F0000) >> 16;
                Town.Side side = Town.Side.values()[ordinal];

                Town town = new Town(this, mapCoord, type, side);
                towns.put(mapCoord, town);
                townsBySide.get(side.ordinal()).add(town);
            }
        }
        for (Town town: towns.values()) {
            town.setNeighborTowns(getNeighborTowns(town.getMapCoord(), true));
        }

        // Calculate viewport clipping area
        OffsetCoord bottomRightMapCoord = new OffsetCoord(mapSize.width-1, mapSize.height-1);
        PointF bottomRightGameCoord = bottomRightMapCoord.toGameCoord();
        clippedViewport = new RectF(-getTileSize().width - leftMargin,
                -getTileSize().height - topMargin,
                bottomRightGameCoord.x + getTileSize().width * 2 + leftMargin + rightMargin,
                bottomRightGameCoord.y + getTileSize().height * 2 + topMargin + bottomMargin);

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
     * @param town
     */
    @Override
    public void onTownUpdated(Town town) {

        redrawTileSprite(town.getMapCoord());
    }

    /**
     *
     * @param town
     * @param prevSide
     * @param newSide
     */
    @Override
    public void onTownOccupied(Town town, Town.Side prevSide, Town.Side newSide) {

        townsBySide.get(prevSide.ordinal()).remove(town);
        townsBySide.get(newSide.ordinal()).add(town);

        if (prevSide != Town.Side.NEUTRAL) {
            redrawTileSprite(prevSide);
        }
        redrawTileSprite(newSide);
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
                    if (listener != null) {
                        Town townToFocus = getTown(touchedMapCoord);
                        townToFocus.setFocus(true);
                        listener.onMapFocused(townToFocus);
                    }
                }
            } else if (eventAction == MotionEvent.ACTION_CANCEL) {
                setDragging(false);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    @Override
    protected ArrayList<Sprite> getTileSprite(OffsetCoord mapCoord) {

        boolean glowing = (glowingTiles != null && glowingTiles.contains(mapCoord));
        return getTown(mapCoord).getTileSprites(glowing, showTerritories);
    }

    /**
     *
     * @param mapCoord
     */
    @Override
    public void removeTileSprite(OffsetCoord mapCoord) {
        getTown(mapCoord).removeTileSprites();
    }

    /**
     *
     * @param side
     */
    public void redrawTileSprite(Town.Side side) {

        for (Town town: townsBySide.get(side.ordinal())) {
            redrawTileSprite(town.getMapCoord());
        }
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    public boolean isMovable(OffsetCoord mapCoord, Squad squad) {

        Town town = towns.get(mapCoord);
        if (town == null) {
            return false;
        }

        if (!town.getType().isMovable(squad)) {
            return false;
        }

        ArrayList<Squad> squads = Objects.requireNonNull(towns.get(mapCoord)).getSquads();
        for (Squad localSquad : squads) {
            if (squad != localSquad && squad.getSide() == localSquad.getSide()) {
                return false;
            }
        }

        return true;
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
     * @return
     */
    public ArrayList<Town> getTowns() {

        return new ArrayList<>(this.towns.values());
    }

    /**
     *
     * @param mapCoord
     * @return
     */
    protected ArrayList<Town> getNeighborTowns(OffsetCoord mapCoord, boolean addNull) {

        ArrayList<Town> neighborTowns = new ArrayList<>();
        ArrayList<OffsetCoord> neighborCoords = mapCoord.getNeighbors();
        for (OffsetCoord coord: neighborCoords) {
            Town neighborTown = towns.get(coord);
            if (addNull || neighborTown != null) {
                neighborTowns.add(neighborTown);
            }
        }
        return neighborTowns;
    }

    /**
     *
     * @return
     */
    public ArrayList<OffsetCoord> findRetreatableMapCoords(Squad squad) {

        OffsetCoord mapCoord = squad.getMapCoord();
        ArrayList<OffsetCoord> retreatableMapCoords = new ArrayList<>();

        ArrayList<Town> neighborTowns = getNeighborTowns(mapCoord, false);
        for (Town town: neighborTowns) {
            if (isMovable(town.getMapCoord(), squad) && town.getSquads().size() == 0) {
                retreatableMapCoords.add(town.getMapCoord());
            }
        }

        return retreatableMapCoords;
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
            for (OffsetCoord mapCoord: this.glowingTiles) {
                if (glowingTiles == null || !glowingTiles.contains(mapCoord)) {
                    redrawTileSprite(mapCoord);
                }
            }
        }
        if (glowingTiles != null) {
            for (OffsetCoord mapCoord: glowingTiles) {
                if (this.glowingTiles == null || !this.glowingTiles.contains(mapCoord)) {
                    redrawTileSprite(mapCoord);
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

    /**
     *
     * @param side
     * @return
     */
    public ArrayList<Town> getTownsBySide(Town.Side side) {

        return townsBySide.get(side.ordinal());
    }

    private final static int HEX_SIZE = 64;

    private Event listener;
    private int leftMargin = 0;
    private int rightMargin = 0;
    private int topMargin = 32;
    private int bottomMargin = 32;
    private boolean dragging = false;
    private HashMap<OffsetCoord, Town> towns = new HashMap<>();
    private ArrayList<ArrayList<Town>> townsBySide = new ArrayList<>(Town.Side.values().length);
    private ArrayList<OffsetCoord> glowingTiles = null;
    private PointF lastTouchedScreenCoord;
    private PointF lastDraggingScreenCoord;
    private RectF clippedViewport;
    private boolean showTerritories = true;
}
