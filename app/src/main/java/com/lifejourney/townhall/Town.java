package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;

public class Town {

    enum Type {
        GRASS,
        BADLAND,
        FOREST,
        HILL,
        MOUNTAIN,
        RIVER,
        HEADQUATER,
        UNKNOWN;

        int availableEconomySlot() {
            switch (this) {
                case GRASS:
                    return 4;
                case BADLAND:
                    return 4;
                case FOREST:
                    return 2;
                case HILL:
                    return 2;
                case MOUNTAIN:
                    return 0;
                case RIVER:
                    return 0;
                case HEADQUATER:
                    return 0;
                case UNKNOWN:
                    return 0;
                default:
                    return 0;
            }
        }

        boolean isMovable(Squad squad) {
            switch (this) {
                case GRASS:
                    return true;
                case BADLAND:
                    return true;
                case FOREST:
                    return true;
                case HILL:
                    return true;
                case MOUNTAIN:
                    return squad.getSide() == Side.BANDIT;
                case RIVER:
                    return squad.getSide() == Side.PIRATE;
                case HEADQUATER:
                    return true;
                case UNKNOWN:
                    return false;
                default:
                    return false;
            }
        }
    }

    enum Side {
        NEUTRAL,
        TOWNER,
        BANDIT,
        PIRATE,
        REBEL
    }

    enum Specialities {
        IRON,
        WOOD,
        GEM,
        HORSE,
        POWDER,
        NONE;
    }

    private static SizeF TileSize;

    /**
     *
     * @param tileSize
     */
    public static void SetTileSize(SizeF tileSize) {

        TileSize = tileSize;
    }

    public Town(OffsetCoord mapCoord, Type type, Side side) {
        this.mapCoord = mapCoord;
        this.type = type;
        this.side = side;
    }

    /**
     *
     * @return
     */
    private Point getTextureGridForTile() {
        switch (type) {
            case GRASS:
                return new Point(0, 0);
            case BADLAND:
                return new Point(0, 1);
            case RIVER:
                return new Point(0, 2);
            case HEADQUATER:
                return new Point(0, 3);
            default:
                return new Point(0, 0);
        }
    }

    /**
     *
     * @return
     */

    public void setType(Type type) {
        this.type = type;
    }

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

    /**
     *
     * @return
     */
    public Type getType() {
        return type;
    }

    private final static int SPRITE_LAYER = 0;
    private final static int MAX_ECONOMY_LEVEL = 5;

    private OffsetCoord mapCoord;
    private Type type;
    private Side side;

    // Economy
    private int townLevel = 0;
    private int fortressLevel = 0;
    private int farmLevel = 0;
    private int marketLevel = 0;
    private int hapiness = 0;
    private Specialities specialties;

    // Squad
    private ArrayList<Squad> squads = new ArrayList<>();
    private Battle battle;

    private boolean focused = false;
}
