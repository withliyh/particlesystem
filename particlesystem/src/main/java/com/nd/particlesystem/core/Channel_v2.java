package com.nd.particlesystem.core;

public class Channel_v2 implements Channel {

    private float[] mData;
    private int mOffset;
    private int mCount;

    public static class V2 {
        public float x;
        public float y;

        public V2(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    //把float 数组作为二维向量
    public Channel_v2(Pool.Block block) {
        mData = block.data.array();
        mOffset = block.data.position();
        mCount = block.cap;
    }

    public void set(int index, float x, float y) {
        int offset = mOffset + index * 2;
        mData[offset] = x;
        mData[offset + 1] = y;
    }

    public V2 get(int index) {
        int offset = mOffset + index;
        V2 v2 = new V2(mData[offset], mData[offset + 1]);
        return v2;
    }


    public void setConst(int n, float x, float y) {
        for (int i = 0; i < n; i++) {
            set(i, x, y);
        }
    }

    public void add(int n, float x, float y) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i * 2;
            mData[offset] += x;
            mData[offset + 1] += y;
        }
    }

    public void sub(int n, float x, float y) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i * 2;
            mData[offset] -= x;
            mData[offset + 1] -= y;
        }
    }

    public void integrate(int n, Channel_v2 delta, float dt) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i * 2;
            V2 v2 = delta.get(i);
            mData[offset] += v2.x * dt;
            mData[offset + 1] += v2.y * dt;
        }
    }


    //ch = normal *m, normal = normal(x, y), m = magnitude
    public void radialIntegrate(int n, Channel_v2 xy, Channel_f32 radial, float dt) {
        for (int i = 0; i < n; i++) {
            float normal_x = 0, normal_y = 0;

            V2 v2 = xy.get(i);

            float invsqrt = (float) (1.0 / Math.sqrt(v2.x * v2.x + v2.y * v2.y));
            normal_x = v2.x * invsqrt;
            normal_y = v2.y * invsqrt;

            int offset = mOffset + i * 2;
            mData[offset] += radial.get(i) * normal_x * dt;
            mData[offset + 1] += radial.get(i) * normal_y * dt;
        }
    }

    public void tangentIntegrate(int n, Channel_v2 xy, Channel_f32 tangent, float dt) {
        for (int i = 0; i < n; i++) {
            float tangent_x = 0, tangent_y = 0;

            V2 v2 = xy.get(i);

            float invsqrt = (float) (1.0 / Math.sqrt(v2.x * v2.x + v2.y * v2.y));
            tangent_y = v2.x * invsqrt;
            tangent_x = v2.y * invsqrt;

            int offset = mOffset + i * 2;
            mData[offset] += tangent.get(i) * tangent_x * dt;
            mData[offset + 1] += tangent.get(i) * tangent_y * dt;
        }
    }
}
