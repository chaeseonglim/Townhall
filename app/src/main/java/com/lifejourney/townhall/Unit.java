package com.lifejourney.townhall;

import android.util.Log;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Shape;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;

public class Unit extends CollidableObject {

    private static final String LOG_TAG = "Unit";

    enum UnitClass {
        SWORD,
        LONGBOW;

        UnitClass() {
        }

        Sprite sprite() {
            Sprite sprite = new Sprite.Builder("unit_class.png").gridSize(new Size(4,1))
                    .size(new Size(16, 16)).smooth(true).build();
            sprite.setGridIndex(spriteGridIndex());
            return sprite;
        }
        Point spriteGridIndex() {
            switch (this) {
                case SWORD:
                    return new Point(0, 0);
                case LONGBOW:
                    return new Point(1, 0);
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
                    return 96.0f;
                case LONGBOW:
                    return 64.0f;
            }
            return 0.0f;
        }
        public float meleeAttackRange() {
            switch (this) {
                case SWORD:
                case LONGBOW:
                    return 24.0f;
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
                    return 10;
                case LONGBOW:
                    return 15;
            }
            return 0;
        }
        public int rangedAttackSpeed() {
            switch (this) {
                case SWORD:
                    return 0;
                case LONGBOW:
                    return 30;
            }
            return 0;
        }
        public int expEarned(int level) {
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
        public int expRequired(int level) {
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
        public float meleeDamage() {
            switch (this) {
                case SWORD:
                    return 20.0f;
                case LONGBOW:
                    return 5.0f;
            }
            return 0.0f;
        }
        public float rangedDamage() {
            switch (this) {
                case SWORD:
                    return 0.0f;
                case LONGBOW:
                    return 10.0f;
            }
            return 0.0f;
        }
        public float meleeEvade() {
            switch (this) {
                case SWORD:
                    return 0.2f;
                case LONGBOW:
                    return 0.05f;
            }
            return 0.0f;
        }
        public float rangedEvade() {
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
                    return 0.1f;
            }
            return 0.0f;
        }
        public float maxForce() {
            return 2.0f;
        }
        public float maxVelocity() {
            return 1.0f;
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
            Sprite unitFrameSprite = new Sprite.Builder("unit_frame.png").gridSize(new Size(4,1))
                    .size(new Size(16, 16)).depth(1.0f).smooth(true).build();
            unitFrameSprite.setGridIndex(new Point(side.ordinal(), 0));
            Sprite unitHealthSprite = new Sprite.Builder("unit_health.png").gridSize(new Size(5,1))
                    .size(new Size(16, 16)).depth(1.0f).smooth(true).build();
            unitFrameSprite.setGridIndex(new Point(side.ordinal(), 0));
            return (Unit) new PrivateBuilder<>(position, unitClass)
                    .sprite(unitClass.sprite())
                    .sprite(unitFrameSprite)
                    .sprite(unitHealthSprite)
                    .maxForce(unitClass.maxForce()).maxVelocity(unitClass.maxVelocity())
                    .maxAngularVelocity(0.0f).inertia(Float.MAX_VALUE)
                    .mass(unitClass.mass()).friction(0.1f)
                    .side(side)
                    .shape(unitClass.shape()).layer(7).build();
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
            for (Unit opponent : opponents) {
                if (opponent.getPosition().distance(getPosition()) <= getUnitClass().awareness()) {
                    float favor = getUnitClass().favor(opponent.getUnitClass());
                    if (favor > 0.0f) {
                        seek(opponent.getPosition(), favor);
                    } else if (favor < 0.0f) {
                        flee(opponent.getPosition(), -favor);
                    }
                }
            }

            // Wander a little
            wander(80.0f, 1.0f, 0.3f);
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
     */
    public void fight() {

        // Search close enemies first
        Unit targetCandidate = null;
        float highestFavor = -Float.MAX_VALUE;
        boolean closeEnemyExist = false;
        for (Unit opponent : opponents) {
            if (opponent.getPosition().distance(getPosition()) <= getUnitClass().meleeAttackRange()) {
                closeEnemyExist = true;
                if (meleeAttackLeft > 0) {
                    break;
                }

                float favor = getUnitClass().favor(opponent.getUnitClass());
                if (favor > highestFavor) {
                    targetCandidate = opponent;
                }
            }
        }

        // Check if melee attack is possible
        if (meleeAttackLeft > 0) {
            meleeAttackLeft--;
        }
        else if (targetCandidate != null) {
            attackMelee(targetCandidate);
            meleeAttackLeft = getUnitClass().meleeAttackSpeed();
        }

        // Check if ranged attack is possible
        if (rangedAttackLeft > 0) {
            rangedAttackLeft--;
        }
        else if (!closeEnemyExist && getUnitClass().rangedAttackRange() > 0.0f) {
            // Search distanced enemies
            targetCandidate = null;
            highestFavor = -Float.MAX_VALUE;

            for (Unit opponent : opponents) {
                if (opponent.getPosition().distance(getPosition()) <=
                        getUnitClass().rangedAttackRange()) {
                    float favor = getUnitClass().favor(opponent.getUnitClass());
                    if (favor > highestFavor) {
                        targetCandidate = opponent;
                    }
                }
            }

            if (targetCandidate != null) {
                attackRanged(targetCandidate);
                meleeAttackLeft = getUnitClass().rangedAttackSpeed();
            }
        }
    }

    /**
     *
     * @param opponent
     */
    private void attackMelee(Unit opponent) {
        if (Math.random() < opponent.getMeleeEvade()) {
            // evaded
            return;
        }

        int damage = (int) (getMeleeDamage() * opponent.getArmor());
        opponent.gotDamage(damage);
    }

    /**
     *
     * @param opponent
     */
    private void attackRanged(Unit opponent) {
        if (Math.random() < opponent.getRangedEvade()) {
            // evaded
            return;
        }

        int damage = (int) (getRangedDamage() * opponent.getArmor());
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
        healthSprite.setGridIndex(new Point((int)((1.0f - health / (float)getMaxHealth()) / 0.2f), 0));
    }

    /**
     *
     */
    public void setKilled() {

        killed = true;
    }

    /**
     *
     */
    public boolean isKilled() {

        return killed;
    }

    /**
     *
     * @param value
     * @return
     */
    private int adjustLevel(int value) {

        return (int) (value * (1.0f + 0.1f * level));
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
     * @return
     */
    public int getMaxHealth() {

        return adjustLevel(getUnitClass().maxHealth());
    }

    /**
     *
     * @return
     */
    private float getMeleeDamage() {

        return adjustLevel(getUnitClass().meleeDamage());
    }

    /**
     *
     * @return
     */
    private float getRangedDamage() {

        return adjustLevel(getUnitClass().rangedDamage());
    }

    /**
     *
     * @return
     */
    private float getMeleeEvade() {

        return adjustLevel(getUnitClass().meleeEvade());
    }

    /**
     *
     * @return
     */
    private float getRangedEvade() {

        return adjustLevel(getUnitClass().rangedEvade());
    }

    /**
     *
     * @return
     */
    private float getArmor() {

        return adjustLevel(getUnitClass().armor());
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
    public int getHealth() {

        return health;
    }

    private final static int MAX_LEVEL = 10;

    private UnitClass unitClass;
    private int level;
    private int exp;
    private int health;
    private ArrayList<Unit> companions;
    private ArrayList<Unit> opponents;
    private ArrayList<Unit> closedOpponents;
    private Town.Side side;

    private boolean killed = false;
    private OffsetCoord targetMapPosition;
    private int meleeAttackLeft = 0;
    private int rangedAttackLeft = 0;
}
