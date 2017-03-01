package io.intheloup.demo_modern;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.intheloup.demo_modern.lib.ModernCameraView;

public class MainActivity extends AppCompatActivity {

    ModernCameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (ModernCameraView) findViewById(R.id.modern);
    }

    @Override
    public void onBackPressed() {
        if (cameraView.onBackPressed()) {
            return;
        }

        super.onBackPressed();
    }
}
