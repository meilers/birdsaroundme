package com.sobremesa.birdwatching.adapters;

/**
 * Created by omegatai on 2014-07-09.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import com.sobremesa.birdwatching.managers.SettingsManager;
import com.sobremesa.birdwatching.models.DistanceType;
import com.sobremesa.birdwatching.models.Settings;
import com.sobremesa.birdwatching.models.SortByType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omegatai on 2014-07-09.
 */
public class SettingsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Item it = mItems.get(position);

        if (it instanceof ListItem) {

            if( position < DistanceType.values().length+1 )
            {
                DistanceType distance = DistanceType.FIFTY_KM;

                switch (position-1) {

                    case 0:
                        distance = DistanceType.FIFTY_KM;
                        setDistanceIndex(0);
                        break;

                    case 1:
                        distance = DistanceType.TWENTY_KM;
                        setDistanceIndex(1);
                        break;

                    case 2:
                        distance = DistanceType.FIVE_KM;
                        setDistanceIndex(2);
                        break;

                }

                SettingsManager.INSTANCE.setDistance(distance);
            }
            else
            {
                SortByType sort = SortByType.DISTANCE;

                switch (position-DistanceType.values().length-2) {
                    case 0:
                        sort = SortByType.DISTANCE;
                        setSortByIndex(0);
                        break;

                    case 1:
                        sort = SortByType.DATE;
                        setSortByIndex(1);
                        break;

                    case 2:
                        sort = SortByType.NAME;
                        setSortByIndex(2);
                        break;


                }

                SettingsManager.INSTANCE.setSortBy(sort);
            }

        }

        notifyDataSetChanged();


    }

    public enum SettingsListItemType {
        DISTANCE_50, DISTANCE_20, DISTANCE_5, SORT_BY_DISTANCE, SORT_BY_DATE, SORT_BY_NAME
    }

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Item> mItems;
    private int mDistanceIndex = 0;
    private int mSortByIndex = 0;

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }


    public SettingsAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mItems = new ArrayList<Item>();

        createListItems();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public boolean isEnabled(int position) {

        Item item = getItem(position);
        if (item.getViewType() == RowType.HEADER_ITEM.ordinal()) {
            return false;
        }
        return true;
        // return false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {

        return getItem(position).getViewType();
    }


    public void setDistanceIndex(int distanceIndex) {
        this.mDistanceIndex = distanceIndex;
    }

    public void setSortByIndex(int sortByIndex) {
        this.mSortByIndex = sortByIndex;
    }

    private void createListItems() {
        mItems.add(new Header("Distance Radius"));
        mItems.add(new ListItem("50 km", SettingsListItemType.DISTANCE_50));
        mItems.add(new ListItem("20 km", SettingsListItemType.DISTANCE_20));
        mItems.add(new ListItem("5 km", SettingsListItemType.DISTANCE_5));

        mItems.add(new Header("Sort By"));
        mItems.add(new ListItem("Distance", SettingsListItemType.SORT_BY_DISTANCE));
        mItems.add(new ListItem("Date", SettingsListItemType.SORT_BY_DATE));
        mItems.add(new ListItem("Name", SettingsListItemType.SORT_BY_NAME));


    }

    static class PlaceHolder {
        TextView txtTitle;
        RadioButton rb;
    }


    public abstract class Item {
        protected String mText;

        public Item(String text) {
            mText = text;
        }

        public abstract int getViewType();

        public abstract View getView(LayoutInflater inflater, View convertView);
    }

    public class Header extends Item {

        public Header(String text) {
            super(text);
        }

        @Override
        public int getViewType() {
            return RowType.HEADER_ITEM.ordinal();
        }

        @Override
        public View getView(LayoutInflater inflater, View convertView) {
            View view;
            if (convertView == null) {
                view = (View) inflater.inflate(R.layout.list_item_settings_header, null);
                // Do some initialization
            } else {
                view = convertView;
            }

            TextView text = (TextView) view.findViewById(R.id.list_item_settings_header_tv);
            text.setText(mText);

            return view;
        }


    }


    public class ListItem extends Item {

        private SettingsListItemType mSettingsListItemType;

        public ListItem(String text, SettingsListItemType listItemType) {
            super(text);
            mSettingsListItemType = listItemType;
        }

        @Override
        public int getViewType() {
            return RowType.LIST_ITEM.ordinal();
        }

        @Override
        public View getView(LayoutInflater inflater, View convertView) {
            View view;
            if (convertView == null) {
                view = (View) inflater.inflate(R.layout.list_item_settings, null);
            } else {
                view = convertView;
            }

            TextView text = (TextView) view.findViewById(R.id.list_item_settings_tv);
            text.setText(mText);

            RadioButton rb = (RadioButton) view.findViewById(R.id.list_item_settings_rb);
            boolean isChecked = false;

            if( mSettingsListItemType == SettingsListItemType.DISTANCE_50 && mDistanceIndex == 0 ||
                    mSettingsListItemType == SettingsListItemType.DISTANCE_20 && mDistanceIndex == 1 ||
                    mSettingsListItemType == SettingsListItemType.DISTANCE_5 && mDistanceIndex == 2 ||
                    mSettingsListItemType == SettingsListItemType.SORT_BY_DISTANCE && mSortByIndex == 0 ||
                    mSettingsListItemType == SettingsListItemType.SORT_BY_DATE && mSortByIndex == 1 ||
                    mSettingsListItemType == SettingsListItemType.SORT_BY_NAME && mSortByIndex == 2 )
            {
                isChecked = true;
            }
            rb.setChecked(isChecked);

            return view;
        }

        public SettingsListItemType getSettingsListItemType() {
            return mSettingsListItemType;
        }

    }
}
