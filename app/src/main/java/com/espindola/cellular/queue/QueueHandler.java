package com.espindola.cellular.queue;


import android.os.Handler;
import android.os.Message;

import com.espindola.cellular.queue.listeners.UserTaskListener;
import com.espindola.cellular.queue.tasks.UserTask;

public class QueueHandler extends Handler{

    private UserTaskListener mListener;

    QueueHandler(UserTaskListener listener){
        super();
        this.mListener = listener;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        mListener.updateTask((UserTask) msg.obj);
    }


}
