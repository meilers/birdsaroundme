package com.sobremesa.birdwatching.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.database.BirdSoundTable;
import com.sobremesa.birdwatching.fragments.BirdDescriptionFragment;
import com.sobremesa.birdwatching.fragments.BirdImagePagerFragment;
import com.sobremesa.birdwatching.fragments.BirdSoundsDialogFragment;
import com.sobremesa.birdwatching.managers.LocationManager;
import com.sobremesa.birdwatching.models.remote.RemoteBirdSound;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.tasks.DownloadBirdSoundsTask;
import com.sobremesa.birdwatching.util.LocationUtil;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by omegatai on 2014-07-09.
 */
public class BirdActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = BirdActivity.class.getSimpleName();


    public final static class Extras
    {
        public static final String BIRD = "bird";
    }



    private RemoteSighting mBird;


    private TextView mComNameTv;
    private TextView mSciNameTv;
    private TextView mHowManyTv;
    private TextView mDateTv;
    private TextView mDistanceTv;

    private DownloadBirdSoundsTask mDownloadBirdSoundsTask;

    private boolean mIsHasBirdSounds = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(android.view.Window.FEATURE_ACTION_BAR_OVERLAY );

        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        /* TODO remove */
//        getActionBar().hide();
        /* END */

        setContentView(R.layout.activity_bird);

        mBird = (RemoteSighting)getIntent().getParcelableExtra(Extras.BIRD);

        mComNameTv = (TextView)findViewById(R.id.activity_bird_com_name_tv);
        mSciNameTv = (TextView)findViewById(R.id.activity_bird_sci_name_tv);
        mHowManyTv = (TextView)findViewById(R.id.activity_bird_how_many_tv);
        mDateTv = (TextView)findViewById(R.id.activity_bird_date_tv);
        mDistanceTv = (TextView)findViewById(R.id.activity_bird_distance_tv);

        // Fragments
        BirdImagePagerFragment imageFragment = BirdImagePagerFragment.newInstance(mBird.getSciName());
        BirdDescriptionFragment descriptionFragment = BirdDescriptionFragment.newInstance(mBird);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_bird_images_container, imageFragment).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_bird_description_container, descriptionFragment).commit();

        updateView();
    }


    @Override
    protected void onStart() {
        super.onStart();

        mDownloadBirdSoundsTask = new DownloadBirdSoundsTask();
        mDownloadBirdSoundsTask.execute(mBird.getComName());

        getSupportLoaderManager().initLoader(BAMConstants.BIRD_SOUND_LOADER_ID, null, this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if( mDownloadBirdSoundsTask != null )
        {
            mDownloadBirdSoundsTask.cancel(true);
            mDownloadBirdSoundsTask = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bird, menu);

        MenuItem soundMenuItem = menu.findItem(R.id.action_sounds);
        soundMenuItem.setVisible(mIsHasBirdSounds);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId())
        {
            case android.R.id.home:
                // do something useful
                finish();
                break;

            case R.id.action_sounds:

                FragmentManager fm = getSupportFragmentManager();
                BirdSoundsDialogFragment dialog = BirdSoundsDialogFragment.newInstance(mBird);
                dialog.setCancelable(true);
                dialog.show(fm, BirdSoundsDialogFragment.class.getCanonicalName());
                break;
            case R.id.action_map:
                Intent intent = new Intent(this, BirdMapActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable(BirdMapActivity.Extras.BIRD, mBird);
                intent.putExtras(extras);
                startActivity(intent);

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void updateView()
    {
        mComNameTv.setText(mBird.getComName());
        mSciNameTv.setText(mBird.getSciName());


        // How Many
        mHowManyTv.setText(mBird.getHowMany()+ " Spotted");


        // Last Seen
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;

        try {
            date = dateFormat.parse(mBird.getObsDt());

            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(date);

            Calendar today = Calendar.getInstance();

            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
            long days = diff / (24 * 60 * 60 * 1000);
            long hours = diff / (60 * 60 * 1000);

            if( days < 1 )
            {
                if( hours < 2 )
                    mDateTv.setText(hours + " hour");
                else
                    mDateTv.setText(hours + " hours");
            }
            else {

                if( days < 2 )
                    mDateTv.setText(days + " day");
                else
                    mDateTv.setText(days + " days");
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Distance
        Location location = LocationManager.INSTANCE.getLocation();

        if( location != null )
        {
            float distance = LocationUtil.computeDistance((float) location.getLatitude(), (float) location.getLongitude(), mBird.getLat().floatValue(), mBird.getLng().floatValue());
            float distanceInKm = distance/1000.0f;

            mDistanceTv.setText(String.format("%.2f Km", distanceInKm));
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case BAMConstants.BIRD_SOUND_LOADER_ID:
            return new CursorLoader(this, BAMContentProvider.Uris.BIRD_SOUNDS_URI, BirdSoundTable.ALL_COLUMNS, BirdSoundTable.COM_NAME + "=?", new String[]{mBird.getComName()}, null);
        }

        return  null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            case BAMConstants.BIRD_SOUND_LOADER_ID:

                mIsHasBirdSounds = false;

                if( cursor != null )
                {
                    if( cursor.getCount() > 0 )
                        mIsHasBirdSounds = true;
                }

                invalidateOptionsMenu();
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
