package com.nd.particlesystem.core;


public class Channel_v4 implements Channel {
    private float[] mData;
    private int mOffset;
    private int mCount;

    public static class V4 {
        public float x;
        public float y;
        public float z;
        public float w;

        public V4(float x, float y, float z, float w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }
    }

    //把float 数组作为四维向量
    public Channel_v4(Pool.Block block) {
        mData = block.data.array();
        mOffset = block.data.position();
        mCount = block.cap;
    }

    public void set(int index, float x, float y, float z, float w) {
        int offset = mOffset + index * 4;
        mData[offset] = x;
        mData[offset + 1] = y;
        mData[offset + 2] = z;
        mData[offset + 3] = w;
    }

    public V4 get(int index) {
        int offset = mOffset + index * 4;
        V4 v4 = new V4(mData[offset], mData[offset + 1], mData[offset + 2], mData[offset + 3]);
        return v4;
    }

    public void setConst(int n, float x, float y, float z, float w) {
        for (int i = 0; i < n; i++) {
            set(i, x, y, z, w);
        }
    }

    public void add(int n, float x, float y, float z, float w) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i * 4;
            mData[offset] += x;
            mData[offset + 1] += y;
            mData[offset + 3] += z;
            mData[offset + 4] += w;
        }
    }

    public void sub(int n, float x, float y, float z, float w) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i * 4;
            mData[offset] -= x;
            mData[offset + 1] -= y;
            mData[offset + 3] -= z;
            mData[offset + 4] -= w;
        }
    }

    public void integrate(int n, Channel_v4 delta, float dt) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i * 4;
            V4 v4 = delta.get(i);
            mData[offset] += v4.x * dt;
            mData[offset + 1] += v4.y * dt;
            mData[offset + 2] += v4.z * dt;
            mData[offset + 3] += v4.w * dt;
        }
    }
}
