package com.sobremesa.birdwatching.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.managers.LocationManager;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteBirdSound;
import com.sobremesa.birdwatching.util.LocationUtil;

import org.apache.commons.lang3.text.WordUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by omegatai on 2014-06-20.
 */
public class BirdSoundsAdapter extends BaseAdapter
{
    private Context mContext;

    private List<RemoteBirdSound> mBirdSoundsList;


    public BirdSoundsAdapter(Context context, List<RemoteBirdSound> birdSoundsList) {
        this.mContext = context;
        this.mBirdSoundsList = birdSoundsList;

    }


    public int getCount() {
        return mBirdSoundsList.size();
    }

    public Object getItem(int position) {
        return mBirdSoundsList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        RemoteBirdSound birdSound = mBirdSoundsList.get(position);
        PlaceHolder holder;

        if( convertView == null )
        {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_bird_sound, null);

            holder = new PlaceHolder();
            holder.mTypeTv = (TextView)convertView.findViewById(R.id.list_item_bird_sound_type_tv);
            holder.mLocTv = (TextView)convertView.findViewById(R.id.list_item_bird_sound_loc_tv);
            holder.mAuthorTv = (TextView)convertView.findViewById(R.id.list_item_bird_sound_author_tv);

            convertView.setTag(holder);
        }


        holder = (PlaceHolder)convertView.getTag();

        // Type
        String type = birdSound.getType();

        if( type != null && !type.isEmpty() ) {
            type = WordUtils.capitalize(type);
            holder.mTypeTv.setText(type);
        }

        // Location
        String loc = birdSound.getLoc();

        if( loc != null && !loc.isEmpty() ) {
            loc = WordUtils.capitalize(loc);
            holder.mLocTv.setText(loc);
        }


        // Author
        String author = "";

        if( birdSound.getRec() != null && !birdSound.getRec().isEmpty() )
        {
            author = birdSound.getRec();

            if( birdSound.getCnt() != null && !birdSound.getCnt().isEmpty() )
                author += ", " + birdSound.getCnt();

            holder.mAuthorTv.setText(author);
        }


        return convertView;
    }

    private static class PlaceHolder
    {
        private TextView mTypeTv;
        private TextView mLocTv;
        private TextView mAuthorTv;
    }
}

