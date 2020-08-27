package com.lifejourney.townhall;

import android.graphics.Color;
import android.util.Log;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.World;

import java.util.ArrayList;
import java.util.ListIterator;

public class MainGame extends World
        implements Squad.Event, GameMap.Event, Button.Event, MessageBox.Event, DateBar.Event,
                SpeedControl.Event, InfoBox.Event, HomeBox.Event, UpgradeBox.Event, Tribe.Event,
                SettingBox.Event, TutorialGuideForManagement.Event {

    static final String LOG_TAG = "MainGame";

    interface Event {
        void onGameFinished(MainGame game, int stars);
    }

    MainGame(Event eventHandler, Mission mission) {
        super();

        this.eventHandler = eventHandler;
        this.mission = mission;

        // Init upgradable status
        Upgradable.reset();

        // Set update FPS
        setUpdateFPS(20.0f);

        // Build map
        map = new GameMap(this, mission.getMapFile(), false);
        map.show();
        setView(map);

        // Build tribe
        Villager villager = new Villager(this, map);
        tribes.add(villager);
        tribes.add(new Raider(this, map, villager));
        tribes.add(new Viking(this, map, villager));

        // Build UIs
        economyBar = new EconomyBar(villager, new Rect(20, 10, 440, 64),
                20, 0.0f);
        economyBar.show();
        economyBar.refresh();
        addWidget(economyBar);

        speedControl = new SpeedControl(this, new Rect(1080, 10, 174, 64),
                20, 0.0f);
        speedControl.show();
        addWidget(speedControl);

        dateBar = new DateBar(this, new Rect(480, 10, 230, 64),
                20, 0.0f);
        dateBar.show();
        addWidget(dateBar);

        Rect viewport = Engine2D.GetInstance().getViewport();

        settingButton = new Button.Builder(this,
                new Rect(1160,  viewport.height - 74, 100, 64))
                .imageSpriteAsset("setting_btn.png").numImageSpriteSet(1).layer(20).build();
        settingButton.setImageSpriteSet(0);
        settingButton.show();
        addWidget(settingButton);

        homeButton = new Button.Builder(this,
                new Rect(20,  viewport.height - 74, 100, 64))
                .imageSpriteAsset("home_btn.png").numImageSpriteSet(1).layer(20).build();
        homeButton.setImageSpriteSet(0);
        homeButton.show();
        addWidget(homeButton);

        infoButton = new Button.Builder(this,
                new Rect(140, viewport.height - 74, 100, 64))
                .imageSpriteAsset("info_btn.png").numImageSpriteSet(1).layer(20).build();
        infoButton.setImageSpriteSet(0);
        infoButton.hide();
        addWidget(infoButton);

        squadBuilderButton = new Button.Builder(this,
                new Rect(260, viewport.height - 74, 100, 64))
                .imageSpriteAsset("squad_builder_btn.png").numImageSpriteSet(1).layer(20).build();
        squadBuilderButton.setImageSpriteSet(0);
        squadBuilderButton.hide();
        addWidget(squadBuilderButton);

        missionButton = new Button.Builder(this,
                new Rect(960, 10, 100, 64))
                .imageSpriteAsset("mission_btn.png").numImageSpriteSet(1).layer(20).build();
        missionButton.setImageSpriteSet(0);
        missionButton.show();
        addWidget(missionButton);

        newsBar = new NewsBar(new Rect(380, viewport.height - 74, 760, 64),
                20, 0.0f);
        newsBar.setFollowParentVisibility(false);
        newsBar.show();
        addWidget(newsBar);

        mission.init(this);
    }

    /**
     *
     */
    @Override
    public void close() {
        super.close();

        map.close();
        map = null;
        setView(null);
    }

    @Override
    protected void preUpdate() {
        // Update tribes
        for (Tribe tribe: tribes) {
            tribe.update();
        }

        // Update territories
        map.updateTerritories();
    }

    /**
     *
     */
    @Override
    protected void postUpdate() {
        // Check if new battle arises
        for (Squad squad: squads) {
            Territory thisTerritory = map.getTerritory(squad.getMapPosition());
            ArrayList<Squad> squadsInSameMap = thisTerritory.getSquads();
            assert squadsInSameMap.size() <= 2;
            if (squadsInSameMap.size() == 2 && thisTerritory.getBattle() == null) {
                Battle battle = new Battle(map, squadsInSameMap.get(1), squadsInSameMap.get(0));
                battles.add(battle);
                thisTerritory.setBattle(battle);
            }
        }

        // Do battles
        ListIterator<Battle> iterBattle = battles.listIterator();
        while (iterBattle.hasNext()) {
            // Update battle
            Battle battle = iterBattle.next();
            battle.update();

            // Remove if battle is finished
            if (battle.isFinished()) {
                iterBattle.remove();
                map.getTerritory(battle.getMapPosition()).setBattle(null);
            }
        }

        // Update fog state
        for (Territory territory : map.getTerritories()) {
            if (territory.getFaction() == Tribe.Faction.VILLAGER) {
                if (territory.getFogState() != Territory.FogState.CLEAR) {
                    territory.setFogState(Territory.FogState.CLEAR);
                    map.redraw(territory.getMapPosition());
                }
            } else if (territory.getFogState() == Territory.FogState.CLEAR) {
                territory.setFogState(Territory.FogState.MIST);
                map.redraw(territory.getMapPosition());
            }
        }
        for (Squad squad : tribes.get(0).getSquads()) {
            setMapFogState(squad.getMapPosition(), squad.getVision(), Territory.FogState.CLEAR);
        }

        // Update mission
        mission.update(this);
    }

    /**
     *
     */
    @Override
    public void pause() {
        super.pause();
        dateBar.pause();
    }

    /**
     *
     */
    @Override
    public void resume() {
        super.resume();
        dateBar.resume();
    }

    /**
     *
     */
    @Override
    public void pauseForBackground() {
        super.pauseForBackground();
        playSpeedReturnedFromBackground = speedControl.getPlaySpeed();
        speedControl.setPlaySpeed(0);
    }

    /**
     *
     */
    @Override
    public void resumeFromBackground() {
        super.resumeFromBackground();
        speedControl.setPlaySpeed(playSpeedReturnedFromBackground);
    }

    /**
     *
     */
    public void pauseForWidget() {
        playSpeedReturnedFromWidget = speedControl.getPlaySpeed();
        speedControl.setPlaySpeed(0);
    }

    /**
     *
     */
    public void resumeFromWidget() {
        speedControl.setPlaySpeed(playSpeedReturnedFromWidget);
    }

    /**
     *
     * @param territory
     */
    @Override
    public void onMapTerritoryFocused(Territory territory) {
        Engine2D.GetInstance().playSoundEffect("click5", 1.0f);

        if (focusedTerritory == territory) {
            territory.setFocus(false);
            focusedTerritory = null;
        } else {
            if (focusedSquad != null) {
                focusedSquad.setFocus(false);
                focusedSquad = null;
            }
            if (focusedTerritory != null) {
                focusedTerritory.setFocus(false);
                focusedTerritory = null;
            }
            focusedTerritory = territory;
        }

        // Care for info button
        if (focusedTerritory == null && focusedSquad == null) {
            infoButton.hide();
            squadBuilderButton.hide();
        } else {
            infoButton.show();
            if (focusedTerritory != null && focusedTerritory.getFaction() == Tribe.Faction.VILLAGER &&
                    focusedTerritory.getSquads().isEmpty()) {
                squadBuilderButton.show();
            } else {
                squadBuilderButton.hide();
            }
        }
    }

    /**
     *
     * @param territory
     * @param prevFaction
     */
    @Override
    public void onMapTerritoryOccupied(Territory territory, Tribe.Faction prevFaction) {
        // Check if it's shrine
        if (territory.getTerrain() == Territory.Terrain.SHRINE_WIND ||
            territory.getTerrain() == Territory.Terrain.SHRINE_HEAL ||
            territory.getTerrain() == Territory.Terrain.SHRINE_LOVE ||
            territory.getTerrain() == Territory.Terrain.SHRINE_PROSPER) {
            Tribe.ShrineBonus factor = territory.getTerrain().bonusFactor();
            int value = territory.getTerrain().bonusValue();
            if (prevFaction != Tribe.Faction.NEUTRAL) {
                getTribe(prevFaction).addShrineBonus(factor, -value);
            }
            getTribe(territory.getFaction()).addShrineBonus(factor, value);
        }

        // Check UI
        if (focusedTerritory == territory && focusedTerritory.getFaction() == Tribe.Faction.VILLAGER &&
                focusedTerritory.getSquads().isEmpty()) {
            squadBuilderButton.show();
        } else {
            squadBuilderButton.hide();
        }

        if (territory.getMapPosition().equals(tribes.get(0).getHeadquarterPosition())) {
            newsBar.addNews("우리 본부가 점령되었습니다. 이제 더이상 희망이 없습니다!");
            missionFailed();
        } else {
            for (int i = 0; i < tribes.size(); ++i) {
                // Check if some faction's headquarter is occupied
                if (territory.getMapPosition().equals(tribes.get(i).getHeadquarterPosition()) &&
                    territory.getFaction() == Tribe.Faction.VILLAGER &&
                        prevFaction == tribes.get(i).getFaction()) {
                    newsBar.addNews("우리가 " + tribes.get(i).getFaction().toGameString() + "의 본부를 점령했습니다.");
                    if (tribes.get(i).getSquads().size() > 0) {
                        for (Squad squad : tribes.get(i).getSquads()) {
                            squad.berserk();
                        }
                        newsBar.addNews("조심하세요! 남은 " + tribes.get(i).getFaction().toGameString() +
                                "의 병력들이 강해집니다.");
                    }
                }
            }
        }
        if (prevFaction != Tribe.Faction.NEUTRAL && territory.getFaction() != Tribe.Faction.NEUTRAL) {
            newsBar.addNews(territory.getFaction().toGameString() + "이 " +
                    prevFaction.toGameString() + "의 영토를 차지했습니다.");
        }

    }

    /**
     *
     * @param tribe
     */
    @Override
    public void onTribeCollected(Tribe tribe) {
        economyBar.refresh();
    }

    /**
     *
     * @param tribe
     * @param upgradable
     */
    @Override
    public void onTribeUpgraded(Tribe tribe, Upgradable upgradable) {
        newsBar.addNews(tribe.getFaction().toGameString() + "이 새로운 기술을 습득했습니다.");
    }

    /**
     *
     * @param tribe
     */
    @Override
    public void onTribeDefeated(Tribe tribe) {
        newsBar.addNews(tribe.getFaction().toGameString() + "이 패배했습니다.");
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadCreated(Squad squad) {
        OffsetCoord squadMapPosition = squad.getMapPosition();

        Territory squadTerritory = map.getTerritory(squadMapPosition);
        squadTerritory.addSquad(squad);
        addSquad(squad);

        if (newsBar != null && squad.getFaction() != Tribe.Faction.VILLAGER) {
            newsBar.addNews(squad.getFaction().toGameString() + "이 새로운 부대를 만들었습니다.");
        }
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadDestroyed(Squad squad) {
        OffsetCoord squadMapPosition = squad.getMapPosition();

        map.getTerritory(squadMapPosition).removeSquad(squad);
        removeSquad(squad);

        // Check if destroyed squad is focused
        if (focusedSquad == squad) {
            focusedSquad = null;
        }
        if (focusedTerritory == null && focusedSquad == null) {
            infoButton.hide();
        } else {
            infoButton.show();
        }

        if (newsBar != null && squad.isDeployed()) {
            newsBar.addNews(squad.getFaction().toGameString() + "의 부대가 제거되었습니다.");
        }
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadFocused(Squad squad) {
        if (squad.getFaction() == Tribe.Faction.VILLAGER) {
            Engine2D.GetInstance().playSoundEffect("villager", 1.0f);
        } else if (squad.getFaction() == Tribe.Faction.RAIDER) {
            Engine2D.GetInstance().playSoundEffect("raiders", 1.0f);
        } else if (squad.getFaction() == Tribe.Faction.VIKING) {
            Engine2D.GetInstance().playSoundEffect("viking", 1.0f);
        } else if (squad.getFaction() == Tribe.Faction.REBEL) {
            Engine2D.GetInstance().playSoundEffect("rebel", 1.0f);
        }

        if (focusedSquad == squad) {
            return;
        }
        if (focusedSquad != null) {
            focusedSquad.setFocus(false);
            focusedSquad = null;
        }
        if (focusedTerritory != null) {
            focusedTerritory.setFocus(false);
            focusedTerritory = null;
        }
        focusedSquad = squad;

        // Care for info button
        infoButton.show();
        squadBuilderButton.hide();
    }

    /**
     *
     * @param squad
     * @param prevMapPosition
     * @param newMapPosition
     */
    @Override
    public void onSquadMoved(Squad squad, OffsetCoord prevMapPosition, OffsetCoord newMapPosition) {
        map.getTerritory(prevMapPosition).removeSquad(squad);
        map.getTerritory(newMapPosition).addSquad(squad);
    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitAdded(Squad squad, Unit unit) {
        addUnit(unit);

        // Refresh UI state
        if (squad.getFaction() == Tribe.Faction.VILLAGER && economyBar != null) {
            economyBar.refresh();
            Engine2D.GetInstance().playSoundEffect("coin1", 1.0f);
        }
    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitRemoved(Squad squad, Unit unit) {
        removeUnit(unit);

        if (squad.getFaction() == Tribe.Faction.VILLAGER && economyBar != null) {
            economyBar.refresh();
        }
    }

    /**
     *
     * @param messageBox
     * @param buttonType
     */
    @Override
    public void onMessageBoxButtonPressed(MessageBox messageBox, MessageBox.ButtonType buttonType) {
        if (messageBox == gameFinishMessageBox) {
            eventHandler.onGameFinished(this, gameResultStars);
        } else if (messageBox == missionMessageBox) {
            missionMessageBox.close();
            removeWidget(missionMessageBox);
            missionMessageBox = null;

            resumeFromWidget();
        } else if (messageBox == tutorialStartMessageBox) {
            tutorialStartMessageBox.close();
            removeWidget(tutorialStartMessageBox);
            tutorialStartMessageBox = null;

            if (buttonType == MessageBox.ButtonType.YES) {
                TutorialGuideForManagement tutorialGuideForManagement =
                        new TutorialGuideForManagement(this, this);
                tutorialGuideForManagement.show();
                this.addWidget(tutorialGuideForManagement);
            } else {
                resumeFromWidget();
            }
        }
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {
        if (button == homeButton) { // Home button pressed
            // Pause game
            pauseForWidget();

            // Pop up home box
            popupHomeBox();
        } else if (button == infoButton) { // Info button pressed
            // Pause game
            pauseForWidget();

            // Pop up info box
            if (focusedSquad != null) {
                popupInfoBox(focusedSquad);
            } else if (focusedTerritory != null) {
                popupInfoBox(focusedTerritory);
            }
        } else if (button == squadBuilderButton) { // Squad Builder Button
            // Pause game
            pauseForWidget();

            // Spawn a squad
            Squad squad = tribes.get(0).spawnSquad(focusedTerritory.getMapPosition().toGameCoord(),
                    Tribe.Faction.VILLAGER);
            focusedTerritory.setFocus(false);
            focusedTerritory = null;
            squad.setFocus(true);
            focusedSquad = squad;

            // Pop up info box
            popupInfoBox(squad);
        } else if (button == settingButton) { // Settings Button
            // Pause game
            pauseForWidget();

            // Pop up setting box
            popupSettingBox();
        } else if (button == missionButton) { // Mission Button
            // Pause game
            pauseForWidget();

            // Pop up mission box
            Rect viewport = Engine2D.GetInstance().getViewport();
            missionMessageBox = new MessageBox.Builder(this, MessageBox.Type.CLOSE,
                    new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                            353, 275), "승리 조건:\n" + mission.getVictoryCondition() +
                    "\n\n시간 제한:\n" + mission.getTimeLimit()+"일")
                    .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 230, 230))
                    .build();
            missionMessageBox.show();
            addWidget(missionMessageBox);
        }
    }

    /**
     *
     * @param speedControl
     */
    @Override
    public void onSpeedControlUpdate(SpeedControl speedControl) {
        if (speedControl.getPlaySpeed() == 0) { // Pause
            setUpdateFPS(20.0f);
            pause();
        } else if (speedControl.getPlaySpeed() == 1) { // 1x
            setUpdateFPS(20.0f);
            resume();
        } else if (speedControl.getPlaySpeed() == 2) { // 2x
            setUpdateFPS(40.0f);
            resume();
        } else if (speedControl.getPlaySpeed() == 3) { // 3x
            setUpdateFPS(60.0f);
            resume();
        }
    }

    /**
     *
     * @param homeBox
     */
    @Override
    public void onHomeBoxSwitchToResearchBox(HomeBox homeBox) {
        popupUpgradeBox();

        homeBox.close();
        removeWidget(homeBox);
    }

    /**
     *
     * @param homeBox
     */
    @Override
    public void onHomeBoxClosed(HomeBox homeBox) {
        homeBox.close();
        removeWidget(homeBox);

        resumeFromWidget();
    }

    /**
     *
     * @param upgradeBox
     */
    @Override
    public void onUpgradeBoxSwitchToHomeBox(UpgradeBox upgradeBox) {
        popupHomeBox();

        upgradeBox.close();
        removeWidget(upgradeBox);
    }

    /**
     *
     * @param upgradeBox
     * @param upgradable
     */
    @Override
    public void onUpgradeBoxUpgraded(UpgradeBox upgradeBox, Upgradable upgradable) {
        economyBar.refresh();
        Engine2D.GetInstance().playSoundEffect("coin1", 1.0f);
    }

    /**
     *
     * @param upgradeBox
     */
    @Override
    public void onUpgradeBoxClosed(UpgradeBox upgradeBox) {
        upgradeBox.close();
        removeWidget(upgradeBox);

        resumeFromWidget();
    }

    /**
     *
     * @param infoBox
     */
    @Override
    public void onInfoBoxSwitchToTown(InfoBox infoBox) {
        popupInfoBox(map.getTerritory(focusedSquad.getMapPosition()));

        infoBox.close();
        removeWidget(infoBox);
    }

    /**
     *
     * @param infoBox
     */
    @Override
    public void onInfoBoxClosed(InfoBox infoBox) {
        infoBox.close();
        removeWidget(infoBox);

        // In case of building new squad
        if (focusedSquad != null && focusedSquad.getUnits().isEmpty()) {
            Territory territory = map.getTerritory(focusedSquad.getMapPosition());
            Squad squad = focusedSquad;
            squad.close();
            territory.setFocus(true);
            onMapTerritoryFocused(territory);
        }

        resumeFromWidget();
    }

    /**
     *
     * @param settingBox
     */
    @Override
    public void onSettingBoxClosed(SettingBox settingBox) {
        settingBox.close();
        removeWidget(settingBox);

        resumeFromWidget();
    }

    @Override
    public void onSettingBoxExitPressed(SettingBox settingBox) {
        eventHandler.onGameFinished(this, 0);
    }

    /**
     *
     * @param days
     */
    @Override
    public void onDateBarPassed(int days) {
    }

    /**
     *
     * @param tutorial
     */
    @Override
    public void onTutorialGuideForManagementFinished(TutorialGuideForManagement tutorial) {
        tutorial.close();
        removeWidget(tutorial);

        resumeFromWidget();
    }

    /**
     *
     * @param mapPosition
     * @param radius
     * @param fogState
     */
    private void setMapFogState(OffsetCoord mapPosition, int radius, Territory.FogState fogState) {
        ArrayList<Territory> visibleTerritories = map.getNeighborTerritories(mapPosition, radius, false);
        visibleTerritories.add(map.getTerritory(mapPosition));
        for (Territory territory : visibleTerritories) {
            if (territory.getFogState() != fogState) {
                territory.setFogState(fogState);
                map.redraw(territory.getMapPosition());
            }
        }
    }

    /**
     *
     */
    public void popupHomeBox() {
        homeBox = new HomeBox(this, (Villager) tribes.get(0),
                30, 0.0f);
        homeBox.show();
        addWidget(homeBox);
    }

    /**
     *
     */
    public void closeHomeBox() {
        homeBox.close();
        removeWidget(homeBox);
        homeBox = null;
    }

    /**
     *
     * @return
     */
    public Rect getHomeBoxRegion() {
        return homeBox.getRegion();
    }

    /**
     *
     */
    public void popupSettingBox() {
        settingBox = new SettingBox(this,30, 0.0f);
        settingBox.show();
        addWidget(settingBox);
    }

    /**
     *
     */
    public void closeSettingBox() {
        settingBox.close();
        removeWidget(settingBox);
        settingBox = null;
    }

    /**
     *
     */
    public void popupUpgradeBox() {
        upgradeBox = new UpgradeBox(this, (Villager) tribes.get(0), mission);
        upgradeBox.show();
        addWidget(upgradeBox);
    }

    /**
     *
     */
    public void closeUpgradeBox() {
        upgradeBox.close();
        removeWidget(upgradeBox);
        upgradeBox = null;
    }

    /**
     *
     * @return
     */
    public Rect getUpgradeBoxRegion() {
        return upgradeBox.getRegion();
    }

    /**
     *
     * @param territory
     */
    public void popupInfoBox(Territory territory) {
        infoBox = new InfoBox(this, mission, territory);
        infoBox.show();
        addWidget(infoBox);
    }

    /**
     *
     * @param squad
     */
    public void popupInfoBox(Squad squad) {
        infoBox = new InfoBox(this, mission, (Villager)tribes.get(0), squad);
        infoBox.show();
        addWidget(infoBox);
    }

    /**
     *
     */
    public void closeInfoBox() {
        infoBox.close();
        removeWidget(infoBox);
        infoBox = null;

        // In case of building new squad
        if (focusedSquad != null && focusedSquad.getUnits().isEmpty()) {
            Territory territory = map.getTerritory(focusedSquad.getMapPosition());
            Squad squad = focusedSquad;
            squad.close();
            territory.setFocus(true);
            onMapTerritoryFocused(territory);
        }
    }

    /**
     *
     * @return
     */
    public Rect getInfoBoxRegion() {
        return infoBox.getRegion();
    }

    /**
     *
     */
    public void pressSquadBuilderButton() {
        // Spawn a squad
        Squad squad = tribes.get(0).spawnSquad(focusedTerritory.getMapPosition().toGameCoord(),
                Tribe.Faction.VILLAGER);
        focusedTerritory.setFocus(false);
        focusedTerritory = null;
        squad.setFocus(true);
        focusedSquad = squad;

        // Pop up info box
        popupInfoBox(squad);
    }

    /**
     *
     */
    public void pressUnitSelectionButton() {
        infoBox.popupUnitSelectionBox(null);
    }

    /**
     *
     */
    public void closeUnitSelectionBox() {
        infoBox.closeUnitSelectionBox();
    }

    /**
     *
     * @param squad
     */
    public void addSquad(Squad squad) {
        squads.add(squad);
        addObject(squad);
    }

    /**
     *
     * @param squad
     */
    public void removeSquad(Squad squad) {
        squads.remove(squad);
        getTribe(squad.getFaction()).getSquads().remove(squad);
        removeObject(squad);
    }

    /**
     *
     * @param unit
     */
    public void addUnit(Unit unit) {

        units.add(unit);
        addObject(unit);
    }

    /**
     *
     * @param unit
     */
    public void removeUnit(Unit unit) {

        units.remove(unit);
        removeObject(unit);
    }

    /**
     *
     * @param faction
     * @return
     */
    public Tribe getTribe(Tribe.Faction faction) {

        for (Tribe tribe: tribes) {
            if (tribe.getFaction() == faction) {
                return tribe;
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public int getDays() {
        return dateBar.getDays();
    }

    /**
     *
     * @return
     */
    public Mission getMission() {
        return mission;
    }

    /**
     *
     * @return
     */
    public GameMap getMap() {
        return map;
    }

    /**
     *
     * @param stars
     */
    public void missionCompleted(int stars) {
        pauseForWidget();

        gameResultStars = stars;
        Rect viewport = Engine2D.GetInstance().getViewport();
        gameFinishMessageBox = new MessageBox.Builder(this, MessageBox.Type.CLOSE,
                new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                        353, 275), "미션을 완수했습니다!!!\n별 " + stars + "개 달성")
                .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 230, 0))
                .build();
        gameFinishMessageBox.show();
        addWidget(gameFinishMessageBox);
    }

    /**
     *
     */
    public void missionTimeout() {
        pauseForWidget();

        gameResultStars = 0;
        Rect viewport = Engine2D.GetInstance().getViewport();
        gameFinishMessageBox = new MessageBox.Builder(this, MessageBox.Type.CLOSE,
                new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                        353, 275), "게임 오버!!!\n시간 내에 미션을 완수하지 못했습니다.")
                .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 0, 0))
                .textShadow(Color.rgb(235, 235, 235), 2.0f)
                .build();
        gameFinishMessageBox.show();
        addWidget(gameFinishMessageBox);
    }

    /**
     *
     */
    public void missionFailed() {
        pauseForWidget();

        gameResultStars = 0;
        Rect viewport = Engine2D.GetInstance().getViewport();
        gameFinishMessageBox = new MessageBox.Builder(this, MessageBox.Type.CLOSE,
                new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                        353, 275), "게임 오버!!!\n본부가 점령되었습니다.")
                .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 0, 0))
                .textShadow(Color.rgb(235, 235, 235), 2.0f)
                .build();
        gameFinishMessageBox.show();
        addWidget(gameFinishMessageBox);
    }

    /**
     *
     */
    public void startTutorialForManagement() {
        pauseForWidget();

        Rect viewport = Engine2D.GetInstance().getViewport();
        tutorialStartMessageBox = new MessageBox.Builder(this, MessageBox.Type.YES_OR_NO,
                new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                        353, 275), "튜토리얼을 진행하시겠습니까?")
                .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 230, 230))
                .build();
        tutorialStartMessageBox.show();
        addWidget(tutorialStartMessageBox);
    }

    private final static float MUSIC_VOLUME = 0.3f;

    private Event eventHandler;
    private int playSpeedReturnedFromWidget = 0;
    private int playSpeedReturnedFromBackground = 0;

    private GameMap map;
    private Mission mission;
    private MessageBox gameFinishMessageBox;
    private MessageBox tutorialStartMessageBox;
    private MessageBox missionMessageBox;
    private Button squadBuilderButton;
    private Button infoButton;
    private Button homeButton;
    private Button settingButton;
    private Button missionButton;
    private EconomyBar economyBar;
    private DateBar dateBar;
    private SpeedControl speedControl;
    private NewsBar newsBar;
    private HomeBox homeBox;
    private UpgradeBox upgradeBox;
    private InfoBox infoBox;
    private SettingBox settingBox;
    private int gameResultStars;

    private Squad focusedSquad = null;
    private Territory focusedTerritory = null;

    private ArrayList<Battle> battles = new ArrayList<>();
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();
    private ArrayList<Tribe> tribes = new ArrayList<>();
}
