package com.bcit.soundshift;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class ShiftScreenActivity extends AppCompatActivity {
    private final String TAG = "ShiftScreenActivity";
    private Button addButton, removeButton;
    private ToggleButton connectorToggle;
    private ArrayList<Button> buttonArrayList;
    private GridLayout parentLayout;
    private Random random = new Random();
    private final int offsetX = -120;
    private final int offsetY = -370;
    private boolean connectorToggleBool = false;
    private ArrayList<Button> buttonsTemp;
    private LinearLayout connectorLayout;
    private ArrayList<Connection> connectionArrayList;
    private DrawConnection drawConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_screen_activity);

        buttonArrayList = new ArrayList<>();
        buttonsTemp = new ArrayList<>();
        connectionArrayList = new ArrayList<>();
        drawConnection = new DrawConnection(this);

        parentLayout = findViewById(R.id.shiftLayout);
        addButton = findViewById(R.id.addShift);
        removeButton = findViewById(R.id.removeShift);
        connectorToggle = findViewById(R.id.toggleConnectMode);
        connectorLayout = findViewById(R.id.connectorID);

        setupButtonOnClickListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllShifts();
    }

    @SuppressLint("ClickableViewAccessibility") // hehe
    private void insertBlankShift() {
        Log.i(TAG, "Inserting new blank shift...");

        // make new button
        buttonArrayList.add(new Button(this));
        // get most recent element and set it up
        Button latestButton = buttonArrayList.get(buttonArrayList.size() - 1);

        String shiftyboi = Integer.toString(random.nextInt(1000));
        latestButton.setText("Shift " + shiftyboi);
        latestButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Set touch listener for drag
                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                // Get the new position of the button
                                int newX = (int) event.getRawX() + offsetX;
                                int newY = (int) event.getRawY() + offsetY;

                                // Update button position
                                //Log.i("ShiftScreenActivity", "ButtonX=" + newX + ", ButtonY=" + newY);
                                v.setX(newX);
                                v.setY(newY);
                                break;
                            case MotionEvent.ACTION_UP:
                                // Remove the touch listener when released
                                v.setOnTouchListener(null);
                                break;
                        }
                        return false;
                    }
                });

                return true;
            }
        });

        latestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawConnection.invalidate();
                if (connectorToggleBool) {
                    // connect doohickeys
                    if (buttonsTemp.size() < 2) {
                        buttonsTemp.add(latestButton);
                    } else if (buttonsTemp.size() == 2) {
                        if (buttonsTemp.get(0) == buttonsTemp.get(1)) {
                            Log.e(TAG, "button cant connect to itself");
                            buttonsTemp.clear();
                            return;
                        }

                        Connection newCon = new Connection("test", buttonsTemp.get(0), buttonsTemp.get(1));
                        connectionArrayList.add(newCon);
                        drawConnection.setList(connectionArrayList);

                        connectorLayout.removeAllViews();
                        connectorLayout.addView(drawConnection);

                        buttonsTemp.clear();
                    } else {
                        Log.wtf(TAG, "More elements than expected in buttonsTemp");
                        buttonsTemp.clear();
                    }
                } else {
                    buttonsTemp.clear();
                    // open playlist menu
                }
            }
        });

        Log.i(TAG, "Adding view...");
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.setGravity(Gravity.CENTER);
        layoutParams.rowSpec = GridLayout.spec(1); // TODO: figure this grid crap out later
        layoutParams.columnSpec = GridLayout.spec(1);
        parentLayout.addView(latestButton, layoutParams);

    }

    private void removeAllShifts() {
        for (Button button : buttonArrayList) {
            parentLayout.removeView(button);
        }
        buttonArrayList.clear();
    }


    private void removeShift(Button valueToRemove) {
        buttonArrayList.remove(valueToRemove);
    }

    private void setupButtonOnClickListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertBlankShift();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllShifts();
            }
        });

        connectorToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                connectorToggleBool = isChecked;
            }
        });
    }

}