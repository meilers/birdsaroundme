package com.sobremesa.birdwatching.models.remote;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.sobremesa.birdwatching.database.BirdSoundTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 2014-03-17.
 */
public class RemoteBirdRecordings extends RemoteObject {


    @Expose
    private int page;

    @Expose
    private int numPages;

    @Expose
    private ArrayList<RemoteBirdSound> recordings;


    @Override
    public String getIdentifier() {
        return null;
    }



    public RemoteBirdRecordings() {}


    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setRecordings(ArrayList<RemoteBirdSound> recordings) {
        this.recordings = recordings;
    }

    public ArrayList<RemoteBirdSound> getRecordings() {
        return recordings;
    }
}
