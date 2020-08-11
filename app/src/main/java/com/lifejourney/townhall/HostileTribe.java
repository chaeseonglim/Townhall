package com.lifejourney.townhall;
import android.util.Log;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Waypoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class HostileTribe extends Tribe {

    private static final String LOG_TAG = "HostileTribe";

    enum Policy {
        DEFENSIVE,
        EXPANSION,
        ASSAULT
    }

    enum UnitSpawnType {
        MELEE,
        RANGED,
        ANY
    }

    public HostileTribe(Event eventHandler, Faction faction, GameMap map, Villager villager) {

        super(eventHandler, faction, map);

        // Write down villager's information
        this.villager = villager;
        for (Territory territory : map.getTerritories()) {
            if (territory.getTerrain() == Territory.Terrain.HEADQUARTER &&
                    territory.getFaction() == Faction.VILLAGER) {
                this.villagerHeadquarterPosition = territory.getMapPosition();
            }
        }
    }

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        if (!isDestroyed()) {

            // Collect resource
            collect();

            // Recruiting
            recruit();

            // Upgrade
            upgrade();

            // Policy
            decidePolicy();

            // Tactic
            decideTactic();
        }
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

        Log.i(LOG_TAG, getFaction().toGameString() + " gold: " + gold);
    }

    /**
     *
     * @return
     */
    protected Unit.UnitClass selectUnitToSpawn(UnitSpawnType spawnType) {
        int minRand = 1;
        int maxRand = Unit.UnitClass.values().length;

        if (recruitingProgressive / RECRUITING_PROGRESSIVE_THRESHOLD < 2 && getSquads().size() < 2) {
            maxRand -= 4;
        } else if (recruitingProgressive / RECRUITING_PROGRESSIVE_THRESHOLD < 3 && getSquads().size() < 3) {
            maxRand -= 3;
        } else if (recruitingProgressive / RECRUITING_PROGRESSIVE_THRESHOLD < 5 && getSquads().size() < 5) {
            maxRand -= 2;
        } else if (recruitingProgressive / RECRUITING_PROGRESSIVE_THRESHOLD < 7 && getSquads().size() < 7) {
            maxRand -= 1;
        }

        Unit.UnitClass selectedUnitClass = null;
        if (spawnType == UnitSpawnType.MELEE) {
            while (selectedUnitClass == null ||
                    selectedUnitClass.unitClassType() != Unit.UnitClassType.MELEE_FIGHTER &&
                    selectedUnitClass.unitClassType() != Unit.UnitClassType.MELEE_HEALER &&
                    selectedUnitClass.unitClassType() != Unit.UnitClassType.MELEE_SUPPORTER) {
                selectedUnitClass =
                        Unit.UnitClass.values()[(int) (Math.random() * (maxRand - minRand) + minRand)];
            }
        } else if (spawnType == UnitSpawnType.RANGED) {
            while (selectedUnitClass == null ||
                    selectedUnitClass.unitClassType() != Unit.UnitClassType.RANGED_FIGHTER &&
                    selectedUnitClass.unitClassType() != Unit.UnitClassType.RANGED_HEALER &&
                    selectedUnitClass.unitClassType() != Unit.UnitClassType.RANGED_SUPPORTER) {
                selectedUnitClass =
                        Unit.UnitClass.values()[(int) (Math.random() * (maxRand - minRand) + minRand)];
            }
        } else {
            selectedUnitClass = Unit.UnitClass.values()[(int)(Math.random()*(maxRand-minRand)+minRand)];
        }

        return selectedUnitClass;
    }

    /**
     *
     */
    protected void recruit() {

        if (recruitingUpdateTimeLeft-- > 0) {
            return;
        }
        recruitingUpdateTimeLeft = SQUAD_RECRUITING_UPDATE_TIME;

        recruitingProgressive += RECRUITING_PROGRESSIVE_DELTA;
        Log.i(LOG_TAG, getFaction().toGameString() + " progressive: " + recruitingProgressive);

        // Create squad if conditions are met
        if (getSquads().size() < SQUAD_COUNT_LIMIT &&
                gold >= SQUAD_CREATION_ALLOW_GOLD * (getSquads().size() + 1) &&
                getMap().getTerritory(getHeadquarterPosition()).getFaction() == getFaction() &&
                getMap().getTerritory(getHeadquarterPosition()).getSquads().size() == 0) {
            gold -= SQUAD_CREATION_ALLOW_GOLD;
            spawnSquad(getHeadquarterPosition().toGameCoord(), getFaction(),
                    selectUnitToSpawn(Raider.UnitSpawnType.MELEE),
                    selectUnitToSpawn(Raider.UnitSpawnType.ANY),
                    selectUnitToSpawn(Raider.UnitSpawnType.RANGED));
        }
    }

    /**
     *
     */
    protected void upgrade() {

        if (upgradingProgressive++ > UPGRADING_PROGRESSIVE_THRESHOLD) {
            for (Upgradable upgradable : Upgradable.values()) {
                if (upgradable.getLevel(getFaction()) < 3 && Math.random() > 0.5f) {
                    upgradable.setLevel(getFaction(), upgradable.getLevel(getFaction()) + 1);
                    getEventHandler().onTribeUpgraded(this, upgradable);
                    break;
                }
            }
            upgradingProgressive -= UPGRADING_PROGRESSIVE_THRESHOLD;
        }

    }

    /**
     *
     */
    protected void decidePolicy() {

        if (policyDecisionUpdateTimeLeft-- > 0) {
            return;
        }
        policyDecisionUpdateTimeLeft = POLICY_DECISION_UPDATE_TIME;

        if (policyTransitionTimeLeft-- <= 0) {
            float villagerBattleMetric = villager.getTotalBattleMetric();
            float myBattleMetric = getTotalBattleMetric();

            Raider.Policy prevPolicy = policy;

            if (myBattleMetric > villagerBattleMetric * 2.0f && getSquads().size() >= 4) {
                policy = Raider.Policy.ASSAULT;
            } else if (myBattleMetric * 2.0f < villagerBattleMetric) {
                policy = Raider.Policy.DEFENSIVE;
            } else {
                policy = Raider.Policy.EXPANSION;
            }

            if (prevPolicy != policy) {
                policyTransitionTimeLeft = POLICY_TRANSITION_TIME;
            }
        }

        if ((policy == Raider.Policy.EXPANSION || policy == Raider.Policy.DEFENSIVE) &&
                getMap().getTerritory(getHeadquarterPosition()).getFaction() != getFaction()) {
            // If someone took its hq, take it back
            strategicTarget = getHeadquarterPosition();
        } else if (policy == Raider.Policy.EXPANSION || policy == Raider.Policy.ASSAULT) {
            // First try to take shrines
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

            // And then villager's HQ
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
    protected void decideTactic() {

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
    protected void decideASquadTactic(final Squad squad) {

        if (squad.isFighting() || squad.isOccupying() || squad.isSupporting() ||
                squad.isRecruiting() || squad.getHealthPercentage() <= SQUAD_ACTIVATE_THRESHOLD) {
            return;
        }

        if (policy == Raider.Policy.EXPANSION && squad.isMoving() && (
                getMap().getTerritory(squad.getMapPosition()).getFaction() == Faction.NEUTRAL ||
                        getMap().getTerritory(squad.getMapPosition()).getFaction() == Faction.VILLAGER)) {
            // Wait for occupying when it's on other faction's land on expansion policy
            squad.stopMoving();
            return;
        }

        if (squad.isMoving()) {
            return;
        }

        // Recruit unit if needed
        if (squad.getUnits().size() < 3 &&
                getMap().getTerritory(squad.getMapPosition()).getFaction() != getFaction()) {
            squad.spawnUnit(selectUnitToSpawn(Raider.UnitSpawnType.ANY));
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

            // Check if strategic target is movable
            if (neighborTerritory.getMapPosition().equals(strategicTarget) &&
                neighborTerritory.isFactionSquadExist(getFaction()) &&
                neighborTerritory.isMovable(squad)) {
                candidatesToExpansion.add(neighborTerritory);
            } else if (policy == Policy.EXPANSION) {
                // Check if there's a other tiles to occupy when the expansion policy is on
                if (neighborTerritory.getFaction() == Faction.NEUTRAL ||
                    neighborTerritory.getFaction() == Faction.VILLAGER) {
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
            Territory weakestTerritory = null;
            float weakestBattleMetric = Float.MAX_VALUE;
            float weakestSupportMetric = Float.MAX_VALUE;
            for (Territory candidateTerritory: candidatesToAttack) {
                Squad squad1 = candidateTerritory.getSquads().get(0);
                if (squad1.getBattleMetric() < weakestBattleMetric) {
                    weakestTerritory = candidateTerritory;
                    weakestBattleMetric = squad1.getBattleMetric();
                    weakestSupportMetric = squad1.getSupportMetric();
                } else if (squad1.getBattleMetric() == weakestBattleMetric) {
                    if (squad1.getSupportMetric() > weakestSupportMetric) {
                        weakestTerritory = candidateTerritory;
                        weakestSupportMetric = squad1.getSupportMetric();
                    } else if (squad1.getSupportMetric() == weakestSupportMetric) {
                        weakestTerritory =
                                (Math.random() < 0.5f) ? candidateTerritory : weakestTerritory;
                    }
                }
            }
            assert weakestTerritory != null;
            squad.seekTo(weakestTerritory.getMapPosition(), true);
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

    protected static final int COLLECT_UPDATE_TIME = 50;
    protected static final int POLICY_DECISION_UPDATE_TIME = 130;
    protected static final int POLICY_TRANSITION_TIME = 300;
    protected static final int SQUAD_RECRUITING_UPDATE_TIME = 70;
    protected static final int TACTICAL_DECISION_UPDATE_TIME = 90;
    protected static final int SQUAD_CREATION_ALLOW_GOLD = 10000;
    protected static final int SQUAD_COUNT_LIMIT = 10;
    protected static final int INCOME_PER_TERRITORY = 20;
    protected static final int BASE_INCOME = 100;
    protected static final int STARTING_GOLD = 10000;
    protected static final int SQUAD_AWARENESS_RANGE = 3;
    protected static final float SQUAD_ACTIVATE_THRESHOLD = 0.8f;
    protected static final int RECRUITING_PROGRESSIVE_DELTA = 10;
    protected static final int RECRUITING_PROGRESSIVE_THRESHOLD = 1000;
    protected static final int UPGRADING_PROGRESSIVE_THRESHOLD = 3000;

    protected Villager villager;
    protected Policy policy = Policy.EXPANSION;
    protected OffsetCoord strategicTarget = null;
    protected OffsetCoord villagerHeadquarterPosition;

    protected int recruitingProgressive = 0;
    protected int upgradingProgressive = 0;
    protected int collectUpdateTimeLeft = COLLECT_UPDATE_TIME;
    protected int recruitingUpdateTimeLeft = SQUAD_RECRUITING_UPDATE_TIME;
    protected int policyDecisionUpdateTimeLeft = POLICY_DECISION_UPDATE_TIME;
    protected int policyTransitionTimeLeft = POLICY_TRANSITION_TIME;
    protected int tacticalDecisionUpdateTime = TACTICAL_DECISION_UPDATE_TIME;
    protected int gold = STARTING_GOLD;
}
