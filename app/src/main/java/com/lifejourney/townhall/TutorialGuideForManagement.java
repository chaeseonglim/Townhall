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
        FINAL
    }

    public TutorialGuideForManagement(Event eventHandler, MainGame game) {
        super(new Rect(), 30, 0);
        this.eventHandler = eventHandler;
        this.game = game;

        Rect viewport = Engine2D.GetInstance().getViewport();
        tutorialBox = new MessageBox.Builder(this, MessageBox.Type.PLATE,
                new Rect(viewport.width - 340 - 20, viewport.height - 275 - 100,
                        340, 275),
                "존경하는 촌장님!\n\n" +
                "저는 당신의 조언가입니다. " +
                "지금부터 마을을 운영하는 방법을 알려드리고자 합니다.\n\n\n" +
                "아무 곳이나 터치하세요.")
                .fontSize(24.0f).layer(50).textColor(Color.rgb(210, 210, 210))
                .bgAsset("tutorial_box_bg.png").bgOpaque(1.0f)
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
            Engine2D.GetInstance().playSoundEffect("switch33", 1.0f);
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
                    "∙ 상단의 숫자는 마을의 금화를 나타냅니다.\n\n" +
                    "∙ 금화는 병력 모집 및 강화에 사용됩니다. 농장 개발을 통해 늘릴 수 있습니다.");
        } else if (step == Step.POPULATION) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(330, 10)), new SizeF(130, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 상단의 숫자는 마을의 인구를 나타냅니다.\n\n" +
                    "∙ 인구는 병력 모집에 사용되며 마을 개발을 통해 늘릴 수 있습니다.");
        } else if (step == Step.HAPPINESS) {
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(270, 10)), new SizeF(60, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 상단의 표정은 마을 사람들의 행복도를 나타냅니다.\n\n" +
                    "∙ 행복도는 금화 수입에 영향을 주며 낮아지면 반란군이 발생할 가능성이 높아집니다.");
        } else if (step == Step.SPEED) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(viewport.width - 210, 10)), new SizeF(200, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 상단의 버튼은 게임의 속도를 조절합니다.\n\n" +
                    "∙ 게임은 언제든 일시 정지 혹은 속도 조절이 가능합니다.");
        } else if (step == Step.MISSION_TARGET) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(viewport.width - 320, 10)), new SizeF(100, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 상단 흰색 상자의 버튼은 현재 미션의 목표를 보여줍니다.\n");
        } else if (step == Step.HOME_BUTTON) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(20, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 하단 홈 버튼은 마을 전체의 상태를 보여줍니다.\n\n" +
                    "∙ 병력의 강화도 이 버튼을 통해 할 수 있습니다.");
        } else if (step == Step.HOME_UI) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            game.popupHomeBox();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 홈 버튼을 누르면 나오는 화면입니다.\n\n" +
                    "∙ 마을 전체의 통계가 한눈에 보입니다.");
        } else if (step == Step.UPGRADE_BUTTON) {
            Rect boxRegion = game.getHomeBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.right() - 310, boxRegion.bottom() - 69)),
                    new SizeF(150, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 하단 강화하기 버튼을 누르면 병력 강화를 진행할 수 있습니다.");
        } else if (step == Step.UPGRADE_UI) {
            game.closeHomeBox();
            game.popupUpgradeBox();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 병력 강화 화면입니다. 상자 하단 버튼을 눌러 강화할 클래스를 선택할 수 있습니다.\n\n" +
                            "∙ 병력을 강화하면 유지비가 발생하며 취소할 수 없습니다.");
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
                    "∙ 타일을 선택하면 다양한 추가 행동이 가능합니다.");
        } else if (step == Step.INFO_BUTTON) {
            Rect viewport = Engine2D.GetInstance().getViewport();

            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(140, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 하단 정보 버튼을 선택하면 선택된 타일 혹은 병력의 정보를 보여줍니다.");
        } else if (step == Step.INFO_TERRITORY) {
            Territory territory = game.getMap().getTerritory(new OffsetCoord(2, 3));
            game.popupInfoBox(territory);

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 타일 정보를 보여주는 화면입니다.\n\n" +
                    "∙ 각 타일의 수입이나 인구 등의 정보를 파악 가능합니다.");
        } else if (step == Step.INFO_FACILITY) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 20, boxRegion.top() + 180)),
                    new SizeF(290, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 각 시설별 버튼을 통해 개발 방향을 정할 수 있습니다.\n\n" +
                    "∙ 시설의 전체 레벨 합이 5로 제한되어 있으므로 적절하게 조정하는 것이 중요합니다.");
        } else if (step == Step.INFO_FACILITY2) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 20, boxRegion.top() + 180)),
                    new SizeF(65, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 농장은 인구를 늘려줍니다.\n\n" +
                    "∙ 농장은 초원과 황무지에서 빠르게 성장하는 반면 숲과 언덕에서는 느립니다.");
        } else if (step == Step.INFO_FACILITY3) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 95, boxRegion.top() + 180)),
                    new SizeF(65, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 시장은 금화 수입을 늘려줍니다.\n\n" +
                    "∙ 시장은 초원과 숲에서 빠르게 성장하는 반면 언덕에서는 성장이 느립니다.");
        } else if (step == Step.INFO_FACILITY4) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 170, boxRegion.top() + 180)),
                    new SizeF(65, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 마을은 인접 타일의 농장 및 시장의 효율을 높여줍니다.\n\n" +
                    "∙ 마을은 황무지와 언덕에서 빠르게 성장하는 반면 숲에서는 성장이 느립니다.");
        } else if (step == Step.INFO_FACILITY5) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 245, boxRegion.top() + 180)),
                    new SizeF(65, 70)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 요새는 지역 방어도를 올리지만 인접 타일의 개발과 행복도에 악영향을 줍니다.\n\n" +
                    "∙ 요새는 숲과 언덕에서 빠르게 성장하지만 평지와 황무지에서는 성장이 느립니다.");
        } else if (step == Step.SQUAD_BUILDER_BUTTON) {
            game.closeInfoBox();

            Rect viewport = Engine2D.GetInstance().getViewport();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(260, viewport.height - 74)), new SizeF(100, 64)));
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 하단 부대 모집 버튼을 눌러 신규 부대를 생성할 수 있습니다.\n\n" +
                    "∙ 이 버튼은 부대 생성이 가능한 타일을 선택한 경우에만 활성화됩니다.");
        } else if (step == Step.SQUAD_BUILDING) {
            game.pressSquadBuilderButton();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 부대 모집 버튼을 누르면 부대가 즉시 소집되고 병력을 추가할 수 있습니다.\n\n" +
                    "∙ 만약 병력을 추가하지 않으면 부대 소집은 취소됩니다.");
        } else if (step == Step.SQUAD_BUILDING2) {
            Rect boxRegion = game.getInfoBoxRegion();
            guideRectangle.setRegion(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                    new PointF(boxRegion.left() + 20, boxRegion.top() + 190)),
                    new SizeF(230, 64)));
            guideRectangle.show();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 병력 추가 버튼을 눌러서 병력을 추가할 수 있습니다.\n\n" +
                    "∙ 부대 별로 최대 3개의 병력을 추가할 수 있습니다.");
        } else if (step == Step.SQUAD_RECRUITING) {
            game.pressUnitSelectionButton();

            guideRectangle.hide();
            guideRectangle.commit();

            tutorialBox.setMessage(
                    "∙ 병력 추가 버튼을 누르면 나오는 화면입니다.\n\n" +
                    "∙ 하단 클래스 버튼을 선택하여 추가할 병력을 선택할 수 있습니다.");
        } else if (step == Step.FINAL) {
            game.closeUnitSelectionBox();
            game.closeInfoBox();

            tutorialBox.setMessage(
                    "촌장님!\n" +
                    "마을 운영에 대한 설명을 이만 마치겠습니다.\n\n" +
                    "∙ 팁: 농장 개발에 집중하면 쉽게 미션 목표를 달성할 수 있습니다.");
        }
    }

    private Event eventHandler;
    private MainGame game;
    private MessageBox tutorialBox;
    private Rectangle guideRectangle = null;
    private Step step = Step.INTRODUCTION;
}
