package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;
import java.util.Arrays;

public class Town {

    private final static String LOG_TAG = "Town";

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

    enum EconomyArea {
        TOWN,
        FARM,
        MARKET,
        FORTRESS
    }

    enum EconomyProsperity {
        PROSPER,
        STALL,
        DETERIORATE
    }

    private static SizeF TileSize;

    /**
     *
     * @param tileSize
     */
    public static void SetTileSize(SizeF tileSize) {

        TileSize = tileSize;
    }

    public Town(TownMap map, OffsetCoord mapCoord, Type type, Side side) {

        this.map = map;
        this.mapCoord = mapCoord;
        this.type = type;
        this.side = side;
        this.level = new int[EconomyArea.values().length];
        Arrays.fill(this.level, 0);
        if (type == Type.HEADQUATER) {
            // Headquater always have 5-level town
            this.level[EconomyArea.TOWN.ordinal()] = 5;
        }
        this.exp = new int[EconomyArea.values().length];
        Arrays.fill(this.exp, 0);
        this.economyProsperity = new EconomyProsperity[EconomyArea.values().length];
        Arrays.fill(this.economyProsperity, EconomyProsperity.PROSPER);
        this.specialties = Specialities.NONE;
        this.happiness = 50;
    }

    /**
     *
     */
    public void update() {

        // If at peace
        if (getBattle() == null) {
            // Check if there's enemy squads
            boolean enemyExist = false;
            Side enemySide = Side.NEUTRAL;
            for (Squad squad : squads) {
                if (squad.getSide() != getSide() && !squad.isMoving()) {
                    enemyExist = true;
                    enemySide = squad.getSide();
                }
            }

            if (enemyExist) {
                // If enemy squad is exist, occupation town
                updateConquer(enemySide);
            } else {
                // Else, update economy
                resetConquer();
                if (type != Type.HEADQUATER) {
                    updateEconomy();
                }
            }
        }
    }

    /**
     *
     */
    private void updateConquer(Side occupationingSide) {

        if (this.occupyingSide != occupationingSide) {
            // New occupationor coming
            this.occupyingSide = occupationingSide;
            this.currentOccupationStep = 0;
            this.currentOccupationUpdateLeft = CONQUER_STEP_UPDATE_TIME;
            map.redrawTileSprite(mapCoord);
        } else if (--this.currentOccupationUpdateLeft == 0) {
            map.redrawTileSprite(mapCoord);
            if (++this.currentOccupationStep > CONQUER_TOTAL_STEP) {
                // Conquered!!
                Side prevSide = this.side;
                this.side = occupationingSide;
                this.currentOccupationStep = 0;
                this.currentOccupationUpdateLeft = CONQUER_STEP_UPDATE_TIME;

                // Update tiles
                map.redrawTileSprite(this.side);
                if (prevSide != Side.NEUTRAL) {
                    map.redrawTileSprite(prevSide);
                }
            } else {
                this.currentOccupationUpdateLeft = CONQUER_STEP_UPDATE_TIME;
            }
        }
    }

    /**
     *
     */
    private void resetConquer() {

        if (this.currentOccupationStep > 0) {
            map.redrawTileSprite(mapCoord);
        }
        this.occupyingSide = Side.NEUTRAL;
        this.currentOccupationStep = 0;
        this.currentOccupationUpdateLeft = CONQUER_STEP_UPDATE_TIME;
    }

    /**
     *
     */
    private void updateEconomy() {
        if (--economyUpdateLeft > 0) {
            return;
        }

        ArrayList<Town> neighborTowns = map.getNeighborTowns(mapCoord, false);

        // If it's towner town
        if (side == Side.TOWNER) {

            // Get base exp delta by economy prosper
            int[] expDeltas = new int[EconomyArea.values().length];
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                if (economyProsperity[i] == EconomyProsperity.PROSPER) {
                    expDeltas[i] = BASE_ECONOMY_EXP_DELTA_PROSPER;
                } else if (economyProsperity[i] == EconomyProsperity.STALL) {
                    expDeltas[i] = BASE_ECONOMY_EXP_DELTA_STALL;
                } else {
                    expDeltas[i] = BASE_ECONOMY_EXP_DELTA_DETERIORATE;
                }
            }

            // Update exp delta by aggregate neighbor town's status
            for (Town neighborTown : neighborTowns) {
                if (neighborTown.getSide() == getSide()) {
                    // Add exp delta from friendly neighbor towns
                    for (int i = 0; i < EconomyArea.values().length; ++i) {
                        if (economyProsperity[i] != EconomyProsperity.DETERIORATE) {
                            EconomyArea area = EconomyArea.values()[i];
                            if (EconomyArea.values()[i] != EconomyArea.FORTRESS) {
                                expDeltas[i] += Math.max(0, neighborTown.getLevel(area) -
                                        getLevel(area)) * BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR;
                            }
                        }
                    }
                } else if (neighborTown.getSide() != Side.NEUTRAL) {
                    // Add exp delta from enemy neighbor towns
                    for (int i = 0; i < EconomyArea.values().length; ++i) {
                        if (EconomyArea.values()[i] != EconomyArea.FORTRESS) {
                            expDeltas[i] += BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR;
                        } else {
                            expDeltas[i] -= BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR;
                        }
                    }
                }
            }

            // Update exp
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                exp[i] += expDeltas[i];
            }
            // First check if there's level down
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                // Level up or down
                if (exp[i] < 0) {
                    if (level[i] > 0) {
                        level[i]--;
                        exp[i] = REQUIRED_ECONOMY_EXP_FOR_LEVELUP[level[i]];
                    } else {
                        exp[i] = 0;
                    }
                }
            }
            // And then check level up case
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                if (level[i] > MAX_ECONOMY_LEVEL &&
                        exp[i] >= REQUIRED_ECONOMY_EXP_FOR_LEVELUP[level[i]]) {
                    if (Arrays.stream(level).sum() < MAX_ECONOMY_LEVEL) {
                        level[i]++;
                        exp[i] = 0;
                    } else {
                        exp[i] = REQUIRED_ECONOMY_EXP_FOR_LEVELUP[level[i]];
                    }
                }
            }

            // Get happiness delta
            int happinessDelta = getLevel(EconomyArea.TOWN) * BASE_HAPPINESS_DELTA_FROM_TOWN_LEVEL;
            for (Town neighborTown : neighborTowns) {
                if (neighborTown.getSide() == getSide()) {
                    happinessDelta += neighborTown.getLevel(EconomyArea.TOWN) *
                            BASE_HAPPINESS_DELTA_FROM_NEIGHBOR_TOWN;
                } else if (neighborTown.getSide() != Side.NEUTRAL) {
                    happinessDelta += BASE_HAPPINESS_DELTA_FROM_ENEMY_TOWN;
                }
            }

            // Update happiness
            happiness = Math.min(Math.max(happiness + happinessDelta, 0), 100);

        } else if (side != Side.NEUTRAL) {
            // It only deteriorated if it's on enemy's hand
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                // Update exp
                exp[i] = BASE_ECONOMY_EXP_DELTA_DETERIORATE;

                // Level down
                if (exp[i] < 0) {
                    if (level[i] > 0) {
                        level[i]--;
                        exp[i] = REQUIRED_ECONOMY_EXP_FOR_LEVELUP[level[i]];
                    } else {
                        exp[i] = 0;
                    }
                }
            }

            // Update happiness
            this.happiness = 0;
        }

        this.economyUpdateLeft = ECONOMY_UPDATE_COUNT;
    }

    /**
     *
     * @return
     */
    private Point getBaseSpriteIndex() {

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
     */
    public void createTileSprites() {

        if (baseSprite == null) {
            baseSprite =
                    new Sprite.Builder("Base", "tiles.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(2, 5).smooth(false)
                            .layer(SPRITE_LAYER).visible(true).build();
        }

        if (sideSprite == null) {
            sideSprite =
                    new Sprite.Builder("TerritoryBase", "tiles_territory.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(7, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.1f).visible(true).build();
        }

        if (borderSprites == null) {
            borderSprites = new ArrayList<>();
            for (int i = 0; i < 6; ++i) {
                Sprite border =
                        new Sprite.Builder("TerritoryBorder", "tiles_territory.png")
                                .position(new PointF(mapCoord.toGameCoord()))
                                .size(TileSize).gridSize(7, 5).smooth(false)
                                .layer(SPRITE_LAYER).depth(0.2f).visible(true).build();
                border.setGridIndex(i, side.ordinal());
                borderSprites.add(border);
            }
        }

        if (occupationSprite == null) {
            occupationSprite =
                    new Sprite.Builder("TerritoryOccupation", "tiles_occupation.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(6, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.3f).visible(true).build();
        }

        if (glowingSprite == null) {
            glowingSprite =
                    new Sprite.Builder("GlowingLine", "tiles.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(2, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.4f).visible(true).build();
        }
    }

    /**
     *
     * @param glowing
     * @param showTerritories
     * @return
     */
    public ArrayList<Sprite> getTileSprites(boolean glowing, boolean showTerritories) {

        createTileSprites();

        ArrayList<Sprite> sprites = new ArrayList<>();

        Point baseSpriteIndex = getBaseSpriteIndex();
        baseSprite.setGridIndex(baseSpriteIndex.x, baseSpriteIndex.y);
        sprites.add(baseSprite);

        if (showTerritories) {
            sideSprite.setGridIndex(6, side.ordinal());
            sideSprite.setVisible(true);
            sprites.add(sideSprite);

            ArrayList<Town> neighbors = map.getNeighborTowns(mapCoord, true);
            int index = 0;
            for (Town neighbor : neighbors) {
                Sprite borderSprite = borderSprites.get(index);
                if (neighbor == null || neighbor.getSide() != side) {
                    borderSprite.setGridIndex(index, side.ordinal());
                    borderSprite.setVisible(true);
                    sprites.add(borderSprite);
                } else {
                    borderSprite.setVisible(false);
                    borderSprite.commit();
                }
                index++;
            }
        } else {
            sideSprite.setVisible(false);
            sideSprite.commit();
            for (Sprite border : borderSprites) {
                border.setVisible(false);
                border.commit();
            }
        }

        // If occupation is ongoing, show progress
        if (currentOccupationStep > 0) {
            occupationSprite.setVisible(true);
            occupationSprite.setGridIndex(currentOccupationStep - 1, occupyingSide.ordinal());
            sprites.add(occupationSprite);
        } else {
            occupationSprite.setVisible(false);
            occupationSprite.commit();
        }

        // Show glowing sprites
        if (glowing) {
            glowingSprite.setVisible(true);
            glowingSprite.setGridIndex(0, 4);
            sprites.add(glowingSprite);
        } else {
            glowingSprite.setVisible(false);
            glowingSprite.commit();
        }

        return sprites;
    }

    /**
     *
     */
    public void removeTileSprites() {

        if (baseSprite != null) {
            baseSprite.close();
            baseSprite = null;
        }

        if (sideSprite != null) {
            sideSprite.close();
            sideSprite = null;
        }

        if (borderSprites != null) {
            for (Sprite border: borderSprites) {
                border.close();
            }
            borderSprites = null;
        }

        if (occupationSprite != null) {
            occupationSprite.close();
            occupationSprite = null;
        }

        if (glowingSprite != null) {
            glowingSprite.close();
            glowingSprite = null;
        }
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

    /**
     *
     * @param area
     * @return
     */
    public int getLevel(EconomyArea area) {

        return level[area.ordinal()];
    }

    /**
     *
     * @return
     */
    public Side getSide() {

        return side;
    }

    /**
     *
     * @return
     */
    public boolean isOccupying() {
        return occupyingSide != Side.NEUTRAL && getBattle() == null;
    }

    private final static int SPRITE_LAYER = 0;
    private final static int ECONOMY_UPDATE_COUNT = 30;
    private final static int MAX_ECONOMY_LEVEL = 5;
    private final static int[] REQUIRED_ECONOMY_EXP_FOR_LEVELUP =
            new int[] { 1000, 2000, 3000, 4000, 5000};
    private final static int BASE_ECONOMY_EXP_DELTA_PROSPER = 10;
    private final static int BASE_ECONOMY_EXP_DELTA_STALL = 0;
    private final static int BASE_ECONOMY_EXP_DELTA_DETERIORATE = -10;
    private final static int BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR = 10;
    private final static int BASE_HAPPINESS_DELTA_FROM_TOWN_LEVEL = 2;
    private final static int BASE_HAPPINESS_DELTA_FROM_NEIGHBOR_TOWN = 1;
    private final static int BASE_HAPPINESS_DELTA_FROM_ENEMY_TOWN = -5;
    private final static int CONQUER_TOTAL_STEP = 5;
    private final static int CONQUER_STEP_UPDATE_TIME = 30;

    private TownMap map;
    private OffsetCoord mapCoord;
    private Type type;
    private Side side;
    private boolean focused = false;

    // Occupation
    private Side occupyingSide = Side.NEUTRAL;
    private int currentOccupationStep = 0;
    private int currentOccupationUpdateLeft = CONQUER_STEP_UPDATE_TIME;

    // Economy
    private int[] level;
    private int[] exp;
    private EconomyProsperity[] economyProsperity;
    private Specialities specialties;
    private int happiness;
    private int economyUpdateLeft = ECONOMY_UPDATE_COUNT;

    // Squad
    private ArrayList<Squad> squads = new ArrayList<>();
    private Battle battle;

    // Sprite
    Sprite baseSprite = null;
    Sprite sideSprite = null;
    ArrayList<Sprite> borderSprites = null;
    Sprite occupationSprite = null;
    Sprite glowingSprite = null;
}
