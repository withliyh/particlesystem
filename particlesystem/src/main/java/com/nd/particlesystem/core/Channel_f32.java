package com.nd.particlesystem.core;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.nio.FloatBuffer;

public class Channel_f32 implements Channel {

    //Java 不支持数组切片，导致使用起来很麻烦
    private float[] mData;
    private int mOffset;
    private int mCount;

    public Channel_f32(@NonNull Pool.Block block) {
        mData = block.data.array();
        mOffset = block.data.position();
        mCount = block.cap;
    }

    public float get(int index) {
        int offset = mOffset + index;
        return mData[offset];
    }

    public void set(int index, float v) {
        int offset = mOffset + index;
        mData[offset] = v;
    }

    public void setConst(int n, float v) {
        for (int i = 0; i < n; i++) {
            set(i, v);
        }
    }

    public void add(int n, float v) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i;
            mData[offset] += v;
        }
    }

    public void sub(int n, float v) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i;
            mData[offset] -= v;
        }
    }

    public void mul(int n, float v) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i;
            mData[offset] *= v;
        }
    }

    public void integrate(int n, Channel_f32 delta, float dt) {
        for (int i = 0; i < n; i++) {
            int offset = mOffset + i;
            mData[offset] += delta.get(i) * dt;
        }
    }
}
