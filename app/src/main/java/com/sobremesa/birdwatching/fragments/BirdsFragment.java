package com.sobremesa.birdwatching.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.activities.MainActivity;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BirdsFragment extends Fragment implements LoaderCallbacks<Cursor> {

	public static final String TAG = BirdsFragment.class.getSimpleName();



	private CursorLoader mCusorLoader;


    public static final BirdsFragment newInstance() {
        BirdsFragment f = new BirdsFragment();

        Bundle args = f.getArguments();
        if (args == null) {
            args = new Bundle();
        }

        f.setArguments(args);

        return f;
    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		Bundle arguments = getArguments();


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_birds, container, false);

		return view;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		getLoaderManager().initLoader(0, null, this);
	}



	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		mCusorLoader = new CursorLoader(getActivity(), BAMContentProvider.Uris.SIGHTINGS_GROUP_BY_BIRD_URI, SightingTable.ALL_COLUMNS, null, null, null);

		return mCusorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if( cursor != null )
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                RemoteSighting sighting = new RemoteSighting(cursor);


                int count = cursor.getCount();


            }




        }
	}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
