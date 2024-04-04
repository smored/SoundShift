package com.bcit.soundshift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter<T> extends BaseAdapter {

    private Context context;
    private ArrayList<T> dataList;
    private boolean[] selectedItems;
    private OnTextClickListener textClickListener;

    public CustomAdapter(Context context, ArrayList<T> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.selectedItems = new boolean[dataList.size()];
    }

    public interface OnTextClickListener {
        void onTextClick(View view, int position);
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
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.list_item_text_view);
            holder.checkBox = convertView.findViewById(R.id.list_item_check_box);
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
                    ((OnTextClickListener) textClickListener).onTextClick(v, position); // Pass position to listener
                }
            });
        }

        holder.checkBox.setChecked(selectedItems[position]);
        holder.checkBox.setOnClickListener(v -> {
            CheckBox checkBox = (CheckBox) v;
            selectedItems[position] = checkBox.isChecked();
        });

        holder.checkBox.setChecked(selectedItems[position]);
        holder.checkBox.setOnClickListener(v -> {
            CheckBox checkBox = (CheckBox) v;
            selectedItems[position] = checkBox.isChecked();
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }

    public void setTextClickListener(OnTextClickListener listener) {
        this.textClickListener = listener;
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