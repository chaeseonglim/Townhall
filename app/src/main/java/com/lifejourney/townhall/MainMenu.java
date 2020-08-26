package com.lifejourney.townhall;

import android.graphics.Color;
import android.text.Layout;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.ResourceManager;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Sprite;
import com.lifejourney.engine2d.TextSprite;
import com.lifejourney.engine2d.World;

public class MainMenu extends World
        implements GameMap.Event, Button.Event, SettingBox.Event, MainGame.Event,
                   MissionSelectionBox.Event {

    static final String LOG_TAG = "MainMenu";

    MainMenu() {
        super();

        // Set audio configuration
        Engine2D engine2D = Engine2D.GetInstance();
        boolean musicEnabled = engine2D.loadPreference(engine2D.getString(R.string.music_enable), 1) == 1;
        boolean soundEffectEnabled = engine2D.loadPreference(engine2D.getString(R.string.sound_effect_enable), 1) == 1;
        engine2D.enableMusic(musicEnabled);
        engine2D.enableSoundEffect(soundEffectEnabled);

        ResourceManager resourceManager = engine2D.getResourceManager();

        // Play BGM
        resourceManager.addMusic(R.raw.town_theme);
        engine2D.playMusic(MUSIC_VOLUME);

        // Load sound effect
        resourceManager.loadSoundEffect("click3", R.raw.click3);
        resourceManager.loadSoundEffect("click5", R.raw.click5);
        resourceManager.loadSoundEffect("switch33", R.raw.switch33);
        resourceManager.loadSoundEffect("levelup", R.raw.rise01);
        resourceManager.loadSoundEffect("news", R.raw.rise02);
        resourceManager.loadSoundEffect("arrow", R.raw.metal_small2);
        resourceManager.loadSoundEffect("coin1", R.raw.coin1);
        resourceManager.loadSoundEffect("heal", R.raw.flame);
        resourceManager.loadSoundEffect("sword1", R.raw.sword_unsheathe1);
        resourceManager.loadSoundEffect("sword2", R.raw.sword_unsheathe2);
        resourceManager.loadSoundEffect("sword3", R.raw.sword_unsheathe3);
        resourceManager.loadSoundEffect("hit1", R.raw.hit1);
        resourceManager.loadSoundEffect("hit2", R.raw.hit2);
        resourceManager.loadSoundEffect("hit3", R.raw.hit3);
        resourceManager.loadSoundEffect("hit4", R.raw.hit4);
        resourceManager.loadSoundEffect("hit5", R.raw.hit5);
        resourceManager.loadSoundEffect("die1", R.raw.die1);
        resourceManager.loadSoundEffect("trot", R.raw.trot);
        resourceManager.loadSoundEffect("villager", R.raw.ready);
        resourceManager.loadSoundEffect("raiders", R.raw.ogre3);
        resourceManager.loadSoundEffect("viking", R.raw.ogre1);
        resourceManager.loadSoundEffect("rebel", R.raw.shade3);
        resourceManager.loadSoundEffect("move", R.raw.war_go_go_go);

        // Init menu
        startMenu();
    }

    private void startMenu() {
        // Set FPS
        setUpdateFPS(30.0f);

        // Build map
        sampleMap = new GameMap(this, "map/map_mainmenu.png", true);
        sampleMap.show();
        setView(sampleMap);

        // Show buttons
        Rect viewport = Engine2D.GetInstance().getViewport();

        // Logo
        String logoText = "마을 대전략";
        logo = new TextSprite.Builder("logo", logoText, 120)
                .fontColor(Color.rgb(255, 255, 0))
                .fontName("neodgm.ttf")
                .bgColor(Color.argb(0, 0, 0, 0))
                .shadow(Color.rgb(0, 0, 0), 7.0f)
                .horizontalAlign(Layout.Alignment.ALIGN_CENTER)
                .verticalAlign(Layout.Alignment.ALIGN_CENTER)
                .size(new SizeF(600, 220))
                .position(new PointF(viewport.centerX(), viewport.height * 2 / 5))
                .smooth(true).depth(0.1f)
                .layer(20).visible(true).build();

        // Buttons
        startButton = new Button.Builder(this,
                new Rect((viewport.width - 300) / 2,  viewport.height - 200, 302, 64))
                .imageSpriteAsset("main_menu_btn.png").numImageSpriteSet(1).layer(20)
                .message("게임 시작").fontSize(29).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(235, 235, 235), 1.0f)
                .build();
        startButton.setImageSpriteSet(0);
        startButton.show();
        addWidget(startButton);

        settingButton = new Button.Builder(this,
                new Rect((viewport.width - 300) / 2,  viewport.height - 120, 302, 64))
                .imageSpriteAsset("main_menu_btn.png").numImageSpriteSet(1).layer(20)
                .message("설정").fontSize(29).fontColor(Color.rgb(0, 0, 0))
                .fontName("neodgm.ttf")
                .shadow(Color.rgb(235, 235, 235), 1.0f)
                .build();
        settingButton.setImageSpriteSet(0);
        settingButton.show();
        addWidget(settingButton);

    }

    /**
     *
     */
    @Override
    public void close() {
        super.close();

        if (sampleMap != null) {
            sampleMap.close();
            sampleMap = null;
            setView(null);
        }

        // Unload sound
        Engine2D.GetInstance().stopMusic();
        Engine2D.GetInstance().getResourceManager().unloadSoundEffects();
    }

    /**
     *
     */
    @Override
    public void update() {
        super.update();

        if (game != null) {
            game.update();
        }
    }

    /**
     *
     */
    @Override
    public void commit() {
        super.commit();

        if (logo != null) {
            logo.commit();
        }

        if (game != null) {
            game.commit();
        }
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game != null) {
            return game.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    /**
     *
     */
    @Override
    public void pauseForBackground() {
        Engine2D.GetInstance().stopMusic();
        super.pauseForBackground();

        if (game != null) {
            game.pauseForBackground();
        }
    }

    /**
     *
     */
    @Override
    public void resumeFromBackground() {
        Engine2D.GetInstance().playMusic(MUSIC_VOLUME);
        super.resumeFromBackground();

        if (game != null) {
            game.resumeFromBackground();
        }
    }

    /**
     *
     * @param territory
     */
    @Override
    public void onMapTerritoryFocused(Territory territory) {

    }

    /**
     *
     * @param territory
     * @param prevFaction
     */
    @Override
    public void onMapTerritoryOccupied(Territory territory, Tribe.Faction prevFaction) {

    }

    /**
     *
     * @param button
     */
    @Override
    public void onButtonPressed(Button button) {
        if (button == startButton) {    // Start button
            popupMissionSelectBox();
        } else if (button == settingButton) {   // Setting button
            popupSettingBox();
        }
    }

    /**
     *
     * @param settingBox
     */
    @Override
    public void onSettingBoxClosed(SettingBox settingBox) {
        settingBox.close();
        removeWidget(settingBox);

        logo.show();
        startButton.show();
        settingButton.show();
    }

    /**
     *
     * @param settingBox
     */
    @Override
    public void onSettingBoxExitPressed(SettingBox settingBox) {
        System.exit(0);
    }

    /**
     *
     * @param missionSelectionBox
     */
    @Override
    public void onMissionSelectionBoxCanceled(MissionSelectionBox missionSelectionBox) {
        missionSelectionBox.close();
        removeWidget(missionSelectionBox);

        logo.show();
        startButton.show();
        settingButton.show();
    }

    /**
     *
     * @param missionSelectionBox
     * @param mission
     */
    @Override
    public void onMissionSelectionBoxStart(MissionSelectionBox missionSelectionBox, Mission mission) {
        missionSelectionBox.close();
        removeWidget(missionSelectionBox);

        startButton.close();
        removeWidget(startButton);
        startButton = null;

        settingButton.close();
        removeWidget(settingButton);
        settingButton = null;

        logo.close();
        logo = null;

        if (sampleMap != null) {
            sampleMap.close();
            sampleMap = null;
            setView(null);
        }

        game = new MainGame(this, mission);
    }

    /**
     *
     */
    @Override
    public void onGameFinished(MainGame game, int starRating) {
        game.close();
        this.game = null;

        if (game.getMission().getStarRating() < starRating) {
            game.getMission().setStarRating(starRating);
        }

        nextMission = game.getMission();
        if (starRating > 0 && nextMission.ordinal() + 1 < Mission.values().length) {
            nextMission = Mission.values()[nextMission.ordinal() + 1];
        }

        startMenu();
        popupMissionSelectBox();
    }

    /**
     *
     */
    private void popupSettingBox() {
        logo.hide();
        startButton.hide();
        settingButton.hide();

        SettingBox settingBox = new SettingBox(this,30, 0.0f);
        settingBox.show();
        addWidget(settingBox);
    }

    /**
     *
     */
    private void popupMissionSelectBox() {
        logo.hide();
        startButton.hide();
        settingButton.hide();

        MissionSelectionBox missionSelectionBox =
                new MissionSelectionBox(this, nextMission);
        missionSelectionBox.show();
        addWidget(missionSelectionBox);
    }

    private final static float MUSIC_VOLUME = 0.3f;

    private MainGame game;
    private Mission nextMission = null;
    private GameMap sampleMap;
    private Sprite logo;
    private Button startButton;
    private Button settingButton;
}
