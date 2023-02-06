package com.example.labgameprm392;

public class SeekBarRunner {

    private int progress;
    private String name;
    private boolean isRunning;

    public SeekBarRunner(String name) {
        this.progress = 0;
        this.name = name;
        this.isRunning = true;
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

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
