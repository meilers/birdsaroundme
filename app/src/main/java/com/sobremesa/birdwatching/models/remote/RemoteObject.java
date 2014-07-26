package com.sobremesa.birdwatching.models.remote;

/**
 * Created by Michael on 2014-03-11.
 */
public abstract class RemoteObject {

    public enum SyncStatus
    {
        NO_CHANGES, QUEUED_TO_SYNC, TEMP /* NO SYNCING */
    }


    public abstract String getIdentifier();
    public String getIdentifier2() { return ""; };
    public String getIdentifier3() { return ""; };

}

