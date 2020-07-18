package com.lifejourney.townhall;

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

        void onTownUpdated(Town town);

        void onTownOccupied(Town town, Faction oldFaction);
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
                    return "본부";
                case UNKNOWN:
                default:
                    return "모름";
            }
        }

        int facilitySlots() {
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

        public boolean canDevelop() {
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

        public int bonusDevelopmentDelta(Facility facility) {
            switch (this) {
                case GRASS:
                    if (facility == Facility.DOWNTOWN || facility == Facility.FARM) {
                        return 1;
                    } else {
                        return 0;
                    }
                case BADLANDS:
                    if (facility == Facility.DOWNTOWN || facility == Facility.MARKET) {
                        return 1;
                    } else {
                        return 0;
                    }
                case FOREST:
                    if (facility == Facility.FARM || facility == Facility.FORTRESS) {
                        return 1;
                    } else if (facility == Facility.DOWNTOWN) {
                        return -1;
                    } else {
                        return 0;
                    }
                case HILL:
                    if (facility == Facility.FORTRESS) {
                        return 2;
                    } else if (facility == Facility.DOWNTOWN || facility == Facility.FARM) {
                        return -1;
                    } else {
                        return 0;
                    }
                default:
                    return 0;
            }
        }

        public int bonusHappinessDelta() {
            switch (this) {
                case GRASS:
                    return 1;
                case BADLANDS:
                    return -1;
                case FOREST:
                case HILL:
                default:
                    return 0;
            }
        }

        public int bonusDefenseDelta() {
            switch (this) {
                case GRASS:
                case BADLANDS:
                    return 0;
                case FOREST:
                    return 1;
                case HILL:
                    return 2;
                default:
                    return 0;
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

    enum Facility {
        DOWNTOWN,
        FARM,
        MARKET,
        FORTRESS;
    }

    enum FacilityDevelopment {
        PROSPER,
        STALL,
        DETERIORATE;

        public int baseDevelopmentDelta() {
            switch (this) {
                case PROSPER:
                case STALL:
                    return 1;
                case DETERIORATE:
                    return -5;
                default:
                    return 0;
            }
        }
    }

    enum DeltaAttribute {
        FARM_DEVELOPMENT,
        MARKET_DEVELOPMENT,
        DOWNTOWN_DEVELOPMENT,
        FORTRESS_DEVELOPMENT,
        GOLD,
        POPULATION,
        DEFENSE,
        HAPPINESS
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
        if (this.terrain.canDevelop()) {
            Arrays.fill(this.facilityDevelopment, FacilityDevelopment.PROSPER);
        } else {
            Arrays.fill(this.facilityDevelopment, FacilityDevelopment.DETERIORATE);
        }
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

        assert squads.size() <= 1;

        Squad squad = (squads.isEmpty())? null : squads.get(0);
        if (squad != null &&
                squad.getFaction() != getFaction() &&
                !squad.isMoving() &&
                !squad.isEliminated()) {
            // If an enemy squad is exist, occupy town
            updateOccupation(squad.getFaction());
        } else {
            cancelOccupation();

            // Or update town
            if (townUpdateLeft-- == 0) {
                updateFacility();
                updateDelta();
                townUpdateLeft = TOWN_UPDATE_COUNT;
            }
        }
    }

    /**
     *
     */
    private void updateOccupation(Faction occupyingFaction) {

        if (this.occupyingFaction != occupyingFaction) {
            // If someone new try to occupy this, update information
            this.occupyingFaction = occupyingFaction;
            this.occupationStep = 0;
            this.occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;
            listener.onTownUpdated(this);
        } else if (--this.occupationUpdateForThisStepLeft == 0) {
            if (++this.occupationStep > OCCUPATION_TOTAL_STEP) {
                // If occupation is done, change the owner faction of town
                Faction prevFaction = this.faction;
                this.faction = occupyingFaction;
                this.occupationStep = 0;
                this.occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;
                listener.onTownOccupied(this, prevFaction);
            } else {
                // Or just update the occupation status
                this.occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;
            }
            listener.onTownUpdated(this);
        }
    }

    /*
     *
     */
    private void cancelOccupation() {

        // Cancel occupation process
        if (this.occupationStep > 0) {
            listener.onTownUpdated(this);
        }
        this.occupyingFaction = Faction.NEUTRAL;
        this.occupationStep = 0;
        this.occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;
    }

    /**
     *
     */
    private void updateDelta() {

        if (faction == Faction.VILLAGER) {
            // Calculate delta from this town
            for (int i = 0; i < Facility.values().length; ++i) {
                deltas[i] = facilityDevelopment[i].baseDevelopmentDelta() +
                        terrain.bonusDevelopmentDelta(Facility.values()[i]);
            }
            deltas[DeltaAttribute.HAPPINESS.ordinal()] = terrain.bonusHappinessDelta();
            deltas[DeltaAttribute.DEFENSE.ordinal()] = terrain.bonusDefenseDelta();
            deltas[DeltaAttribute.GOLD.ordinal()] = getFacilityLevel(Facility.MARKET);
            deltas[DeltaAttribute.POPULATION.ordinal()] = getFacilityLevel(Facility.FARM);

            // Calculate delta from neighbors
            for (Town neighbor : neighbors) {
                if (neighbor != null) {
                    if (neighbor.getFaction() == getFaction()) {
                        int downtownLvl = neighbor.getFacilityLevel(Facility.DOWNTOWN);
                        int fortressLvl = neighbor.getFacilityLevel(Facility.FORTRESS);
                        for (int i = 0; i < Facility.values().length; ++i) {
                            if (facilityDevelopment[i] != FacilityDevelopment.DETERIORATE) {
                                deltas[i] += downtownLvl;
                            }
                            deltas[i] -= fortressLvl;
                        }
                        deltas[DeltaAttribute.HAPPINESS.ordinal()] -= fortressLvl;
                        deltas[DeltaAttribute.GOLD.ordinal()] +=
                                neighbor.getFacilityLevel(Facility.DOWNTOWN);
                        deltas[DeltaAttribute.POPULATION.ordinal()] +=
                                neighbor.getFacilityLevel(Facility.DOWNTOWN);
                        deltas[DeltaAttribute.DEFENSE.ordinal()] +=
                                neighbor.getFacilityLevel(Facility.FORTRESS);
                    } else if (neighbor.getFaction() != Faction.NEUTRAL) {
                        for (int i = 0; i < Facility.values().length; ++i) {
                            deltas[i] -= 1;
                        }
                        deltas[DeltaAttribute.HAPPINESS.ordinal()] -= 5;
                    }
                }
            }
        } else if (faction != Faction.NEUTRAL) {
            // Facility is deteriorated if it's on enemy's hand
            for (int i = 0; i < Facility.values().length; ++i) {
                deltas[i] = FacilityDevelopment.DETERIORATE.baseDevelopmentDelta();
            }
            deltas[DeltaAttribute.HAPPINESS.ordinal()] = 0;
            deltas[DeltaAttribute.DEFENSE.ordinal()] = 0;
    }
}

    /**
     *
     */
    private void updateFacility() {

        // Get base exp delta for facilities
        int[] facilityExpDelta = new int[Facility.values().length];
        for (int i = 0; i < Facility.values().length; ++i) {
            facilityExpDelta[i] = FACILITY_EXP_STEP * deltas[i];
        }

        // Update exp
        for (int i = 0; i < Facility.values().length; ++i) {
            facilityExps[i] += facilityExpDelta[i];
        }

        //Log.i(LOG_TAG, "levels " + levels[0] + " " + levels[1] + " " + levels[2] + " " + levels[3]);
        //Log.i(LOG_TAG, "exps " + exps[0] + " " + exps[1] + " " + exps[2] + " " + exps[3]);

        // Back-up previous levels
        int[] prevLevels = Arrays.copyOf(facilityLevels, facilityLevels.length);

        // Level down if exp is negative
        for (int i = 0; i < Facility.values().length; ++i) {
            if (facilityExps[i] < 0) {
                if (facilityLevels[i] > 0) {
                    facilityLevels[i]--;
                    facilityExps[i] = REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[facilityLevels[i]]-1;
                    if (facilityLevels[i] == 4 || facilityLevels[i] == 3 || facilityLevels[i] == 0) {
                        // Remove facility from placement if facility level is certain level
                        facilitySlots.remove(Facility.values()[i]);
                        Log.e(LOG_TAG, "facilitySlots: " + facilitySlots.size());
                    }
                } else {
                    facilityExps[i] = 0;
                }
            }
        }

        // Level up if exp is above the required exp for this level
        for (int i = 0; i < Facility.values().length; ++i) {
            if (facilityDevelopment[i] == FacilityDevelopment.PROSPER &&
                    facilityLevels[i] < MAX_FACILITY_LEVEL &&
                    facilityExps[i] >= REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[facilityLevels[i]]) {
                // Check if town have max facility already
                if (Arrays.stream(facilityLevels).sum() < MAX_FACILITY_LEVEL) {
                    // Check if facility need additional slots
                    if (facilityLevels[i] == 0 || facilityLevels[i] == 3 || facilityLevels[i] == 4) {
                        if (facilitySlots.size() < terrain.facilitySlots()) {
                            facilitySlots.add(Facility.values()[i]);
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

        // Update happiness
        int happinessDelta = HAPPINESS_STEP * deltas[DeltaAttribute.HAPPINESS.ordinal()];
        happiness = Math.min(Math.max(BASE_HAPPINESS + happinessDelta, 0), 100);

        // If any facility level is changed, redraw the tile
        if (!Arrays.equals(prevLevels, facilityLevels)) {
            listener.onTownUpdated(this);
        }
    }

    /**
     *
     */
    private ArrayList<Sprite> getTileSprites() {

        ArrayList<Sprite> sprites = new ArrayList<>();

        if (baseSprite == null) {
            baseSprite =
                    new Sprite.Builder("Base", "tiles.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(4, 7).smooth(false)
                            .layer(SPRITE_LAYER).visible(true).build();
        }
        sprites.add(baseSprite);

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
        sprites.addAll(facilitySprites);

        if (factionSprite == null) {
            factionSprite =
                    new Sprite.Builder("TownFaction", "tiles_territory.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(7, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.2f).visible(true).build();
        }
        sprites.add(factionSprite);

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
        sprites.addAll(borderSprites);

        if (occupationSprite == null) {
            occupationSprite =
                    new Sprite.Builder("TerritoryOccupation", "tiles_occupation.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(6, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.4f).visible(true).build();
        }
        sprites.add(occupationSprite);

        if (glowingSprite == null) {
            glowingSprite =
                    new Sprite.Builder("Glowing", "tiles_glowing.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(1, 1).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.5f).visible(true).build();
            glowingSprite.setGridIndex(0, 0);
        }
        sprites.add(glowingSprite);

        if (selectionSprite == null) {
            selectionSprite =
                    new Sprite.Builder("Selection", "tiles_selection.png")
                            .position(new PointF(mapCoord.toGameCoord()))
                            .size(TileSize).gridSize(1, 1).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.6f).visible(true).build();
            selectionSprite.setGridIndex(0, 0);
        }
        sprites.add(selectionSprite);

        return sprites;
    }

    /**
     *
     * @param glowing
     * @param showTerritories
     * @return
     */
    public ArrayList<Sprite> getTileSprites(boolean glowing, boolean showTerritories) {

        // Get sprites list
        ArrayList<Sprite> sprites = getTileSprites();

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

        Log.e(LOG_TAG, "facilitySlot " + facilitySlots.size());

        // Set facility sprite
        if (facilitySlots.size() == 0) {
            for (Sprite sprite : facilitySprites) {
                sprite.setVisible(false);
            }
        } else {
            int i = 0;
            for (; i < facilitySlots.size(); ++i) {
                Sprite sprite = facilitySprites.get(i);

                Facility facility = facilitySlots.get(i);
                int level = getFacilityLevel(facility);
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
                } else if (terrain == Terrain.HILL) {
                    sprite.setGridIndex(1, Facility.values().length);
                    sprite.setVisible(true);
                } else {
                    sprite.setVisible(false);
                }
            }
        }

        if (showTerritories) {
            // Set faction sprite
            factionSprite.setGridIndex(6, faction.ordinal());
            factionSprite.setVisible(true);

            // Set border sprite
            int index = 0;
            for (Town neighbor : neighbors) {
                Sprite borderSprite = borderSprites.get(index);
                if (neighbor == null || neighbor.getFaction() != faction) {
                    borderSprite.setGridIndex(index, faction.ordinal());
                    borderSprite.setVisible(true);
                } else {
                    borderSprite.setVisible(false);
                }
                index++;
            }
        } else {
            factionSprite.setVisible(false);
            for (Sprite border : borderSprites) {
                border.setVisible(false);
            }
        }

        // If occupation is ongoing, show progress
        if (occupationStep > 0) {
            occupationSprite.setVisible(true);
            occupationSprite.setGridIndex(occupationStep - 1, occupyingFaction.ordinal());
        } else {
            occupationSprite.setVisible(false);
        }

        // Show glowing sprites
        if (glowing) {
            glowingSprite.setVisible(true);
        } else {
            glowingSprite.setVisible(false);
        }

        // Show selection sprites
        if (focused) {
            selectionSprite.setVisible(true);
        } else {
            selectionSprite.setVisible(false);
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
        } else {
            return (int) (GOLD_STEP * deltas[DeltaAttribute.GOLD.ordinal()] * (100.0f / happiness));
        }
    }

    /**
     *
     * @return
     */
    public int collectPopulation() {

        if (getBattle() != null) {
            return 0;
        } else {
            return (POPULATION_STEP * deltas[DeltaAttribute.POPULATION.ordinal()]);
        }
    }

    /**
     *
     * @return
     */
    public int getHappiness() {

        return happiness;
    }

    /**
     *
     * @return
     */
    public boolean isOccupying() {

        return (occupyingFaction != Faction.NEUTRAL && getBattle() == null &&
                !squads.get(0).isEliminated());
    }

    /**
     *
     * @param neighbors
     */
    public void setNeighbors(ArrayList<Town> neighbors) {

        this.neighbors = neighbors;
    }

    private final static int SPRITE_LAYER = 0;
    private final static int TOWN_UPDATE_COUNT = 30;
    private final static int MAX_FACILITY_LEVEL = 5;
    private final static int[] REQUIRED_FACILITY_EXP_FOR_LEVEL_UP =
            new int[] { 100, 200, 300, 400, 500};
    private final static int FACILITY_EXP_STEP = 10;
    private final static int GOLD_STEP = 10;
    private final static int POPULATION_STEP = 1;
    private final static int HAPPINESS_STEP = 5;
    private final static int BASE_HAPPINESS = 50;
    private final static int OCCUPATION_TOTAL_STEP = 5;
    private final static int OCCUPATION_UPDATE_TIME_FOR_EACH_STEP = 30;

    private Event listener;
    private OffsetCoord mapCoord;
    private Terrain terrain;
    private Faction faction;
    private boolean focused = false;
    private ArrayList<Town> neighbors = null;

    // Occupation
    private Faction occupyingFaction = Faction.NEUTRAL;
    private int occupationStep = 0;
    private int occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;

    // Deltas
    private int[] deltas = new int[DeltaAttribute.values().length];

    // Facility
    private int[] facilityLevels;
    private int[] facilityExps;
    private FacilityDevelopment[] facilityDevelopment;
    private ArrayList<Facility> facilitySlots = new ArrayList<>();
    private int townUpdateLeft = (int)(Math.random()* TOWN_UPDATE_COUNT);
    private int happiness;

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
