package com.lifejourney.townhall;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;
import java.util.Arrays;

public class Territory {

    private final static String LOG_TAG = "Territory";

    public interface Event {
        void onTerritoryUpdated(Territory territory);
        void onTerritoryOccupied(Territory territory, Tribe.Faction oldFaction);
    }

    enum Terrain {
        GRASS(
                Engine2D.GetInstance().getString(R.string.grass),
                3,
                 new boolean[] { true, true, true, true, true },
                 new int[] {1, 1, 0, -1},
                0,
                0,
                1,
                0,
                0,
                null,
                0
        ),
        BADLANDS(
                Engine2D.GetInstance().getString(R.string.badlands),
                3,
                new boolean[] { true, true, true, true, true },
                new int[] {1, 0, 1, -1},
                0,
                0,
                -1,
                0,
                0,
                null,
                0
        ),
        FOREST(
                Engine2D.GetInstance().getString(R.string.forest),
                2,
                new boolean[] { true, true, true, true, true },
                new int[] {-1, 1, -1, 1},
                0,
                0,
                0,
                0,
                1,
                null,
                0
        ),
        HILL(
                Engine2D.GetInstance().getString(R.string.hill),
                2,
                new boolean[] { true, true, true, true, true },
                new int[] {-1, -1, 1, 2},
                0,
                0,
                0,
                0,
                2,
                null,
                0
        ),
        MOUNTAIN(
                Engine2D.GetInstance().getString(R.string.mountain),
                0,
                new boolean[] { false, false, false, false, false },
                new int[] {0, 0, 0, 0},
                0,
                0,
                0,
                0,
                3,
                null,
                0
        ),
        RIVER(
                Engine2D.GetInstance().getString(R.string.river),
                0,
                new boolean[] { false, false, false, true, false },
                new int[] {0, 0, 0, 0},
                0,
                0,
                0,
                0,
                3,
                null,
                0
        ),
        HEADQUARTER(
                Engine2D.GetInstance().getString(R.string.headquarter),
                0,
                new boolean[] { true, true, true, true, true },
                new int[] {0, 0, 0, 0},
                5,
                5,
                5,
                5,
                5,
                null,
                0
        ),
        SHRINE_WIND(
                Engine2D.GetInstance().getString(R.string.wind_shrine),
                0,
                new boolean[] { true, true, true, true, true },
                new int[] {0, 0, 0, 0},
                0,
                0,
                10,
                0,
                3,
                Tribe.ShrineBonus.WIND,
                -2
        ),
        SHRINE_HEAL(
                Engine2D.GetInstance().getString(R.string.healing_shrine),
                0,
                new boolean[] { true, true, true, true, true },
                new int[] {0, 0, 0, 0},
                0,
                0,
                10,
                0,
                3,
                Tribe.ShrineBonus.HEAL,
                2
        ),
        SHRINE_LOVE(
                Engine2D.GetInstance().getString(R.string.love_shrine),
                0,
                new boolean[] { true, true, true, true, true },
                new int[] {0, 0, 0, 0},
                0,
                0,
                10,
                0,
                3,
                Tribe.ShrineBonus.LOVE,
                2
        ),
        SHRINE_PROSPER(
                Engine2D.GetInstance().getString(R.string.prosper_shrine),
                0,
                new boolean[] { true, true, true, true, true },
                new int[] {0, 0, 0, 0},
                0,
                0,
                10,
                0,
                3,
                Tribe.ShrineBonus.PROSPERITY,
                2
        ),
        UNKNOWN(
                Engine2D.GetInstance().getString(R.string.unknown),
                0,
                new boolean[] { false, false, false, false, false },
                new int[] {0, 0, 0, 0},
                0,
                0,
                0,
                0,
                0,
                null,
                0
        );

        private String word;
        private int facilitySlots;
        private boolean[] movable;
        private boolean canDevelop;
        private int[] developmentDelta;
        private int goldDelta;
        private int populationDelta;
        private int happinessDelta;
        private int offenseDelta;
        private int defenseDelta;
        private Tribe.ShrineBonus shrineFactor;
        private int shrineBonus;

        Terrain(String word, int facilitySlots, boolean[] movable, int[] developmentDeltas,
                int goldDelta, int populationDelta, int happinessDelta, int offenseDelta,
                int defenseDelta, Tribe.ShrineBonus shrineFactor, int shrineBonus) {
            this.word = word;
            this.facilitySlots = facilitySlots;
            this.movable = movable;
            this.canDevelop = (facilitySlots > 0);
            this.developmentDelta = developmentDeltas;
            this.goldDelta = goldDelta;
            this.populationDelta = populationDelta;
            this.happinessDelta = happinessDelta;
            this.offenseDelta = offenseDelta;
            this.defenseDelta = defenseDelta;
            this.shrineFactor = shrineFactor;
            this.shrineBonus = shrineBonus;
        }

        String word() {
            return word;
        }
        int facilitySlots() {
            return facilitySlots;
        }
        boolean isMovable(Tribe.Faction faction) {
            return movable[faction.ordinal()];
        }
        public boolean canDevelop() {
            return canDevelop;
        }
        public int developmentDelta(Facility facility) {
            return developmentDelta[facility.ordinal()];
        }
        public int goldDelta() {
            return goldDelta;
        }
        public int populationDelta() {
            return populationDelta;
        }
        public int happinessDelta() {
            return happinessDelta;
        }
        public int offenseDelta() {
            return offenseDelta;
        }
        public int defenseDelta() {
            return defenseDelta;
        }
        public Tribe.ShrineBonus bonusFactor() {
            return shrineFactor;
        }
        public int bonusValue() {
            return shrineBonus;
        }
    }

    enum Facility {
        DOWNTOWN,
        FARM,
        MARKET,
        FORTRESS;
    }

    enum DevelopmentPolicy {
        PROSPER(1),
        STALL(1),
        DETERIORATE(-5);

        private int developmentDelta;

        DevelopmentPolicy(int developmentDelta) {
            this.developmentDelta = developmentDelta;
        }

        public int developmentDelta() {
            return developmentDelta;
        }
    }

    enum DeltaAttribute {
        DEVELOPMENT_DOWNTOWN,
        DEVELOPMENT_FARM,
        DEVELOPMENT_MARKET,
        DEVELOPMENT_FORTRESS,
        GOLD,
        POPULATION,
        HAPPINESS,
        DEFENSIVE,
        OFFENSIVE
    }

    enum FogState {
        CLEAR,
        MIST,
        CLOUDY
    }

    private static SizeF TileSize;

    /**
     *
     * @param tileSize
     */
    public static void SetTileSize(SizeF tileSize) {
        TileSize = tileSize;
    }

    public Territory(Event eventHandler, OffsetCoord mapPosition, Terrain terrain, Tribe.Faction faction) {
        this.eventHandler = eventHandler;
        this.mapPosition = mapPosition;
        this.terrain = terrain;
        this.faction = faction;
        this.facilityLevels = new int[Facility.values().length];
        Arrays.fill(this.facilityLevels, 0);
        this.facilityExps = new int[Facility.values().length];
        Arrays.fill(this.facilityExps, 0);
        this.enemyFacilityIndex = new int[3];
        int enemyFacilityCount = 3;
        if (terrain == Terrain.HEADQUARTER ||
            terrain == Terrain.RIVER ||
            terrain == Terrain.MOUNTAIN) {
            enemyFacilityCount = 0;
        } else if (terrain == Terrain.FOREST ||
            terrain == Terrain.HILL) {
            enemyFacilityCount = 2;
        }
        for (int i = 0; i < enemyFacilityCount; ++i) {
            this.enemyFacilityIndex[i] = (terrain == Terrain.HEADQUARTER)? 0 : (int) (Math.random() * 6);
        }
        this.developmentPolicy = new DevelopmentPolicy[Facility.values().length];
        if (this.terrain.canDevelop()) {
            Arrays.fill(this.developmentPolicy, DevelopmentPolicy.PROSPER);
        } else {
            Arrays.fill(this.developmentPolicy, DevelopmentPolicy.DETERIORATE);
        }
        this.happiness = 50;
        this.fogState = (this.faction == Tribe.Faction.VILLAGER)? FogState.CLEAR : FogState.CLOUDY;
    }

    /**
     *
     */
    public void update() {
        // Update territory only when it's at peace
        if (getBattle() != null) {
            return;
        }

        assert squads.size() <= 1;

        Squad squad = (squads.isEmpty())? null : squads.get(0);
        if (squad != null && squad.getFaction() != getFaction() &&
                !squad.isMoving() && !squad.isEliminated()) {
            // If an enemy squad is exist, occupy territory
            updateOccupation(squad.getFaction());
        } else {
            cancelOccupation();

            // Or update territory
            if (firstUpdate || territoryUpdateLeft-- == 0) {
                updateDelta();
                updateFacility();
                territoryUpdateLeft = TERRITORY_UPDATE_COUNT;
                firstUpdate = false;
            }
        }
    }

    /**
     *
     */
    private void updateOccupation(Tribe.Faction occupyingFaction) {
        if (this.occupyingFaction != occupyingFaction) {
            // If someone new try to occupy this, update information
            this.occupyingFaction = occupyingFaction;
            this.occupationStep = 0;
            this.occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;
            eventHandler.onTerritoryUpdated(this);
        } else if (--this.occupationUpdateForThisStepLeft == 0) {
            if (++this.occupationStep > OCCUPATION_TOTAL_STEP) {
                // If occupation is done, change the owner faction of territory
                this.occupationStep = 0;
                this.occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;
                setFaction(occupyingFaction);
            } else {
                // Or just update the occupation status
                this.occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;
            }
            eventHandler.onTerritoryUpdated(this);
        }
    }

    /**
     *
     * @param faction
     */
    public void setFaction(Tribe.Faction faction) {
        Tribe.Faction prevFaction = this.faction;
        this.faction = faction;
        eventHandler.onTerritoryOccupied(this, prevFaction);
    }

    /*
     *
     */
    private void cancelOccupation() {
        // Cancel occupation process
        if (this.occupationStep > 0) {
            eventHandler.onTerritoryUpdated(this);
        }
        this.occupyingFaction = Tribe.Faction.NEUTRAL;
        this.occupationStep = 0;
        this.occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;
    }

    /**
     *
     */
    private void updateDelta() {
        if (faction == Tribe.Faction.VILLAGER) {
            // Calculate delta from this territory
            for (int i = 0; i < Facility.values().length; ++i) {
                deltas[i] = developmentPolicy[i].developmentDelta() +
                        ((developmentPolicy[i] != DevelopmentPolicy.DETERIORATE) ?
                        terrain.developmentDelta(Facility.values()[i]) : 0);
            }
            if (getFacilityLevel(Facility.MARKET) > 0 || terrain == Terrain.HEADQUARTER) {
                deltas[DeltaAttribute.GOLD.ordinal()] = getFacilityLevel(Facility.MARKET) * 2 +
                        terrain.goldDelta();
            }
            if (getFacilityLevel(Facility.FARM) > 0 || terrain == Terrain.HEADQUARTER) {
                deltas[DeltaAttribute.POPULATION.ordinal()] = getFacilityLevel(Facility.FARM) * 2 +
                        terrain.populationDelta();
            }
            deltas[DeltaAttribute.HAPPINESS.ordinal()] = terrain.happinessDelta();
            deltas[DeltaAttribute.OFFENSIVE.ordinal()] = terrain.offenseDelta();
            deltas[DeltaAttribute.DEFENSIVE.ordinal()] = getFacilityLevel(Facility.FORTRESS) * 2 +
                    terrain.defenseDelta();

            // Calculate delta from neighbors
            int maxDowntownLvl = 0;
            int maxFortressLvl = 0;
            for (Territory neighbor : neighbors) {
                if (neighbor != null) {
                    if (neighbor.getFaction() == getFaction()) {
                        int downtownLvl = neighbor.getFacilityLevel(Facility.DOWNTOWN);
                        int fortressLvl = neighbor.getFacilityLevel(Facility.FORTRESS);
                        maxDowntownLvl = Math.max(maxDowntownLvl, downtownLvl);
                        maxFortressLvl = Math.max(maxFortressLvl, fortressLvl);
                    } else if (neighbor.getFaction() != Tribe.Faction.NEUTRAL) {
                        for (int i = 0; i < Facility.values().length; ++i) {
                            deltas[i] -= 1;
                        }
                        deltas[DeltaAttribute.HAPPINESS.ordinal()] -= 5;
                    }
                }
            }
            for (int i = 0; i < Facility.values().length; ++i) {
                if (developmentPolicy[i] != DevelopmentPolicy.DETERIORATE) {
                    deltas[i] += maxDowntownLvl;
                }
                /*
                if (deltas[i] > 0) {
                    deltas[i] = Math.max(deltas[i] - maxFortressLvl, 0);
                }
                 */
            }
            deltas[DeltaAttribute.HAPPINESS.ordinal()] += maxDowntownLvl;
            deltas[DeltaAttribute.HAPPINESS.ordinal()] -= maxFortressLvl;
            if (getFacilityLevel(Facility.MARKET) > 0 || terrain == Terrain.HEADQUARTER) {
                deltas[DeltaAttribute.GOLD.ordinal()] += maxDowntownLvl;
            }
            if (getFacilityLevel(Facility.FARM) > 0 || terrain == Terrain.HEADQUARTER) {
                deltas[DeltaAttribute.POPULATION.ordinal()] += maxDowntownLvl;
            }
            deltas[DeltaAttribute.DEFENSIVE.ordinal()] += maxFortressLvl;

            // Calculate delta from squads
            for (Squad squad: squads) {
                if (squad.getFaction() == faction) {
                    int[] developmentDeltasFromSquad = squad.collectDevelopmentBonus();
                    if (developmentPolicy[DeltaAttribute.DEVELOPMENT_DOWNTOWN.ordinal()] != DevelopmentPolicy.DETERIORATE) {
                        deltas[DeltaAttribute.DEVELOPMENT_DOWNTOWN.ordinal()] +=
                                developmentDeltasFromSquad[DeltaAttribute.DEVELOPMENT_DOWNTOWN.ordinal()];
                    }
                    if (developmentPolicy[DeltaAttribute.DEVELOPMENT_FARM.ordinal()] != DevelopmentPolicy.DETERIORATE) {
                        deltas[DeltaAttribute.DEVELOPMENT_FARM.ordinal()] +=
                                developmentDeltasFromSquad[DeltaAttribute.DEVELOPMENT_FARM.ordinal()];
                    }
                    if (developmentPolicy[DeltaAttribute.DEVELOPMENT_MARKET.ordinal()] != DevelopmentPolicy.DETERIORATE) {
                        deltas[DeltaAttribute.DEVELOPMENT_MARKET.ordinal()] +=
                                developmentDeltasFromSquad[DeltaAttribute.DEVELOPMENT_MARKET.ordinal()];
                    }
                    if (developmentPolicy[DeltaAttribute.DEVELOPMENT_FORTRESS.ordinal()] != DevelopmentPolicy.DETERIORATE) {
                        deltas[DeltaAttribute.DEVELOPMENT_FORTRESS.ordinal()] +=
                                developmentDeltasFromSquad[DeltaAttribute.DEVELOPMENT_FORTRESS.ordinal()];
                    }
                    if (getFacilityLevel(Facility.MARKET) > 0 || terrain == Terrain.HEADQUARTER) {
                        deltas[DeltaAttribute.GOLD.ordinal()] += squad.collectGoldBonus();
                    }
                    deltas[DeltaAttribute.HAPPINESS.ordinal()] += squad.collectHappinessBonus();
                    deltas[DeltaAttribute.DEFENSIVE.ordinal()] += squad.collectDefensiveBonus();
                    break;
                }
            }
        } else {
            if (faction != Tribe.Faction.NEUTRAL) {
                // Facility is deteriorated if it's on enemy's hand
                for (int i = 0; i < Facility.values().length; ++i) {
                    deltas[i] = DevelopmentPolicy.DETERIORATE.developmentDelta();
                }
            }
            deltas[DeltaAttribute.HAPPINESS.ordinal()] = 0;
            deltas[DeltaAttribute.OFFENSIVE.ordinal()] = 0;
            deltas[DeltaAttribute.DEFENSIVE.ordinal()] = terrain.defenseDelta();
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
                setFacilityLevel(Facility.values()[i],
                        Math.max(getFacilityLevel(Facility.values()[i]) - 1, 0));
            }
        }

        // Level up if exp is above the required exp for this level
        for (int i = 0; i < Facility.values().length; ++i) {
            if (developmentPolicy[i] == DevelopmentPolicy.PROSPER &&
                    facilityLevels[i] < MAX_FACILITY_LEVEL &&
                    facilityExps[i] >= REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[facilityLevels[i]]) {
                setFacilityLevel(Facility.values()[i],
                        Math.min(getFacilityLevel(Facility.values()[i]) + 1, 5));
            }
        }

        // Update happiness
        int happinessDelta = HAPPINESS_STEP * deltas[DeltaAttribute.HAPPINESS.ordinal()];
        happiness = Math.min(Math.max(BASE_HAPPINESS + happinessDelta, 0), 100);

        // If any facility level is changed, redraw the tile
        if (!Arrays.equals(prevLevels, facilityLevels)) {
            eventHandler.onTerritoryUpdated(this);
        }
    }

    /**
     *
     */
    private void createTileSprites() {

        if (baseSprite == null) {
            baseSprite = new Sprite.Builder("Base", "tiles.png")
                            .position(new PointF(mapPosition.toGameCoord()))
                            .size(TileSize).gridSize(4, Terrain.values().length).smooth(false)
                            .layer(SPRITE_LAYER).visible(true).build();
        }

        if (factionSprite == null) {
            factionSprite = new Sprite.Builder("TownFaction", "tiles_territory.png")
                            .position(new PointF(mapPosition.toGameCoord()))
                            .size(TileSize).gridSize(7, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.1f).visible(true).build();
        }

        if (borderSprites == null) {
            borderSprites = new ArrayList<>();
            for (int i = 0; i < 6; ++i) {
                Sprite border = new Sprite.Builder("TownTerritory", "tiles_territory.png")
                                .position(new PointF(mapPosition.toGameCoord()))
                                .size(TileSize).gridSize(7, 5).smooth(false)
                                .layer(SPRITE_LAYER).depth(0.2f).visible(true).build();
                border.setGridIndex(i, faction.ordinal());
                borderSprites.add(border);
            }
        }

        if (occupationSprite == null) {
            occupationSprite = new Sprite.Builder("TerritoryOccupation", "tiles_occupation.png")
                            .position(new PointF(mapPosition.toGameCoord()))
                            .size(TileSize).gridSize(6, 5).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.3f).visible(true).build();
        }

        if (facilitySprites == null) {
            facilitySprites = new ArrayList<>();
            for (int i = 0; i < 3; ++i) {
                Sprite facilitySprite = new Sprite.Builder("Facility", "tiles_facility_objects.png")
                                .position(new PointF(mapPosition.toGameCoord()))
                                .size(TileSize.clone().multiply(0.5f)).gridSize(6, 8).smooth(false)
                                .layer(SPRITE_LAYER).depth(0.4f).visible(false).build();
                facilitySprites.add(facilitySprite);
            }
        }

        if (glowingSprite == null) {
            glowingSprite = new Sprite.Builder("Glowing", "tiles_glowing.png")
                            .position(new PointF(mapPosition.toGameCoord()))
                            .size(TileSize).gridSize(1, 1).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.5f).visible(true).build();
            glowingSprite.setGridIndex(0, 0);
        }

        if (selectionSprite == null) {
            selectionSprite = new Sprite.Builder("Selection", "tiles_selection.png")
                            .position(new PointF(mapPosition.toGameCoord()))
                            .size(TileSize).gridSize(1, 1).smooth(false)
                            .layer(SPRITE_LAYER).depth(0.6f).visible(true).build();
            selectionSprite.setGridIndex(0, 0);
        }

        if (fogSprite == null) {
            fogSprite = new Sprite.Builder("Fog", "tiles_fog.png")
                            .position(new PointF(mapPosition.toGameCoord()))
                            .size(TileSize).gridSize(2, 1).smooth(false)
                            .layer(SPRITE_FOG_LAYER).depth(0.7f).visible(true).build();
            fogSprite.setGridIndex(0, 0);
        }
    }

    /**
     *
     * @param glowing
     * @param showTerritories
     * @return
     */
    public ArrayList<Sprite> getTileSprites(boolean glowing, boolean showTerritories) {

        ArrayList<Sprite> sprites = new ArrayList<>();

        // create sprites list
        createTileSprites();

        // Set base sprite
        if (getTotalFacilityLevel() > 0) {
            if (terrain == Terrain.FOREST) {
                baseSprite.setGridIndex(0, Terrain.GRASS.ordinal());
            } else if (terrain == Terrain.HILL) {
                baseSprite.setGridIndex(0, Terrain.BADLANDS.ordinal());
            } else {
                baseSprite.setGridIndex(0, terrain.ordinal());
            }
        } else {
            if (terrain == Terrain.HEADQUARTER) {
                baseSprite.setGridIndex(getFaction().ordinal() - 1, Terrain.HEADQUARTER.ordinal());
            } else {
                baseSprite.setGridIndex(baseSpriteSelection, terrain.ordinal());
            }
        }
        sprites.add(baseSprite);

        // Set facility sprite
        if (fogState != FogState.CLEAR) {
            for (Sprite sprite : facilitySprites) {
                sprite.setVisible(false);
                sprite.commit();
            }
        } else if (facilitySlots.size() == 0) {
            // Draw enemy facility
            if (getFaction() != Tribe.Faction.VILLAGER && getFaction() != Tribe.Faction.NEUTRAL &&
                    getTerrain().canDevelop()) {
                boolean enemyFacilityExist = false;
                for (int enemyFacility: enemyFacilityIndex) {
                    if (enemyFacility > 3) {
                        enemyFacilityExist = true;
                        break;
                    }
                }
                if (enemyFacilityExist) {
                    // Switch terrain to basic
                    if (terrain == Terrain.FOREST) {
                        baseSprite.setGridIndex(0, Terrain.GRASS.ordinal());
                    } else if (terrain == Terrain.HILL) {
                        baseSprite.setGridIndex(0, Terrain.BADLANDS.ordinal());
                    } else {
                        baseSprite.setGridIndex(0, terrain.ordinal());
                    }
                    // Position enemy facilities
                    for (int facilityIndex = 0; facilityIndex < 3; ++facilityIndex) {
                        Sprite sprite = facilitySprites.get(facilityIndex);

                        int placement = (facilityIndex + facilitySpriteSelection) % 3;
                        if (placement == 0) {
                            sprite.setPositionOffset(new PointF(0.0f, TileSize.height / 4));
                        } else if (placement == 1) {
                            sprite.setPositionOffset(new PointF(-TileSize.width / 4, -TileSize.height / 8));
                        } else if (placement == 2) {
                            sprite.setPositionOffset(new PointF(TileSize.width / 4, -TileSize.height / 8));
                        }
                        if (enemyFacilityIndex[facilityIndex] >= 3) {
                            sprite.setGridIndex(enemyFacilityIndex[facilityIndex] - 3,
                                    getFaction().ordinal() + Facility.values().length - 1);
                            sprite.setVisible(true);
                            sprites.add(sprite);
                        } else {
                            if (terrain == Terrain.FOREST) {
                                sprite.setGridIndex(0, Facility.values().length);
                                sprite.setVisible(true);
                                sprites.add(sprite);
                            } else if (terrain == Terrain.HILL) {
                                sprite.setGridIndex(1, Facility.values().length);
                                sprite.setVisible(true);
                                sprites.add(sprite);
                            } else {
                                sprite.setVisible(false);
                                sprite.commit();
                            }
                        }
                    }
                }
            } else {
                for (Sprite sprite : facilitySprites) {
                    sprite.setVisible(false);
                    sprite.commit();
                }
            }
        } else {
            // Draw facilities for villager
            int facilityIndex = 0;
            for (int highLevelIndex = 0; facilityIndex < facilitySlots.size(); ++facilityIndex) {
                Sprite sprite = facilitySprites.get(facilityIndex);
                Facility facility = facilitySlots.get(facilityIndex);
                int level = getFacilityLevel(facility);
                if (level <= 3) {
                    sprite.setGridIndex(level - 1, facility.ordinal());
                } else {
                    sprite.setGridIndex(3 + highLevelIndex++, facility.ordinal());
                }

                int placement = (facilityIndex + facilitySpriteSelection) % 3;
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
            for (; facilityIndex < 3; ++facilityIndex) {
                Sprite sprite = facilitySprites.get(facilityIndex);

                int placement = (facilityIndex + facilitySpriteSelection) % 3;
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
                    sprite.setVisible(false);
                    sprite.commit();
                }
            }
        }

        if (showTerritories && fogState == FogState.CLEAR) {
            // Set faction sprite
            factionSprite.setGridIndex(6, faction.ordinal());
            factionSprite.setVisible(true);
            sprites.add(factionSprite);

            // Set border sprite
            int index = 0;
            for (Territory neighbor : neighbors) {
                Sprite border = borderSprites.get(index);
                if (neighbor == null || neighbor.getFaction() != faction) {
                    border.setGridIndex(index, faction.ordinal());
                    border.setDepth(2.0f + 0.01f * (9 - faction.ordinal()));
                    border.setVisible(true);
                    sprites.add(border);
                } else {
                    border.setVisible(false);
                    border.commit();
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
        if (occupationStep > 0 && fogState == FogState.CLEAR) {
            occupationSprite.setVisible(true);
            occupationSprite.setGridIndex(occupationStep - 1, occupyingFaction.ordinal());
            sprites.add(occupationSprite);
        } else {
            occupationSprite.setVisible(false);
            occupationSprite.commit();
        }

        // Show glowing sprite
        if (glowing && fogState != FogState.CLOUDY) {
            glowingSprite.setVisible(true);
            sprites.add(glowingSprite);
        } else {
            glowingSprite.setVisible(false);
            glowingSprite.commit();
        }

        // Show selection sprite
        if (focused && fogState != FogState.CLOUDY) {
            selectionSprite.setVisible(true);
            sprites.add(selectionSprite);
        } else {
            selectionSprite.setVisible(false);
            selectionSprite.commit();
        }

        // Show fog sprite
        if (fogState == FogState.CLEAR) {
            fogSprite.setVisible(false);
            fogSprite.commit();
        } else if (fogState == FogState.MIST) {
            fogSprite.setGridIndex(1, 0);
            fogSprite.setVisible(true);
            sprites.add(fogSprite);
        } else if (fogState == FogState.CLOUDY) {
            fogSprite.setVisible(true);
            fogSprite.setGridIndex(0, 0);
            sprites.add(fogSprite);
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
        if (selectionSprite != null) {
            selectionSprite.close();
            selectionSprite = null;
        }
        if (fogSprite != null) {
            fogSprite.close();
            fogSprite = null;
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
    public OffsetCoord getMapPosition() {
        return mapPosition;
    }

    /**
     *
     * @param mapPosition
     */
    public void setMapPosition(OffsetCoord mapPosition) {
        this.mapPosition = mapPosition;
    }

    /**
     *
     * @param focused
     */
    public void setFocus(boolean focused) {
        this.focused = focused;
        eventHandler.onTerritoryUpdated(this);
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
     * @param level
     */
    public void setFacilityLevel(Facility facility, int level) {
        int currentFacilityLevel = facilityLevels[facility.ordinal()];
        int levelDelta = level - currentFacilityLevel;

        // Check if the territory has already the max facility level
        if (getTotalFacilityLevel() + levelDelta > MAX_FACILITY_LEVEL) {
            //Log.w(LOG_TAG, "Facility level can't go over " + MAX_FACILITY_LEVEL + "!!");
            return;
        }

        // Count the previous facility slot count
        int currentNeededSlotCount;
        if (currentFacilityLevel <= 0) {
            currentNeededSlotCount = 0;
        } else if (currentFacilityLevel <= 3) {
            currentNeededSlotCount = 1;
        } else if (currentFacilityLevel <= 4) {
            currentNeededSlotCount = 2;
        } else {
            currentNeededSlotCount = 3;
        }

        // Count the needed facility slot count
        int newNeededSlotCount;
        if (level <= 0) {
            newNeededSlotCount = 0;
        } else if (level <= 3) {
            newNeededSlotCount = 1;
        } else if (level <= 4) {
            newNeededSlotCount = 2;
        } else {
            newNeededSlotCount = 3;
        }

        // Add/remove facility slot
        int slotDelta = newNeededSlotCount - currentNeededSlotCount;
        if (facilitySlots.size() + slotDelta > terrain.facilitySlots()) {
            return;
        }

        for (int i = 0; i < Math.abs(slotDelta); ++i) {
            if (slotDelta > 0) {
                facilitySlots.add(facility);
            } else {
                facilitySlots.remove(facility);
            }
        }

        // Set facility level
        facilityLevels[facility.ordinal()] = level;
        if (levelDelta >= 0) {
            facilityExps[facility.ordinal()] = 0;
        } else {
            facilityExps[facility.ordinal()] = REQUIRED_FACILITY_EXP_FOR_LEVEL_UP[level] - 1;
        }
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
    public int getTotalFacilityLevel() {
        return Arrays.stream(facilityLevels).sum();
    }

    /**
     *
     * @return
     */
    public Tribe.Faction getFaction() {
        return faction;
    }

    /**
     *
     * @param facility
     * @return
     */
    public DevelopmentPolicy getDevelopmentPolicy(Facility facility) {
        return developmentPolicy[facility.ordinal()];
    }

    /**
     *
     * @param facility
     * @param policy
     */
    public void setDevelopmentPolicy(Facility facility, DevelopmentPolicy policy) {
        developmentPolicy[facility.ordinal()] = policy;
    }

    /**
     *
     * @return
     */
    public int getTax() {
        if (getBattle() != null) {
            return 0;
        } else {
            return (int) (GOLD_STEP * deltas[DeltaAttribute.GOLD.ordinal()] * (happiness / 100.0f));
        }
    }

    /**
     *
     * @return
     */
    public int getPopulation() {
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
        return (occupyingFaction != Tribe.Faction.NEUTRAL && getBattle() == null &&
                !squads.get(0).isEliminated());
    }

    /**
     *
     * @param neighbors
     */
    public void setNeighbors(ArrayList<Territory> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     *
     * @param attribute
     * @return
     */
    public int getDelta(DeltaAttribute attribute) {
        return deltas[attribute.ordinal()];
    }

    /**
     *
     * @param fogState
     */
    public void setFogState(FogState fogState) {
        this.fogState = fogState;
    }

    /**
     *
     * @return
     */
    public FogState getFogState() {
        return fogState;
    }

    /**
     *
     * @param faction
     * @return
     */
    public boolean isFactionSquadExist(Tribe.Faction faction) {
        for (Squad squad: squads) {
            if (squad.getFaction() == faction) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param squad
     * @return
     */
    public boolean isMovable(Squad squad) {

        if (!getTerrain().isMovable(squad.getFaction())) {
            return false;
        }

        for (Squad localSquad : getSquads()) {
            if (squad != localSquad && squad.getFaction() == localSquad.getFaction() ||
                    (squad.getFaction() != Tribe.Faction.VILLAGER &&
                            localSquad.getFaction() != Tribe.Faction.VILLAGER)) {
                return false;
            }
        }

        return true;
    }

    private final static int SPRITE_LAYER = 0;
    private final static int SPRITE_FOG_LAYER = 10;
    private final static int TERRITORY_UPDATE_COUNT = 30;
    private final static int MAX_FACILITY_LEVEL = 5;
    private final static int[] REQUIRED_FACILITY_EXP_FOR_LEVEL_UP =
            new int[] { 500, 1000, 2000, 4000, 8000};
    private final static int FACILITY_EXP_STEP = 10;
    private final static int GOLD_STEP = 10;
    private final static int POPULATION_STEP = 5;
    private final static int HAPPINESS_STEP = 10;
    private final static int BASE_HAPPINESS = 50;
    private final static int OCCUPATION_TOTAL_STEP = 5;
    private final static int OCCUPATION_UPDATE_TIME_FOR_EACH_STEP = 30;

    private Event eventHandler;
    private OffsetCoord mapPosition;
    private Terrain terrain;
    private Tribe.Faction faction;
    private boolean focused = false;
    private ArrayList<Territory> neighbors = null;
    private boolean firstUpdate = true;

    // Occupation
    private Tribe.Faction occupyingFaction = Tribe.Faction.NEUTRAL;
    private int occupationStep = 0;
    private int occupationUpdateForThisStepLeft = OCCUPATION_UPDATE_TIME_FOR_EACH_STEP;

    // Deltas
    private int[] deltas = new int[DeltaAttribute.values().length];

    // Facility
    private int[] facilityLevels;
    private int[] facilityExps;
    private int[] enemyFacilityIndex;
    private DevelopmentPolicy[] developmentPolicy;
    private ArrayList<Facility> facilitySlots = new ArrayList<>();
    private int territoryUpdateLeft = (int)(Math.random()* TERRITORY_UPDATE_COUNT);
    private int happiness;
    private FogState fogState;

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
    private Sprite fogSprite = null;
    private int baseSpriteSelection = (int)(Math.random()*4);
    private int facilitySpriteSelection = (int)(Math.random()*3);
}
