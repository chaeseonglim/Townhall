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
            if (supporter.getSide().equals(attacker.getSide())) {
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
            if (squad.getSide() == attacker.getSide()) {
                squad.addExp(attackerEarnedExp);
            } else if (squad.getSide() == defender.getSide()) {
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

        Squad winner = null;
        if (attacker.isEliminated() || defender.isEliminated()) {
            // If one or them is elimated, finish battle
            attacker.endFight();
            defender.endFight();
            if (attacker.isEliminated()) {
                winner = defender;
            }
            else if (defender.isEliminated()) {
                winner = attacker;
            }
            finished = true;
        }
        else if (attacker.isWillingToRetreat()) {
            // Try retreating attacker
            ArrayList<OffsetCoord> retreatableCoords = map.findRetreatableMapCoords(attacker);
            if (retreatableCoords != null && retreatableCoords.size() > 0) {
                // Retreat attacker
                attacker.moveTo(retreatableCoords.get(0));

                // Finish battle
                attacker.endFight();
                defender.endFight();
                finished = true;

                winner = defender;
            }
        }
        else if (defender.isWillingToRetreat()) {
            // Try retreating defender
            ArrayList<OffsetCoord> retreatableCoords = map.findRetreatableMapCoords(defender);
            if (retreatableCoords != null) {
                for (OffsetCoord retreatableCoord: retreatableCoords) {
                    if (!retreatableCoord.equals(attacker.getPrevMapCoord())) {
                        // Retreat defender
                        defender.moveTo(retreatableCoord);

                        // Finish battle
                        attacker.endFight();
                        defender.endFight();
                        finished = true;

                        winner = attacker;
                        break;
                    }
                }
            }
        }

        if (winner != null) {
            winner.addExp(WINNER_EXP);
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
    private TownMap map;
    private Squad attacker;
    private Squad defender;
    private ArrayList<Squad> supporters = new ArrayList<>();
    private boolean finished = false;
}
