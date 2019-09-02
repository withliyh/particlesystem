package com.nd.particlesystem.core;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchSystem {

    private static ExecutorService sFixedThreadPool = Executors.newFixedThreadPool(3);

    public static abstract class Task implements Runnable {
        private CountDownLatch mLatch;
        @Override
        public void run() {
            onWork();
            mLatch.countDown();
        }

        public abstract void onWork();

        protected void setCountDownLatch(CountDownLatch latch) {
            mLatch = latch;
        }
    }

    public static class Batch {
        private CountDownLatch mCountDownLatch;
        private ArrayList<Task> mRunnables;
        public Batch() {
            mRunnables = new ArrayList<>();
        }

        public void addTask(Task runnable) {
            mRunnables.add(runnable);
        }

        public void perform() {
            int count = mRunnables.size();
            mCountDownLatch = new CountDownLatch(count);
            for (int i = 0; i < count; i++) {
                Task task = mRunnables.get(i);
                task.setCountDownLatch(mCountDownLatch);
                sFixedThreadPool.submit(task);
            }
        }

        public void complete() {
            try {
                mCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public BatchSystem() {

    }

    public Batch newBatch() {
        return new Batch();
    }

}
