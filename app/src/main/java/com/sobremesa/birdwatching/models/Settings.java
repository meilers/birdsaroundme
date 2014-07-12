package com.sobremesa.birdwatching.models;

/**
 * Created by omegatai on 2014-07-09.
 */
public class Settings extends BaseModel {


    private SortByType mSortBy;

    public SortByType getSortBy() {
        return mSortBy;
    }

    public void setSortBy(SortByType mSortBy) {
        this.mSortBy = mSortBy;
    }

}
