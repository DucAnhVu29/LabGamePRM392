package com.example.labgameprm392;

public class SeekBarRunner {

    private int progress;
    private String name;
    private boolean isRunning;
    private int rank;
    public int speed;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public SeekBarRunner(String name, int speed) {
        this.progress = 0;
        this.name = name;
        this.isRunning = true;
        this.rank = -1;
        this.speed = speed;
    }

    public void reset() {
        this.progress = 0;
        this.name = name;
        this.isRunning = true;
        this.rank = -1;
        this.speed = speed;
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
