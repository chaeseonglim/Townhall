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
                "존경하는 촌장님!\n\n" +
                "저는 당신의 조언가입니다. " +
                "지금부터 부대를 운영하는 방법을 알려드리고자 합니다.\n\n\n" +
                "아무 곳이나 터치하세요.")
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

            tutorialBox.setMessage(
                    "∙ 당신은 두 개의 부대를 이미 보유하고 있습니다.\n\n" +
                    "∙ 부대의 위치는 타일 중앙에 아이콘으로 표시됩니다.");
        } else if (step == Step.FOCUS_SQUAD2) {
            tutorialBox.setMessage(
                    "∙ 각 부대는 3개의 유닛을 보유할 수 있습니다.\n\n" +
                    "∙ 현재 유닛을 모집 중인 상태입니다. 잠시 기다려주세요...");
        } else if (step == Step.UNIT) {
            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 유닛 모집이 완료 되었습니다.\n\n" +
                    "∙ 부대에 속한 유닛이 지도 상에 작은 원으로 나타납니다. 아무 곳이나 터치하세요.");
        } else if (step == Step.INFO_SQUAD) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(140, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(0);
            game.popupInfoBox(squad);

            tutorialBox.setMessage(
                    "∙ 하단의 정보 버튼을 눌러 부대의 정보를 확인 가능합니다. \n\n" +
                    "∙ 추가 효과에 나타난 것처럼 다양한 지역/시설이 부대에 영향을 주게 됩니다.");
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

            tutorialBox.setMessage(
                    "∙ 이제 부대를 이동해 보겠습니다.\n\n" +
                    "∙ 부대 아이콘을 드래그해서 다른 타일에 옮기면 부대가 이동합니다.");
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

            tutorialBox.setMessage(
                    "∙ 이번엔 제가 직접 이동시켜 드리겠습니다." +
                    "∙ 부대가 빨간 상자의 위치로 이동 중입니다. \n\n잠시 기다려주세요..");
        } else if (step == Step.FOG) {
            guideRectangle.hide();
            guideRectangle.commit();

            guideRectangle2.hide();
            guideRectangle2.commit();

            tutorialBox.setMessage(
                    "∙ 자 이제 부대 이동이 완료되었습니다.\n\n" +
                    "∙ 부대가 이동함에 따라서 주위의 안개가 걷히는 것을 볼 수 있습니다.");
        } else if (step == Step.OCCUPYING_TERRITORY) {
            tutorialBox.setMessage(
                    "∙ 부대가 위치한 곳이 아군의 영토가 아니라면 점령을 시작합니다.\n\n잠시 기다려주세요..");
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

            tutorialBox.setMessage(
                    "∙ 자 이제 새로운 영토를 점령했습니다.\n\n" +
                    "∙ 분명 도적이 이 근처에 있을 것입니다. 유닛을 더 이동해보겠습니다.");
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

            tutorialBox.setMessage(
                    "∙ 드디어 도적을 발견했습니다.\n\n" +
                    "∙ 도적 부대는 현재 본부에 주둔 중입니다.");
        } else if (step == Step.ENEMY_SQUAD_INFO2) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(140, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            Squad squad = game.getTribe(Tribe.Faction.BANDIT).getSquads().get(0);
            squad.setFocus(true);
            game.onSquadFocused(squad);
            game.popupInfoBox(squad);

            tutorialBox.setMessage(
                    "∙ 도적 부대를 선택 후 하단 정보 버튼을 누르면 정보를 보여줍니다.\n\n" +
                    "∙ 도적은 본부에 주둔하고 있어 방어도 추가 효과를 누리고 있습니다.");
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

            tutorialBox.setMessage(
                    "∙ 이제 도적을 제거할 시간입니다.\n\n" +
                    "∙ 전투를 시작하기 위해서는 부대를 적 부대 위치로 드래그 해야 합니다.");
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

            tutorialBox.setMessage(
                    "∙ 이번에는 제가 부대를 움직여보겠습니다.\n\n" +
                    "∙ 잠시 전투를 감상하세요..");
        } else if (step == Step.SUPPORT) {
            Squad squad = game.getTribe(Tribe.Faction.VILLAGER).getSquads().get(1);
            OffsetCoord squadMapPosition = squad.getMapPosition();
            PointF squadPosition = squadMapPosition.toGameCoord();
            guideRectangle.setRegion(new RectF(squadPosition.offset(-40, -60),
                    new SizeF(80, 80)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 우리 부대는 잘 싸우고 있지만 도움이 필요할 것 같습니다.\n\n" +
                    "∙ 나머지 부대를 움직여 아군을 지원하도록 하겠습니다.");
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

            tutorialBox.setMessage(
                    "∙ 아군 부대를 전투가 벌어지는 타일 옆에 놓으면 전투를 지원합니다. " +
                    "∙ 이번에는 제가 직접 옮겨보겠습니다.\n\n잠시 기다려주세요..");
        } else if (step == Step.SUPPORT3) {
            guideRectangle.hide();
            guideRectangle.commit();

            guideRectangle2.hide();
            guideRectangle2.commit();

            tutorialBox.setMessage(
                    "∙ 이제 아군 부대가 전투를 지원하기 시작했습니다.\n\n" +
                    "∙ 지원은 불리한 전황을 바꿀 수 있는 핵심적인 전략입니다. 현명하게 사용하세요.");
        } else if (step == Step.SUPPORT4) {
            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 참고로 모든 유닛이 다 지원이 가능하지는 않습니다.\n\n" +
                    "∙ 따라서 직접 전투할 부대와 지원할 부대를 신중하게 선택해야 합니다.");
        } else if (step == Step.FINAL) {
            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "촌장님!\n" +
                    "부대 운영에 대한 설명을 이만 마치겠습니다.\n\n" +
                    "∙ 팁: 이제 도적 부대를 박살내세요!");
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
