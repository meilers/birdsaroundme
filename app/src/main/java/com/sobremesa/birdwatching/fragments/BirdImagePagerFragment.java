package com.sobremesa.birdwatching.fragments;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.activities.BirdActivity;
import com.sobremesa.birdwatching.adapters.BirdImagesPagerAdapter;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by omegatai on 2014-07-28.
 */
public class BirdImagePagerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = BirdImagePagerFragment.class.getSimpleName();

    private static final class Extras {
        public static final String SCI_NAME = "sciName";
    }

    private String mSciName;
    private ArrayList<RemoteBirdImage> mImages;

    private BirdImagesPagerAdapter mImageAdapter;
    private ViewPager mImagePager;
    private CirclePageIndicator mImageIndicator;


    public static final BirdImagePagerFragment newInstance(String sciName)  {
        BirdImagePagerFragment f = new BirdImagePagerFragment();

        Bundle args = f.getArguments();
        if (args == null) {
            args = new Bundle();
            args.putString(Extras.SCI_NAME, sciName);

        }

        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSciName = getArguments().getString(Extras.SCI_NAME);
        mImages = new ArrayList<RemoteBirdImage>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bird_image_pager, container, false);

        mImageAdapter = new BirdImagesPagerAdapter(getChildFragmentManager(), mImages);
        mImagePager = (ViewPager)view.findViewById(R.id.fragment_bird_image_pager_view_pager);
        mImagePager.setAdapter(mImageAdapter);

        mImageIndicator = (CirclePageIndicator)view.findViewById(R.id.fragment_bird_image_pager_view_pager_indicator);
        mImageIndicator.setViewPager(mImagePager);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        getActivity().getSupportLoaderManager().initLoader(BAMConstants.BIRD_IMAGE_LOADER_ID, null, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        switch (id)
        {
            case BAMConstants.BIRD_IMAGE_LOADER_ID:
                return new CursorLoader(getActivity(), BAMContentProvider.Uris.BIRD_IMAGES_URI, BirdImageTable.ALL_COLUMNS, BirdImageTable.SCI_NAME + "=?", new String[] {mSciName}, BirdImageTable.POSITION);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {

        mImages.clear();

        if( cursor != null )
        {
            RemoteBirdImage image;

            for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() )
            {
                image = new RemoteBirdImage(cursor);
                mImages.add(image);
            }
        }

        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }



}
