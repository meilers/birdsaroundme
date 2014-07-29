package com.sobremesa.birdwatching.activities;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.adapters.BirdsAdapter;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.managers.LocationManager;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.tasks.PopulateBirdImagesTask;
import com.sobremesa.birdwatching.util.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by omegatai on 2014-07-28.
 */
public class BirdMapActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = BirdMapActivity.class.getSimpleName();

    private static final int INFO_WINDOW_IMAGE_SIZE = 50;
    private static final int INFO_WINDOW_PADDING = 10;

    public static final class Extras {
        public static final String BIRD = "bird";
    }

    private RemoteSighting mSelectedBird;
    private ArrayList<RemoteSighting> mBirds;
    private HashMap<LatLng, ArrayList<RemoteSighting>> mLocationBirdsMap;
    private HashMap<Marker, ArrayList<RemoteSighting>> mMarkerBirdsMap;

    private GoogleMap mMap;
    private Marker mSelectedMarker;

    private PopulateBirdImagesTask mPopulateImagesTask;
    private AsyncTask<ArrayList<RemoteBirdImage>,Void,Void> mLoadImagesTask;

    private GoogleMap.OnMarkerClickListener mOnMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(final Marker marker) {

            mSelectedMarker = marker;

            ArrayList<RemoteSighting> birds = mMarkerBirdsMap.get(marker);

            if( birds != null ) {
                ArrayList<RemoteBirdImage> birdImages = new ArrayList<RemoteBirdImage>();

                for (int i = 0; i < birds.size() && birdImages.size() < 5; ++i) {
                    RemoteSighting bird = birds.get(i);

                    if (bird.getImages().size() > 0)
                        birdImages.add(bird.getImages().get(0));
                }

                if( mLoadImagesTask != null )
                {
                    mLoadImagesTask.cancel(true);
                    mLoadImagesTask = null;
                }

                mLoadImagesTask = new AsyncTask<ArrayList<RemoteBirdImage>,Void,Void>()
                {
                    @Override
                    protected Void doInBackground(ArrayList<RemoteBirdImage>... params) {

                        ArrayList<RemoteBirdImage> birdImages = params[0];

                        for( RemoteBirdImage image : birdImages )
                        {
                            ImageSize targetSize = new ImageSize(UiUtil.convertDpToPixels(50,BirdMapActivity.this), UiUtil.convertDpToPixels(50,BirdMapActivity.this));
                            BAMApplication.getImageLoader().loadImageSync(image.getImageUrl(), targetSize);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        marker.hideInfoWindow();
                        marker.showInfoWindow();
                    }
                };

                mLoadImagesTask.execute(birdImages);



            }

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
        mMarkerBirdsMap = new HashMap<Marker, ArrayList<RemoteSighting>>();

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(mOnMarkerClickListener);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                ArrayList<RemoteSighting> birds = mMarkerBirdsMap.get(marker);

                if( birds != null )
                {
                    View view = getLayoutInflater().inflate(R.layout.info_window, null);
                    ImageView iv1 = (ImageView)view.findViewById(R.id.info_window_iv1);
                    ImageView iv2 = (ImageView)view.findViewById(R.id.info_window_iv2);
                    ImageView iv3= (ImageView)view.findViewById(R.id.info_window_iv3);
                    ImageView iv4 = (ImageView)view.findViewById(R.id.info_window_iv4);
                    ImageView iv5 = (ImageView)view.findViewById(R.id.info_window_iv5);

                    ArrayList<RemoteBirdImage> birdImages = new ArrayList<RemoteBirdImage>();

                    for( int i=0; i < birds.size() && birdImages.size() < 5; ++i )
                    {
                        RemoteSighting bird = birds.get(i);

                        if( bird.getImages().size() > 0 )
                            birdImages.add(bird.getImages().get(0));
                    }

                    if( birdImages.size() > 0 )
                    {
                        iv1.setVisibility(View.VISIBLE);
                        BAMApplication.getImageLoader().displayImage(birdImages.get(0).getImageUrl(), iv1);

                        if( birdImages.size() > 1 )
                        {
                            iv2.setVisibility(View.VISIBLE);
                            BAMApplication.getImageLoader().displayImage(birdImages.get(1).getImageUrl(), iv2);

                            if( birdImages.size() > 2)
                            {
                                iv3.setVisibility(View.VISIBLE);
                                BAMApplication.getImageLoader().displayImage(birdImages.get(2).getImageUrl(), iv3);

                                if( birdImages.size() > 3 )
                                {
                                    iv4.setVisibility(View.VISIBLE);
                                    BAMApplication.getImageLoader().displayImage(birdImages.get(3).getImageUrl(), iv4);

                                    if( birdImages.size() > 4 )
                                    {
                                    }
                                }
                            }

                        }
                    }

                    view.setLayoutParams(new LinearLayout.LayoutParams(UiUtil.convertDpToPixels(INFO_WINDOW_IMAGE_SIZE*5 + 2*INFO_WINDOW_PADDING, BirdMapActivity.this), LinearLayout.LayoutParams.WRAP_CONTENT));


                    return view;
                }

                return null;

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportLoaderManager().initLoader(BAMConstants.SIGHTING_LOADER_ID, null, this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if( mPopulateImagesTask != null )
        {
            mPopulateImagesTask.cancel(true);
            mPopulateImagesTask = null;
        }

        if( mLoadImagesTask != null )
        {
            mLoadImagesTask.cancel(true);
            mLoadImagesTask = null;
        }
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



        // We're going to need images for the markers
        if( mPopulateImagesTask != null )
            mPopulateImagesTask.cancel(true);

        mPopulateImagesTask = new PopulateBirdImagesTask()
        {
            @Override
            protected void onPostExecute(List<RemoteSighting> remoteSightings) {
                super.onPostExecute(remoteSightings);

                if( mSelectedMarker != null ) {
                    // refresh
                    mSelectedMarker.hideInfoWindow();
                    mSelectedMarker.showInfoWindow();
                }
            }
        };
        mPopulateImagesTask.execute(mBirds);


        // Now let's create the markers
        Marker marker;
        MarkerOptions markerOptions;
        LatLng location;
        boolean isSelectedMarker = false;

        mLocationBirdsMap.clear();
        mMarkerBirdsMap.clear();

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

            if( !mMarkerBirdsMap.containsKey(marker) )
                mMarkerBirdsMap.put(marker, entry.getValue());

            if( isSelectedMarker ) {
                mSelectedMarker = marker;
                marker.showInfoWindow();
                isSelectedMarker = false;
            }
        }


    }
}
