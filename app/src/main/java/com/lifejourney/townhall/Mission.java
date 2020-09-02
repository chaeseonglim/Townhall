package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;

enum Mission {
    LV1("map/map_lv1.png",
            "굶주림",
            "당신은 어느 작은 마을의 촌장입니다.\n\n" +
                    "유난히 혹독했던 지난 겨울, 당신은 많은 " +
                    "친구들을 잃었습니다. 하지만 이제 애도를 " +
                    "마치고 다시 일어설 때입니다. \n\n미래를 대비해 " +
                    "마을을 정돈하고 인구를 충분히 회복해야 " +
                    "합니다.",
            "전체 인구 100 이상",
            250,
            100,
            new boolean[] { true, false, false, false, false, false, false }
            ) {

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
            "마을 근처에 도적들이 접근하고 있습니다." +
                    "마을 사람들이 불안해 하기 전에 처리해야 합니다." +
                    "\n\n당신의 전술적 지식을 확인해 볼 수 있는 좋은 기회입니다.",
            "도적 패배",
            250,
            100,
            new boolean[] { true, true, true, false, false, false, false }
            ) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units for this mission
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
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
    },
    LV3("map/map_lv3.png",
                "당근 시장",
                "훌륭합니다!\n우리 병사들이 마을 근처에 접근한 도적들을 물리쳤습니다." +
                        " 하지만 그들은 다시 올 것입니다. 만약을 대비해 많은 금화를 모아야 합니다." +
                        " \n\n마을 근처에 큰 시장이 있다면 도움이 될 것입니다.",
                "시장 Lv4 1개 / 금화 4000 보유",
                250,
                150,
                new boolean[] { true, true, true, false, false, false, false }
                ) {

        public void init(MainGame game) {
            turn = 0;

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.WORKER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();
            if (((Villager)game.getTribe(Tribe.Faction.VILLAGER)).getGold() > 4000) {
                for (Territory territory: game.getTribe(Tribe.Faction.VILLAGER).getTerritories()) {
                    if (territory.getFacilityLevel(Territory.Facility.MARKET) >= 4) {
                        if (day <= getTimeLimit() * 0.6f) {
                            game.missionCompleted(3);
                        } else if (day <= getTimeLimit() * 0.8f) {
                            game.missionCompleted(2);
                        } else {
                            game.missionCompleted(1);
                        }
                        break;
                    }
                }
            }

            if (day > getTimeLimit()) {
                game.missionTimeout();
            }
        }

        private int turn = 0;
    },
    LV4("map/map_lv4.png",
                "금화 맛",
                "당신의 예상대로 북쪽 숲 너머에서 도적들이 다시 움직이기 시작했습니다." +
                " 하지만 일찌감치 시장이 활성화 되어 마을에는 많은 금화가 모였습니다." +
                " \n\n도적들에게 당신의 힘을 보여줄 기회입니다.",
                "도적 패배",
                4000,
                200,
                new boolean[] { true, true, true, false, false, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(bandit.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            OffsetCoord squadMapPosition = bandit.getHeadquarterPosition().clone();
            squadMapPosition.offset(-1, 0);
            bandit.spawnSquad(squadMapPosition.toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.WORKER);
        }
        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            if (turn == 1600) {
                // Prevent AI control units at the beginning
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

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
    },
    LV5("map/map_lv5.png",
                "내 꿈은 시장님",
                "마을 근처의 시장은 매우 번창하고 있습니다. 많은 사람들이 당신의 지혜와 추진력을 인정하고 있습니다." +
                " 물론 여전히 도적들이 근처에 나타나고 있지만 별 문제는 아닐 것입니다." +
                " \n\n마을을 더 키워 큰 도시로 만들어주세요.",
                "마을 & 시장 각각 Lv5 1개 이상 보유",
                2000,
                300,
                new boolean[] { true, true, true, false, false, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(bandit.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            OffsetCoord squadMapPosition = bandit.getHeadquarterPosition().clone();
            squadMapPosition.offset(1, 0);
            bandit.spawnSquad(squadMapPosition.toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            squadMapPosition = bandit.getHeadquarterPosition().clone();
            squadMapPosition.offset(0, -1);
            bandit.spawnSquad(squadMapPosition.toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.WORKER);
        }
        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            if (turn == 3000) {
                // Prevent AI control units at the beginning
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            boolean marketDone = false, downtownDone = false;
            for (Territory territory: game.getTribe(Tribe.Faction.VILLAGER).getTerritories()) {
                if (territory.getFacilityLevel(Territory.Facility.MARKET) == 5) {
                    marketDone = true;
                } else if (territory.getFacilityLevel(Territory.Facility.DOWNTOWN) == 5) {
                    downtownDone = true;
                }

                if (marketDone && downtownDone) {
                    if (day <= getTimeLimit() * 0.6f) {
                        game.missionCompleted(3);
                    } else if (day <= getTimeLimit() * 0.8f) {
                        game.missionCompleted(2);
                    } else {
                        game.missionCompleted(1);
                    }
                    break;
                }
            }

            if (day > getTimeLimit()) {
                game.missionTimeout();
            }
        }

        private int turn = 0;
    },
    LV6("map/map_lv6.png",
                "병원 개원",
                "마을이 매우 번창해지자 흩어져있던 도적들이 다시 모이기 시작했습니다." +
                " 이번에는 도적의 규모가 지금까지와는 다릅니다." +
                " 위기의 순간 당신은 남쪽에 치유사들이 모여 사는 제단이 있다는 소문을 떠올립니다. " +
                " \n\n도적을 물리치고 마을을 지켜내야 합니다.",
                "도적 패배",
                4000,
                300,
                new boolean[] { true, true, true, false, false, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Initialize recruiting availability
            setRecruitAvailable(new boolean[] { true, true, true, false, false, false, false });

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(0, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(1, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(8, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Rebel rebel = (Rebel)game.getTribe(Tribe.Faction.REBEL);
            game.getMap().getTerritory(new OffsetCoord(8, 8)).setFogState(Territory.FogState.MIST);
            rebel.setRetretable(false);
            rebel.setControlledByAI(false);
            rebel.spawnSquad(new OffsetCoord(8, 8).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
            rebel.spawnSquad(new OffsetCoord(7, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.WORKER, Unit.UnitClass.WORKER, Unit.UnitClass.WORKER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            if (turn == 3000) {
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Check if shrine is taken
            if (!shrineTaken &&
                    game.getMap().getTerritory(new OffsetCoord(8, 8)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                shrineTaken = true;
                setRecruitAvailable(new boolean[] { true, true, true, false, true, false, false });
                game.addNews("치유사들이 지금부터 당신을 돕기로 결정했습니다.");
                game.popupMsgBox("당신은 치유사들에게 고개를 숙여 마을을 돕기를 청합니다..\n\n그들은 잠시의 침묵 후 당신을 따라 나섭니다..");

                // spawn a bonus healer squad
                Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
                villager.spawnSquad(new OffsetCoord(7, 8).toGameCoord(),
                        Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
            }

            // Check if bandits are defeated
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
        private boolean shrineTaken = false;
    },
    LV7("map/map_lv7.png",
                "바이킹 등장",
                "당신은 치유사들의 도움으로 계속해서 몰려드는 도적들을 간신히 막아냈습니다." +
                " 하지만 한숨 돌리기도 전에 남서쪽 강 너머에서 나팔 소리가 들려옵니다." +
                " \n\n촌장님, 바이킹입니다!",
                "200일간 생존",
                4000,
                200,
                new boolean[] { true, true, true, false, true, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(0, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            bandit.spawnSquad(new OffsetCoord(1, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            game.getMap().getTerritory(viking.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            viking.setDifficultyFactor(0.5f);
            viking.setControlledByAI(false);
            viking.spawnSquad(new OffsetCoord(0, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            viking.spawnSquad(new OffsetCoord(0, 6).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.WORKER, Unit.UnitClass.WORKER, Unit.UnitClass.WORKER);
            villager.spawnSquad(new OffsetCoord(2, 3).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(3, 4).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            if (turn == 1000) {
                // Prevent AI control units at the beginning
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            } else if (turn == 3000) {
                // Prevent AI control units at the beginning
                Viking viking = (Viking) game.getTribe(Tribe.Faction.VIKING);
                viking.setControlledByAI(true);
                game.addNews("조심하세요!! 바이킹이 이제 활동을 시작합니다.");
            }

            if (day > getTimeLimit()) {
                game.missionCompleted(3);
            }
        }

        private int turn = 0;
    },
    LV8("map/map_lv8.png",
                "바람의 신",
                "바이킹은 우리의 모든 것을 불태웠습니다. 당신과 생존자들은 치유의 제단 근처로 가까스로 대피했습니다." +
                " 도적은 여전히 남아서 우리의 터전을 약탈하고 있습니다." +
                " \n\n이곳에서 북으로 올라가면 바람의 제단이 있습니다. 그곳에서부터 실마리를 찾아야 합니다.",
                "도적 본부 점령",
                2000,
                300,
                new boolean[] { true, true, true, false, true, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Initialize recruiting availability
            setRecruitAvailable(new boolean[] { true, true, true, false, true, false, false });

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(4, 3).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(2, 5).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            bandit.spawnSquad(new OffsetCoord(4, 4).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Rebel rebel = (Rebel)game.getTribe(Tribe.Faction.REBEL);
            game.getMap().getTerritory(new OffsetCoord(7, 1)).setFogState(Territory.FogState.MIST);
            rebel.setRetretable(false);
            rebel.setControlledByAI(false);
            rebel.spawnSquad(new OffsetCoord(7, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(new OffsetCoord(7, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(8, 7).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.ARCHER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
            if (turn == 3000) {
                bandit.setControlledByAI(true);
                bandit.setPolicy(HostileTribe.Policy.DEFENSIVE);
            } else if (turn == 5000) {
                // Prevent AI control units at the beginning
                bandit.setControlledByAI(true);
                bandit.setPolicy(HostileTribe.Policy.EXPANSION);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Check if shrine is taken
            if (!shrineTaken &&
                    game.getMap().getTerritory(new OffsetCoord(7, 1)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                shrineTaken = true;
                setRecruitAvailable(new boolean[] { true, true, true, true, true, false, false });
                game.addNews("바람의 신은 당신의 기도에 응답했습니다.");
                game.popupMsgBox("당신은 제단에 기도를 올립니다..\n\n어느새 날이 어두워지고 일어서는 찰나 어디선가 동물의 울음소리가 들려옵니다..");

                // spawn a bonus healer squad
                Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
                villager.spawnSquad(new OffsetCoord(8, 0).toGameCoord(),
                        Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
                villager.spawnSquad(new OffsetCoord(8, 1).toGameCoord(),
                        Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            }

            // Check if bandits are defeated
            if (game.getMap().getTerritory(bandit.getHeadquarterPosition()).getFaction() == Tribe.Faction.VILLAGER) {
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
        private boolean shrineTaken = false;
    },
    LV9("map/map_lv9.png",
                "마을 탈환",
                "우리의 기마병 도적의 선봉을 격파했습니다." +
                        " 이대로 밀어붙여 도적으로부터 마을을 되찾아야 합니다." +
                " \n\n남아있는 도적들을 물리치고 다시 마을을 되찾아야 합니다. 마을을 복구하려면 많은 노력이 필요할 것입니다.",
                "도적 패배 / 인구 500 이상",
                2000,
                300,
                new boolean[] { true, true, true, true, true, false, false }) {
        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(3, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(2, 5).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            bandit.spawnSquad(new OffsetCoord(3, 4).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager) game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(new OffsetCoord(6, 5).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            villager.spawnSquad(new OffsetCoord(6, 6).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
            if (turn == 3000) {
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Check if bandits are defeated
            Villager villager = (Villager) game.getTribe(Tribe.Faction.VILLAGER);
            if (bandit.isDefeated() && villager.getTotalPopulation() >= 500) {
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
    LV10("map/map_lv10.png",
                "도적 소탕",
                "촌장님, 드디어 마을을 완전히 되찾았습니다." +
                " 남은 도적들은 뿔뿔히 흩어졌습니다." +
                " 어제 북서쪽 숲 근처에서 정찰병들이 제단을 하나 찾았습니다. 한 번 가보셔도 좋을 듯 합니다." +
                " \n\n새로운 제단을 방문하고 도적을 전부 물리치세요.",
                "도적 패배 / 사랑의 제단 점령",
                5000,
                200,
                new boolean[] { true, true, true, true, true, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(4, 0).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(4, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            bandit.spawnSquad(new OffsetCoord(1, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(2, 8).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(8, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            bandit.spawnSquad(new OffsetCoord(8, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.HORSE_MAN);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(new OffsetCoord(4, 4).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.HORSE_MAN);
            villager.spawnSquad(new OffsetCoord(3, 4).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(3, 5).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);

            game.getMap().getTerritory(new OffsetCoord(0, 2)).setFogState(Territory.FogState.MIST);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
            if (turn == 3000) {
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Check if bandits are defeated
            if (bandit.isDefeated() &&
                    game.getMap().getTerritory(new OffsetCoord(0, 2)).getFaction() == Tribe.Faction.VILLAGER) {
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

    Mission(String mapFile, String title, String description, String victoryCondition,
            int startingGold, int timeLimit, boolean[] recruitAvailable) {
        this.mapFile = mapFile;
        this.title = title;
        this.description = description;
        this.victoryCondition = victoryCondition;
        this.startingGold = startingGold;
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

    public int getStartingGold() {
        return startingGold;
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

    public void setRecruitAvailable(boolean[] recruitAvailable) {
        this.recruitAvailable = recruitAvailable;
    }

    private String mapFile;
    private String title;
    private String description;
    private String victoryCondition;
    private int startingGold;
    private int timeLimit;
    private boolean unlocked = true;
    private int starRating = 0;
    private boolean[] recruitAvailable;
}
