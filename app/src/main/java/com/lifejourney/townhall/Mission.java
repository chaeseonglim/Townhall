package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;

enum Mission {
    LV1("map/map_lv1.png",
            "굶주림",
            "당신은 어느 작은 마을의 촌장입니다.\n" +
                    "유난히 혹독했던 지난 겨울 당신은 많은 " +
                    "친구들을 잃었습니다. 하지만 이제 애도를 " +
                    "마치고 다시 일어설 때입니다. 미래를 대비해 " +
                    "마을을 정돈하고 인구를 충분히 회복해야 " +
                    "합니다.",
            "전체 인구 100 이상",
            100,
            new boolean[] { true, false, false, false, false, false, false }) {

        public void init(MainGame game) {
            turn = 0;
        }

        public void update(MainGame game) {
            turn++;

            // Start the tutorial for management
            if (turn == 5) {
                game.startTutorialForManagement();
            }

            int day = game.getDays();
            if (((Villager)game.getTribe(Tribe.Faction.VILLAGER)).getTotalPopulation() > 100) {
                if (day <= getTimeLimit() * 0.6f) {
                    game.missionCompleted(3);
                } else if (day <= getTimeLimit() * 0.8f) {
                    game.missionCompleted(2);
                } else {
                    game.missionCompleted(1);
                }
            }

            if (day > getTimeLimit()) {
                game.missionTimeout();
            }
        }

        private int turn = 0;
    },
    LV2("map/map_lv2.png",
            "조짐",
            "마을 근처에 수상한 도적들이 출몰하기 " +
                    "시작했습니다. 마을 사람들이 불안해 하고 " +
                    "있습니다. 당신의 병사들이 준비되었는지 " +
                    "확인해 볼 수 있는 좋은 기회입니다.",
            "도적 패배",
            100,
            new boolean[] { true, true, true, false, false, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units for this mission
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(bandit.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            OffsetCoord squadMapPosition = villager.getHeadquarterPosition().clone();
            squadMapPosition.offset(1, 0);
            villager.spawnSquad(squadMapPosition.toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        }
        public void update(MainGame game) {
            turn++;

            // Start the tutorial for battle
            if (turn == 5) {
                game.startTutorialForBattle(TutorialGuideForBattle.Step.INTRODUCTION);
            }

            int day = game.getDays();
            if (game.getTribe(Tribe.Faction.BANDIT).isDefeated()) {
                if (day <= getTimeLimit() * 0.6f) {
                    game.missionCompleted(3);
                } else if (day <= getTimeLimit() * 0.8f) {
                    game.missionCompleted(2);
                } else {
                    game.missionCompleted(1);
                }
            }

            if (day > getTimeLimit()) {
                game.missionTimeout();
            }
        }

        private int turn = 0;
    };

    abstract void init(MainGame game);
    abstract void update(MainGame game);

    Mission(String mapFile, String title, String description, String victoryCondition, int timeLimit,
            boolean[] recruitAvailable) {
        this.mapFile = mapFile;
        this.title = title;
        this.description = description;
        this.victoryCondition = victoryCondition;
        this.timeLimit = timeLimit;
        this.recruitAvailable = recruitAvailable;
    }

    public String getMapFile() {
        return mapFile;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVictoryCondition() {
        return victoryCondition;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getStarRating() {
        return starRating;
    }

    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public boolean[] getRecruitAvailable() {
        return recruitAvailable;
    }

    private String mapFile;
    private String title;
    private String description;
    private String victoryCondition;
    private int timeLimit;
    private boolean unlocked = true;
    private int starRating = 0;
    private boolean[] recruitAvailable = null;
}
