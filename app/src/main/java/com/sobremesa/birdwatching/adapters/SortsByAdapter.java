package com.sobremesa.birdwatching.adapters;

/**
 * Created by omegatai on 2014-07-09.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sobremesa.birdwatching.R;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sobremesa.birdwatching.R;

/**
 * Created by omegatai on 2014-07-09.
 */
public class SortsByAdapter extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    String data[] = null;
    int selectedIndex = 0;

    public SortsByAdapter(Context context, int layoutResourceId, String[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public void setSelectedIndex(int index){
        selectedIndex = index;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PlaceHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PlaceHolder();
            holder.rb = (RadioButton)row.findViewById(R.id.list_item_dialog_list_selection_rb);
            holder.txtTitle = (TextView)row.findViewById(R.id.list_item_dialog_list_selection_tv);

            row.setTag(holder);
        }
        else
        {
            holder = (PlaceHolder)row.getTag();
        }

        String text = data[position];
        holder.txtTitle.setText(text);

        if(selectedIndex == position){
            holder.rb.setChecked(true);
        }
        else{
            holder.rb.setChecked(false);
        }

        return row;
    }

    static class PlaceHolder
    {
        TextView txtTitle;
        RadioButton rb;
    }
}
