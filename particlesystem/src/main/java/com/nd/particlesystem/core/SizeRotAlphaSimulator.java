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
    private BatchSystem mBatchSystem;

    public SizeRotAlphaSimulator(SizeRotAlphaConfig config) {
        super(config);
        mSizeRotAlphaConfig = config;
        mBatchSystem = new BatchSystem();
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
    public void onSimulate(final float dt) {
        final int n = mLifeController.mLifeCount;

        //使用3个线程执行计算任务，一个主线程，两个线程池中的线程
        BatchSystem.Batch batch = mBatchSystem.newBatch();
        batch.addTask(new BatchSystem.Task() {
            @Override
            public void onWork() {
                mVisualController.mPositionChannel.integrate(n, mVelocity, dt);
            }
        });
        batch.addTask(new BatchSystem.Task() {
            @Override
            public void onWork() {
                mVisualController.mColorChannel.integrate(n, mColorDelta, dt);
            }
        });

        batch.perform();
        mVisualController.mSizeChannel.integrate(n, mSizeDelta, dt);
        batch.complete();
    }
}
