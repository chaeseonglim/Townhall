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

public class InfoBox extends Widget implements Button.Event, UnitBuilderBox.Event {

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
        Rect closeButtonRegion = new Rect(region.right() - 155, region.bottom() - 67,
                150, 60);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message("닫기").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 0))
                .build();
        addWidget(closeButton);

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

        if (town.getTerrain().availableEconomySlot() == 0) {
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

                developmentButtonRegion.offset(73, 0);
                marketDevelopmentButton =
                        new Button.Builder(this, developmentButtonRegion.clone())
                        .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                        .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                        .build();
                marketDevelopmentButton.setImageSpriteSet(
                        town.getFacilityDevelopment(Town.Facility.MARKET).ordinal() + 3);
                addWidget(marketDevelopmentButton);

                developmentButtonRegion.offset(73, 0);
                downtownDevelopmentButton =
                        new Button.Builder(this, developmentButtonRegion.clone())
                        .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                        .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                        .build();
                downtownDevelopmentButton.setImageSpriteSet(
                        town.getFacilityDevelopment(Town.Facility.DOWNTOWN).ordinal() + 6);
                addWidget(downtownDevelopmentButton);

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
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 0))
                .build();
        addWidget(closeButton);

        // Town button
        Rect toTownButtonRegion = new Rect(region.right() - 310, region.bottom() - 65,
                150, 60);
        toTownButton = new Button.Builder(this, toTownButtonRegion)
                .message("마을로").imageSpriteAsset("")
                .fontSize(25).layer(layer+1).textColor(Color.rgb(255, 255, 0))
                .build();
        addWidget(toTownButton);

        // Faction
        PointF textPosition = new PointF(-250, -155);
        addText("소속", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(squad.getFaction().toGameString(), new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));

        // Status
        textPosition.offset(0, 30);
        addText("상태", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        String status;
        if (squad.isMoving()) {
            status = "이동중";
        } else if (squad.isFighting()) {
            status = "전투중";
        } else if (squad.isSupporting()) {
            status = "지원중";
        } else if (squad.isOccupying()) {
            status = "점령중";
        } else {
            status = "대기중";
        }
        textPosition.offset(0, 30);
        addText(status, new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));

        // Unit information
        textPosition.offset(0, 30);
        addText("유닛", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        for (Unit unit: squad.getUnits()) {
            textPosition.offset(0, 30);
            addText(unit.getUnitClass().toGameString()+" Lv"+unit.getLevel(),
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
        }

        // Recruiting
        textPosition.offset(0, 30);
        addText("모집", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        if (!squad.isMoving() && !squad.isSupporting() && !squad.isOccupying() && !squad.isFighting()) {
            Rect recruitingButtonRegion =
                    new Rect(region.left() + 22, region.top() + (int) textPosition.y + 219,
                            64, 64);
            recruitingButton1 =
                    new Button.Builder(this, recruitingButtonRegion.clone())
                            .imageSpriteAsset("unit_recruiting_btn.png").numImageSpriteSet(3)
                            .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                            .build();
            recruitingButton1.setImageSpriteSet((squad.getUnits().size() < 1) ?
                    0 : squad.getUnits().get(0).getUnitClass().ordinal() + 1);
            addWidget(recruitingButton1);

            recruitingButtonRegion.offset(73, 0);
            recruitingButton2 =
                    new Button.Builder(this, recruitingButtonRegion.clone())
                            .imageSpriteAsset("unit_recruiting_btn.png").numImageSpriteSet(3)
                            .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                            .build();
            recruitingButton2.setImageSpriteSet((squad.getUnits().size() < 2) ?
                    0 : squad.getUnits().get(1).getUnitClass().ordinal() + 1);
            addWidget(recruitingButton2);

            recruitingButtonRegion.offset(73, 0);
            recruitingButton3 =
                    new Button.Builder(this, recruitingButtonRegion.clone())
                            .imageSpriteAsset("unit_recruiting_btn.png").numImageSpriteSet(3)
                            .fontSize(25).layer(layer + 1).textColor(Color.rgb(255, 255, 0))
                            .build();
            recruitingButton3.setImageSpriteSet((squad.getUnits().size() < 3) ?
                    0 : squad.getUnits().get(2).getUnitClass().ordinal() + 1);
            addWidget(recruitingButton3);
        } else {
            textPosition.offset(0, 30);
            addText("모집 불가", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 255));
        }

        // Stats
        textPosition.setTo(175, -155);
        addText("보너스", new SizeF(300, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(-75, 30);
        addText("-", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 255));
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
        } else if (button == recruitingButton1) {
            UnitBuilderBox unitBuilderBox =
                    new UnitBuilderBox(this, getRegion(), getLayer()+10, 0.0f);
            hide();
            unitBuilderBox.show();
            addWidget(unitBuilderBox);
        } else if (button == recruitingButton2) {
            UnitBuilderBox unitBuilderBox =
                    new UnitBuilderBox(this, getRegion(), getLayer()+10, 0.0f);
            hide();
            unitBuilderBox.show();
            addWidget(unitBuilderBox);
        } else if (button == recruitingButton3) {
            UnitBuilderBox unitBuilderBox =
                    new UnitBuilderBox(this, getRegion(), getLayer()+10, 0.0f);
            hide();
            unitBuilderBox.show();
            addWidget(unitBuilderBox);
        }
    }

    @Override
    public void onUnitBuilderBoxSelected(UnitBuilderBox infoBox, Unit.UnitClass unitClass) {
        infoBox.close();
        removeWidget(infoBox);

        show();
    }

    private Event listener;
    private Button closeButton;
    private Button toTownButton;
    private Button farmDevelopmentButton;
    private Button downtownDevelopmentButton;
    private Button marketDevelopmentButton;
    private Button fortressDevelopmentButton;
    private Button recruitingButton1;
    private Button recruitingButton2;
    private Button recruitingButton3;
    private Town town;
    private Squad squad;
}
