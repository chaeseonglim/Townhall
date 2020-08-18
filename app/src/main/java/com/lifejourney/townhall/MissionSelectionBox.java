package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
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

    public MissionSelectionBox(Event eventHandler, int layer, float depth) {
        super(null, layer, depth);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 700) / 2, (viewport.height - 500) / 2,
                700, 500);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;
        this.selectedMission = Mission.LV1;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("mission_selection_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(true).layer(layer).depth(depth)
                .gridSize(1, 1).visible(false).opaque(1.0f).build();
        addSprite(backgroundSprite);

        // Cancel button
        Rect cancelButtonRegion = new Rect(getRegion().right() - 384, getRegion().bottom() - 73,
                136, 60);
        cancelButton = new Button.Builder(this, cancelButtonRegion)
                .message("뒤로").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(cancelButton);

        // Start button
        Rect startButtonRegion = new Rect(getRegion().right() - 244, getRegion().bottom() - 73,
                136, 60);
        startButton = new Button.Builder(this, startButtonRegion)
                .message("시작").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(startButton);

        // Left button
        Rect leftButtonRegion = new Rect(getRegion().left() - 5, getRegion().top() + 250 - 50,
                100, 100);
        leftButton = new Button.Builder(this, leftButtonRegion)
                .imageSpriteAsset("left_right_btns.png").numImageSpriteSet(2)
                .fontSize(25).layer(layer+1).build();
        leftButton.setFollowParentVisibility(false);
        addWidget(leftButton);

        // Right button
        Rect rightButtonRegion = new Rect(getRegion().right() - 95, getRegion().top() + 250 - 50,
                100, 100);
        rightButton = new Button.Builder(this, rightButtonRegion)
                .imageSpriteAsset("left_right_btns.png").numImageSpriteSet(2)
                .fontSize(25).layer(layer+1).build();
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
        PointF textPosition = new PointF(0, -210);
        addText("챕터 " + (selectedMission.ordinal() + 1) + " - " + selectedMission.getTitle(),
                new SizeF(480, 40), textPosition.clone(), 32,
                Color.rgb(255, 255, 0), Paint.Align.CENTER);

        // Mission description
        textPosition.setTo(0, -70);
        addText(selectedMission.getDescription(), new SizeF(460, 210), textPosition.clone(),
                25, Color.rgb(235, 235, 235), Paint.Align.LEFT);

        // Mission victory condition
        textPosition.setTo(0, 60);
        addText("승리 조건:", new SizeF(460, 40),
                textPosition.clone(),
                25, Color.rgb(255, 255, 0), Paint.Align.LEFT);

        textPosition.setTo(0, 90);
        addText(selectedMission.getVictoryCondition(), new SizeF(460, 40),
                textPosition.clone(),
                25, Color.rgb(235, 235, 235), Paint.Align.LEFT);

        // Mission time limit
        textPosition.setTo(0, 120);
        addText("시간 제한:", new SizeF(460, 40),
                textPosition.clone(),
                25, Color.rgb(255, 255, 0), Paint.Align.LEFT);

        textPosition.setTo(0, 150);
        addText(selectedMission.getTimeLimit() + "일", new SizeF(460, 40),
                textPosition.clone(),
                25, Color.rgb(235, 235, 235), Paint.Align.LEFT);

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
    private void addText(String text, SizeF size, PointF position, int fontSize, int fontColor, Paint.Align align) {
        addSprite(new TextSprite.Builder("text", text, fontSize)
                .fontColor(fontColor).bgColor(Color.argb(0, 0, 0, 0))
                .textAlign(align)
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
