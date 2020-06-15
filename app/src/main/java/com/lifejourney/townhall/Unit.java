package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Size;
import com.lifejourney.engine2d.Sprite;

public class Unit extends CollidableObject {

    enum Class {
        SWORD,
        LONGBOW;

        Class() {
        }

        Sprite sprite() {
            return new Sprite.Builder("unit.png").gridSize(new Size(2,1))
                    .size(new Size(32, 32)).smooth(false).layer(4).build();
        }

    }

    public static class Builder {

        private Class unitClass;
        private float scale;

        public Builder(Class unitClass, float scale) {
            this.unitClass = unitClass;
            this.scale = scale;
        }
        public Unit build() {
            return new Unit.PrivateBuilder<>(new PointF(), unitClass)
                    .sprite(unitClass.sprite()).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Unit.PrivateBuilder<T>> extends CollidableObject.Builder<T> {

        private Class unitClass;

        public PrivateBuilder(PointF position, Class unitClass) {
            super(position);
            this.unitClass = unitClass;
        }
        public Unit build() {
            return new Unit(this);
        }
    }

    private Unit(PrivateBuilder builder) {

        super(builder);

        unitClass = builder.unitClass;
        level = 1;
    }

    private final static int MAX_LEVEL = 10;

    private Class unitClass;
    private int level;
    private float meleeDamage;
    private float rangeDamage;
    private float meleeBlock;
    private float rangeBlock;
    private float attackSpeed;
    private float armor;
    private float maxHealth;
    private float health;
}
