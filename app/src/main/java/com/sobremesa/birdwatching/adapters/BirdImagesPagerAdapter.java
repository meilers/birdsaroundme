package com.sobremesa.birdwatching.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sobremesa.birdwatching.fragments.BirdImageFragment;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;

import java.util.List;

/**
 * Created by omegatai on 2014-07-25.
 */
public class BirdImagesPagerAdapter extends FragmentStatePagerAdapter {
    List<RemoteBirdImage> mImages;

    public BirdImagesPagerAdapter(FragmentManager fragmentManager, List<RemoteBirdImage> images) {
        super(fragmentManager);
        mImages = images;
    }

    @Override
    public Fragment getItem(int position) {

        return BirdImageFragment.newInstance(mImages.get(position));
    }

    @Override
    public int getCount() {
        return mImages.size();

    }

}