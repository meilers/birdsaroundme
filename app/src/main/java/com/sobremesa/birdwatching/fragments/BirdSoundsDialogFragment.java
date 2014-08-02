package com.sobremesa.birdwatching.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.adapters.BirdSoundsAdapter;
import com.sobremesa.birdwatching.database.BirdSoundTable;
import com.sobremesa.birdwatching.models.remote.RemoteBirdSound;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        Dialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();

        getActivity().getSupportLoaderManager().initLoader(BAMConstants.BIRD_SOUND_LOADER_ID, null, this);
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
