package com.nd.particlesystem;

public class Channel_v2 implements Channel {

    public float[] mData;
    public int mCount;

    //把float 数组作为二维向量
    public Channel_v2(Pool.Block block) {
        mData = block.data.asFloatBuffer().array();
        mCount = block.cap;
    }

    public void setConst(int n, float x, float y) {

        for (int i = 0; i <n; i++) {
            mData[i] = x;
            mData[i+1] = y;
        }
    }

    public void add(int n, float x, float y) {
        for (int i = 0; i <n; i++) {
            mData[i] += x;
            mData[i+1] += y;
        }
    }

    public void sub(int n, float x, float y) {
        for (int i = 0; i <n; i++) {
            mData[i] -= x;
            mData[i+1] -= y;
        }
    }

    public void integrate(int n, Channel_v2 delta, float dt) {
        for (int i = 0; i < n; i++) {
            mData[i] = delta.mData[i] * dt;
            mData[i+1] = delta.mData[i+1] * dt;
        }
    }


    //ch = normal *m, normal = normal(x, y), m = magnitude
    public void radialIntegrate(int n, Channel_v2 xy, Channel_f32 radial, float dt) {
        for (int i = 0; i < n; i++) {
            float normal_x = 0, normal_y = 0;

            float x = xy.mData[i];
            float y = xy.mData[i+1];

            float invsqrt = (float) (1.0 / Math.sqrt(x*x + y * y));
            normal_x = x * invsqrt;
            normal_y = y * invsqrt;

            mData[i] += radial.mData[i] * normal_x * dt;
            mData[i + 1] += radial.mData[i] * normal_y * dt;
        }
    }

    public void tangentIntegrate(int n, Channel_v2 xy, Channel_f32 tangent, float dt) {
        for (int i = 0; i < n; i++) {
            float tangent_x = 0, tangent_y = 0;

            float x = xy.mData[i];
            float y = xy.mData[i+1];

            float invsqrt = (float) (1.0 / Math.sqrt(x*x + y * y));
            tangent_y = x * invsqrt;
            tangent_x = y * invsqrt;

            mData[i] += tangent.mData[i] * tangent_x * dt;
            mData[i + 1] += tangent.mData[i] * tangent_y * dt;
        }
    }
}
