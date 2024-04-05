package com.bcit.soundshift;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class utils
{
    public static void displayEntryData(Context context, String tablename, int id) {
        DatabaseHelper sql = new DatabaseHelper(context);
        ArrayList<String> columnNames = sql.getColumnNames(tablename);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Modify Entry");

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_modify_entry, null);

        LinearLayout editContainer = dialogView.findViewById(R.id.edit_text_container);
        ArrayList<EditText> editTextList = new ArrayList<>(); // Store references to EditText fields

        for (String columnName : columnNames) {
            ArrayList<String> param = new ArrayList<>();
            param.add(columnName);
            param.add(tablename);
            param.add(Integer.toString(id));
            String current = sql.cursorToSingleColumn(sql.executeQuery(sql.replaceNamedParams("SELECT :param1 FROM :param2 WHERE id = :param3", param)));

            if (current.toLowerCase().contains("id"))
            {
                continue;
            }
            // Add TextView for column name
            TextView columnNameTextView = new TextView(context);
            columnNameTextView.setText(columnName);
            editContainer.addView(columnNameTextView);

            // Add EditText for data entry
            EditText editText = new EditText(context);
            editText.setHint("Enter " + columnName);



            // Add other customization for the EditText as needed
            editText.setText(current);
            editContainer.addView(editText);


            // Add EditText to the list
            editTextList.add(editText);
        }

        builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ArrayList<String> newDataList = new ArrayList<>();
                for (EditText editText : editTextList) {
                    newDataList.add(editText.getText().toString());
                }
                sql.modifyData(tablename, id, newDataList);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
