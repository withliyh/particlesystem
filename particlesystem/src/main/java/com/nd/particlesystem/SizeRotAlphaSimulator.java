package com.nd.particlesystem;

public class SizeRotAlphaSimulator extends BaseSimulate {



    static class SizeRotAlphaConfig extends BaseConfig {

    }

    private Channel_v4 mColorDelta;
    private Channel_f32 mSizeDelta;
    private Channel_f32 mRotationDelta;
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
        pool.addChannel(Pool.RotationDelta);
        pool.addChannel(Pool.Velocity);
        pool.addChannel(Pool.MaxLife);
    }

    @Override
    public void onInitField(Pool pool) {
        mColorDelta = new Channel_v4(pool.field(Pool.ColorDelta));
        mSizeDelta = new Channel_f32(pool.field(Pool.SizeDelta));
        mRotationDelta = new Channel_f32(pool.field(Pool.RotationDelta));
        mVelocity = new Channel_v2(pool.field(Pool.Velocity));
        mMaxLife = new Channel_f32(pool.field(Pool.MaxLife));
    }

    @Override
    public void onEmitting(int n) {

    }

    @Override
    public void onSimulate(float dt) {
        int n = mLifeController.mLifeCount;
        mVisualController.mPositionChannel.integrate(n, mVelocity, dt);
        mVisualController.mColorChannel.integrate(n, mColorDelta, dt);
        mVisualController.mSizeChannel.integrate(n, mSizeDelta, dt);
    }
}
