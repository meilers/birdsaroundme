package com.sobremesa.birdwatching.models.remote;

import android.database.Cursor;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.sobremesa.birdwatching.database.SightingTable;
import com.sobremesa.birdwatching.managers.LocationManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Michael on 2014-03-17.
 */
public class RemoteSighting extends BaseRemoteModel implements Parcelable {


    public static class DateComparator implements Comparator<RemoteSighting> {
        @Override
        public int compare(RemoteSighting o1, RemoteSighting o2) {

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date1;
            Date date2;

            try {
                date1 = dateFormat.parse(o1.getObsDt());
                date2 = dateFormat.parse(o2.getObsDt());

                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public static class NameComparator implements Comparator<RemoteSighting> {
        @Override
        public int compare(RemoteSighting o1, RemoteSighting o2) {

            return o1.getComName().toLowerCase().compareTo(o2.getComName().toLowerCase());
        }
    }

    public static class DistanceComparator implements Comparator<RemoteSighting> {
        @Override
        public int compare(RemoteSighting o1, RemoteSighting o2) {

            Location curLocation = LocationManager.INSTANCE.getLocation();



            int dist1 = (int)distFrom((float)curLocation.getLatitude(), (float)curLocation.getLongitude(), o1.getLat().floatValue(), o1.getLng().floatValue());
            int dist2 = (int)distFrom((float)curLocation.getLatitude(), (float)curLocation.getLongitude(), o2.getLat().floatValue(), o2.getLng().floatValue());

            return dist1 - dist2;
        }
    }

    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    @Expose
    private String comName;

    @Expose
    private String sciName;

    @Expose
    private Integer howMany = 1;

    @Expose
    private Double lat;

    @Expose
    private Double lng;


    @Expose
    private String locID;

    @Expose
    private String locName = "";

    @Expose
    private Boolean locationPrivate = false;

    @Expose
    private String obsDt = "";

    @Expose
    private Boolean obsReviewed = false;

    @Expose
    private Boolean obsValid = false;


    private ArrayList<RemoteBirdImage> mImages = new ArrayList<RemoteBirdImage>();



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

        dest.writeList(this.getImages());
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

        setImages(in.readArrayList(RemoteBirdImage.class.getClassLoader()));
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

    public ArrayList<RemoteBirdImage> getImages() {
        return mImages;
    }

    public void setImages(ArrayList<RemoteBirdImage> mImages) {
        this.mImages = mImages;
    }
}