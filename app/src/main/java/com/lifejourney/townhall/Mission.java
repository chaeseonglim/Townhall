package com.lifejourney.townhall;

enum Mission {
    LV1("map/map_lv1.png",
            "굶주림 [튜토리얼]",
            "당신은 어느 작은 마을의 촌장입니다.\n" +
                    "유난히 혹독했던 지난 겨울 당신은 많은 " +
                    "친구들을 잃었습니다. 하지만 이제 애도를 " +
                    "마치고 다시 일어설 때입니다. 미래를 대비해 " +
                    "마을을 정돈하고 인구를 충분히 회복해야 " +
                    "합니다.",
            "전체 인구 100 이상",
            100) {

        public void update(MainGame game) {
            // Start the tutorial for management
            if (++turn == 30) {
                game.pauseForWidget();

                TutorialGuideForManagement tutorialGuideForManagement =
                        new TutorialGuideForManagement(game, game);
                tutorialGuideForManagement.show();
                game.addWidget(tutorialGuideForManagement);
            }

            if (((Villager)game.getTribe(Tribe.Faction.VILLAGER)).getTotalPopulation() > 100) {
                int day = game.getDays();
            }
        }

        private int turn = 0;
    },
    LV2("map/map_lv2.png",
            "조짐",
            "마을 근처에 수상한 도적들이 출몰하기 " +
                    "시작했습니다. 마을 사람들이 불안해하고 " +
                    "있습니다. 당신의 병사들이 준비되었는지 " +
                    "확인해 볼 수 있는 좋은 기회입니다.",
            "도적 본부 점령",
            100) {

        public void update(MainGame game) {
        }
    };

    abstract void update(MainGame game);

    Mission(String mapFile, String title, String description, String victoryCondition, int timeLimit) {
        this.mapFile = mapFile;
        this.title = title;
        this.description = description;
        this.victoryCondition = victoryCondition;
        this.timeLimit = timeLimit;
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

    private String mapFile;
    private String title;
    private String description;
    private String victoryCondition;
    private int timeLimit;
    private boolean unlocked = true;
    private int starRating = 0;
}
