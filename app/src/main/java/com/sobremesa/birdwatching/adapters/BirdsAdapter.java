package com.sobremesa.birdwatching.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omegatai on 2014-06-20.
 */
public class BirdsAdapter extends BaseAdapter
{
    private Context mContext;

    private List<RemoteSighting> mBirdList;


    public BirdsAdapter(Context context, List<RemoteSighting> birdList ) {
        this.mContext = context;
        this.mBirdList = birdList;

    }


    public int getCount() {
        return mBirdList.size();
    }

    public Object getItem(int position) {
        return mBirdList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        RemoteSighting bird = mBirdList.get(position);

        if( convertView == null )
        {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_bird, null);
        }

        ImageView iv = (ImageView)convertView.findViewById(R.id.list_item_bird_iv);


        List<RemoteBirdImage> images = bird.getImages();
        if( images != null && images.size() > 0 )
        {
            RemoteBirdImage image = images.get(0);
            final String imageUrl = image.getImageUrl();
            Log.d("ima" + position, imageUrl);

            BAMApplication.getImageLoader().displayImage(imageUrl, iv, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView newIv = (ImageView)view;
                    if(imageUri.equals(imageUrl))
                    {

                        newIv.setImageBitmap(loadedImage);
                    }
                    else
                        newIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_bird));
                }
            });
        }
        else
            iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_bird));

        return convertView;
    }
}

