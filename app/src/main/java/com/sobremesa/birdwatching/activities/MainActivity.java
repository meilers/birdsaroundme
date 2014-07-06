package com.sobremesa.birdwatching.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.fragments.BirdsFragment;
import com.sobremesa.birdwatching.tasks.DownloadSightingsTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    private GoogleApiClient.ConnectionCallbacks mApiClient1 = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnectionSuspended(int cause) {
            Log.i(TAG, "GoogleApiClient connection suspended");
            retryConnecting();

        }

        @Override
        public void onConnected(Bundle connectionHint) {
        }
    };

    private GoogleApiClient.OnConnectionFailedListener mApiClient2 = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
            if (!result.hasResolution()) {
                // Show a localized error dialog.
                GooglePlayServicesUtil.getErrorDialog(
                        result.getErrorCode(), MainActivity.this, 0, new OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                retryConnecting();
                            }
                        }).show();
                return;
            }
            // If there is an existing resolution error being displayed or a resolution
            // activity has started before, do nothing and wait for resolution
            // progress to be completed.
            if (mIsInResolution) {
                return;
            }
            mIsInResolution = true;
            try {
                result.startResolutionForResult(MainActivity.this, REQUEST_CODE_RESOLUTION);
            } catch (SendIntentException e) {
                Log.e(TAG, "Exception while starting resolution activity", e);
                retryConnecting();
            }
        }
    };

    private GooglePlayServicesClient.ConnectionCallbacks mServicesClient1 = new GooglePlayServicesClient.ConnectionCallbacks() {
        @Override
        public void onDisconnected() {
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            retrieveLocation();
        }
    };

    private GooglePlayServicesClient.OnConnectionFailedListener mServicesClient2 = new GooglePlayServicesClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.d("retrieve location", "ouaip");
        }
    };

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Google Services client.
     */
    private LocationClient mLocationClient;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    private Location mLocation;


    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }

        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, BirdsFragment.newInstance(), BirdsFragment.TAG).commit();
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClient == null) {
            mGoogleApiClient =  new GoogleApiClient.Builder(this, mApiClient1, mApiClient2)
                    // Optionally, add additional APIs and scopes if required.
                    // builder.addApi(...).addScope(...);
                    .build();
        }

        if( mLocationClient == null )
            mLocationClient = new LocationClient(this, mServicesClient1, mServicesClient2);

        mGoogleApiClient.connect();
        mLocationClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        if( mLocationClient != null )
            mLocationClient.disconnect();

        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    private void retrieveLocation()
    {
        Log.d("retrieve location", "ouaip");

        mLocation = mLocationClient.getLastLocation();
        locationUpdated();
    }

    private void locationUpdated()
    {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);

            if (addresses.size() > 0) {
                String city = addresses.get(0).getLocality();
                getActionBar().setSubtitle(city + ", lat: " + String.format("%.2f", mLocation.getLatitude()) + ", lng: " + String.format("%.2f", mLocation.getLongitude()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        if( isNetworkConnected() )
            new DownloadSightingsTask().execute(mLocation.getLatitude(), mLocation.getLongitude());
            //fetchRecordings(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()));
        else
            showConnectionAlert();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public void showConnectionAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("No Internet Connection");

        // Setting Dialog Message
        alertDialog.setMessage("You need an internet connection to see the latest birds.");

        // On pressing Settings button
//        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });


        // Showing Alert Message
        alertDialog.show();
    }
}
