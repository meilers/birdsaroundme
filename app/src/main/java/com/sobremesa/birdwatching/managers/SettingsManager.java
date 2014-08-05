package com.sobremesa.birdwatching.managers;

import android.content.SharedPreferences;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.listeners.SettingsListener;
import com.sobremesa.birdwatching.models.DateType;
import com.sobremesa.birdwatching.models.DistanceType;
import com.sobremesa.birdwatching.models.SettingType;
import com.sobremesa.birdwatching.models.Settings;
import com.sobremesa.birdwatching.models.SortByType;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by omegatai on 2014-07-09.
 */
public enum SettingsManager {

    INSTANCE;

    private Settings mSettings;

    private CopyOnWriteArrayList<SettingsListener> mSettingsListeners = new CopyOnWriteArrayList<SettingsListener>();


    private SettingsManager() {
    }


    public void setDistance(DistanceType type) {
        this.mSettings.setDistance(type);

        SharedPreferences prefs = BAMApplication.getContext().getSharedPreferences("com.sobremesa.birdwatching", BAMApplication.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BAMConstants.DISTANCE_PREF, getSettings().getDistance().ordinal());
        editor.commit();

        fireSettingsEvent(SettingType.DISTANCE);
    }

    public void setDate(DateType type) {
        this.mSettings.setDate(type);

        SharedPreferences prefs = BAMApplication.getContext().getSharedPreferences("com.sobremesa.birdwatching", BAMApplication.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BAMConstants.DATE_PREF, getSettings().getDate().ordinal());
        editor.commit();

        fireSettingsEvent(SettingType.DATE);
    }

    public void setSortBy(SortByType type) {
        this.mSettings.setSortBy(type);

        SharedPreferences prefs = BAMApplication.getContext().getSharedPreferences("com.sobremesa.birdwatching", BAMApplication.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(BAMConstants.SORT_PREF, getSettings().getSortBy().ordinal());
        editor.commit();

        fireSettingsEvent(SettingType.SORT_BY);
    }



    public Settings getSettings() {

        if( mSettings == null )
        {
            SharedPreferences prefs = BAMApplication.getContext().getSharedPreferences("com.sobremesa.birdwatching", BAMApplication.getContext().MODE_PRIVATE);
            int distanceType = prefs.getInt(BAMConstants.DISTANCE_PREF, 0);
            int dateType = prefs.getInt(BAMConstants.DATE_PREF, 0);
            int sortType = prefs.getInt(BAMConstants.SORT_PREF, 0);

            Settings settings = new Settings();
            settings.setDistance(DistanceType.values()[distanceType]);
            settings.setDate(DateType.values()[dateType]);
            settings.setSortBy(SortByType.values()[sortType]);

            this.mSettings = settings;
        }

        return mSettings;
    }


    public void addSettingsListener(SettingsListener l) {
        mSettingsListeners.add(l);
    }

    public void removeSettingsListener(SettingsListener l) {
        mSettingsListeners.remove(l);
    }

    public void fireSettingsEvent(SettingType type) {

        for (SettingsListener l : mSettingsListeners) {
            if (l != null)
                l.settingsEventReceived(type);
        }
    }

}
