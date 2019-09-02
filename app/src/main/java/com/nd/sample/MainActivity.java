package com.nd.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.nd.particlesystem.ParticleView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout rootContainer = findViewById(R.id.fl_root_container);

        ParticleView particleView = new ParticleView(this);
        rootContainer.addView(particleView);
    }
}
