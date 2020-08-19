package com.lifejourney.townhall;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.World;

import java.util.Locale;

public class Townhall extends FragmentActivity
        implements Choreographer.FrameCallback, SurfaceHolder.Callback {

    private static final long ONE_MS_IN_NS = 1000000;
    private static final long ONE_S_IN_NS = 1000 * ONE_MS_IN_NS;

    private static final String LOG_TAG = "Townhall";

    /**
     *
     */
    protected void initEngine() {
        // Get display metrics
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        float refreshRateHz = display.getRefreshRate();
        Log.i(LOG_TAG, String.format("Refresh rate: %.1f Hz", refreshRateHz));

        // Initialize the surfaceView
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);

        Log.i(LOG_TAG, "Engine initialized: " + Engine2D.GetInstance().isInitialized());

        // Initialize Engine
        Engine2D.GetInstance().initEngine(this);

        // Set resolution of Engine
        Point size = new Point();
        display.getSize(size);
        float ratio = 1280.0f / size.x;
        Engine2D.GetInstance().setViewport(new Rect(0, 0, 1280, (int)(size.y*ratio)));

        // Set background color
        Engine2D.GetInstance().setBackgroundColor(Color.rgb(50, 50, 50));
    }

    /**
     *
     */
    protected void finalizeEngine() {
        // Finalize Engine
        Engine2D.GetInstance().finalizeEngine();
        Log.i(LOG_TAG, "Engine finalized: " + Engine2D.GetInstance().isInitialized());
    }

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onCreate");

        super.onCreate(savedInstanceState);

        // Initialize view
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initialize display & engine
        initEngine();
    }

    @Override
    protected void onDestroy() {

        Log.i(LOG_TAG, "onDestroy");

        super.onDestroy();

        world.close();

        finalizeEngine();
    }

    /**
     *
     */
    protected void onEngine2DPrepared() {

        // Initialize resources
        initResources();
    }

    /**
     *
     */
    @Override
    protected void onStart() {

        Log.i(LOG_TAG, "onStart");

        super.onStart();

        isRunning = true;
        Engine2D.GetInstance().start();
        Choreographer.getInstance().postFrameCallback(this);
    }

    /**
     *
     */
    @Override
    protected void onStop() {

        Log.i(LOG_TAG, "onStop");

        super.onStop();

        isRunning = false;
        Engine2D.GetInstance().stop();
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "onPause");

        super.onPause();

        if (world != null) {
            world.onPause();
        }
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "onResume");

        super.onResume();

        if (world != null) {
            world.onResume();
        }
    }

    /**
     *
     * @param frameTimeNanos
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        Trace.beginSection("doFrame");

        if (fpsMarkerCount++%120 == 0) {
            Log.i(LOG_TAG, String.format(Locale.US, "FPS: %.1f", Engine2D.GetInstance().getAverageFps()));
        }

        if (isRunning) {
            if (surfacePrepared) {
                // Update world
                world.update();
                world.commit();
            }

            Trace.beginSection("Requesting callback");
            Choreographer.getInstance().postFrameCallback(this);
            Trace.endSection();
        }

        Trace.endSection();
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return world.onTouchEvent(event);
    }

    /**
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Do nothing here, waiting for surfaceChanged instead
    }

    /**
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Surface surface = holder.getSurface();
        Engine2D.GetInstance().setSurface(surface, width, height);

        if (!surfacePrepared) {
            Log.i(LOG_TAG, "surfaceChanged");

            onEngine2DPrepared();
            surfacePrepared = true;
        }
    }

    /**
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Engine2D.GetInstance().clearSurface();
    }

    /**
     *
     */
    protected void initResources() {

        world = new MainMenu();
    }

    private World world = null;
    private boolean isRunning;
    private boolean surfacePrepared = false;
    private int fpsMarkerCount = 0;
}
