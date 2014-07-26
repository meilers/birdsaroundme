package com.sobremesa.birdwatching.tasks;

import android.database.Cursor;
import android.os.AsyncTask;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.database.BirdImageTable;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.providers.BAMContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 2014-07-07.
 */
public class PopulateBirdImagesTask extends AsyncTask<List<RemoteSighting>, Void, List<RemoteSighting>> {
    @Override
    protected List<RemoteSighting> doInBackground(List<RemoteSighting>... params) {

        try {
            List<RemoteSighting> birds = params[0];

            for (RemoteSighting bird : birds) {
                populateBirdImages(bird);
            }

            return birds;
        }
        catch (Exception e) {}


        return null;
    }

    private void populateBirdImages(RemoteSighting bird)
    {
        ArrayList<RemoteBirdImage> birdImages = new ArrayList<RemoteBirdImage>();
        Cursor imageCursor = BAMApplication.getContext().getContentResolver().query(BAMContentProvider.Uris.BIRD_IMAGES_URI, BirdImageTable.ALL_COLUMNS, BirdImageTable.SCI_NAME + "=?", new String[] { bird.getSciName()}, BirdImageTable.POSITION);

        if( imageCursor != null ) {
            for (imageCursor.moveToFirst(); !imageCursor.isAfterLast(); imageCursor.moveToNext()) {
                RemoteBirdImage image = new RemoteBirdImage(imageCursor);
                birdImages.add(image);
            }
        }

        imageCursor.close();

        bird.setImages(birdImages);
    }
}
