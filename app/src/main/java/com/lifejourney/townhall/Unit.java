package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.PointF;

public class Unit extends CollidableObject {

    enum Class {
        FOOT_SOLDIER,
        CROSSBOW_ARCHER,
        LONGBOW_ARCHER,
        GENERAL
    }

    @SuppressWarnings("unchecked")
    private static class Builder<T extends Unit.Builder<T>> extends CollidableObject.Builder<T> {

        private Class unitClass;

        public Builder(PointF position, Class unitClass) {
            super(position);
            this.unitClass = unitClass;
        }
        public Unit build() {
            return new Unit(this);
        }
    }

    private Unit(Builder builder) {

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
