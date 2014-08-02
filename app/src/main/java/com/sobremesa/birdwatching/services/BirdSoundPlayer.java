package com.sobremesa.birdwatching.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.models.SoundStateType;
import com.sobremesa.birdwatching.models.remote.RemoteBirdSound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by omegatai on 2014-08-01.
 */
public class BirdSoundPlayer extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    public final static class Extras
    {
        // in
        public static final String BIRD_SOUNDS = "birdSounds";

        // out
        public static final String BROADCAST_PLAYING_SOUND_ID = "broadcastPlayingSoundId";
        public static final String BROADCAST_PLAY_STATE = "broadcastPlayState";
    }

    public final static class Actions
    {
        // in
        public static final String ACTION_PLAY = "com.sobremesa.birdwatching.services.PLAY";
        public static final String ACTION_PAUSE = "com.sobremesa.birdwatching.services.PAUSE";

        // out
        public static final String ACTION_BROADCAST_PLAY_STATE = "com.sobremesa.birdwatching.services.BROADCAST_PLAY_STATE";
    }


    private ConcurrentLinkedQueue<RemoteBirdSound> mTrackQueue;
    private MediaPlayer mMediaPlayer;

    private String mPlayingBirdSoundId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTrackQueue = new ConcurrentLinkedQueue<RemoteBirdSound>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (Actions.ACTION_PLAY.equals(action)) {
            ArrayList<RemoteBirdSound> sounds = intent.getExtras().getParcelableArrayList(Extras.BIRD_SOUNDS);

            for( RemoteBirdSound sound : sounds )
            {
                addTrackToQueue(sound);
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Broadcast playing event
        Intent broadcastIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putString(Extras.BROADCAST_PLAYING_SOUND_ID, mPlayingBirdSoundId);
        extras.putInt(Extras.BROADCAST_PLAY_STATE, SoundStateType.STOPPED.ordinal());
        broadcastIntent.setAction(Actions.ACTION_BROADCAST_PLAY_STATE);
        broadcastIntent.putExtras(extras);
        sendBroadcast(broadcastIntent);


        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }



    /**
     * Add track to end of queue if already playing,
     * otherwise create a new MediaPlayer and start playing.
     */
    private synchronized void addTrackToQueue(final RemoteBirdSound sound) {


        new AsyncTask<RemoteBirdSound,Void,Void>() {

            @Override
            protected Void doInBackground(RemoteBirdSound... params) {

                Uri uri;
                RemoteBirdSound sound = params[0];

                if (mMediaPlayer == null) {
                    mPlayingBirdSoundId = sound.getBirdSoundId();

                    // Broadcast buffering event
                    Log.d("sending broad", "cast");

                    Intent broadcastIntent = new Intent();
                    Bundle extras = new Bundle();
                    extras.putString(Extras.BROADCAST_PLAYING_SOUND_ID, mPlayingBirdSoundId);
                    extras.putInt(Extras.BROADCAST_PLAY_STATE, SoundStateType.BUFFERING.ordinal());
                    broadcastIntent.setAction(Actions.ACTION_BROADCAST_PLAY_STATE);
                    broadcastIntent.putExtras(extras);
                    sendBroadcast(broadcastIntent);


                    uri = Uri.parse(sound.getFile());
                    mMediaPlayer = MediaPlayer.create(BirdSoundPlayer.this, uri);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setOnCompletionListener(BirdSoundPlayer.this);
                    mMediaPlayer.setOnBufferingUpdateListener(BirdSoundPlayer.this);
                    mMediaPlayer.start();

                } else {
                    mTrackQueue.offer(params[0]);
                }

                return null;
            }
        }.execute(sound);

    }



    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // Broadcast playing event
        Intent broadcastIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putString(Extras.BROADCAST_PLAYING_SOUND_ID, mPlayingBirdSoundId);
        extras.putInt(Extras.BROADCAST_PLAY_STATE, SoundStateType.PLAYING.ordinal());
        broadcastIntent.setAction(Actions.ACTION_BROADCAST_PLAY_STATE);
        broadcastIntent.putExtras(extras);
        sendBroadcast(broadcastIntent);
    }


    // Track completed, start playing next or stop service...
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        // Broadcast end event
        Intent broadcastIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putString(Extras.BROADCAST_PLAYING_SOUND_ID, mPlayingBirdSoundId);
        extras.putInt(Extras.BROADCAST_PLAY_STATE, SoundStateType.STOPPED.ordinal());
        broadcastIntent.setAction(Actions.ACTION_BROADCAST_PLAY_STATE);
        broadcastIntent.putExtras(extras);
        sendBroadcast(broadcastIntent);

        // Start next song
        mediaPlayer.reset();
        RemoteBirdSound nextTrackUri = mTrackQueue.poll();

        if (nextTrackUri != null) {
            new AsyncTask<RemoteBirdSound, Void, Void>() {

                @Override
                protected Void doInBackground(RemoteBirdSound... params) {

                    Uri uri;
                    RemoteBirdSound sound = params[0];

                    try {
                        mPlayingBirdSoundId = sound.getBirdSoundId();

                        // Broadcast buffering event
                        Intent broadcastIntent = new Intent();
                        Bundle extras = new Bundle();
                        extras.putString(Extras.BROADCAST_PLAYING_SOUND_ID, mPlayingBirdSoundId);
                        extras.putInt(Extras.BROADCAST_PLAY_STATE, SoundStateType.BUFFERING.ordinal());
                        broadcastIntent.setAction(Actions.ACTION_BROADCAST_PLAY_STATE);
                        broadcastIntent.putExtras(extras);
                        sendBroadcast(broadcastIntent);


                        uri = Uri.parse(sound.getFile());
                        mMediaPlayer.setDataSource(BirdSoundPlayer.this, uri);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                    } catch (IOException e) {
                        Log.d("stoping", "stopping1");
                        stopSelf();
                    }

                    return null;
                }
            }.execute(nextTrackUri);
        } else {
            Log.d("stoping", "stopping2");
            stopSelf();
        }

    }
}
