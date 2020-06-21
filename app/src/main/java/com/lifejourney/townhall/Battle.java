package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.CollisionDetector;
import com.lifejourney.engine2d.Engine2D;

import java.util.ArrayList;

public class Battle {

    public Battle(Squad attacker, Squad defender) {

        this.attacker = attacker;
        this.defender = defender;
        this.squads.add(attacker);
        this.squads.add(defender);
        this.attacker.enterBattle(defender);
        this.defender.enterBattle(attacker);
    }

    /**
     *
     */
    public void update() {

        resolveCollision();
        fight();
    }

    /**
     *
     */
    private void resolveCollision() {
        ArrayList<Unit> units = new ArrayList<>();
        for (Squad squad: squads) {
            units.addAll(squad.getUnits());
        }

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

    /**
     *
     */
    private void fight() {

        // First fight
        for (Squad squad: squads) {
            squad.fight();
        }

        // Second count fight result
        for (Squad squad: squads) {
            squad.countFightResult();
        }
    }

    /**
     *
     */
    public void finish() {

        attacker.leaveBattle();
        defender.leaveBattle();
    }

    /**
     *
     * @return
     */
    public boolean isAttackerEliminated() {

        return attacker.getUnits().size() == 0;
    }

    /**
     *
     * @return
     */
    public boolean isDefenderEliminated() {

        return defender.getUnits().size() == 0;
    }

    /**
     *
     * @return
     */
    public Squad getAttacker() {
        return attacker;
    }

    /**
     *
     * @return
     */
    public Squad getDefender() {
        return defender;
    }

    private Squad attacker;
    private Squad defender;
    private ArrayList<Squad> squads = new ArrayList<>();
}
