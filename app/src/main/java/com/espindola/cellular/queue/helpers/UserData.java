package com.espindola.cellular.queue.helpers;

import java.io.Serializable;
import java.util.Random;

public class UserData implements Serializable {

    private long pid;
    private int progress;
    private String name;
    private boolean active;
    private int ttl;

    public UserData() {
        Random r = new Random();
        this.pid = r.nextInt(50 - 1) + 1;
        this.progress = 0;
        this.name = "Unknown";
        this.active = false;
        this.ttl = 0;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof UserData)) {
            return false;
        }

        UserData user = (UserData) o;

        return user.name.equals(name) &&
                user.progress == progress &&
                user.pid == pid &&
                user.ttl == ttl &&
                user.active == active;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + (int)pid;
        result = 31 * result + progress;
        result = 31 * result + ttl;
        result = 31 * result + String.valueOf(active).hashCode();
        return result;
    }
}
