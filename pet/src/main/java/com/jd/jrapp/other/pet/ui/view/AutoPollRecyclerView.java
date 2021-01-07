package com.jd.jrapp.other.pet.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Author: chenghuan15
 * Date: 2020/12/31
 * Time: 3:47 PM
 */
public class AutoPollRecyclerView extends RecyclerView {
    private static final long TIME_AUTO_POLL = 3000;
    AutoPollTask autoPollTask;
    private boolean running; //标示是否正在自动轮询
    private boolean canRun;//标示是否可以自动轮询,可在不需要的是否置false
    private AutoTaskLister autoTaskCallback;

    public AutoPollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        autoPollTask = new AutoPollTask(this);
    }

    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<AutoPollRecyclerView>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {
//                recyclerView.scrollBy(2, 2);
                if (recyclerView.autoTaskCallback != null) {
                    recyclerView.autoTaskCallback.scrollCallback();
                }
                recyclerView.postDelayed(recyclerView.autoPollTask, recyclerView.TIME_AUTO_POLL);
            }
        }
    }

    public void setAutoTaskCallback(AutoTaskLister autoTaskCallback) {
        this.autoTaskCallback = autoTaskCallback;
    }

    //开启:如果正在运行,先停止->再开启
    public void start(boolean isDelay) {
        if (running) {
            stop();
        }
        canRun = true;
        running = true;
        postDelayed(autoPollTask, isDelay ? 2*1000 : 0);

    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (running)
                    stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (canRun)
                    start(true);
                break;
        }
        //return  false，注释掉onTouchEvent()方法里面的stop和start方法，则列表自动滚动且不可触摸
        return super.onTouchEvent(e);
    }

    public interface AutoTaskLister {
        void scrollCallback();
    }

}
