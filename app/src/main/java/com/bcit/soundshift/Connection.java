package com.bcit.soundshift;

import android.widget.Button;

public class Connection {
    private String ID;
    private Button startButton;
    private Button endButton;
    private int startX, startY, endX, endY;
    private double weighting;

    public Connection(String ID, Button startButton, Button endButton) {
        this.ID = ID;
        this.startButton = startButton;
        this.endButton = endButton;

        calcLocation();
    }

    private void calcLocation() {
        int[] location1 = new int[2];
        int[] location2 = new int[2];
        startButton.getLocationOnScreen(location1);
        endButton.getLocationOnScreen(location2);
        startX = location1[0];
        startY = location1[1];
        endX = location2[0];
        endY = location2[1];
    }

    public Button getStartButton() {
        return startButton;
    }

    public void setStartButton(Button startButton) {
        this.startButton = startButton;
    }

    public Button getEndButton() {
        return endButton;
    }

    public void setEndButton(Button endButton) {
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

    public double getWeighting() {
        return weighting;
    }

    public void setWeighting(double weighting) {
        this.weighting = weighting;
    }
}