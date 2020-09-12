package com.lifejourney.townhall;

import androidx.core.util.Pair;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Shape;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.Vector2D;

import java.util.ArrayList;

public class Unit extends CollidableObject implements Projectile.Event {
    private static final String LOG_TAG = "Unit";

    enum UnitClassType {
        MELEE_FIGHTER(Engine2D.GetInstance().getString(R.string.melee_fighter)),
        MELEE_SUPPORTER(Engine2D.GetInstance().getString(R.string.melee_supporter)),
        MELEE_HEALER(Engine2D.GetInstance().getString(R.string.melee_healer)),
        RANGED_FIGHTER(Engine2D.GetInstance().getString(R.string.ranged_fighter)),
        RANGED_SUPPORTER(Engine2D.GetInstance().getString(R.string.ranged_supporter)),
        RANGED_HEALER(Engine2D.GetInstance().getString(R.string.ranged_healer)),
        CIVIL(Engine2D.GetInstance().getString(R.string.civil));

        private String word;

        UnitClassType(String word) {
            this.word = word;
        }

        String word() {
            return word;
        }
    };

    enum UnitClass {
        WORKER(
                Engine2D.GetInstance().getString(R.string.worker),
                UnitClassType.CIVIL,
                Engine2D.GetInstance().getString(R.string.worker_desc),
                new Point(1, 0),
                new SizeF(18, 18),
                new Shape(9.0f),
                200,
                20,
                10,
                // Worker / Sword / Archer / Horse / Healer / Cannon / Paladin
                // moving favor
                new float[] {1.0f, -0.5f, -0.5f, -0.5f, -0.5f, 1.0f, -0.5f},
                // target favor
                new float[] {1.0f, -0.5f, -0.5f, -0.5f, -0.5f, 1.0f, -0.5f},
                // damage attribute
                new float[] {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                10.0f,
                26.0f,
                0.0f,
                0.0f,
                30,
                0,
                0,
                1,
                0,
                0,
                0,
                0,
                0,
                50,
                1,
                5,
                null,
                2.0f,
                2.5f,
                5.0f,
                false,
                false,
                0,
                0
        ),
        FIGHTER(
                Engine2D.GetInstance().getString(R.string.fighter),
                UnitClassType.MELEE_FIGHTER,
                Engine2D.GetInstance().getString(R.string.fighter_desc),
                new Point(2, 0),
                new SizeF(18, 18),
                new Shape(9.0f),
                200,
                20,
                20,
                // Worker / Sword / Archer / Horse / Healer / Cannon / Paladin
                // moving favor
                new float[] {0.1f, 0.3f, 0.2f, 0.1f, 0.2f, 0.1f, 0.1f},
                // target favor
                new float[] {0.1f, 0.1f, 0.2f, 0.1f, 0.2f, 0.1f, 0.1f},
                // damage attribute
                new float[] {1.0f, 1.0f, 1.0f, 0.8f, 1.0f, 1.0f, 1.0f},
                150.0f,
                26.0f,
                0.0f,
                0.0f,
                30,
                0,
                0,
                10,
                0,
                0,
                20,
                5,
                20,
                300,
                1,
                10,
                null,
                2.5f,
                3.5f,
                5.0f,
                false,
                true,
                100,
                0
        ),
        ARCHER(
                Engine2D.GetInstance().getString(R.string.archer),
                UnitClassType.RANGED_FIGHTER,
                Engine2D.GetInstance().getString(R.string.archer_desc),
                new Point(3, 0),
                new SizeF(18, 18),
                new Shape(9.0f),
                200,
                20,
                20,
                // Worker / Sword / Archer / Horse / Healer / Cannon / Paladin
                // moving favor
                new float[] {-0.1f, -0.3f, -0.1f, -0.3f, -0.1f, -0.3f, -0.3f},
                // target favor
                new float[] {0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f},
                // damage attribute
                new float[] {1.0f, 1.0f, 1.0f, 1.5f, 1.0f, 1.0f, 1.0f},
                64.0f,
                26.0f,
                200.0f,
                0.0f,
                50,
                30,
                0,
                2,
                9,
                0,
                5,
                5,
                5,
                100,
                1,
                10,
                Projectile.ProjectileType.ARROW,
                2.0f,
                3.0f,
                5.0f,
                true,
                true,
                100,
                200
        ),
        HORSE_MAN(
                Engine2D.GetInstance().getString(R.string.horse_man),
                UnitClassType.MELEE_FIGHTER,
                Engine2D.GetInstance().getString(R.string.horse_man_desc),
                new Point(4, 0),
                new SizeF(18, 18),
                new Shape(9.0f),
                500,
                40,
                40,
                // Worker / Sword / Archer / Horse / Healer / Cannon / Paladin
                // moving favor
                new float[] {0.2f, 0.3f, 0.5f, 0.1f, 0.5f, 0.6f, 0.4f},
                // target favor
                new float[] {0.1f, 0.4f, 0.3f, 0.1f, 0.5f, 0.6f, 0.4f},
                // damage attribute
                new float[] {1.3f, 1.3f, 0.8f, 1.0f, 1.0f, 0.8f, 1.3f},
                300.0f,
                26.0f,
                0.0f,
                0.0f,
                30,
                0,
                0,
                17,
                0,
                0,
                20,
                0,
                10,
                220,
                1,
                30,
                null,
                3.5f,
                4.5f,
                5.0f,
                false,
                true,
                200,
                0
        ),
        HEALER(
                Engine2D.GetInstance().getString(R.string.healer),
                UnitClassType.RANGED_HEALER,
                Engine2D.GetInstance().getString(R.string.healer_desc),
                new Point(5, 0),
                new SizeF(18, 18),
                new Shape(9.0f),
                500,
                40,
                40,
                // Worker / Sword / Archer / Horse / Healer / Cannon / Paladin
                // moving favor
                new float[] {0.0f, -0.4f, 0.9f, -0.5f, 0.0f, 1.0f, -0.4f},
                // target favor
                new float[] {0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f},
                // damage attribute
                new float[] {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                10.0f,
                26.0f,
                0.0f,
                200.0f,
                0,
                0,
                60,
                0,
                0,
                9,
                0,
                5,
                0,
                100,
                1,
                30,
                Projectile.ProjectileType.HEAL,
                2.0f,
                2.5f,
                5.0f,
                true,
                false,
                100,
                300
                ),
        CANNON(
                Engine2D.GetInstance().getString(R.string.cannon),
                UnitClassType.RANGED_SUPPORTER,
                Engine2D.GetInstance().getString(R.string.cannon_desc),
                new Point(6, 0),
                new SizeF(18, 18),
                new Shape(9.0f),
                1000,
                60,
                80,
                // Worker / Sword / Archer / Horse / Healer / Cannon / Paladin
                // moving favor
                new float[] {0.0f, -0.4f, -0.2f, -0.5f, -0.1f, 0.0f, -0.4f},
                // target favor
                new float[] {0.4f, 0.8f, 0.6f, 0.8f, 0.6f, 0.3f, 0.7f},
                // damage attribute
                new float[] {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                200.0f,
                36.0f,
                300.0f,
                0.0f,
                0,
                90,
                0,
                0,
                50,
                0,
                0,
                0,
                0,
                150,
                1,
                40,
                 Projectile.ProjectileType.CANNON,
                1.5f,
                2.0f,
                5.0f,
                true,
                true,
                0,
                300
                ),
        PALADIN(
                Engine2D.GetInstance().getString(R.string.paladin),
                UnitClassType.MELEE_SUPPORTER,
                Engine2D.GetInstance().getString(R.string.paladin_desc),
                new Point(7, 0),
                new SizeF(18, 18),
                new Shape(9.0f),
                1000,
                100,
                160,
                // Worker / Sword / Archer / Horse / Healer / Cannon / Paladin
                // moving favor
                new float[] {0.1f, 0.4f, 0.2f, 0.4f, 0.2f, 0.3f, 0.1f},
                // target favor
                new float[] {0.1f, 0.4f, 0.2f, 0.4f, 0.2f, 0.3f, 0.1f},
                // damage attribute
                new float[] {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                50.0f,
                26.0f,
                0.0f,
                100.0f,
                60,
                0,
                90,
                20,
                0,
                11,
                10,
                10,
                30,
                500,
                1,
                40,
                Projectile.ProjectileType.HEAL,
                2.0f,
                3.5f,
                5.0f,
                true,
                true,
                300,
                100
                );

        private UnitClassType unitClassType;
        private String word;
        private String description;
        private Point spriteGridIndex;
        private SizeF spriteSize;
        private Shape shape;
        private int costToPurchase;
        private int costUpkeep;
        private int population;
        private float[] steeringFavors;
        private float[] targetFavors;
        private float[] strengthMetrics;
        private float awareness;
        private float meleeAttackRange;
        private float rangedAttackRange;
        private float healRange;
        private int meleeAttackSpeed;
        private int rangedAttackSpeed;
        private int healSpeed;
        private float meleeAttackDamage;
        private float rangedAttackDamage;
        private float healPower;
        private float meleeEvasion;
        private float rangedEvasion;
        private float armor;
        private float bountyExp;
        private float health;
        private int vision;
        private Projectile.ProjectileType projectileType;
        private float maxVelocity;
        private float maxForce;
        private float mass;
        private boolean supportable;
        private boolean aggressive;
        private float battleMetric;
        private float supportMetric;

        UnitClass(String word, UnitClassType unitClassType, String description,
                  Point spriteGridIndex, SizeF spriteSize, Shape shape,
                  int costToPurchase, int costUpkeep, int population, float[] steeringFavors,
                  float[] targetFavors, float[] strengthMetrics, float awareness,
                  float meleeAttackRange, float rangedAttackRange, float healRange,
                  int meleeAttackSpeed, int rangedAttackSpeed, int healSpeed,
                  float meleeAttackDamage, float rangedAttackDamage, float healPower,
                  float meleeEvasion, float rangedEvasion, float armor, float health, int vision,
                  float bountyExp, Projectile.ProjectileType projectileType, float maxVelocity,
                  float maxForce, float mass, boolean supportable, boolean aggressive,
                  float battleMetric, float supportMetric) {
            this.word = word;
            this.unitClassType = unitClassType;
            this.description = description;
            this.spriteGridIndex = spriteGridIndex;
            this.spriteSize = spriteSize;
            this.shape = shape;
            this.costToPurchase = costToPurchase;
            this.costUpkeep = costUpkeep;
            this.population = population;
            this.steeringFavors = steeringFavors;
            this.targetFavors = targetFavors;
            this.strengthMetrics = strengthMetrics;
            this.awareness = awareness;
            this.meleeAttackRange = meleeAttackRange;
            this.rangedAttackRange = rangedAttackRange;
            this.healRange = healRange;
            this.meleeAttackSpeed = meleeAttackSpeed;
            this.rangedAttackSpeed = rangedAttackSpeed;
            this.healSpeed = healSpeed;
            this.meleeAttackDamage = meleeAttackDamage;
            this.rangedAttackDamage = rangedAttackDamage;
            this.healPower = healPower;
            this.meleeEvasion = meleeEvasion;
            this.rangedEvasion = rangedEvasion;
            this.armor = armor;
            this.health = health;
            this.vision = vision;
            this.bountyExp = bountyExp;
            this.projectileType = projectileType;
            this.maxVelocity = maxVelocity;
            this.maxForce = maxForce;
            this.mass = mass;
            this.supportable = supportable;
            this.aggressive = aggressive;
            this.battleMetric = battleMetric;
            this.supportMetric = supportMetric;
        }

        public String word() {
            return word;
        }
        public UnitClassType unitClassType() {
            return unitClassType;
        }
        public String description() {
            return description;
        }
        public Point spriteGridIndex() {
            return spriteGridIndex;
        }
        public SizeF spriteSize() {
            return spriteSize;
        }
        public Shape shape() {
            return shape;
        }
        public int costToPurchase() {
            return costToPurchase;
        }
        public int costUpkeep() {
            return costUpkeep;
        }
        public int population() {
            return population;
        }
        public float steeringFavor(UnitClass unitClass) {
            return steeringFavors[unitClass.ordinal()];
        }
        public float targetFavor(UnitClass unitClass) {
            return targetFavors[unitClass.ordinal()];
        }
        public float strengthMetric(UnitClass unitClass) {
            return strengthMetrics[unitClass.ordinal()];
        }
        public float awareness() {
            return awareness;
        }
        public float seekingFavorRange() {
            return meleeAttackRange();
        }
        public float meleeAttackRange() {
            return meleeAttackRange;
        }
        public float rangedAttackRange() {
            return rangedAttackRange;
        }
        public float healRange() {
            return healRange;
        }
        public int meleeAttackSpeed() {
            return meleeAttackSpeed;
        }
        public int rangedAttackSpeed() {
            return rangedAttackSpeed;
        }
        public int healSpeed() {
            return healSpeed;
        }
        public float meleeAttackDamage() {
            return meleeAttackDamage;
        }
        public float rangedAttackDamage() {
            return rangedAttackDamage;
        }
        public float healPower() {
            return healPower;
        }
        public float meleeEvasion() {
            return meleeEvasion;
        }
        public float rangedEvasion() {
            return rangedEvasion;
        }
        public float armor() {
            return armor;
        }
        public float health() {
            return health;
        }
        public int vision() {
            return vision;
        }
        public int bountyExp(int level) {
            return (int) (bountyExp * (1.0 + level * 0.2));
        }
        public int requiredExpToLevelUp(int level) {
            return 300*level;
        }
        public Projectile.ProjectileType projectileType() {
            return projectileType;
        }
        public float friction() {
            return 0.1f;
        }
        public float maxVelocity() {
            return maxVelocity;
        }
        public float maxForce() {
            return maxForce;
        }
        public float mass() {
            return mass;
        }
        public boolean isSupportable() {
            return supportable;
        }
        public boolean isAggressive() {
            return aggressive;
        }
        public float battleMetric() {
            return battleMetric;
        }
        public float supportMetric() {
            return supportMetric;
        }
    }

    public static class Builder {

        private UnitClass unitClass;
        private Tribe.Faction faction;
        private GameMap map;

        private PointF position = new PointF();

        public Builder(UnitClass unitClass, Tribe.Faction faction, GameMap map) {
            this.unitClass = unitClass;
            this.faction = faction;
            this.map = map;
        }
        public Builder position(PointF position) {
            this.position = position;
            return this;
        }
        public Unit build() {
            Sprite unitClassSprite = new Sprite.Builder("class", "unit_class.png")
                    .gridSize(UnitClass.values().length+1,1).size(unitClass.spriteSize())
                    .smooth(true).opaque(0.0f).build();
            Point unitGridIndex = unitClass.spriteGridIndex();
            unitClassSprite.setGridIndex(unitGridIndex.x, unitGridIndex.y);
            Sprite unitFrameSprite = new Sprite.Builder("frame", "unit_frame.png")
                    .gridSize(5,1).size(unitClass.spriteSize()).smooth(true).opaque(0.0f)
                    .build();
            unitFrameSprite.setGridIndex(faction.ordinal(), 0);
            Sprite unitHealthSprite = new Sprite.Builder("health", "unit_health.png")
                    .gridSize(9,1).size(unitClass.spriteSize()).smooth(true).opaque(0.0f)
                    .build();
            Sprite unitEffectSprite = new Sprite.Builder("effect", "unit_effect.png")
                    .gridSize(27,1).size(unitClass.spriteSize().clone().multiply(2.0f))
                    .smooth(false).opaque(0.0f).build();
            return (Unit) new PrivateBuilder<>(position, unitClass, faction, map)
                    .sprite(unitClassSprite)
                    .sprite(unitFrameSprite)
                    .sprite(unitHealthSprite)
                    .sprite(unitEffectSprite)
                    .maxForce(unitClass.maxForce()).maxVelocity(unitClass.maxVelocity())
                    .maxAngularVelocity(0.0f).inertia(Float.MAX_VALUE)
                    .mass(unitClass.mass()).friction(unitClass.friction())
                    .shape(unitClass.shape()).layer(SPRITE_LAYER).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Unit.PrivateBuilder<T>> extends CollidableObject.Builder<T> {

        private UnitClass unitClass;
        private Tribe.Faction faction;
        private GameMap map;

        public PrivateBuilder(PointF position, UnitClass unitClass, Tribe.Faction faction, GameMap map) {
            super(position);
            this.unitClass = unitClass;
            this.faction = faction;
            this.map = map;
        }
        public Unit build() {
            return new Unit(this);
        }
    }

    private Unit(PrivateBuilder builder) {
        super(builder);

        targetMapPosition = new OffsetCoord(getPosition());
        unitClass = builder.unitClass;
        faction = builder.faction;
        map = builder.map;
        health = getUnitClass().health();
        level = 1;
    }

    @Override
    public void close() {
        for (Projectile projectile: projectiles) {
            projectile.close();
        }

        super.close();
    }

    /**
     *
     */
    @Override
    public void update() {
        // Adjust moving speed
        if (Upgradable.HORSE_MAN_MOVE_SPEED.getLevel(faction) > 0 &&
                getUnitClass() == UnitClass.HORSE_MAN) {
            setMaxVelocity(getUnitClass().maxVelocity() * (1.0f + HORSEMAN_MOVE_SPEED_DELTA *
                    Upgradable.HORSE_MAN_MOVE_SPEED.getLevel(faction)));
            setMaxForce(getUnitClass().maxForce() * (1.0f + HORSEMAN_MOVE_SPEED_DELTA *
                    Upgradable.HORSE_MAN_MOVE_SPEED.getLevel(faction)));
        } else if (Upgradable.CANNON_MOVE_SPEED.getLevel(faction) > 0 &&
                getUnitClass() == UnitClass.CANNON) {
            setMaxVelocity(getUnitClass().maxVelocity() * (1.0f + CANNON_MOVE_SPEED_DELTA *
                    Upgradable.CANNON_MOVE_SPEED.getLevel(faction)));
            setMaxForce(getUnitClass().maxForce() * (1.0f + CANNON_MOVE_SPEED_DELTA *
                    Upgradable.CANNON_MOVE_SPEED.getLevel(faction)));
        } else {
            setMaxVelocity(getUnitClass().maxVelocity());
            setMaxForce(getUnitClass().maxForce());
        }

        if (isInvincible()) {
            invincibled();
        } else if (invincibleCooldownLeft > 0) {
            invincibleCooldownLeft--;
        }

        if (isSlowed()) {
            slowed();
        }

        if (isStunned()) {
            stunned();
        } else if (!isRecruiting() && isFighting()) {
            // If it's on battle,  seek or flee enemies
            float highestFavor = -Float.MAX_VALUE, lowestFavor = Float.MAX_VALUE;
            Unit highestFavorUnit = null, lowestFavorUnit = null;
            float highestFavorDistance = Float.MAX_VALUE, lowestFavorDistance = Float.MAX_VALUE;
            for (Unit opponent : opponents) {
                if (opponent.isRecruiting() || opponent.isInvincible()) {
                    continue;
                }

                float distance = opponent.getPosition().distance(getPosition());
                if (distance <= getUnitClass().awareness()) {
                    float favor = getUnitClass().steeringFavor(opponent.getUnitClass());
                    if (favor <= lowestFavor) {
                        if (favor < lowestFavor || distance < lowestFavorDistance) {
                            lowestFavor = favor;
                            lowestFavorUnit = opponent;
                            lowestFavorDistance = distance;
                        }
                    }
                    if (favor >= highestFavor) {
                        if (favor > highestFavor || distance < highestFavorDistance) {
                            highestFavor = favor;
                            highestFavorUnit = opponent;
                            highestFavorDistance = distance;
                        }
                    }
                }
            }
            if (highestFavor > 0.0f && highestFavorUnit != null) {
                if (highestFavorDistance > getUnitClass().seekingFavorRange()) {
                    seek(highestFavorUnit.getPosition(), 1.0f);
                    wander(80.0f, 1.0f, 0.1f);
                }
            } else if (lowestFavor < 0.0f && lowestFavorUnit != null) {
                if (lowestFavorDistance < getUnitClass().rangedAttackRange()) {
                    flee(lowestFavorUnit.getPosition(), 1.0f);
                    wander(80.0f, 1.0f, 0.1f);
                }
            } else {
                wander(80.0f, 1.0f, 0.2f);
            }
        } else {
            // If it's at peace, seek to target position
            PointF targetPosition = new PointF(targetMapPosition.toGameCoord());
            OffsetCoord currentMapOffset = new OffsetCoord(getPosition());

            if (!currentMapOffset.equals(targetMapPosition)) {
                seek(targetPosition, 1.0f);

                // Wander a little
                wander(80.0f, 1.0f, 0.3f);
            } else {
                seek(targetPosition, 0.5f);

                // Wander a big
                wander(80.0f, 1.0f, 0.5f);
            }

            // Separation
            separate(companions, 20.0f, 1.0f);
        }

        // Add gravity to target
        restrict(targetMapPosition);

        // Update moving
        super.update();

        // Update projectile
        ArrayList<Projectile> projectilesCopy = new ArrayList<>();
        projectilesCopy.addAll(projectiles);
        for (Projectile projectile: projectilesCopy) {
            projectile.update();
        }

        // Handle recruiting
        if (isRecruiting()) {
            if (!isFighting()) {
                recruitingTimeLeft--;
            }
        } else if (isInvisible()) {
            for (Sprite sprite : getSprites()) {
                sprite.setOpaque(0.0f);
            }
        } else {
            for (Sprite sprite : getSprites()) {
                sprite.setOpaque(1.0f);
            }
        }

        // Update dot damage
        ArrayList<Pair<Float, Integer>> dotsCopy = new ArrayList<>(dotDamages);
        for (Pair<Float, Integer> dot : dotsCopy) {
            health -= dot.first;
            if (health < 0.0f) {
                health = 0.0f;
            } else if (health > getMaxHealth()) {
                health = getMaxHealth();
            }
            if (dot.second > 1) {
                dotDamages.add(new Pair<>(dot.first, dot.second - 1));
            }
            dotDamages.remove(dot);
        }

        // Check invincible mode
        if (getUnitClass() == UnitClass.PALADIN &&
                Upgradable.PALADIN_INVINCIBLE.getLevel(faction) > 0 &&
                getHealth()/getMaxHealth() < INVINCIBLE_HEALTH_THRESHOLD) {
            if (!isInvincible() && invincibleCooldownLeft == 0) {
                health = Math.max(health, getMaxHealth() * INVINCIBLE_HEALTH_THRESHOLD);
                invincibleCooldownLeft = INVINCIBLE_COOLDOWN;
                invincibleTimeLeft = (int) (INVINCIBLE_DURATION * (1.0f + (INVINCIBLE_DURATION_DELTA *
                        Upgradable.PALADIN_INVINCIBLE.getLevel(faction) - 1)));
            }
        }

        // Adjust health sprite
        Sprite healthSprite = getSprite("health");
        healthSprite.setGridIndex((int) ((1.0f - health / getMaxHealth()) / 0.1275f), 0);

        // Hide unit if it's in fog
        if (!isRecruiting()) {
            OffsetCoord mapPosition = new OffsetCoord(getPosition());
            Territory currentTerritory = map.getTerritory(mapPosition);
            if (currentTerritory != null &&
                map.getTerritory(targetMapPosition).getFogState() != Territory.FogState.CLEAR) {
                hide();
            } else {
                show();
            }
        }
    }

    /**
     *
     */
    @Override
    public void commit() {

        super.commit();

        for (Projectile projectile: projectiles) {
            projectile.commit();
        }
    }

    /**
     *
     * @param target
     */
    @Override
    public void onCollisionOccurred(CollidableObject target) {
    }

    /**
     *
     * @param projectile
     */
    @Override
    public void onProjectileReached(Projectile projectile) {

        projectiles.remove(projectile);
        projectile.close();

        Unit target = projectile.getTarget();
        if (target.isClosed() || !target.isFighting()) {
            return;
        }

        if (projectile.getType() == Projectile.ProjectileType.ARROW) { // Arrow
            if (Math.random() < target.getRangedEvasion()) {
                // Check evading
                return;
            }

            if (focused || (getOpponents() != null && getOpponents().size() > 0
                    && getOpponents().get(0).isFocused())) {
                Engine2D.GetInstance().playSoundEffect("arrow", 1.0f);
            }

            // Point blank
            float buff = 1.0f;
            if (Upgradable.ARCHER_POINT_BLANK.getLevel(faction) > 1 &&
                    getUnitClass() == UnitClass.ARCHER) {
                buff += BUFF_DELTA * (Upgradable.ARCHER_POINT_BLANK.getLevel(faction) - 1);
            }

            float damageBase =
                    getRangedDamage() * buff * getUnitClass().strengthMetric(target.getUnitClass());

            // Poison arrow
            if (Upgradable.ARCHER_POISON_ARROW.getLevel(faction) > 0 &&
                getUnitClass() == UnitClass.ARCHER) {

                float dotDamage =
                        damageBase * DOT_DELTA * Upgradable.ARCHER_POISON_ARROW.getLevel(faction);
                target.gotDotDamage(dotDamage, DOT_DURATION);
            }

            // Deal damage
            float damage = Math.max(damageBase * (1.0f - target.getArmor()), 1.0f);
            target.gotDamage(damage);

        } else if (projectile.getType() == Projectile.ProjectileType.HEAL) { // Heal
            if (focused || (getOpponents() != null && getOpponents().size() > 0
                    && getOpponents().get(0).isFocused())) {
                Engine2D.GetInstance().playSoundEffect("heal", 1.0f);
            }

            if (Upgradable.HEALER_DOT_HEAL.getLevel(faction) > 0 &&
                getUnitClass() == UnitClass.HEALER) {
                float dotHeal =
                        getHealPower() * DOT_DELTA * Upgradable.HEALER_DOT_HEAL.getLevel(faction);
                for (Unit unit: companions) {
                    unit.gotDotHeal(dotHeal, DOT_DURATION);
                }
            }

            // Splash damage
            if (Upgradable.HEALER_SPLASH_DAMAGE.getLevel(faction) > 0 &&
                    getUnitClass() == UnitClass.HEALER) {
                float splashDamage = getHealPower() *
                        (SPLASH_DAMAGE_DELTA * Upgradable.HEALER_SPLASH_DAMAGE.getLevel(faction));
                for (Unit opponent : target.getOpponents()) {
                    if (!opponent.isClosed() && opponent.isFighting() &&
                            opponent.getPosition().distance(target.getPosition()) <= SPLASH_DAMAGE_RANGE) {
                        opponent.gotSplashDamage(splashDamage);
                    }
                }
            }

            // Heal
            target.gotHeal(getHealPower());
        } else if (projectile.getType() == Projectile.ProjectileType.CANNON) { // Cannon
            if (Math.random() < target.getRangedEvasion()) {
                // Check evading
                return;
            }

            // Deal damage
            float damage = Math.max(getRangedDamage() *
                    getUnitClass().strengthMetric(target.getUnitClass()) * (1.0f - target.getArmor()), 1.0f);
            target.gotDamage(damage);

            float splashDamage = damage * SPLASH_DAMAGE_DELTA;
            float splashDamageRange =
                    SPLASH_DAMAGE_RANGE * (1.0f + SPLASH_DAMAGE_RANGE_DELTA *
                            Upgradable.CANNON_SPLASH_RANGE.getLevel(faction));

            for (Unit opponent : target.getCompanions()) {
                if (!opponent.isClosed() && opponent.isFighting() &&
                        opponent.getPosition().distance(target.getPosition()) <= splashDamageRange) {
                    opponent.gotSplashDamage(splashDamage);
                    if (Upgradable.CANNON_SLOWNESS.getLevel(faction) > 0) {
                        opponent.gotSlow(SLOW_DELTA * Upgradable.CANNON_SLOWNESS.getLevel(faction));
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void fight() {
        if (isRecruiting() || isStunned()) {
            return;
        }

        // Do the melee attack
        Unit meleeTarget = searchFavoredMeleeTarget();
        if (getMeleeAttackSpeed() > 0 && meleeAttackTimeLeft == 0) {
            if (meleeTarget != null) {
                attackMelee(meleeTarget);
            }
            meleeAttackTimeLeft = getMeleeAttackSpeed();
        } else {
            meleeAttackTimeLeft--;
        }

        // Do the ranged attack if if doesn't attack melee
        if (meleeTarget == null) {
            if (getRangedAttackSpeed() > 0 && rangedAttackTimeLeft == 0) {
                Unit rangedTarget = searchFavoredRangedTarget();
                if (rangedTarget != null) {
                    attackRanged(rangedTarget);
                }
                rangedAttackTimeLeft = getRangedAttackSpeed();
            } else {
                rangedAttackTimeLeft--;
            }
        }

        // Do healing
        if (getUnitClass().healSpeed() > 0 && healTimeLeft == 0) {
            Unit healTarget = searchFavoredHealTarget();
            if (healTarget != null) {
                rest(healTarget);
            }
            healTimeLeft = getUnitClass().healSpeed();
        } else {
            healTimeLeft--;
        }
    }

    /**
     *
     */
    public void support() {

        if (isRecruiting()) {
            return;
        }

        if (getUnitClass().rangedAttackSpeed() > 0 && rangedAttackTimeLeft == 0) {
            Unit rangedTarget = searchFavoredRangedTarget();
            if (rangedTarget != null) {
                attackRanged(rangedTarget);
            }
            rangedAttackTimeLeft = getUnitClass().rangedAttackSpeed();
        } else {
            rangedAttackTimeLeft--;
        }

        // Do healing
        if (unitClass.healSpeed() > 0 && healTimeLeft == 0) {
            Unit healTarget = searchFavoredHealTarget();
            if (healTarget != null) {
                rest(healTarget);
            }
            healTimeLeft = unitClass.healSpeed();
        } else {
            healTimeLeft--;
        }
    }

    /**
     *
     * @return
     */
    private Unit searchFavoredMeleeTarget() {
        // Find favored enemy for melee attack
        Unit targetCandidate = null;
        float highestFavor = -Float.MAX_VALUE;
        float highestHealth = Float.MAX_VALUE;
        for (Unit opponent : opponents) {
            if (opponent.isRecruiting() || opponent.isInvincible()) {
                continue;
            }
            if (opponent.getPosition().distance(getPosition()) <= getMeleeAttackRange()) {
                float favor = getUnitClass().targetFavor(opponent.getUnitClass());
                if (favor > highestFavor ||
                        (favor == highestFavor && opponent.getHealth() < highestHealth)) {
                    targetCandidate = opponent;
                    highestFavor = favor;
                    highestHealth = opponent.getHealth();
                }
            }
        }

        return targetCandidate;
    }

    /**
     *
     * @return
     */
    private Unit searchFavoredRangedTarget() {
        if (getUnitClass().rangedAttackRange() == 0.0f) {
            return null;
        }

        Unit targetCandidate = null;
        float highestFavor = -Float.MAX_VALUE;
        float highestHealth = Float.MAX_VALUE;

        // Select the highest favored enemy for ranged attack
        for (Unit opponent : opponents) {
            if (opponent.isRecruiting() || opponent.isInvincible()) {
                continue;
            }
            if (opponent.getPosition().distance(getPosition()) <=
                    getUnitClass().rangedAttackRange()) {
                float favor = getUnitClass().targetFavor(opponent.getUnitClass());
                if (favor > highestFavor ||
                        (favor == highestFavor && opponent.getHealth() < highestHealth)) {
                    targetCandidate = opponent;
                    highestFavor = favor;
                    highestHealth = opponent.getHealth();
                }
            }
        }

        return targetCandidate;
    }


    /**
     *
     * @return
     */
    private Unit searchFavoredHealTarget() {

        if (getUnitClass().healRange() == 0.0f) {
            return null;
        }

        Unit targetCandidate = null;
        float lowestHealthPercentage = Float.MAX_VALUE;

        // Select the highest favored companion for heal
        for (Unit companion : companions) {
            if (companion.isRecruiting()) {
                continue;
            }
            if (companion.getPosition().distance(getPosition()) <= getUnitClass().healRange()) {
                float healthPercentage = companion.getHealth() / companion.getMaxHealth();
                if (healthPercentage < 1.0f && healthPercentage < lowestHealthPercentage) {
                    targetCandidate = companion;
                    lowestHealthPercentage = healthPercentage;
                }
            }
        }

        return targetCandidate;
    }

    /**
     *
     */
    public void endFight() {
        for (Projectile projectile: projectiles) {
            projectile.close();
        }
        projectiles.clear();
        dotDamages.clear();
        slowTimeLeft = 0;
        stunTimeLeft = 0;
        opponents = null;
    }

    /**
     *
     * @param opponent
     */
    private void attackMelee(Unit opponent) {

        if (focused || (getOpponents() != null && getOpponents().size() > 0
                && getOpponents().get(0).isFocused())) {
            if (getUnitClass() == UnitClass.HORSE_MAN) {
                Engine2D.GetInstance().playSoundEffect("trot", 1.0f);
            } else {
                Engine2D.GetInstance().playSoundEffect("sword" + (int)(Math.random() * 2 + 1),
                        1.0f);
            }
        }

        // Check evading
        if (Math.random() < opponent.getMeleeEvasion()) {
            return;
        }

        // Critical attack
        boolean critical = false;
        if (getUnitClass() == UnitClass.FIGHTER &&
                Upgradable.FIGHTER_CRITICAL_ATTACK.getLevel(faction) > 0) {
            critical = Math.random() < (Upgradable.FIGHTER_CRITICAL_ATTACK.getLevel(faction) *
                    CRITICAL_ATTACK_RATE_DELTA);
        }

        // Stun attack
        if (getUnitClass() == UnitClass.HORSE_MAN &&
            Upgradable.HORSE_MAN_STUN.getLevel(faction) > 0) {
            boolean stun = Math.random() < (Upgradable.HORSE_MAN_STUN.getLevel(faction) *
                    STUN_DELTA);

            if (stun) {
                opponent.gotStun();
            }
        }

        // Apply buff
        float buff = 1.0f;
        if (Upgradable.FIGHTER_BUFF.getLevel(faction) > 0) {
            for (Unit companion: companions) {
                if (companion.getUnitClass() == UnitClass.FIGHTER) {
                    buff += Upgradable.FIGHTER_BUFF.getLevel(faction) * BUFF_DELTA;
                }
            }
        }

        // Deal damage
        float damage = Math.max(getMeleeDamage() * ((critical)?4.0f:1.0f) * buff *
                unitClass.strengthMetric(opponent.unitClass) * (1.0f - opponent.getArmor()), 1.0f);
        if (critical) {
            opponent.gotCriticalDamage(damage);
        } else {
            opponent.gotDamage(damage);
        }
    }

    /**
     *
     * @param target
     */
    private void attackRanged(Unit target) {

        // Create a projectile
        Projectile projectile =
                new Projectile.Builder(this, unitClass.projectileType(), target,
                        this.getPosition().clone()).build();
        projectiles.add(projectile);
    }

    /**
     *
     */
    private  void invincibled() {

        ArrayList<Pair<Float, Integer>> dotDamagesCopy = new ArrayList<>(dotDamages);
        for (Pair<Float, Integer> dotDamage : dotDamagesCopy) {
            if (dotDamage.first > 0.0f) {
                dotDamages.remove(dotDamage);
            }
        }
        slowTimeLeft = 0;
        stunTimeLeft = 0;

        Sprite effectSprite = getSprite("effect");
        if (invincibleTimeLeft-- > 0) {
            effectSprite.setAnimationWrap(false);
            effectSprite.setGridIndex(25, 0);
        } else {
            effectSprite.clearAnimation();
            effectSprite.setAnimationWrap(false);
        }
    }

    /**
     *
     *
     */
    private void stunned() {

        // If it's stunned
        setVelocity(new Vector2D());
        setForce(new Vector2D());

        Sprite effectSprite = getSprite("effect");
        if (--stunTimeLeft > 0) {
            // stun effect should be remained
            Point gridIndex = effectSprite.getGridIndex();
            if (gridIndex.x != 19 && gridIndex.x != 20 && gridIndex.x != 21 &&
                    gridIndex.x != 22) {
                effectSprite.setAnimationWrap(true);
                effectSprite.clearAnimation();
                effectSprite.addAnimationFrame(19, 0, 10);
                effectSprite.addAnimationFrame(20, 0, 10);
                effectSprite.addAnimationFrame(21, 0, 10);
                effectSprite.addAnimationFrame(22, 0, 10);
            }
        } else {
            effectSprite.clearAnimation();
            effectSprite.setAnimationWrap(false);
        }
    }

    /**
     *
     */
    private void slowed() {

        setMaxVelocity(getMaxVelocity() * (1.0f - HORSEMAN_MOVE_SPEED_DELTA * slowness));
        setMaxForce(getMaxForce() * (1.0f - HORSEMAN_MOVE_SPEED_DELTA * slowness));

        Sprite effectSprite = getSprite("effect");
        if (--slowTimeLeft > 0) {
            if (!isStunned()) {
                // slow effect should be remained
                Point gridIndex = effectSprite.getGridIndex();
                if (gridIndex.x != 23 && gridIndex.x != 24) {
                    effectSprite.setAnimationWrap(true);
                    effectSprite.clearAnimation();
                    effectSprite.addAnimationFrame(23, 0, 10);
                    effectSprite.addAnimationFrame(24, 0, 10);
                }
            }
        } else {
            if (!isStunned()) {
                effectSprite.clearAnimation();
                effectSprite.setAnimationWrap(false);
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isStunned() {

        return (stunTimeLeft > 0);
    }

    /**
     *
     * @return
     */
    public boolean isSlowed() {

        return (slowTimeLeft > 0);
    }

    /**
     *
     * @param target
     */
    private void rest(Unit target) {

        // Create a projectile
        Projectile projectile =
                new Projectile.Builder(this, unitClass.projectileType(), target,
                        this.getPosition().clone()).build();
        projectiles.add(projectile);
    }

    /**
     *
     */
    private void gotStun() {

        if (isInvincible()) {
            return;
        }
        stunTimeLeft += STUN_DURATION;
    }

    /**
     *
     * @param slowness
     */
    private void gotSlow(float slowness) {

        if (isInvincible()) {
            return;
        }
        this.slowTimeLeft += SLOW_DURATION;
        this.slowness = slowness;
    }

    /**
     *
     * @param damage
     */
    private void gotDamage(float damage) {

        if (isInvincible()) {
            return;
        }

        if (focused || (getOpponents().size() > 0 && getOpponents().get(0).isFocused())) {
            Engine2D.GetInstance().playSoundEffect("hit" + (int)(Math.random()*5 + 1), 1.0f);
        }

        if (Upgradable.PALADIN_GUARDIAN.getLevel(faction) > 0) {
            float guardianDelta =
                    damage * GUARDIAN_DAMAGE_DELTA * Upgradable.PALADIN_GUARDIAN.getLevel(faction);
            for (Unit companion: companions) {
                if (companion != this && companion.getUnitClass() == UnitClass.PALADIN) {
                    damage -= guardianDelta;
                    companion.gotGuardianDamage(guardianDelta);
                }
            }
        }

        health -= damage;
        if (health < 0) {
            health = 0;
        }

        // Blinking
        Sprite classSprite = getSprite("class");
        classSprite.clearAnimation();
        classSprite.addAnimationFrame(0, 0, 5);
        Point unitGridIndex = unitClass.spriteGridIndex();
        classSprite.addAnimationFrame(unitGridIndex.x, unitGridIndex.y,5);
        classSprite.addAnimationFrame(0, 0, 5);
        classSprite.addAnimationFrame(unitGridIndex.x, unitGridIndex.y,5);
    }

    /**
     *
     * @param damage
     */
    private void gotDotDamage(float damage, int duration) {

        if (isInvincible()) {
            return;
        }

        dotDamages.add(new Pair<>(damage, duration));

        // Poison effect
        Sprite effectSprite = getSprite("effect");
        effectSprite.clearAnimation();
        effectSprite.addAnimationFrame(15, 0, 10);
        effectSprite.addAnimationFrame(16, 0, 10);
        effectSprite.addAnimationFrame(17, 0, 10);
        effectSprite.addAnimationFrame(18, 0, 10);
        effectSprite.addAnimationFrame(0, 0, 5);
    }

    /**
     *
     * @param damage
     */
    private void gotGuardianDamage(float damage) {

        if (!isInvincible()) {
            health -= damage;
            if (health < 0) {
                health = 0;
            }
        }

        // Blinking
        Sprite classSprite = getSprite("class");
        classSprite.clearAnimation();
        classSprite.addAnimationFrame(0, 0, 5);
        Point unitGridIndex = unitClass.spriteGridIndex();
        classSprite.addAnimationFrame(unitGridIndex.x, unitGridIndex.y,5);
        classSprite.addAnimationFrame(0, 0, 5);
        classSprite.addAnimationFrame(unitGridIndex.x, unitGridIndex.y,5);

        // Guardian effect
        Sprite effectSprite = getSprite("effect");
        effectSprite.clearAnimation();
        effectSprite.addAnimationFrame(26, 0, 10);
        effectSprite.addAnimationFrame(0, 0, 5);
    }

    /**
     *
     * @param damage
     */
    private void gotCriticalDamage(float damage) {

        if (isInvincible()) {
            return;
        }

        if (focused || (getOpponents().size() > 0 && getOpponents().get(0).isFocused())) {
            Engine2D.GetInstance().playSoundEffect("hit" + (int)(Math.random()*5 + 1), 1.0f);
        }

        if (Upgradable.PALADIN_GUARDIAN.getLevel(faction) > 0) {
            float guardianDelta =
                    damage * MELEE_DAMAGE_UPGRADE_DELTA * Upgradable.PALADIN_GUARDIAN.getLevel(faction);
            for (Unit companion: companions) {
                if (companion != this && companion.getUnitClass() == UnitClass.PALADIN) {
                    damage -= guardianDelta;
                    companion.gotGuardianDamage(guardianDelta);
                }
            }
        }

        health -= damage;
        if (health < 0) {
            health = 0;
        }

        // Blinking
        Sprite classSprite = getSprite("class");
        classSprite.clearAnimation();
        classSprite.addAnimationFrame(0, 0, 5);
        Point unitGridIndex = unitClass.spriteGridIndex();
        classSprite.addAnimationFrame(unitGridIndex.x, unitGridIndex.y,5);
        classSprite.addAnimationFrame(0, 0, 5);
        classSprite.addAnimationFrame(unitGridIndex.x, unitGridIndex.y,5);

        // Critical effect
        Sprite effectSprite = getSprite("effect");
        effectSprite.clearAnimation();
        effectSprite.addAnimationFrame(12, 0, 10);
        effectSprite.addAnimationFrame(13, 0, 10);
        effectSprite.addAnimationFrame(14, 0, 10);
        effectSprite.addAnimationFrame(0, 0, 5);
    }

    /**
     *
     * @param damage
     */
    private void gotSplashDamage(float damage) {
        if (isInvincible()) {
            return;
        }

        health = Math.max(health - damage, 0);

        // Blinking
        Sprite classSprite = getSprite("class");
        classSprite.clearAnimation();
        classSprite.addAnimationFrame(0, 0, 5);
        Point unitGridIndex = unitClass.spriteGridIndex();
        classSprite.addAnimationFrame(unitGridIndex.x, unitGridIndex.y,5);
        classSprite.addAnimationFrame(0, 0, 5);
        classSprite.addAnimationFrame(unitGridIndex.x, unitGridIndex.y,5);

        // Splashing effect
        Sprite effectSprite = getSprite("effect");
        effectSprite.clearAnimation();
        effectSprite.addAnimationFrame(6, 0, 10);
        effectSprite.addAnimationFrame(7, 0, 10);
        effectSprite.addAnimationFrame(0, 0, 5);
    }

    /**
     *
     * @param healPower
     */
    private void gotHeal(float healPower) {
        health = Math.min(health + healPower, getMaxHealth());

        // Healing effect
        Sprite effectSprite = getSprite("effect");
        effectSprite.clearAnimation();
        effectSprite.addAnimationFrame(1, 0, 10);
        effectSprite.addAnimationFrame(0, 0, 5);
    }

    /**
     *
     * @param healPower
     */
    private void gotDotHeal(float healPower, int duration) {
        dotDamages.add(new Pair<>(-healPower, duration));

        // Healing effect
        Sprite effectSprite = getSprite("effect");
        effectSprite.clearAnimation();
        effectSprite.addAnimationFrame(1, 0, 10);
        effectSprite.addAnimationFrame(0, 0, 5);
    }

    /**
     *
     */
    public boolean isKilled() {

        return health <= 0.0f;
    }

    /**
     *
     * @param value
     * @return
     */
    private float adjustLevel(float value) {

        return value * (1.0f + 0.1f * level);
    }

    /**
     *
     * @param value
     * @return
     */
    private float adjustBonus(float value, float bonus) {
        return value * (1.0f + bonus);
    }

    /**
     *
     * @return
     */
    public float getMaxHealth() {
        float maxHealth = adjustBonus(adjustLevel(getUnitClass().health()), healthBonus);
        if (Upgradable.PALADIN_HEALTH.getLevel(faction) > 0 && getUnitClass() == UnitClass.PALADIN) {
            maxHealth *= 1.0f + Upgradable.PALADIN_HEALTH.getLevel(faction) * HEALTH_UPGRADE_DELTA;
        }

        return maxHealth;
    }

    /**
     *
     * @return
     */
    public float getHealth() {
        return health;
    }

    /**
     *
     * @param health
     */
    public void setHealth(float health) {
        this.health = health;
    }

    /**
     *
     * @return
     */
    private float getMeleeDamage() {
        float meleeDamage =
                adjustBonus(adjustLevel(getUnitClass().meleeAttackDamage()), attackDamageBonus);

        // Apply upgrade
        if (getUnitClass() == UnitClass.FIGHTER) {
            meleeDamage = adjustBonus(meleeDamage,
                    Upgradable.FIGHTER_MELEE_DAMAGE.getLevel(faction) * RANGED_DAMAGE_UPGRADE_DELTA);
        } else if (getUnitClass() == UnitClass.HORSE_MAN) {
            meleeDamage = adjustBonus(meleeDamage,
                    Upgradable.HORSE_MAN_MELEE_DAMAGE.getLevel(faction) * RANGED_DAMAGE_UPGRADE_DELTA);
        } else if (getUnitClass() == UnitClass.PALADIN) {
            meleeDamage = adjustBonus(meleeDamage,
                    Upgradable.PALADIN_MELEE_DAMAGE.getLevel(faction) * RANGED_DAMAGE_UPGRADE_DELTA);
        }
        return meleeDamage;
    }

    /**
     *
     * @return
     */
    private float getRangedDamage() {

        float rangedDamage =
                adjustBonus(adjustLevel(getUnitClass().rangedAttackDamage()), attackDamageBonus);

        // Apply upgrade
        if (getUnitClass() == UnitClass.ARCHER) {
            rangedDamage = adjustBonus(rangedDamage,
                    Upgradable.ARCHER_RANGED_DAMAGE.getLevel(faction) * MELEE_DAMAGE_UPGRADE_DELTA);
        } else if (getUnitClass() == UnitClass.CANNON) {
            rangedDamage = adjustBonus(rangedDamage,
                    Upgradable.CANNON_RANGED_DAMAGE.getLevel(faction) * MELEE_DAMAGE_UPGRADE_DELTA);
        }
        return rangedDamage;
    }

    /**
     *
     * @return
     */
    private int getMeleeAttackSpeed() {

        float meleeAttackSpeed =
                adjustBonus(getUnitClass().meleeAttackSpeed(), attackSpeedBonus);

        // Apply upgrade
        if (getUnitClass() == UnitClass.FIGHTER) {
            meleeAttackSpeed = adjustBonus(meleeAttackSpeed,
                    -Upgradable.FIGHTER_MELEE_ATTACK_SPEED.getLevel(faction) * ATTACK_SPEED_UPGRADE_DELTA);
        } else if (getUnitClass() == UnitClass.HORSE_MAN) {
            meleeAttackSpeed = adjustBonus(meleeAttackSpeed,
                    -Upgradable.HORSE_MAN_MELEE_ATTACK_SPEED.getLevel(faction) * ATTACK_SPEED_UPGRADE_DELTA);
        }

        return (int) meleeAttackSpeed;
    }

    /**
     *
     * @return
     */
    private int getRangedAttackSpeed() {

        float rangedAttackSpeed =
                adjustBonus(getUnitClass().rangedAttackSpeed(), attackSpeedBonus);

        // Apply upgrade
        if (getUnitClass() == UnitClass.ARCHER) {
            rangedAttackSpeed = adjustBonus(rangedAttackSpeed,
                    -Upgradable.ARCHER_RANGED_ATTACK_SPEED.getLevel(faction) * ATTACK_SPEED_UPGRADE_DELTA);
        } else if (getUnitClass() == UnitClass.CANNON) {
            rangedAttackSpeed = adjustBonus(rangedAttackSpeed,
                    -Upgradable.CANNON_RANGED_ATTACK_SPEED.getLevel(faction) * ATTACK_SPEED_UPGRADE_DELTA);
        }

        return (int) rangedAttackSpeed;
    }

    /**
     *
     * @return
     */
    private float getMeleeAttackRange() {

        if (Upgradable.ARCHER_POINT_BLANK.getLevel(faction) > 0 &&
                getUnitClass() == UnitClass.ARCHER) {
            return 0.0f;
        } else {
            return getUnitClass().meleeAttackRange();
        }
    }

    /**
     *
     * @return
     */
    private float getHealPower() {

        float healPower = adjustBonus(adjustLevel(getUnitClass().healPower()), healPowerBonus);

        // Apply upgrade
        if (getUnitClass() == UnitClass.HEALER) {
            healPower = adjustBonus(healPower,
                    Upgradable.HEALER_HEAL_POWER.getLevel(faction) * HEAL_POWER_UPGRADE_DELTA);
        }

        return healPower;
    }

    /**
     *
     * @return
     */
    private float getMeleeEvasion() {

        float meleeEvasion = adjustLevel(getUnitClass().meleeEvasion() / 100);

        // Apply upgrade
        if (getUnitClass() == UnitClass.FIGHTER) {
            meleeEvasion += Upgradable.FIGHTER_MELEE_EVASION.getLevel(faction) * EVASION_UPGRADE_DELTA;
        } else if (getUnitClass() == UnitClass.ARCHER) {
            meleeEvasion += Upgradable.ARCHER_MELEE_EVASION.getLevel(faction) * EVASION_UPGRADE_DELTA;
        } else if (getUnitClass() == UnitClass.HEALER) {
            meleeEvasion += Upgradable.HEALER_MELEE_EVASION.getLevel(faction) * EVASION_UPGRADE_DELTA;
        } else if (getUnitClass() == UnitClass.PALADIN) {
            meleeEvasion += Upgradable.PALADIN_MELEE_EVASION.getLevel(faction) * EVASION_UPGRADE_DELTA;
        }

        return meleeEvasion;
    }

    /**
     *
     * @return
     */
    private float getRangedEvasion() {

        float rangedEvasion = adjustLevel(getUnitClass().rangedEvasion() / 100);

        // Apply upgrade
        if (getUnitClass() == UnitClass.HORSE_MAN) {
            rangedEvasion += Upgradable.HORSE_MAN_RANGED_EVASION.getLevel(faction) * EVASION_UPGRADE_DELTA;
        } else if (getUnitClass() == UnitClass.HEALER) {
            rangedEvasion += Upgradable.HEALER_RANGED_EVASION.getLevel(faction) * EVASION_UPGRADE_DELTA;
        } else if (getUnitClass() == UnitClass.PALADIN) {
            rangedEvasion += Upgradable.PALADIN_RANGED_EVASION.getLevel(faction) * EVASION_UPGRADE_DELTA;
        }

        return rangedEvasion;
    }

    /**
     *
     * @return
     */
    private float getArmor() {
        float armor = adjustBonus(adjustLevel(getUnitClass().armor() / 100), armorBonus);

        // Apply upgrade
        if (getUnitClass() == UnitClass.FIGHTER) {
            armor += Upgradable.FIGHTER_ARMOR.getLevel(faction) * ARMOR_UPGRADE_DELTA;
        } else if (getUnitClass() == UnitClass.ARCHER) {
            armor += Upgradable.ARCHER_ARMOR.getLevel(faction) * ARMOR_UPGRADE_DELTA;
        } else if (getUnitClass() == UnitClass.HORSE_MAN) {
            armor += Upgradable.HORSE_MAN_ARMOR.getLevel(faction) * ARMOR_UPGRADE_DELTA;
        } else if (getUnitClass() == UnitClass.CANNON) {
            armor += Upgradable.CANNON_ARMOR.getLevel(faction) * ARMOR_UPGRADE_DELTA;
        }

        return armor;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getTargetMapPosition() {

        return targetMapPosition;
    }

    /**
     *
     * @param targetMapPosition
     */
    public void setTargetMapOffset(OffsetCoord targetMapPosition) {

        this.targetMapPosition = targetMapPosition;
    }

    /**
     *
     * @return
     */
    public ArrayList<Unit> getCompanions() {
        return companions;
    }

    /**
     *
     * @param companions
     */
    public void setCompanions(ArrayList<Unit> companions) {
        this.companions = companions;
    }

    /**
     *
     * @return
     */
    public ArrayList<Unit> getOpponents() {
        return opponents;
    }

    /**
     *
     * @param opponents
     */
    public void setOpponents(ArrayList<Unit> opponents) {
        this.opponents = opponents;
    }

    /**
     *
     * @return
     */
    public UnitClass getUnitClass() {

        return unitClass;
    }

    /**
     *
     * @return
     */
    public int getLevel() {

        return level;
    }

    /**
     *
     * @param level
     */
    public void setLevel(int level) {

        this.level = level;
    }

    /**
     *
     */
    public void addExp(int expEarned) {
        if (isRecruiting()) {
            return;
        }

        exp += expEarned;
        if (level < MAX_LEVEL && exp > getUnitClass().requiredExpToLevelUp(level)) {
            exp = 0;
            level++;

            if (focused) {
                Engine2D.GetInstance().playSoundEffect("levelup", 1.0f);
            }

            // Level up effect
            Sprite effectSprite = getSprite("effect");
            effectSprite.clearAnimation();
            effectSprite.addAnimationFrame(2, 0, 15);
            effectSprite.addAnimationFrame(3, 0, 15);
            effectSprite.addAnimationFrame(4, 0, 10);
            effectSprite.addAnimationFrame(5, 0, 10);
            effectSprite.addAnimationFrame(0, 0, 10);
        }
    }

    /**
     *
     * @return
     */
    public boolean isRecruiting() {
        return recruitingTimeLeft > 0;
    }

    /**
     *
     * @return
     */
    public boolean isFighting() {
        return opponents != null;
    }

    /**
     *
     * @return
     */
    public int getGoldUpkeep() {
        return unitClass.costUpkeep();
    }

    /**
     *
     * @return
     */
    public int getPopulationUpkeep() {
        return unitClass.population();
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
     * @param bonus
     */
    public void setHealthBonus(float bonus) {
        this.healthBonus = bonus;
    }

    /**
     *
     * @param bonus
     */
    public void setAttackDamageBonus(float bonus) {
        this.attackDamageBonus = bonus;
    }

    /**
     *
     * @param bonus
     */
    public void setAttackSpeedBonus(float bonus) {
        this.attackSpeedBonus = bonus;
    }

    /**
     *
     * @param bonus
     */
    public void setHealPowerBonus(float bonus) {
        this.healPowerBonus = bonus;
    }

    /**
     *
     * @param bonus
     */
    public void setArmorBonus(float bonus) {
        this.armorBonus = bonus;
    }

    /**
     *
     * @param percentage
     */
    public void rest(float percentage) {
        if (health < getMaxHealth() && restingTimeLeft-- == 0) {
            health = Math.min(health + getMaxHealth() * percentage, getMaxHealth());

            // Resting effect
            Sprite effectSprite = getSprite("effect");
            effectSprite.clearAnimation();
            effectSprite.addAnimationFrame(8, 0, 15);
            effectSprite.addAnimationFrame(9, 0, 15);
            effectSprite.addAnimationFrame(10, 0, 10);
            effectSprite.addAnimationFrame(11, 0, 10);
            effectSprite.addAnimationFrame(0, 0, 10);

            restingTimeLeft = RESTING_TIME;
        }
    }

    /**
     *
     * @return
     */
    public boolean isInvincible() {
        return invincibleTimeLeft > 0;
    }

    /**
     *
     * @param invisible
     */
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    /**
     *
     * @return
     */
    public boolean isInvisible() {
        return invisible;
    }

    /**
     *
     * @param focused
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    /**
     *
     * @return
     */
    public boolean isFocused() {
        return focused;
    }

    private final static int MAX_LEVEL = 10;
    private final static int SPRITE_LAYER = 7;
    private final static int RECRUITING_TIME = 120;
    private final static int RESTING_TIME = 30;
    private final static float SPLASH_DAMAGE_RANGE = 30.0f;
    private final static float SPLASH_DAMAGE_DELTA = 0.1f;
    private final static float SPLASH_DAMAGE_RANGE_DELTA = 0.05f;
    private final static float MELEE_DAMAGE_UPGRADE_DELTA = 0.05f;
    private final static float RANGED_DAMAGE_UPGRADE_DELTA = 0.03f;
    private final static float ATTACK_SPEED_UPGRADE_DELTA = 0.05f;
    private final static float HEAL_POWER_UPGRADE_DELTA = 0.05f;
    private final static float EVASION_UPGRADE_DELTA = 0.03f;
    private final static float ARMOR_UPGRADE_DELTA = 0.03f;
    private final static float CRITICAL_ATTACK_RATE_DELTA = 0.05f;
    private final static float BUFF_DELTA = 0.03f;
    private final static float DOT_DELTA = 0.01f;
    private final static float HORSEMAN_MOVE_SPEED_DELTA = 0.05f;
    private final static float CANNON_MOVE_SPEED_DELTA = 0.1f;
    private final static float STUN_DELTA = 0.03f;
    private final static float SLOW_DELTA = 0.03f;
    private final static float HEALTH_UPGRADE_DELTA = 0.03f;
    private final static int DOT_DURATION = 30;
    private final static int STUN_DURATION = 60;
    private final static int SLOW_DURATION = 60;
    private final static float INVINCIBLE_DURATION_DELTA = 0.05f;
    private final static float INVINCIBLE_HEALTH_THRESHOLD = 0.2f;
    private final static int INVINCIBLE_DURATION = 120;
    private final static int INVINCIBLE_COOLDOWN = 400;
    private final static float GUARDIAN_DAMAGE_DELTA = 0.1f;

    private GameMap map;
    private UnitClass unitClass;
    private int level;
    private int exp;
    private float health;
    private ArrayList<Unit> companions;
    private ArrayList<Unit> opponents;
    private Tribe.Faction faction;
    private float healthBonus = 0.0f;
    private float attackDamageBonus = 0.0f;
    private float attackSpeedBonus = 0.0f;
    private float healPowerBonus = 0.0f;
    private float armorBonus = 0.0f;

    private OffsetCoord targetMapPosition;
    private int recruitingTimeLeft = RECRUITING_TIME;
    private int meleeAttackTimeLeft = 0;
    private int rangedAttackTimeLeft = 0;
    private int healTimeLeft = 0;
    private int restingTimeLeft = RESTING_TIME;
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private ArrayList<Pair<Float, Integer>> dotDamages = new ArrayList<>();
    private int slowTimeLeft = 0;
    private float slowness = 0.0f;
    private int stunTimeLeft = 0;
    private int invincibleTimeLeft = 0;
    private int invincibleCooldownLeft = 0;
    private boolean invisible = false;
    private boolean focused = false;
}
