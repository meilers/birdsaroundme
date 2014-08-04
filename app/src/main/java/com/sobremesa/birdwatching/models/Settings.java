package com.sobremesa.birdwatching.models;

/**
 * Created by omegatai on 2014-07-09.
 */
public class Settings extends ModelObject {

    private DistanceType mDistance;
    private SortByType mSortBy;

    public DistanceType getDistance() {
        return mDistance;
    }

    public void setDistance(DistanceType mDistance) {
        this.mDistance = mDistance;
    }

    public SortByType getSortBy() {
        return mSortBy;
    }

    public void setSortBy(SortByType mSortBy) {
        this.mSortBy = mSortBy;
    }



}
