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

    enum UnitClass {
        SWORDMAN,
        LONGBOWMAN;

        String toGameString() {
            switch (this) {
                case SWORDMAN:
                    return "검병";
                case LONGBOWMAN:
                    return "장궁병";
                default:
                    return "";
            }
        }
        Point spriteGridIndex() {
            switch (this) {
                case SWORDMAN:
                    return new Point(1, 0);
                case LONGBOWMAN:
                    return new Point(2, 0);
                default:
                    return null;
            }
        }
        public Shape shape() {
            switch (this) {
                case SWORDMAN:
                case LONGBOWMAN:
                    return new Shape(8.0f);
                default:
                    return null;
            }
        }
        public int costToPurchase() {
            switch (this) {
                case SWORDMAN:
                    return 200;
                case LONGBOWMAN:
                    return 200;
                default:
                    return 0;
            }
        }
        public int costUpkeep() {
            switch (this) {
                case SWORDMAN:
                    return 50;
                case LONGBOWMAN:
                    return 50;
                default:
                    return 0;
            }
        }
        public int population() {
            switch (this) {
                case SWORDMAN:
                    return 5;
                case LONGBOWMAN:
                    return 5;
                default:
                    return 0;
            }
        }
        public float favor(UnitClass unitClassType) {
            switch (this) {
                case SWORDMAN:
                    if (unitClassType == SWORDMAN) {
                        return 0.1f;
                    }
                    else if (unitClassType == LONGBOWMAN) {
                        return 0.5f;
                    }
                    break;
                case LONGBOWMAN:
                    if (unitClassType == SWORDMAN) {
                        return -0.5f;
                    }
                    else if (unitClassType == LONGBOWMAN) {
                        return -0.1f;
                    }
                    break;
            }
            return 0.0f;
        }
        public float awareness() {
            switch (this) {
                case SWORDMAN:
                    return 150.0f;
                case LONGBOWMAN:
                    return 64.0f;
            }
            return 0.0f;
        }
        public int seekingFavorRange() {
            return 20;
        }
        public float meleeAttackRange() {
            switch (this) {
                case SWORDMAN:
                case LONGBOWMAN:
                    return 26.0f;
            }
            return 0.0f;
        }
        public float rangedAttackRange() {
            switch (this) {
                case SWORDMAN:
                    return 0.0f;
                case LONGBOWMAN:
                    return 150.0f;
            }
            return 0.0f;
        }
        public int meleeAttackSpeed() {
            switch (this) {
                case SWORDMAN:
                    return 30;
                case LONGBOWMAN:
                    return 50;
            }
            return 0;
        }
        public int rangedAttackSpeed() {
            switch (this) {
                case SWORDMAN:
                    return 0;
                case LONGBOWMAN:
                    return 40;
            }
            return 0;
        }
        public float meleeDamage() {
            switch (this) {
                case SWORDMAN:
                    return 10.0f;
                case LONGBOWMAN:
                    return 5.0f;
            }
            return 0.0f;
        }
        public float rangedDamage() {
            switch (this) {
                case SWORDMAN:
                    return 0.0f;
                case LONGBOWMAN:
                    return 5.0f;
            }
            return 0.0f;
        }
        public float meleeEvasion() {
            switch (this) {
                case SWORDMAN:
                    return 0.2f;
                case LONGBOWMAN:
                    return 0.05f;
            }
            return 0.0f;
        }
        public float rangedEvasion() {
            switch (this) {
                case SWORDMAN:
                    return 0.5f;
                case LONGBOWMAN:
                    return 0.1f;
            }
            return 0.0f;
        }
        public float armor() {
            switch (this) {
                case SWORDMAN:
                    return 0.2f;
                case LONGBOWMAN:
                    return 0.05f;
            }
            return 0.0f;
        }
        public int earnedExp(int level) {
            int expEarned = 0;
            switch (this) {
                case SWORDMAN:
                    expEarned = 10;
                    break;
                case LONGBOWMAN:
                    expEarned = 10;
                    break;
            }
            return (int) (expEarned * (1.0 + level*0.2));
        }
        public int requiredExp(int level) {
            return 100*level;
        }
        public int health() {
            switch (this) {
                case SWORDMAN:
                    return 100;
                case LONGBOWMAN:
                    return 50;
            }
            return 0;
        }
        public Projectile.ProjectileClass projectileClass() {
            return Projectile.ProjectileClass.ARROW;
        }
        public float friction() {
            return 0.1f;
        }
        public float maxForce() {
            switch (this) {
                case SWORDMAN:
                    return 3.0f;
                case LONGBOWMAN:
                    return 1.5f;
            }
            return 0;
        }
        public float maxVelocity() {
            switch (this) {
                case SWORDMAN:
                    return 2.0f;
                case LONGBOWMAN:
                    return 2.0f;
            }
            return 0;
        }
        public float mass() {
            switch (this) {
                case SWORDMAN:
                    return 5.0f;
                case LONGBOWMAN:
                    return 3.0f;
            }
            return 0;
        }
    }

    public static class Builder {

        private UnitClass unitClass;
        private Town.Faction faction;

        private PointF position = new PointF();

        public Builder(UnitClass unitClass, Town.Faction faction) {
            this.unitClass = unitClass;
            this.faction = faction;
        }
        public Builder position(PointF position) {
            this.position = position;
            return this;
        }
        public Unit build() {
            Sprite unitClassSprite = new Sprite.Builder("class", "unit_class.png").gridSize(5,1)
                    .size(new SizeF(16, 16)).smooth(true).opaque(0.0f).build();
            unitClassSprite.setGridIndex(unitClass.spriteGridIndex().x, unitClass.spriteGridIndex().y);
            Sprite unitFrameSprite = new Sprite.Builder("frame", "unit_frame.png").gridSize(5,1)
                    .size(new SizeF(16, 16)).smooth(true).opaque(0.0f).build();
            unitFrameSprite.setGridIndex(faction.ordinal(), 0);
            Sprite unitHealthSprite = new Sprite.Builder("health", "unit_health.png").gridSize(9,1)
                    .size(new SizeF(16, 16)).smooth(true).opaque(0.0f).build();
            return (Unit) new PrivateBuilder<>(position, unitClass)
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
        private Town.Faction faction;

        public PrivateBuilder(PointF position, UnitClass unitClass) {
            super(position);
            this.unitClass = unitClass;
        }
        public PrivateBuilder side(Town.Faction faction) {
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
            } else {
                seek(targetPosition, 0.5f);
            }

            // Wander a little
            wander(80.0f, 1.0f, 0.3f);

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
        if (meleeTarget != null) {
            attackMelee(meleeTarget);
        }

        // Do the ranged attack
        if (meleeTarget == null) {
            Unit rangedTarget = searchFavoredRangedTarget();
            if (rangedTarget != null) {
                attackRanged(rangedTarget);
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

        // Do ranged attack
        Unit rangedTarget = searchFavoredRangedTarget();
        if (rangedTarget != null) {
            attackRanged(rangedTarget);
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
        int highestHealth = Integer.MAX_VALUE;
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
        float highestHealth = Integer.MAX_VALUE;

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
     * @param opponent
     */
    private void attackMelee(Unit opponent) {

        if (meleeAttackLeft == 0) {
            // Check evading
            if (Math.random() < opponent.getMeleeEvasion()) {
                return;
            }

            // Deal damage
            int damage = Math.max((int) (getMeleeDamage() * opponent.getArmor()), 1);
            opponent.gotDamage(damage);

            meleeAttackLeft = getUnitClass().meleeAttackSpeed();
        }
        else {
            meleeAttackLeft--;
        }
    }

    /**
     *
     * @param opponent
     */
    private void attackRanged(Unit opponent) {

        if (rangedAttackLeft == 0) {
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
            int damage = Math.max((int) (getRangedDamage() * opponent.getArmor()), 1);
            opponent.gotDamage(damage);

            rangedAttackLeft = getUnitClass().rangedAttackSpeed();
        } else {
            rangedAttackLeft--;
        }
    }

    /**
     *
     * @param damage
     */
    private void gotDamage(int damage) {

        health -= damage;
        if (health < 0) {
            health = 0;
        }

        Sprite healthSprite = getSprite(2);
        healthSprite.setGridIndex((int) ((1.0f - health / (float)getMaxHealth()) / 0.1275f), 0);

        // Blinking
        Sprite classSprite = getSprite(0);
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
     */
    public boolean isKilled() {

        return health <= 0;
    }

    /**
     *
     * @param value
     * @return
     */
    private int adjustByLevel(int value) {

        return (int) (value * (1.0f + 0.1f * level));
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

        return value * (1.0f + 0.1f * defensiveBonus);
    }

    /**
     *
     * @return
     */
    public int getMaxHealth() {

        return adjustByLevel(getUnitClass().health());
    }

    /**
     *
     * @return
     */
    public int getHealth() {

        return health;
    }

    /**
     *
     * @return
     */
    private float getMeleeDamage() {

        return adjustByLevel(getUnitClass().meleeDamage());
    }

    /**
     *
     * @return
     */
    private float getRangedDamage() {

        return adjustByLevel(getUnitClass().rangedDamage());
    }

    /**
     *
     * @return
     */
    private float getMeleeEvasion() {

        return adjustByLevel(getUnitClass().meleeEvasion());
    }

    /**
     *
     * @return
     */
    private float getRangedEvasion() {

        return adjustByLevel(getUnitClass().rangedEvasion());
    }

    /**
     *
     * @return
     */
    private float getArmor() {

        return adjustByDefensiveBonus(adjustByLevel(getUnitClass().armor()));
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
    public Town.Faction getFaction() {
        return faction;
    }

    /**
     *
     * @param defenseBonus
     */
    public void setDefensiveBonus(int defenseBonus) {
        this.defensiveBonus = defenseBonus;
    }

    private final static int MAX_LEVEL = 10;
    private final static int SPRITE_LAYER = 7;
    private final static int RECRUITING_TIME = 300;

    private UnitClass unitClass;
    private int level;
    private int exp;
    private int health;
    private ArrayList<Unit> companions;
    private ArrayList<Unit> opponents;
    private Town.Faction faction;
    private int defensiveBonus;

    private int recruitingTimeLeft = RECRUITING_TIME;
    private OffsetCoord targetMapPosition;
    private int meleeAttackLeft = 0;
    private int rangedAttackLeft = 0;
    private Projectile projectile;
}
