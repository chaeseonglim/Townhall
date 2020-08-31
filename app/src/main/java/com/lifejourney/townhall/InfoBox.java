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

public class InfoBox extends Widget implements Button.Event, MessageBox.Event,
        UnitSelectionBox.Event {

    private final String LOG_TAG = "InfoBox";

    public interface Event {

        void onInfoBoxSwitchToTown(InfoBox infoBox);

        void onInfoBoxClosed(InfoBox infoBox);
    }

    public InfoBox(Event eventHandler, Mission mission, Territory territory) {
        super(null, 30, 0.0f);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 702) / 2, (viewport.height - 402) / 2,
                702, 402);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;
        this.territory = territory;
        this.mission = mission;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("info_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(getLayer()).depth(getDepth())
                .gridSize(1, 1).visible(false).opaque(1.0f).build();
        addSprite(backgroundSprite);

        // Close button
        Rect closeButtonRegion = new Rect(getRegion().right() - 156, getRegion().bottom() - 81,
                138, 64);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message("닫기").imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 2.0f)
                .layer(getLayer() + 1).build();
        addWidget(closeButton);

        updateTerritoryInfo();

    }

    public InfoBox(Event eventHandler, Mission mission, Villager villager, Squad squad) {
        super(null, 30, 0.0f);

        Rect viewport = Engine2D.GetInstance().getViewport();
        Rect boxRegion = new Rect((viewport.width - 702) / 2, (viewport.height - 402) / 2,
                702, 402);
        setRegion(boxRegion);

        this.eventHandler = eventHandler;
        this.villager = villager;
        this.squad = squad;
        this.mission = mission;

        // Background sprite
        Sprite backgroundSprite = new Sprite.Builder("info_box.png")
                .size(new SizeF(getRegion().size()))
                .smooth(false).layer(getLayer()).depth(getDepth())
                .gridSize(1, 1).visible(false).opaque(1.0f).build();
        addSprite(backgroundSprite);

        // Close button
        Rect closeButtonRegion = new Rect(getRegion().right() - 156, getRegion().bottom() - 81,
                138, 64);
        closeButton = new Button.Builder(this, closeButtonRegion)
                .message("닫기").imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(getLayer() + 1).build();
        addWidget(closeButton);

        // Territory button
        Rect toTownButtonRegion = new Rect(getRegion().right() - 300, getRegion().bottom() - 81,
                138, 64);
        toTerritoryButton = new Button.Builder(this, toTownButtonRegion)
                .message("지역").imageSpriteAsset("messagebox_btn_bg.png")
                .fontSize(25).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .fontShadow(Color.rgb(235, 235, 235), 1.0f)
                .layer(getLayer() + 1).build();
        addWidget(toTerritoryButton);


        updateSquadInfo();
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
        if (button == closeButton) { // Close button
            setVisible(false);
            eventHandler.onInfoBoxClosed(this);
        } else if (button == toTerritoryButton) { // To territory button
            setVisible(false);
            eventHandler.onInfoBoxSwitchToTown(this);
        } else if (button == farmDevelopmentButton) { // Farm development button
            Territory.DevelopmentPolicy development = territory.getDevelopmentPolicy(Territory.Facility.FARM);
            Territory.DevelopmentPolicy newDevelopment =
                    Territory.DevelopmentPolicy.values()[
                            (development.ordinal()+1)% Territory.DevelopmentPolicy.values().length];
            territory.setDevelopmentPolicy(Territory.Facility.FARM, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal());
        } else if (button == marketDevelopmentButton) { // Market development button
            Territory.DevelopmentPolicy development = territory.getDevelopmentPolicy(Territory.Facility.MARKET);
            Territory.DevelopmentPolicy newDevelopment =
                    Territory.DevelopmentPolicy.values()[
                            (development.ordinal()+1)% Territory.DevelopmentPolicy.values().length];
            territory.setDevelopmentPolicy(Territory.Facility.MARKET, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal() + 3);
        } else if (button == downtownDevelopmentButton) { // Downtown development button
            Territory.DevelopmentPolicy development = territory.getDevelopmentPolicy(Territory.Facility.DOWNTOWN);
            Territory.DevelopmentPolicy newDevelopment =
                    Territory.DevelopmentPolicy.values()[
                            (development.ordinal()+1)% Territory.DevelopmentPolicy.values().length];
            territory.setDevelopmentPolicy(Territory.Facility.DOWNTOWN, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal() + 6);
        } else if (button == fortressDevelopmentButton) { // Fortress development button
            Territory.DevelopmentPolicy development = territory.getDevelopmentPolicy(Territory.Facility.FORTRESS);
            Territory.DevelopmentPolicy newDevelopment =
                    Territory.DevelopmentPolicy.values()[
                            (development.ordinal()+1)% Territory.DevelopmentPolicy.values().length];
            territory.setDevelopmentPolicy(Territory.Facility.FORTRESS, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal() + 9);
        } else if (button == unitButtons[0] || button == unitButtons[1] || button == unitButtons[2]) {  // Unit buttons
            for (int i = 0;; ++i) {
                if (button == unitButtons[i]) {
                    recruitingSlot = i;
                    break;
                }
            }

            if (squad.getUnit(recruitingSlot) != null) {
                Rect viewport = Engine2D.GetInstance().getViewport();
                recruitingReplacementConfirmBox = new MessageBox.Builder(this, MessageBox.Type.YES_OR_NO,
                        new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                                353, 275), "기존 병력이 교체되며 \n비용이 발생합니다.\n\n진행하시겠습니까?")
                        .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 230, 230))
                        .fontName("neodgm.ttf")
                        .build();
                recruitingReplacementConfirmBox.show();
                addWidget(recruitingReplacementConfirmBox);
            } else {
                hide();
                popupUnitSelectionBox(null);
            }
        }
    }

    /**
     *
     * @param messageBox
     * @param buttonType
     */
    @Override
    public void onMessageBoxButtonPressed(MessageBox messageBox, MessageBox.ButtonType buttonType) {
        if (buttonType == MessageBox.ButtonType.YES) {
            hide();

            Unit.UnitClass replacementUnitClass = null;
            if (squad.getUnit(recruitingSlot) != null) {
                replacementUnitClass = squad.getUnit(recruitingSlot).getUnitClass();
            }
            popupUnitSelectionBox(replacementUnitClass);
        }

        messageBox.close();
        removeWidget(messageBox);
        recruitingReplacementConfirmBox = null;
    }

    /**
     *
     * @param infoBox
     * @param unitClass
     */
    @Override
    public void onUnitBuilderBoxSelected(UnitSelectionBox infoBox, Unit.UnitClass unitClass) {
        infoBox.close();
        removeWidget(infoBox);
        show();

        if (unitClass != null) {
            // Remove unit if there's a unit in target slot
            Unit unitToRemove = squad.getUnit(recruitingSlot);
            if (unitToRemove != null) {
                squad.removeUnit(recruitingSlot);
                villager.pay(0, -unitClass.population());
            }

            // Add unit
            villager.pay(unitClass.costToPurchase(), unitClass.population());
            squad.spawnUnit(unitClass);
            updateSquadInfo();
        }
    }

    /**
     *
     */
    private void updateTerritoryInfo() {
        Rect region = getRegion();
        int layer = getLayer();

        removeSprites("text");
        removeSprites("icon");

        // Tile type
        PointF textPosition = new PointF(-238, -148);
        addText("지형", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(territory.getTerrain().word(), new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        textPosition.offset(150, -30);
        addText("소속", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        if (territory.getFogState() != Territory.FogState.CLEAR) {
            textPosition.offset(0, 30);
            addText("모름", new SizeF(150, 40),
                    textPosition.clone(), Color.rgb(230, 230, 230));
        } else {
            textPosition.offset(0, 30);
            addText(territory.getFaction().toGameString(), new SizeF(150, 40),
                    textPosition.clone(), Color.rgb(230, 230, 230));
        }

        // Status
        textPosition.offset(-150, 30);
        addText("상태", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        String status = "없음";
        if (territory.getBattle() != null) {
            status = "전투중";
        } else if (territory.isOccupying()) {
            status = "점령중";
        } else if (territory.getFaction() == Tribe.Faction.VILLAGER &&
            territory.getTotalFacilityLevel() < 5 &&
            territory.getTerrain().facilitySlots() > 0) {
            status = "개발중";
        }
        textPosition.offset(0, 30);
        addText(status, new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        // Facility
        textPosition.offset(0, 30);
        addText("시설", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        if (territory.getTerrain().facilitySlots() == 0 ||
                territory.getFaction() != Tribe.Faction.VILLAGER) {
            textPosition.offset(0, 30);
            addText("없음", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
        } else { // Development buttons status
            if (farmDevelopmentButton != null) {
                farmDevelopmentButton.close();
                removeWidget(farmDevelopmentButton);
            }
            Rect developmentButtonRegion =
                    new Rect(region.left() + 36, region.top() + (int)textPosition.y + 218,
                            61, 65);
            farmDevelopmentButton =
                    new Button.Builder(this,  developmentButtonRegion.clone())
                            .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                            .fontSize(25).layer(layer + 1).fontColor(Color.rgb(255, 255, 0))
                            .build();
            farmDevelopmentButton.setImageSpriteSet(
                    territory.getDevelopmentPolicy(Territory.Facility.FARM).ordinal());
            addWidget(farmDevelopmentButton);

            if (marketDevelopmentButton != null) {
                marketDevelopmentButton.close();
                removeWidget(marketDevelopmentButton);
            }
            developmentButtonRegion.offset(71, 0);
            marketDevelopmentButton =
                    new Button.Builder(this, developmentButtonRegion.clone())
                            .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                            .fontSize(25).layer(layer + 1).fontColor(Color.rgb(255, 255, 0))
                            .build();
            marketDevelopmentButton.setImageSpriteSet(
                    territory.getDevelopmentPolicy(Territory.Facility.MARKET).ordinal() + 3);
            addWidget(marketDevelopmentButton);

            if (downtownDevelopmentButton != null) {
                downtownDevelopmentButton.close();
                removeWidget(downtownDevelopmentButton);
            }
            developmentButtonRegion.offset(71, 0);
            downtownDevelopmentButton =
                    new Button.Builder(this, developmentButtonRegion.clone())
                            .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                            .fontSize(25).layer(layer + 1).fontColor(Color.rgb(255, 255, 0))
                            .build();
            downtownDevelopmentButton.setImageSpriteSet(
                    territory.getDevelopmentPolicy(Territory.Facility.DOWNTOWN).ordinal() + 6);
            addWidget(downtownDevelopmentButton);

            if (fortressDevelopmentButton != null) {
                fortressDevelopmentButton.close();
                removeWidget(fortressDevelopmentButton);
            }
            developmentButtonRegion.offset(71, 0);
            fortressDevelopmentButton =
                    new Button.Builder(this, developmentButtonRegion.clone())
                            .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                            .fontSize(25).layer(layer + 1).fontColor(Color.rgb(255, 255, 0))
                            .build();
            fortressDevelopmentButton.setImageSpriteSet(
                    territory.getDevelopmentPolicy(Territory.Facility.FORTRESS).ordinal() + 9);
            addWidget(fortressDevelopmentButton);

            textPosition.offset(-46, 100);
            addText("농장", new SizeF(100, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230), Layout.Alignment.ALIGN_CENTER);
            textPosition.offset(71, 0);
            addText("시장", new SizeF(100, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230), Layout.Alignment.ALIGN_CENTER);
            textPosition.offset(71, 0);
            addText("마을", new SizeF(100, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230), Layout.Alignment.ALIGN_CENTER);
            textPosition.offset(71, 0);
            addText("요새", new SizeF(100, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230), Layout.Alignment.ALIGN_CENTER);

            textPosition.offset(-71*3+1, 30);
            addText("Lv." + territory.getFacilityLevel(Territory.Facility.FARM),
                    new SizeF(100, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230), Layout.Alignment.ALIGN_CENTER);
            textPosition.offset(71, 0);
            addText("Lv." + territory.getFacilityLevel(Territory.Facility.MARKET),
                    new SizeF(100, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230), Layout.Alignment.ALIGN_CENTER);
            textPosition.offset(71, 0);
            addText("Lv." + territory.getFacilityLevel(Territory.Facility.DOWNTOWN),
                    new SizeF(100, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230), Layout.Alignment.ALIGN_CENTER);
            textPosition.offset(71, 0);
            addText("Lv." + territory.getFacilityLevel(Territory.Facility.FORTRESS),
                    new SizeF(100, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230), Layout.Alignment.ALIGN_CENTER);
        }

        if (territory.getFaction() == Tribe.Faction.VILLAGER) {
            // Population
            textPosition.setTo(111, -148);
            addText("인구", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("people.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(100, 0);
            addText(territory.getPopulation() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Income
            textPosition.offset(150 - 30, -30);
            addText("수입", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(100, 0);
            addText(territory.getTax() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Happiness
            textPosition.offset(-190, 30);
            addText("행복도", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            if (territory.getHappiness() > 80) {
                addIcon("very_happy.png", new SizeF(25, 25), textPosition.clone());
            } else if (territory.getHappiness() > 60) {
                addIcon("happy.png", new SizeF(25, 25), textPosition.clone());
            } else if (territory.getHappiness() > 40) {
                addIcon("soso.png", new SizeF(25, 25), textPosition.clone());
            } else if (territory.getHappiness() > 20) {
                addIcon("bad.png", new SizeF(25, 25), textPosition.clone());
            } else {
                addIcon("very_bad.png", new SizeF(25, 25), textPosition.clone());
            }
            textPosition.offset(100, 0);
            addText(territory.getHappiness() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Defense
            textPosition.offset(120, -30);
            addText("방어도", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("armor.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(100, 0);
            addText(territory.getDelta(Territory.DeltaAttribute.DEFENSIVE) + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Bonus
            textPosition.offset(-190, 30);
            addText("보너스", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            boolean isSquadWorking = false;
            for (Squad squad: territory.getSquads()) {
                if (squad.isWorking()) {
                    isSquadWorking = true;
                    break;
                }
            }
            if (isSquadWorking) {
                addText("일꾼 개발",
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            } else {
                addText("없음",
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
        } else {
            textPosition.setTo(111, -148);
            addText("지역 방어도",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("armor.png", new SizeF(25, 25), textPosition.clone());
            textPosition.offset(100, 0);
            addText((territory.getDelta(Territory.DeltaAttribute.DEFENSIVE)==0)?
                            "-" : territory.getDelta(Territory.DeltaAttribute.DEFENSIVE) + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
        }
    }

    /**
     *
     */
    private void updateSquadInfo() {
        Rect region = getRegion();
        int layer = getLayer();

        removeSprites("text");
        removeSprites("icon");

        PointF textPosition = new PointF(-238, -148);
        addText("소속", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(squad.getFaction().toGameString(), new SizeF(150, 40),
                textPosition.clone(), Color.rgb(230, 230, 230));

        // Status
        textPosition.offset(150, -30);
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
                Color.rgb(230, 230, 230));

        // Unit information
        textPosition.offset(-150, 30);
        addText("유닛", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(75, 0);
        if (squad.getUnits().isEmpty()) {
            textPosition.offset(0, 55);
            addText("유닛이 없습니다.\n하단 버튼을 눌러 모집해주세요.", new SizeF(300, 80), textPosition.clone(),
                    Color.rgb(230, 230, 230));
            textPosition.offset(0, 25);
        } else {
            for (Unit unit : squad.getUnits()) {
                textPosition.offset(0, 30);
                String unitInfoStr = unit.getUnitClass().word() + " Lv." + unit.getLevel();
                if (unit.isRecruiting()) {
                    unitInfoStr += " (모집중)";
                }
                addText(unitInfoStr, new SizeF(300, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
        }

        if (squad.getFaction() == Tribe.Faction.VILLAGER) { // Unit buttons
            Rect unitButtonRegion =
                    new Rect(region.left() + 35, region.top() + (int) textPosition.y + 220,
                            62, 66);
            for (int i = 0; i < 3; ++i) {
                if (unitButtons[i] != null) {
                    unitButtons[i].close();
                    removeWidget(unitButtons[i]);
                }
                if (squad.getUnits().size() > i) {
                    unitButtons[i] =
                            new Button.Builder(this, unitButtonRegion.clone())
                                    .imageSpriteAsset("unit_recruiting_btn.png")
                                    .numImageSpriteSet(Unit.UnitClass.values().length + 1)
                                    .layer(layer + 1).build();
                    unitButtons[i].setImageSpriteSet(squad.getUnits().get(i).getUnitClass().ordinal() + 1);
                    addWidget(unitButtons[i]);
                } else if (squad.getUnits().size() == i) {
                    unitButtons[i] =
                            new Button.Builder(this, unitButtonRegion.clone())
                                    .imageSpriteAsset("unit_recruiting_btn.png")
                                    .numImageSpriteSet(Unit.UnitClass.values().length + 1)
                                    .layer(layer + 1).build();
                    unitButtons[i].setImageSpriteSet(0);
                    if (squad.isMoving() || squad.isSupporting() || squad.isOccupying() ||
                            squad.isFighting()) {
                        unitButtons[i].disable();
                    }
                    addWidget(unitButtons[i]);
                } else {
                    unitButtons[i] =
                            new Button.Builder(this, unitButtonRegion.clone())
                                    .imageSpriteAsset("unit_recruiting_btn.png")
                                    .numImageSpriteSet(Unit.UnitClass.values().length + 1)
                                    .layer(layer + 1).build();
                    unitButtons[i].setImageSpriteSet(0);
                    unitButtons[i].disable();
                    addWidget(unitButtons[i]);
                }

                unitButtonRegion.offset(73, 0);
            }
        }

        // Stats
        if (squad.getFaction() == Tribe.Faction.VILLAGER) {
            textPosition.setTo(111, -148);
            addText("소모 인구", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("people.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(100, 0);
            addText((squad.getPopulation() == 0) ? "-" : squad.getPopulation() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            textPosition.offset(110, -30);
            addText("유지비", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(-65, 30);
            addIcon("gold.png", new SizeF(30, 30), textPosition.clone());
            textPosition.offset(100, 0);
            addText((squad.getUpkeep() == 0) ? "-" : squad.getUpkeep() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
        }

        if (squad.getUnits().size() > 0) {
            if (squad.getFaction() == Tribe.Faction.VILLAGER) {
                textPosition.offset(-180, 30);
            } else {
                textPosition.setTo(111, -148);
            }
            int attackDamageBonus = squad.getAttackDamageBonus();
            int attackSpeedBonus = squad.getAttackSpeedBonus();
            int armorBonus = squad.getArmorBonus();
            int healPowerBonus = squad.getHealPowerBonus();

            if (attackDamageBonus != 0 || attackSpeedBonus != 0 || armorBonus != 0 || healPowerBonus != 0) {
                addText("추가 효과", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));
            }

            textPosition.offset(30, 0);

            if (attackDamageBonus != 0) {
                textPosition.offset(-95, 30);
                addIcon("attack.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(100, 0);
                addText("공격력 " + ((attackDamageBonus > 0) ? "+" : "") + attackDamageBonus,
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
                textPosition.offset(-5, 0);
            }

            if (attackSpeedBonus != 0) {
                textPosition.offset(-95, 30);
                addIcon("wind.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(100, 0);
                addText("공격 속도 " + ((attackSpeedBonus < 0) ? "+" : "") + (-attackSpeedBonus),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
                textPosition.offset(-5, 0);
            }

            if (armorBonus != 0) {
                textPosition.offset(-95, 30);
                addIcon("armor.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(100, 0);
                addText("방어도 " + ((armorBonus>0)?"+":"") + armorBonus,
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
                textPosition.offset(-5, 0);
            }

            if (healPowerBonus != 0) {
                textPosition.offset(-95, 30);
                addIcon("heal.png", new SizeF(25, 25), textPosition.clone());
                textPosition.offset(100, 0);
                addText("치유량 " + ((healPowerBonus>0)?"+":"") + healPowerBonus,
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
                textPosition.offset(-5, 0);
            }
        }
    }

    /**
     *
     * @param text
     * @param size
     * @param position
     * @param fontColor
     */
    private void addText(String text, SizeF size, PointF position, int fontColor) {
        addSprite(new TextSprite.Builder("text", text, 23)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(61, 61, 61), 2.0f)
                .horizontalAlign(Layout.Alignment.ALIGN_NORMAL)
                .verticalAlign(Layout.Alignment.ALIGN_CENTER)
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
    private void addText(String text, SizeF size, PointF position, int fontColor, Layout.Alignment alignment) {
        addSprite(new TextSprite.Builder("text", text, 23)
                .fontColor(fontColor)
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(61, 61, 61), 2.0f)
                .horizontalAlign(alignment)
                .verticalAlign(Layout.Alignment.ALIGN_CENTER)
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
    private void addIcon(String asset, SizeF size, PointF position) {
        addSprite(new Sprite.Builder("icon", asset)
                .size(size).positionOffset(position)
                .smooth(false).depth(0.1f)
                .layer(getLayer()+1).visible(false).build());
    }

    /**
     *
     * @param replacementUnitClass
     */
    public void popupUnitSelectionBox(Unit.UnitClass replacementUnitClass) {
        Rect unitSelectBoxRegion = getRegion().clone();
        unitSelectBoxRegion.y -= 10;
        unitSelectBoxRegion.height += 20;
        unitSelectionBox = new UnitSelectionBox(this, mission, villager,
                replacementUnitClass, unitSelectBoxRegion);
        addWidget(unitSelectionBox);
        unitSelectionBox.show();
    }

    /**
     *
     */
    public void closeUnitSelectionBox() {
        unitSelectionBox.close();
        removeWidget(unitSelectionBox);
    }

    private Event eventHandler;
    private Villager villager;
    private Mission mission;
    private Territory territory;
    private Squad squad;
    private Button closeButton;
    private Button toTerritoryButton;
    private Button farmDevelopmentButton;
    private Button downtownDevelopmentButton;
    private Button marketDevelopmentButton;
    private Button fortressDevelopmentButton;
    private Button[] unitButtons = new Button[3];
    private MessageBox recruitingReplacementConfirmBox;
    private UnitSelectionBox unitSelectionBox;
    private int recruitingSlot = 0;
}
