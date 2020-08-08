package com.lifejourney.townhall;

import android.util.Log;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Raider extends Tribe {

    private static final String LOG_TAG = "Raider";

    enum Policy {
        DEFENSIVE,
        EXPANSION,
        ASSAULT
    }

    public Raider(Event eventHandler, GameMap map) {

        super(eventHandler, Faction.RAIDER, map);

        for (Territory territory : map.getTerritories()) {
            if (territory.getTerrain() == Territory.Terrain.HEADQUARTER &&
                territory.getFaction() == Faction.VILLAGER) {
                this.villagerHeadquarterPosition = territory.getMapPosition();
            }
        }
        /*
        spawnSquad(new OffsetCoord(9, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        spawnSquad(new OffsetCoord(10, 8).toGameCoord(), getFaction(),
                Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.CANNON);
         */
    }

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        collect();

        recruit();

        decidePolicy();

        decideSquadTactics();
    }

    /**
     *
     */
    private void collect() {

        if (collectUpdateTimeLeft-- > 0) {
            return;
        }
        collectUpdateTimeLeft = COLLECT_UPDATE_TIME;

        gold += BASE_INCOME;
        for (Territory territory: getTerritories()) {
            gold += INCOME_PER_TERRITORY;
        }

        Log.e(LOG_TAG, "Raider gold: " + gold);
    }

    /**
     *
     */
    private void recruit() {

        if (recruitingUpdateTimeLeft-- > 0) {
            return;
        }
        recruitingUpdateTimeLeft = SQUAD_RECRUITING_UPDATE_TIME;

        // Create squad if conditions are met
        if (getSquads().size() < SQUAD_COUNT_LIMIT && gold >= SQUAD_CREATION_ALLOW_GOLD &&
                getMap().getTerritory(getHeadquarterPosition()).getSquads().size() == 0) {
            gold -= SQUAD_CREATION_ALLOW_GOLD;
            spawnSquad(getHeadquarterPosition().toGameCoord(), getFaction(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        }

    }

    /**
     *
     */
    private void decidePolicy() {

        if (policyDecisionUpdateTimeLeft-- > 0) {
            return;
        }
        policyDecisionUpdateTimeLeft = POLICY_DECISION_UPDATE_TIME;

        if (policy == Policy.EXPANSION || policy == Policy.ASSAULT) {
            int nearestDistanceToShrine = Integer.MAX_VALUE;
            OffsetCoord nearestShrinePosition = null;
            for (OffsetCoord shrinePosition: getShrinePositions()) {
                if (getMap().getTerritory(shrinePosition).getFaction() == getFaction()) {
                    continue;
                }
                GamePathFinder pathFinder = new GamePathFinder(getHeadquarterPosition(),
                        shrinePosition, getMap(), getFaction());
                ArrayList<Waypoint> optimalPath = pathFinder.findOptimalPath();

                if (optimalPath != null && nearestDistanceToShrine > optimalPath.size()) {
                    nearestDistanceToShrine = optimalPath.size();
                    nearestShrinePosition = shrinePosition;
                }
            }

            if (nearestShrinePosition != null) {
                strategicTarget = nearestShrinePosition;
            } else {
                strategicTarget = villagerHeadquarterPosition;
            }
        }
    }

    /**
     *
     */
    private void decideSquadTactics() {

        if (tacticalDecisionUpdateTime-- > 0) {
            return;
        }
        tacticalDecisionUpdateTime = TACTICAL_DECISION_UPDATE_TIME;

        for (Squad squad: getSquads()) {
            decideASquadTactic(squad);
        }
    }

    /**
     *
     * @param squad
     */
    private void decideASquadTactic(final Squad squad) {

        if (squad.isFighting() || squad.isOccupying() || squad.isSupporting() ||
                squad.isRecruiting() || squad.getHealthPercentage() <= SQUAD_ACTIVATE_THRESHOLD) {
            return;
        }

        if (policy == Policy.EXPANSION && squad.isMoving() &&
                getMap().getTerritory(squad.getMapPosition()).getFaction() != getFaction()) {
            // Wait for occupying when it's on other faction's land on expansion policy
            squad.stopMoving();
            return;
        }

        if (squad.isMoving()) {
            return;
        }

        // Find if neighbor territories have any events which need to go
        ArrayList<Territory> neighborTerritories =
                getMap().getNeighborTerritories(squad.getMapPosition(), SQUAD_AWARENESS_RANGE, false);
        ArrayList<Territory> candidatesToSupport = new ArrayList<>();
        ArrayList<Territory> candidatesToAttack = new ArrayList<>();
        ArrayList<Territory> candidatesToExpansion = new ArrayList<>();
        Collections.sort(neighborTerritories, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Territory t1 = (Territory)o1;
                Territory t2 = (Territory)o2;
                int t1Distance = t1.getMapPosition().getDistance(squad.getMapPosition());
                int t2Distance = t2.getMapPosition().getDistance(squad.getMapPosition());
                return Integer.compare(t1Distance, t2Distance);
            } } );

        for (Territory neighborTerritory : neighborTerritories) {
            if (!neighborTerritory.getTerrain().isMovable(getFaction())) {
                continue;
            }

            // Check if there's a battle needs to support
            if (squad.isSupportable() && neighborTerritory.getBattle() != null &&
                    neighborTerritory.isFactionSquadExist(getFaction())) {
                candidatesToSupport.add(neighborTerritory);
            }

            // Check if there's a enemy alone
            if (neighborTerritory.isFactionSquadExist(Faction.VILLAGER)) {
                candidatesToAttack.add(neighborTerritory);
            }

            // Check if there's a other tiles to occupy when the expansion policy is on
            if (policy == Policy.EXPANSION) {
                if (neighborTerritory.getFaction() != getFaction()) {
                    candidatesToExpansion.add(neighborTerritory);
                }
            }
        }

        if (candidatesToSupport.size() > 0) {
            // Find adjacent available tiles to support
            ArrayList<Territory> candidateTerritories = new ArrayList<>();
            for (Territory territoryNeedsSupport : candidatesToSupport) {
                candidateTerritories.addAll(getMap().getNeighborTerritories(
                        territoryNeedsSupport.getMapPosition(), 1, false));
            }
            ArrayList<Territory> candidateTerritoriesCopy = new ArrayList<>(candidateTerritories);
            for (Territory territory : candidateTerritoriesCopy) {
                if (territory.getBattle() != null || territory.isFactionSquadExist(getFaction())) {
                    candidateTerritories.remove(territory);
                }
            }

            // Go to support squads
            if (candidateTerritories.size() > 0) {
                squad.seekTo(candidateTerritories.get(
                        (int)(Math.random() * candidateTerritories.size())).getMapPosition(), false);
            }
        } else if (candidatesToAttack.size() > 0) {
            // Go to attack
            // FIXME: Need to compare both power before engaging
            squad.seekTo(candidatesToAttack.get(
                    (int)(Math.random()*candidatesToAttack.size())).getMapPosition(), true);
        } else if (candidatesToExpansion.size() > 0) {
            int nearestDistance = candidatesToExpansion.get(0).getMapPosition().getDistance(squad.getMapPosition());
            ArrayList<Territory> nearestDistanceTerritories = new ArrayList<>();
            for (Territory candidate: candidatesToExpansion) {
                if (candidate.getMapPosition().getDistance(squad.getMapPosition()) == nearestDistance) {
                    nearestDistanceTerritories.add(candidate);
                }
            }
            // Go to expand
            squad.seekTo(nearestDistanceTerritories.get(
                    (int)(Math.random()*nearestDistanceTerritories.size())).getMapPosition(), true);
        } else if (strategicTarget != null) {
            // Go to strategic target
            squad.seekTo(strategicTarget, true);
        }
    }

    private static final int COLLECT_UPDATE_TIME = 50;
    private static final int POLICY_DECISION_UPDATE_TIME = 130;
    private static final int SQUAD_RECRUITING_UPDATE_TIME = 70;
    private static final int TACTICAL_DECISION_UPDATE_TIME = 90;
    private static final int SQUAD_CREATION_ALLOW_GOLD = 10000;
    private static final int SQUAD_COUNT_LIMIT = 10;
    private static final int INCOME_PER_TERRITORY = 10;
    private static final int BASE_INCOME = 100;
    private static final int STARTING_GOLD = 10000;
    private static final int SQUAD_AWARENESS_RANGE = 3;
    private static final float SQUAD_ACTIVATE_THRESHOLD = 0.8f;

    private int collectUpdateTimeLeft = COLLECT_UPDATE_TIME;
    private int recruitingUpdateTimeLeft = SQUAD_RECRUITING_UPDATE_TIME;
    private int policyDecisionUpdateTimeLeft = POLICY_DECISION_UPDATE_TIME;
    private int tacticalDecisionUpdateTime = TACTICAL_DECISION_UPDATE_TIME;
    private int gold = STARTING_GOLD;
    private Policy policy = Policy.EXPANSION;
    private OffsetCoord strategicTarget = null;
    private OffsetCoord villagerHeadquarterPosition;
}
