package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.CollisionDetector;
import com.lifejourney.engine2d.Engine2D;

import java.util.ArrayList;

public class Battle {

    public Battle(Squad squadA, Squad squadB) {

        squadA.enterBattle(squadB);
        squadB.enterBattle(squadA);
        fighters.add(squadA);
        fighters.add(squadB);
        units.addAll(squadA.getUnits());
        units.addAll(squadB.getUnits());
    }

    public void update() {

        // Collision detection
        CollisionDetector collisionDetector = Engine2D.GetInstance().getCollisionDetector();
        for (CollidableObject refUnit : units) {
            if (!refUnit.isCollisionEnabled())
                continue;

            for (CollidableObject candidateUnit : units) {
                if (refUnit == candidateUnit || candidateUnit.isCollisionChecked() ||
                        !candidateUnit.isCollisionEnabled()) {
                    continue;
                }

                collisionDetector.checkAndReponseCollision(refUnit, candidateUnit, false);
            }

            refUnit.setCollisionChecked(true);
        }
        for (CollidableObject unit: units) {
            unit.setCollisionChecked(false);
        }
    }

    private ArrayList<Squad> fighters = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();
}
