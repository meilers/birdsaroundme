package com.sobremesa.birdwatching.models.remote;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.sobremesa.birdwatching.database.BirdDescriptionTable;

/**
 * Created by Michael on 2014-03-17.
 */
public class RemoteBirdDescription extends RemoteObject implements Parcelable {

    @Expose
    private String description;

    @Expose
    private String sciName;



    @Override
    public String getIdentifier() {
        return description;
    }


    public RemoteBirdDescription() {}


    public RemoteBirdDescription(final Cursor cursor) {
        setDescription(cursor.getString(cursor.getColumnIndex(BirdDescriptionTable.DESCRIPTION)));
        setSciName(cursor.getString(cursor.getColumnIndex(BirdDescriptionTable.SCI_NAME)));
    }

    public RemoteBirdDescription(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getDescription());
        dest.writeString(this.getSciName());
    }


    public void readFromParcel(Parcel in) {
        setDescription(in.readString());
        setSciName(in.readString());

    }

    public static Creator<RemoteBirdDescription> CREATOR = new Creator<RemoteBirdDescription>() {

        @Override
        public RemoteBirdDescription createFromParcel(Parcel in) {
            return new RemoteBirdDescription(in);
        }

        @Override
        public RemoteBirdDescription[] newArray(int size) {
            return new RemoteBirdDescription[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSciName() {
        return sciName;
    }

    public void setSciName(String sciName) {
        this.sciName = sciName;
    }

}