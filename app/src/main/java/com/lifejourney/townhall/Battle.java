package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.CollisionDetector;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;

import java.util.ArrayList;

public class Battle {

    private static final String LOG_TAG = "Battle";

    public Battle(Squad attacker, Squad defender) {

        this.attacker = attacker;
        this.defender = defender;
        this.squads.add(attacker);
        this.squads.add(defender);
        this.attacker.enterBattle(defender);
        this.defender.enterBattle(attacker);
        this.mapCoord = defender.getMapCoord();
    }

    /**
     *
     */
    public void update() {

        // Collistion detection between units first
        resolveCollision();

        // Fight
        fight();

        // Check if a battle is finished
        if (attacker.isEliminated() || defender.isEliminated()) {
            attacker.leaveBattle();
            defender.leaveBattle();
            finished = true;
        }
        else if (attacker.isWillingToRetreat()) {
            // TODO: try retreating attacker
        }
        else if (defender.isWillingToRetreat()) {
            // TODO: try retreating defender
        }
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
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     *
     * @return
     */
    public OffsetCoord getMapCoord() {
        return mapCoord;
    }

    private OffsetCoord mapCoord;
    private Squad attacker;
    private Squad defender;
    private ArrayList<Squad> squads = new ArrayList<>();
    private boolean finished = false;
}
