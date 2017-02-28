package io.intheloup.demo_modern.lib;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.cam2.AbstractCameraActivity;
import com.commonsware.cwac.cam2.CameraController;
import com.commonsware.cwac.cam2.CameraEngine;
import com.commonsware.cwac.cam2.CameraSelectionCriteria;
import com.commonsware.cwac.cam2.CameraView;
import com.commonsware.cwac.cam2.ErrorConstants;
import com.commonsware.cwac.cam2.Facing;
import com.commonsware.cwac.cam2.FlashMode;
import com.commonsware.cwac.cam2.FocusMode;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 27/02/2017.
 */

class Presenter {

    private final ModernCameraView view;
    private final CameraController controller;

    private boolean isVideoRecording = false;

    public Presenter(ModernCameraView view) {
        this.view = view;

        controller = new CameraController(FocusMode.CONTINUOUS, new ErrorResultReceiver(), true, false);

        CameraEngine engine = CameraEngine.buildInstance(view.getContext(), null);
        engine.setPreferredFlashModes(new ArrayList<FlashMode>());

        controller.setEngine(engine, new CameraSelectionCriteria.Builder()
                .facing(Facing.BACK)
                .build());
        controller.setQuality(1);
    }

    void onReady() {
        if (controller.getNumberOfCameras() > 0) {
            prepController();
        }

        AbstractCameraActivity.BUS.register(this);
        controller.start();
    }

    void onDrop() {
        AbstractCameraActivity.BUS.unregister(this);
        shutdown();
        controller.destroy();
    }

    void didChangeZoom(int delta) {
        controller.changeZoom(delta);
    }


    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CameraController.ControllerReadyEvent event) {
        if (event.isEventForController(controller)) {
            prepController();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CameraEngine.OpenedEvent event) {
        if (event.exception == null) {
            view.bindZoom(controller.supportsZoom());
        } else {
            controller.postError(ErrorConstants.ERROR_OPEN_CAMERA, event.exception);
            shutdown();
//            getActivity().finish();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CameraEngine.VideoTakenEvent event) {
        isVideoRecording = false;
//        stopChronometers();

        if (event.exception == null) {
//            if (getArguments().getBoolean(ARG_UPDATE_MEDIA_STORE, false)) {
//                final Context app=getActivity().getApplicationContext();
//                Uri output=getArguments().getParcelable(ARG_OUTPUT);
//                final String path=output.getPath();
//
//                new Thread() {
//                    @Override
//                    public void run() {
//                        SystemClock.sleep(2000);
//                        MediaScannerConnection.scanFile(app,
//                                new String[]{path}, new String[]{"video/mp4"},
//                                null);
//                    }
//                }.start();
//            }

            isVideoRecording = false;
//            setVideoFABToNormal();
        }
//        else if (getActivity().isFinishing()) {
//            shutdown();
//        }
        else {
            controller.postError(ErrorConstants.ERROR_VIDEO_TAKEN, event.exception);
            shutdown();
//            getActivity().finish();
        }
    }

//    @SuppressWarnings("unused")
//    @Subscribe(threadMode=ThreadMode.MAIN)
//    public void onEventMainThread(
//            CameraEngine.SmoothZoomCompletedEvent event) {
//        inSmoothPinchZoom=false;
//        zoomSlider.setEnabled(true);
//    }

    protected void performCameraAction() {
//        if (isVideo()) {
//            recordVideo();
//        }
//        else {
//            takePicture();
//        }
    }

    private void prepController() {
        Log.d(getClass().getSimpleName(), "prepController: ");
        LinkedList<CameraView> cameraViews = new LinkedList<CameraView>();
        CameraView cv = view.cameraView;
        cv.setMirror(false);
        cameraViews.add(cv);

        cameraViews.addAll(view.bindCameras(controller.getNumberOfCameras()));

        controller.setCameraViews(cameraViews);
    }

    private void shutdown() {
        if (isVideoRecording) {
            stopVideoRecording(true);
        } else {
//            progress.setVisibility(View.VISIBLE);

            if (controller != null) {
                try {
                    controller.stop();
                } catch (Exception e) {
                    controller.postError(ErrorConstants.ERROR_STOPPING, e);
                    Log.e(getClass().getSimpleName(),
                            "Exception stopping controller", e);
                }
            }
        }
    }

    private void stopVideoRecording(boolean abandon) {
//        setVideoFABToNormal();

        try {
            controller.stopVideoRecording(abandon);
        } catch (Exception e) {
            controller.postError(ErrorConstants.ERROR_STOPPING_VIDEO, e);
            Log.e(getClass().getSimpleName(),
                    "Exception stopping recording of video", e);
        } finally {
            isVideoRecording = false;
        }
    }


    @SuppressLint("ParcelCreator")
    private class ErrorResultReceiver extends ResultReceiver {
        public ErrorResultReceiver() {
            super(new Handler(Looper.getMainLooper()));
        }

        @Override
        protected void onReceiveResult(int resultCode,
                                       Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (view != null) {
                Toast.makeText(view.getContext(), "We had an error", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
