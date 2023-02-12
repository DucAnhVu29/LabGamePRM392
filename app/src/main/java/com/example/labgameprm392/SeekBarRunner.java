package com.example.labgameprm392;

public class SeekBarRunner {

    private int progress;
    private String name;
    private boolean isRunning;
    private int rank;
    private final int initialSpeed;
    private int speed;

    private int image;


    //    Constructor
    public SeekBarRunner(String name, int initialSpeed, int image) {
        this.progress = 0;
        this.name = name;
        this.isRunning = true;
        this.rank = -1;
        this.speed = initialSpeed;
        this.initialSpeed = speed;
        this.image = image;
    }

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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void reset() {
        this.progress = 0;
        this.isRunning = true;
        this.rank = -1;
        this.speed = this.initialSpeed;
    }
}
