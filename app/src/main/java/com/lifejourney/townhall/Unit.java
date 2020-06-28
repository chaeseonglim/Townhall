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
        SWORD,
        LONGBOW;

        Point spriteGridIndex() {
            switch (this) {
                case SWORD:
                    return new Point(1, 0);
                case LONGBOW:
                    return new Point(2, 0);
                default:
                    return null;
            }
        }
        public Shape shape() {
            switch (this) {
                case SWORD:
                case LONGBOW:
                    return new Shape(8.0f);
                default:
                    return null;
            }
        }
        public float favor(UnitClass unitClassType) {
            switch (this) {
                case SWORD:
                    if (unitClassType == SWORD) {
                        return 0.1f;
                    }
                    else if (unitClassType == LONGBOW) {
                        return 0.5f;
                    }
                    break;
                case LONGBOW:
                    if (unitClassType == SWORD) {
                        return -0.5f;
                    }
                    else if (unitClassType == LONGBOW) {
                        return -0.1f;
                    }
                    break;
            }
            return 0.0f;
        }
        public float awareness() {
            switch (this) {
                case SWORD:
                    return 150.0f;
                case LONGBOW:
                    return 64.0f;
            }
            return 0.0f;
        }
        public int closeApproachRange() {
            return 20;
        }
        public float meleeAttackRange() {
            switch (this) {
                case SWORD:
                case LONGBOW:
                    return 26.0f;
            }
            return 0.0f;
        }
        public float rangedAttackRange() {
            switch (this) {
                case SWORD:
                    return 0.0f;
                case LONGBOW:
                    return 150.0f;
            }
            return 0.0f;
        }
        public int meleeAttackSpeed() {
            switch (this) {
                case SWORD:
                    return 30;
                case LONGBOW:
                    return 40;
            }
            return 0;
        }
        public int rangedAttackSpeed() {
            switch (this) {
                case SWORD:
                    return 0;
                case LONGBOW:
                    return 40;
            }
            return 0;
        }
        public float meleeDamage() {
            switch (this) {
                case SWORD:
                    return 10.0f;
                case LONGBOW:
                    return 3.0f;
            }
            return 0.0f;
        }
        public float rangedDamage() {
            switch (this) {
                case SWORD:
                    return 0.0f;
                case LONGBOW:
                    return 5.0f;
            }
            return 0.0f;
        }
        public float meleeEvasion() {
            switch (this) {
                case SWORD:
                    return 0.2f;
                case LONGBOW:
                    return 0.05f;
            }
            return 0.0f;
        }
        public float rangedEvasion() {
            switch (this) {
                case SWORD:
                case LONGBOW:
                    return 0.1f;
            }
            return 0.0f;
        }
        public float armor() {
            switch (this) {
                case SWORD:
                    return 0.2f;
                case LONGBOW:
                    return 0.05f;
            }
            return 0.0f;
        }
        public int earnedExp(int level) {
            int expEarned = 0;
            switch (this) {
                case SWORD:
                    expEarned = 10;
                    break;
                case LONGBOW:
                    expEarned = 10;
                    break;
            }
            return (int) (expEarned * (1.0 + level*0.2));
        }
        public int requiredExp(int level) {
            return 100*level;
        }
        public int maxHealth() {
            switch (this) {
                case SWORD:
                    return 100;
                case LONGBOW:
                    return 60;
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
            return 3.0f;
        }
        public float maxVelocity() {
            return 2.0f;
        }
        public float mass() {
            return 5.0f;
        }
    }

    public static class Builder {

        private UnitClass unitClass;
        private Town.Side side;

        private PointF position = new PointF();

        public Builder(UnitClass unitClass, Town.Side side) {
            this.unitClass = unitClass;
            this.side = side;
        }
        public Builder position(PointF position) {
            this.position = position;
            return this;
        }
        public Unit build() {
            Sprite unitClassSprite = new Sprite.Builder("class", "unit_class.png").gridSize(5,1)
                    .size(new SizeF(16, 16)).smooth(true).build();
            unitClassSprite.setGridIndex(unitClass.spriteGridIndex().x, unitClass.spriteGridIndex().y);
            Sprite unitFrameSprite = new Sprite.Builder("frame", "unit_frame.png").gridSize(4,1)
                    .size(new SizeF(16, 16)).smooth(true).build();
            unitFrameSprite.setGridIndex(side.ordinal(), 0);
            Sprite unitHealthSprite = new Sprite.Builder("health", "unit_health.png").gridSize(5,1)
                    .size(new SizeF(16, 16)).smooth(true).build();
            unitFrameSprite.setGridIndex(side.ordinal(), 0);
            return (Unit) new PrivateBuilder<>(position, unitClass)
                    .sprite(unitClassSprite)
                    .sprite(unitFrameSprite)
                    .sprite(unitHealthSprite)
                    .maxForce(unitClass.maxForce()).maxVelocity(unitClass.maxVelocity())
                    .maxAngularVelocity(0.0f).inertia(Float.MAX_VALUE)
                    .mass(unitClass.mass()).friction(unitClass.friction())
                    .side(side)
                    .shape(unitClass.shape()).layer(SPRITE_LAYER).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Unit.PrivateBuilder<T>> extends CollidableObject.Builder<T> {

        private UnitClass unitClass;
        private Town.Side side;

        public PrivateBuilder(PointF position, UnitClass unitClass) {
            super(position);
            this.unitClass = unitClass;
        }
        public PrivateBuilder side(Town.Side side) {
            this.side = side;
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
        side = builder.side;
        level = 1;
        health = getUnitClass().maxHealth();
    }

    /**
     *
     */
    @Override
    public void update() {

        // If it's on battle
        if (opponents != null) {
            // Seek or flee enemies
            float highestFavor = -Float.MAX_VALUE, lowestFavor = Float.MAX_VALUE;
            Unit highestFavorUnit = null, lowestFavorUnit = null;
            float highestFavorDistance = Float.MAX_VALUE, lowestFavorDistance = Float.MAX_VALUE;
            for (Unit opponent : opponents) {
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
                if (highestFavorDistance > getUnitClass().closeApproachRange()) {
                    seek(highestFavorUnit.getPosition(), 1.0f);
                }
            }
            else if (lowestFavor < 0.0f && lowestFavorUnit != null) {
                if (lowestFavorDistance < getUnitClass().rangedAttackRange()) {
                    flee(lowestFavorUnit.getPosition(), 1.0f);
                }
            }

            wander(80.0f, 1.0f, 0.1f);
        }

        // if it's at peace
        else {
            // Seek to target position
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

        if (projectile != null) {
            projectile.update();
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

        Unit collidedUnit = (Unit) targetObject;

        // if collided unit is enemy, stop and fight
        if (opponents.contains(collidedUnit)) {
            if (closedOpponents == null) {
                closedOpponents = new ArrayList<>();
            }
            closedOpponents.add(collidedUnit);
        }
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

        // Find favored enemy for attacking target
        Unit targetCandidate = null;
        float highestFavor = -Float.MAX_VALUE;
        int highestHealth = Integer.MAX_VALUE;
        boolean enemyExistInClosedArea = false;
        for (Unit opponent : opponents) {
            if (opponent.getPosition().distance(getPosition()) <=
                    getUnitClass().meleeAttackRange()) {
                enemyExistInClosedArea = true;
                if (meleeAttackLeft > 0) {
                    break;
                }

                float favor = getUnitClass().favor(opponent.getUnitClass());
                if (favor > highestFavor ||
                        (favor == highestFavor && opponent.getHealth() < highestHealth)) {
                    targetCandidate = opponent;
                    highestFavor = favor;
                    highestHealth = opponent.getHealth();
                }
            }
        }

        // Check if melee attack is possible
        if (meleeAttackLeft > 0) {
            meleeAttackLeft--;
        }
        else if (targetCandidate != null) {
            // Do melee attack
            attackMelee(targetCandidate);
            meleeAttackLeft = getUnitClass().meleeAttackSpeed();
        }

        // Check if ranged attack is possible
        if (rangedAttackLeft > 0) {
            rangedAttackLeft--;
        }
        else if (!enemyExistInClosedArea && getUnitClass().rangedAttackRange() > 0.0f) {
            // Search enemy target for ranged attack
            targetCandidate = null;
            highestFavor = -Float.MAX_VALUE;
            highestHealth = Integer.MAX_VALUE;

            // Select highest favored enemy
            for (Unit opponent : opponents) {
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

            if (targetCandidate != null) {
                attackRanged(targetCandidate);
                rangedAttackLeft = getUnitClass().rangedAttackSpeed();
            }
        }
    }

    /**
     *
     * @param opponent
     */
    private void attackMelee(Unit opponent) {

        Sprite classSprite = getSprite(0);
        classSprite.clearAnimation();
        classSprite.addAnimationFrame(0, 0, 5);
        classSprite.addAnimationFrame(unitClass.spriteGridIndex().x, unitClass.spriteGridIndex().y,
                5);
        classSprite.addAnimationFrame(0, 0, 5);
        classSprite.addAnimationFrame(unitClass.spriteGridIndex().x, unitClass.spriteGridIndex().y,
                5);

        // Check evading
        if (Math.random() < opponent.getMeleeEvasion()) {
            return;
        }

        // Deal damage
        int damage = Math.max((int) (getMeleeDamage() * opponent.getArmor()), 1);
        opponent.gotDamage(damage);
    }

    /**
     *
     * @param opponent
     */
    private void attackRanged(Unit opponent) {

        // Create projectile for ranged attack
        // Currently projectile is just an fake
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
        healthSprite.setGridIndex((int) ((1.0f - health / (float)getMaxHealth()) / 0.25f), 0);
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
     * @return
     */
    public int getMaxHealth() {

        return adjustByLevel(getUnitClass().maxHealth());
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

        return adjustByLevel(getUnitClass().armor());
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
        exp += expEarned;
        if (level < MAX_LEVEL && exp > getUnitClass().requiredExp(level)) {
            exp = 0;
            level++;
        }
    }

    private final static int MAX_LEVEL = 10;
    private final static int SPRITE_LAYER = 7;

    private UnitClass unitClass;
    private int level;
    private int exp;
    private int health;
    private ArrayList<Unit> companions;
    private ArrayList<Unit> opponents;
    private ArrayList<Unit> closedOpponents;
    private Town.Side side;

    private OffsetCoord targetMapPosition;
    private int meleeAttackLeft = 0;
    private int rangedAttackLeft = 0;
    private Projectile projectile;
}
