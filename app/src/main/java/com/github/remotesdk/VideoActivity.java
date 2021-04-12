package com.github.remotesdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

public class VideoActivity extends AppCompatActivity {
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video2);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
    }

    public SurfaceView showSurfaceView() {
        return surfaceView;
    }

}