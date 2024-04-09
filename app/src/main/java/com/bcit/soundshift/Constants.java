package com.bcit.soundshift;

public class Constants {

    // SQL -----------------------------------------------------------------
    public static final String DATABASE_TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "soundshift.db";
    public static final int DATABASE_VERSION = 1;
    // ----------------------------------------------------------------------


    // LASTFM ---------------------------------------------------------------
    public static final String LFM_TAG = "LastFMApiHelper";
    public static final String API_KEY = "e0c830774d6ed7a898d1f4b0f59d18c7";
    public static final String SECRET = "8609e02fecda0869ea4b28a7bdbb5561";
    // ----------------------------------------------------------------------


    // DRAW CONNETIONS -------------------------------------------------------------------------------------------------------
    public static final int TOUCH_THRESHOLD = 25; // Buffer for making the buttons a little bigger and easier to touch
    public static final int SIDE_BUTTON_THRESHOLD = 200; // Controls how easily the connection points will snap to the sides
    public static final int BUTTON_HEIGHT_FIX = +12; // Manual fix to the lines being slightly misaligned
    public static final int TEXTSIZE = 40; // Size of Text
    public static final int CIRCLESIZE = 15; // How big to draw the connector circles
    public static final int STROKEWIDTH = 7; // Default Stroke Width
    // ------------------------------------------------------------------------------------------------------------------------

    // SHIFT SCREEN ACTIVITY -------------------------------------------------
    public static final String SHIFTSCREENACTIVITY_TAG = "ShiftScreenActivity";
    // -----------------------------------------------------------------------


}
