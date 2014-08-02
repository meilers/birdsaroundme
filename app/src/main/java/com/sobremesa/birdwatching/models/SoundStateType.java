package com.sobremesa.birdwatching.models;

/**
 * Created by omegatai on 2014-07-09.
 */
public enum SoundStateType {
    STOPPED(0), PLAYING(1), BUFFERING(2), PAUSED(3);
    private final int value;

    private SoundStateType(int value) {
        this.value = value;
    }
}