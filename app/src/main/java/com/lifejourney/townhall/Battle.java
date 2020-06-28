package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.CollisionDetector;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;

import java.util.ArrayList;

public class Battle {

    private static final String LOG_TAG = "Battle";

    public Battle(TownMap map, Squad attacker, Squad defender) {

        this.map = map;
        this.attacker = attacker;
        this.defender = defender;
        this.squads.add(attacker);
        this.squads.add(defender);
        this.attacker.startFight(defender);
        this.defender.startFight(attacker);
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

        if (attacker.isEliminated() || defender.isEliminated()) {
            // finish battle
            attacker.finishFight();
            defender.finishFight();
            finished = true;
        }
        else if (attacker.isWillingToRetreat()) {
            // try retreating attacker
            ArrayList<OffsetCoord> retreatableCoords = map.findRetreatableMapCoords(attacker.getMapCoord());
            if (retreatableCoords != null && retreatableCoords.size() > 0) {
                // Retreat attacker
                attacker.moveTo(retreatableCoords.get(0));

                // Finish battle
                attacker.finishFight();
                defender.finishFight();
                finished = true;
            }
        }
        else if (defender.isWillingToRetreat()) {
            // try retreating defender
            ArrayList<OffsetCoord> retreatableCoords = map.findRetreatableMapCoords(defender.getMapCoord());
            if (retreatableCoords != null) {
                for (OffsetCoord retreatableCoord: retreatableCoords) {
                    if (!retreatableCoord.equals(attacker.getPrevMapCoord())) {
                        // Retreat defender
                        defender.moveTo(retreatableCoord);

                        // Finish battle
                        attacker.finishFight();
                        defender.finishFight();
                        finished = true;
                        break;
                    }
                }
            }
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
            int expEarned = squad.countFightResult();
            // Adding exp to opposite sided squads
            for (Squad squad1: squads) {
                if (squad1.getSide() != squad.getSide()) {
                    squad1.addExp(expEarned);
                }
            }
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
    private TownMap map;
    private Squad attacker;
    private Squad defender;
    private ArrayList<Squad> squads = new ArrayList<>();
    private boolean finished = false;
}
