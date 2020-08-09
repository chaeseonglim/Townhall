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

public class InfoBox extends Widget implements Button.Event, MessageBox.Event,
        UnitSelectionBox.Event {

    private final String LOG_TAG = "InfoBox";

    public interface Event {

        void onInfoBoxSwitchToTown(InfoBox infoBox);

        void onInfoBoxClosed(InfoBox infoBox);
    }

    public InfoBox(Event eventHandler, Rect region, int layer, float depth, Territory territory) {

        super(region, layer, depth);

        this.eventHandler = eventHandler;
        this.territory = territory;

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
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(closeButton);

        updateTownInfo();

    }

    public InfoBox(Event eventHandler, Villager villager, Rect region, int layer, float depth, Squad squad) {

        super(region, layer, depth);

        this.eventHandler = eventHandler;
        this.villager = villager;
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
                .fontSize(25).layer(layer+1).fontColor(Color.rgb(230, 230, 230))
                .build();
        addWidget(closeButton);

        // Territory button
        Rect toTownButtonRegion = new Rect(region.right() - 310, region.bottom() - 65,
                150, 60);
        toTownButton = new Button.Builder(this, toTownButtonRegion)
                .message("마을로").imageSpriteAsset("")
                .fontSize(25).layer(layer + 1).fontColor(Color.rgb(230, 230, 230))
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

        addSprite(new TextSprite.Builder("text", text, 24)
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
            eventHandler.onInfoBoxClosed(this);
        } else if (button == toTownButton) {
            // To territory button
            setVisible(false);
            eventHandler.onInfoBoxSwitchToTown(this);
        } else if (button == farmDevelopmentButton) {
            // Farm development button
            Territory.DevelopmentPolicy development = territory.getDevelopmentPolicy(Territory.Facility.FARM);
            Territory.DevelopmentPolicy newDevelopment =
                    Territory.DevelopmentPolicy.values()[
                            (development.ordinal()+1)% Territory.DevelopmentPolicy.values().length];
            territory.setDevelopmentPolicy(Territory.Facility.FARM, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal());
        } else if (button == marketDevelopmentButton) {
            // Market development button
            Territory.DevelopmentPolicy development = territory.getDevelopmentPolicy(Territory.Facility.MARKET);
            Territory.DevelopmentPolicy newDevelopment =
                    Territory.DevelopmentPolicy.values()[
                            (development.ordinal()+1)% Territory.DevelopmentPolicy.values().length];
            territory.setDevelopmentPolicy(Territory.Facility.MARKET, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal() + 3);
        } else if (button == downtownDevelopmentButton) {
            // Downtown development button
            Territory.DevelopmentPolicy development = territory.getDevelopmentPolicy(Territory.Facility.DOWNTOWN);
            Territory.DevelopmentPolicy newDevelopment =
                    Territory.DevelopmentPolicy.values()[
                            (development.ordinal()+1)% Territory.DevelopmentPolicy.values().length];
            territory.setDevelopmentPolicy(Territory.Facility.DOWNTOWN, newDevelopment);
            button.setImageSpriteSet(newDevelopment.ordinal() + 6);
        } else if (button == fortressDevelopmentButton) {
            // Fortress development button
            Territory.DevelopmentPolicy development = territory.getDevelopmentPolicy(Territory.Facility.FORTRESS);
            Territory.DevelopmentPolicy newDevelopment =
                    Territory.DevelopmentPolicy.values()[
                            (development.ordinal()+1)% Territory.DevelopmentPolicy.values().length];
            territory.setDevelopmentPolicy(Territory.Facility.FORTRESS, newDevelopment);
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

            if (squad.getUnit(recruitingSlot) != null) {
                Rect viewport = Engine2D.GetInstance().getViewport();
                recruitingReplacementConfirmBox = new MessageBox.Builder(this, MessageBox.Type.YES_OR_NO,
                        new Rect((viewport.width - 353) / 2, (viewport.height - 275) / 2,
                                353, 275), "기존 병력이 교체되며 \n비용이 발생합니다.\n\n진행하시겠습니까?")
                        .fontSize(25.0f).layer(50).textColor(Color.rgb(230, 230, 230))
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
     * @param replacementUnitClass
     */
    private void popupUnitSelectionBox(Unit.UnitClass replacementUnitClass) {

        Rect unitSelectBoxRegion = getRegion().clone();
        unitSelectBoxRegion.y -= 10;
        unitSelectBoxRegion.height += 20;
        UnitSelectionBox unitSelectionBox = new UnitSelectionBox(this, villager,
                replacementUnitClass, unitSelectBoxRegion, getLayer() + 10, 0.0f);
        addWidget(unitSelectionBox);
        unitSelectionBox.show();
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
    private void updateTownInfo() {

        Rect region = getRegion();
        int layer = getLayer();

        removeSprites("text");

        // Tile type
        PointF textPosition = new PointF(-250, -155);
        addText("타일", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(territory.getTerrain().word(), new SizeF(150, 40), textPosition.clone(),
                Color.rgb(230, 230, 230));

        textPosition.offset(150, -30);
        addText("소속", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(territory.getFaction().toGameString(), new SizeF(150, 40),
                textPosition.clone(), Color.rgb(230, 230, 230));

        // Status
        textPosition.offset(-150, 30);
        addText("상태", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        String status = "-";
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

        if (territory.getTerrain().facilitySlots() == 0) {
            textPosition.offset(0, 30);
            addText("개발 불가", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
        } else {
            int facilityCount = 0;
            if (territory.getFacilityLevel(Territory.Facility.FARM) > 0) {
                facilityCount++;
                textPosition.offset(0, 30);
                addText("농장 Lv." + territory.getFacilityLevel(Territory.Facility.FARM),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
            if (territory.getFacilityLevel(Territory.Facility.MARKET) > 0) {
                facilityCount++;
                textPosition.offset(0, 30);
                addText("시장 Lv." + territory.getFacilityLevel(Territory.Facility.MARKET),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
            if (territory.getFacilityLevel(Territory.Facility.DOWNTOWN) > 0) {
                facilityCount++;
                textPosition.offset(0, 30);
                addText("마을 Lv." + territory.getFacilityLevel(Territory.Facility.DOWNTOWN),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
            if (territory.getFacilityLevel(Territory.Facility.FORTRESS) > 0) {
                facilityCount++;
                textPosition.offset(0, 30);
                addText("요새 Lv." + territory.getFacilityLevel(Territory.Facility.FORTRESS),
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
            if (facilityCount == 0) {
                textPosition.offset(0, 30);
                addText("없음",
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }

            // Development buttons
            if (territory.getFaction() == Tribe.Faction.VILLAGER) {
                textPosition.offset(0, 30);
                addText("개발 방향",
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(255, 255, 0));

                if (farmDevelopmentButton != null) {
                    farmDevelopmentButton.close();
                    removeWidget(farmDevelopmentButton);
                }
                Rect developmentButtonRegion =
                        new Rect(region.left() + 22, region.top() + (int)textPosition.y + 219,
                                64, 64);
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
                developmentButtonRegion.offset(73, 0);
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
                developmentButtonRegion.offset(73, 0);
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
                developmentButtonRegion.offset(73, 0);
                fortressDevelopmentButton =
                        new Button.Builder(this, developmentButtonRegion.clone())
                                .imageSpriteAsset("facility_development_btn.png").numImageSpriteSet(12)
                                .fontSize(25).layer(layer + 1).fontColor(Color.rgb(255, 255, 0))
                                .build();
                fortressDevelopmentButton.setImageSpriteSet(
                        territory.getDevelopmentPolicy(Territory.Facility.FORTRESS).ordinal() + 9);
                addWidget(fortressDevelopmentButton);
            }
        }

        if (territory.getFaction() == Tribe.Faction.VILLAGER) {
            // Population
            textPosition.setTo(100, -155);
            addText("인구",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText((territory.getPopulation()==0)?"-": territory.getPopulation() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Income
            textPosition.offset(150, -30);
            addText("수입",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText((territory.getTax()==0)?"-": territory.getTax() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Happiness
            textPosition.offset(-150, 30);
            addText("행복도",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText(territory.getHappiness() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Defense
            textPosition.offset(150, -30);
            addText("방어도",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText((territory.getDelta(Territory.DeltaAttribute.DEFENSIVE)==0)?
                            "-" : territory.getDelta(Territory.DeltaAttribute.DEFENSIVE) + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            // Bonus
            textPosition.offset(-150, 30);
            addText("보너스",
                    new SizeF(150, 40), textPosition.clone(),
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
                addText("개발 속도(일꾼)",
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            } else {
                addText("-",
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
        } else {
            // Bonus
            textPosition.setTo(100, -155);
            addText("방어도",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));
            textPosition.offset(0, 30);
            addText((territory.getDelta(Territory.DeltaAttribute.DEFENSIVE)==0)?
                            "-" : territory.getDelta(Territory.DeltaAttribute.DEFENSIVE) + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

        }
    }

    private void updateSquadInfo() {

        Rect region = getRegion();
        int layer = getLayer();

        removeSprites("text");

        PointF textPosition = new PointF(-250, -155);
        addText("소속", new SizeF(150, 40), textPosition.clone(),
                Color.rgb(255, 255, 0));

        textPosition.offset(0, 30);
        addText(squad.getFaction().toGameString() + " 부대", new SizeF(150, 40),
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
            textPosition.offset(0, 50);
            addText("유닛이 없습니다.\n하단 모집 버튼을 누르세요.", new SizeF(300, 80), textPosition.clone(),
                    Color.rgb(230, 230, 230));
            textPosition.offset(0, 8);
        } else {
            for (Unit unit : squad.getUnits()) {
                textPosition.offset(0, 30);
                String unitStr = unit.getUnitClass().word() + " Lv." + unit.getLevel();
                if (unit.isRecruiting()) {
                    unitStr += " (모집중)";
                }
                addText(unitStr, new SizeF(300, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
        }

        if (squad.getFaction() == Tribe.Faction.VILLAGER) {
            // Recruiting
            textPosition.offset(-75, 30);
            addText("모집", new SizeF(150, 40), textPosition.clone(),
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
                        recruitingButtons[i].close();
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
                        Color.rgb(230, 230, 230));
            }
        }

        // Stats
        if (squad.getFaction() == Tribe.Faction.VILLAGER) {
            textPosition.setTo(100, -155);
            addText("인구", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));

            textPosition.offset(0, 30);
            addText((squad.getPopulation() == 0) ? "-" : squad.getPopulation() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));

            textPosition.offset(150, -30);
            addText("유지비", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));

            textPosition.offset(0, 30);
            addText((squad.getUpkeep() == 0) ? "-" : squad.getUpkeep() + "",
                    new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(230, 230, 230));
        }

        if (squad.getUnits().size() > 0) {
            if (squad.getFaction() == Tribe.Faction.VILLAGER) {
                textPosition.offset(-150, 30);
            } else {
                textPosition.setTo(100, -155);
            }
            addText("보너스", new SizeF(150, 40), textPosition.clone(),
                    Color.rgb(255, 255, 0));

            textPosition.offset(0, 30);
            int offensiveBonus = squad.getOffensiveBonusFromTerritory();
            int defensiveBonus = squad.getDefensiveBonusFromTerritory();
            if (offensiveBonus != 0 || defensiveBonus != 0) {
                addText("공격력 " + ((offensiveBonus > 0) ? "+" : "") + offensiveBonus,
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));

                textPosition.offset(150, 0);
                addText("방어도 " + ((defensiveBonus > 0) ? "+" : "") + defensiveBonus,
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            } else {
                addText("-", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }

            textPosition.offset(0, 30);
            int shrineBonusAttackSpeed = squad.getShrineBonus(Tribe.ShrineBonus.UNIT_ATTACK_SPEED);
            int shrineBonusHealPower = squad.getShrineBonus(Tribe.ShrineBonus.UNIT_HEAL_POWER);
            if (shrineBonusAttackSpeed != 0 || shrineBonusHealPower != 0) {
                addText("공격 속도 " + ((offensiveBonus > 0) ? "+" : "") + shrineBonusAttackSpeed,
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));

                textPosition.offset(150, 0);
                addText("치유량 " + ((defensiveBonus > 0) ? "+" : "") + shrineBonusHealPower,
                        new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            } else {
                addText("-", new SizeF(150, 40), textPosition.clone(),
                        Color.rgb(230, 230, 230));
            }
        }
    }

    private Event eventHandler;
    private Villager villager;
    private Territory territory;
    private Squad squad;
    private Button closeButton;
    private Button toTownButton;
    private Button farmDevelopmentButton;
    private Button downtownDevelopmentButton;
    private Button marketDevelopmentButton;
    private Button fortressDevelopmentButton;
    private Button[] recruitingButtons = new Button[3];
    private MessageBox recruitingReplacementConfirmBox;
    private int recruitingSlot = 0;
}
