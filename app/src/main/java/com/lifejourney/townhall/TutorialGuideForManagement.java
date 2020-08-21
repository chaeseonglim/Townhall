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
        SQUAD_BUILDER_BUTTON,
        SQUAD_BUILDING,
        SQUAD_BUILDING2,
        SQUAD_RECRUITING,
        FINAL
    }

    public TutorialGuideForManagement(Event eventHandler, MainGame game) {
        super(new Rect(), 30, 0);
        this.eventHandler = eventHandler;
        this.game = game;

        Rect viewport = Engine2D.GetInstance().getViewport();
        tutorialBox = new MessageBox.Builder(this, MessageBox.Type.PLATE,
                new Rect(viewport.width - 353 - 20, viewport.height - 275 - 100,
                        353, 275),
                "존경하는 촌장님!\n" +
                "저는 당신의 조언가입니다.\n\n" +
                "지금부터 마을을 운영하는\n방법을 알려드리고자 합니다.\n\n" +
                "아무 곳이나 터치하세요.")
                .fontSize(25.0f).layer(50).textColor(Color.rgb(0, 0, 0))
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
            Engine2D.GetInstance().playSoundEffect("click3", 1.0f);
            if (Step.values().length > step.ordinal() + 1) {
                step = Step.values()[step.ordinal() + 1];
                updateGuide();
            } else {
                // Tutorial is finished
                eventHandler.onTutorialGuideForManagementFinished(this);
            }
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
        if (step == Step.GOLD) {
            guideRectangle = new Rectangle.Builder(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(80, 10)), new SizeF(170, 64)))
                    .color(Color.argb(255, 255, 255, 255)).lineWidth(10.0f)
                    .layer(255).visible(true).build();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "상단의 숫자는 마을이 보유\n하고 있는 금화의 갯수를\n보여줍니다.\n\n" +
                            "금화는 마을 개발을 통해 늘릴\n수 있으며 병력 모집 및 강화\n에 사용됩니다.");
        } else if (step == Step.POPULATION) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(330, 10)), new SizeF(130, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "상단의 숫자는 마을의 인구를\n보여줍니다.\n\n" +
                            "인구는 마을 개발을 통해 늘릴\n수 있으며 병력 모집에 사용\n됩니다.");
        } else if (step == Step.HAPPINESS) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(270, 10)), new SizeF(60, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "상단의 표정은 마을 사람들의\n행복도를 보여줍니다.\n\n" +
                            "행복도는 금화 수입에 영향을\n주며 낮아지면 반란군이 발생\n할 가능성이 높아집니다.");
        } else if (step == Step.SPEED) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(viewport.width - 210, 10)), new SizeF(200, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "상단의 버튼은 게임의\n속도를 조절합니다.\n\n" +
                            "게임은 언제든 일시 정지 혹은\n진행 속도 조절이 가능합니다.");
        } else if (step == Step.MISSION_TARGET) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(viewport.width - 320, 10)), new SizeF(100, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "상단 흰색 상자의 버튼은 현재\n미션의 목표를 보여줍니다.\n");
        } else if (step == Step.HOME_BUTTON) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(20, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "하단 홈 버튼은 마을 전체의\n상태를 보여줍니다.\n\n이 곳에서 병력의 강화도\n할 수 있습니다.");
        } else if (step == Step.HOME_UI) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            game.popupHomeBox();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "홈 버튼을 누르면 나오는 화면\n입니다. 마을 전체의 통계가\n보여집니다.");
        } else if (step == Step.UPGRADE_BUTTON) {
            Rect boxRegion = game.getHomeBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.right() - 310, boxRegion.bottom() - 69)),
                    new SizeF(150, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "하단 강화하기 버튼을 누르면 \n병력 강화 화면으로\n전환됩니다.");
        } else if (step == Step.UPGRADE_UI) {
            game.closeHomeBox();
            game.popupUpgradeBox();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "병력 강화 화면입니다. 아래\n클래스 버튼을 선택해\n병력을 강화할 수 있습니다.\n\n병력을 강화하면 유지비가 발생\n하며 취소할 수 없습니다.");
        } else if (step == Step.FOCUS_TILE) {
            game.closeUpgradeBox();

            Territory territory = game.getMap().getTerritory(new OffsetCoord(2, 3));
            territory.setFocus(true);
            game.onMapTerritoryFocused(territory);
            PointF tile = new OffsetCoord(2, 3).toGameCoord();
            guideRectangle.setRegion(new RectF(tile.clone().offset(-60, -60), new SizeF(120, 130)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "타일을 선택하면 추가 행동이\n가능합니다.");
        } else if (step == Step.INFO_BUTTON) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(140, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "하단 정보 버튼은 선택된\n타일 혹은 병력의 상태를\n보여줍니다.");
        } else if (step == Step.INFO_TERRITORY) {
            Territory territory = game.getMap().getTerritory(new OffsetCoord(2, 3));
            game.popupInfoBox(territory);

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "타일 정보를 보여주는 화면\n입니다. 시설의 개발 방향을\n설정 가능합니다.");
        } else if (step == Step.INFO_FACILITY) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 20, boxRegion.top() + 190)),
                    new SizeF(300, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "시설은 4개 버튼을 눌러 개발\n방향을 결정할 수 있습니다.\n\n시설의 전체 레벨 합이 5로\n제한되어 있으므로 적절하게\n조정하는 것이 중요합니다.");
        } else if (step == Step.INFO_FACILITY2) {
            tutorialBox.setMessage(
                    "농장은 인구를, 시장은 금화\n수입을 늘려줍니다.\n" +
                            "마을은 인접 타일의 농장 및\n시장의 효율을 높여줍니다.\n요새는 지역 방어도를 올리지만\n인접 타일의 개발과 행복도에\n악영향이 있습니다.");
        } else if (step == Step.SQUAD_BUILDER_BUTTON) {
            game.closeInfoBox();

            Rect viewport = Engine2D.GetInstance().getViewport();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(260, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "하단 부대 모집 버튼을 눌러\n신규 부대를 생성할 수\n 있습니다.\n\n이 버튼은 부대 생성이 가능한\n타일을 선택한 경우에만\n활성화됩니다.");
        } else if (step == Step.SQUAD_BUILDING) {
            game.pressSquadBuilderButton();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "부대 모집 버튼을 누르면\n부대가 즉시 소집되고 병력을\n추가할 수 있습니다.\n\n만약 병력을 추가하지 않으면\n부대 소집은 취소됩니다.");
        } else if (step == Step.SQUAD_BUILDING2) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 20, boxRegion.top() + 190)),
                    new SizeF(200, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "병력 추가 버튼을 눌러서\n병력을 추가할 수 있습니다.\n\n부대 별로 최대 3개의 병력을\n추가할 수 있습니다.");
        } else if (step == Step.SQUAD_RECRUITING) {
            game.pressUnitSelectionButton();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "병력 추가 버튼을 누르면\n나오는 화면입니다.\n\n하단 클래스 버튼을 선택하여\n추가할 병력을 선택할 수\n있습니다.");
        } else if (step == Step.FINAL) {
            game.closeUnitSelectionBox();
            game.closeInfoBox();

            tutorialBox.setMessage(
                    "촌장님!\n마을 운영에 대한 설명을\n이만 마치겠습니다.\n\n이제 주변 농장을 개척하여\n인구를 늘려셔야 합니다.");
        }
    }

    private Event eventHandler;
    private MainGame game;
    private MessageBox tutorialBox;
    private Rectangle guideRectangle = null;
    private Step step = Step.INTRODUCTION;
}
