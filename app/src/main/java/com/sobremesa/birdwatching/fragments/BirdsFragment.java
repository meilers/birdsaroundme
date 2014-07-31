package com.sobremesa.birdwatching.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.activities.BirdActivity;
import com.sobremesa.birdwatching.activities.MainActivity;
import com.sobremesa.birdwatching.adapters.BirdsAdapter;
import com.sobremesa.birdwatching.adapters.SortByAdapter;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.listeners.LocationListener;
import com.sobremesa.birdwatching.listeners.SettingsListener;
import com.sobremesa.birdwatching.managers.LocationManager;
import com.sobremesa.birdwatching.managers.SettingsManager;
import com.sobremesa.birdwatching.models.Settings;
import com.sobremesa.birdwatching.models.SortByType;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.services.BirdImageService;
import com.sobremesa.birdwatching.tasks.DownloadSightingsTask;
import com.sobremesa.birdwatching.tasks.PopulateBirdImagesTask;
import com.sobremesa.birdwatching.util.AnalyticsUtil;
import com.sobremesa.birdwatching.views.SoftKeyboardHandledLinearLayout;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class BirdsFragment extends Fragment implements LoaderCallbacks<Cursor>, LocationListener, SettingsListener {

	public static final String TAG = BirdsFragment.class.getSimpleName();

    private ArrayList<RemoteSighting> mFullBirds;

    private ArrayList<RemoteSighting> mBirds;
    private HashMap<String, RemoteSighting> mBirdMap;
    private HashMap<String, Integer> mBirdPositionMap;

	private CursorLoader mCusorLoader;

    private BirdsAdapter mBirdsAdapter;
    private GridView mBirdsGv;

    // Search
    private MenuItem mSearchMenuItem;
    private SearchView mSearchView;
    private String mSearchStr = "";

    private DownloadSightingsTask mDownloadSightingsTask;
    private PopulateBirdImagesTask mPopulateImagesTask;

    private AlertDialog mAlertDialog;

    private final BroadcastReceiver mImageBirdReceiver = new BroadcastReceiver() {

        private boolean mIsUpdateGv;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BAMConstants.RELOAD_BIRD_IMAGES_BROADCAST_ACTION)){

                ArrayList<RemoteSighting> birdsComputed = intent.getExtras().getParcelableArrayList(BAMConstants.RELOAD_BIRD_IMAGES_BROADCAST_EXTRA);

                if( birdsComputed != null )
                {
                    setBirdImages(birdsComputed);


                    mIsUpdateGv = false;
                    int firstPosition = mBirdsGv.getFirstVisiblePosition();
                    int lastPosition = mBirdsGv.getLastVisiblePosition();
                    Integer position = 0;

                    for( RemoteSighting bird : birdsComputed )
                    {
                        position = mBirdPositionMap.get(bird.getSciName());

                        if( position != null )
                        {
                            if( position >= firstPosition && position < lastPosition )
                                mIsUpdateGv = true;
                        }
                    }

                    BAMApplication.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if( mIsUpdateGv ) {

                                mBirdsAdapter.notifyDataSetChanged();

                            }
                        }
                    });
                }

            }
        }
    };


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

        mFullBirds = new ArrayList<RemoteSighting>();
        mBirds = new ArrayList<RemoteSighting>();
        mBirdMap = new HashMap<String, RemoteSighting>();
        mBirdPositionMap = new HashMap<String, Integer>();

        mBirdsAdapter = new BirdsAdapter(getActivity(), mBirds);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_birds, container, false);

        mBirdsGv = (GridView)view.findViewById(R.id.bird_gv);
        mBirdsGv.setAdapter(mBirdsAdapter);
        mBirdsGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RemoteSighting bird = mBirds.get(position);

                Intent intent = new Intent(getActivity(), BirdActivity.class);
                intent.putExtra(BirdActivity.Extras.BIRD, bird);
                startActivity(intent);
            }
        });

        setHasOptionsMenu(true);

		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BAMConstants.RELOAD_BIRD_IMAGES_BROADCAST_ACTION);
        getActivity().registerReceiver(mImageBirdReceiver, filter);

		getLoaderManager().initLoader(BAMConstants.SIGHTING_LOADER_ID, null, this);

        syncBirds();


        LocationManager.INSTANCE.addLocationListener(this);
        SettingsManager.INSTANCE.addSettingsListener(this);

        AnalyticsUtil.sendView(TAG);
	}

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(mImageBirdReceiver);


        if( mDownloadSightingsTask != null )
        {
            mDownloadSightingsTask.cancel(true);
            mDownloadSightingsTask = null;
        }

        if( mPopulateImagesTask != null )
        {
            mPopulateImagesTask.cancel(true);
            mPopulateImagesTask = null;
        }

        LocationManager.INSTANCE.removeLocationListener(this);
        SettingsManager.INSTANCE.removeSettingsListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.birds, menu);

        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();

        // Image
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView searchIv = (ImageView) mSearchView.findViewById(searchImgId);
        searchIv.setImageResource(R.drawable.ic_action_search);

        // Text
        int searchEtId = getResources().getIdentifier("android:id/search_src_text", null, null);
        final EditText searchEt = (EditText) mSearchView.findViewById(searchEtId);
        searchEt.setTextColor(getResources().getColor(android.R.color.black));
        searchEt.setHintTextColor(getResources().getColor(android.R.color.darker_gray));
        searchEt.setHint("Enter common name");

        mSearchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchStr = query;
                updateGridView(mSearchStr);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mSearchStr = newText;
                updateGridView(mSearchStr);
                return false;
            }
        });

        mSearchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchStr = "";
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSearchStr = "";
                updateGridView(mSearchStr);
                return true;
            }
        });

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if(!queryTextFocused) {
                    mSearchMenuItem.collapseActionView();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId() )
        {
            case R.id.action_search:

//                if( mSearchMenuItem != null )
//                    mSearchMenuItem.collapseActionView();
                break;

            case R.id.action_settings:
                showSortByDialog();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    private void syncBirds()
    {
        Location location = LocationManager.INSTANCE.getLocation();

        if( location != null )
        {
            mDownloadSightingsTask = new DownloadSightingsTask()
            {
                @Override
                protected void onPostExecute(ArrayList<RemoteSighting> remoteSightings) {
                    super.onPostExecute(remoteSightings);

                    if( remoteSightings != null ) {
                        syncBirdImages(remoteSightings);

                        AnalyticsUtil.sendEvent(TAG, AnalyticsUtil.Categories.SIGHTINGS, AnalyticsUtil.Actions.SYNC, "Number of sightings downloaded: " + remoteSightings.size() );
                    }
                    else
                        AnalyticsUtil.sendEvent(TAG, AnalyticsUtil.Categories.SIGHTINGS, AnalyticsUtil.Actions.SYNC, "Error: Sightings result is null" );




                }
            };
            mDownloadSightingsTask.execute(location.getLatitude(), location.getLongitude());
        }
    }

    private void syncBirdImages(ArrayList<RemoteSighting> birds)
    {
        if( getActivity() != null ) {
            final Intent intent = new Intent(getActivity(), BirdImageService.class);
            intent.putParcelableArrayListExtra(BirdImageService.Extras.BIRDS, birds);
            intent.setAction(Intent.ACTION_SYNC);

            getActivity().startService(intent);
        }
    }

    private synchronized void setBirdImages(List<RemoteSighting> birds)
    {
        for( RemoteSighting bird : birds )
        {
            RemoteSighting birdInGridView = mBirdMap.get(bird.getSciName());

            if( birdInGridView != null )
                birdInGridView.setImages(bird.getImages());
        }
    }




    @Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		mCusorLoader = new CursorLoader(getActivity(), BAMContentProvider.Uris.SIGHTINGS_GROUP_BY_BIRD_URI, SightingTable.ALL_COLUMNS, null, null, SightingTable.OBS_DT + " DESC");

		return mCusorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {

        if( cursor != null )
        {

            mFullBirds.clear();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                RemoteSighting bird = new RemoteSighting(cursor);
                mFullBirds.add(bird);
            }


            // Update Grid View
            updateGridView(mSearchStr);

            // Update Title
            MainActivity act = (MainActivity)getActivity();

            if( act != null )
                act.setTitle(cursor.getCount() + " Birds (within 50 km)");

        }


	}

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void updateGridView(String searchStr)
    {
        mBirds.clear();
        mBirdMap.clear();
        mBirdPositionMap.clear();

        int i = 0;

        for( RemoteSighting bird : mFullBirds )
        {
            if( searchStr == null || searchStr.isEmpty() || bird.getComName().toLowerCase().contains(searchStr.toLowerCase())) {
                mBirds.add(bird);
                mBirdMap.put(bird.getSciName(), bird);
                mBirdPositionMap.put(bird.getSciName(), i);

                ++i;
            }
        }

        switch (SettingsManager.INSTANCE.getSettings().getSortBy())
        {
            case DATE:
                Collections.sort(mBirds, new RemoteSighting.DateComparator());
                break;

            case NAME:
                Collections.sort(mBirds, new RemoteSighting.NameComparator());
                break;

            case DISTANCE:
                Collections.sort(mBirds, new RemoteSighting.DistanceComparator());
                break;
        }

        // Update right away if we're searching
        if( searchStr != null || !searchStr.isEmpty() )
            mBirdsAdapter.notifyDataSetChanged();


        // Populate Existing Images
        if( mPopulateImagesTask != null )
            mPopulateImagesTask.cancel(true);

        mPopulateImagesTask = new PopulateBirdImagesTask()
        {
            @Override
            protected void onPostExecute(List<RemoteSighting> birds) {
                super.onPostExecute(birds);

                if( birds != null )
                {
                    mBirdsAdapter.notifyDataSetChanged();
                }

            }
        };

        mPopulateImagesTask.execute(mBirds);
    }

    private void showSortByDialog()
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_list_selection, null);

        // ListView
        final ListView lv = (ListView) view.findViewById(R.id.dialog_list_selection_lv);

        Resources r = getActivity().getResources();
        String[] sortBys = r.getStringArray(R.array.sort_bys_array);

        final SortByAdapter adapter = new SortByAdapter(getActivity(), R.layout.list_item_dialog_list_selection, sortBys );
        adapter.setSelectedIndex(SettingsManager.INSTANCE.getSettings().getSortBy().ordinal());


        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                adapter.setSelectedIndex(position);
                adapter.notifyDataSetChanged();

                Settings set = SettingsManager.INSTANCE.getSettings();

                final SortByType sort = SortByType.values()[position];
                switch( sort )
                {
                    case DISTANCE:
                        set.setSortBy(SortByType.DISTANCE);
                        break;

                    case NAME:
                        set.setSortBy(SortByType.NAME);
                        break;

                    case DATE:
                        set.setSortBy(SortByType.DATE);
                        break;

                    default:
                        set.setSortBy(SortByType.DISTANCE);
                        break;
                }

                SettingsManager.INSTANCE.setSettings(set);


                mAlertDialog.dismiss();
            }
        });

        TextView dialogTitleTv = (TextView) view.findViewById(R.id.dialog_list_selection_title_tv);
        dialogTitleTv.setText("Sort By");

        if (this.getActivity().getWindow() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(view);
            mAlertDialog = builder.create();

            mAlertDialog.show();
        }
    }




    // EVENTS

    @Override
    public void locationEventReceived() {


        syncBirds();
    }



    @Override
    public void settingsEventReceived() {
        switch (SettingsManager.INSTANCE.getSettings().getSortBy())
        {
            case DATE:
                Collections.sort(mBirds, new RemoteSighting.DateComparator());
                break;

            case NAME:
                Collections.sort(mBirds, new RemoteSighting.NameComparator());
                break;

            case DISTANCE:
                Collections.sort(mBirds, new RemoteSighting.DistanceComparator());
                break;
        }


        mBirdsAdapter.notifyDataSetChanged();
    }



}
