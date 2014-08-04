package com.sobremesa.birdwatching.models;

/**
 * Created by omegatai on 2014-07-09.
 */
public enum DistanceType {
    FIFTY_KM(0), TWENTY_KM(1), FIVE_KM(2);
    private final int value;

    private DistanceType(int value) {
        this.value = value;
    }
}