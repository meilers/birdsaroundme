package com.sobremesa.birdwatching.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.managers.LocationManager;
import com.sobremesa.birdwatching.models.SoundStateType;
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
    private String mPlayingSoundId;
    private SoundStateType mPlayingSoundStateType;

    public BirdSoundsAdapter(Context context, List<RemoteBirdSound> birdSoundsList) {
        this.mContext = context;
        this.mBirdSoundsList = birdSoundsList;
    }

    public void setPlayingSound(String soundId, SoundStateType playState )
    {
        mPlayingSoundId = soundId;
        mPlayingSoundStateType = playState;
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
            holder.mPlayIv = (ImageView)convertView.findViewById(R.id.list_item_bird_sound_play_iv);
            holder.mPlayPb = (ProgressBar)convertView.findViewById(R.id.list_item_bird_sound_play_progress_bar);
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


        // Play State
        Log.d("HERE", "IN ADAPTER");
        if( mPlayingSoundId != null && birdSound.getBirdSoundId().equals(mPlayingSoundId) )
        {
            Log.d("HERE", "playing sound id");

            switch (mPlayingSoundStateType)
            {

                case PAUSED:
                case STOPPED:
                    Log.d("HERE", "pause");
                    holder.mPlayPb.setVisibility(View.GONE);
                    holder.mPlayIv.setVisibility(View.VISIBLE);
                    holder.mPlayIv.setImageResource(R.drawable.ic_action_play);
                    break;

                case BUFFERING:
                    Log.d("HERE", "HEEEERE");
                    holder.mPlayPb.setVisibility(View.VISIBLE);
                    holder.mPlayIv.setVisibility(View.GONE);
                    break;

                case PLAYING:
                    holder.mPlayPb.setVisibility(View.GONE);
                    holder.mPlayIv.setVisibility(View.VISIBLE);
                    holder.mPlayIv.setImageResource(R.drawable.ic_action_pause);
                    break;
            }
        }
        else
        {
            holder.mPlayPb.setVisibility(View.GONE);
            holder.mPlayIv.setVisibility(View.VISIBLE);
            holder.mPlayIv.setImageResource(R.drawable.ic_action_play);
        }

        return convertView;
    }

    private static class PlaceHolder
    {
        private TextView mTypeTv;
        private TextView mLocTv;
        private TextView mAuthorTv;
        private ProgressBar mPlayPb;
        private ImageView mPlayIv;
    }
}

