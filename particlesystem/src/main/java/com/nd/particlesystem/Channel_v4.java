package com.nd.particlesystem;


public class Channel_v4 implements Channel {
    public float[] mData;
    public int mCount;

    //把float 数组作为四维向量
    public Channel_v4(Pool.Block block) {
        mData = block.data.asFloatBuffer().array();
        mCount = block.cap;
    }

    public void setConst(int n, float x, float y, float z, float w) {

        for (int i = 0; i <n; i++) {
            mData[i] = x;
            mData[i+1] = y;
            mData[i+3] = z;
            mData[i+4] = w;
        }
    }

    public void add(int n, float x, float y, float z, float w) {
        for (int i = 0; i <n; i++) {
            mData[i] += x;
            mData[i+1] += y;
            mData[i+3] += z;
            mData[i+4] += w;
        }
    }

    public void sub(int n, float x, float y, float z, float w) {
        for (int i = 0; i <n; i++) {
            mData[i] -= x;
            mData[i+1] -= y;
            mData[i+3] -= z;
            mData[i+4] -= w;
        }
    }

    public void integrate(int n,Channel_v4 delta, float dt) {

        for (int i = 0; i < n; i++) {
            mData[i] += delta.mData[i] * dt;
            mData[i+1] += delta.mData[i+1] * dt;
            mData[i+2] += delta.mData[i+2] * dt;
            mData[i+3] += delta.mData[i+3] * dt;
        }
    }
}
