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
        this.mapPosition = defender.getMapPosition();
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

        Squad winningOne = null, losingOne = null;
        boolean eliminated = false;
        if (attacker.isEliminated() || defender.isEliminated()) {
            // If one or them is eliminated, finish battle
            if (attacker.isEliminated()) {
                winningOne = defender;
                losingOne = attacker;
            } else if (defender.isEliminated()) {
                winningOne = attacker;
                losingOne = defender;
            }
            eliminated = true;
        } else {
            if (attacker.isWillingToRetreat()) {
                winningOne = defender;
                losingOne = attacker;
            } else if (defender.isWillingToRetreat()) {
                winningOne = attacker;
                losingOne = defender;
            } else if (battleTimeLeft-- == 0) {
                winningOne = defender;
                losingOne = attacker;
            }
        }

        // If there's a loser
        if (winningOne != null && losingOne != null) {
            if (eliminated) {
                attacker.endFight();
                defender.endFight();
                winningOne.addExp(WINNER_EXP);
                if (attacker.isEliminated()) {
                    attacker.close();
                }
                if (defender.isEliminated()) {
                    defender.close();
                }
            } else { // Try to retreat loser
                if (losingOne.getHealthPercentage() < RETRETABLE_HEALTH_PERCENTAGE) {
                    // Failed to retreat
                    return;
                }

                ArrayList<OffsetCoord> retreatableMapPositions = map.findMapPositionToRetreat(losingOne);
                if (retreatableMapPositions == null || retreatableMapPositions.size() == 0) {
                    // Failed to retreat
                    return;
                }

                // End fight
                attacker.endFight();
                defender.endFight();
                winningOne.addExp(WINNER_EXP);

                // Move loser to an other tile
                ArrayList<OffsetCoord> retreatableSameFactionMapPosition = new ArrayList<>();
                ArrayList<OffsetCoord> retretableNeutralFactionMapPosition = new ArrayList<>();
                for (OffsetCoord retreatableMapPosition: retreatableMapPositions) {
                    if (map.getTerritory(retreatableMapPosition).getFaction() == losingOne.getFaction()) {
                        retreatableSameFactionMapPosition.add(retreatableMapPosition);
                    } else if (map.getTerritory(retreatableMapPosition).getFaction() == Tribe.Faction.NEUTRAL) {
                        retretableNeutralFactionMapPosition.add(retreatableMapPosition);
                    }
                }
                if (retreatableSameFactionMapPosition.size() > 0) {
                    losingOne.moveTo(retreatableSameFactionMapPosition.get(
                            (int)(Math.random()*retreatableSameFactionMapPosition.size())));
                } else if (retretableNeutralFactionMapPosition.size() > 0) {
                    losingOne.moveTo(retretableNeutralFactionMapPosition.get(
                            (int)(Math.random()*retretableNeutralFactionMapPosition.size())));
                } else {
                    losingOne.moveTo(retreatableMapPositions.get(
                            (int)(Math.random()*retreatableMapPositions.size())));
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
    public OffsetCoord getMapPosition() {
        return mapPosition;
    }

    /**
     *
     * @param squad
     */
    public void addSupporter(Squad squad) {
        supporters.add(squad);
    }

    private int WINNER_EXP = 50;
    private int FIGHTING_EXP = 2;
    private int SUPPORTING_EXP = 1;
    private int BATTLE_TIME_LIMIT = 1000;
    private float RETRETABLE_HEALTH_PERCENTAGE = 0.2f;

    private GameMap map;
    private OffsetCoord mapPosition;
    private Squad attacker;
    private Squad defender;
    private ArrayList<Squad> supporters = new ArrayList<>();
    private boolean finished = false;
    private int battleTimeLeft = BATTLE_TIME_LIMIT;
}
