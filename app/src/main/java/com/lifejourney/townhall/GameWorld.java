package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.World;

import java.util.ArrayList;
import java.util.ListIterator;

public class GameWorld extends World
        implements Squad.Event, TownMap.Event, Button.Event, MessageBox.Event {

    static final String LOG_TAG = "GameWorld";

    GameWorld() {
        super();
        setDesiredFPS(15.0f);

        map = new TownMap(this, "map.png", scale);
        map.show();
        addView(map);

        /*
        initCollisionPool(map.getMapSize().clone()
                .multiply(map.getTileSize().width, map.getTileSize().height), false);
        */

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

        Squad squadA =
                new Squad.Builder(this, map.getHeadquarterMapChord().toGameCoord(), map, Town.Side.TOWNER).build();
        addSquad(squadA);
        squadA.show();

        addUnit(squadA.spawnUnit(Unit.UnitClass.SWORD));
        addUnit(squadA.spawnUnit(Unit.UnitClass.LONGBOW));
        addUnit(squadA.spawnUnit(Unit.UnitClass.LONGBOW));

        OffsetCoord offsetB = map.getHeadquarterMapChord().clone();
        offsetB.offset(-1, 0);
        Squad squadB =
                new Squad.Builder(this, offsetB.toGameCoord(), map, Town.Side.BANDIT).build();
        addSquad(squadB);
        squadB.show();

        addUnit(squadB.spawnUnit(Unit.UnitClass.SWORD));
        addUnit(squadB.spawnUnit(Unit.UnitClass.LONGBOW));
        addUnit(squadB.spawnUnit(Unit.UnitClass.LONGBOW));

        OffsetCoord offsetC = map.getHeadquarterMapChord().clone();
        offsetC.offset(1, 0);
        Squad squadC =
                new Squad.Builder(this, offsetC.toGameCoord(), map, Town.Side.TOWNER).build();
        addSquad(squadC);
        squadC.show();

        addUnit(squadC.spawnUnit(Unit.UnitClass.SWORD));
        addUnit(squadC.spawnUnit(Unit.UnitClass.LONGBOW));
        addUnit(squadC.spawnUnit(Unit.UnitClass.LONGBOW));
    }

    /**
     *
     */
    @Override
    public void close() {
        map.close();
        map = null;
    }

    @Override
    protected void postupdate() {

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

        // Remove killed units
        ListIterator<Unit> iterUnit = units.listIterator();
        while (iterUnit.hasNext()) {
            Unit unit = iterUnit.next();
            if (unit.isKilled()) {
                unit.close();
                removeObject(unit);
                iterUnit.remove();
            }
        }

        // Remove eliminated squads
        ListIterator<Squad> iterSquad = squads.listIterator();
        while (iterSquad.hasNext()) {
            Squad squad = iterSquad.next();
            if (squad.isEliminated()) {
                squad.close();
                removeObject(squad);
                iterSquad.remove();
            }
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
            return;
        }
        if (focusedTown != null) {
            focusedTown.setFocus(false);
        }
        if (focusedSquad != null) {
            focusedSquad.setFocus(false);
        }
        focusedTown = town;
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadCreated(Squad squad) {

        map.getTown(squad.getMapCoord()).addSquad(squad);
    }

    /**
     *
     * @param squad
     */
    @Override
    public void onSquadDestroyed(Squad squad) {

        map.getTown(squad.getMapCoord()).removeSquad(squad);
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
        }
        if (focusedTown != null) {
            focusedTown.setFocus(false);
        }
        focusedSquad = squad;
    }

    @Override
    public void onSquadMoved(Squad squad, OffsetCoord prevMapCoord, OffsetCoord newMapCoord) {

        map.getTown(prevMapCoord).removeSquad(squad);
        map.getTown(newMapCoord).addSquad(squad);
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

        messageBox.show();
    }

    /**
     *
     * @return
     */
    public float getScale() {
        return scale;
    }

    /**
     *
     * @param scale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     *
     * @return
     */
    public TownMap getMap() {
        return map;
    }

    /**
     *
     * @param map
     */
    public void setMap(TownMap map) {
        this.map = map;
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

    private float scale = 1.0f;
    private TownMap map;
    private MessageBox messageBox;
    private ArrayList<Battle> battles = new ArrayList<>();
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<Unit> units = new ArrayList<>();
    private Squad focusedSquad = null;
    private Town focusedTown = null;
}
