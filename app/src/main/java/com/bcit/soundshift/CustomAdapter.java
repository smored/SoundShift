package com.bcit.soundshift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import java.util.ArrayList;

public class CustomAdapter<T> extends BaseAdapter {

    private Context context;
    private ArrayList<T> dataList;
    private boolean[] selectedItems;
    private OnTextClickListener textClickListener;
    private OnShiftButtonClickListener shiftButtonClickListener;
    private OnRemoveClickListener removeShiftClickListener;
    private int resource;

    public CustomAdapter(Context context, ArrayList<T> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.selectedItems = new boolean[dataList.size()];
        this.resource = R.layout.list_item;
    }

    public CustomAdapter(Context context, @LayoutRes int resource, ArrayList<T> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.selectedItems = new boolean[dataList.size()];
        this.resource = resource;
    }

    public interface OnTextClickListener {
        void onTextClick(View view, int position);
    }

    public interface OnShiftButtonClickListener {
        void onShiftButtonClick(View view, int position);
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(View view, int position);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public T getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.list_item_text_view);
            if (resource == R.layout.list_item) {
                holder.checkBox = convertView.findViewById(R.id.list_item_check_box);
            } else if (resource == R.layout.shift_list) {
                holder.shiftButton = convertView.findViewById(R.id.shift_list_button);
                holder.shiftButton.setOnClickListener(v -> {
                    if (shiftButtonClickListener != null) {
                        shiftButtonClickListener.onShiftButtonClick(v, position);
                    }
                });
                holder.removeButton = convertView.findViewById(R.id.remove_shift);
                holder.removeButton.setOnClickListener(v -> {
                    if (removeShiftClickListener != null) {
                        removeShiftClickListener.onRemoveClick(v, position);
                    }
                });
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        T item = dataList.get(position);
        if (item != null) {
            holder.textView.setText(item.toString());
        }

        if (textClickListener != null) {
            holder.textView.setOnClickListener(v -> {
                if (textClickListener instanceof OnTextClickListener) {
                    textClickListener.onTextClick(v, position); // Pass position to listener
                }
            });
        }

        if (resource == R.layout.list_item) {
            holder.checkBox.setChecked(selectedItems[position]);
            holder.checkBox.setOnClickListener(v -> {
                CheckBox checkBox = (CheckBox) v;
                selectedItems[position] = checkBox.isChecked();
            });
        }

        return convertView;
    }

    private static class ViewHolder {
        ShiftButton shiftButton;
        Button removeButton;
        TextView textView;
        CheckBox checkBox;
    }

    public void setTextClickListener(OnTextClickListener listener) {
        this.textClickListener = listener;
    }

    public void setShiftButtonClickListener(OnShiftButtonClickListener listener) {
        this.shiftButtonClickListener = listener;
    }

    public void setRemoveClickListener(OnRemoveClickListener listener) {
        this.removeShiftClickListener = listener;
    }

    public ArrayList<T> getSelectedItems() {
        ArrayList<T> selectedList = new ArrayList<>();
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i]) {
                selectedList.add(dataList.get(i));
            }
        }
        return selectedList;
    }
}