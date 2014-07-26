package com.sobremesa.birdwatching.models.remote;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.sobremesa.birdwatching.database.BirdImageTable;

/**
 * Created by Michael on 2014-03-17.
 */
public class RemoteBirdImage extends RemoteObject implements Parcelable {

    @Expose
    private String imageUrl;

    @Expose
    private String sciName;


    private int mPosition;



    @Override
    public String getIdentifier() {
        return imageUrl;
    }

    @Override
    public String getIdentifier2() {
        return sciName;
    }


    public RemoteBirdImage() {}


    public RemoteBirdImage(final Cursor cursor) {
        setImageUrl(cursor.getString(cursor.getColumnIndex(BirdImageTable.IMAGE_URL)));
        setSciName(cursor.getString(cursor.getColumnIndex(BirdImageTable.SCI_NAME)));
        setPosition(cursor.getInt(cursor.getColumnIndex(BirdImageTable.POSITION)));
    }

    public RemoteBirdImage(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getImageUrl());
        dest.writeString(this.getSciName());
        dest.writeInt(this.getPosition());
    }


    public void readFromParcel(Parcel in) {
        setImageUrl(in.readString());
        setSciName(in.readString());
        setPosition(in.readInt());

    }

    public static Creator<RemoteBirdImage> CREATOR = new Creator<RemoteBirdImage>() {

        @Override
        public RemoteBirdImage createFromParcel(Parcel in) {
            return new RemoteBirdImage(in);
        }

        @Override
        public RemoteBirdImage[] newArray(int size) {
            return new RemoteBirdImage[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSciName() {
        return sciName;
    }

    public void setSciName(String sciName) {
        this.sciName = sciName;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }


}