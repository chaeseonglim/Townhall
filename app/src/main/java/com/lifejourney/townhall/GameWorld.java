package com.lifejourney.townhall;

import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.World;

import java.util.ArrayList;

public class GameWorld extends World implements Button.Event, MessageBox.Event {

    static final String LOG_TAG = "GameWorld";

    GameWorld() {
        super();
        setDesiredFPS(20.0f);

        map = new TownMap("map.png", scale);
        addView(map);
        map.show();

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

        Squad squadA = new Squad.Builder(new PointF(map.getCapitalOffset().toGameCoord()),
                scale, map, Town.Side.TOWN).build();
        addSquad(squadA);
        squadA.show();

        addUnit(squadA.spawnUnit(Unit.UnitClass.SWORD));
        addUnit(squadA.spawnUnit(Unit.UnitClass.LONGBOW));
        addUnit(squadA.spawnUnit(Unit.UnitClass.LONGBOW));

        OffsetCoord offsetB = map.getCapitalOffset().clone();
        offsetB.offset(-1, 0);
        Squad squadB = new Squad.Builder(new PointF(offsetB.toGameCoord()),
                scale, map, Town.Side.BANDIT).build();
        addSquad(squadB);
        squadB.show();

        addUnit(squadB.spawnUnit(Unit.UnitClass.SWORD));
        addUnit(squadB.spawnUnit(Unit.UnitClass.LONGBOW));
        addUnit(squadB.spawnUnit(Unit.UnitClass.LONGBOW));

        OffsetCoord offsetC = map.getCapitalOffset().clone();
        offsetC.offset(1, 0);
        Squad squadC = new Squad.Builder(new PointF(offsetC.toGameCoord()),
                scale, map, Town.Side.TOWN).build();
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

    /**
     *
     */
    @Override
    public void update() {

        super.update();

        // Check if new battle is arisen
        for (Squad squad: squads) {
            ArrayList<Squad> squadsInSameMap = map.getSquads(squad.getMapOffsetCoord());
            assert squadsInSameMap.size() <= 2;
            if (!squad.isBattling() && squadsInSameMap.size() == 2 &&
                    squadsInSameMap.get(0).getMapOffsetCoord()
                            .equals(squadsInSameMap.get(1).getMapOffsetCoord())) {
                addBattle(squadsInSameMap.get(0), squadsInSameMap.get(1));
            }
        }
    }

    /**
     *
     * @param a
     * @param b
     */
    private void addBattle(Squad a, Squad b) {

        Battle battle = new Battle(a, b);
        battles.add(battle);
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
}
