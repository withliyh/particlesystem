package com.nd.particlesystem.core;

public class SizeRotAlphaSimulator extends BaseSimulate {



    public static class SizeRotAlphaConfig extends BaseConfig {
        public Var speed;
        public Var angle;
    }

    private Channel_v4 mColorDelta;
    private Channel_f32 mSizeDelta;
    private Channel_v2 mVelocity;
    private Channel_f32 mMaxLife;

    private SizeRotAlphaConfig mSizeRotAlphaConfig;

    public SizeRotAlphaSimulator(SizeRotAlphaConfig config) {
        super(config);
        mSizeRotAlphaConfig = config;
    }

    @Override
    public void onAddField(Pool pool) {
        pool.addChannel(Pool.ColorDelta);
        pool.addChannel(Pool.SizeDelta);
        pool.addChannel(Pool.Rotation);
        pool.addChannel(Pool.RotationDelta);
        pool.addChannel(Pool.Velocity);
        pool.addChannel(Pool.MaxLife);
    }

    @Override
    public void onInitField(Pool pool) {
        mColorDelta = new Channel_v4(pool.field(Pool.ColorDelta));
        mSizeDelta = new Channel_f32(pool.field(Pool.SizeDelta));
        mVelocity = new Channel_v2(pool.field(Pool.Velocity));
        mMaxLife = new Channel_f32(pool.field(Pool.MaxLife));
    }

    @Override
    public void onEmitting(int n) {
        if (n == 0) {
            return;
        }
        float random = (float)Math.random();

        int start = mLifeController.mLifeCount - n;
        int end = mLifeController.mLifeCount;

        for (int i = start; i < end; i++) {
            float life = mSizeRotAlphaConfig.life.base + mSizeRotAlphaConfig.life.var * random;
            float invLife = 1.0f / life;
            mLifeController.mLifeChannel.set(i, life);

            float x = mSizeRotAlphaConfig.x.random();
            float y = mSizeRotAlphaConfig.y.random();
            mVisualController.mPositionChannel.set(i, x, y);

            Range.Delta red = mSizeRotAlphaConfig.R.rangeInit(invLife);
            Range.Delta green = mSizeRotAlphaConfig.G.rangeInit(invLife);
            Range.Delta blue = mSizeRotAlphaConfig.B.rangeInit(invLife);
            Range.Delta alpha = mSizeRotAlphaConfig.A.rangeInit(invLife);
            mVisualController.mColorChannel.set(i, red.begin, green.begin, blue.begin, alpha.begin);
            mColorDelta.set(i, red.delta, green.delta, blue.delta, alpha.delta);

            Range.Delta size = mSizeRotAlphaConfig.size.rangeInit(invLife);
            mVisualController.mSizeChannel.set(i, size.begin);
            mSizeDelta.set(i, size.delta);

            float angle = mSizeRotAlphaConfig.angle.random();
            float speed = mSizeRotAlphaConfig.speed.random();
            float radians = (float) (Math.PI / 180.f * angle);
            mVelocity.set(i, (float)Math.cos(radians) * speed, (float)Math.sin(radians) * speed);
        }
    }

    @Override
    public void onSimulate(float dt) {
        int n = mLifeController.mLifeCount;
        mVisualController.mPositionChannel.integrate(n, mVelocity, dt);
        mVisualController.mColorChannel.integrate(n, mColorDelta, dt);
        mVisualController.mSizeChannel.integrate(n, mSizeDelta, dt);
    }
}
