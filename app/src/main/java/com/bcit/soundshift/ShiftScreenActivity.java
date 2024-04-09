package com.bcit.soundshift;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.Random;

public class ShiftScreenActivity extends AppCompatActivity {
    private Button addButton, removeButton;
    private TextView title;
    private ToggleButton connectorToggle;
    private ArrayList<ShiftButton> buttonArrayList;
    private GridLayout parentLayout;
    private Random random = new Random();
    private final int offsetX = -120;
    private final int offsetY = -370;
    private boolean connectorToggleBool = false;
    private ArrayList<ShiftButton> buttonsLastClicked;
    private LinearLayout connectorLayout;
    private DrawConnection drawConnection;
    private int connection_ids;
    private Shift currentShift;
    private int insert_pos_x;
    private int insert_pos_y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_screen_activity);

        currentShift = (Shift) getIntent().getSerializableExtra("shift");
        currentShift.transientDatabase(this);

        insert_pos_x = 1;
        insert_pos_y = 1;

        connection_ids = 0;

        buttonArrayList = new ArrayList<>();
        buttonsLastClicked = new ArrayList<>();
        drawConnection = new DrawConnection(this);

        parentLayout = findViewById(R.id.shiftLayout);
        addButton = findViewById(R.id.addShift);
        removeButton = findViewById(R.id.removeShift);
        connectorToggle = findViewById(R.id.toggleConnectMode);
        connectorLayout = findViewById(R.id.connectorID);
        title = findViewById(R.id.shiftName);
        title.setText(currentShift.getName());

        parentLayout.setElevation(1);

        setupButtonOnClickListener();

        shiftInit();
    }

    private void shiftInit() {
        ArrayList<Shift.connection> conn = new ArrayList<>();
        conn = currentShift.getConnections(currentShift.getID());

        // Iterate through the list of connections
        for (Shift.connection connection : conn) {
            int playlist1Id = connection.playlist_1_id;
            int playlist2Id = connection.playlist_2_id;

            ShiftButton playlist1 = insertPlaylistIfNotExists(playlist1Id);
            ShiftButton playlist2 = insertPlaylistIfNotExists(playlist2Id);

            startConnection(playlist1, playlist2, connection.weight); // Assuming weight is always 1.0
        }

    }

    private ShiftButton insertPlaylistIfNotExists(int playlistId) {
        for (ShiftButton button : buttonArrayList)
        {
            if(button.id == playlistId)
            {
                return button;
            }
        }
        insertPlaylist(playlistId, currentShift.getPlaylistName(playlistId));
        ShiftButton newButton = buttonArrayList.get(buttonArrayList.size() - 1);


        return newButton;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllShifts();
    }

    @SuppressLint("ClickableViewAccessibility") // hehe
    private void insertPlaylist(int id, String name) {
        Log.i(Constants.SHIFTSCREENACTIVITY_TAG, "Inserting new blank shift...");

        // make new button
        buttonArrayList.add(new ShiftButton(this, id, name));
        // get most recent element and set it up
        ShiftButton latestButton = buttonArrayList.get(buttonArrayList.size() - 1);

        latestButton.setText(name);
        latestButton.setTextSize(Constants.TEXTSIZE/3);
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
                                //Log.i("ShiftScreenActivity", "ButtonX=" + location[0] + ", ButtonY=" + location[1]);
                                v.setX(newX);
                                v.setY(newY);

                                drawConnection.updateConnections();
                                connectorLayout.removeAllViews();
                                connectorLayout.addView(drawConnection);

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
                ShiftButton button = (ShiftButton)v;
                button.setBackgroundColor(getResources().getColor(R.color.backPurple));

                if(buttonsLastClicked.size() >= 2)
                {
                    if(buttonsLastClicked.get(1).id == button.id)
                    {
                        buttonsLastClicked.get(1).setBackgroundColor(getResources().getColor(R.color.foreGrey));
                        buttonsLastClicked.clear();
                        return;
                    }
                    buttonsLastClicked.remove(0);
                    buttonsLastClicked.get(0).setBackgroundColor(getResources().getColor(R.color.foreGrey));
                }
                else if(buttonsLastClicked.size() == 1)
                {
                    if(buttonsLastClicked.get(0).id == button.id)
                    {
                        buttonsLastClicked.get(0).setBackgroundColor(getResources().getColor(R.color.foreGrey));
                        buttonsLastClicked.clear();
                        return;
                    }
                    buttonsLastClicked.get(0).setBackgroundColor(getResources().getColor(R.color.foreGrey));
                }
                buttonsLastClicked.add(button);


                if (connectorToggleBool)
                {
                    if(buttonsLastClicked.size() >= 2)
                    {
                        newConnection(buttonsLastClicked.get(0), buttonsLastClicked.get(1), 0.3f);
                        buttonsLastClicked.get(1).setBackgroundColor(getResources().getColor(R.color.foreGrey));
                        buttonsLastClicked.clear();
                    }
                }
                else
                {
                    utils.displayEntryData(ShiftScreenActivity.this, "playlist", button.id);
                }
            }
        });

        Log.i(Constants.SHIFTSCREENACTIVITY_TAG, "Adding view...");
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.setGravity(Gravity.CENTER);
        layoutParams.rowSpec = GridLayout.spec(insert_pos_y); // TODO: figure this grid crap out later
        layoutParams.columnSpec = GridLayout.spec(insert_pos_x);

        if(insert_pos_x < 4)
        {
            insert_pos_x += 1;
        }
        else
        {
            insert_pos_x = 1;
            insert_pos_y += 1;
        }
        parentLayout.addView(latestButton, layoutParams);

    }

    private void newConnection(ShiftButton button1, ShiftButton button2, float weight) {
        if(!checkIfConnectionExists(button1.id, button2.id))
        {
            button1.add(connection_ids, button2.id, weight);
            currentShift.addNewConnection(button1.id, button2.id, weight);
            Connection newCon = new Connection(connection_ids, button1, button2, weight);
            drawConnection.addConnection(newCon);
            drawConnection.updateConnections();

            connectorLayout.removeAllViews();
            connectorLayout.addView(drawConnection);

            connection_ids++;
        }
    }

    private void startConnection(ShiftButton button1, ShiftButton button2, float weight) {
        if(!checkIfConnectionExists(button1.id, button2.id))
        {
            button1.add(connection_ids, button2.id, weight);
            Connection newCon = new Connection(connection_ids, button1, button2, weight);
            drawConnection.addConnection(newCon);
            drawConnection.updateConnections();

            connectorLayout.removeAllViews();
            connectorLayout.addView(drawConnection);

            connection_ids++;
        }
    }

    private boolean checkIfConnectionExists(int id1, int id2) {
        for (ShiftButton button : buttonArrayList)
        {
            for(ShiftButton.button_connections conn : button.connection_list)
            {
                if(button.id == id1 && conn.shift_connection == id2)
                {
                    return true;
                }
                if(button.id == id2 && conn.shift_connection == id1)
                {
                    return true;
                }
            }
        }
        return false;
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
                choosePlaylist();
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.displayEntryData(ShiftScreenActivity.this, "shift", currentShift.getID());
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlaylist(v);
            }
        });

        connectorToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectorToggleBool = !connectorToggleBool;
            }
        });

        drawConnection.setOnConnectionClickListener(new DrawConnection.OnConnectionClickListener() {
            @Override
            public void onConnectionClick(Connection connectionId) {
                // Handle the click event for the connection
                Log.d(Constants.SHIFTSCREENACTIVITY_TAG, "Connection clicked: " + connectionId);
                modifyConnection(connectionId);
                Log.d(Constants.SHIFTSCREENACTIVITY_TAG, "Going Crazy");
            }
        });
    }

    private void removePlaylist(View v) {
        if(buttonsLastClicked.size() == 0)
        {
            return;
        }
        ShiftButton button_to_remove = buttonsLastClicked.get(buttonsLastClicked.size() - 1);

        for(ShiftButton button : buttonArrayList)
        {
            if(button.id == button_to_remove.id)
            {
                for(ShiftButton.button_connections conn : button_to_remove.connection_list)
                {
                    drawConnection.removeConnection(conn.id);
                    currentShift.removeConnection(button_to_remove.id, conn.shift_connection);
                }
                //remove all connections from drawconnections
            }
            else
            {
                for(ShiftButton.button_connections conn : button.connection_list)
                {
                    if(conn.shift_connection == button_to_remove.id)
                    {
                        // Remove the connection from button_connections
                        button.connection_list.remove(conn);
                        // Remove the connection from drawconnections
                        drawConnection.removeConnection(conn.id);
                        currentShift.removeConnection(button.id, button_to_remove.id);
                        break; // No need to continue searching for this connection
                    }
                }
            }
        }

        buttonArrayList.remove(button_to_remove);
        buttonsLastClicked.clear();
        parentLayout.removeView(v);
        button_to_remove.setVisibility(View.GONE);
        parentLayout.invalidate();
    }

    private void choosePlaylist() {
        SearchView playlistSearch;
        ArrayAdapter<Playlist> playlistAdapter;
        ArrayList<Playlist> playlistArray;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_songs, null);
        dialogBuilder.setView(dialogView);

        ListView playlistView = dialogView.findViewById(R.id.songListView);
        playlistSearch = dialogView.findViewById(R.id.songSearchView);

        ArrayList<Playlist> playlists = new ArrayList<>();
        // Populate song list
        DatabaseHelper sql = new DatabaseHelper(this);
        try {
            ArrayList<ArrayList<String>> results = sql.cursorToList(sql.executeQuery("SELECT * FROM playlist"));
            results:
            for (int i = 0; i < results.size(); i++) {
                try {
                    for(ShiftButton alreadyAdded : buttonArrayList) {
                        Log.i(Constants.SHIFTSCREENACTIVITY_TAG, "WHY DOESNT THIS WORK ID: " + alreadyAdded.id + " ID: " + results.get(i).get(0));
                        if (alreadyAdded.id == Integer.parseInt(results.get(i).get(0))) {
                            continue results;
                        }
                    }
                    playlists.add( new Playlist(Integer.parseInt(results.get(i).get(0)), results.get(i).get(1)));
                } catch (Exception e) {
                    System.out.println("An exception occurred: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("An exception occurred: " + e.getMessage());
        }
        playlistArray = playlists;
        // Create an adapter to display the songs in the ListView
        playlistAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, playlistArray);
        playlistView.setAdapter(playlistAdapter);
        playlistView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        playlistSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                playlistAdapter.getFilter().filter(query);
                return true;
            }
        });

        dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve selected playlists

                SparseBooleanArray checkedItems = playlistView.getCheckedItemPositions();
                for (int i = 0; i < checkedItems.size(); i++) {
                    int position = checkedItems.keyAt(i);
                    if (checkedItems.get(position)) {
                        Playlist selectedPlaylist = playlistAdapter.getItem(position);
                        insertPlaylist(selectedPlaylist.getId(), selectedPlaylist.getName());
                    }
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void modifyConnection(Connection connection) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ShiftScreenActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.modify_connection_dialog, null);
        EditText editWeight = dialogView.findViewById(R.id.editWeight);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnAccept = dialogView.findViewById(R.id.btnAccept);
        Button btnRemove = dialogView.findViewById(R.id.btnRemove);
        // Log.d(TAG, "Im not crazy I knew I would get here"); // yeah youre crazy

        editWeight.setText(Float.toString(drawConnection.getConnectionWeight(connection.getID())));

        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();

        // Set click listeners for buttons
        btnCancel.setOnClickListener(view -> {
            // Handle cancel button click
            // Close the dialog
            dialog.dismiss();
        });

        btnAccept.setOnClickListener(view -> {
            // Handle accept button click
            String newWeight = editWeight.getText().toString();
            // Update weight logic
            currentShift.removeConnection(connection.getStartButton().id, connection.getEndButton().id);
            currentShift.addNewConnection(connection.getStartButton().id, connection.getEndButton().id, Float.parseFloat(newWeight));
            connection.setWeighting(Float.parseFloat(newWeight));
            drawConnection.updateConnections();
            connectorLayout.removeAllViews();
            connectorLayout.addView(drawConnection);
            dialog.dismiss();
        });

        btnRemove.setOnClickListener(view -> {
            // Handle remove button click
            currentShift.removeConnection(connection.getStartButton().id, connection.getEndButton().id);
            drawConnection.removeConnection(connection.getID());
            drawConnection.updateConnections();
            connectorLayout.removeAllViews();
            connectorLayout.addView(drawConnection);
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER; // Adjust as needed (e.g., Gravity.BOTTOM)
        layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND; // Prevent background dimming
        window.setAttributes(layoutParams);

        dialog.setView(dialogView);
        dialog.show();

    }
}
