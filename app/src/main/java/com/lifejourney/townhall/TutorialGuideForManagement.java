package com.lifejourney.townhall;

import android.graphics.Color;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.OffsetCoord;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.Rectangle;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Widget;

public class TutorialGuideForManagement extends Widget implements MessageBox.Event {

    private final static String LOG_TAG = "TutorialGuideForManagement";

    interface Event {
        void onTutorialGuideForManagementFinished(TutorialGuideForManagement tutorial);
    }

    enum Step {
        INTRODUCTION,
        GOLD,
        POPULATION,
        HAPPINESS,
        SPEED,
        MISSION_TARGET,
        HOME_BUTTON,
        HOME_UI,
        UPGRADE_BUTTON,
        UPGRADE_UI,
        FOCUS_TILE,
        INFO_BUTTON,
        INFO_TERRITORY,
        INFO_FACILITY,
        INFO_FACILITY2,
        INFO_FACILITY3,
        INFO_FACILITY4,
        INFO_FACILITY5,
        SQUAD_BUILDER_BUTTON,
        SQUAD_BUILDING,
        SQUAD_BUILDING2,
        SQUAD_RECRUITING,
        FINAL,
        FINISHED
    }

    public TutorialGuideForManagement(Event eventHandler, MainGame game) {
        super(new Rect(), 30, 0);
        this.eventHandler = eventHandler;
        this.game = game;

        guideRectangle = new Rectangle.Builder(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                new PointF(80, 10)), new SizeF(170, 64)))
                .color(Color.argb(255, 255, 255, 255)).lineWidth(10.0f)
                .layer(255).visible(true).build();
        guideRectangle.hide();
        guideRectangle.commit();

        Rect viewport = Engine2D.GetInstance().getViewport();
        tutorialBox = new MessageBox.Builder(this, MessageBox.Type.PLATE,
                new Rect(viewport.width - 340 - 20, viewport.height - 240 - 90,
                        340, 250),
                Engine2D.GetInstance().getString(R.string.tutorial_management_1))
                .fontSize(24.0f).layer(50).textColor(Color.rgb(235, 235, 235))
                .fontName("neodgm.ttf")
                .bgAsset("tutorial_box_bg.png").bgOpaque(0.8f)
                .build();
        tutorialBox.setFollowParentVisibility(false);
        tutorialBox.show();
        addWidget(tutorialBox);

        updateGuide();
    }

    /**
     *
     */
    @Override
    public void close() {
        super.close();

        if (guideRectangle != null) {
            guideRectangle.close();
        }
    }

    /**
     *
     */
    @Override
    public void commit() {
        super.commit();

        if (guideRectangle != null) {
            guideRectangle.commit();
        }
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) {
            updateGuide();
        }
        return true;
    }

    /**
     *
     * @param messageBox
     * @param buttonType
     */
    @Override
    public void onMessageBoxButtonPressed(MessageBox messageBox, MessageBox.ButtonType buttonType) {
    }

    /**
     *
     */
    private void updateGuide() {
        boolean goNext = true;
        Rect viewport = Engine2D.GetInstance().getViewport();

        if (step == Step.INTRODUCTION) {
            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.show();
        } else if (step == Step.GOLD) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(80, 10)), new SizeF(170, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_2));
            tutorialBox.show();
        } else if (step == Step.POPULATION) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(330, 10)), new SizeF(130, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_3));
        } else if (step == Step.HAPPINESS) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(270, 10)), new SizeF(60, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_4));
        } else if (step == Step.SPEED) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(viewport.width - 200, 10)), new SizeF(180, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_5));
        } else if (step == Step.MISSION_TARGET) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(viewport.width - 320, 10)), new SizeF(100, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_6));
        } else if (step == Step.HOME_BUTTON) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(20, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_7));
        } else if (step == Step.HOME_UI) {
            game.popupHomeBox();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_8));
        } else if (step == Step.UPGRADE_BUTTON) {
            Rect boxRegion = game.getHomeBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.right() - 310, boxRegion.bottom() - 79)),
                    new SizeF(140, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_9));
        } else if (step == Step.UPGRADE_UI) {
            guideRectangle.hide();
            guideRectangle.commit();

            game.closeHomeBox();
            game.popupUpgradeBox();

            Rect boxRegion = game.getUpgradeBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 20, boxRegion.bottom() - 79)),
                    new SizeF(430, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_10));
        } else if (step == Step.FOCUS_TILE) {
            guideRectangle.hide();
            guideRectangle.commit();

            game.closeUpgradeBox();

            OffsetCoord tileMapPosition = new OffsetCoord(2,3);
            Territory territory = game.getMap().getTerritory(tileMapPosition);
            territory.setFocus(true);
            game.onMapTerritoryFocused(territory);
            PointF tilePosition = tileMapPosition.toGameCoord();
            guideRectangle.setRegion(new RectF(tilePosition.offset(-60, -60), new SizeF(120, 120)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_11));
        } else if (step == Step.INFO_BUTTON) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(140, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_12));
        } else if (step == Step.INFO_TERRITORY) {
            Territory territory = game.getMap().getTerritory(new OffsetCoord(2, 3));
            game.popupInfoBox(territory);

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_13));
        } else if (step == Step.INFO_FACILITY) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 35, boxRegion.top() + 190)),
                    new SizeF(280, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_14));
        } else if (step == Step.INFO_FACILITY2) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 35, boxRegion.top() + 190)),
                    new SizeF(65, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_15));
        } else if (step == Step.INFO_FACILITY3) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 110, boxRegion.top() + 190)),
                    new SizeF(65, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_16));
        } else if (step == Step.INFO_FACILITY4) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 180, boxRegion.top() + 190)),
                    new SizeF(65, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_17));
        } else if (step == Step.INFO_FACILITY5) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 250, boxRegion.top() + 190)),
                    new SizeF(65, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_18));
        } else if (step == Step.SQUAD_BUILDER_BUTTON) {
            game.closeInfoBox();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(260, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_19));
        } else if (step == Step.SQUAD_BUILDING) {
            game.pressSquadBuilderButton();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_20));
        } else if (step == Step.SQUAD_BUILDING2) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 35, boxRegion.top() + 210)),
                    new SizeF(220, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_21));
        } else if (step == Step.SQUAD_RECRUITING) {
            guideRectangle.hide();
            guideRectangle.commit();

            game.pressUnitSelectionButton();

            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 15, boxRegion.bottom() - 62)),
                    new SizeF(430, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_22));
        } else if (step == Step.FINAL) {
            guideRectangle.hide();
            guideRectangle.commit();

            game.closeUnitSelectionBox();
            game.closeInfoBox();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_management_23));
        } else if (step == Step.FINISHED) {
            goNext = false;

            // Tutorial is finished
            eventHandler.onTutorialGuideForManagementFinished(this);
        }

        if (goNext) {
            step = Step.values()[step.ordinal() + 1];
            Engine2D.GetInstance().playSoundEffect("switch33", 1.0f);
        }
    }

    private Event eventHandler;
    private MainGame game;
    private MessageBox tutorialBox;
    private Rectangle guideRectangle;
    private Step step = Step.INTRODUCTION;
}
