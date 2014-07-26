package com.sobremesa.birdwatching.activities;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.adapters.BirdImagesPagerAdapter;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.fragments.BirdsFragment;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.viewpagerindicator.CirclePageIndicator;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by omegatai on 2014-07-09.
 */
public class BirdActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private RemoteSighting mBird;
    private List<RemoteBirdImage> mBirdImages;


    private CursorLoader mCusorLoader;

    private BirdImagesPagerAdapter mImageAdapter;
    private ViewPager mImagePager;
    private CirclePageIndicator mImageIndicator;

    private TextView mComNameTv;
    private TextView mSciNameTv;


    public final static class Extras
    {
        public static final String BIRD = "bird";
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(android.view.Window.FEATURE_ACTION_BAR_OVERLAY );

        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_bird);

        mBird = (RemoteSighting)getIntent().getParcelableExtra(Extras.BIRD);
        mBirdImages = new ArrayList<RemoteBirdImage>();

        mImageAdapter = new BirdImagesPagerAdapter(getSupportFragmentManager(), mBirdImages);
        mImagePager = (ViewPager)findViewById(R.id.activity_bird_view_pager);
        mImagePager.setAdapter(mImageAdapter);

        mImageIndicator = (CirclePageIndicator)findViewById(R.id.activity_bird_view_pager_indicator);
        mImageIndicator.setViewPager(mImagePager);

        mComNameTv = (TextView)findViewById(R.id.activity_bird_com_name_tv);
        mSciNameTv = (TextView)findViewById(R.id.activity_bird_sci_name_tv);


        updateView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bird, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mCusorLoader = new CursorLoader(this, BAMContentProvider.Uris.BIRD_IMAGES_URI, BirdImageTable.ALL_COLUMNS, BirdImageTable.SCI_NAME + "=?", new String[] {mBird.getSciName()}, null);

        return mCusorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if( cursor != null )
        {
            mBirdImages.clear();

            for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                mBirdImages.add(new RemoteBirdImage(cursor));
            }

            mImageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void updateView()
    {
        mComNameTv.setText(mBird.getComName());
        mSciNameTv.setText(mBird.getSciName());
    }
}
