package com.sobremesa.birdwatching.activities;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.managers.LocationManager;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by omegatai on 2014-07-28.
 */
public class BirdMapActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = BirdMapActivity.class.getSimpleName();

    public static final class Extras {
        public static final String BIRD = "bird";
    }


    private GoogleMap mMap;
    private RemoteSighting mSelectedBird;
    private ArrayList<RemoteSighting> mBirds;
    private HashMap<LatLng, ArrayList<RemoteSighting>> mLocationBirdsMap;
    private Marker mSelectedMarker;

    private GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {


            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_map);

        Bundle extras = getIntent().getExtras();

        if( extras != null )
            mSelectedBird = extras.getParcelable(Extras.BIRD);

        mBirds = new ArrayList<RemoteSighting>();
        mLocationBirdsMap = new HashMap<LatLng, ArrayList<RemoteSighting>>();
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportLoaderManager().initLoader(BAMConstants.SIGHTING_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCamera();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id)
        {
            case BAMConstants.SIGHTING_LOADER_ID:
                return new CursorLoader(this, BAMContentProvider.Uris.SIGHTINGS_GROUP_BY_BIRD_URI, SightingTable.ALL_COLUMNS, null, null, SightingTable.OBS_DT + " DESC");
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        boolean isFoundSelectedBird = false;

        mBirds.clear();

        if( cursor != null )
        {
            RemoteSighting bird;

            for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() )
            {
                bird = new RemoteSighting(cursor);
                mBirds.add(bird);

                if( mSelectedBird != null && bird.getSciName().equals(mSelectedBird) )
                    isFoundSelectedBird = true;
            }
        }

        if( mSelectedBird != null && !isFoundSelectedBird )
            mBirds.add(mSelectedBird);

        updateMarkers();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateCamera()
    {
        // Default Montreal location
        Double lat = 45.5086699;
        Double lng = -73.5539924;

        Location myLocation = LocationManager.INSTANCE.getLocation();

        if( mSelectedBird != null )
        {
            lat = mSelectedBird.getLat();
            lng = mSelectedBird.getLng();
        }
        else if( myLocation != null )
        {
            lat = myLocation.getLatitude();
            lng = myLocation.getLongitude();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12));


    }

    private void updateMarkers()
    {
        Marker marker;
        MarkerOptions markerOptions;
        LatLng location;
        boolean isSelectedMarker = false;


        mLocationBirdsMap.clear();

        for( RemoteSighting bird: mBirds )
        {
            location = new LatLng(bird.getLat(), bird.getLng());

            if( !mLocationBirdsMap.containsKey(location) )
                mLocationBirdsMap.put(location, new ArrayList<RemoteSighting>());

            ArrayList<RemoteSighting> birdsAtLocation = mLocationBirdsMap.get(location);
            birdsAtLocation.add(bird);
        }

        for (Map.Entry<LatLng, ArrayList<RemoteSighting>> entry : mLocationBirdsMap.entrySet()) {
            ArrayList<RemoteSighting> birds = (entry.getValue());
            RemoteSighting firstBird = birds.get(0);
            markerOptions = new MarkerOptions().position(entry.getKey()).title(firstBird.getLocName()).snippet(birds.size() + " birds");

            if (mSelectedBird != null)
            {
                LatLng birdLocation = entry.getKey();
                LatLng selectedBirdLocation = new LatLng(mSelectedBird.getLat(), mSelectedBird.getLng());

                if( selectedBirdLocation.equals(birdLocation)) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    isSelectedMarker = true;
                }
            }

            marker = mMap.addMarker(markerOptions);

            if( isSelectedMarker ) {
                mSelectedMarker = marker;
                marker.showInfoWindow();
                isSelectedMarker = false;
            }

        }


    }
}
