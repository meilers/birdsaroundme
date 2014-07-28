package com.sobremesa.birdwatching.fragments;

import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.adapters.BirdImagesPagerAdapter;
import com.sobremesa.birdwatching.database.BirdDescriptionTable;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.managers.LocationManager;
import com.sobremesa.birdwatching.models.remote.RemoteBirdDescription;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.tasks.DownloadBirdDescriptionTask;
import com.sobremesa.birdwatching.util.LocationUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by omegatai on 2014-07-28.
 */
public class BirdDescriptionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = BirdDescriptionFragment.class.getSimpleName();


    private static final class Extras {
        public static final String BIRD = "bird";
    }

    private RemoteSighting mBird;

    private TextView mDescriptionTv;

    private DownloadBirdDescriptionTask mDownloadBirdDescriptionTask;

    public static final BirdDescriptionFragment newInstance(RemoteSighting bird)  {
        BirdDescriptionFragment f = new BirdDescriptionFragment();

        Bundle args = f.getArguments();
        if (args == null) {
            args = new Bundle();
            args.putParcelable(Extras.BIRD, bird);

        }

        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBird = getArguments().getParcelable(Extras.BIRD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bird_description, container, false);

        mDescriptionTv = (TextView)view.findViewById(R.id.fragment_bird_description_tv);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().getSupportLoaderManager().initLoader(BAMConstants.BIRD_DESCRIPTION_LOADER_ID, null, this);

        // sync
        if( mDownloadBirdDescriptionTask != null )
        {
            mDownloadBirdDescriptionTask.cancel(true);
            mDownloadBirdDescriptionTask = null;
        }

        mDownloadBirdDescriptionTask = new DownloadBirdDescriptionTask();
        mDownloadBirdDescriptionTask.execute(mBird);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        switch (id)
        {
            case BAMConstants.BIRD_DESCRIPTION_LOADER_ID:
                return new CursorLoader(getActivity(), BAMContentProvider.Uris.BIRD_DESCRIPTIONS_URI, BirdDescriptionTable.ALL_COLUMNS, BirdDescriptionTable.SCI_NAME + "=?", new String[] {mBird.getSciName()}, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if( cursor != null && cursor.getCount() > 0 )
        {
            cursor.moveToFirst();

            RemoteBirdDescription description = new RemoteBirdDescription(cursor);
            mDescriptionTv.setText(Html.fromHtml(description.getDescription()));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
