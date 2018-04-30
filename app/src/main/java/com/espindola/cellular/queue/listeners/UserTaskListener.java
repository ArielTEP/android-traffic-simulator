package com.espindola.cellular.queue.listeners;


import com.espindola.cellular.queue.tasks.UserTask;

public interface UserTaskListener {
    void completeTask(UserTask userTask);
    void updateTask(UserTask userTask);
}
