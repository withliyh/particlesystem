package com.nd.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.nd.particlesystem.ParticleView;
import com.nd.particlesystem.core.BaseSimulate;
import com.nd.particlesystem.core.SizeRotAlphaSimulator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout rootContainer = findViewById(R.id.fl_root_container);

        final ParticleView particleView = new ParticleView(this);
        rootContainer.addView(particleView);



        rootContainer.post(new Runnable() {
            @Override
            public void run() {
                fireConfig(particleView);
            }
        });
    }

    private void fireConfig (ParticleView particleView) {
        SizeRotAlphaSimulator.SizeRotAlphaConfig config;
        config = new SizeRotAlphaSimulator.SizeRotAlphaConfig();
        config.macParticleCount = 1000;
        config.duration = 10; // sec
        config.rate = 100; // count/sec
        config.life = new BaseSimulate.Var(5, 0); //sec
        config.size = new BaseSimulate.Range(new BaseSimulate.Var(10, 0), new BaseSimulate.Var(10, 0));
        config.R = new BaseSimulate.Range(new BaseSimulate.Var(1, 0), new BaseSimulate.Var(1, 0));
        config.G = new BaseSimulate.Range(new BaseSimulate.Var(0, 0), new BaseSimulate.Var(0, 0));
        config.B = new BaseSimulate.Range(new BaseSimulate.Var(0, 0), new BaseSimulate.Var(0, 0));
        config.A = new BaseSimulate.Range(new BaseSimulate.Var(1, 0), new BaseSimulate.Var(1, 0));
        config.speed = new BaseSimulate.Var(300, 0);
        config.angle = new BaseSimulate.Var(0, 90);
        config.x = new BaseSimulate.Var(500, 200);
        config.y = new BaseSimulate.Var(500, 200);

        particleView.setConfig(config);
        particleView.start();
    }
}
