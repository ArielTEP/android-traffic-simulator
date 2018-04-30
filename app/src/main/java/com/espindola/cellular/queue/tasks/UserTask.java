package com.espindola.cellular.queue.tasks;

import android.os.Handler;
import android.os.Message;

import com.espindola.cellular.queue.listeners.UserTaskListener;

public class UserTask extends Thread {

    private Handler handler;
    private UserTaskListener listener;

    private int progress;
    private int TTL;

    public void setListener(UserTaskListener listener) {
        this.listener = listener;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTTL() {
        return TTL;
    }

    public void setTTL(int TTL) {
        this.TTL = TTL;
    }

    @Override
    public void run() {
        // this reduces resource competition between Thread and UI
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        UserTask userTask = (UserTask) Thread.currentThread();
        userTask.setTTL(9);


        for (int i = 0; i <= 9; i++) {

            userTask.setProgress(i);
            Message message = Message.obtain();
            message.obj = userTask;
            try {
                Thread.sleep(900);
                handler.sendMessage(message);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        sendCompleteEvent(userTask);
        Thread.currentThread().interrupt();
    }


    void sendCompleteEvent(UserTask task){
        listener.completeTask(task);
    }



}
