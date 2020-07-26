package com.lifejourney.townhall;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.World;

import java.util.ArrayList;
import java.util.ListIterator;

public class GameWorld extends World
        implements Squad.Event, GameMap.Event, Button.Event, MessageBox.Event,
                SpeedControl.Event, InfoBox.Event, Tribe.Event {

    static final String LOG_TAG = "GameWorld";

    GameWorld(String mapFileName) {

        super();

        // Set FPS
        setDesiredFPS(20.0f);

        // Build map
        map = new GameMap(this, mapFileName);
        map.show();
        addView(map);

        // Build tribe
        Villager villager = new Villager(this, map);
        tribes.add(villager);
        tribes.add(new Bandit(this, map));

        // Build UIs
        economyBar = new EconomyBar(villager, new Rect(20, 10, 440, 64),
                20, 0.0f);
        economyBar.show();
        addWidget(economyBar);

        dateBar = new DateBar(new Rect(480, 10, 230, 64), 20, 0.0f);
        dateBar.show();
        addWidget(dateBar);

        speedControl = new SpeedControl(this, new Rect(1080, 10, 174, 64),
                20, 0.0f);
        speedControl.show();
        addWidget(speedControl);

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

        /*
        messageBox = new MessageBox.Builder(this,
                new Rect(100, 100, 500, 400),"한글은?\ntest\ntest")
                .fontSize(35.0f).layer(9).textColor(Color.rgb(0, 0, 0))
                .build();
        messageBox.show();
        addWidget(messageBox);
        */
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

        // Update towns
        ArrayList<Town> towns = map.getTowns();
        for (Town town: towns) {
            town.update();
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
            Town thisTown = map.getTown(squad.getMapPosition());
            ArrayList<Squad> squadsInSameMap = thisTown.getSquads();
            assert squadsInSameMap.size() <= 2;
            if (squadsInSameMap.size() == 2 && thisTown.getBattle() == null) {
                Battle battle = new Battle(map, squadsInSameMap.get(1), squadsInSameMap.get(0));
                battles.add(battle);
                thisTown.setBattle(battle);
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
                map.getTown(battle.getMapCoord()).setBattle(null);
            }
        }
    }

    /**
     *
     * @param town
     */
    @Override
    public void onMapTownFocused(Town town) {

        if (focusedTown == town) {
            town.setFocus(false);
            focusedTown = null;
        } else {
            if (focusedSquad != null) {
                focusedSquad.setFocus(false);
                focusedSquad = null;
            }
            if (focusedTown != null) {
                focusedTown.setFocus(false);
                focusedTown = null;
            }
            focusedTown = town;
        }

        // Care for info button
        if (focusedTown == null && focusedSquad == null) {
            infoButton.hide();
            squadBuilderButton.hide();
        } else {
            infoButton.show();
            if (focusedTown != null && focusedTown.getFaction() == Tribe.Faction.VILLAGER &&
                    focusedTown.getSquads().isEmpty()) {
                squadBuilderButton.show();
            } else {
                squadBuilderButton.hide();
            }
        }
    }

    /**
     *
     * @param town
     * @param prevFaction
     */
    @Override
    public void onMapTownOccupied(Town town, Tribe.Faction prevFaction) {

        // Check if it's shrine
        if (town.getTerrain() == Town.Terrain.SHRINE_WIND ||
            town.getTerrain() == Town.Terrain.SHRINE_HEAL ||
            town.getTerrain() == Town.Terrain.SHRINE_LOVE ||
            town.getTerrain() == Town.Terrain.SHRINE_PROSPER) {
            Tribe.GlobalBonusFactor factor = town.getTerrain().bonusFactor();
            float value = town.getTerrain().bonusValue();
            if (prevFaction != Tribe.Faction.NEUTRAL) {
                getTribe(prevFaction).addGlobalFactor(factor, -value);
            }
            getTribe(town.getFaction()).addGlobalFactor(factor, value);
        }

        // Check UI
        if (focusedTown == town && focusedTown.getFaction() == Tribe.Faction.VILLAGER &&
                focusedTown.getSquads().isEmpty()) {
            squadBuilderButton.show();
        } else {
            squadBuilderButton.hide();
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

        map.getTown(squad.getMapPosition()).addSquad(squad);
        addSquad(squad);
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadDestroyed(Squad squad) {

        map.getTown(squad.getMapPosition()).removeSquad(squad);
        removeSquad(squad);

        // Check if destroyed squad is focused
        if (focusedSquad == squad) {
            focusedSquad = null;
        }
        if (focusedTown == null && focusedSquad == null) {
            infoButton.hide();
        } else {
            infoButton.show();
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
        if (focusedTown != null) {
            focusedTown.setFocus(false);
            focusedTown = null;
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

        map.getTown(prevMapPosition).removeSquad(squad);
        map.getTown(newMapPosition).addSquad(squad);
    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitAdded(Squad squad, Unit unit) {

        addUnit(unit);
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

    /**
     *
     * @param messageBox
     */
    @Override
    public void onMessageBoxTouched(MessageBox messageBox) {

        messageBox.hide();
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {

        if (button == infoButton) {
            // Info button is pressed
            playSpeedReturnedFromWidget = speedControl.getPlaySpeed();
            speedControl.setPlaySpeed(0);

            if (focusedSquad != null) {
                popupNewInfoBox(focusedSquad);
            } else if (focusedTown != null) {
                popupNewInfoBox(focusedTown);
            }
        } else if (button == squadBuilderButton) {
            // Squad builder button is pressed
            Squad squad = tribes.get(0).spawnSquad(focusedTown.getMapCoord().toGameCoord(),
                    Tribe.Faction.VILLAGER);
            focusedTown.setFocus(false);
            focusedTown = null;
            squad.setFocus(true);
            focusedSquad = squad;

            playSpeedReturnedFromWidget = speedControl.getPlaySpeed();
            speedControl.setPlaySpeed(0);
            popupNewInfoBox(squad);
        }
    }

    @Override
    public void onSpeedControlUpdate(SpeedControl speedControl) {

        if (speedControl.getPlaySpeed() == 0) {
            // Pause
            setDesiredFPS(20.0f);
            paused = true;
        } else if (speedControl.getPlaySpeed() == 1) {
            // 1x
            setDesiredFPS(20.0f);
            paused = false;
        } else if (speedControl.getPlaySpeed() == 2) {
            // 2x
            setDesiredFPS(30.0f);
            paused = false;
        } else if (speedControl.getPlaySpeed() == 3) {
            // 3x
            setDesiredFPS(40.0f);
            paused = false;
        }
    }

    /**
     *
     * @param infoBox
     */
    @Override
    public void onInfoBoxSwitchToTown(InfoBox infoBox) {

        popupNewInfoBox(map.getTown(focusedSquad.getMapPosition()));

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
            Town town = map.getTown(focusedSquad.getMapPosition());
            Squad squad = focusedSquad;
            squad.close();
            town.setFocus(true);
            onMapTownFocused(town);
        }
    }

    /**
     *
     * @param town
     */
    private void popupNewInfoBox(Town town) {

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect infoBoxRegion = new Rect((viewport.width - 700) / 2, (viewport.height - 400) / 2,
                700, 400);

        InfoBox infoBox = new InfoBox(this, infoBoxRegion, 30, 0.0f, town);
        infoBox.show();
        addWidget(infoBox);
    }

    /**
     *
     * @param squad
     */
    private void popupNewInfoBox(Squad squad) {

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect infoBoxRegion = new Rect((viewport.width - 700) / 2, (viewport.height - 400) / 2,
                700, 400);

        InfoBox infoBox = new InfoBox(this, (Villager)tribes.get(0), infoBoxRegion,
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

    private Squad focusedSquad = null;
    private Town focusedTown = null;

    private ArrayList<Battle> battles = new ArrayList<>();
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();
    private ArrayList<Tribe> tribes = new ArrayList<>();
}
