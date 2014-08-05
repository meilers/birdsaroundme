package com.sobremesa.birdwatching.models;

/**
 * Created by omegatai on 2014-07-09.
 */
public enum DateType {
    THIRTY_DAYS(0), SEVEN_DAYS(1), ONE_DAY(2) ;
    private final int value;

    private DateType(int value) {
        this.value = value;
    }
}