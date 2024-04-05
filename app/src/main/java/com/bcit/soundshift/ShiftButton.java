package com.bcit.soundshift;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class ShiftButton extends androidx.appcompat.widget.AppCompatButton {
    public ShiftButton(Context context) {
        super(context);
        connection_list = new ArrayList<>();
        id = -1;
        name = "Why you here?";
    }

    public ShiftButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShiftButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ShiftButton(Context context, int id, String name) {
        super(context);
        this.id = id;
        this.name = name;
        connection_list = new ArrayList<>();
    }

    public int id;
    public String name;
    public ArrayList<button_connections> connection_list;

    public void add(int id, int shift_connection, float weight)
    {
        connection_list.add(new button_connections(id, shift_connection, weight));
    }
    public class button_connections {
        public int id;
        public int shift_connection;
        public float weight;
        public button_connections(int id, int shift_connection, float weight)
        {
            this.id = id;
            this.shift_connection = shift_connection;
            this.weight = weight;
        }
    }
}
