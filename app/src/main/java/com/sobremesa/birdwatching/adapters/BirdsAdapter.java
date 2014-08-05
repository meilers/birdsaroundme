package com.sobremesa.birdwatching.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.managers.LocationManager;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.util.LocationUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by omegatai on 2014-06-20.
 */
public class BirdsAdapter extends BaseAdapter
{
    private Context mContext;

    private List<RemoteSighting> mBirdList;

    private int mRandomNumber = 0;


    public BirdsAdapter(Context context, List<RemoteSighting> birdList ) {
        this.mContext = context;
        this.mBirdList = birdList;

        Random r = new Random();
        this.mRandomNumber = r.nextInt(5);
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
        PlaceHolder holder;

        if( convertView == null )
        {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_bird, null);

            holder = new PlaceHolder();
            holder.mComNameTv = (TextView)convertView.findViewById(R.id.list_item_bird_tv);
            holder.mIv = (ImageView)convertView.findViewById(R.id.list_item_bird_iv);
            holder.mLastSeenTv = (TextView)convertView.findViewById(R.id.list_item_bird_last_seen_tv);
            holder.mDistanceTv = (TextView)convertView.findViewById(R.id.list_item_bird_distance_tv);

            convertView.setTag(holder);
        }


        holder = (PlaceHolder)convertView.getTag();

        // Name
        holder.mComNameTv.setText(bird.getComName());

        // Image

        List<RemoteBirdImage> images = bird.getImages();
        if( images != null && images.size() > 0 )
        {
            int i = Math.min(this.mRandomNumber, images.size()-1);

            RemoteBirdImage image = images.get(i);
            final String imageUrl = image.getImageUrl();

            BAMApplication.getImageLoader().displayImage(imageUrl, holder.mIv, new SimpleImageLoadingListener() {
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
            holder.mIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_bird));


        // Last Seen
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;

        try {
            date = dateFormat.parse(bird.getObsDt());

            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(date);

            Calendar today = Calendar.getInstance();

            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
            long days = diff / (24 * 60 * 60 * 1000);
            long hours = diff / (60 * 60 * 1000);

            if( days < 1 )
            {
                if( hours < 2 )
                    holder.mLastSeenTv.setText(hours + " hour ago");
                else
                    holder.mLastSeenTv.setText(hours + " hours ago");
            }
            else {

                if( days < 2 )
                    holder.mLastSeenTv.setText(days + " day ago");
                else
                    holder.mLastSeenTv.setText(days + " days ago");
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Distance
        Location location = LocationManager.INSTANCE.getLocation();

        if( location != null )
        {
            float distance = LocationUtil.computeDistance((float)location.getLatitude(), (float)location.getLongitude(), bird.getLat().floatValue(), bird.getLng().floatValue());
            float distanceInKm = distance/1000.0f;

            holder.mDistanceTv.setText(String.format("%.2f Km", distanceInKm));
        }


        return convertView;
    }

    private static class PlaceHolder
    {
        private TextView mComNameTv;
        private ImageView mIv;
        private TextView mLastSeenTv;
        private TextView mDistanceTv;
    }
}

