package com.sobremesa.birdwatching.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.adapters.BirdSoundsAdapter;
import com.sobremesa.birdwatching.database.BirdSoundTable;
import com.sobremesa.birdwatching.models.SoundStateType;
import com.sobremesa.birdwatching.models.remote.RemoteBirdSound;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;
import com.sobremesa.birdwatching.services.BirdSoundPlayerService;

import java.util.ArrayList;

/**
 * Created by omegatai on 2014-07-31.
 */
public class BirdSoundsDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final class Extras {
        public static final String BIRD = "bird";
    }

    private TextView mTitleTv;
    private TextView mSubtitleTv;
    private ListView mBirdSoundsLv;

    private RemoteSighting mBird;
    private ArrayList<RemoteBirdSound> mBirdSounds;

    private BirdSoundsAdapter mBirdSoundsAdapter;

    private String mPlayingSoundId;
    private SoundStateType mPlayingSoundStateType;

    private final BroadcastReceiver mBirdSoundStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BirdSoundPlayerService.Actions.ACTION_BROADCAST_PLAY_STATE)){

                mPlayingSoundId = intent.getExtras().getString(BirdSoundPlayerService.Extras.BROADCAST_PLAYING_SOUND_ID);
                mPlayingSoundStateType = SoundStateType.values()[intent.getExtras().getInt(BirdSoundPlayerService.Extras.BROADCAST_PLAY_STATE)];

                if( mPlayingSoundStateType != SoundStateType.PLAYING && mPlayingSoundStateType != SoundStateType.BUFFERING )
                    mPlayingSoundId = null;

                // Update Lv
                if( mBirdSoundsAdapter != null )
                {
                    mBirdSoundsAdapter.setPlayingSound(mPlayingSoundId, mPlayingSoundStateType);
                    mBirdSoundsAdapter.notifyDataSetChanged();
                }

            }
        }
    };

    public static final BirdSoundsDialogFragment newInstance(RemoteSighting bird)  {
        BirdSoundsDialogFragment f = new BirdSoundsDialogFragment();

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
        mBirdSounds = new ArrayList<RemoteBirdSound>();

        mBirdSoundsAdapter = new BirdSoundsAdapter(getActivity(), mBirdSounds);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_bird_sounds, null);

        mTitleTv = (TextView)view.findViewById(R.id.dialog_fragment_bird_title_tv);
        mSubtitleTv = (TextView)view.findViewById(R.id.dialog_fragment_bird_subtitle_tv);

        mTitleTv.setText(mBird.getComName());

        mBirdSoundsLv = (ListView)view.findViewById(R.id.dialog_fragment_bird_sounds_lv);
        mBirdSoundsLv.setAdapter(mBirdSoundsAdapter);
        mBirdSoundsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if( getActivity() != null ) {


                    ArrayList<RemoteBirdSound> soundsQueued = new ArrayList<RemoteBirdSound>(mBirdSounds.subList(position, mBirdSounds.size()));

                    RemoteBirdSound sound = soundsQueued.get(0);
                    final Intent intent = new Intent(getActivity(), BirdSoundPlayerService.class);
                    getActivity().stopService(intent);

                    if( !sound.getBirdSoundId().equals(mPlayingSoundId))
                    {
                        Log.d("starting SERVICE", "fdsfsd");
                        intent.putParcelableArrayListExtra(BirdSoundPlayerService.Extras.BIRD_SOUNDS, soundsQueued);
                        intent.setAction(BirdSoundPlayerService.Actions.ACTION_PLAY);
                        getActivity().startService(intent);
                    }

                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final Intent intent = new Intent(getActivity(), BirdSoundPlayerService.class);
        getActivity().stopService(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BirdSoundPlayerService.Actions.ACTION_BROADCAST_PLAY_STATE);
        getActivity().registerReceiver(mBirdSoundStateReceiver, filter);

        getActivity().getSupportLoaderManager().initLoader(BAMConstants.BIRD_SOUND_LOADER_ID, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(mBirdSoundStateReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case BAMConstants.BIRD_SOUND_LOADER_ID:
                return new CursorLoader(getActivity(), BAMContentProvider.Uris.BIRD_SOUNDS_URI, BirdSoundTable.ALL_COLUMNS, BirdSoundTable.COM_NAME + "=?", new String[]{mBird.getComName()}, null);
        }

        return  null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            case BAMConstants.BIRD_SOUND_LOADER_ID:
                if( cursor != null )
                {
                    RemoteBirdSound sound;
                    mBirdSounds.clear();

                    for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() )
                    {
                        sound = new RemoteBirdSound(cursor);
                        mBirdSounds.add(sound);
                    }

                    mBirdSoundsAdapter.notifyDataSetChanged();

                    mSubtitleTv.setText(cursor.getCount() + " Bird Sounds");
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
