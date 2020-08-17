package com.lifejourney.townhall;

import java.util.ArrayList;

interface MissionAction {
    void doEvent(Mission mission, ArrayList<Tribe> tribes, GameMap map);
    boolean checkVictory();
}

enum Mission {
    LV1("map.png",
            "굶주림",
            "당신은 어느 작은 마을의 촌장입니다.\n" +
                    "유난히 혹독했던 지난 겨울 당신은 많은\n" +
                    "친구들을 잃었습니다. 하지만 이제 애도의\n" +
                    "묵념을 마치고 다시 일어설 때입니다.\n" +
                    "미래를 대비해 마을을 정돈하고 인구를\n" +
                    "충분히 회복해야 합니다.",
            "전체 인구 20 이상",
            100,
            new MissionAction() {
                @Override
                public void doEvent(Mission mission, ArrayList<Tribe> tribes, GameMap map) {

                }

                @Override
                public boolean checkVictory() {
                    return false;
                }
            }),
    LV2("map.png",
            "조짐",
            "마을 근처에 수상한 도적들이 출몰하기\n" +
                    "시작했습니다. 병사들을 보내 처리해야\n" +
                    "합니다. 적절히 지휘를 한다면 어렵지\n" +
                    "않을 것입니다.",
            "도적 본부 점령",
            100,
            new MissionAction() {
                @Override
                public void doEvent(Mission mission, ArrayList<Tribe> tribes, GameMap map) {

                }

                @Override
                public boolean checkVictory() {
                    return false;
                }
            });

    Mission(String mapFile, String title, String description, String victoryCondition, int timeLimit,
            MissionAction action) {
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

    public int getStar() {
        return star;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    private String mapFile;
    private String title;
    private String description;
    private String victoryCondition;
    private int timeLimit;
    private MissionAction func;
    private boolean unlocked = true;
    private int star = 0;
}
