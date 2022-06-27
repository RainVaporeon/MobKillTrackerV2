package com.spiritlight.mobkilltracker;

public class status {
    private boolean b;

    public status() {
    }

    public status(boolean b) {
        this.b = b;
    }

    public void on() {
        this.b = true;
    }

    public void off() {
        this.b = false;
    }

    public boolean check() {
        return b;
    }
}
