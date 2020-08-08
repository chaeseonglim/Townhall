package com.lifejourney.townhall;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.World;

import java.util.ArrayList;
import java.util.ListIterator;

public class GameWorld extends World
        implements Squad.Event, GameMap.Event, Button.Event, MessageBox.Event,
                SpeedControl.Event, InfoBox.Event, HomeBox.Event, UpgradeBox.Event, Tribe.Event {

    static final String LOG_TAG = "GameWorld";

    GameWorld(String mapFileName) {

        super();

        // Set FPS
        setDesiredFPS(20.0f);

        // Init singleton
        Upgradable.reset();

        // Build map
        map = new GameMap(this, mapFileName);
        map.show();
        addView(map);

        // Build tribe
        Villager villager = new Villager(this, map);
        tribes.add(villager);
        tribes.add(new Raider(this, map));

        // Build UIs
        economyBar = new EconomyBar(villager, new Rect(20, 10, 440, 64),
                20, 0.0f);
        economyBar.show();
        addWidget(economyBar);

        speedControl = new SpeedControl(this, new Rect(1080, 10, 174, 64),
                20, 0.0f);
        speedControl.show();
        addWidget(speedControl);

        dateBar = new DateBar(speedControl, new Rect(480, 10, 230, 64), 20, 0.0f);
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

        newsBar = new NewsBar(new Rect(380, viewport.height - 74, 760, 64),
                20, 0.0f);
        newsBar.setFollowParentVisibility(false);
        newsBar.show();
        addWidget(newsBar);
    }

    /**
     *
     */
    @Override
    public void close() {

        super.close();

        map.close();
        map = null;
    }

    /**
     *
     */
    @Override
    protected void updateObjects() {

        if (paused) {
            return;
        }

        super.updateObjects();
    }

    @Override
    protected void preUpdate() {

        if (paused) {
            return;
        }

        // Update tribes
        for (Tribe tribe: tribes) {
            tribe.update();
        }

        // Update territories
        ArrayList<Territory> territories = map.getTerritories();
        for (Territory territory : territories) {
            territory.update();
        }
    }

    /**
     *
     */
    @Override
    protected void postUpdate() {

        if (paused) {
            return;
        }

        // Check if new battle is arisen
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
                map.getTerritory(battle.getMapCoord()).setBattle(null);
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
    }

    /**
     *
     * @param territory
     */
    @Override
    public void onMapTownFocused(Territory territory) {

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
    public void onMapTownOccupied(Territory territory, Tribe.Faction prevFaction) {

        // Check if it's shrine
        if (territory.getTerrain() == Territory.Terrain.SHRINE_WIND ||
            territory.getTerrain() == Territory.Terrain.SHRINE_HEAL ||
            territory.getTerrain() == Territory.Terrain.SHRINE_LOVE ||
            territory.getTerrain() == Territory.Terrain.SHRINE_PROSPER) {
            Tribe.ShrineBonus factor = territory.getTerrain().bonusFactor();
            float value = territory.getTerrain().bonusValue();
            if (prevFaction != Tribe.Faction.NEUTRAL) {
                getTribe(prevFaction).addGlobalFactor(factor, -value);
            }
            getTribe(territory.getFaction()).addGlobalFactor(factor, value);
        }

        // Check UI
        if (focusedTerritory == territory && focusedTerritory.getFaction() == Tribe.Faction.VILLAGER &&
                focusedTerritory.getSquads().isEmpty()) {
            squadBuilderButton.show();
        } else {
            squadBuilderButton.hide();
        }

        if (prevFaction != Tribe.Faction.NEUTRAL) {
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
     * @param squad
     */
    @Override
    public void onSquadCreated(Squad squad) {

        OffsetCoord squadMapPosition = squad.getMapPosition();

        Territory squadTerritory = map.getTerritory(squadMapPosition);
        squadTerritory.addSquad(squad);
        addSquad(squad);

        if (newsBar != null) {
            newsBar.addNews(squad.getFaction().toGameString() + "(이)가 새로운 부대를 만들었습니다.");
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

        if (newsBar != null) {
            newsBar.addNews(squad.getFaction().toGameString() + "의 부대가 제거되었습니다.");
        }
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadFocused(Squad squad) {

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

    @Override
    public void onMessageBoxButtonPressed(MessageBox messageBox, MessageBox.ButtonType buttonType) {
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {

        if (button == homeButton) {
            // Pause game temporarily
            playSpeedReturnedFromWidget = speedControl.getPlaySpeed();
            speedControl.setPlaySpeed(0);

            // Pop up home box
            popupHomeBox();
        }
        if (button == infoButton) {
            // Pause game temporarily
            playSpeedReturnedFromWidget = speedControl.getPlaySpeed();
            speedControl.setPlaySpeed(0);

            // Pop up info box
            if (focusedSquad != null) {
                popupInfoBox(focusedSquad);
            } else if (focusedTerritory != null) {
                popupInfoBox(focusedTerritory);
            }
        } else if (button == squadBuilderButton) {
            // Pause game temporarily
            playSpeedReturnedFromWidget = speedControl.getPlaySpeed();
            speedControl.setPlaySpeed(0);

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
    }

    @Override
    public void onSpeedControlUpdate(SpeedControl speedControl) {

        if (speedControl.getPlaySpeed() == 0) {
            // Pause
            setDesiredFPS(20.0f);
            dateBar.pause();
            paused = true;
        } else if (speedControl.getPlaySpeed() == 1) {
            // 1x
            setDesiredFPS(20.0f);
            dateBar.resume();
            paused = false;
        } else if (speedControl.getPlaySpeed() == 2) {
            // 2x
            setDesiredFPS(40.0f);
            dateBar.resume();
            paused = false;
        } else if (speedControl.getPlaySpeed() == 3) {
            // 3x
            setDesiredFPS(60.0f);
            dateBar.resume();
            paused = false;
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

        speedControl.setPlaySpeed(playSpeedReturnedFromWidget);
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
    }

    /**
     *
     * @param upgradeBox
     */
    @Override
    public void onUpgradeBoxClosed(UpgradeBox upgradeBox) {

        upgradeBox.close();
        removeWidget(upgradeBox);

        speedControl.setPlaySpeed(playSpeedReturnedFromWidget);
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

        speedControl.setPlaySpeed(playSpeedReturnedFromWidget);

        // In case of building new squad
        if (focusedSquad != null && focusedSquad.getUnits().isEmpty()) {
            Territory territory = map.getTerritory(focusedSquad.getMapPosition());
            Squad squad = focusedSquad;
            squad.close();
            territory.setFocus(true);
            onMapTownFocused(territory);
        }
    }

    /**
     *
     * @param mapCoord
     * @param radius
     * @param fogState
     */
    private void setMapFogState(OffsetCoord mapCoord, int radius, Territory.FogState fogState) {

        ArrayList<Territory> visibleTerritories = map.getNeighborTerritories(mapCoord, radius, false);
        visibleTerritories.add(map.getTerritory(mapCoord));
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
    private void popupHomeBox() {

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 700) / 2, (viewport.height - 400) / 2,
                700, 400);

        HomeBox homeBox = new HomeBox(this, boxRegion, 30, 0.0f,
                (Villager)tribes.get(0));
        homeBox.show();
        addWidget(homeBox);
    }

    /**
     *
     */
    private void popupUpgradeBox() {

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 800) / 2, (viewport.height - 450) / 2,
                800, 450);

        UpgradeBox upgradeBox = new UpgradeBox(this, (Villager) tribes.get(0),
                boxRegion, 30, 0.0f);
        upgradeBox.show();
        addWidget(upgradeBox);
    }

    /**
     *
     * @param territory
     */
    private void popupInfoBox(Territory territory) {

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect infoBoxRegion = new Rect((viewport.width - 700) / 2, (viewport.height - 400) / 2,
                700, 400);

        InfoBox infoBox = new InfoBox(this, infoBoxRegion, 30, 0.0f, territory);
        infoBox.show();
        addWidget(infoBox);
    }

    /**
     *
     * @param squad
     */
    private void popupInfoBox(Squad squad) {

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 700) / 2, (viewport.height - 400) / 2,
                700, 400);

        InfoBox infoBox = new InfoBox(this, (Villager)tribes.get(0), boxRegion,
                30, 0.0f, squad);
        infoBox.show();
        addWidget(infoBox);
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

    private boolean paused = false;
    private int playSpeedReturnedFromWidget = 0;

    private GameMap map;
    private MessageBox messageBox;
    private Button squadBuilderButton;
    private Button infoButton;
    private Button homeButton;
    private Button settingButton;
    private EconomyBar economyBar;
    private DateBar dateBar;
    private SpeedControl speedControl;
    private NewsBar newsBar;

    private Squad focusedSquad = null;
    private Territory focusedTerritory = null;

    private ArrayList<Battle> battles = new ArrayList<>();
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();
    private ArrayList<Tribe> tribes = new ArrayList<>();
}
