package com.nd.particlesystem;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Channel_f32 implements Channel {

    public float[] mData;
    public int mCount;

    public Channel_f32(@NonNull Pool.Block block) {
        mCount = block.cap;
        mData = block.data.asFloatBuffer().array();
    }

    public float get(int i ) {
        return mData[i];
    }

    public void setConst(int n, float v) {
        for (int i = 0; i < n; i++) {
            mData[i] = v;
        }
    }

    public void add(int n, float v) {
        for (int i = 0; i < n; i++) {
            mData[i] += v;
        }
    }

    public void sub(int n, float v) {
        for (int i = 0; i < n; i++) {
            mData[i] -= v;
        }
    }

    public void mul(int n, float v) {
        for (int i = 0; i < n; i++) {
            mData[i] *= v;
        }
    }

    public void integrate(int n , Channel_f32 delta, float dt) {
        for (int i = 0; i < n; i++) {
            mData[i] += delta.mData[i] * dt;
        }
    }
}
