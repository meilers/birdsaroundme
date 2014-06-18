package com.sobremesa.birdwatching.models.remote;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.sobremesa.birdwatching.database.SightingTable;

/**
 * Created by Michael on 2014-03-17.
 */
public class RemoteSighting extends BaseRemoteModel implements Parcelable {

    @Expose
    private String comName;

    @Expose
    private String sciName;

    @Expose
    private Integer howMany;

    @Expose
    private Double lat;

    @Expose
    private Double lng;


    @Expose
    private String locID;

    @Expose
    private String locName;

    @Expose
    private Boolean locationPrivate;

    @Expose
    private String obsDt;

    @Expose
    private Boolean obsReviewed;

    @Expose
    private Boolean obsValid;




    @Override
    public String getIdentifier() {
        return sciName;
    }

    @Override
    public String getIdentifier2() {
        return locID;
    }

    @Override
    public String getIdentifier3() {
        return obsDt;
    }


    public RemoteSighting() {}


    public RemoteSighting(final Cursor cursor) {
        setComName(cursor.getString(cursor.getColumnIndex(SightingTable.COM_NAME)));
        setSciName(cursor.getString(cursor.getColumnIndex(SightingTable.SCI_NAME)));
        setHowMany(cursor.getInt(cursor.getColumnIndex(SightingTable.HOW_MANY)));
        setLat(cursor.getDouble(cursor.getColumnIndex(SightingTable.LAT)));
        setLng(cursor.getDouble(cursor.getColumnIndex(SightingTable.LNG)));
        setLocID(cursor.getString(cursor.getColumnIndex(SightingTable.LOC_ID)));

        setLocName(cursor.getString(cursor.getColumnIndex(SightingTable.LOC_NAME)));
        setLocationPrivate(cursor.getInt(cursor.getColumnIndex(SightingTable.LOCATION_PRIVATE)) == 1);

        setObsDt(cursor.getString(cursor.getColumnIndex(SightingTable.OBS_DT)));
        setObsReviewed(cursor.getInt(cursor.getColumnIndex(SightingTable.OBS_REVIEWED)) == 1);
        setObsValid(cursor.getInt(cursor.getColumnIndex(SightingTable.OBS_VALID)) == 1);
    }

    public RemoteSighting(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getComName());
        dest.writeString(this.getSciName());
        dest.writeInt(this.getHowMany());
        dest.writeDouble(this.getLat());
        dest.writeDouble(this.getLng());
        dest.writeString(this.getLocID());
        dest.writeString(this.getLocName());

        dest.writeInt(this.getLocationPrivate() ? 1:0);
        dest.writeString(this.getObsDt());
        dest.writeInt(this.getObsReviewed() ? 1:0);
        dest.writeInt(this.getObsValid() ? 1:0);
    }


    public void readFromParcel(Parcel in) {
        setComName(in.readString());
        setSciName(in.readString());
        setHowMany(in.readInt());
        setLat(in.readDouble());
        setLng(in.readDouble());
        setLocID(in.readString());
        setLocName(in.readString());

        setLocationPrivate(in.readInt() == 1);
        setObsDt(in.readString());
        setObsReviewed(in.readInt() == 1);
        setObsValid(in.readInt() == 1);

    }

    public static Creator<RemoteSighting> CREATOR = new Creator<RemoteSighting>() {

        @Override
        public RemoteSighting createFromParcel(Parcel in) {
            return new RemoteSighting(in);
        }

        @Override
        public RemoteSighting[] newArray(int size) {
            return new RemoteSighting[size];
        }
    };

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getSciName() {
        return sciName;
    }

    public void setSciName(String sciName) {
        this.sciName = sciName;
    }

    public Integer getHowMany() {
        return howMany;
    }

    public void setHowMany(Integer howMany) {
        this.howMany = howMany;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getLocID() {
        return locID;
    }

    public void setLocID(String locID) {
        this.locID = locID;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public Boolean getLocationPrivate() {
        return locationPrivate;
    }

    public void setLocationPrivate(Boolean locationPrivate) {
        this.locationPrivate = locationPrivate;
    }

    public String getObsDt() {
        return obsDt;
    }

    public void setObsDt(String obsDt) {
        this.obsDt = obsDt;
    }

    public Boolean getObsReviewed() {
        return obsReviewed;
    }

    public void setObsReviewed(Boolean obsReviewed) {
        this.obsReviewed = obsReviewed;
    }

    public Boolean getObsValid() {
        return obsValid;
    }
    

    public void setObsValid(Boolean obsValid) {
        this.obsValid = obsValid;
    }
}