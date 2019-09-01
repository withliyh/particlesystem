package com.nd.particlesystem;

public abstract class BaseSimulate {


    static class Var {
        public float base;
        public float var;
    }

    static class Range {
        public Var start;
        public Var end;
    }

    static class BaseConfig {
        public int macParticleCount;
        public float duration;
        public float rate;
        public Var life;
        public Var x, y;
        public Range R,G,B,A;
    }

    public abstract void onAddField(Pool pool);
    public abstract void onInitField(Pool pool);
    public abstract void onEmitting(int n);
    public abstract void onSimulate(float dt);

    protected Pool mPool;
    protected LifeController mLifeController;
    protected VisualController mVisualController;
    protected RateController mRateController;

    public BaseSimulate(BaseConfig config) {
        mPool = new Pool(config.macParticleCount);
        mLifeController = new LifeController(mPool, config.macParticleCount);
        mVisualController = new VisualController(mPool);
        mRateController = new RateController(config.duration, config.rate);
    }
    public void initialize() {
        onAddField(mPool);
        mPool.initialize();
        onInitField(mPool);

        mLifeController.initialize();
        mVisualController.initialize();
    }

    public void simulate(float dt) {
        int newParticle = mRateController.rate(dt);
        if (mLifeController.addParticle(newParticle)) {
            onEmitting(newParticle);
        }
        mLifeController.sub(dt);
        onSimulate(dt);
        mLifeController.gc();
    }

    class LifeController {
        protected Pool mPool;
        protected Channel_f32 mLifeChannel;
        protected int mLifeCount;
        protected int mMaxLifeCount;

        public LifeController(Pool pool, int maxLifeCount) {
            mPool = pool;
            mMaxLifeCount = maxLifeCount;
            pool.addChannel(Pool.Life);
        }

        public void initialize() {
            mLifeCount = 0;
            Pool.Block block = mPool.field(Pool.Life);
            mLifeChannel = new Channel_f32(block);
        }

        public boolean addParticle(int n) {
            if (n + mLifeCount < mMaxLifeCount) {
                mLifeCount += n;
                return true;
            }
            return false;
        }

        void sub(float dt) {
            mLifeChannel.sub(mLifeCount ,dt);
        }

        int gc() {
            int i = 0;
            int j = mLifeCount - 1;
            while (i <= j) {
                if (mLifeChannel.get(i) <= 0) {
                    mPool.swap(i, j);
                    j--;
                } else {
                    i++;
                }
            }
            int dead = mLifeCount - i;
            mLifeCount = i;

            return dead;
        }
    }

    class RateController {
        private float mAccTime;
        private float mThreshTime;

        private float mLifeTime;
        private float mDuration;
        private boolean mStop;

        public RateController(float duration, float rate) {
            mAccTime = 0;
            mLifeTime = 0;
            mStop = false;

            mDuration = duration;
            if (rate == 0) {
                mThreshTime = 0;
            } else {
                mThreshTime = 1.0f / rate;
            }
        }

        public int rate(float dt) {
            mLifeTime += dt;

            if (mStop || mLifeTime > mDuration || mThreshTime == 0) {
                return 0;
            }

            int n = 0;
            mAccTime += dt;

            while (mAccTime > mThreshTime) {
                mAccTime -= mThreshTime;
                n++;
            }

            return n;
        }

        public void start() {
            mStop = false;
            mLifeTime = 0;
        }

        public void stop() {
            mStop = true;
        }
    }

    class VisualController {
        protected Pool mPool;
        protected Channel_v2 mPositionChannel;
        protected Channel_v4 mColorChannel;
        protected Channel_f32 mSizeChannel;
        protected Channel_f32 mRotationChannel;

        public VisualController(Pool pool) {
            mPool = pool;
            pool.addChannel(Pool.Position);
            pool.addChannel(Pool.Color);
            pool.addChannel(Pool.Size);
            pool.addChannel(Pool.Rotation);
        }

        public void initialize() {
            Pool.Block pos = mPool.field(Pool.Position);
            Pool.Block color = mPool.field(Pool.Color);
            Pool.Block size = mPool.field(Pool.Size);
            Pool.Block rotation = mPool.field(Pool.Rotation);

            mPositionChannel = new Channel_v2(pos);
            mColorChannel = new Channel_v4(color);
            mSizeChannel = new Channel_f32(size);
            mRotationChannel = new Channel_f32(rotation);
        }

        public void visualize() {

        }
    }
}
