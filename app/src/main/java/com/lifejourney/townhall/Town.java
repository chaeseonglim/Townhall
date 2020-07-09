package com.lifejourney.townhall;

import android.net.sip.SipSession;
import android.util.Log;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;
import java.util.Arrays;

public class Town {

    private final static String LOG_TAG = "Town";

    public interface Event {

        void onTownOccupied(Town town, Side prevSide, Side newSide);
    }

    enum Type {
        GRASS,
        BADLAND,
        FOREST,
        HILL,
        MOUNTAIN,
        RIVER,
        HEADQUARTER,
        UNKNOWN;

        int availableEconomySlot() {
            switch (this) {
                case GRASS:
                    return 3;
                case BADLAND:
                    return 3;
                case FOREST:
                    return 2;
                case HILL:
                    return 2;
                case MOUNTAIN:
                case RIVER:
                case HEADQUARTER:
                case UNKNOWN:
                    return 0;
                default:
                    return 0;
            }
        }

        boolean isMovable(Squad squad) {
            switch (this) {
                case GRASS:
                case BADLAND:
                case FOREST:
                case HILL:
                    return true;
                case MOUNTAIN:
                    return squad.getSide() == Side.BANDIT;
                case RIVER:
                    return squad.getSide() == Side.PIRATE;
                case HEADQUARTER:
                    return true;
                case UNKNOWN:
                    return false;
                default:
                    return false;
            }
        }

        public boolean isProsperable() {
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
                    return false;
                case RIVER:
                    return false;
                case HEADQUARTER:
                    return false;
                case UNKNOWN:
                    return false;
                default:
                    return false;
            }
        }

        public float prosperRate(EconomyArea area) {
            switch (this) {
                case GRASS:
                    if (area == EconomyArea.FORTRESS) {
                        return 0.5f;
                    } else {
                        return 1.0f;
                    }
                case BADLAND:
                    if (area == EconomyArea.FORTRESS) {
                        return 0.6f;
                    } else {
                        return 0.8f;
                    }
                case FOREST:
                    if (area == EconomyArea.FORTRESS) {
                        return 0.9f;
                    } else if (area == EconomyArea.FARM) {
                        return 0.8f;
                    } else if (area == EconomyArea.MARKET) {
                        return 0.4f;
                    } else {
                        return 0.5f;
                    }
                case HILL:
                    if (area == EconomyArea.FORTRESS) {
                        return 1.0f;
                    } else if (area == EconomyArea.FARM) {
                        return 0.3f;
                    } else if (area == EconomyArea.DOWNTOWN) {
                        return 0.8f;
                    } else {
                        return 0.5f;
                    }
                default:
                    return 0.0f;
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
        DOWNTOWN,
        FARM,
        MARKET,
        FORTRESS;
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

    public Town(Event listener, TownMap map, OffsetCoord mapCoord, Type type, Side side) {

        this.listener = listener;
        this.map = map;
        this.mapCoord = mapCoord;
        this.type = type;
        this.side = side;
        this.levels = new int[EconomyArea.values().length];
        Arrays.fill(this.levels, 0);
        this.exps = new int[EconomyArea.values().length];
        Arrays.fill(this.exps, 0);
        this.economyProsperity = new EconomyProsperity[EconomyArea.values().length];
        Arrays.fill(this.economyProsperity, EconomyProsperity.PROSPER);
        for (int i = 0; i < 4; ++i) {
            if (!this.type.isProsperable()) {
                this.economyProsperity[i] = EconomyProsperity.DETERIORATE;
            }
        }
        /*
        this.economyProsperity[0] = EconomyProsperity.DETERIORATE;
        this.economyProsperity[2] = EconomyProsperity.DETERIORATE;
        this.economyProsperity[3] = EconomyProsperity.DETERIORATE;
         */
        this.specialties = Specialities.NONE;
        this.happiness = 50;
    }

    /**
     *
     */
    public void update() {

        // Update town only when it's at peace
        if (getBattle() != null) {
            return;
        }

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
            // If enemy squad is exist, occupy town
            updateOccupation(enemySide);
        } else {
            // Else, update economy
            resetOccupation();
            if (type != Type.HEADQUARTER) {
                updateEconomy();
            }
        }
    }

    /**
     *
     */
    private void updateOccupation(Side occupationingSide) {

        if (this.occupyingSide != occupationingSide) {
            // Someone try to occupy this
            this.occupyingSide = occupationingSide;
            this.occupationStep = 0;
            this.occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;
            map.redrawTileSprite(mapCoord);
        } else if (--this.occupationUpdateLeft == 0) {
            map.redrawTileSprite(mapCoord);
            if (++this.occupationStep > OCCUPATION_TOTAL_STEP) {
                // It's finally occupied
                Side prevSide = this.side;
                this.side = occupationingSide;
                this.occupationStep = 0;
                this.occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;

                // Update tiles
                map.redrawTileSprite(this.side);
                if (prevSide != Side.NEUTRAL) {
                    map.redrawTileSprite(prevSide);
                }

                if (listener != null) {
                    listener.onTownOccupied(this, prevSide, side);
                }
            } else {
                this.occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;
            }
        }
    }

    /**
     *
     */
    private void resetOccupation() {

        if (this.occupationStep > 0) {
            map.redrawTileSprite(mapCoord);
        }
        this.occupyingSide = Side.NEUTRAL;
        this.occupationStep = 0;
        this.occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;
    }

    /**
     *
     */
    private void updateEconomy() {

        if (economyUpdateLeft-- > 0) {
            return;
        }

        ArrayList<Town> neighborTowns = map.getNeighborTowns(mapCoord, false);

        if (side == Side.TOWNER) {
            // Economy only prosper when it's Towner's town
            // Get base exp delta by economy prosper
            int[] expDeltas = new int[EconomyArea.values().length];
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                if (economyProsperity[i] == EconomyProsperity.PROSPER) {
                    EconomyArea area = EconomyArea.values()[i];
                    expDeltas[i] = (int) (BASE_ECONOMY_EXP_DELTA_PROSPER * type.prosperRate(area));
                } else if (economyProsperity[i] == EconomyProsperity.STALL) {
                    expDeltas[i] = BASE_ECONOMY_EXP_DELTA_STALL;
                } else {
                    expDeltas[i] = BASE_ECONOMY_EXP_DELTA_DETERIORATE;
                }
            }

            // Count neighbor town's stat
            int totalNeighborTownLevel = 0;
            int totalEnemyTownCount = 0;
            for (Town neighborTown : neighborTowns) {
                if (neighborTown.getSide() == getSide()) {
                    totalNeighborTownLevel += neighborTown.getLevel(EconomyArea.DOWNTOWN);
                } else if (neighborTown.getSide() != Side.NEUTRAL) {
                    totalEnemyTownCount++;
                }
            }

            // Update exp delta by aggregating neighbor town's status
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                // Exp delta from friendly neighbor downtown level
                if (economyProsperity[i] != EconomyProsperity.DETERIORATE) {
                    EconomyArea area = EconomyArea.values()[i];
                    if (EconomyArea.values()[i] != EconomyArea.FORTRESS) {
                        expDeltas[i] += totalNeighborTownLevel *
                                BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR_DOWNTOWN * type.prosperRate(area);
                    }
                }
                // Exp delta from enemy neighbor towns
                if (EconomyArea.values()[i] != EconomyArea.FORTRESS) {
                    expDeltas[i] += BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR_ENEMY * totalEnemyTownCount;
                } else {
                    expDeltas[i] -= BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR_ENEMY * totalEnemyTownCount;
                }
            }

            // Update exp
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                exps[i] += expDeltas[i];
            }

            // Back-up previous levels
            int[] prevLevels = Arrays.copyOf(levels, levels.length);

            Log.i(LOG_TAG, "levels " + levels[0] + " " + levels[1] + " " + levels[2] + " " + levels[3]);
            Log.i(LOG_TAG, "exps " + exps[0] + " " + exps[1] + " " + exps[2] + " " + exps[3]);

            // Level down if exp is negative
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                if (exps[i] < 0) {
                    if (levels[i] > 0) {
                        levels[i]--;
                        exps[i] = REQUIRED_ECONOMY_EXP_FOR_LEVEL_UP[levels[i]];

                        // Remove from placement
                        if (levels[i] == 4 || levels[i] == 3 || levels[i] == 0) {
                            economySlots.remove(EconomyArea.values()[i]);
                        }
                    } else {
                        exps[i] = 0;
                    }
                }
            }

            // And level up if exp is above the required exp for this level
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                EconomyArea area = EconomyArea.values()[i];
                if (levels[i] < MAX_ECONOMY_LEVEL &&
                        exps[i] >= REQUIRED_ECONOMY_EXP_FOR_LEVEL_UP[levels[i]]) {
                    if (Arrays.stream(levels).sum() < MAX_ECONOMY_LEVEL) {
                        // In case of level 1, need to check if slot is full
                        if (levels[i] == 0 || levels[i] == 3 || levels[i] == 4) {
                            if (economySlots.size() < type.availableEconomySlot()) {
                                economySlots.add(area);
                                levels[i]++;
                                exps[i] = 0;
                            } else {
                                exps[i] = REQUIRED_ECONOMY_EXP_FOR_LEVEL_UP[levels[i]];
                            }
                        } else {
                            levels[i]++;
                            exps[i] = 0;
                        }
                    } else {
                        exps[i] = REQUIRED_ECONOMY_EXP_FOR_LEVEL_UP[levels[i]];
                    }
                }
            }

            // If economy is changed, redraw the tile
            if (!Arrays.equals(prevLevels, levels)) {
                map.redrawTileSprite(mapCoord);
            }

            // Get happiness delta
            int happinessDelta = getLevel(EconomyArea.DOWNTOWN) * BASE_HAPPINESS_DELTA_FROM_TOWN_LEVEL;
            for (Town neighborTown : neighborTowns) {
                if (neighborTown.getSide() == getSide()) {
                    happinessDelta += neighborTown.getLevel(EconomyArea.DOWNTOWN) *
                            BASE_HAPPINESS_DELTA_FROM_NEIGHBOR_TOWN;
                } else if (neighborTown.getSide() != Side.NEUTRAL) {
                    happinessDelta += BASE_HAPPINESS_DELTA_FROM_ENEMY_TOWN;
                }
            }

            // Update happiness
            happiness = Math.min(Math.max(BASE_HAPPINESS + happinessDelta, 0), 100);

        } else if (side != Side.NEUTRAL) {

            // It only deteriorated if it's on enemy's hand
            for (int i = 0; i < EconomyArea.values().length; ++i) {
                // Update exp
                exps[i] = BASE_ECONOMY_EXP_DELTA_DETERIORATE;

                // Level down
                if (exps[i] < 0) {
                    if (levels[i] > 0) {
                        levels[i]--;
                        exps[i] = REQUIRED_ECONOMY_EXP_FOR_LEVEL_UP[levels[i]];
                    } else {
                        exps[i] = 0;
                    }
                }
            }

            // Update happiness
            happiness = 0;
        }

        economyUpdateLeft = ECONOMY_UPDATE_COUNT;
    }

    /**
     *
     */
    public void createTileSprites() {

        if (baseSprite == null) {
            baseSprite =
                    new Sprite.Builder("Base", "tiles.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(4, 8).smooth(false)
                            .layer(SPRITE_LAYER).visible(true).build();
        }

        if (economySprites == null) {
            economySprites = new ArrayList<>();
            for (int i = 0; i < 3; ++i) {
                Sprite economyObject =
                        new Sprite.Builder("Economy", "tiles_economy_objects.png")
                                .position(new PointF(mapCoord.toGameCoord()))
                                .size(TileSize.clone().multiply(0.5f)).gridSize(5, 5).smooth(false)
                                .layer(SPRITE_LAYER).depth(0.1f).visible(false).build();
                economySprites.add(economyObject);
            }
        }

        if (sideSprite == null) {
            sideSprite =
                    new Sprite.Builder("TerritoryBase", "tiles_territory.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(7, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.2f).visible(true).build();
        }

        if (borderSprites == null) {
            borderSprites = new ArrayList<>();
            for (int i = 0; i < 6; ++i) {
                Sprite border =
                        new Sprite.Builder("TerritoryBorder", "tiles_territory.png")
                                .position(new PointF(mapCoord.toGameCoord()))
                                .size(TileSize).gridSize(7, 5).smooth(false)
                                .layer(SPRITE_LAYER).depth(0.3f).visible(true).build();
                border.setGridIndex(i, side.ordinal());
                borderSprites.add(border);
            }
        }

        if (occupationSprite == null) {
            occupationSprite =
                    new Sprite.Builder("TerritoryOccupation", "tiles_occupation.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(6, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.4f).visible(true).build();
        }

        if (glowingSprite == null) {
            glowingSprite =
                    new Sprite.Builder("GlowingLine", "tiles.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(4, 8).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.5f).visible(true).build();
            glowingSprite.setGridIndex(0, 7);
        }
    }

    /**
     *
     * @param glowing
     * @param showTerritories
     * @return
     */
    public ArrayList<Sprite> getTileSprites(boolean glowing, boolean showTerritories) {

        // Create tile sprites first
        createTileSprites();

        ArrayList<Sprite> sprites = new ArrayList<>();

        // Set base sprite
        if (Arrays.stream(levels).sum() > 0) {
            if (type == Type.FOREST) {
                baseSprite.setGridIndex(0, Type.GRASS.ordinal());
            } else if (type == Type.HILL) {
                baseSprite.setGridIndex(0, Type.BADLAND.ordinal());
            } else {
                baseSprite.setGridIndex(0, type.ordinal());
            }
        } else {
            baseSprite.setGridIndex(baseSpriteSelection, type.ordinal());
        }
        sprites.add(baseSprite);

        // Set economy sprite
        if (economySlots.size() == 0) {
            for (Sprite sprite : economySprites) {
                sprite.setVisible(false);
            }
        } else {
            int i;
            for (i = 0; i < economySlots.size(); ++i) {
                Sprite sprite = economySprites.get(i);

                EconomyArea area = economySlots.get(i);
                int level = levels[area.ordinal()];
                sprite.setGridIndex(level - 1, area.ordinal());

                int placement = (i + economySpriteSelection) % 3;
                if (placement == 0) {
                    sprite.setPositionOffset(new PointF(0.0f, TileSize.height/4));
                } else if (placement == 1) {
                    sprite.setPositionOffset(new PointF(-TileSize.width/4, -TileSize.height/8));
                } else if (placement == 2) {
                    sprite.setPositionOffset(new PointF(TileSize.width/4, -TileSize.height/8));
                }

                sprite.setVisible(true);
                sprites.add(sprite);
            }
            for (; i < 3; ++i) {
                Sprite sprite = economySprites.get(i);

                int placement = (i + economySpriteSelection) % 3;
                if (placement == 0) {
                    sprite.setPositionOffset(new PointF(0.0f, TileSize.height/4));
                } else if (placement == 1) {
                    sprite.setPositionOffset(new PointF(-TileSize.width/4, -TileSize.height/8));
                } else if (placement == 2) {
                    sprite.setPositionOffset(new PointF(TileSize.width/4, -TileSize.height/8));
                }
                if (type == Type.FOREST) {
                    sprite.setGridIndex(0, EconomyArea.values().length);
                    sprite.setVisible(true);
                    sprites.add(sprite);
                } else if (type == Type.HILL) {
                    sprite.setGridIndex(1, EconomyArea.values().length);
                    sprite.setVisible(true);
                    sprites.add(sprite);
                } else {
                    economySprites.get(i).setVisible(false);
                }
            }
        }

        if (showTerritories) {
            // Set side sprite
            sideSprite.setGridIndex(6, side.ordinal());
            sideSprite.setVisible(true);
            sprites.add(sideSprite);

            // Set border sprite
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
        if (occupationStep > 0) {
            occupationSprite.setVisible(true);
            occupationSprite.setGridIndex(occupationStep - 1, occupyingSide.ordinal());
            sprites.add(occupationSprite);
        } else {
            occupationSprite.setVisible(false);
            occupationSprite.commit();
        }

        // Show glowing sprites
        if (glowing) {
            glowingSprite.setVisible(true);
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
        if (economySprites != null ) {
            for (Sprite economySprite: economySprites) {
                economySprite.close();
            }
            economySprites = null;
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

        return levels[area.ordinal()];
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
    public int collectTax() {

        if (getBattle() != null) {
            return 0;
        }

        // Count neighbor town's stat
        ArrayList<Town> neighborTowns = map.getNeighborTowns(mapCoord, false);
        int totalNeighborTownLevel = 0;
        for (Town neighborTown : neighborTowns) {
            if (neighborTown.getSide() == getSide()) {
                totalNeighborTownLevel += neighborTown.getLevel(EconomyArea.DOWNTOWN);
            }
        }

        return (int) (levels[EconomyArea.MARKET.ordinal()] * BASE_TAX_DELTA_FROM_MARKET_LEVEL *
                (1.0f + totalNeighborTownLevel * TAX_PROPOSITION_FROM_NEIGHBOR_DOWNTOWN_LEVEL) *
                (100.0f / happiness));
    }

    /**
     *
     * @return
     */
    public int collectPopulation() {

        if (getBattle() != null) {
            return 0;
        }

        if (type == Type.HEADQUARTER) {
            return HEADQUARTER_POPULATION;
        }

        return levels[EconomyArea.FARM.ordinal()] * BASE_POPULATION_DELTA_FROM_FARM_LEVEL +
               levels[EconomyArea.DOWNTOWN.ordinal()] * BASE_POPULATION_DELTA_FROM_NEIGHBOR_DOWNTOWN_LEVEL;
    }

    /**
     *
     * @return
     */
    public int collectHappiness() {
        return happiness;
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
    private final static int[] REQUIRED_ECONOMY_EXP_FOR_LEVEL_UP =
            new int[] { 100, 200, 300, 400, 500};
    private final static int BASE_ECONOMY_EXP_DELTA_PROSPER = 10;
    private final static int BASE_ECONOMY_EXP_DELTA_STALL = 0;
    private final static int BASE_ECONOMY_EXP_DELTA_DETERIORATE = -5;
    private final static int BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR_DOWNTOWN = 5;
    private final static int BASE_ECONOMY_EXP_DELTA_FROM_NEIGHBOR_ENEMY = 5;
    private final static int BASE_HAPPINESS = 50;
    private final static int BASE_HAPPINESS_DELTA_FROM_TOWN_LEVEL = 2;
    private final static int BASE_HAPPINESS_DELTA_FROM_NEIGHBOR_TOWN = 1;
    private final static int BASE_HAPPINESS_DELTA_FROM_ENEMY_TOWN = -5;
    private final static int BASE_TAX_DELTA_FROM_MARKET_LEVEL = 10;
    private final static float TAX_PROPOSITION_FROM_NEIGHBOR_DOWNTOWN_LEVEL = 0.1f;
    private final static int BASE_POPULATION_DELTA_FROM_FARM_LEVEL = 20;
    private final static int BASE_POPULATION_DELTA_FROM_NEIGHBOR_DOWNTOWN_LEVEL = 10;
    private final static int HEADQUARTER_POPULATION = 50;
    private final static int OCCUPATION_TOTAL_STEP = 5;
    private final static int OCCUPATION_STEP_UPDATE_TIME = 30;

    private Event listener = null;
    private TownMap map;
    private OffsetCoord mapCoord;
    private Type type;
    private Side side;
    private boolean focused = false;

    // Occupation
    private Side occupyingSide = Side.NEUTRAL;
    private int occupationStep = 0;
    private int occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;

    // Economy
    private int[] levels;
    private int[] exps;
    private EconomyProsperity[] economyProsperity;
    private ArrayList<EconomyArea> economySlots = new ArrayList<>();
    private int happiness;
    private Specialities specialties;
    private int economyUpdateLeft = ECONOMY_UPDATE_COUNT;

    // Squad
    private ArrayList<Squad> squads = new ArrayList<>();
    private Battle battle;

    // Sprite
    private Sprite baseSprite = null;
    private Sprite sideSprite = null;
    private ArrayList<Sprite> economySprites = null;
    private ArrayList<Sprite> borderSprites = null;
    private Sprite occupationSprite = null;
    private Sprite glowingSprite = null;
    private int baseSpriteSelection = (int)(Math.random()*4);
    private int economySpriteSelection = (int)(Math.random()*3);
}
