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

public class TutorialGuideForBattle extends Widget implements MessageBox.Event {

    private final static String LOG_TAG = "TutorialGuideForBattle";

    interface Event {
        void onTutorialGuideForBattleFinished(TutorialGuideForBattle tutorial);
    }

    enum Step {
        INTRODUCTION,
        FOCUS_SQUAD,
        FOCUS_SQUAD2,
        UNIT,
        INFO_SQUAD,
        MOVE_SQUAD,
        MOVE_SQUAD2,
        FOG,
        OCCUPYING_TERRITORY,
        MOVE_SQUAD3,
        ENEMY_SQUAD_INFO,
        ENEMY_SQUAD_INFO2,
        BATTLE,
        BATTLE2,
        SUPPORT,
        SUPPORT2,
        SUPPORT3,
        SUPPORT4,
        FINAL,
        FINISHED
    }

    public TutorialGuideForBattle(Event eventHandler, MainGame game, Step startingStep) {
        super(new Rect(), 30, 0);
        this.eventHandler = eventHandler;
        this.game = game;
        this.step = startingStep;

        guideRectangle = new Rectangle.Builder(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                new PointF(80, 10)), new SizeF(170, 64)))
                .color(Color.argb(255, 255, 255, 255)).lineWidth(10.0f)
                .layer(255).visible(false).build();
        guideRectangle.commit();

        guideRectangle2 = new Rectangle.Builder(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                new PointF(80, 10)), new SizeF(170, 64)))
                .color(Color.argb(255, 255, 0, 0)).lineWidth(10.0f)
                .layer(255).visible(false).build();
        guideRectangle2.commit();

        Rect viewport = Engine2D.GetInstance().getViewport();
        tutorialBox = new MessageBox.Builder(this, MessageBox.Type.PLATE,
                new Rect(viewport.width - 340 - 20, viewport.height - 240 - 90,
                        340, 250),
                Engine2D.GetInstance().getString(R.string.tutorial_battle_1))
                .fontSize(24.0f).layer(50).textColor(Color.rgb(235, 235, 235))
                .fontName("neodgm.ttf")
                .bgAsset("tutorial_box_bg.png").bgOpaque(0.8f)
                .build();
        tutorialBox.setFollowParentVisibility(false);
        tutorialBox.show();
        addWidget(tutorialBox);
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
            handleTouch();
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
    private void handleTouch() {
        if (step != Step.FOCUS_SQUAD2 &&
            step != Step.MOVE_SQUAD2 &&
            step != Step.OCCUPYING_TERRITORY &&
            step != Step.BATTLE2 &&
            step != Step.SUPPORT2 &&
            step != Step.FINISHED) {
            Engine2D.GetInstance().playSoundEffect("switch33", 1.0f);
            step = Step.values()[step.ordinal() + 1];
        }
    }

    /**
     *
     */
    @Override
    public void update() {
        super.update();

        boolean goNext = false;

        if (step == Step.FOCUS_SQUAD2) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            if (!squad.isRecruiting()) {
                goNext = true;
            } else {
                game.resumeFromTutorial();
            }
        } else if (step == Step.MOVE_SQUAD2 ||
                    step == Step.MOVE_SQUAD3) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            if (!squad.isMoving()) {
                goNext = true;
            } else {
                game.resumeFromTutorial();
            }
        } else if (step == Step.OCCUPYING_TERRITORY) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            if (game.getMap().getTerritory(squad.getMapPosition()).getFaction() == Tribe.Faction.VILLAGER) {
                goNext = true;
            } else {
                game.resumeFromTutorial();
            }
        } else if (step == Step.BATTLE2) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            if (squad.isFighting() && squad.getHealthPercentage() < 0.9f) {
                goNext = true;
            } else {
                game.resumeFromTutorial();
            }
        } else if (step == Step.SUPPORT2) {
            Squad squad0 = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            Squad squad1 = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(1);
            if (!squad0.isFighting() ||
                    (squad1.isSupporting() &&
                    game.getMap().getTerritory(squad1.getMapPosition()).getFaction() == Tribe.Faction.VILLAGER)) {
                goNext = true;
            } else {
                game.resumeFromTutorial();
            }
        } else {
            game.pauseForTutorial();
        }

        if (goNext) {
            step = Step.values()[step.ordinal() + 1];
            Engine2D.GetInstance().playSoundEffect("switch33", 1.0f);

            updateGuide();
        }
    }

    /**
     *
     */
    private void updateGuide() {
        Rect viewport = Engine2D.GetInstance().getViewport();

        if (step == Step.INTRODUCTION) {
            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.show();
        } else if (step == Step.FOCUS_SQUAD) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            squad.setFocus(true);
            game.onSquadFocused(squad);

            PointF squadPosition = game.getTribe(Tribe.Faction.VILLAGER).getHeadquarterPosition().toGameCoord();
            guideRectangle.setRegion(new RectF(squadPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_2));
        } else if (step == Step.FOCUS_SQUAD2) {
            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_3));
        } else if (step == Step.UNIT) {
            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_4));
        } else if (step == Step.INFO_SQUAD) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(140, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            game.popupInfoBox(squad);

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_5));
        } else if (step == Step.MOVE_SQUAD) {
            guideRectangle.hide();
            guideRectangle.commit();

            game.closeInfoBox();

            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            OffsetCoord squadMapPosition = squad.getMapPosition();
            PointF squadPosition = squadMapPosition.toGameCoord();
            guideRectangle.setRegion(new RectF(squadPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_6));
        } else if (step == Step.MOVE_SQUAD2) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            OffsetCoord squadTargetMapPosition = squad.getMapPosition().clone();
            squadTargetMapPosition.offset(0, -1);
            PointF squadTargetPosition = squadTargetMapPosition.toGameCoord();
            guideRectangle2.setRegion(new RectF(squadTargetPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle2.show();
            guideRectangle2.commit();

            squad.seekTo(squadTargetMapPosition, false);

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_7));
        } else if (step == Step.FOG) {
            guideRectangle.hide();
            guideRectangle.commit();

            guideRectangle2.hide();
            guideRectangle2.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_8));
        } else if (step == Step.OCCUPYING_TERRITORY) {
            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_9));
        } else if (step == Step.MOVE_SQUAD3) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            OffsetCoord squadMapPosition = squad.getMapPosition();
            PointF squadPosition = squadMapPosition.toGameCoord();
            guideRectangle.setRegion(new RectF(squadPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle.show();
            guideRectangle.commit();

            OffsetCoord squadTargetMapPosition = squad.getMapPosition().clone();
            squadTargetMapPosition.offset(1, -1);
            PointF squadTargetPosition = squadTargetMapPosition.toGameCoord();
            guideRectangle2.setRegion(new RectF(squadTargetPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle2.show();
            guideRectangle2.commit();

            squad.seekTo(squadTargetMapPosition, false);

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_10));
        } else if (step == Step.ENEMY_SQUAD_INFO) {
            Squad squad = game.getTribe(Tribe.Faction.BANDIT).getSquads().get(0);
            OffsetCoord squadMapPosition = squad.getMapPosition();
            PointF squadPosition = squadMapPosition.toGameCoord();
            guideRectangle.setRegion(new RectF(squadPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle.show();
            guideRectangle.commit();

            guideRectangle2.hide();
            guideRectangle2.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_11));
        } else if (step == Step.ENEMY_SQUAD_INFO2) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(140, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            Squad squad = game.getTribe(Tribe.Faction.BANDIT).getSquads().get(0);
            squad.setFocus(true);
            game.onSquadFocused(squad);
            game.popupInfoBox(squad);

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_12));
        } else if (step == Step.BATTLE) {
            guideRectangle.hide();
            guideRectangle.commit();

            game.closeInfoBox();

            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            OffsetCoord squadMapPosition = squad.getMapPosition();
            PointF squadPosition = squadMapPosition.toGameCoord();
            guideRectangle.setRegion(new RectF(squadPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle.show();
            guideRectangle.commit();

            Squad bandit = game.getTribe(Tribe.Faction.BANDIT).getSquads().get(0);
            OffsetCoord banditMapPosition = bandit.getMapPosition();
            PointF banditPosition = banditMapPosition.toGameCoord();
            guideRectangle2.setRegion(new RectF(banditPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle2.show();
            guideRectangle2.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_13));
        } else if (step == Step.BATTLE2) {
            guideRectangle.hide();
            guideRectangle.commit();

            guideRectangle2.hide();
            guideRectangle2.commit();

            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            squad.setFocus(true);
            game.onSquadFocused(squad);
            Squad bandit = game.getTribe(Tribe.Faction.BANDIT).getSquads().get(0);
            squad.seekTo(bandit.getMapPosition(), false);

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_14));
        } else if (step == Step.SUPPORT) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(1);
            OffsetCoord squadMapPosition = squad.getMapPosition();
            PointF squadPosition = squadMapPosition.toGameCoord();
            guideRectangle.setRegion(new RectF(squadPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_15));
        } else if (step == Step.SUPPORT2) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(1);
            OffsetCoord targetMapPosition = squad.getMapPosition().clone();
            targetMapPosition.offset(0, -1);
            PointF targetPosition = targetMapPosition.toGameCoord();
            guideRectangle2.setRegion(new RectF(targetPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle2.show();
            guideRectangle2.commit();

            squad.seekTo(targetMapPosition, false);

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_16));
        } else if (step == Step.SUPPORT3) {
            guideRectangle.hide();
            guideRectangle.commit();

            guideRectangle2.hide();
            guideRectangle2.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_17));
        } else if (step == Step.SUPPORT4) {
            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_18));
        } else if (step == Step.FINAL) {
            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(Engine2D.GetInstance().getString(R.string.tutorial_battle_19));
        } else if (step == Step.FINISHED) {
            eventHandler.onTutorialGuideForBattleFinished(this);
        }
    }

    private Event eventHandler;
    private MainGame game;
    private MessageBox tutorialBox;
    private Rectangle guideRectangle;
    private Rectangle guideRectangle2;
    private Step step;
}
