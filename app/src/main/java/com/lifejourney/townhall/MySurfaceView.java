package com.lifejourney.townhall;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView {

    public interface Event {
        boolean onViewTouchEvent(MotionEvent event);
    }

    public MySurfaceView(Context context) {
        super(context);
    }

    public void setEventHandler(Event eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return eventHandler.onViewTouchEvent(event);
    }

    private Event eventHandler;
}
