package com.sobremesa.birdwatching.models.remote;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.sobremesa.birdwatching.database.BirdSoundTable;

/**
 * Created by Michael on 2014-03-17.
 */
public class RemoteBirdSound extends RemoteObject implements Parcelable {


    @Expose
    private String id;


    @Expose
    private String comName;

    @Expose
    private String gen;

    @Expose
    private String sp;

    @Expose
    private String ssp;

    @Expose
    private String en;

    @Expose
    private String rec;

    @Expose
    private String cnt;

    @Expose
    private String loc;

    @Expose
    private String lat;

    @Expose
    private String lng;

    @Expose
    private String type;

    @Expose
    private String file;

    @Expose
    private String lic;

    @Expose
    private String url;


    @Override
    public String getIdentifier() {
        return id;
    }



    public RemoteBirdSound() {}


    public RemoteBirdSound(final Cursor cursor) {
        setBirdSoundId(cursor.getString(cursor.getColumnIndex(BirdSoundTable.BIRD_SOUND_ID)));
        setComName(cursor.getString(cursor.getColumnIndex(BirdSoundTable.COM_NAME)));
        setGen(cursor.getString(cursor.getColumnIndex(BirdSoundTable.GEN)));
        setSp(cursor.getString(cursor.getColumnIndex(BirdSoundTable.SP)));
        setSsp(cursor.getString(cursor.getColumnIndex(BirdSoundTable.SSP)));
        setEn(cursor.getString(cursor.getColumnIndex(BirdSoundTable.EN)));
        setRec(cursor.getString(cursor.getColumnIndex(BirdSoundTable.REC)));
        setCnt(cursor.getString(cursor.getColumnIndex(BirdSoundTable.CNT)));
        setLoc(cursor.getString(cursor.getColumnIndex(BirdSoundTable.LOC)));
        setLat(cursor.getString(cursor.getColumnIndex(BirdSoundTable.LAT)));
        setLng(cursor.getString(cursor.getColumnIndex(BirdSoundTable.LNG)));
        setType(cursor.getString(cursor.getColumnIndex(BirdSoundTable.TYPE)));
        setFile(cursor.getString(cursor.getColumnIndex(BirdSoundTable.FILE)));
        setLic(cursor.getString(cursor.getColumnIndex(BirdSoundTable.LIC)));
        setUrl(cursor.getString(cursor.getColumnIndex(BirdSoundTable.URL)));

    }

    public RemoteBirdSound(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getBirdSoundId());
        dest.writeString(this.getComName());
        dest.writeString(this.getGen());
        dest.writeString(this.getSp());
        dest.writeString(this.getSsp());
        dest.writeString(this.getEn());
        dest.writeString(this.getRec());
        dest.writeString(this.getCnt());
        dest.writeString(this.getLoc());
        dest.writeString(this.getLat());
        dest.writeString(this.getLng());
        dest.writeString(this.getType());
        dest.writeString(this.getFile());
        dest.writeString(this.getLic());
        dest.writeString(this.getUrl());
    }


    public void readFromParcel(Parcel in) {
        setBirdSoundId(in.readString());
        setComName(in.readString());
        setGen(in.readString());
        setSp(in.readString());
        setSsp(in.readString());
        setEn(in.readString());
        setRec(in.readString());
        setCnt(in.readString());
        setLoc(in.readString());
        setLat(in.readString());
        setLng(in.readString());
        setType(in.readString());
        setFile(in.readString());
        setLic(in.readString());
        setUrl(in.readString());

    }

    public static Creator<RemoteBirdSound> CREATOR = new Creator<RemoteBirdSound>() {

        @Override
        public RemoteBirdSound createFromParcel(Parcel in) {
            return new RemoteBirdSound(in);
        }

        @Override
        public RemoteBirdSound[] newArray(int size) {
            return new RemoteBirdSound[size];
        }
    };

    public String getBirdSoundId() {
        return id;
    }

    public void setBirdSoundId(String id) {
        this.id = id;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getSsp() {
        return ssp;
    }

    public void setSsp(String ssp) {
        this.ssp = ssp;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getRec() {
        return rec;
    }

    public void setRec(String rec) {
        this.rec = rec;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLic() {
        return lic;
    }

    public void setLic(String lic) {
        this.lic = lic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
