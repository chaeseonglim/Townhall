package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

import java.util.ArrayList;

public class Projectile extends CollidableObject {

    private static final String LOG_TAG = "Projectile";

    interface Event {
        void onProjectileReached(Projectile projectile);
    }

    enum ProjectileClass {
        ARROW;

        Sprite sprite() {
            Sprite sprite = new Sprite.Builder("bullet.png").gridSize(1,1)
                    .size(new SizeF(3, 3)).smooth(false).build();
            sprite.setGridIndex(spriteGridIndex().x, spriteGridIndex().y);
            return sprite;
        }
        Point spriteGridIndex() {
            switch (this) {
                case ARROW:
                    return new Point(0, 0);
                default:
                    return null;
            }
        }
        public float maxForce() {
            return 10.0f;
        }
        public float maxVelocity() {
            return 10.0f;
        }
        public float mass() {
            return 1.0f;
        }
    }

    public static class Builder {

        private Event event;
        private ProjectileClass projectileClass;
        private Unit target;
        private PointF position = new PointF();

        public Builder(Event event, ProjectileClass projectileClass, Unit target) {
            this.event = event;
            this.projectileClass = projectileClass;
            this.target = target;
        }
        public Builder position(PointF position) {
            this.position = position;
            return this;
        }
        public Projectile build() {
            return (Projectile) new PrivateBuilder<>(event, position, projectileClass, target)
                    .sprite(projectileClass.sprite())
                    .maxForce(projectileClass.maxForce()).maxVelocity(projectileClass.maxVelocity())
                    .maxAngularVelocity(0.0f).inertia(Float.MAX_VALUE)
                    .mass(projectileClass.mass()).friction(0.1f)
                    .layer(SPRITE_LAYER).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Projectile.PrivateBuilder<T>> extends CollidableObject.Builder<T> {

        private Event event;
        private ProjectileClass unitClass;
        private Unit target;

        public PrivateBuilder(Event event, PointF position, ProjectileClass unitClass, Unit target) {
            super(position);
            this.event = event;
            this.unitClass = unitClass;
            this.target = target;
        }
        public Projectile build() {
            return new Projectile(this);
        }
    }

    private Projectile(PrivateBuilder builder) {

        super(builder);

        event = builder.event;
        projectileClass = builder.unitClass;
        target = builder.target;
    }

    /**
     *
     */
    @Override
    public void update() {

        seek(target.getPosition(), 1.0f);

        // Update moving
        super.update();

        if (target.getPosition().distance(getPosition()) < target.getShape().getRadius()) {
            event.onProjectileReached(this);
        }
    }

    /**
     *
     * @return
     */
    public ProjectileClass getProjectileClass() {

        return projectileClass;
    }

    private final static int SPRITE_LAYER = 8;

    private ProjectileClass projectileClass;
    private Unit target;
    private Event event;
}
