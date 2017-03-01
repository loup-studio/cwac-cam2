package io.intheloup.demo_modern.lib;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import io.intheloup.demo_modern.R;

/**
 * Created by Lukasz - lukasz.pili@gmail.com on 01/03/2017.
 */

public class ModernCameraControlView extends FrameLayout {

    Button takePictureButton;
    ImageView pictureImageView;

    public ModernCameraControlView(Context context) {
        super(context);
        init(context);
    }

    public ModernCameraControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ModernCameraControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.view_modern_camera_control_default, this);
        takePictureButton = (Button) view.findViewById(R.id.modern_camera_control_take_picture);
        pictureImageView = (ImageView) view.findViewById(R.id.modern_camera_control_picture);
    }

    void bindPicture(Uri uri) {
        Log.d(getClass().getSimpleName(), "bindPicture: " + uri);
        pictureImageView.setImageURI(uri);
        pictureImageView.setVisibility(VISIBLE);

        takePictureButton.setVisibility(GONE);
    }

    void bindClearPicture() {
        Log.d(getClass().getSimpleName(), "bindClearPicture: ");
        pictureImageView.setImageURI(null);
        pictureImageView.setVisibility(GONE);
        takePictureButton.setVisibility(VISIBLE);
    }
}
