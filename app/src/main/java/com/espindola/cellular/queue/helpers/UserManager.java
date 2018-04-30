package com.espindola.cellular.queue.helpers;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.espindola.cellular.queue.tasks.UserTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserManager {

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();


    // A queue of UserTasks.
    private final BlockingQueue<Runnable> mUserTaskWorkQueue;
    // A managed pool of background threads for user calls
    private final ThreadPoolExecutor mUserThreadPool;

    // An object that manages messages in a Thread
    private Handler mHandler;

    // A single instance of UserManager, used to implement singleton pattern
    private static UserManager sInstance = null;

    static {
        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        sInstance = new UserManager();
    }

    /*
    * Constructs the work queues
    * Thread Pool: Service Matrix
    * Work block: CALL
    * Thread: SLOT
    * */

    private UserManager(){
        /*
         * Creates a work queue for the set of of task objects that control user calls,
         * using a linked list queue that blocks when the queue is empty.
         */
        mUserTaskWorkQueue = new LinkedBlockingDeque<Runnable>();
        /*
         * Creates a new pool of Thread objects for the user work queue
         */
        mUserThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mUserTaskWorkQueue);

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    static public void start(){
        UserTask userTask = (UserTask) sInstance.mUserTaskWorkQueue.poll();
        if (userTask!=null)
            sInstance.mUserThreadPool.execute(userTask);
    }
}
