package com.lifejourney.townhall;

import android.graphics.Color;
import android.view.MotionEvent;

import com.lifejourney.engine2d.Engine2D;
import com.lifejourney.engine2d.PointF;
import com.lifejourney.engine2d.Rect;
import com.lifejourney.engine2d.RectF;
import com.lifejourney.engine2d.Rectangle;
import com.lifejourney.engine2d.SizeF;
import com.lifejourney.engine2d.Widget;

public class TutorialGuide extends Widget implements MessageBox.Event {

    private final static String LOG_TAG = "TutorialGuide";

    public TutorialGuide(MainGame game) {
        super(new Rect(), 30, 0);

        this.game = game;

        Rect viewport = Engine2D.GetInstance().getViewport();

        guideRectangle = new Rectangle.Builder(new RectF(Engine2D.GetInstance().fromWidgetToGame(
                new PointF(20, 10)), new SizeF(440, 64)))
                .color(Color.argb(255, 255, 255, 255)).lineWidth(10.0f)
                .layer(255).visible(true).build();
        guideRectangle.commit();

        tutorialBox = new MessageBox.Builder(this, MessageBox.Type.TOUCH,
                new Rect(viewport.width - 353 - 20, viewport.height - 275 - 100,
                        353, 275),
                "안녕하세요! 촌장님\n" +
                "저는 당신의 조언가입니다.\n\n" +
                "지금부터 마을을 운영하는\n" +
                "방법을 알려드리고자 합니다.\n\n" +
                "터치하세요.")
                .fontSize(25.0f).layer(50).textColor(Color.rgb(255, 255, 255))
                .bgAsset("tutorial_box_bg.png").bgOpaque(0.8f)
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

    private MainGame game;
    private MessageBox tutorialBox;
    private Rectangle guideRectangle = null;
}
