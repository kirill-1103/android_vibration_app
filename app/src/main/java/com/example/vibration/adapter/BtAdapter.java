package com.example.vibration.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vibration.R;

import java.util.ArrayList;
import java.util.List;

public class BtAdapter extends ArrayAdapter<ListItem> {

    private List<ListItem> btList;
    private List<ViewHolder> viewHolders;

    private SharedPreferences pref;

    public static final String DEF_ITEM_TYPE = "normal";
    public static final String TITLE_ITEM_TYPE = "title";
    public static final String DISCOVERY_ITEM_TYPE = "discovery";

    public BtAdapter(@NonNull Context context, int resource, List<ListItem> btList) {
        super(context, resource, btList);
        this.btList = btList;
        viewHolders = new ArrayList<>();
        pref = context.getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        switch (btList.get(position).getType()){
            case TITLE_ITEM_TYPE:
                convertView = titleItem(convertView, parent);
                break;
            default:
                convertView = defaultItem(convertView, position, parent);
                break;
        }
        return convertView;
    }

    private void savePref(int pos) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(BtConsts.MAC_KEY, btList.get(pos).getBluetoothDevice().getAddress());
        editor.apply();
    }

    @SuppressLint("MissingPermission")
    private View defaultItem(View convertView, int position, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item, null, false);
            viewHolder.tvBtName = convertView.findViewById(R.id.tvBtName);
            viewHolder.chBtSelected = convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);

            viewHolders.add(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            if(viewHolder == null) return convertView;
            viewHolder.chBtSelected.setChecked(false);
        }

        viewHolder.tvBtName.setText(btList.get(position).getBluetoothDevice().getName());
        viewHolder.chBtSelected.setOnClickListener((View view) -> {
            for (ViewHolder v : viewHolders) {
                v.chBtSelected.setChecked(false);
            }
            viewHolder.chBtSelected.setChecked(true);
            savePref(position);
        });

        if(btList.get(position).getType().equals(DISCOVERY_ITEM_TYPE)){
            viewHolder.chBtSelected.setVisibility(View.GONE);
        }


        if (pref.getString(BtConsts.MAC_KEY, "no bt selected").equals(btList.get(position).getBluetoothDevice().getAddress())) {
            viewHolder.chBtSelected.setChecked(true);
        }
        return convertView;
    }

    private View titleItem(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bt_list_item_title, null, false);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tvBtName;
        CheckBox chBtSelected;
    }
}
