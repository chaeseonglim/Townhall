package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.Point;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;

public class Projectile extends CollidableObject {

    private static final String LOG_TAG = "Projectile";

    interface Event {
        void onProjectileReached(Projectile projectile);
    }

    enum ProjectileType {
        ARROW,
        HEAL;

        Sprite sprite() {
            Sprite sprite = new Sprite.Builder("projectile.png").gridSize(2,1)
                    .size(new SizeF(6, 6)).smooth(false).build();
            Point gridIndex = spriteGridIndex();
            sprite.setGridIndex(gridIndex.x, gridIndex.y);
            return sprite;
        }
        Point spriteGridIndex() {
            switch (this) {
                case ARROW:
                    return new Point(0, 0);
                case HEAL:
                    return new Point(1, 0);
                default:
                    return new Point(0, 0);
            }
        }
        public float maxForce() {
            switch (this) {
                case ARROW:
                    return 10.0f;
                case HEAL:
                    return 5.0f;
                default:
                    return 0.0f;
            }
        }
        public float maxVelocity() {
            switch (this) {
                case ARROW:
                    return 10.0f;
                case HEAL:
                    return 5.0f;
                default:
                    return 0.0f;
            }
        }
        public float mass() {
            return 1.0f;
        }
    }

    public static class Builder {

        private Event event;
        private ProjectileType projectileType;
        private Unit target;
        private PointF position;

        public Builder(Event event, ProjectileType projectileType, Unit target, PointF position) {
            this.event = event;
            this.projectileType = projectileType;
            this.target = target;
            this.position = position;
        }
        public Projectile build() {
            return (Projectile) new PrivateBuilder<>(event, position, projectileType, target)
                    .sprite(projectileType.sprite())
                    .maxForce(projectileType.maxForce()).maxVelocity(projectileType.maxVelocity())
                    .maxAngularVelocity(0.0f).inertia(Float.MAX_VALUE)
                    .mass(projectileType.mass()).friction(0.1f)
                    .layer(SPRITE_LAYER).visible(true).build();
        }
    }

    @SuppressWarnings("unchecked")
    private static class PrivateBuilder<T extends Projectile.PrivateBuilder<T>>
            extends CollidableObject.Builder<T> {

        private Event event;
        private ProjectileType type;
        private Unit target;
        private float damage = 0.0f;
        private float heal = 0.0f;

        public PrivateBuilder(Event event, PointF position, ProjectileType type, Unit target) {
            super(position);
            this.event = event;
            this.type = type;
            this.target = target;
        }
        public Projectile build() {
            return new Projectile(this);
        }
    }

    private Projectile(PrivateBuilder builder) {

        super(builder);

        event = builder.event;
        type = builder.type;
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
    public ProjectileType getType() {

        return type;
    }

    /**
     *
     * @return
     */
    public Unit getTarget() {

        return target;
    }

    private final static int SPRITE_LAYER = 8;

    private ProjectileType type;
    private Unit target;
    private Event event;
}
