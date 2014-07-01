package com.sobremesa.birdwatching.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;

import java.util.List;

/**
 * Created by omegatai on 2014-06-20.
 */
public class BirdsAdapter extends BaseAdapter
{
    private Context mContext;
    private DisplayImageOptions mDisplayImageOptions;

    private List<RemoteSighting> mSubshopList;
    private boolean mShowTile = false;


    public BirdsAdapter(Context context, List<RemoteSighting> subshopList ) {
        this.mContext = context;
        this.mSubshopList = subshopList;

        Drawable placeHolder = mContext.getResources().getDrawable(R.drawable.default_bird);

        this.mDisplayImageOptions = new DisplayImageOptions.Builder().
                cacheInMemory(true).
                cacheOnDisc(true).
                imageScaleType(ImageScaleType.EXACTLY).
                showImageForEmptyUri(placeHolder).
                showImageOnFail(placeHolder).
                showImageOnLoading(placeHolder).build();
    }

    public void setShowTile(boolean showTile) {
        mShowTile = showTile;
    }

    public boolean isShowTile() {
        return mShowTile;
    }

    public int getCount() {
        return mSubshopList.size();
    }

    public Object getItem(int position) {
        return mSubshopList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        RemoteSighting subshop = mSubshopList.get(position);

        if( convertView == null )
        {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_bird, null);
        }



        return convertView;
    }
}

