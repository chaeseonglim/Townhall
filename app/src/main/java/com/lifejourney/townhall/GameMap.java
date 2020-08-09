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
import java.util.Objects;

class GameMap extends HexTileMap implements View, Territory.Event {

    private static final String LOG_TAG = "GameMap";

    public interface Event {

        void onMapTownFocused(Territory territory);

        void onMapTownOccupied(Territory territory, Tribe.Faction prevFaction);
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
        for (int i = 0; i < Tribe.Faction.values().length; ++i) {
            townsBySide.add(new ArrayList<Territory>());
        }

        // Add town information
        Territory villagerHq = null;
        Territory.SetTileSize(getTileSize());
        Size mapSize = getMapSize();
        for (int y = 0; y < mapSize.height; ++y) {
            for (int x = 0; x < mapSize.width; ++x) {
                OffsetCoord mapCoord = new OffsetCoord(x, y);

                // base terrain
                int ordinal = (getMapData(mapCoord) & 0x00F00000) >> 20;
                Territory.Terrain terrain = Territory.Terrain.values()[ordinal];

                // faction
                ordinal = (getMapData(mapCoord) & 0x000F0000) >> 16;
                Tribe.Faction faction = Tribe.Faction.values()[ordinal];

                Territory territory = new Territory(this, mapCoord, terrain, faction);
                territories.put(mapCoord, territory);
                townsBySide.get(faction.ordinal()).add(territory);

                if (terrain == Territory.Terrain.HEADQUARTER && faction == Tribe.Faction.VILLAGER) {
                    villagerHq = territory;
                }
            }
        }
        for (Territory territory : territories.values()) {
            territory.setNeighbors(getNeighborTerritories(territory.getMapPosition(), 1, true));
        }

        // Calculate viewport clipping area
        OffsetCoord bottomRightMapCoord = new OffsetCoord(mapSize.width-1, mapSize.height-1);
        PointF bottomRightGameCoord = bottomRightMapCoord.toGameCoord();
        int leftMargin = 0, rightMargin = 0, topMargin = 32, bottomMargin = 32;
        clippedViewport = new RectF(-getTileSize().width - leftMargin,
                -getTileSize().height - topMargin,
                bottomRightGameCoord.x + getTileSize().width * 2 + leftMargin + rightMargin,
                bottomRightGameCoord.y + getTileSize().height * 2 + topMargin + bottomMargin);

        // Scroll to headquarter
        if (villagerHq != null) {
            Point offset = new Point(villagerHq.getMapPosition().toGameCoord())
                    .subtract(Engine2D.GetInstance().getViewport().center());
            scroll(offset);
        }
    }

    /**
     *
     */
    @Override
    public void close() {

        for (Territory territory : territories.values()) {
            territory.removeTileSprites();
        }
        super.close();
    }

    /**
     *
     * @param territory
     */
    @Override
    public void onTownUpdated(Territory territory) {

        redraw(territory.getMapPosition());
    }

    /**
     *
     * @param territory
     * @param prevFaction
     */
    @Override
    public void onTownOccupied(Territory territory, Tribe.Faction prevFaction) {

        townsBySide.get(prevFaction.ordinal()).remove(territory);
        townsBySide.get(territory.getFaction().ordinal()).add(territory);

        if (prevFaction != Tribe.Faction.NEUTRAL) {
            redrawTileSprites(prevFaction);
        }
        redrawTileSprites(territory.getFaction());

        listener.onMapTownOccupied(territory, prevFaction);
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
                Engine2D.GetInstance().translateScreenToGamePosition(touchedScreenCoord);

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
                        Engine2D.GetInstance().translateScreenToWidgetPosition(lastTouchedScreenCoord);
                PointF touchedWidgetCoord =
                        Engine2D.GetInstance().translateScreenToWidgetPosition(touchedScreenCoord);
                if (lastTouchedWidgetCoord.distance(touchedWidgetCoord) < 60.0f) {
                    OffsetCoord touchedMapCoord = new OffsetCoord(touchedGameCoord);
                    Territory territoryToFocus = getTerritory(touchedMapCoord);
                    if (territoryToFocus != null) {
                        territoryToFocus.setFocus(true);
                        listener.onMapTownFocused(territoryToFocus);
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
     * @param mapPosition
     * @return
     */
    @Override
    protected ArrayList<Sprite> getTileSprites(OffsetCoord mapPosition) {

        boolean glowing = (glowingTilePositions != null && glowingTilePositions.contains(mapPosition));
        return getTerritory(mapPosition).getTileSprites(glowing, showTerritories);
    }

    /**
     *
     * @param mapPosition
     */
    @Override
    public void removeTileSprites(OffsetCoord mapPosition) {

        getTerritory(mapPosition).removeTileSprites();
    }

    /**
     *
     * @param faction
     */
    public void redrawTileSprites(Tribe.Faction faction) {

        for (Territory territory : townsBySide.get(faction.ordinal())) {
            redraw(territory.getMapPosition());
        }
    }

    /**
     *
     * @param mapPosition
     * @param faction
     * @return
     */
    public boolean isMovable(OffsetCoord mapPosition, Tribe.Faction faction) {

        Territory territory = territories.get(mapPosition);
        if (territory == null) {
            return false;
        }

        return territory.getTerrain().isMovable(faction);
    }

    /**
     *
     * @param mapPosition
     * @param squad
     * @return
     */
    public boolean isMovable(OffsetCoord mapPosition, Squad squad) {

        if (!isMovable(mapPosition, squad.getFaction())) {
            return false;
        }

        ArrayList<Squad> squads = Objects.requireNonNull(territories.get(mapPosition)).getSquads();
        for (Squad localSquad : squads) {
            if (squad != localSquad && squad.getFaction() == localSquad.getFaction() ||
                    (squad.getFaction() != Tribe.Faction.VILLAGER &&
                            localSquad.getFaction() != Tribe.Faction.VILLAGER)) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param mapPosition
     * @return
     */
    public Battle getBattle(OffsetCoord mapPosition) {

        Territory territory = territories.get(mapPosition);
        assert territory != null;
        return territory.getBattle();
    }

    /**
     *
     * @param mapPosition
     * @return
     */
    public Territory getTerritory(OffsetCoord mapPosition) {

        return territories.get(mapPosition);
    }

    /**
     *
     * @return
     */
    public ArrayList<Territory> getTerritories() {

        return new ArrayList<>(this.territories.values());
    }

    /**
     *
     * @param mapPosition
     * @return
     */
    protected ArrayList<Territory> getNeighborTerritories(OffsetCoord mapPosition, int radius, boolean addNull) {

        ArrayList<Territory> neighborTerritories = new ArrayList<>();
        ArrayList<OffsetCoord> neighborPositions =
                (radius == 1)?mapPosition.getNeighborsByCcw():mapPosition.getNeighbors(radius);
        for (OffsetCoord neighborPosition: neighborPositions) {
            Territory neighborTerritory = territories.get(neighborPosition);
            if (addNull || neighborTerritory != null) {
                neighborTerritories.add(neighborTerritory);
            }
        }
        return neighborTerritories;
    }

    /**
     *
     * @return
     */
    public ArrayList<OffsetCoord> findMapPositionToRetreat(Squad squad) {

        OffsetCoord currentSquadPosition = squad.getMapPosition();
        ArrayList<OffsetCoord> mapPositionsToRetreat = new ArrayList<>();

        ArrayList<Territory> neighborTerritories =
                getNeighborTerritories(currentSquadPosition, 1,false);
        for (Territory territory : neighborTerritories) {
            if (isMovable(territory.getMapPosition(), squad) && territory.getSquads().size() == 0) {
                mapPositionsToRetreat.add(territory.getMapPosition());
            }
        }

        return mapPositionsToRetreat;
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
     * @param glowingTilePositions
     */
    public void setGlowingTilePositions(ArrayList<OffsetCoord> glowingTilePositions) {

        if (this.glowingTilePositions != null) {
            for (OffsetCoord mapPosition: this.glowingTilePositions) {
                if (glowingTilePositions == null || !glowingTilePositions.contains(mapPosition)) {
                    redraw(mapPosition);
                }
            }
        }
        if (glowingTilePositions != null) {
            for (OffsetCoord mapCoord: glowingTilePositions) {
                if (this.glowingTilePositions == null || !this.glowingTilePositions.contains(mapCoord)) {
                    redraw(mapCoord);
                }
            }
        }
        this.glowingTilePositions = glowingTilePositions;
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
     * @param faction
     * @return
     */
    public ArrayList<Territory> getTownsBySide(Tribe.Faction faction) {

        return townsBySide.get(faction.ordinal());
    }

    private final static int HEX_SIZE = 64;

    private Event listener;
    private boolean dragging = false;
    private HashMap<OffsetCoord, Territory> territories = new HashMap<>();
    private ArrayList<ArrayList<Territory>> townsBySide = new ArrayList<>(Tribe.Faction.values().length);
    private ArrayList<OffsetCoord> glowingTilePositions = null;
    private PointF lastTouchedScreenCoord;
    private PointF lastDraggingScreenCoord;
    private RectF clippedViewport;
    private boolean showTerritories = true;
}
