package com.lifejourney.townhall;

import android.graphics.Color;
import android.text.Layout;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

public class MissionSelectionBox extends Widget implements Button.Event{

    private final String LOG_TAG = "MissionSelectionBox";

    public interface Event {
        void onMissionSelectionBoxCanceled(MissionSelectionBox missionSelectionBox);
        void onMissionSelectionBoxStart(MissionSelectionBox missionSelectionBox, Mission mission);
    }

    public MissionSelectionBox(Event eventHandler, Mission startingMission) {
        super(null, 30, 0.0f);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 702) / 2, (viewport.height - 502) / 2,
                702, 502);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;

        if (startingMission == null) {
            for (Mission mission : Mission.values()) {
                if (mission.getStarRating() == 0) {
                    this.selectedMission = mission;
                    break;
                }
            }
            if (this.selectedMission == null) {
                this.selectedMission = Mission.values()[Mission.values().length - 1];
            }
        } else {
            this.selectedMission = startingMission;
        }

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("mission_selection_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(true).layer(getLayer()).depth(getDepth())
                .gridSize(1, 1).visible(false).opaque(1.0f).build();
        addSprite(backgroundSprite);

        // Cancel button
        Rect cancelButtonRegion = new Rect(getRegion().right() - 392, getRegion().bottom() - 77,
                138, 64);
        cancelButton = new Button.Builder(this, cancelButtonRegion)
                .message("뒤로").imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(getLayer() + 1).build();
        addWidget(cancelButton);

        // Start button
        Rect startButtonRegion = new Rect(getRegion().right() - 246, getRegion().bottom() - 77,
                138, 64);
        startButton = new Button.Builder(this, startButtonRegion)
                .message("시작").imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(getLayer() + 1).build();
        addWidget(startButton);

        // Left button
        Rect leftButtonRegion = new Rect(getRegion().left() - 5, getRegion().top() + 250 - 50,
                100, 100);
        leftButton = new Button.Builder(this, leftButtonRegion)
                .imageSpriteAsset("left_right_btns.png").numImageSpriteSet(2)
                .fontSize(23).layer(getLayer()+1).build();
        leftButton.setFollowParentVisibility(false);
        addWidget(leftButton);

        // Right button
        Rect rightButtonRegion = new Rect(getRegion().right() - 95, getRegion().top() + 250 - 50,
                100, 100);
        rightButton = new Button.Builder(this, rightButtonRegion)
                .imageSpriteAsset("left_right_btns.png").numImageSpriteSet(2)
                .fontSize(23).layer(getLayer()+1).build();
        rightButton.setFollowParentVisibility(false);
        rightButton.setImageSpriteSet(1);
        addWidget(rightButton);

        updateMissionInfo();
    }

    /**
     *
     */
    private void updateMissionInfo() {
        // Remove all previous texts
        removeSprites("text");
        removeSprites("icon");

        // Set left button
        leftButton.show();
        if (selectedMission.ordinal() == 0) {
            leftButton.hide();
        } else if (!Mission.values()[selectedMission.ordinal() - 1].isUnlocked()) {
            leftButton.disable();
        }

        // Set right button
        rightButton.show();
        if (Mission.values().length == selectedMission.ordinal() + 1) {
            rightButton.hide();
        } else if (!Mission.values()[selectedMission.ordinal() + 1].isUnlocked()) {
            rightButton.disable();
        }

        // Mission title
        PointF textPosition = new PointF(0, -208);
        addTitleText((selectedMission.ordinal() + 1) + " - " + selectedMission.getTitle(),
                new SizeF(480, 40), textPosition.clone(), 30,
                Color.rgb(235, 235, 0), Layout.Alignment.ALIGN_CENTER,
                Layout.Alignment.ALIGN_CENTER);

        // Mission description
        textPosition.setTo(0, -80);
        addText(selectedMission.getDescription(), new SizeF(460, 210), textPosition.clone(),
                23, Color.rgb(235, 235, 235), Layout.Alignment.ALIGN_CENTER,
                Layout.Alignment.ALIGN_CENTER);

        // Mission victory condition
        textPosition.setTo(0, 56);
        addText("승리 조건:", new SizeF(460, 40),
                textPosition.clone(),
                23, Color.rgb(235, 235, 0), Layout.Alignment.ALIGN_NORMAL,
                Layout.Alignment.ALIGN_CENTER);

        textPosition.setTo(0, 86);
        addText(selectedMission.getVictoryCondition(), new SizeF(460, 40),
                textPosition.clone(),
                23, Color.rgb(235, 235, 235), Layout.Alignment.ALIGN_NORMAL,
                Layout.Alignment.ALIGN_CENTER);

        // Mission time limit
        textPosition.setTo(0, 116);
        addText("시간 제한:", new SizeF(460, 40),
                textPosition.clone(),
                23, Color.rgb(235, 235, 0), Layout.Alignment.ALIGN_NORMAL,
                Layout.Alignment.ALIGN_CENTER);

        textPosition.setTo(0, 146);
        addText(selectedMission.getTimeLimit() + "일", new SizeF(460, 40),
                textPosition.clone(),
                23, Color.rgb(235, 235, 235), Layout.Alignment.ALIGN_NORMAL,
                Layout.Alignment.ALIGN_CENTER);

        // Star rating
        textPosition.offset(-248, 60);
        for (int i = 0; i < selectedMission.getStarRating(); ++i) {
            textPosition.offset(35, 0);
            addIcon("star.png", new SizeF(30, 30), textPosition.clone(),
                    3, 2, 0, 0);
        }
        for (int i = selectedMission.getStarRating(); i < 3; ++i) {
            textPosition.offset(35, 0);
            addIcon("star.png", new SizeF(30, 30), textPosition.clone(),
                    3, 2, 2, 0);
        }
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible()) {
            return false;
        }

        // It consumes all input when activated
        super.onTouchEvent(event);

        return true;
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {
        if (button == cancelButton) { // Cancel button
            eventHandler.onMissionSelectionBoxCanceled(this);
        } else if (button == startButton) { // Start button
            eventHandler.onMissionSelectionBoxStart(this, selectedMission);
        } else if (button == leftButton) { // Left button
            selectedMission = Mission.values()[selectedMission.ordinal() - 1];
            updateMissionInfo();
        } else if (button == rightButton) { // Right button
            selectedMission = Mission.values()[selectedMission.ordinal() + 1];
            updateMissionInfo();
        }
    }


    /**
     *
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addText(String text, SizeF size, PointF position, int fontSize, int fontColor,
                         Layout.Alignment horizontalAlignment, Layout.Alignment verticalAlignment) {
        addSprite(new TextSprite.Builder("text", text, fontSize)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(0, 0, 0), 1.0f)
                .horizontalAlign(horizontalAlignment).verticalAlign(verticalAlignment)
                .size(size).positionOffset(position)
                .smooth(true).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    /**
     *
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addTitleText(String text, SizeF size, PointF position, int fontSize, int fontColor,
                         Layout.Alignment horizontalAlignment, Layout.Alignment verticalAlignment) {
        addSprite(new TextSprite.Builder("text", text, fontSize)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(35, 35, 35), 2.0f)
                .horizontalAlign(horizontalAlignment).verticalAlign(verticalAlignment)
                .size(size).positionOffset(position)
                .smooth(true).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    /**
     *
     * @param asset
     * @param size
     * @param position
     */
    private void addIcon(String asset, SizeF size, PointF position, int gridCols, int gridRows, int col, int row) {
        Sprite sprite = new Sprite.Builder("icon", asset)
                .size(size).positionOffset(position)
                .smooth(false).depth(0.1f).gridSize(gridCols, gridRows)
                .layer(getLayer()+1).visible(false).build();
        sprite.setGridIndex(col, row);
        addSprite(sprite);
    }

    private Event eventHandler;
    private Button cancelButton;
    private Button startButton;
    private Button leftButton;
    private Button rightButton;
    private Mission selectedMission;
}
