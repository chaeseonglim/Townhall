package com.lifejourney.townhall;

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

        Sprite sprite(float scale) {
            Sprite sprite = new Sprite.Builder("unit.png").gridSize(new Size(4,2))
                    .size(new Size(16, 16).multiply(scale)).smooth(true).build();
            sprite.setGridIndex(spriteGridIndex());
            return sprite;
        }
        Point spriteGridIndex() {
            switch (this) {
                case SWORD:
                    return new Point(0, 1);
                case LONGBOW:
                    return new Point(1, 1);
                default:
                    return null;
            }
        }
        public Shape shape(float scale) {
            switch (this) {
                case SWORD:
                case LONGBOW:
                    return new Shape(8.0f).multiply(scale);
                default:
                    return null;
            }
        }
        public float favor(UnitClass unitClassType) {
            float favor = 0.0f;
            switch (this) {
                case SWORD:
                    if (unitClassType == SWORD) {
                        favor = 0.1f;
                    }
                    else if (unitClassType == LONGBOW) {
                        favor = 0.5f;
                    }
                    break;
                case LONGBOW:
                    if (unitClassType == SWORD) {
                        favor = -0.5f;
                    }
                    else if (unitClassType == LONGBOW) {
                        favor = 0.0f;
                    }
                    break;
                default:
                    break;
            }

            return favor;
        }
        public float maxForce(float scale) {
            return 2.0f * scale;
        }
        public float maxVelocity(float scale) {
            return 1.0f * scale;
        }
        public float mass(float scale) {
            return 5.0f * scale;
        }
    }

    public static class Builder {

        private UnitClass unitClass;
        private float scale;
        private Town.Side side;

        private PointF position = new PointF();

        public Builder(UnitClass unitClass, float scale, Town.Side side) {
            this.unitClass = unitClass;
            this.scale = scale;
            this.side = side;
        }
        public Builder position(PointF position) {
            this.position = position;
            return this;
        }
        public Unit build() {
            Sprite spriteFrame = new Sprite.Builder("unit.png").gridSize(new Size(4,2))
                    .size(new Size(16, 16).multiply(scale)).depth(1.0f).smooth(true).build();
            spriteFrame.setGridIndex(new Point(side.ordinal(), 0));
            return (Unit) new PrivateBuilder<>(position, unitClass)
                    .sprite(unitClass.sprite(scale))
                    .sprite(spriteFrame)
                    .maxForce(unitClass.maxForce(scale)).maxVelocity(unitClass.maxVelocity(scale))
                    .maxAngularVelocity(0.0f).inertia(Float.MAX_VALUE)
                    .mass(unitClass.mass(scale)).friction(0.1f)
                    .side(side)
                    .shape(unitClass.shape(scale)).layer(7).build();
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
    }

    /**
     *
     */
    @Override
    public void update() {

        if (getOpponents() != null) {
            for (Unit opponent: opponents) {
                float favor = getUnitClass().favor(opponent.getUnitClass());
                if (favor > 0.0f) {
                    seek(opponent.getPosition(), favor);
                }
                else if (favor < 0.0f) {
                    flee(opponent.getPosition(), favor);
                }
            }

            // Wander a little
            wander(80.0f, 1.0f, 0.3f);
        }
        else {
            // Seek to target position
            PointF targetPosition = new PointF(targetMapPosition.toGameCoord());
            OffsetCoord currentMapOffset = new OffsetCoord(getPosition());

            if (!currentMapOffset.equals(targetMapPosition)) {
                seek(targetPosition, 1.0f);
            }
            else {
                seek(targetPosition, 0.3f);
            }

            // Wander a little
            wander(80.0f, 1.0f, 0.3f);

            // Separation
            separate(companions, 20.0f, 1.0f);
        }

        // Add gravity to target
        restrict(targetMapPosition);

        super.update();
    }

    @Override
    public void commit() {

        super.commit();
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

    private final static int MAX_LEVEL = 10;

    private UnitClass unitClass;
    private int level;
    private float meleeDamage;
    private float rangeDamage;
    private float meleeBlock;
    private float rangeBlock;
    private float attackSpeed;
    private float armor;
    private float maxHealth;
    private float health;
    private ArrayList<Unit> companions;
    private ArrayList<Unit> opponents;
    private Town.Side side;

    private OffsetCoord targetMapPosition;
}
