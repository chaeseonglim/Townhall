package com.lifejourney.townhall;

/*
- Worker
1.1 Farm Development speed
2.1 Territory Development speed
3.1 Happiness up
1.2 Market Development speed
2.2 Fortress development speed
3.2 Defense up (adjacent tiles)

- Sword
1.1 Melee Damage
2.1 Ranged evasion
3.1 Critical attack
1.2 Armor
2.2 Melee attack speed
3.2 Guardian(Consume companion's damages)

- Archer
1.1 Melee evasion
2.1 Ranged damage
3.1 Point blank (zero ranged attack)
1.2 Ranged attack speed
2.2 Ranged evasion
3.2 Poison arrow (dot damage)

- Horse
1.1 Ranged evasion
2.1 Melee attack speed
3.1 Move speed
1.2 Armor
2.2 Melee damage
3.2 Stun

- Healer
1.1 Melee Evasion
2.1 Heal power
3.1 Dot heal
1.2 Ranged Evasion
2.2 Heal speed
3.2 Heal splash attack

- Cannon
1.1 Armor
2.1 Ranged damage
3.1 Splash range
1.2 Move speed
2.2 Ranged attack speed
3.2 Slowness

- Paladin
1.1 Ranged evasion
2.1 Melee damage
3.1 Aura (companion attack speed up)
1.2 Melee evasion
2.2 Health
3.2 Invincible
 */

import com.lifejourney.engine2d.Engine2D;

import java.util.ArrayList;
import java.util.Arrays;

enum Upgradable {
    WORKER_FARM_DEVELOPMENT_SPEED(
            Engine2D.GetInstance().getString(R.string.farm_development),
            Engine2D.GetInstance().getString(R.string.farm_development_lv1),
            Engine2D.GetInstance().getString(R.string.farm_development_lv2),
            Engine2D.GetInstance().getString(R.string.farm_development_lv3),
            null,
            150,
            15,
            Unit.UnitClass.WORKER
    ),
    WORKER_DOWNTOWN_DEVELOPMENT_SPEED(
            Engine2D.GetInstance().getString(R.string.downtown_development),
            Engine2D.GetInstance().getString(R.string.downtown_development_lv1),
            Engine2D.GetInstance().getString(R.string.downtown_development_lv2),
            Engine2D.GetInstance().getString(R.string.downtown_development_lv3),
            WORKER_FARM_DEVELOPMENT_SPEED,
            300,
            25,
            Unit.UnitClass.WORKER
    ),
    WORKER_HAPPINESS(
            Engine2D.GetInstance().getString(R.string.happiness),
            Engine2D.GetInstance().getString(R.string.happiness_lv1),
            Engine2D.GetInstance().getString(R.string.happiness_lv2),
            Engine2D.GetInstance().getString(R.string.happiness_lv3),
            WORKER_DOWNTOWN_DEVELOPMENT_SPEED,
            450,
            35,
            Unit.UnitClass.WORKER
    ),
    WORKER_MARKET_DEVELOPMENT_SPEED(
            Engine2D.GetInstance().getString(R.string.market_development),
            Engine2D.GetInstance().getString(R.string.market_development_lv1),
            Engine2D.GetInstance().getString(R.string.market_development_lv2),
            Engine2D.GetInstance().getString(R.string.market_development_lv3),
            null,
            150,
            15,
            Unit.UnitClass.WORKER
    ),
    WORKER_FORTRESS_DEVELOPMENT_SPEED(
            Engine2D.GetInstance().getString(R.string.fortress_development),
            Engine2D.GetInstance().getString(R.string.fortress_development_lv1),
            Engine2D.GetInstance().getString(R.string.fortress_development_lv2),
            Engine2D.GetInstance().getString(R.string.fortress_development_lv3),
            WORKER_MARKET_DEVELOPMENT_SPEED,
            300,
            25,
            Unit.UnitClass.WORKER
    ),
    WORKER_DEFENSE(
            Engine2D.GetInstance().getString(R.string.defense_development),
            Engine2D.GetInstance().getString(R.string.defense_development_lv1),
            Engine2D.GetInstance().getString(R.string.defense_development_lv2),
            Engine2D.GetInstance().getString(R.string.defense_development_lv3),
            WORKER_FORTRESS_DEVELOPMENT_SPEED,
            450,
            35,
            Unit.UnitClass.WORKER
    ),

    FIGHTER_MELEE_DAMAGE(
            Engine2D.GetInstance().getString(R.string.melee_damage),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv1),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv2),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv3),
            null,
            150,
            15,
            Unit.UnitClass.FIGHTER
    ),
    FIGHTER_MELEE_ATTACK_SPEED(
            Engine2D.GetInstance().getString(R.string.melee_attack_speed),
            Engine2D.GetInstance().getString(R.string.melee_attack_speed_lv1),
            Engine2D.GetInstance().getString(R.string.melee_attack_speed_lv2),
            Engine2D.GetInstance().getString(R.string.melee_attack_speed_lv3),
            FIGHTER_MELEE_DAMAGE,
            300,
            25,
            Unit.UnitClass.FIGHTER
    ),
    FIGHTER_CRITICAL_ATTACK(
            Engine2D.GetInstance().getString(R.string.melee_critical_attack),
            Engine2D.GetInstance().getString(R.string.melee_critical_attack_lv1),
            Engine2D.GetInstance().getString(R.string.melee_critical_attack_lv2),
            Engine2D.GetInstance().getString(R.string.melee_critical_attack_lv3),
            FIGHTER_MELEE_ATTACK_SPEED,
            450,
            35,
            Unit.UnitClass.FIGHTER
    ),
    FIGHTER_ARMOR(
            Engine2D.GetInstance().getString(R.string.melee_armor),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv1),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv2),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv3),
            null,
            150,
            15,
            Unit.UnitClass.FIGHTER
    ),
    FIGHTER_MELEE_EVASION(
            Engine2D.GetInstance().getString(R.string.melee_evade),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv1),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv2),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv3),
            FIGHTER_ARMOR,
            300,
            25,
            Unit.UnitClass.FIGHTER
    ),
    FIGHTER_BUFF(
            Engine2D.GetInstance().getString(R.string.buff),
            Engine2D.GetInstance().getString(R.string.buff_lv1),
            Engine2D.GetInstance().getString(R.string.buff_lv2),
            Engine2D.GetInstance().getString(R.string.buff_lv3),
            FIGHTER_MELEE_EVASION,
            450,
            35,
            Unit.UnitClass.FIGHTER
    ),

    ARCHER_MELEE_EVASION(
            Engine2D.GetInstance().getString(R.string.melee_evade),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv1),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv2),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv3),
            null,
            150,
            15,
            Unit.UnitClass.ARCHER
    ),
    ARCHER_ARMOR(
            Engine2D.GetInstance().getString(R.string.melee_armor),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv1),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv2),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv3),
            ARCHER_MELEE_EVASION,
            300,
            25,
            Unit.UnitClass.ARCHER
    ),
    ARCHER_POINT_BLANK(
            Engine2D.GetInstance().getString(R.string.point_blank),
            Engine2D.GetInstance().getString(R.string.point_blank_lv1),
            Engine2D.GetInstance().getString(R.string.point_blank_lv2),
            Engine2D.GetInstance().getString(R.string.point_blank_lv3),
            ARCHER_ARMOR,
            450,
            35,
            Unit.UnitClass.ARCHER
    ),
    ARCHER_RANGED_ATTACK_SPEED(
            Engine2D.GetInstance().getString(R.string.ranged_attack_speed),
            Engine2D.GetInstance().getString(R.string.ranged_attack_speed_lv1),
            Engine2D.GetInstance().getString(R.string.ranged_attack_speed_lv2),
            Engine2D.GetInstance().getString(R.string.ranged_attack_speed_lv3),
            null,
            150,
            15,
            Unit.UnitClass.ARCHER
    ),
    ARCHER_RANGED_DAMAGE(
            Engine2D.GetInstance().getString(R.string.ranged_damage),
            Engine2D.GetInstance().getString(R.string.ranged_damage_lv1),
            Engine2D.GetInstance().getString(R.string.ranged_damage_lv2),
            Engine2D.GetInstance().getString(R.string.ranged_damage_lv3),
            ARCHER_RANGED_ATTACK_SPEED,
            300,
            25,
            Unit.UnitClass.ARCHER
    ),
    ARCHER_POISON_ARROW(
            Engine2D.GetInstance().getString(R.string.poison_arrow),
            Engine2D.GetInstance().getString(R.string.poison_arrow_lv1),
            Engine2D.GetInstance().getString(R.string.poison_arrow_lv2),
            Engine2D.GetInstance().getString(R.string.poison_arrow_lv3),
            ARCHER_RANGED_DAMAGE,
            450,
            35,
            Unit.UnitClass.ARCHER
    ),

    HORSE_MAN_RANGED_EVASION(
            Engine2D.GetInstance().getString(R.string.ranged_evade),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv1),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv2),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv3),
            null,
            150,
            15,
            Unit.UnitClass.HORSE_MAN
    ),
    HORSE_MAN_ARMOR(
            Engine2D.GetInstance().getString(R.string.melee_armor),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv1),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv2),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv3),
            HORSE_MAN_RANGED_EVASION,
            300,
            25,
            Unit.UnitClass.HORSE_MAN
    ),
    HORSE_MAN_MOVE_SPEED(
            Engine2D.GetInstance().getString(R.string.move_speed_upgrade),
            Engine2D.GetInstance().getString(R.string.move_speed_upgrade_lv1),
            Engine2D.GetInstance().getString(R.string.move_speed_upgrade_lv2),
            Engine2D.GetInstance().getString(R.string.move_speed_upgrade_lv3),
            HORSE_MAN_ARMOR,
            450,
            35,
            Unit.UnitClass.HORSE_MAN
    ),
    HORSE_MAN_MELEE_ATTACK_SPEED(
            Engine2D.GetInstance().getString(R.string.melee_attack_speed),
            Engine2D.GetInstance().getString(R.string.melee_attack_speed_lv1),
            Engine2D.GetInstance().getString(R.string.melee_attack_speed_lv2),
            Engine2D.GetInstance().getString(R.string.melee_attack_speed_lv3),
            null,
            150,
            15,
            Unit.UnitClass.HORSE_MAN
    ),
    HORSE_MAN_MELEE_DAMAGE(
            Engine2D.GetInstance().getString(R.string.melee_damage),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv1),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv2),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv3),
            HORSE_MAN_MELEE_ATTACK_SPEED,
            300,
            25,
            Unit.UnitClass.HORSE_MAN
    ),
    HORSE_MAN_STUN(
            Engine2D.GetInstance().getString(R.string.stun),
            Engine2D.GetInstance().getString(R.string.stun_lv1),
            Engine2D.GetInstance().getString(R.string.stun_lv2),
            Engine2D.GetInstance().getString(R.string.stun_lv3),
            HORSE_MAN_MELEE_DAMAGE,
            450,
            35,
            Unit.UnitClass.HORSE_MAN
    ),

    HEALER_HEAL_SPEED(
            Engine2D.GetInstance().getString(R.string.heal_speed_upgrade),
            Engine2D.GetInstance().getString(R.string.heal_speed_upgrade_lv1),
            Engine2D.GetInstance().getString(R.string.heal_speed_upgrade_lv2),
            Engine2D.GetInstance().getString(R.string.heal_speed_upgrade_lv3),
            null,
            150,
            15,
            Unit.UnitClass.HEALER
    ),
    HEALER_HEAL_POWER(
            Engine2D.GetInstance().getString(R.string.heal_power_upgrade),
            Engine2D.GetInstance().getString(R.string.heal_power_upgrade_lv1),
            Engine2D.GetInstance().getString(R.string.heal_power_upgrade_lv2),
            Engine2D.GetInstance().getString(R.string.heal_power_upgrade_lv3),
            HEALER_HEAL_SPEED,
            300,
            25,
            Unit.UnitClass.HEALER
    ),
    HEALER_DOT_HEAL(
            Engine2D.GetInstance().getString(R.string.dot_heal),
            Engine2D.GetInstance().getString(R.string.dot_heal_lv1),
            Engine2D.GetInstance().getString(R.string.dot_heal_lv2),
            Engine2D.GetInstance().getString(R.string.dot_heal_lv3),
            HEALER_HEAL_POWER,
            450,
            35,
            Unit.UnitClass.HEALER
    ),
    HEALER_MELEE_EVASION(
            Engine2D.GetInstance().getString(R.string.melee_evade),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv1),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv2),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv3),
            null,
            150,
            15,
            Unit.UnitClass.HEALER
    ),
    HEALER_RANGED_EVASION(
            Engine2D.GetInstance().getString(R.string.ranged_evade),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv1),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv2),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv3),
            HEALER_MELEE_EVASION,
            300,
            25,
            Unit.UnitClass.HEALER
    ),
    HEALER_SPLASH_DAMAGE(
            Engine2D.GetInstance().getString(R.string.spike_heal),
            Engine2D.GetInstance().getString(R.string.spike_heal_lv1),
            Engine2D.GetInstance().getString(R.string.spike_heal_lv2),
            Engine2D.GetInstance().getString(R.string.spike_heal_lv3),
            HEALER_RANGED_EVASION,
            450,
            35,
            Unit.UnitClass.HEALER
    ),

    CANNON_RANGED_ATTACK_SPEED(
            Engine2D.GetInstance().getString(R.string.ranged_attack_speed),
            Engine2D.GetInstance().getString(R.string.ranged_attack_speed_lv1),
            Engine2D.GetInstance().getString(R.string.ranged_attack_speed_lv2),
            Engine2D.GetInstance().getString(R.string.ranged_attack_speed_lv3),
            null,
            150,
            15,
            Unit.UnitClass.CANNON
    ),
    CANNON_RANGED_DAMAGE(
            Engine2D.GetInstance().getString(R.string.ranged_damage),
            Engine2D.GetInstance().getString(R.string.ranged_damage_lv1),
            Engine2D.GetInstance().getString(R.string.ranged_damage_lv2),
            Engine2D.GetInstance().getString(R.string.ranged_damage_lv3),
            CANNON_RANGED_ATTACK_SPEED,
            300,
            25,
            Unit.UnitClass.CANNON
    ),
    CANNON_SPLASH_RANGE(
            Engine2D.GetInstance().getString(R.string.splash_radius),
            Engine2D.GetInstance().getString(R.string.splash_radius_lv1),
            Engine2D.GetInstance().getString(R.string.splash_radius_lv2),
            Engine2D.GetInstance().getString(R.string.splash_radius_lv3),
            CANNON_RANGED_DAMAGE,
            450,
            35,
            Unit.UnitClass.CANNON
    ),
    CANNON_ARMOR(
            Engine2D.GetInstance().getString(R.string.melee_armor),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv1),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv2),
            Engine2D.GetInstance().getString(R.string.melee_armor_lv3),
            null,
            150,
            15,
            Unit.UnitClass.CANNON
    ),
    CANNON_MOVE_SPEED(
            Engine2D.GetInstance().getString(R.string.cannon_move_speed_upgrade),
            Engine2D.GetInstance().getString(R.string.cannon_move_speed_upgrade_lv1),
            Engine2D.GetInstance().getString(R.string.cannon_move_speed_upgrade_lv2),
            Engine2D.GetInstance().getString(R.string.cannon_move_speed_upgrade_lv3),
            CANNON_ARMOR,
            300,
            25,
            Unit.UnitClass.CANNON
    ),
    CANNON_SLOWNESS(
            Engine2D.GetInstance().getString(R.string.sticky_cannon),
            Engine2D.GetInstance().getString(R.string.sticky_cannon_lv1),
            Engine2D.GetInstance().getString(R.string.sticky_cannon_lv2),
            Engine2D.GetInstance().getString(R.string.sticky_cannon_lv3),
            CANNON_MOVE_SPEED,
            450,
            35,
            Unit.UnitClass.CANNON
    ),

    PALADIN_RANGED_EVASION(
            Engine2D.GetInstance().getString(R.string.ranged_evade),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv1),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv2),
            Engine2D.GetInstance().getString(R.string.ranged_evade_lv3),
            null,
            150,
            15,
            Unit.UnitClass.PALADIN
    ),
    PALADIN_MELEE_EVASION(
            Engine2D.GetInstance().getString(R.string.melee_evade),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv1),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv2),
            Engine2D.GetInstance().getString(R.string.melee_evade_lv3),
            PALADIN_RANGED_EVASION,
            300,
            25,
            Unit.UnitClass.PALADIN
    ),
    PALADIN_INVINCIBLE(
            Engine2D.GetInstance().getString(R.string.invincible),
            Engine2D.GetInstance().getString(R.string.invincible_lv1),
            Engine2D.GetInstance().getString(R.string.invincible_lv2),
            Engine2D.GetInstance().getString(R.string.invincible_lv3),
            PALADIN_RANGED_EVASION,
            450,
            35,
            Unit.UnitClass.PALADIN
    ),
    PALADIN_HEALTH(
            Engine2D.GetInstance().getString(R.string.health_upgrade),
            Engine2D.GetInstance().getString(R.string.health_upgrade_lv1),
            Engine2D.GetInstance().getString(R.string.health_upgrade_lv2),
            Engine2D.GetInstance().getString(R.string.health_upgrade_lv3),
            null,
            150,
            15,
            Unit.UnitClass.PALADIN
    ),
    PALADIN_MELEE_DAMAGE(
            Engine2D.GetInstance().getString(R.string.melee_damage),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv1),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv2),
            Engine2D.GetInstance().getString(R.string.melee_damage_lv3),
            PALADIN_HEALTH,
            300,
            25,
            Unit.UnitClass.PALADIN
    ),
    PALADIN_GUARDIAN(
            Engine2D.GetInstance().getString(R.string.guardian),
            Engine2D.GetInstance().getString(R.string.guardian_lv1),
            Engine2D.GetInstance().getString(R.string.guardian_lv2),
            Engine2D.GetInstance().getString(R.string.guardian_lv3),
            PALADIN_MELEE_DAMAGE,
            450,
            35,
            Unit.UnitClass.PALADIN
    );

    Upgradable(String title, String descriptionLv1, String descriptionLv2, String descriptionLv3, Upgradable parent,
               int purchaseCost, int upkeepCost, Unit.UnitClass relatedUnitClass) {
        this.title = title;
        this.descriptionLv1 = descriptionLv1;
        this.descriptionLv2 = descriptionLv2;
        this.descriptionLv3 = descriptionLv3;
        this.parent = parent;
        this.purchaseCost = purchaseCost;
        this.upkeepCost = upkeepCost;
        this.relatedUnitClass = relatedUnitClass;
    }

    String getTitle() {
        return title;
    }

    String getDescriptionLv1() {
        return descriptionLv1;
    }

    String getDescriptionLv2() {
        return descriptionLv2;
    }

    String getDescriptionLv3() {
        return descriptionLv3;
    }

    int getLevel(Tribe.Faction faction) {
        return levels[faction.ordinal()];
    }

    void setLevel(Tribe.Faction faction, int level) {
        this.levels[faction.ordinal()] = level;
    }

    Upgradable getParent() {
        return parent;
    }

    int getPurchaseCost() {
        return purchaseCost;
    }

    int getUpkeepCost() {
        return upkeepCost;
    }

    Unit.UnitClass getRelatedUnitClass() {
        return relatedUnitClass;
    }

    static void reset() {
        for (Upgradable upgradable : Upgradable.values()) {
            Arrays.fill(upgradable.levels, 0);
        }
    }

    static int getTotalUpkeep(Tribe.Faction faction) {
        int totalUpkeep = 0;
        for (Upgradable upgradable : Upgradable.values()) {
            totalUpkeep +=
                    upgradable.upkeepCost * upgradable.levels[Tribe.Faction.VILLAGER.ordinal()];
        }
        return totalUpkeep;
    }

    private String title;
    private String descriptionLv1;
    private String descriptionLv2;
    private String descriptionLv3;
    private Upgradable parent;
    private int purchaseCost;
    private int upkeepCost;
    private int[] levels = new int[Tribe.Faction.values().length];
    private Unit.UnitClass relatedUnitClass;
}
