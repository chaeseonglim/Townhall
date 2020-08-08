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
        this.mapCoord = defender.getMapPosition();
    }

    /**
     *
     */
    public void update() {

        // Collision detection between units first
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
        for (Unit unit: attacker.getUnits()) {
            if (!unit.isRecruiting()) {
                units.add(unit);
            }
        }
        for (Unit unit: defender.getUnits()) {
            if (!unit.isRecruiting()) {
                units.add(unit);
            }
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

        // Share exp to fighters
        attacker.addExp(FIGHTING_EXP);
        defender.addExp(FIGHTING_EXP);

        // Share exp to supporters
        for (Squad supporter: supporters) {
            supporter.addExp(SUPPORTING_EXP);
        }
    }

    /**
     *
     */
    public void handlePostFight() {

        Squad winner = null, loser = null;
        boolean eliminated = false;
        if (attacker.isEliminated() || defender.isEliminated()) {
            // If one or them is eliminated, finish battle
            if (attacker.isEliminated()) {
                winner = defender;
                loser = attacker;
            } else if (defender.isEliminated()) {
                winner = attacker;
                loser = defender;
            }
            eliminated = true;
        } else {
            if (attacker.isWillingToRetreat()) {
                winner = defender;
                loser = attacker;
            } else if (defender.isWillingToRetreat()) {
                winner = attacker;
                loser = defender;
            } else if (battleTimeLeft-- == 0) {
                winner = defender;
                loser = attacker;
            }
        }

        // If there's loser
        if (winner != null && loser != null) {
            if (eliminated) {
                attacker.endFight();
                defender.endFight();
                winner.addExp(WINNER_EXP);
                if (attacker.isEliminated()) {
                    attacker.close();
                }
                if (defender.isEliminated()) {
                    defender.close();
                }
            } else {
                // Try retreating loser
                ArrayList<OffsetCoord> retreatableCoords = map.findMapPositionToRetreat(loser);
                if (retreatableCoords == null || retreatableCoords.size() == 0) {
                    // Failed to retreat
                    return;
                }

                // End fight
                attacker.endFight();
                defender.endFight();
                winner.addExp(WINNER_EXP);

                // Move loser to an other tile
                ArrayList<OffsetCoord> retreatableSameFactionCoords = new ArrayList<>();
                ArrayList<OffsetCoord> retretableNeutralFactionCoords = new ArrayList<>();
                for (OffsetCoord retreatableCoord: retreatableCoords) {
                    if (map.getTerritory(retreatableCoord).getFaction() == loser.getFaction()) {
                        retreatableSameFactionCoords.add(retreatableCoord);
                    } else if (map.getTerritory(retreatableCoord).getFaction() == Tribe.Faction.NEUTRAL) {
                        retretableNeutralFactionCoords.add(retreatableCoord);
                    }
                }
                if (retreatableSameFactionCoords.size() > 0) {
                    loser.moveTo(retreatableSameFactionCoords.get(
                            (int)(Math.random()*retreatableSameFactionCoords.size())));
                } else if (retretableNeutralFactionCoords.size() > 0) {
                    loser.moveTo(retretableNeutralFactionCoords.get(
                            (int)(Math.random()*retretableNeutralFactionCoords.size())));
                } else {
                    loser.moveTo(retreatableCoords.get(
                            (int)(Math.random()*retreatableCoords.size())));
                }
            }

            finished = true;
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
    private int FIGHTING_EXP = 1;
    private int SUPPORTING_EXP = 1;
    private int BATTLE_TIME_LIMIT = 1000;

    private OffsetCoord mapCoord;
    private GameMap map;
    private Squad attacker;
    private Squad defender;
    private ArrayList<Squad> supporters = new ArrayList<>();
    private boolean finished = false;
    private int battleTimeLeft = BATTLE_TIME_LIMIT;
}
