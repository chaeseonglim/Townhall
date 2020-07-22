package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Shape;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;

public class Unit extends CollidableObject implements Projectile.Event {

    private static final String LOG_TAG = "Unit";

    enum UnitClassType {
        MELEE_FIGHTER("근접 전투형"),
        MELEE_SUPPORTER("근접 지원형"),
        MELEE_HEALER("근접 회복형"),
        RANGED_FIGHTER("원거리 전투형"),
        RANGED_SUPPORTER("원거리 지원형"),
        RANGED_HEALER("원거리 회복형");

        private String word;

        UnitClassType(String word) {
            this.word = word;
        }

        String word() {
            return word;
        }
    };

    enum UnitClass {
        SWORD_MAN(
                "검병",
                UnitClassType.MELEE_FIGHTER,
                "균형잡힌 근접 보병입니다.\n모든 면에서 무난합니다.",
                "균형잡힘",
                "기병",
                new Point(1, 0),
                new SizeF(16, 16),
                new Shape(8.0f),
                200,
                50,
                5,
                new float[] {0.1f, 0.2f, 0.2f, 0.4f, 0.4f, 0.5f},
                150.0f,
                26.0f,
                0.0f,
                0.0f,
                30,
                0,
                10,
                0,
                0,
                20,
                0,
                5,
                20,
                100,
                10,
                null,
                2.0f,
                3.0f,
                5.0f,
                false
        ),
        LONGBOW_ARCHER(
                "궁수",
                UnitClassType.RANGED_FIGHTER,
                "원거리 공격이 가능하며\n전투 및 지원에 적합합니다.",
                "원거리 공격",
                "근접 방어",
                new Point(2, 0),
                new SizeF(16, 16),
                new Shape(8.0f),
                200,
                50,
                5,
                new float[] {-0.5f, -0.3f, -0.3f, -0.1f, -0.1f, -0.1f},
                64.0f,
                26.0f,
                150.0f,
                0.0f,
                50,
                30,
                0,
                3,
                5,
                0,
                5,
                5,
                5,
                50,
                10,
                Projectile.ProjectileType.ARROW,
                2.0f,
                2.0f,
                4.0f,
                true
        ),
        WORKER(
                "일꾼",
                UnitClassType.RANGED_SUPPORTER,
                "지역 발전 속도를 높이며\n수입에도 기여합니다.\n하지만 전투 능력이 없습니다.",
                "지역 발전",
                "전투능력 없음",
                new Point(3, 0),
                new SizeF(16, 16),
                new Shape(8.0f),
                200,
                30,
                5,
                new float[] {-0.5f, -0.3f, -0.3f, -0.1f, -0.1f, -0.1f},
                10.0f,
                26.0f,
                0.0f,
                0.0f,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                30,
                5,
                null,
                1.5f,
                1.5f,
                3.0f,
                false
                ),
        HEALER(
                "치유사",
                UnitClassType.RANGED_HEALER,
                "주변 유닛을 치유합니다.\n하지만 전투 능력이 없습니다.",
                "치유력",
                "전투능력 없음",
                new Point(4, 0),
                new SizeF(16, 16),
                new Shape(8.0f),
                500,
                50,
                10,
                new float[] {-0.5f, -0.3f, -0.3f, -0.1f, -0.1f, -0.1f},
                10.0f,
                26.0f,
                0.0f,
                150.0f,
                0,
                0,
                30,
                0,
                0,
                3,
                0,
                5,
                0,
                50,
                20,
                Projectile.ProjectileType.ARROW,
                1.5f,
                1.5f,
                3.0f,
                true
                );

        private UnitClassType unitClassType;
        private String word;
        private String description;
        private String strongPoint;
        private String weaknessPoint;
        private Point spriteGridIndex;
        private SizeF spriteSize;
        private Shape shape;
        private int costToPurchase;
        private int costUpkeep;
        private int population;
        private float[] favors;
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
        private Projectile.ProjectileType projectileType;
        private float maxVelocity;
        private float maxForce;
        private float mass;
        private boolean supportable;

        UnitClass(String word, UnitClassType unitClassType, String description, String strongPoint,
                  String weaknessPoint, Point spriteGridIndex, SizeF spriteSize, Shape shape,
                  int costToPurchase, int costUpkeep, int population, float[] favors, float awareness,
                  float meleeAttackRange, float rangedAttackRange, float healRange,
                  int meleeAttackSpeed, int rangedAttackSpeed, int healSpeed,
                  float meleeAttackDamage, float rangedAttackDamage, float healPower,
                  float meleeEvasion, float rangedEvasion, float armor, float health,
                  float bountyExp, Projectile.ProjectileType projectileType, float maxVelocity,
                  float maxForce, float mass, boolean supportable) {
            this.word = word;
            this.unitClassType = unitClassType;
            this.description = description;
            this.strongPoint = strongPoint;
            this.weaknessPoint = weaknessPoint;
            this.spriteGridIndex = spriteGridIndex;
            this.spriteSize = spriteSize;
            this.shape = shape;
            this.costToPurchase = costToPurchase;
            this.costUpkeep = costUpkeep;
            this.population = population;
            this.favors = favors;
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
            this.bountyExp = bountyExp;
            this.projectileType = projectileType;
            this.maxVelocity = maxVelocity;
            this.maxForce = maxForce;
            this.mass = mass;
            this.supportable = supportable;
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
        public String strongPoint() {
            return strongPoint;
        }
        public String weaknessPoint() {
            return weaknessPoint;
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
        public float favor(UnitClass unitClass) {
            return favors[unitClass.unitClassType.ordinal()];
        }
        public float awareness() {
            return awareness;
        }
        public int seekingFavorRange() {
            return 20;
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
        public int earnedExp(int level) {
            return (int) (bountyExp * (1.0 + level * 0.2));
        }
        public int requiredExp(int level) {
            return 100*level;
        }
        public Projectile.ProjectileType projectileClass() {
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
    }

    public static class Builder {

        private UnitClass unitClass;
        private Tribe.Faction faction;

        private PointF position = new PointF();

        public Builder(UnitClass unitClass, Tribe.Faction faction) {
            this.unitClass = unitClass;
            this.faction = faction;
        }
        public Builder position(PointF position) {
            this.position = position;
            return this;
        }
        public Unit build() {
            Sprite unitEffectSprite = new Sprite.Builder("effect", "unit_effect.png").gridSize(4,1)
                    .size(unitClass.spriteSize().clone().multiply(2.0f)).smooth(true).opaque(0.0f).build();
            Sprite unitClassSprite = new Sprite.Builder("class", "unit_class.png").gridSize(5,1)
                    .size(unitClass.spriteSize()).smooth(true).opaque(0.0f).build();
            unitClassSprite.setGridIndex(unitClass.spriteGridIndex().x, unitClass.spriteGridIndex().y);
            Sprite unitFrameSprite = new Sprite.Builder("frame", "unit_frame.png").gridSize(5,1)
                    .size(unitClass.spriteSize()).smooth(true).opaque(0.0f).build();
            unitFrameSprite.setGridIndex(faction.ordinal(), 0);
            Sprite unitHealthSprite = new Sprite.Builder("health", "unit_health.png").gridSize(9,1)
                    .size(unitClass.spriteSize()).smooth(true).opaque(0.0f).build();
            return (Unit) new PrivateBuilder<>(position, unitClass)
                    .sprite(unitEffectSprite)
                    .sprite(unitClassSprite)
                    .sprite(unitFrameSprite)
                    .sprite(unitHealthSprite)
                    .maxForce(unitClass.maxForce()).maxVelocity(unitClass.maxVelocity())
                    .maxAngularVelocity(0.0f).inertia(Float.MAX_VALUE)
                    .mass(unitClass.mass()).friction(unitClass.friction())
                    .side(faction)
                    .shape(unitClass.shape()).layer(SPRITE_LAYER).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Unit.PrivateBuilder<T>> extends CollidableObject.Builder<T> {

        private UnitClass unitClass;
        private Tribe.Faction faction;

        public PrivateBuilder(PointF position, UnitClass unitClass) {
            super(position);
            this.unitClass = unitClass;
        }
        public PrivateBuilder side(Tribe.Faction faction) {
            this.faction = faction;
            return this;
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
        health = getUnitClass().health();
        level = 1;
    }

    /**
     *
     */
    @Override
    public void update() {

        if (!isRecruiting() && isFighting()) {
            // If it's on battle,  seek or flee enemies
            float highestFavor = -Float.MAX_VALUE, lowestFavor = Float.MAX_VALUE;
            Unit highestFavorUnit = null, lowestFavorUnit = null;
            float highestFavorDistance = Float.MAX_VALUE, lowestFavorDistance = Float.MAX_VALUE;
            for (Unit opponent : opponents) {
                if (opponent.isRecruiting()) {
                    continue;
                }

                float distance = opponent.getPosition().distance(getPosition());
                if (distance <= getUnitClass().awareness()) {
                    float favor = getUnitClass().favor(opponent.getUnitClass());
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
                }
            } else if (lowestFavor < 0.0f && lowestFavorUnit != null) {
                if (lowestFavorDistance < getUnitClass().rangedAttackRange()) {
                    flee(lowestFavorUnit.getPosition(), 1.0f);
                }
            }

            wander(80.0f, 1.0f, 0.1f);
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
        if (projectile != null) {
            projectile.update();
        }

        // Handle recruiting
        if (!isRecruiting()) {
            for (Sprite sprite : getSprites()) {
                sprite.setOpaque(1.0f);
            }
        } else if (opponents == null) {
            recruitingTimeLeft--;
        }
    }

    /**
     *
     */
    @Override
    public void commit() {

        super.commit();

        if (projectile != null) {
            projectile.commit();
        }
    }

    /**
     *
     * @param targetObject
     */
    @Override
    public void onCollisionOccurred(CollidableObject targetObject) {
    }

    /**
     *
     * @param projectile
     */
    @Override
    public void onProjectileReached(Projectile projectile) {
        this.projectile.close();
        this.projectile = null;
    }

    /**
     *
     */
    public void fight() {

        if (isRecruiting())
            return;

        // Do the melee attack
        Unit meleeTarget = searchFavoredMeleeTarget();
        if (unitClass.meleeAttackSpeed() > 0 && meleeAttackLeft == 0) {
            if (meleeTarget != null) {
                attackMelee(meleeTarget);
            }
            meleeAttackLeft = unitClass.meleeAttackSpeed();
        } else {
            meleeAttackLeft--;
        }

        // Do the ranged attack if if doesn't attack melee
        if (meleeTarget == null) {
            if (unitClass.rangedAttackSpeed() > 0 && rangedAttackLeft == 0) {
                Unit rangedTarget = searchFavoredRangedTarget();
                if (rangedTarget != null) {
                    attackRanged(rangedTarget);
                }
                rangedAttackLeft = unitClass.rangedAttackSpeed();
            } else {
                rangedAttackLeft--;
            }
        }

        // Do healing
        if (unitClass.unitClassType() == UnitClassType.MELEE_HEALER ||
            unitClass.unitClassType() == UnitClassType.RANGED_HEALER) {
            if (unitClass.healSpeed() > 0 && healLeft == 0) {
                Unit healTarget = searchFavoredHealTarget();
                if (healTarget != null) {
                    heal(healTarget);
                }
                healLeft = unitClass.healSpeed();
            } else {
                healLeft--;
            }
        }
    }

    /**
     *
     */
    public void support() {

        if (isRecruiting()) {
            return;
        }

        if (unitClass.rangedAttackSpeed() > 0 && rangedAttackLeft == 0) {
            Unit rangedTarget = searchFavoredRangedTarget();
            if (rangedTarget != null) {
                attackRanged(rangedTarget);
            }
            rangedAttackLeft = unitClass.rangedAttackSpeed();
        } else {
            rangedAttackLeft--;
        }

        // Do healing
        if (unitClass.unitClassType() == UnitClassType.MELEE_HEALER ||
                unitClass.unitClassType() == UnitClassType.RANGED_HEALER) {
            if (unitClass.healSpeed() > 0 && healLeft == 0) {
                Unit healTarget = searchFavoredHealTarget();
                if (healTarget != null) {
                    heal(healTarget);
                }
                healLeft = unitClass.healSpeed();
            } else {
                healLeft--;
            }
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
            if (opponent.isRecruiting()) {
                continue;
            }
            if (opponent.getPosition().distance(getPosition()) <=
                    getUnitClass().meleeAttackRange()) {
                float favor = getUnitClass().favor(opponent.getUnitClass());
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

        // Select highest favored enemy for ranged attack
        for (Unit opponent : opponents) {
            if (opponent.isRecruiting()) {
                continue;
            }
            if (opponent.getPosition().distance(getPosition()) <=
                    getUnitClass().rangedAttackRange()) {
                float favor = getUnitClass().favor(opponent.getUnitClass());
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

        // Select highest favored companion for heal
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
     * @param opponent
     */
    private void attackMelee(Unit opponent) {

        // Check evading
        if (Math.random() < opponent.getMeleeEvasion()) {
            return;
        }

        // Deal damage
        float damage = Math.max(getMeleeDamage() * opponent.getArmor(), 1.0f);
        opponent.gotDamage(damage);
    }

    /**
     *
     * @param opponent
     */
    private void attackRanged(Unit opponent) {

        // Create projectile for ranged attack
        // NOTE: This is just an graphical effect yet
        if (projectile != null) {
            projectile.close();
        }
        projectile = new Projectile.Builder(this, unitClass.projectileClass(), opponent).build();
        projectile.setPosition(this.getPosition().clone());
        projectile.setVisible(true);

        // Check evading
        if (Math.random() < opponent.getRangedEvasion()) {
            return;
        }

        // Deal damage
        float damage = Math.max(getRangedDamage() * opponent.getArmor(), 1.0f);
        opponent.gotDamage(damage);
    }

    /**
     *
     * @param companion
     */
    private void heal(Unit companion) {

        // Heal
        companion.gotHeal(getHealPower());
    }

    /**
     *
     * @param damage
     */
    private void gotDamage(float damage) {

        health -= damage;
        if (health < 0) {
            health = 0;
        }

        // Adjust health sprite
        Sprite healthSprite = getSprite("health");
        healthSprite.setGridIndex((int) ((1.0f - health / getMaxHealth()) / 0.1275f), 0);

        // Blinking
        Sprite classSprite = getSprite("class");
        classSprite.clearAnimation();
        classSprite.addAnimationFrame(0, 0, 5);
        classSprite.addAnimationFrame(unitClass.spriteGridIndex().x,
                unitClass.spriteGridIndex().y,5);
        classSprite.addAnimationFrame(0, 0, 5);
        classSprite.addAnimationFrame(unitClass.spriteGridIndex().x,
                unitClass.spriteGridIndex().y,5);
    }

    /**
     *
     * @param healPower
     */
    private void gotHeal(float healPower) {

        health += healPower;
        if (health > getMaxHealth()) {
            health = getMaxHealth();
        }

        // Adjust health sprite
        Sprite healthSprite = getSprite("health");
        healthSprite.setGridIndex((int) ((1.0f - health / getMaxHealth()) / 0.1275f), 0);

        // Healing effect
        Sprite effectSprite = getSprite("effect");
        effectSprite.clearAnimation();
        effectSprite.addAnimationFrame(1, 0, 5);
        effectSprite.addAnimationFrame(2, 0, 5);
        effectSprite.addAnimationFrame(3, 0, 5);
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
    private float adjustByLevel(float value) {

        return value * (1.0f + 0.1f * level);
    }

    /**
     *
     * @param value
     * @return
     */
    private float adjustByDefensiveBonus(float value) {

        return value * (1.0f + 0.05f * defensiveBonus);
    }

    /**
     *
     * @param value
     * @return
     */
    private float adjustByOffensiveBonus(float value) {

        return value * (1.0f + 0.05f * offensiveBonus);
    }

    /**
     *
     * @return
     */
    public float getMaxHealth() {

        return adjustByLevel(getUnitClass().health());
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
     * @return
     */
    private float getMeleeDamage() {

        return adjustByOffensiveBonus(adjustByLevel(getUnitClass().meleeAttackDamage()));
    }

    /**
     *
     * @return
     */
    private float getRangedDamage() {

        return adjustByOffensiveBonus(adjustByLevel(getUnitClass().rangedAttackDamage()));
    }

    /**
     *
     * @return
     */
    private float getHealPower() {

        return adjustByDefensiveBonus(adjustByLevel(getUnitClass().healPower()));
    }

    /**
     *
     * @return
     */
    private float getMeleeEvasion() {

        return adjustByLevel(getUnitClass().meleeEvasion()/100);
    }

    /**
     *
     * @return
     */
    private float getRangedEvasion() {

        return adjustByLevel(getUnitClass().rangedEvasion()/100);
    }

    /**
     *
     * @return
     */
    private float getArmor() {

        return adjustByDefensiveBonus(adjustByLevel(getUnitClass().armor()/100));
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
     */
    public void addExp(int expEarned) {
        if (isRecruiting()) {
            return;
        }

        exp += expEarned;
        if (level < MAX_LEVEL && exp > getUnitClass().requiredExp(level)) {
            exp = 0;
            level++;
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
     * @param defenseBonus
     */
    public void setDefensiveBonus(int defenseBonus) {
        this.defensiveBonus = defenseBonus;
    }

    /**
     *
     * @param offensiveBonus
     */
    public void setOffensiveBonus(int offensiveBonus) {
        this.offensiveBonus = offensiveBonus;
    }

    private final static int MAX_LEVEL = 10;
    private final static int SPRITE_LAYER = 7;
    private final static int RECRUITING_TIME = 300;

    private UnitClass unitClass;
    private int level;
    private int exp;
    private float health;
    private ArrayList<Unit> companions;
    private ArrayList<Unit> opponents;
    private Tribe.Faction faction;
    private int offensiveBonus;
    private int defensiveBonus;

    private int recruitingTimeLeft = RECRUITING_TIME;
    private OffsetCoord targetMapPosition;
    private int meleeAttackLeft = 0;
    private int rangedAttackLeft = 0;
    private int healLeft = 0;
    private Projectile projectile;
}
