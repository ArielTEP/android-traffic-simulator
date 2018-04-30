package com.espindola.cellular.queue.listeners;

import android.view.View;

import com.espindola.cellular.queue.tasks.UserTask;

public interface OnTaskClickListener {
    void onClick(UserTask userData, View view);
    void onLongClick(UserTask userData);
}
