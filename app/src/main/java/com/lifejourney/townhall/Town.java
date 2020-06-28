package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;

public class Town {

    enum Side {
        TOWN,
        BANDIT,
        PIRATE,
        REBEL,
        NEUTRAL
    }

    private static SizeF TileSize;

    /**
     *
     * @param tileSize
     */
    public static void SetTileSize(SizeF tileSize) {

        TileSize = tileSize;
    }

    public Town(OffsetCoord mapCoord, TownMap.TileType type) {

        this.mapCoord = mapCoord;
        this.type = type;
    }

    private Point getTextureGridForTile() {
        switch (type) {
            case GRASS:
                return new Point(0, 0);
            case BADLAND:
                return new Point(0, 1);
            case WATER:
                return new Point(0, 2);
            case TOWNHALL:
                return new Point(0, 3);
            default:
                return new Point(0, 0);
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<Sprite> getTileSprite() {

        ArrayList<Sprite> sprites = new ArrayList<>();

        Sprite baseSprite =
                new Sprite.Builder("Base", "tiles.png")
                        .position(new PointF(mapCoord.toGameCoord()))
                        .size(TileSize).gridSize(2, 5).smooth(false)
                        .layer(SPRITE_LAYER).visible(true).build();
        Point textureGridForTile = getTextureGridForTile();
        baseSprite.setGridIndex(textureGridForTile.x, textureGridForTile.y);
        sprites.add(baseSprite);

        return sprites;
    }

    /**
     *
     * @return
     */
    public ArrayList<Squad> getSquads() {

        return squads;
    }

    /**
     *
     * @param squads
     */
    public void setSquads(ArrayList<Squad> squads) {

        this.squads = squads;
    }

    /**
     *
     * @param squad
     */
    public void addSquad(Squad squad) {

        if (!this.squads.contains(squad)) {
            this.squads.add(squad);
        }
    }

    /**
     *
     * @param squad
     */
    public void removeSquad(Squad squad) {

        this.squads.remove(squad);
    }

    /**
     *
     * @return
     */
    public Battle getBattle() {

        return battle;
    }

    /**
     *
     * @param battle
     */
    public void setBattle(Battle battle) {

        this.battle = battle;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getMapCoord() {

        return mapCoord;
    }

    /**
     *
     * @param mapCoord
     */
    public void setMapCoord(OffsetCoord mapCoord) {

        this.mapCoord = mapCoord;
    }

    /**
     *
     * @param focused
     */
    public void setFocus(boolean focused) {
        this.focused = focused;
    }

    /**
     *
     * @return
     */
    public boolean getFocus() {
        return focused;
    }

    private final static int SPRITE_LAYER = 0;

    private OffsetCoord mapCoord;
    private TownMap.TileType type;
    private Battle battle;
    private ArrayList<Squad> squads = new ArrayList<>();
    private boolean focused = false;
}
