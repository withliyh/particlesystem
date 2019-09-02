package com.nd.particlesystem.core;


public abstract class BaseSimulate {


    public static class Var {
        public float base;
        public float var;

        public Var(float base, float var) {
            this.base = base;
            this.var = var;
        }

        public float random() {
            if (var == 0) {
                return base;
            }
            return base + var * (float) Math.random();
        }
    }

    public static class Range {
        public Var start;
        public Var end;

        public Range(Var start, Var end) {
            this.start = start;
            this.end = end;
        }

        public static class Delta {
            public float begin;
            public float delta;

            public Delta(float begin, float delta) {
                this.begin = begin;
                this.delta = delta;
            }
        }

        public Delta rangeInit(float invLife) {
            float begin = start.random();
            float d = 0;
            if (start != end) {
                d = (end.random() - begin) * invLife;
            }
            return new Delta(begin, d);
        }
    }

    static class BaseConfig {
        public int macParticleCount;
        public float duration;
        public float rate;
        public Var life;
        public Var x, y;
        public Range size;
        public Range R, G, B, A;
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

    public int visualize(VisualController.Box[] boxes) {
        mVisualController.visualize(boxes, mLifeController.mLifeCount);
        return mLifeController.mLifeCount;
    }

    public void start() {
        mRateController.start();
    }

    public void stop() {
        mRateController.stop();
    }

    static class LifeController {
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
            mLifeChannel.sub(mLifeCount, dt);
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

    static class RateController {
        private float mAccTime;
        private float mThreshTime;

        private float mLifeTime;
        private float mDuration;
        private boolean mStop;

        public RateController(float duration, float rate/* count per sec */) {
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

    public static class VisualController {

        public static class Vertex {
            public float x;
            public float y;
            public int rgba;

            public Vertex() {
            }

            public Vertex(float x, float y, int rgba) {
                this.x = x;
                this.y = y;
                this.rgba = rgba;
            }

            public void set(float x, float y, int rgba) {
                this.x = x;
                this.y = y;
                this.rgba = rgba;
            }
        }

        public static class Box {
            public Vertex lb;
            public Vertex rb;
            public Vertex rt;
            public Vertex lt;

            public Box() {
                this.lb = new Vertex();
                this.rb = new Vertex();
                this.rt = new Vertex();
                this.lt = new Vertex();
            }
        }

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

        public static float clamp(float val, float min, float max) {
            return Math.max(min, Math.min(max, val));
        }

        public void visualize(Box[] buf, int lifeCount) {
            for (int i = 0; i < lifeCount; i++) {

                Channel_v4.V4 color = mColorChannel.get(i);
                float r = clamp(color.x, 0, 1);
                float g = clamp(color.y, 0, 1);
                float b = clamp(color.z, 0, 1);
                float a = clamp(color.w, 0, 1);

                boolean premul = false;
                int rgba;
                if (premul) {
                    rgba = (int) (r * 255) << 16 + (int) (g * 255) << 8 + (int) (b * 255);
                } else {
                    rgba = (int) (r * 255) << 24 + (int) (g * 255) << 16 + (int) (b * 255) << 8 + (int) (a * 255);
                }

                Channel_v2.V2 pos = mPositionChannel.get(i);
                float x = pos.x;
                float y = pos.y;

                float size = mSizeChannel.get(i);
                float half = size / 2.0f;

                buf[i].lb.set(x - half, y + half, rgba);
                buf[i].rb.set(x + half, y + half, rgba);
                buf[i].rt.set(x + half, y - half, rgba);
                buf[i].lt.set(x - half, y - half, rgba);
            }
        }
    }
}
