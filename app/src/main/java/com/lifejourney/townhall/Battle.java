package com.lifejourney.townhall;

import com.lifejourney.engine2d.CollidableObject;
import com.lifejourney.engine2d.CollisionDetector;
import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;

import java.util.ArrayList;

public class Battle {

    private static final String LOG_TAG = "Battle";

    public Battle(GameMap map, Squad attacker, Squad defender) {

        this.map = map;
        this.attacker = attacker;
        this.defender = defender;
        this.attacker.beginFight(defender);
        this.defender.beginFight(attacker);
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

        // Handle post fight situation
        handlePostFight();

        // Reset supporters
        supporters.clear();
    }

    /**
     *
     */
    private void resolveCollision() {

        ArrayList<Unit> units = new ArrayList<>();
        units.addAll(attacker.getUnits());
        units.addAll(defender.getUnits());

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

        // Fight each others
        attacker.fight();
        defender.fight();
        for (Squad supporter: supporters) {
            if (supporter.getFaction().equals(attacker.getFaction())) {
                supporter.support(attacker, defender);
            } else {
                supporter.support(defender, attacker);
            }
        }

        // Handle fight result
        ArrayList<Squad> squads = new ArrayList<>(supporters);
        squads.add(attacker);
        squads.add(defender);
        int defenderEarnedExp = attacker.handleFightResult(),
                attackerEarnedExp = defender.handleFightResult();
        for (Squad squad: squads) {
            if (squad.getFaction() == attacker.getFaction()) {
                squad.addExp(attackerEarnedExp);
            } else if (squad.getFaction() == defender.getFaction()) {
                squad.addExp(defenderEarnedExp);
            }
        }

        // Share exp to supporters
        for (Squad supporter: supporters) {
            supporter.addExp(SUPPORTER_EXP);
        }
    }

    /**
     *
     */
    public void handlePostFight() {

        Squad winner = null, loser = null;
        if (attacker.isEliminated() || defender.isEliminated()) {
            // If one or them is eliminated, finish battle
            if (attacker.isEliminated()) {
                winner = defender;
            } else if (defender.isEliminated()) {
                winner = attacker;
            }
            finished = true;
        } else {
            if (attacker.isWillingToRetreat()) {
                winner = defender;
                loser = attacker;
                finished = true;
            } else if (defender.isWillingToRetreat()) {
                winner = attacker;
                loser = defender;
                finished = true;
            }
        }

        // If there's loser
        if (finished) {
            if (loser != null) {
                // Try retreating loser
                ArrayList<OffsetCoord> retreatableCoords = map.findRetreatableMapCoords(loser);
                if (retreatableCoords != null && retreatableCoords.size() > 0) {
                    // Retreat loser
                    loser.moveTo(retreatableCoords.get(0));
                }
            }

            attacker.endFight();
            defender.endFight();

            if (winner != null) {
                winner.addExp(WINNER_EXP);
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

    /**
     *
     * @param squad
     */
    public void addSupporter(Squad squad) {
        supporters.add(squad);
    }

    private int WINNER_EXP = 50;
    private int SUPPORTER_EXP  = 25;

    private OffsetCoord mapCoord;
    private GameMap map;
    private Squad attacker;
    private Squad defender;
    private ArrayList<Squad> supporters = new ArrayList<>();
    private boolean finished = false;
}
