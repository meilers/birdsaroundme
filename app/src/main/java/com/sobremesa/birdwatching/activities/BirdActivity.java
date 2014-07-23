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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.fragments.BirdsFragment;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omegatai on 2014-07-09.
 */
public class BirdActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private RemoteSighting mBird;
    private CursorLoader mCusorLoader;

    private ImageView mIv;

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

        mIv = (ImageView)findViewById(R.id.activity_bird_iv);

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
            ArrayList<RemoteBirdImage> birdImages = new ArrayList<RemoteBirdImage>();

            for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                birdImages.add(new RemoteBirdImage(cursor));
            }

            mBird.setImages(birdImages);

            if( mBird.getImages().size() > 0 )
                BAMApplication.getImageLoader().displayImage(mBird.getImages().get(0).getImageUrl(), mIv);
            else
                mIv.setImageDrawable(BAMApplication.getContext().getResources().getDrawable(R.drawable.default_bird));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
