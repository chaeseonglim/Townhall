package com.lifejourney.townhall;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.World;

import java.util.ArrayList;
import java.util.ListIterator;

public class GameWorld extends World
        implements Squad.Event, GameMap.Event, Button.Event, MessageBox.Event,
                SpeedControl.Event {

    static final String LOG_TAG = "GameWorld";

    GameWorld() {

        super();

        setDesiredFPS(10.0f);

        // Build map
        map = new GameMap(this, "map.png");
        map.show();
        addView(map);

        // Build tribes
        Villager villager = new Villager(this, map);
        tribes.add(villager);
        tribes.add(new Bandit(this, map));

        // Build UIs
        EconomyBar economyBar = new EconomyBar(villager,
            new Rect(20, 10, 440, 64), 20, 0.0f);
        economyBar.show();
        addWidget(economyBar);

        dateBar = new DateBar(
                new Rect(480, 10, 230, 64), 20, 0.0f);
        dateBar.show();
        addWidget(dateBar);

        SpeedControl speedControl = new SpeedControl(this,
            new Rect(1080, 10, 174, 64), 20, 0.0f);
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

        unitBuilderButton = new Button.Builder(this,
                new Rect(140, viewport.height - 74, 100, 64))
                .imageSpriteAsset("unit_builder_btn.png").numImageSpriteSet(1).layer(20).build();
        unitBuilderButton.setImageSpriteSet(0);
        unitBuilderButton.show();
        addWidget(unitBuilderButton);

        researchButton = new Button.Builder(this,
                new Rect(260,  viewport.height - 74, 100, 64))
                .imageSpriteAsset("research_btn.png").numImageSpriteSet(1).layer(20).build();
        researchButton.setImageSpriteSet(0);
        researchButton.show();
        addWidget(researchButton);

        infoButton = new Button.Builder(this,
                new Rect(380, viewport.height - 74, 100, 64))
                .imageSpriteAsset("info_btn.png").numImageSpriteSet(1).layer(20).build();
        infoButton.setImageSpriteSet(0);
        infoButton.hide();
        addWidget(infoButton);

        /*
        messageBox = new MessageBox.Builder(this,
                new Rect(100, 100, 500, 400),"한글은?\ntest\ntest")
                .fontSize(35.0f).layer(9).textColor(Color.rgb(0, 0, 0))
                .build();
        messageBox.show();
        addWidget(messageBox);

        Button okButton = new Button.Builder(this,
                new Rect(400, 380, 150, 80), "확인")
                .fontSize(35.0f).layer(10).textColor(Color.rgb(0, 0, 0))
                .build();
        okButton.show();
        addWidget(okButton);
        */
    }

    /**
     *
     */
    @Override
    public void close() {

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
            Town thisTown = map.getTown(squad.getMapCoord());
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

        // Close eliminated squads (removing will be placed on callback)
        for (Squad squad : squads) {
            if (squad.isEliminated()) {
                squad.close();
            }
        }

        // Update towns
        ArrayList<Town> towns = map.getTowns();
        for (Town town: towns) {
            town.update();
        }

        // Update tribes
        for (Tribe tribe: tribes) {
            tribe.update();
        }

        // Update date
        if (--dayUpdateTimeLeft == 0) {
            day++;
            dateBar.setDay(day);
            dayUpdateTimeLeft = DAY_UPDATE_PERIOD;
        }

    }

    /**
     *
     */
    @Override
    public void onMapCreated() {
    }

    /**
     *
     */
    @Override
    public void onMapDestroyed() {
    }

    /**
     *
     * @param town
     */
    @Override
    public void onMapFocused(Town town) {

        if (focusedTown == town) {
            town.setFocus(false);
            focusedTown = null;
        } else if (focusedSquad != null) {
            focusedSquad.setFocus(false);
            focusedSquad = null;
            town.setFocus(false);
        } else {
            if (focusedTown != null) {
                focusedTown.setFocus(false);
                focusedTown = null;
            }
            focusedTown = town;
        }

        // Care for info button
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
    public void onSquadCreated(Squad squad) {

        map.getTown(squad.getMapCoord()).addSquad(squad);
        addSquad(squad);
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadDestroyed(Squad squad) {

        map.getTown(squad.getMapCoord()).removeSquad(squad);
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
    }

    /**
     *
     * @param squad
     * @param prevMapCoord
     * @param newMapCoord
     */
    @Override
    public void onSquadMoved(Squad squad, OffsetCoord prevMapCoord, OffsetCoord newMapCoord) {

        map.getTown(prevMapCoord).removeSquad(squad);
        map.getTown(newMapCoord).addSquad(squad);
    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitSpawned(Squad squad, Unit unit) {

        addUnit(unit);
    }

    /**
     *
     * @param squad
     * @param unit
     */
    @Override
    public void onSquadUnitKilled(Squad squad, Unit unit) {

        removeUnit(unit);
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

    }

    @Override
    public void onSpeedControlUpdate(SpeedControl speedControl) {

        if (speedControl.getPlaySpeed() == 0) {
            // Pause
            setDesiredFPS(15.0f);
            paused = true;
        } else if (speedControl.getPlaySpeed() == 1) {
            // 1x
            setDesiredFPS(15.0f);
            paused = false;
        } else if (speedControl.getPlaySpeed() == 2) {
            // 2x
            setDesiredFPS(25.0f);
        } else if (speedControl.getPlaySpeed() == 3) {
            // 3x
            setDesiredFPS(35.0f);
        }
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
     * @return
     */
    public int getDay() {
        return day;
    }

    private static final int DAY_UPDATE_PERIOD = 90;

    private boolean paused = false;
    private int day = 0;
    private int dayUpdateTimeLeft = DAY_UPDATE_PERIOD;

    private GameMap map;
    private MessageBox messageBox;
    private Button unitBuilderButton;
    private Button infoButton;
    private Button homeButton;
    private Button settingButton;
    private Button researchButton;
    private DateBar dateBar;

    private Squad focusedSquad = null;
    private Town focusedTown = null;

    private ArrayList<Battle> battles = new ArrayList<>();
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();
    private ArrayList<Tribe> tribes = new ArrayList<>();
}
