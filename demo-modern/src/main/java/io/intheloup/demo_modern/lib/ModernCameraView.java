package io.intheloup.demo_modern.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.commonsware.cwac.cam2.CameraView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 27/02/2017.
 */

public class ModernCameraView extends FrameLayout {

    private static final int PINCH_ZOOM_DELTA = 20;

    final ViewGroup previewStack = new FrameLayout(getContext());
    final CameraView cameraView = new CameraView(getContext());
    final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());

    private final Presenter presenter = new Presenter(this);

    public ModernCameraView(Context context) {
        super(context);
        init(context);
    }

    public ModernCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ModernCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        addView(previewStack, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        previewStack.addView(cameraView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.onReady();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.onDrop();
        super.onDetachedFromWindow();
    }

    LinkedList<CameraView> bindCameras(int count) {
        LinkedList<CameraView> cameraViews = new LinkedList<CameraView>();
        CameraView cv;
        for (int i = 1; i < count; i++) {
            cv = new CameraView(getContext());
            cv.setVisibility(View.INVISIBLE);
            cv.setMirror(false);
            previewStack.addView(cv);
            cameraViews.add(cv);
        }

        return cameraViews;
    }

    void bindZoom(boolean isEnabled) {
        if (isEnabled) {
            previewStack.setOnTouchListener(
                    new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return (scaleDetector.onTouchEvent(event));
                        }
                    });
        } else {
            previewStack.setOnTouchListener(null);
        }
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            int delta;

            if (scale > 1.0f) {
                delta = PINCH_ZOOM_DELTA;
            } else if (scale < 1.0f) {
                delta = -1 * PINCH_ZOOM_DELTA;
            } else {
                return;
            }

            presenter.didChangeZoom(delta);
        }
    }
}
