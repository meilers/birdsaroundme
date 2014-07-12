package com.sobremesa.birdwatching.models;

/**
 * Created by omegatai on 2014-07-09.
 */
public enum SortByType {
    DATE(0), NAME(1), DISTANCE(2);
    private final int value;

    private SortByType(int value) {
        this.value = value;
    }
}