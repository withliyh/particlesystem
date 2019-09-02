package com.nd.particlesystem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.View;

import androidx.annotation.Nullable;

import com.nd.particlesystem.core.BaseSimulate;
import com.nd.particlesystem.core.SizeRotAlphaSimulator;

public class ParticleView extends View {

    private SizeRotAlphaSimulator.SizeRotAlphaConfig mConfig;
    private SizeRotAlphaSimulator mSimulator;
    private BaseSimulate.VisualController.Box []mBoxes;

    private Paint mPaint;

    private Choreographer.FrameCallback mRefreshAction;
    private float mLastFrameTime;

    public ParticleView(Context context) {
        super(context);
        init();
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setConfig(SizeRotAlphaSimulator.SizeRotAlphaConfig config) {
        mConfig = config;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mRefreshAction = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long l) {
                invalidate();
                Choreographer.getInstance().postFrameCallback(mRefreshAction);
            }
        };
    }

    public void start() {
        if (mConfig == null) {
            throw new IllegalArgumentException("没有设置粒子属性配置");
        }
        mSimulator = new SizeRotAlphaSimulator(mConfig);
        mSimulator.initialize();
        mSimulator.start();

        mBoxes = new BaseSimulate.VisualController.Box[mConfig.macParticleCount];
        for (int i = 0; i < mConfig.macParticleCount; i++) {
            mBoxes[i] = new BaseSimulate.VisualController.Box();
        }

        Choreographer.getInstance().postFrameCallback(mRefreshAction);
        mLastFrameTime = SystemClock.currentThreadTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSimulator == null) return;

        long curMillis = SystemClock.currentThreadTimeMillis();
        float dt = (curMillis - mLastFrameTime) / 1000.0f;
        mLastFrameTime = curMillis;
        //sec
        mSimulator.simulate(dt);
        int n = mSimulator.visualize(mBoxes);

        for (int i = 0; i < n; i++) {
            mPaint.setColor(0xFF00FFFF);
            canvas.drawRect(mBoxes[i].lt.x, mBoxes[i].lt.y, mBoxes[i].rb.x, mBoxes[i].rb.y, mPaint);
        }
    }
}
