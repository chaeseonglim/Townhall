package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.Widget;

public class InfoBox extends Widget implements Button.Event, UnitSelectionBox.Event {

    private final String LOG_TAG = "InfoBox";

    public interface Event {

        void onInfoBoxSwitchToTown(InfoBox infoBox);

        void onInfoBoxClosed(InfoBox infoBox);
    }

    public InfoBox(Event listener, Rect region, int layer, float depth, Town town) {

        super(region, layer, depth);

        this.listener = listener;
        this.town = town;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("info_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(layer).depth(depth)
                .gridSize(2, 1).visible(false).opaque(0.8f).build();
        backgroundSprite.setGridIndex(0, 0);
        addSprite(backgroundSprite);

        // Close button
        Rect closeButtonRegion = new Rect(region.right() - 155, region.bottom() - 65,
                150, 60);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message("닫기").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 0))
                .build();
        addWidget(closeButton);

        updateTownInfo();

    }

    public InfoBox(Event listener, Rect region, int layer, float depth, Squad squad) {

        super(region, layer, depth);
        this.listener = listener;
        this.squad = squad;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("info_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(layer).depth(depth)
                .gridSize(2, 1).visible(false).opaque(0.8f).build();
        backgroundSprite.setGridIndex(1, 0);
        addSprite(backgroundSprite);

        // Close button
        Rect closeButtonRegion = new Rect(region.right() - 155, region.bottom() - 65,
                150, 60);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message("닫기").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 255))
                .build();
        addWidget(closeButton);

        // Town button
        Rect toTownButtonRegion = new Rect(region.right() - 310, region.bottom() - 65,
                150, 60);
        toTownButton = new Button.Builder(this, toTownButtonRegion)
                .message("마을로").imageSpriteAsset("")
                .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 255))
                .build();
        addWidget(toTownButton);


        updateSquadInfo();
    }

    /**
     *
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addText(String text, SizeF size, PointF position, int fontColor) {

        addSprite(new TextSprite.Builder("text", text, 25)
                .fontColor(fontColor).bgColor(Color.argb(0, 0, 0, 0))
                .fontName("NanumBarunGothic.ttf")
                .textAlign(Paint.Align.LEFT)
                .size(size).positionOffset(position)
                .smooth(true).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // It consumes all input
        super.onTouchEvent(event);
        return true;
    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {

        if (button == closeButton) {
            // Close button
            setVisible(false);
            listener.onInfoBoxClosed(this);
        } else if (button == toTownButton) {
            // To town button
            setVisible(false);
            listener.onInfoBoxSwitchToTown(this);
        } else if (button == farmDevelopmentButton) {
            // Farm development button
            Town.FacilityDevelopment development = town.getFacilityDevelopment(Town.Facility.FARM);
            Town.FacilityDevelopment newDevelopment =
                    Town.FacilityDevelopment.values()[
                            (development.ordinal()+1)%Town.FacilityDevelopment.values().length];
            town.setFacilityDevelopment(Town.Facility.FARM, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal());
        } else if (button == marketDevelopmentButton) {
            // Market development button
            Town.FacilityDevelopment development = town.getFacilityDevelopment(Town.Facility.MARKET);
            Town.FacilityDevelopment newDevelopment =
                    Town.FacilityDevelopment.values()[
                            (development.ordinal()+1)%Town.FacilityDevelopment.values().length];
            town.setFacilityDevelopment(Town.Facility.MARKET, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal() + 3);
        } else if (button == downtownDevelopmentButton) {
            // Downtown development button
            Town.FacilityDevelopment development = town.getFacilityDevelopment(Town.Facility.DOWNTOWN);
            Town.FacilityDevelopment newDevelopment =
                    Town.FacilityDevelopment.values()[
                            (development.ordinal()+1)%Town.FacilityDevelopment.values().length];
            town.setFacilityDevelopment(Town.Facility.DOWNTOWN, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal() + 6);
        } else if (button == fortressDevelopmentButton) {
            // Fortress development button
            Town.FacilityDevelopment development = town.getFacilityDevelopment(Town.Facility.FORTRESS);
            Town.FacilityDevelopment newDevelopment =
                    Town.FacilityDevelopment.values()[
                            (development.ordinal()+1)%Town.FacilityDevelopment.values().length];
            town.setFacilityDevelopment(Town.Facility.FORTRESS, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal() + 9);
        } else if (button == recruitingButtons[0] ||
                button == recruitingButtons[1] ||
                button == recruitingButtons[2]) {
            for (int i = 0;; ++i) {
                if (button == recruitingButtons[i]) {
                    recruitingSlot = i;
                    break;
                }
            }
            hide();
            UnitSelectionBox unitSelectionBox =
                    new UnitSelectionBox(this, getRegion(), getLayer()+10, 0.0f);
            addWidget(unitSelectionBox);
            unitSelectionBox.show();
        }
    }

    /**
     *
     * @param infoBox
     * @param unitClass
     */
    @Override
    public void onUnitBuilderBoxSelected(UnitSelectionBox infoBox, Unit.UnitClass unitClass) {

        removeWidget(infoBox);
        show();

        if (unitClass != null) {
            squad.removeUnit(recruitingSlot);
            squad.spawnUnit(unitClass);
            updateSquadInfo();
        }
    }

    /**
     *
     */
    private void updateTownInfo() {

        Rect region = getRegion();
        int layer = getLayer();

        removeSprites("text");

        // Tile type
        PointF textPosition = new PointF(-250, -155);
        addText("지형", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(town.getTerrain().toGameString(), new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));

        // Status
        textPosition.offset(0, 30);
        addText("상태", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        String townStatus = town.getFaction().toGameString() + " 소유";
        if (town.getBattle() != null) {
            townStatus += " (전투중)";
        } else if (town.isOccupying()) {
            townStatus += " (점령중)";
        } else if (town.getFaction() == Town.Faction.VILLAGER){
            townStatus += " (개발중)";
        }
        textPosition.offset(75, 30);
        addText(townStatus, new SizeF(300, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));

        // Facility
        textPosition.offset(-75, 30);
        addText("시설", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        if (town.getTerrain().facilitySlots() == 0) {
            textPosition.offset(0, 30);
            addText("개발 불가", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
        } else {
            int facilityCount = 0;
            if (town.getFacilityLevel(Town.Facility.FARM) > 0) {
                facilityCount++;
                textPosition.offset(0, 30);
                addText("농장 Lv" + town.getFacilityLevel(Town.Facility.FARM),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));
            }
            if (town.getFacilityLevel(Town.Facility.MARKET) > 0) {
                facilityCount++;
                textPosition.offset(0, 30);
                addText("시장 Lv" + town.getFacilityLevel(Town.Facility.MARKET),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));
            }
            if (town.getFacilityLevel(Town.Facility.DOWNTOWN) > 0) {
                facilityCount++;
                textPosition.offset(0, 30);
                addText("마을 Lv" + town.getFacilityLevel(Town.Facility.DOWNTOWN),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));
            }
            if (town.getFacilityLevel(Town.Facility.FORTRESS) > 0) {
                facilityCount++;
                textPosition.offset(0, 30);
                addText("요새 Lv" + town.getFacilityLevel(Town.Facility.FORTRESS),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));
            }
            if (facilityCount == 0) {
                textPosition.offset(0, 30);
                addText("없음",
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));
            }

            // Development buttons
            if (town.getFaction() == Town.Faction.VILLAGER) {
                textPosition.offset(0, 30);
                addText("개발 방향",
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));

                if (farmDevelopmentButton != null) {
                    removeWidget(farmDevelopmentButton);
                }
                Rect developmentButtonRegion =
                        new Rect(region.left() + 22, region.top() + (int)textPosition.y + 219,
                                64, 64);
                farmDevelopmentButton =
                        new Button.Builder(this,  developmentButtonRegion.clone())
                                .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                                .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                                .build();
                farmDevelopmentButton.setImageSpriteSet(
                        town.getFacilityDevelopment(Town.Facility.FARM).ordinal());
                addWidget(farmDevelopmentButton);

                if (marketDevelopmentButton != null) {
                    removeWidget(marketDevelopmentButton);
                }
                developmentButtonRegion.offset(73, 0);
                marketDevelopmentButton =
                        new Button.Builder(this, developmentButtonRegion.clone())
                                .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                                .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                                .build();
                marketDevelopmentButton.setImageSpriteSet(
                        town.getFacilityDevelopment(Town.Facility.MARKET).ordinal() + 3);
                addWidget(marketDevelopmentButton);

                if (downtownDevelopmentButton != null) {
                    removeWidget(downtownDevelopmentButton);
                }
                developmentButtonRegion.offset(73, 0);
                downtownDevelopmentButton =
                        new Button.Builder(this, developmentButtonRegion.clone())
                                .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                                .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                                .build();
                downtownDevelopmentButton.setImageSpriteSet(
                        town.getFacilityDevelopment(Town.Facility.DOWNTOWN).ordinal() + 6);
                addWidget(downtownDevelopmentButton);

                if (fortressDevelopmentButton != null) {
                    removeWidget(fortressDevelopmentButton);
                }
                developmentButtonRegion.offset(73, 0);
                fortressDevelopmentButton =
                        new Button.Builder(this, developmentButtonRegion.clone())
                                .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                                .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                                .build();
                fortressDevelopmentButton.setImageSpriteSet(
                        town.getFacilityDevelopment(Town.Facility.FORTRESS).ordinal() + 9);
                addWidget(fortressDevelopmentButton);
            }
        }

        if (town.getFaction() == Town.Faction.VILLAGER) {
            // Population
            textPosition.setTo(100, -155);
            addText("인구 / 행복도",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText("없음 / 없음",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));

            // Income
            textPosition.offset(0, 30);
            addText("수입",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText("없음",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
            // Influence
            textPosition.offset(0, 30);
            addText("영향도",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText("없음",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
        }
    }

    private void updateSquadInfo() {

        Rect region = getRegion();
        int layer = getLayer();

        removeSprites("text");

        // Status
        PointF textPosition = new PointF(-250, -155);
        addText("상태", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        String status = squad.getFaction().toGameString() + " 부대";
        if (squad.isMoving()) {
            status += " (이동중)";
        } else if (squad.isFighting()) {
            status += " (전투중)";
        } else if (squad.isSupporting()) {
            status += " (지원중)";
        } else if (squad.isOccupying()) {
            status += " (점령중)";
        } else {
            status += " (대기중)";
        }
        textPosition.offset(75, 30);
        addText(status, new SizeF(300, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));

        // Unit information
        textPosition.offset(0, 30);
        addText("유닛", new SizeF(300, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        if (squad.getUnits().isEmpty()) {
            textPosition.offset(0, 50);
            addText("유닛이 없습니다.\n충원 버튼을 누르세요.", new SizeF(300, 80), textPosition.clone(),
                    Color.rgb(255, 255, 255));
            textPosition.offset(0, 8);
        }
        for (Unit unit : squad.getUnits()) {
            textPosition.offset(0, 30);
            String unitStr = unit.getUnitClass().toGameString() + " Lv" + unit.getLevel();
            if (unit.isRecruiting()) {
                unitStr += " (충원중)";
            }
            addText(unitStr, new SizeF(300, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
        }

        if (squad.getFaction() == Town.Faction.VILLAGER) {
            // Recruiting
            textPosition.offset(-75, 30);
            addText("충원", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));

            if (!squad.isMoving() &&
                    !squad.isSupporting() &&
                    !squad.isOccupying() &&
                    !squad.isFighting()) {
                Rect recruitingButtonRegion =
                        new Rect(region.left() + 22, region.top() + (int) textPosition.y + 219,
                                60, 64);
                for (int i = 0; i < 3; ++i) {
                    if (recruitingButtons[i] != null) {
                        removeWidget(recruitingButtons[i]);
                    }
                    if (squad.getUnits().size() >= i) {
                        recruitingButtons[i] =
                                new Button.Builder(this, recruitingButtonRegion.clone())
                                        .imageSpriteAsset("unit_recruiting_btn.png")
                                        .numImageSpriteSet(Unit.UnitClass.values().length + 1)
                                        .layer(layer + 1).build();
                        recruitingButtons[i].setImageSpriteSet((squad.getUnits().size() < (i + 1)) ?
                                0 : squad.getUnits().get(i).getUnitClass().ordinal() + 1);
                        addWidget(recruitingButtons[i]);
                    } else {
                        recruitingButtons[i] =
                                new Button.Builder(this, recruitingButtonRegion.clone())
                                        .imageSpriteAsset("unit_recruiting_btn.png")
                                        .numImageSpriteSet(Unit.UnitClass.values().length + 1)
                                        .layer(layer + 1).build();
                        recruitingButtons[i].setImageSpriteSet((squad.getUnits().size() < (i + 1)) ?
                                0 : squad.getUnits().get(i).getUnitClass().ordinal() + 1);
                        recruitingButtons[i].disable();
                        addWidget(recruitingButtons[i]);
                    }

                    recruitingButtonRegion.offset(69, 0);
                }
            } else {
                textPosition.offset(0, 30);
                addText("모집 불가", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 255));
            }
        }

        // Stats
        textPosition.setTo(175, -155);
        addText("보너스", new SizeF(300, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-75, 30);
        addText("-", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));
    }

    private Event listener;
    private Button closeButton;
    private Button toTownButton;
    private Button farmDevelopmentButton;
    private Button downtownDevelopmentButton;
    private Button marketDevelopmentButton;
    private Button fortressDevelopmentButton;
    private Button[] recruitingButtons = new Button[3];
    private int recruitingSlot = 0;
    private Town town;
    private Squad squad;
}
