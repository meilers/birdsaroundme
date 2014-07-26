package com.sobremesa.birdwatching.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.R;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;


/**
 * Created by Michael on 2014-03-26.
 */
public class BirdImageFragment extends Fragment {


    private static final class Extras {
        public static final String IMAGE_URL = "mImageUrl";
    }

    private RemoteBirdImage mImage;

    public static final BirdImageFragment newInstance(RemoteBirdImage image)  {
        BirdImageFragment f = new BirdImageFragment();

        Bundle args = f.getArguments();
        if (args == null) {
            args = new Bundle();
            args.putParcelable(Extras.IMAGE_URL, image);

        }

        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImage = getArguments().getParcelable(Extras.IMAGE_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bird_image, container, false);

        ImageView iv = (ImageView)view.findViewById(R.id.fragment_bird_image_iv);


        if (mImage.getImageUrl() != null)
            BAMApplication.getImageLoader().displayImage(mImage.getImageUrl(), iv);
        else
            iv.setImageDrawable(BAMApplication.getContext().getResources().getDrawable(R.drawable.default_bird));

        return view;
    }
}