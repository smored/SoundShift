package com.bcit.soundshift;

import android.widget.Button;

public class Connection {
    private int ID;
    private ShiftButton startButton;
    private ShiftButton endButton;
    private int startX, startY, endX, endY;
    private float weighting;

    public Connection(int ID, ShiftButton startButton, ShiftButton endButton, float weight) {
        this.ID = ID;
        this.startButton = startButton;
        this.endButton = endButton;
        this.weighting = weight;

        calcLocation();
    }

    public void calcLocation() {
        int[] location1 = new int[2];
        int[] location2 = new int[2];
        startButton.getLocationOnScreen(location1);
        endButton.getLocationOnScreen(location2);
        startX = location1[0] + startButton.getWidth() / 2;
        startY = location1[1] - startButton.getHeight() / 8;
        endX = location2[0] + endButton.getWidth() / 2;
        endY = location2[1] - endButton.getHeight() / 8;
    }

    public ShiftButton getStartButton() {
        return startButton;
    }

    public void setStartButton(ShiftButton startButton) {
        this.startButton = startButton;
    }

    public ShiftButton getEndButton() {
        return endButton;
    }

    public void setEndButton(ShiftButton endButton) {
        this.endButton = endButton;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public float getWeighting() {
        return weighting;
    }

    public void setWeighting(float weighting) {
        this.weighting = weighting;
    }

    public int getID() {
        return ID;
    }
}