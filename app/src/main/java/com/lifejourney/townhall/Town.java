package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;
import java.util.Arrays;

public class Town {

    private final static String LOG_TAG = "Town";

    public interface Event {

        void onTownUpdated(Town town);

        void onTownOccupied(Town town, Faction prevFaction, Faction newFaction);
    }

    enum Terrain {
        GRASS,
        BADLANDS,
        FOREST,
        HILL,
        MOUNTAIN,
        RIVER,
        HEADQUARTER_GRASS,
        HEADQUARTER_BADLANDS,
        UNKNOWN;

        String toGameString() {
            switch (this) {
                case GRASS:
                    return "초원";
                case BADLANDS:
                    return "황무지";
                case FOREST:
                    return "숲";
                case HILL:
                    return "언덕";
                case MOUNTAIN:
                    return "산";
                case RIVER:
                    return "강";
                case HEADQUARTER_GRASS:
                case HEADQUARTER_BADLANDS:
                    return "마을회관";
                case UNKNOWN:
                default:
                    return "모름";
            }
        }

        int availableEconomySlot() {
            switch (this) {
                case GRASS:
                case BADLANDS:
                    return 3;
                case FOREST:
                case HILL:
                    return 2;
                case MOUNTAIN:
                case RIVER:
                case HEADQUARTER_GRASS:
                case HEADQUARTER_BADLANDS:
                case UNKNOWN:
                default:
                    return 0;
            }
        }

        boolean isMovable(Squad squad) {
            switch (this) {
                case GRASS:
                case BADLANDS:
                case FOREST:
                case HILL:
                case HEADQUARTER_GRASS:
                case HEADQUARTER_BADLANDS:
                    return true;
                case MOUNTAIN:
                    return squad.getFaction() == Faction.BANDIT;
                case RIVER:
                    return squad.getFaction() == Faction.PIRATE;
                case UNKNOWN:
                default:
                    return false;
            }
        }

        public boolean isProsperable() {
            switch (this) {
                case GRASS:
                case BADLANDS:
                case FOREST:
                case HILL:
                    return true;
                case MOUNTAIN:
                case RIVER:
                case HEADQUARTER_GRASS:
                case HEADQUARTER_BADLANDS:
                case UNKNOWN:
                default:
                    return false;
            }
        }

        public float prosperRate(Facility facility) {
            switch (this) {
                case GRASS:
                    if (facility == Facility.FORTRESS) {
                        return 0.5f;
                    } else {
                        return 1.0f;
                    }
                case BADLANDS:
                    if (facility == Facility.FORTRESS) {
                        return 0.6f;
                    } else {
                        return 0.8f;
                    }
                case FOREST:
                    if (facility == Facility.FORTRESS) {
                        return 0.9f;
                    } else if (facility == Facility.FARM) {
                        return 0.8f;
                    } else if (facility == Facility.MARKET) {
                        return 0.4f;
                    } else {
                        return 0.5f;
                    }
                case HILL:
                    if (facility == Facility.FORTRESS) {
                        return 1.0f;
                    } else if (facility == Facility.FARM) {
                        return 0.3f;
                    } else if (facility == Facility.DOWNTOWN) {
                        return 0.8f;
                    } else {
                        return 0.5f;
                    }
                default:
                    return 0.0f;
            }
        }
    }

    enum Faction {
        NEUTRAL,
        VILLAGER,
        BANDIT,
        PIRATE,
        REBEL;

        String toGameString() {
            switch(this) {
                case NEUTRAL:
                    return "중립";
                case VILLAGER:
                    return "주민";
                case BANDIT:
                    return "도적";
                case PIRATE:
                    return "해적";
                case REBEL:
                    return "반란군";
                default:
                    return "";
            }
        }
    }

    enum Specialities {
        IRON,
        WOOD,
        GEM,
        HORSE,
        POWDER,
        NONE;
    }

    enum Facility {
        DOWNTOWN,
        FARM,
        MARKET,
        FORTRESS;
    }

    enum FacilityDevelopment {
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

    public Town(Event listener, OffsetCoord mapCoord, Terrain terrain, Faction faction) {

        this.listener = listener;
        this.mapCoord = mapCoord;
        this.terrain = terrain;
        this.faction = faction;
        this.facilityLevels = new int[Facility.values().length];
        Arrays.fill(this.facilityLevels, 0);
        this.facilityExps = new int[Facility.values().length];
        Arrays.fill(this.facilityExps, 0);
        this.facilityDevelopment = new FacilityDevelopment[Facility.values().length];
        Arrays.fill(this.facilityDevelopment, FacilityDevelopment.PROSPER);
        for (int i = 0; i < 4; ++i) {
            if (!this.terrain.isProsperable()) {
                this.facilityDevelopment[i] = FacilityDevelopment.DETERIORATE;
            }
        }
        /*
        this.facilityDevelopment[0] = FacilityDevelopment.DETERIORATE;
        this.facilityDevelopment[2] = FacilityDevelopment.DETERIORATE;
        this.facilityDevelopment[3] = FacilityDevelopment.DETERIORATE;
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
        Faction enemyFaction = Faction.NEUTRAL;
        for (Squad squad : squads) {
            if (squad.getFaction() != getFaction() && !squad.isMoving()) {
                enemyExist = true;
                enemyFaction = squad.getFaction();
            }
        }

        if (enemyExist) {
            // If enemy squad is exist, occupy town
            updateOccupation(enemyFaction);
        } else {
            // Else, update facility
            resetOccupation();
            if (terrain != Terrain.HEADQUARTER_GRASS) {
                updateFacility();
            }
        }
    }

    /**
     *
     */
    private void updateOccupation(Faction occupationingFaction) {

        if (this.occupyingFaction != occupationingFaction) {
            // Someone try to occupy this
            this.occupyingFaction = occupationingFaction;
            this.occupationStep = 0;
            this.occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;

            listener.onTownUpdated(this);
        } else if (--this.occupationUpdateLeft == 0) {
            if (++this.occupationStep > OCCUPATION_TOTAL_STEP) {
                // It's finally occupied
                Faction prevFaction = this.faction;
                this.faction = occupationingFaction;
                this.occupationStep = 0;
                this.occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;

                listener.onTownOccupied(this, prevFaction, faction);
            } else {
                this.occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;
            }

            listener.onTownUpdated(this);
        }
    }

    /**
     *
     */
    private void resetOccupation() {

        if (this.occupationStep > 0) {
            listener.onTownUpdated(this);
        }
        this.occupyingFaction = Faction.NEUTRAL;
        this.occupationStep = 0;
        this.occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;
    }

    /**
     *
     */
    private void updateFacility() {

        if (facilityUpdateLeft-- > 0) {
            return;
        }

        if (faction == Faction.VILLAGER) {
            // Economy only prosper when it's Villager's town
            // Get base exp delta by facility development
            int[] expDeltas = new int[Facility.values().length];
            for (int i = 0; i < Facility.values().length; ++i) {
                if (facilityDevelopment[i] == FacilityDevelopment.PROSPER) {
                    Facility facility = Facility.values()[i];
                    expDeltas[i] = (int) (BASE_FACILITY_EXP_DELTA_PROSPER * terrain.prosperRate(facility));
                } else if (facilityDevelopment[i] == FacilityDevelopment.STALL) {
                    expDeltas[i] = BASE_FACILITY_EXP_DELTA_STALL;
                } else {
                    expDeltas[i] = BASE_FACILITY_EXP_DELTA_DETERIORATE;
                }
            }

            // Count neighbor town's stat
            int totalNeighborTownLevel = 0;
            int totalEnemyTownCount = 0;
            for (Town neighborTown : neighborTowns) {
                if (neighborTown != null) {
                    if (neighborTown.getFaction() == getFaction()) {
                        totalNeighborTownLevel += neighborTown.getFacilityLevel(Facility.DOWNTOWN);
                    } else if (neighborTown.getFaction() != Faction.NEUTRAL) {
                        totalEnemyTownCount++;
                    }
                }
            }

            // Update exp delta by aggregating neighbor town's status
            for (int i = 0; i < Facility.values().length; ++i) {
                // Exp delta from friendly neighbor downtown level
                if (facilityDevelopment[i] != FacilityDevelopment.DETERIORATE) {
                    Facility facility = Facility.values()[i];
                    if (Facility.values()[i] != Facility.FORTRESS) {
                        expDeltas[i] += totalNeighborTownLevel *
                                BASE_FACILITY_EXP_DELTA_FROM_NEIGHBOR_DOWNTOWN * terrain.prosperRate(facility);
                    }
                }
                // Exp delta from enemy neighbor towns
                if (Facility.values()[i] != Facility.FORTRESS) {
                    expDeltas[i] += BASE_FACILITY_EXP_DELTA_FROM_NEIGHBOR_ENEMY * totalEnemyTownCount;
                } else {
                    expDeltas[i] -= BASE_FACILITY_EXP_DELTA_FROM_NEIGHBOR_ENEMY * totalEnemyTownCount;
                }
            }

            // Update exp
            for (int i = 0; i < Facility.values().length; ++i) {
                facilityExps[i] += expDeltas[i];
            }

            // Back-up previous levels
            int[] prevLevels = Arrays.copyOf(facilityLevels, facilityLevels.length);

            //Log.i(LOG_TAG, "levels " + levels[0] + " " + levels[1] + " " + levels[2] + " " + levels[3]);
            //Log.i(LOG_TAG, "exps " + exps[0] + " " + exps[1] + " " + exps[2] + " " + exps[3]);

            // Level down if exp is negative
            for (int i = 0; i < Facility.values().length; ++i) {
                if (facilityExps[i] < 0) {
                    if (facilityLevels[i] > 0) {
                        facilityLevels[i]--;
                        facilityExps[i] = REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[facilityLevels[i]];

                        // Remove from placement
                        if (facilityLevels[i] == 4 || facilityLevels[i] == 3 || facilityLevels[i] == 0) {
                            facilitySlots.remove(Facility.values()[i]);
                        }
                    } else {
                        facilityExps[i] = 0;
                    }
                }
            }

            // And level up if exp is above the required exp for this level
            for (int i = 0; i < Facility.values().length; ++i) {
                Facility facility = Facility.values()[i];
                if (facilityLevels[i] < MAX_FACILITY_LEVEL &&
                        facilityExps[i] >= REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[facilityLevels[i]]) {
                    if (Arrays.stream(facilityLevels).sum() < MAX_FACILITY_LEVEL) {
                        // In case of level 1, need to check if slot is full
                        if (facilityLevels[i] == 0 || facilityLevels[i] == 3 || facilityLevels[i] == 4) {
                            if (facilitySlots.size() < terrain.availableEconomySlot()) {
                                facilitySlots.add(facility);
                                facilityLevels[i]++;
                                facilityExps[i] = 0;
                            } else {
                                facilityExps[i] = REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[facilityLevels[i]];
                            }
                        } else {
                            facilityLevels[i]++;
                            facilityExps[i] = 0;
                        }
                    } else {
                        facilityExps[i] = REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[facilityLevels[i]];
                    }
                }
            }

            // If a facility level is changed, redraw the tile
            if (!Arrays.equals(prevLevels, facilityLevels)) {
                listener.onTownUpdated(this);
            }

            // Get happiness delta
            int happinessDelta = getFacilityLevel(Facility.DOWNTOWN) * BASE_HAPPINESS_DELTA_FROM_TOWN_LEVEL;
            for (Town neighborTown : neighborTowns) {
                if (neighborTown != null) {
                    if (neighborTown.getFaction() == getFaction()) {
                        happinessDelta += neighborTown.getFacilityLevel(Facility.DOWNTOWN) *
                                BASE_HAPPINESS_DELTA_FROM_NEIGHBOR_TOWN;
                    } else if (neighborTown.getFaction() != Faction.NEUTRAL) {
                        happinessDelta += BASE_HAPPINESS_DELTA_FROM_ENEMY_TOWN;
                    }
                }
            }

            // Update happiness
            happiness = Math.min(Math.max(BASE_HAPPINESS + happinessDelta, 0), 100);

        } else if (faction != Faction.NEUTRAL) {

            // It only deteriorated if it's on enemy's hand
            for (int i = 0; i < Facility.values().length; ++i) {
                // Update exp
                facilityExps[i] = BASE_FACILITY_EXP_DELTA_DETERIORATE;

                // Level down
                if (facilityExps[i] < 0) {
                    if (facilityLevels[i] > 0) {
                        facilityLevels[i]--;
                        facilityExps[i] = REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[facilityLevels[i]];
                    } else {
                        facilityExps[i] = 0;
                    }
                }
            }

            // Update happiness
            happiness = 0;
        }

        facilityUpdateLeft = FACILITY_UPDATE_COUNT;
    }

    /**
     *
     */
    public void createTileSprites() {

        if (baseSprite == null) {
            baseSprite =
                    new Sprite.Builder("Base", "tiles.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(4, 7).smooth(false)
                            .layer(SPRITE_LAYER).visible(true).build();
        }

        if (facilitySprites == null) {
            facilitySprites = new ArrayList<>();
            for (int i = 0; i < 3; ++i) {
                Sprite facilitySprite =
                        new Sprite.Builder("Facility", "tiles_facility_objects.png")
                                .position(new PointF(mapCoord.toGameCoord()))
                                .size(TileSize.clone().multiply(0.5f)).gridSize(5, 5).smooth(false)
                                .layer(SPRITE_LAYER).depth(0.1f).visible(false).build();
                facilitySprites.add(facilitySprite);
            }
        }

        if (factionSprite == null) {
            factionSprite =
                    new Sprite.Builder("TownFaction", "tiles_territory.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(7, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.2f).visible(true).build();
        }

        if (borderSprites == null) {
            borderSprites = new ArrayList<>();
            for (int i = 0; i < 6; ++i) {
                Sprite border =
                        new Sprite.Builder("TownTerritory", "tiles_territory.png")
                                .position(new PointF(mapCoord.toGameCoord()))
                                .size(TileSize).gridSize(7, 5).smooth(false)
                                .layer(SPRITE_LAYER).depth(0.3f).visible(true).build();
                border.setGridIndex(i, faction.ordinal());
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
                    new Sprite.Builder("Glowing", "tiles_glowing.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(1, 1).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.5f).visible(true).build();
            glowingSprite.setGridIndex(0, 0);
        }

        if (selectionSprite == null) {
            selectionSprite =
                    new Sprite.Builder("Selection", "tiles_selection.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(1, 1).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.6f).visible(true).build();
            selectionSprite.setGridIndex(0, 0);
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
        if (Arrays.stream(facilityLevels).sum() > 0) {
            if (terrain == Terrain.FOREST) {
                baseSprite.setGridIndex(0, Terrain.GRASS.ordinal());
            } else if (terrain == Terrain.HILL) {
                baseSprite.setGridIndex(0, Terrain.BADLANDS.ordinal());
            } else {
                baseSprite.setGridIndex(0, terrain.ordinal());
            }
        } else {
            baseSprite.setGridIndex(baseSpriteSelection, terrain.ordinal());
        }
        sprites.add(baseSprite);

        // Set facility sprite
        if (facilitySlots.size() == 0) {
            for (Sprite sprite : facilitySprites) {
                sprite.setVisible(false);
            }
        } else {
            int i;
            for (i = 0; i < facilitySlots.size(); ++i) {
                Sprite sprite = facilitySprites.get(i);

                Facility facility = facilitySlots.get(i);
                int level = facilityLevels[facility.ordinal()];
                sprite.setGridIndex(level - 1, facility.ordinal());

                int placement = (i + facilitySpriteSelection) % 3;
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
                Sprite sprite = facilitySprites.get(i);

                int placement = (i + facilitySpriteSelection) % 3;
                if (placement == 0) {
                    sprite.setPositionOffset(new PointF(0.0f, TileSize.height/4));
                } else if (placement == 1) {
                    sprite.setPositionOffset(new PointF(-TileSize.width/4, -TileSize.height/8));
                } else if (placement == 2) {
                    sprite.setPositionOffset(new PointF(TileSize.width/4, -TileSize.height/8));
                }
                if (terrain == Terrain.FOREST) {
                    sprite.setGridIndex(0, Facility.values().length);
                    sprite.setVisible(true);
                    sprites.add(sprite);
                } else if (terrain == Terrain.HILL) {
                    sprite.setGridIndex(1, Facility.values().length);
                    sprite.setVisible(true);
                    sprites.add(sprite);
                } else {
                    facilitySprites.get(i).setVisible(false);
                }
            }
        }

        if (showTerritories) {
            // Set faction sprite
            factionSprite.setGridIndex(6, faction.ordinal());
            factionSprite.setVisible(true);
            sprites.add(factionSprite);

            // Set border sprite
            int index = 0;
            for (Town neighbor : neighborTowns) {
                Sprite borderSprite = borderSprites.get(index);
                if (neighbor == null || neighbor.getFaction() != faction) {
                    borderSprite.setGridIndex(index, faction.ordinal());
                    borderSprite.setVisible(true);
                    sprites.add(borderSprite);
                } else {
                    borderSprite.setVisible(false);
                    borderSprite.commit();
                }
                index++;
            }
        } else {
            factionSprite.setVisible(false);
            factionSprite.commit();
            for (Sprite border : borderSprites) {
                border.setVisible(false);
                border.commit();
            }
        }

        // If occupation is ongoing, show progress
        if (occupationStep > 0) {
            occupationSprite.setVisible(true);
            occupationSprite.setGridIndex(occupationStep - 1, occupyingFaction.ordinal());
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

        // Show selection sprites
        if (focused) {
            selectionSprite.setVisible(true);
            sprites.add(selectionSprite);
        } else {
            selectionSprite.setVisible(false);
            selectionSprite.commit();
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
        if (factionSprite != null) {
            factionSprite.close();
            factionSprite = null;
        }
        if (facilitySprites != null ) {
            for (Sprite facilitySprite: facilitySprites) {
                facilitySprite.close();
            }
            facilitySprites = null;
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
        listener.onTownUpdated(this);
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
    public Terrain getTerrain() {

        return terrain;
    }

    /**
     *
     * @param facility
     * @return
     */
    public int getFacilityLevel(Facility facility) {

        return facilityLevels[facility.ordinal()];
    }

    /**
     *
     * @return
     */
    public Faction getFaction() {

        return faction;
    }

    /**
     *
     * @param facility
     * @return
     */
    public FacilityDevelopment getFacilityDevelopment(Facility facility) {

        return facilityDevelopment[facility.ordinal()];
    }

    /**
     *
     * @param facility
     * @param development
     */
    public void setFacilityDevelopment(Facility facility, FacilityDevelopment development) {

        facilityDevelopment[facility.ordinal()] = development;
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
        int totalNeighborTownLevel = 0;
        for (Town neighborTown : neighborTowns) {
            if (neighborTown != null && neighborTown.getFaction() == getFaction()) {
                totalNeighborTownLevel += neighborTown.getFacilityLevel(Facility.DOWNTOWN);
            }
        }

        return (int) (facilityLevels[Facility.MARKET.ordinal()] * BASE_TAX_DELTA_FROM_MARKET_LEVEL *
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

        if (terrain == Terrain.HEADQUARTER_GRASS) {
            return HEADQUARTER_POPULATION;
        }

        return facilityLevels[Facility.FARM.ordinal()] * BASE_POPULATION_DELTA_FROM_FARM_LEVEL +
               facilityLevels[Facility.DOWNTOWN.ordinal()] * BASE_POPULATION_DELTA_FROM_NEIGHBOR_DOWNTOWN_LEVEL;
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

        return occupyingFaction != Faction.NEUTRAL && getBattle() == null;
    }

    /**
     *
     * @param towns
     */
    public void setNeighborTowns(ArrayList<Town> towns) {

        neighborTowns = towns;
    }

    private final static int SPRITE_LAYER = 0;
    private final static int FACILITY_UPDATE_COUNT = 30;
    private final static int MAX_FACILITY_LEVEL = 5;
    private final static int[] REQUIRED_FACILITY_EXP_FOR_LEVEL_UP =
            new int[] { 100, 200, 300, 400, 500};
    private final static int BASE_FACILITY_EXP_DELTA_PROSPER = 10;
    private final static int BASE_FACILITY_EXP_DELTA_STALL = 0;
    private final static int BASE_FACILITY_EXP_DELTA_DETERIORATE = -5;
    private final static int BASE_FACILITY_EXP_DELTA_FROM_NEIGHBOR_DOWNTOWN = 5;
    private final static int BASE_FACILITY_EXP_DELTA_FROM_NEIGHBOR_ENEMY = 5;
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

    private Event listener;
    private OffsetCoord mapCoord;
    private Terrain terrain;
    private Faction faction;
    private boolean focused = false;
    private ArrayList<Town> neighborTowns = null;

    // Occupation
    private Faction occupyingFaction = Faction.NEUTRAL;
    private int occupationStep = 0;
    private int occupationUpdateLeft = OCCUPATION_STEP_UPDATE_TIME;

    // Facility
    private int[] facilityLevels;
    private int[] facilityExps;
    private FacilityDevelopment[] facilityDevelopment;
    private ArrayList<Facility> facilitySlots = new ArrayList<>();
    private int happiness;
    private Specialities specialties;
    private int facilityUpdateLeft = FACILITY_UPDATE_COUNT;

    // Squad
    private ArrayList<Squad> squads = new ArrayList<>();
    private Battle battle;

    // Sprite
    private Sprite baseSprite = null;
    private Sprite factionSprite = null;
    private ArrayList<Sprite> facilitySprites = null;
    private ArrayList<Sprite> borderSprites = null;
    private Sprite occupationSprite = null;
    private Sprite glowingSprite = null;
    private Sprite selectionSprite = null;
    private int baseSpriteSelection = (int)(Math.random()*4);
    private int facilitySpriteSelection = (int)(Math.random()*3);
}
