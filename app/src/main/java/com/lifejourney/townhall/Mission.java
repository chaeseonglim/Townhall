package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;

enum Mission {
    LV1("map/map_lv1.png",
            "굶주림 (튜토리얼)",
            "당신은 어느 작은 마을의 촌장입니다.\n\n" +
                    "유난히 혹독했던 지난 겨울, 당신은 많은 " +
                    "친구들을 잃었습니다. 하지만 이제 애도를 " +
                    "마치고 다시 일어설 때입니다. \n\n미래를 대비해 " +
                    "마을을 정돈하고 인구를 충분히 회복해야 " +
                    "합니다.",
            "전체 인구 65 이상",
            250,
            40,
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

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 농장은 인구를 늘려줍니다.");
            } else if (turn == 1000) {
                game.addNews("힌트: 일꾼을 뽑아 영토를 늘리는 방법도 있습니다.");
            } else if (turn == 1500) {
                game.addNews("힌트: 이번 미션에 시장과 요새는 불필요합니다.");
            } else if (turn == 2000) {
                game.addNews("힌트: 지루하면 게임 속도를 빠르게 해보세요.");
            }

            // Game clear check
            int day = game.getDays();
            if (((Villager)game.getTribe(Tribe.Faction.VILLAGER)).getTotalPopulation() >= 65) {
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
            "조짐 (튜토리얼)",
            "마을 근처에 도적들이 접근하고 있습니다." +
                    "마을 사람들이 불안해 하기 전에 처리해야 합니다." +
                    "\n\n당신의 전술적 지식을 확인해 볼 수 있는 좋은 기회입니다.",
            "도적 패배",
            250,
            50,
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

            // Hints
            if (turn == 1500) {
                game.addNews("힌트: 후퇴한 적은 바로 추격해서 공격 가능합니다.");
            } else if (turn == 1000) {
                game.addNews("힌트: 전투가 불리하면 추가 부대를 모집하세요.");
            } else if (turn == 3000) {
                game.addNews("힌트: 지루하면 게임 속도를 빠르게 해보세요.");
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
                "시장 Lv4 이상 1개 / 금화 5000 보유",
                250,
                120,
                new boolean[] { true, true, true, false, false, false, false }
                ) {

        public void init(MainGame game) {
            turn = 0;

            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(0, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.WORKER);
        }

        public void update(MainGame game) {
            turn++;

            if (turn == 1300) {
                // Prevent AI control units at the beginning
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 시장은 황무지에서 빠르게 성장합니다.");
            } else if (turn == 1000) {
                game.addNews("힌트: 지역 전체 시설의 Lv 합은 5를 넘지 못합니다.");
            } else if (turn == 1500) {
                game.addNews("힌트: 영토를 정복하면 수입을 쉽게 늘릴 수 있습니다.");
            } else if (turn == 2000) {
                game.addNews("힌트: 일꾼이 있는 지역은 빠르게 개발이 진행됩니다.");
            } else if (turn == 2500) {
                game.addNews("힌트: 시장은 황무지에서 빠르게 성장합니다.");
            } else if (turn == 3000) {
                game.addNews("힌트: 한 시설을 Lv4이상 만드려면 개발도를 조정해보세요.");
            } else if (turn == 4000) {
                game.addNews("힌트: 지루하면 게임 속도를 빠르게 해보세요.");
            } else if (turn == 4500) {
                game.addNews("힌트: 지역 전체 시설의 Lv 합은 5를 넘지 못합니다.");
            } else if (turn == 5000) {
                game.addNews("힌트: 일꾼이 있는 지역은 빠르게 개발이 진행됩니다.");
            } else if (turn == 5500) {
                game.addNews("힌트: 지루하면 게임 속도를 빠르게 해보세요.");
            }

            // Victory condition
            int day = game.getDays();
            if (((Villager)game.getTribe(Tribe.Faction.VILLAGER)).getGold() > 5000) {
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
                " 하지만 시장이 활성화 되면서 마을은 많은 금화를 모았습니다." +
                " \n\n도적들에게 당신의 힘을 보여줄 기회입니다.",
                "도적 패배",
                3500,
                150,
                new boolean[] { true, true, true, false, false, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(0, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            bandit.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(6, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(6, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.WORKER, Unit.UnitClass.WORKER, Unit.UnitClass.WORKER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            if (turn == 1000) {
                // Prevent AI control units at the beginning
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 돈은 충분합니다. 부대를 모집하세요.");
            } else if (turn == 1500) {
                game.addNews("힌트: 유지비를 감당할 수 있다면 업그레이드도 좋습니다.");
            } else if (turn == 2000) {
                game.addNews("힌트: 영토를 정복하면 수입을 쉽게 늘릴 수 있습니다.");
            } else if (turn == 2500) {
                game.addNews("힌트: 전투는 지원 병력을 최대한 활용하세요.");
            } else if (turn == 3000) {
                game.addNews("힌트: 적의 영토가 많아지면 부대도 빨리 늘어납니다.");
            } else if (turn == 5000) {
                game.addNews("힌트: 지루하면 게임 속도를 빠르게 해보세요.");
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
                "마을 근처의 시장은 매우 번창하고 있습니다. 많은 사람들이 당신을 우러러봅니다." +
                " 여전히 도적들이 근처에 출몰하고 있지만 별 문제는 아닐 것입니다." +
                " \n\n마을을 더 키워 큰 도시로 만들어주세요.",
                "마을 & 시장 각각 Lv5 1개 이상 보유",
                1000,
                150,
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
            bandit.spawnSquad(new OffsetCoord(6, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(villager.getHeadquarterPosition().toGameCoord(),
                    Unit.UnitClass.WORKER, Unit.UnitClass.WORKER, Unit.UnitClass.WORKER);
        }
        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            if (turn == 1300) {
                // Prevent AI control units at the beginning
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 돈은 충분합니다. 일꾼을 모집하세요.");
            } else if (turn == 1000) {
                game.addNews("힌트: 일꾼은 시설을 Lv5로 올리는데 도움을 줄 것입니다.");
            } else if (turn == 2000) {
                game.addNews("힌트: Lv5 시설을 만들 지역은 신중하게 고려하세요.");
            } else if (turn == 2500) {
                game.addNews("힌트: 마을은 주위 영토에 많은 도움을 줍니다.");
            } else if (turn == 3000) {
                game.addNews("힌트: Lv5 시설을 만들 지역에 일꾼 부대를 파견해보세요.");
            } else if (turn == 8000 && game.getTribe(Tribe.Faction.BANDIT).isDefeated()) {
                game.addNews("힌트: 지루하면 게임 속도를 빠르게 해보세요.");
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
                3000,
                150,
                new boolean[] { true, true, true, false, false, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(0, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(1, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(1, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(8, 0).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(8, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(7, 1).toGameCoord(),
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

            if (turn == 2500) {
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 가능하면 도적이 나타나기 전에 사원을 방문하세요.");
            } else if (turn == 1000) {
                game.addNews("힌트: 사원에 갈때는 충분한 병력을 데려가야합니다.");
            } else if (turn == 1500) {
                game.addNews("힌트: 이번 도적은 규모가 만만치 않습니다.");
            } else if (turn == 2000) {
                game.addNews("힌트: 언덕을 요새로 변경하면 방어에 도움이 될지 모릅니다.");
            } else if (turn == 3000) {
                game.addNews("힌트: 적은 쉴 틈을 주면 빠르게 회복합니다.");
            } else if (turn == 4000) {
                game.addNews("힌트: 일꾼은 후방에서 경제 활동을 계속 하는 것이 좋습니다.");
            } else if (turn == 5000) {
                game.addNews("힌트: 본부가 점령되면 신규 적 부대가 안나옵니다.");
            }

            // Check if shrine is taken
            if (!shrineTaken &&
                    game.getMap().getTerritory(new OffsetCoord(8, 8)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                shrineTaken = true;
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
                "100일간 생존",
                2000,
                100,
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
            bandit.spawnSquad(new OffsetCoord(1, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(1, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(5, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(8, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            game.getMap().getTerritory(viking.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            viking.setDifficultyFactor(0.4f);
            viking.setControlledByAI(false);
            viking.spawnSquad(new OffsetCoord(0, 8).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            viking.spawnSquad(new OffsetCoord(0, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            viking.spawnSquad(new OffsetCoord(0, 6).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            viking.spawnSquad(new OffsetCoord(1, 6).toGameCoord(),
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

            if (turn == 800) {
                // Prevent AI control units at the beginning
                Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            } else if (turn == 1600) {
                // Prevent AI control units at the beginning
                Viking viking = (Viking) game.getTribe(Tribe.Faction.VIKING);
                viking.setControlledByAI(true);
                game.addNews("조심하세요!! 바이킹이 이제 활동을 시작합니다.");
            }

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 이번에는 일정 시간 버티면 미션이 종료됩니다.");
            } else if (turn == 1000) {
                game.addNews("힌트: 일꾼이 필요치 않다면 다른 유닛으로 변경하세요.");
            } else if (turn == 1500) {
                game.addNews("힌트: 기존 시설을 요새로 변경하는 방법도 있습니다.");
            } else if (turn == 2000) {
                game.addNews("힌트: 바이킹은 강을 건너 넘어올 수 있습니다.");
            } else if (turn == 3000) {
                game.addNews("힌트: 치유사는 때로는 지루하지만 버티기에 적합합니다.");
            } else if (turn == 4000) {
                game.addNews("힌트: 도적의 본부는 점령 가능합니다만...");
            } else if (turn == 7000) {
                game.addNews("힌트: 조금만 더 버티면 됩니다!");
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
                "도적 패배",
                1000,
                150,
                new boolean[] { true, true, true, false, true, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.setDifficultyFactor(0.8f);
            bandit.spawnSquad(new OffsetCoord(4, 2).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(4, 3).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(4, 4).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(2, 3).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(2, 4).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(2, 5).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);

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

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 선택지가 많지 않습니다. 북쪽으로 달리세요!");
            } else if (turn == 1000) {
                game.addNews("힌트: 사원을 빠르게 제압해야 합니다.");
            } else if (turn == 3500) {
                game.addNews("힌트: 기마병은 빠르지만 원거리 공격에 매우 취약합니다.");
            } else if (turn == 4500) {
                game.addNews("힌트: 기마병은 적을 따돌리기 적합합니다.");
            } else if (turn == 5500) {
                game.addNews("힌트: 기마병은 치유사의 지원을 받으면 매우 강해집니다.");
            }

            // Check if shrine is taken
            if (!shrineTaken &&
                    game.getMap().getTerritory(new OffsetCoord(7, 1)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                shrineTaken = true;
                game.addNews("바람의 신은 당신의 기도에 응답했습니다.");
                game.popupMsgBox("당신은 제단에 기도를 올립니다..\n\n어느새 날이 어두워지고 돌아서는 찰나 어디선가 동물의 울음소리가 들려옵니다..");

                // spawn a bonus healer squad
                Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
                villager.spawnSquad(new OffsetCoord(8, 0).toGameCoord(),
                        Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
                villager.spawnSquad(new OffsetCoord(8, 1).toGameCoord(),
                        Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            }

            // Check if bandits are defeated
            if (bandit.isDefeated()) {
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
                "우리의 기마병은 도적의 선봉을 완전히 격파했습니다." +
                        " 이대로 밀어붙여 남아있는 도적으로부터 마을을 되찾아야 합니다." +
                " \n\n마을을 복구하려면 많은 노력이 필요할 것입니다.",
                "도적 패배 / 인구 500 이상",
                3000,
                150,
                new boolean[] { true, true, true, true, true, false, false }) {
        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(3, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(2, 1).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(2, 5).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(3, 4).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(1, 4).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager) game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(new OffsetCoord(6, 5).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            villager.spawnSquad(new OffsetCoord(7, 5).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            villager.spawnSquad(new OffsetCoord(6, 6).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
            if (turn == 1000) {
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 적을 물리치는 것도 중요하지만 인구를 늘려야합니다.");
            } else if (turn == 1500) {
                game.addNews("힌트: 기마병은 원거리 공격에 매우 약합니다.");
            } else if (turn == 2000) {
                game.addNews("힌트: 기마병은 치유사의 지원 하에 굉장히 강해집니다.");
            } else if (turn == 2500) {
                game.addNews("힌트: 승리를 거둔 다음 너무 많은 시간을 주면 쉽게 회복합니다.");
            } else if (turn == 3000) {
                game.addNews("힌트: 인구 500을 만드려면 시간이 부족할 수도 있습니다.");
            } else if (turn == 3500) {
                game.addNews("힌트: 성장을 위해 불필요한 요새는 제거하는 것이 좋습니다.");
            } else if (turn == 4000) {
                game.addNews("힌트: 어느 정도 승기를 잡았다면 일꾼을 늘려도 좋습니다.");
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
                "촌장님, " +
                        "드디어 마을을 완전히 되찾았습니다." +
                " 남은 도적들은 뿔뿔히 흩어졌습니다. 두 번 다시 마을을 넘보지 못하게 본때를 보여줍시다." +
                " \n\n추가로 정찰병에 따르면 북서쪽 숲 근처에서 제단을 발견했다고 합니다. 방문하면 도움이 될지 모르겠습니다.",
                "도적 패배 / 사랑의 제단 점령",
                4000,
                150,
                new boolean[] { true, true, true, true, true, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(false);
            bandit.setDifficultyFactor(0.7f);
            bandit.spawnSquad(new OffsetCoord(4, 0).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(3, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.HORSE_MAN);
            bandit.spawnSquad(new OffsetCoord(4, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            bandit.spawnSquad(new OffsetCoord(1, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(2, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            bandit.spawnSquad(new OffsetCoord(2, 8).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(8, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            bandit.spawnSquad(new OffsetCoord(8, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.HORSE_MAN);

            Rebel rebel = (Rebel)game.getTribe(Tribe.Faction.REBEL);
            game.getMap().getTerritory(new OffsetCoord(0, 2)).setFogState(Territory.FogState.MIST);
            rebel.setRetretable(false);
            rebel.setControlledByAI(false);
            rebel.spawnSquad(new OffsetCoord(0, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            rebel.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.HORSE_MAN);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(new OffsetCoord(4, 4).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.HORSE_MAN);
            villager.spawnSquad(new OffsetCoord(3, 4).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(3, 5).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            Bandit bandit = (Bandit) game.getTribe(Tribe.Faction.BANDIT);
            if (turn == 1500) {
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 도적은 여기저기 흩어져 있습니다만 조심해야 합니다.");
            } else if (turn == 1000) {
                game.addNews("힌트: 여전히 도적이 많습니다. 병력을 준비해두세요.");
            } else if (turn == 2000) {
                game.addNews("힌트: 사원은 천천히 가도 될 것 같습니다.");
            } else if (turn == 2500) {
                game.addNews("힌트: 약간의 업그레이드는 전투에 도움이 됩니다.");
            } else if (turn == 3000) {
                game.addNews("힌트: 적 본부를 점령하면 남은 병력은 매우 강해집니다. 주의하세요!");
            } else if (turn == 3500) {
                game.addNews("힌트: 너무 많은 업그레이드는 파산의 지름길입니다.");
            }

            // Check if bandits are defeated
            if (bandit.isDefeated() &&
                    game.getMap().getTerritory(new OffsetCoord(0, 2)).getFaction() == Tribe.Faction.VILLAGER) {
                if (day <= getTimeLimit() * 0.7f) {
                    game.missionCompleted(3);
                } else if (day <= getTimeLimit() * 0.85f) {
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
    LV11("map/map_lv11.png",
                 "폭죽 소리",
                 "마을은 다시 번화해지고 있습니다." +
                 " 마을에는 곡식과 금화가 넘치고 사람들은 쉬지 않고 축제를 엽니다." +
                 " 축제가 무르익자 어디선가 기쁨의 폭죽 소리가 들리기 시작합니다." +
                 " \n\n잠시만요.. 그런데 대체 누가 폭죽을 쏘고 있는거죠??",
                 "바이킹 본부 점령 / 도적 패배",
                 1000,
                 160,
                 new boolean[] { true, true, true, true, true, false, false }) {

        public void init(MainGame game) {
            turn = 0;

            // Prevent AI control units at the beginning
            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            game.getMap().getTerritory(viking.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            viking.setDifficultyFactor(0.4f);
            viking.setControlledByAI(false);
            viking.spawnSquad(new OffsetCoord(1, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            viking.spawnSquad(new OffsetCoord(2, 8).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            viking.spawnSquad(new OffsetCoord(2, 9).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            viking.spawnSquad(new OffsetCoord(0, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER);
            viking.spawnSquad(new OffsetCoord(0, 9).toGameCoord(),
                    Unit.UnitClass.CANNON, Unit.UnitClass.CANNON, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(0, 8).toGameCoord(),
                    Unit.UnitClass.CANNON, Unit.UnitClass.CANNON, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(0, 10).toGameCoord(),
                    Unit.UnitClass.CANNON, Unit.UnitClass.CANNON, Unit.UnitClass.CANNON);

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setDifficultyFactor(0.8f);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(1, 1).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(0, 0).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(new OffsetCoord(6, 5).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.HORSE_MAN);
            villager.spawnSquad(new OffsetCoord(7, 8).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(3, 3).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            if (turn == 700) {
                viking.setControlledByAI(true);
                bandit.setControlledByAI(true);
                game.addNews("조심하세요!! 바이킹이 이제 활동을 시작합니다.");
                game.addNews("조심하세요!! 도적이 이제 활동을 시작합니다.");
            }

            // Hints
            if (turn == 500) {
                game.addNews("힌트: 남쪽에 바이킹이 나타났습니다. 폭죽 소리와 함께요.");
            } else if (turn == 1500) {
                game.addNews("힌트: 바이킹은 처음보는 무기를 가져왔습니다. 조심하세요!");
            } else if (turn == 2000) {
                game.addNews("힌트: 적은 강합니다. 철저히 대비해야 합니다.");
            } else if (turn == 2500) {
                game.addNews("힌트: 대포병에게 지원을 허용하면 병력이 순식간에 녹습니다.");
            } else if (turn == 3500) {
                game.addNews("힌트: 바이킹과 강 근처에서 전투는 매우 불리합니다.");
            } else if (turn == 4000) {
                game.addNews("힌트: 너무 많은 업그레이드는 파산의 지름길입니다.");
            } else if (turn == 4500) {
                game.addNews("힌트: 가능하다면 사원을 점령하세요.");
            } else if (turn == 5000) {
                game.addNews("힌트: 바이킹 본부는 강 너머 건너편에 있습니다.");
            }

            // Check if vikings are defeated
            if (viking.isDefeated() && bandit.isDefeated()) {
                if (day <= getTimeLimit() * 0.7f) {
                    game.missionCompleted(3);
                } else if (day <= getTimeLimit() * 0.85f) {
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
    private int starRating = 0;
    private boolean[] recruitAvailable;
}
