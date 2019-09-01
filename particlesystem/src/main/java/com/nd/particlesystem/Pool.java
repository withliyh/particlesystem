package com.nd.particlesystem;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Pool {

    public enum ChanType {
        ChanF32,
        ChanV2,
        ChanV4,
        ChanV8,
    }

    static class ChanFiled {

        public ChanType type;
        public String name;

        public ChanFiled(ChanType type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    public final static ChanFiled Life = new ChanFiled(ChanType.ChanF32, "Life");
    public final static ChanFiled MaxLife = new ChanFiled(ChanType.ChanF32, "MaxLife");

    public final static ChanFiled Size = new ChanFiled(ChanType.ChanF32, "Size");
    public final static ChanFiled SizeDelta = new ChanFiled(ChanType.ChanF32, "SizeDelta");
    public final static ChanFiled SizeRange = new ChanFiled(ChanType.ChanV2, "SizeRange");

    public final static ChanFiled Color = new ChanFiled(ChanType.ChanV4, "Color");
    public final static ChanFiled ColorDelta = new ChanFiled(ChanType.ChanV4, "ColorDelta");
    public final static ChanFiled ColorRange = new ChanFiled(ChanType.ChanV8, "ColorRange");

    public final static ChanFiled Position = new ChanFiled(ChanType.ChanV2, "Position");
    public final static ChanFiled PositionStart = new ChanFiled(ChanType.ChanV2, "PositionStart");
    public final static ChanFiled Velocity = new ChanFiled(ChanType.ChanV2, "Velocity");

    public final static ChanFiled Speed = new ChanFiled(ChanType.ChanF32, "Speed");
    public final static ChanFiled Direction = new ChanFiled(ChanType.ChanV2, "Direction");
    public final static ChanFiled RadialAcc = new ChanFiled(ChanType.ChanF32, "RadialAcc");
    public final static ChanFiled TangentialAcc = new ChanFiled(ChanType.ChanF32, "TangentialAcc");

    public final static ChanFiled Rotation = new ChanFiled(ChanType.ChanF32, "Rotation");
    public final static ChanFiled RotationDelta = new ChanFiled(ChanType.ChanF32, "RotationDelta");

    public final static ChanFiled Angle = new ChanFiled(ChanType.ChanF32, "Angle");
    public final static ChanFiled AngleDelta = new ChanFiled(ChanType.ChanF32, "AngleDelta");

    public final static ChanFiled Radius = new ChanFiled(ChanType.ChanF32, "Raidus");
    public final static ChanFiled RadiusDelta = new ChanFiled(ChanType.ChanF32, "RadiusDelta");

    class Block {
        public ChanFiled chanFiled;
        ByteBuffer data;
        int stride;
        int cap;
    }

    private ArrayList<Block> mBlocks;

    private ByteBuffer mByteBuffer;

    private int mCap;
    private int mTotalByteCount;

    public Pool(int cap) {
        mCap = cap;
        mTotalByteCount = 0;
        mBlocks = new ArrayList<>(20);
    }

    public void addChannel(ChanFiled chanFiled) {
        Block block = new Block();
        block.chanFiled = chanFiled;
        block.stride = getChanTypeSize(chanFiled.type);
        block.cap = mCap;

        mBlocks.add(block);
        mTotalByteCount += (block.cap * block.stride);
    }

    public Block field(ChanFiled chanFiled) {
        int chanCount = mBlocks.size();
        for (int i = 0; i < chanCount; i++) {
            Block block = mBlocks.get(i);
            if (block.chanFiled == chanFiled) {
                return block;
            }
        }
        return null;
    }

    public void initialize() {
        mByteBuffer = ByteBuffer.allocate(mTotalByteCount);
        byte[] internelArray = mByteBuffer.array();
        int offset = 0;
        int channelCount = mBlocks.size();
        for (int i = 0; i < channelCount; i++) {
            Block block = mBlocks.get(i);
            int length = block.cap * block.stride;
            block.data = ByteBuffer.wrap(internelArray, offset, length);
            offset += length;
        }
    }

    public void swap(int dst, int src) {
        if (src == dst) {
            return;
        }
        int channelCount = mBlocks.size();
        for (int i = 0; i < channelCount; i++) {
            Block block = mBlocks.get(i);
            int dstOffset = dst * block.stride;
            int srcOffset = src * block.stride;
            byte[] memory = block.data.array();
            System.arraycopy(memory, srcOffset, memory, dstOffset, block.stride);
        }
    }

    private int getChanTypeSize(ChanType type) {
        switch (type) {
            case ChanF32:
                return 4;
            case ChanV2:
                return 8;
            case ChanV4:
                return 16;
            case ChanV8:
                return 32;
        }

        return 0;
    }
}
