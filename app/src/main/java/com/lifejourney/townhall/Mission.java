package com.lifejourney.townhall;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;

enum Mission {
    LV1("map/map_lv1.png",
            Engine2D.GetInstance().getString(R.string.lv1_title),
            Engine2D.GetInstance().getString(R.string.lv1_desc),
            Engine2D.GetInstance().getString(R.string.lv1_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.lv1_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv1_hint2));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv1_hint3));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv1_hint4));
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
            Engine2D.GetInstance().getString(R.string.lv2_title),
            Engine2D.GetInstance().getString(R.string.lv2_desc),
            Engine2D.GetInstance().getString(R.string.lv2_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.lv2_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv2_hint2));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv2_hint3));
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
                Engine2D.GetInstance().getString(R.string.lv3_title),
                Engine2D.GetInstance().getString(R.string.lv3_desc),
                Engine2D.GetInstance().getString(R.string.lv3_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint2));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint3));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint4));
            } else if (turn == 2500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint1));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint5));
            } else if (turn == 4000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint6));
            } else if (turn == 4500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint2));
            } else if (turn == 5000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint4));
            } else if (turn == 5500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv3_hint6));
            }

            // Victory condition
            int day = game.getDays();
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

            if (day > getTimeLimit()) {
                game.missionTimeout();
            }
        }

        private int turn = 0;
    },
    LV4("map/map_lv4.png",
                Engine2D.GetInstance().getString(R.string.lv4_title),
                Engine2D.GetInstance().getString(R.string.lv4_desc),
                Engine2D.GetInstance().getString(R.string.lv4_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv4_hint1));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv4_hint2));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv4_hint3));
            } else if (turn == 2500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv4_hint4));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv4_hint5));
            } else if (turn == 5000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv4_hint6));
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
                Engine2D.GetInstance().getString(R.string.lv5_title),
                Engine2D.GetInstance().getString(R.string.lv5_desc),
                Engine2D.GetInstance().getString(R.string.lv5_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv5_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv5_hint2));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv5_hint3));
            } else if (turn == 2500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv5_hint4));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv5_hint5));
            } else if (turn == 8000 && game.getTribe(Tribe.Faction.BANDIT).isDefeated()) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv5_hint6));
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
                Engine2D.GetInstance().getString(R.string.lv6_title),
                Engine2D.GetInstance().getString(R.string.lv6_desc),
                Engine2D.GetInstance().getString(R.string.lv6_victory),
                3000,
                150,
                new boolean[] { true, true, true, false, false, false, false }) {

        public void init(MainGame game) {
            turn = 0;
            shrineTaken = false;

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
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv6_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv6_hint2));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv6_hint3));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv6_hint4));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv6_hint5));
            } else if (turn == 4000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv6_hint6));
            } else if (turn == 5000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv6_hint7));
            }

            // Check if shrine is taken
            if (!shrineTaken &&
                    game.getMap().getTerritory(new OffsetCoord(8, 8)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                shrineTaken = true;
                game.addNews(Engine2D.GetInstance().getString(R.string.lv6_healer_news));
                game.popupMsgBox(Engine2D.GetInstance().getString(R.string.lv6_healer_popup));

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
                Engine2D.GetInstance().getString(R.string.lv7_title),
                Engine2D.GetInstance().getString(R.string.lv7_desc),
                Engine2D.GetInstance().getString(R.string.lv7_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            } else if (turn == 1600) {
                // Prevent AI control units at the beginning
                Viking viking = (Viking) game.getTribe(Tribe.Faction.VIKING);
                viking.setControlledByAI(true);
                game.addNews(Engine2D.GetInstance().getString(R.string.viking_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv7_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv7_hint2));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv7_hint3));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv7_hint4));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv7_hint5));
            } else if (turn == 4000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv7_hint6));
            } else if (turn == 7000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv7_hint7));
            }

            if (day > getTimeLimit()) {
                game.missionCompleted(3);
            }
        }

        private int turn = 0;
    },
    LV8("map/map_lv8.png",
                Engine2D.GetInstance().getString(R.string.lv8_title),
                Engine2D.GetInstance().getString(R.string.lv8_desc),
                Engine2D.GetInstance().getString(R.string.lv8_victory),
                1000,
                150,
                new boolean[] { true, true, true, false, true, false, false }) {

        public void init(MainGame game) {
            turn = 0;
            shrineTaken = false;

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
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv8_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv8_hint2));
            } else if (turn == 3500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv8_hint3));
            } else if (turn == 4500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv8_hint4));
            } else if (turn == 5500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv8_hint5));
            }

            // Check if shrine is taken
            if (!shrineTaken &&
                    game.getMap().getTerritory(new OffsetCoord(7, 1)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                shrineTaken = true;
                game.addNews(Engine2D.GetInstance().getString(R.string.lv8_wind_news));
                game.popupMsgBox(Engine2D.GetInstance().getString(R.string.lv8_wind_popup));

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
                Engine2D.GetInstance().getString(R.string.lv9_title),
            Engine2D.GetInstance().getString(R.string.lv9_desc),
            Engine2D.GetInstance().getString(R.string.lv9_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv9_hint1));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv9_hint2));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv9_hint3));
            } else if (turn == 2500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv9_hint4));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv9_hint5));
            } else if (turn == 3500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv9_hint6));
            } else if (turn == 4000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv9_hint7));
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
                Engine2D.GetInstance().getString(R.string.lv10_title),
                Engine2D.GetInstance().getString(R.string.lv10_desc),
                Engine2D.GetInstance().getString(R.string.lv10_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv10_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv10_hint2));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv10_hint3));
            } else if (turn == 2500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv10_hint4));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv10_hint5));
            } else if (turn == 3500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv10_hint6));
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
                 Engine2D.GetInstance().getString(R.string.lv11_title),
                 Engine2D.GetInstance().getString(R.string.lv11_desc),
                 Engine2D.GetInstance().getString(R.string.lv11_victory),
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
                game.addNews(Engine2D.GetInstance().getString(R.string.viking_activated));
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv11_hint1));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv11_hint2));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv11_hint3));
            } else if (turn == 2500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv11_hint4));
            } else if (turn == 3500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv11_hint5));
            } else if (turn == 4000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv11_hint6));
            } else if (turn == 4500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv11_hint7));
            } else if (turn == 5000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv11_hint8));
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
    },
    LV12("map/map_lv12.png",
                 Engine2D.GetInstance().getString(R.string.lv12_title),
                 Engine2D.GetInstance().getString(R.string.lv12_desc),
                 Engine2D.GetInstance().getString(R.string.lv12_victory),
                 1000,
                 180,
                 new boolean[] { true, true, true, true, true, false, false }) {

        public void init(MainGame game) {
            turn = 0;
            cannonAllowed = false;
            shrineTaken = false;

            setRecruitAvailable(new boolean[] { true, true, true, true, true, false, false });

            // Prevent AI control units at the beginning
            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            game.getMap().getTerritory(viking.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            viking.setDifficultyFactor(0.3f);
            viking.setControlledByAI(false);
            viking.spawnSquad(new OffsetCoord(1, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(2, 8).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            viking.spawnSquad(new OffsetCoord(2, 9).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(0, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            viking.spawnSquad(new OffsetCoord(1, 8).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(1, 9).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            viking.spawnSquad(new OffsetCoord(0, 9).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.CANNON, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(0, 8).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.CANNON, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(0, 10).toGameCoord(),
                    Unit.UnitClass.CANNON, Unit.UnitClass.CANNON, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(4, 10).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);

            // Prevent AI control units at the beginning
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setDifficultyFactor(0.5f);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(1, 1).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(1, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(0, 0).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            bandit.spawnSquad(new OffsetCoord(9, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(8, 0).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(new OffsetCoord(7, 5).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(7, 8).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(4, 3).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);

            game.getMap().getTerritory(new OffsetCoord(6, 0)).setFogState(Territory.FogState.MIST);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            if (turn == 700) {
                viking.setControlledByAI(true);
                bandit.setControlledByAI(true);
                game.addNews(Engine2D.GetInstance().getString(R.string.viking_activated));
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_hint2));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_hint3));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_hint4));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_hint5));
            } else if (turn == 4000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_hint6));
            } else if (turn == 4500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_hint7));
            } else if (turn == 5000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_hint8));
            }

            if (!shrineTaken &&
                    game.getMap().getTerritory(new OffsetCoord(6, 0)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                shrineTaken = true;
                game.addNews(Engine2D.GetInstance().getString(R.string.lv12_prosper_news));
                game.popupMsgBox(Engine2D.GetInstance().getString(R.string.lv12_prosper_popup));
            }

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            if (!cannonAllowed && villager.getGold() >= 20000) {
                game.popupMsgBox(Engine2D.GetInstance().getString(R.string.lv12_cannon_popup));
                setRecruitAvailable(new boolean[] { true, true, true, true, true, true, false });
                cannonAllowed = true;
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
        private boolean cannonAllowed = false;
        private boolean shrineTaken = false;
    },
    LV13("map/map_lv13.png",
                Engine2D.GetInstance().getString(R.string.lv13_title),
                Engine2D.GetInstance().getString(R.string.lv13_desc),
                Engine2D.GetInstance().getString(R.string.lv13_victory),
                 1000,
                 180,
                 new boolean[] { true, true, true, true, true, true, false }) {

        public void init(MainGame game) {
            turn = 0;

            setRecruitAvailable(new boolean[] { true, true, true, true, true, true, false });

            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            game.getMap().getTerritory(viking.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            viking.setDifficultyFactor(0.5f);
            viking.setControlledByAI(false);
            viking.spawnSquad(new OffsetCoord(1, 7).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(2, 8).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            viking.spawnSquad(new OffsetCoord(2, 9).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(1, 8).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER, Unit.UnitClass.CANNON);
            viking.spawnSquad(new OffsetCoord(3, 10).toGameCoord(),
                Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HEALER);

            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setDifficultyFactor(0.5f);
            bandit.setControlledByAI(false);
            bandit.spawnSquad(new OffsetCoord(1, 1).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(1, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(1, 0).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);
            bandit.spawnSquad(new OffsetCoord(0, 1).toGameCoord(),
                    Unit.UnitClass.ARCHER, Unit.UnitClass.HEALER, Unit.UnitClass.HEALER);
            bandit.spawnSquad(new OffsetCoord(0, 0).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
            bandit.spawnSquad(new OffsetCoord(2, 2).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);

            Rebel rebel = (Rebel)game.getTribe(Tribe.Faction.REBEL);
            game.getMap().getTerritory(rebel.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            rebel.setDifficultyFactor(0.5f);
            rebel.setControlledByAI(false);
            rebel.spawnSquad(new OffsetCoord(10, 0).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            rebel.spawnSquad(new OffsetCoord(11, 0).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.FIGHTER, Unit.UnitClass.HEALER);
            rebel.spawnSquad(new OffsetCoord(12, 0).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HEALER);
            rebel.spawnSquad(new OffsetCoord(12, 1).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.ARCHER, Unit.UnitClass.ARCHER);

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            villager.spawnSquad(new OffsetCoord(7, 6).toGameCoord(),
                    Unit.UnitClass.FIGHTER, Unit.UnitClass.FIGHTER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(7, 8).toGameCoord(),
                    Unit.UnitClass.HEALER, Unit.UnitClass.HEALER, Unit.UnitClass.ARCHER);
            villager.spawnSquad(new OffsetCoord(5, 5).toGameCoord(),
                    Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN, Unit.UnitClass.HORSE_MAN);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            Rebel rebel = (Rebel)game.getTribe(Tribe.Faction.REBEL);
            if (turn == 700) {
                bandit.setControlledByAI(true);
                rebel.setControlledByAI(true);
                game.addNews(Engine2D.GetInstance().getString(R.string.bandit_activated));
                game.addNews(Engine2D.GetInstance().getString(R.string.rebel_activated));
            } else if (turn == 1100) {
                viking.setControlledByAI(true);
                game.addNews(Engine2D.GetInstance().getString(R.string.viking_activated));
            }

            // Hints
            if (turn == 500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_hint1));
            } else if (turn == 1000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_hint2));
            } else if (turn == 1500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_hint3));
            } else if (turn == 2000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_hint4));
            } else if (turn == 3000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_hint5));
            } else if (turn == 4000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_hint6));
            } else if (turn == 5000) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_hint7));
            } else if (turn == 5500) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_hint8));
            }

            Villager villager = (Villager)game.getTribe(Tribe.Faction.VILLAGER);
            if (turn == 4200 &&
                game.getMap().getTerritory(new OffsetCoord(11, 11)).getFaction() ==
                        Tribe.Faction.VILLAGER) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_news1));
                game.popupMsgBox(Engine2D.GetInstance().getString(R.string.lv13_popup1));
                villager.spawnSquad(new OffsetCoord(10, 11).toGameCoord(),
                        Unit.UnitClass.PALADIN, Unit.UnitClass.PALADIN, Unit.UnitClass.PALADIN);
            }
            if (turn == 4400 &&
                    game.getMap().getTerritory(new OffsetCoord(10, 4)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_news2));
                game.popupMsgBox(Engine2D.GetInstance().getString(R.string.lv13_popup2));
                villager.spawnSquad(new OffsetCoord(9, 4).toGameCoord(),
                        Unit.UnitClass.PALADIN, Unit.UnitClass.PALADIN, Unit.UnitClass.PALADIN);
            }
            if (turn == 4600 &&
                    game.getMap().getTerritory(new OffsetCoord(7, 1)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_news3));
                game.popupMsgBox(Engine2D.GetInstance().getString(R.string.lv13_popup3));
                villager.spawnSquad(new OffsetCoord(6, 0).toGameCoord(),
                        Unit.UnitClass.PALADIN, Unit.UnitClass.PALADIN, Unit.UnitClass.PALADIN);
            }
            if (turn == 4800 &&
                    game.getMap().getTerritory(new OffsetCoord(3, 5)).getFaction() ==
                            Tribe.Faction.VILLAGER) {
                game.addNews(Engine2D.GetInstance().getString(R.string.lv13_news4));
                game.popupMsgBox(Engine2D.GetInstance().getString(R.string.lv13_popup4));
                villager.spawnSquad(new OffsetCoord(3, 4).toGameCoord(),
                        Unit.UnitClass.PALADIN, Unit.UnitClass.PALADIN, Unit.UnitClass.PALADIN);
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
    },
    FREE_LV1("map/map_free_lv1.png",
                 Engine2D.GetInstance().getString(R.string.free_lv1_title),
                Engine2D.GetInstance().getString(R.string.free_lv1_desc),
                Engine2D.GetInstance().getString(R.string.free_lv1_victory),
                 1000,
                 1000,
                 new boolean[] { true, true, true, true, true, true, true }) {

        public void init(MainGame game) {
            turn = 0;

            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            game.getMap().getTerritory(viking.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            viking.setControlledByAI(true);

            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            game.getMap().getTerritory(bandit.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            bandit.setControlledByAI(true);

            Rebel rebel = (Rebel)game.getTribe(Tribe.Faction.REBEL);
            game.getMap().getTerritory(rebel.getHeadquarterPosition()).setFogState(Territory.FogState.MIST);
            rebel.setControlledByAI(true);
        }

        public void update(MainGame game) {
            turn++;

            int day = game.getDays();

            // Check if vikings are defeated
            Viking viking = (Viking)game.getTribe(Tribe.Faction.VIKING);
            Bandit bandit = (Bandit)game.getTribe(Tribe.Faction.BANDIT);
            Rebel rebel = (Rebel)game.getTribe(Tribe.Faction.REBEL);
            if (viking.isDefeated() && bandit.isDefeated() && rebel.isDefeated()) {
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
