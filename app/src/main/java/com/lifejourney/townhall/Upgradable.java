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

import java.util.Arrays;

enum Upgradable {
    WORKER_FARM_DEVELOPMENT_SPEED(
            "농장 개발",
            "농장 개발 +1",
            "농장 개발 +2",
            "농장 개발 +3",
            null,
            1000,
            100
    ),
    WORKER_DOWNTOWN_DEVELOPMENT_SPEED(
            "도시 개발",
            "도시 개발 +1",
            "도시 개발 +2",
            "도시 개발 +3",
            WORKER_FARM_DEVELOPMENT_SPEED,
            2000,
            200
    ),
    WORKER_HAPPINESS(
            "행복도",
            "주변 타일 행복도 +5",
            "주변 타일 행복도 +10",
            "주변 타일 행복도 +15",
            WORKER_DOWNTOWN_DEVELOPMENT_SPEED,
            3000,
            300
    ),
    WORKER_MARKET_DEVELOPMENT_SPEED(
            "시장 개발",
            "시장 개발 +1",
            "시장 개발 +2",
            "시장 개발 +3",
            null,
            1000,
            100
    ),
    WORKER_FORTRESS_DEVELOPMENT_SPEED(
            "요새 개발",
            "요새 개발 +1",
            "요새 개발 +2",
            "요새 개발 +3",
            WORKER_MARKET_DEVELOPMENT_SPEED,
            2000,
            200
    ),
    WORKER_DEFENSE(
            "지역 방어",
            "주변 타일 방어도 +1",
            "주변 타일 방어도 +2",
            "주변 타일 방어도 +3",
            WORKER_FORTRESS_DEVELOPMENT_SPEED,
            3000,
            300
    ),

    FIGHTER_MELEE_DAMAGE(
            "근접 공격력",
            "근접 공격력 +5%",
            "근접 공격력 +10%",
            "근접 공격력 +15%",
            null,
            1000,
            100
    ),
    FIGHTER_MELEE_ATTACK_SPEED(
            "근접 공격 속도",
            "근접 공격 속도 +5%",
            "근접 공격 속도 +10%",
            "근접 공격 속도 +15%",
            FIGHTER_MELEE_DAMAGE,
            2000,
            200
    ),
    FIGHTER_CRITICAL_ATTACK(
            "치명타",
            "치명타 확률 +5%",
            "치명타 확률 +10%",
            "치명타 확률 +15%",
            FIGHTER_MELEE_ATTACK_SPEED,
            3000,
            300
    ),
    FIGHTER_ARMOR(
            "방어",
            "방어 +5%",
            "방어 +10%",
            "방어 +15%",
            null,
            1000,
            100
    ),
    FIGHTER_MELEE_EVASION(
            "근접 회피",
            "근접 회피 +5%",
            "근접 회피 +10%",
            "근접 회피 +15%",
            FIGHTER_ARMOR,
            2000,
            200
    ),
    FIGHTER_BUFF(
            "아군 강화",
            "아군 공격력 +5%",
            "아군 공격력 +10%",
            "아군 공격력 +15%",
            FIGHTER_MELEE_EVASION,
            3000,
            300
    ),

    ARCHER_MELEE_EVASION(
            "근접 회피",
            "근접 회피 +5%",
            "근접 회피 +10%",
            "근접 회피 +15%",
            null,
            1000,
            100
    ),
    ARCHER_ARMOR(
            "방어",
            "방어 +5%",
            "방어 +10%",
            "방어 +15%",
            ARCHER_MELEE_EVASION,
            2000,
            200
    ),
    ARCHER_POINT_BLANK(
            "근접 화살 공격",
            "근접 화살 공격 가능",
            "근접 화살 피해량 +5%",
            "근접 화살 피해량 +10%",
            ARCHER_ARMOR,
            3000,
            300
    ),
    ARCHER_RANGED_ATTACK_SPEED(
            "원거리 공격 속도",
            "원거리 공격 속도 +5%",
            "원거리 공격 속도 +10%",
            "원거리 공격 속도 +15%",
            null,
            1000,
            100
    ),
    ARCHER_RANGED_DAMAGE(
            "원거리 공격력",
            "원거리 공격력 +5%",
            "원거리 공격력 +10%",
            "원거리 공격력 +15%",
            ARCHER_RANGED_ATTACK_SPEED,
            2000,
            200
    ),
    ARCHER_POISON_ARROW(
            "독 화살",
            "독 피해 추가",
            "독 피해량 +10%",
            "독 피해량 +20%",
            ARCHER_RANGED_DAMAGE,
            3000,
            300
    ),

    HORSE_MAN_RANGED_EVASION(
            "원거리 회피",
            "원거리 회피 +5%",
            "원거리 회피 +10%",
            "원거리 회피 +15%",
            null,
            1000,
            100
    ),
    HORSE_MAN_ARMOR(
            "방어",
            "방어 +5%",
            "방어 +10%",
            "방어 +15%",
            HORSE_MAN_RANGED_EVASION,
            2000,
            200
    ),
    HORSE_MAN_MOVE_SPEED(
            "쾌속 질주",
            "이동 속도 +10%",
            "이동 속도 +20%",
            "이동 속도 +30%",
            HORSE_MAN_ARMOR,
            3000,
            300
    ),
    HORSE_MAN_MELEE_ATTACK_SPEED(
            "근접 공격 속도",
            "근접 공격 속도 +5%",
            "근접 공격 속도 +10%",
            "근접 공격 속도 +15%",
            null,
            1000,
            100
    ),
    HORSE_MAN_MELEE_DAMAGE(
            "근접 공격력",
            "근접 공격력 +5%",
            "근접 공격력 +10%",
            "근접 공격력 +15%",
            HORSE_MAN_MELEE_ATTACK_SPEED,
            2000,
            200
    ),
    HORSE_MAN_STUN(
            "기절",
            "기절 확률 추가",
            "기절 확률 +5%",
            "기절 확률 +10%",
            HORSE_MAN_MELEE_DAMAGE,
            3000,
            300
    ),

    HEALER_HEAL_SPEED(
            "치유 속도",
            "치유 속도 +5%",
            "치유 속도 +10%",
            "치유 속도 +15%",
            null,
            1000,
            100
    ),
    HEALER_HEAL_POWER(
            "치유력",
            "치유력 강화 +5%",
            "치유력 강화 +10%",
            "치유력 강화 +15%",
            HEALER_HEAL_SPEED,
            2000,
            200
    ),
    HEALER_DOT_HEAL(
            "기적의 치유",
            "전체 점진적인 치유 추가",
            "전체 점진적인 치유력 +10%",
            "전체 점진적인 치유력 +20%",
            HEALER_HEAL_POWER,
            3000,
            300
    ),
    HEALER_MELEE_EVASION(
            "근접 회피",
            "근접 회피 +5%",
            "근접 회피 +10%",
            "근접 회피 +15%",
            null,
            1000,
            100
    ),
    HEALER_RANGED_EVASION(
            "원거리 회피",
            "원거리 회피 +5%",
            "원거리 회피 +10%",
            "원거리 회피 +15%",
            HEALER_MELEE_EVASION,
            2000,
            200
    ),
    HEALER_SPLASH_DAMAGE(
            "치유의 가시",
            "치유시 범위 피해 추가",
            "치유시 범위 피해량 +10%",
            "치유시 범위 피해량 +20%",
            HEALER_RANGED_EVASION,
            3000,
            300
    ),

    CANNON_RANGED_ATTACK_SPEED(
            "원거리 공격 속도",
            "원거리 공격 속도 +5%",
            "원거리 공격 속도 +10%",
            "원거리 공격 속도 +15%",
            null,
            1000,
            100
    ),
    CANNON_RANGED_DAMAGE(
            "원거리 공격력",
            "원거리 공격력 +5%",
            "원거리 공격력 +10%",
            "원거리 공격력 +15%",
            CANNON_RANGED_ATTACK_SPEED,
            2000,
            200
    ),
    CANNON_SPLASH_RANGE(
            "피해 범위",
            "피해 범위 +10%",
            "피해 범위 +20%",
            "피해 범위 +30%",
            CANNON_RANGED_DAMAGE,
            3000,
            300
    ),
    CANNON_ARMOR(
            "방어",
            "방어 강화 +5%",
            "방어 강화 +10%",
            "방어 강화 +15%",
            null,
            1000,
            100
    ),
    CANNON_MOVE_SPEED(
            "이동 속도",
            "이동 속도 +10%",
            "이동 속도 +20%",
            "이동 속도 +30%",
            CANNON_ARMOR,
            2000,
            200
    ),
    CANNON_SLOWNESS(
            "끈적이 포탄",
            "피해시 이속 -5%",
            "피해시 이속 -10%",
            "피해시 이속 -15%",
            CANNON_MOVE_SPEED,
            3000,
            300
    ),

    PALADIN_RANGED_EVASION(
            "원거리 회피",
            "원거리 회피 +5%",
            "원거리 회피 +10%",
            "원거리 회피 +15%",
            null,
            1000,
            100
    ),
    PALADIN_MELEE_EVASION(
            "근접 회피",
            "근접 회피 +5%",
            "근접 회피 +10%",
            "근접 회피 +15%",
            PALADIN_RANGED_EVASION,
            2000,
            200
    ),
    PALADIN_INVINCIBLE(
            "무적",
            "체력 저하시 무적",
            "무적 지속시간 +5%",
            "무적 지속시간 +10%",
            PALADIN_RANGED_EVASION,
            3000,
            300
    ),
    PALADIN_HEALTH(
            "체력 증가",
            "체력 +5%",
            "체력 +10%",
            "체력 +15%",
            null,
            1000,
            100
    ),
    PALADIN_MELEE_DAMAGE(
            "근접 공격력",
            "근접 공격력 +5%",
            "근접 공격력 +10%",
            "근접 공격력 +15%",
            PALADIN_HEALTH,
            2000,
            200
    ),
    PALADIN_GUARDIAN(
            "아군 보호",
            "아군 일반 피해 전환 +10%",
            "아군 일반 피해 전환 +20%",
            "아군 일반 피해 전환 +30%",
            PALADIN_MELEE_DAMAGE,
            3000,
            300
    );

    Upgradable(String title, String descriptionLv1, String descriptionLv2, String descriptionLv3, Upgradable parent,
               int purchaseCost, int upkeepCost) {
        this.title = title;
        this.descriptionLv1 = descriptionLv1;
        this.descriptionLv2 = descriptionLv2;
        this.descriptionLv3 = descriptionLv3;
        this.parent = parent;
        this.purchaseCost = purchaseCost;
        this.upkeepCost = upkeepCost;
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
}
