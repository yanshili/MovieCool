package com.coolcool.moviecool.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtils {
    private static ThreadUtils instance;
    private ThreadPoolExecutor mExecutor;
    private LinkedBlockingQueue<Runnable> workQueue;

    private volatile boolean execute=true;

    private ThreadUtils(){
        workQueue=new LinkedBlockingQueue<>(180);
        mExecutor=new ThreadPoolExecutor(15,30,10, TimeUnit.SECONDS,workQueue);
    }

    private static synchronized void init(){
        if (instance==null){
            instance=new ThreadUtils();
        }
    }

    public static ThreadUtils getInstance(){
        if (instance==null){
            init();
        }
        return instance;
    }

    public void execute(Runnable runnable){
        if (runnable!=null)
        mExecutor.execute(runnable);
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public ThreadPoolExecutor getExecutor() {
        return mExecutor;
    }
}
