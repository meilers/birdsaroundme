package com.sobremesa.birdwatching.util;

import android.database.Cursor;


import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.models.remote.RemoteBirdDescription;
import com.sobremesa.birdwatching.models.remote.RemoteBirdImage;
import com.sobremesa.birdwatching.models.remote.RemoteBirdSound;
import com.sobremesa.birdwatching.models.remote.RemoteSighting;
import com.sobremesa.birdwatching.synchronizers.BaseSynchronizer;
import com.sobremesa.birdwatching.synchronizers.preprocessors.BasePreProcessor;

import java.util.List;

/**
 * Created by Michael on 2014-04-07.
 */
public class SyncUtil {

    public static void synchronizeRemoteSightings(List<RemoteSighting> remoteSightings, Cursor localSightings, int remoteIdentifierColumn, int remoteIdentifierColumn2, int remoteIdentifierColumn3, BaseSynchronizer<RemoteSighting> synchronizer,BasePreProcessor<RemoteSighting> preProcessor) {

        if( preProcessor != null )
            preProcessor.preProcessRemoteRecords(remoteSightings);

        synchronizer.synchronize(BAMApplication.getContext(), remoteSightings, localSightings, remoteIdentifierColumn, remoteIdentifierColumn2, remoteIdentifierColumn3);
    }

    public static void synchronizeRemoteBirdImages(List<RemoteBirdImage> remoteBirdImages, Cursor localBirdImages, int remoteIdentifierColumn, BaseSynchronizer<RemoteBirdImage> synchronizer,BasePreProcessor<RemoteBirdImage> preProcessor) {

        if( preProcessor != null )
            preProcessor.preProcessRemoteRecords(remoteBirdImages);

        synchronizer.synchronize(BAMApplication.getContext(), remoteBirdImages, localBirdImages, remoteIdentifierColumn);
    }

    public static void synchronizeRemoteBirdDescriptions(List<RemoteBirdDescription> remoteBirdDescriptions, Cursor localBirdDescriptions, int remoteIdentifierColumn, BaseSynchronizer<RemoteBirdDescription> synchronizer,BasePreProcessor<RemoteBirdDescription> preProcessor) {

        if( preProcessor != null )
            preProcessor.preProcessRemoteRecords(remoteBirdDescriptions);

        synchronizer.synchronize(BAMApplication.getContext(), remoteBirdDescriptions, localBirdDescriptions, remoteIdentifierColumn);
    }

    public static void synchronizeRemoteBirdSounds(List<RemoteBirdSound> remoteBirdSounds, Cursor localBirdSounds, int remoteIdentifierColumn, BaseSynchronizer<RemoteBirdSound> synchronizer,BasePreProcessor<RemoteBirdSound> preProcessor) {

        if( preProcessor != null )
            preProcessor.preProcessRemoteRecords(remoteBirdSounds);

        synchronizer.synchronize(BAMApplication.getContext(), remoteBirdSounds, localBirdSounds, remoteIdentifierColumn);
    }

}
