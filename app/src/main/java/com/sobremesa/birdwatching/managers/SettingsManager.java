package com.sobremesa.birdwatching.managers;

import android.content.SharedPreferences;

import com.sobremesa.birdwatching.BAMApplication;
import com.sobremesa.birdwatching.BAMConstants;
import com.sobremesa.birdwatching.listeners.SettingsListener;
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


    public void setSettings(Settings mSettings) {
        this.mSettings = mSettings;

        saveSettings();
        fireSettingsEvent();
    }

    public Settings getSettings() {

        if( mSettings == null )
        {
            SharedPreferences prefs = BAMApplication.getContext().getSharedPreferences("com.sobremesa.birdwatching", BAMApplication.getContext().MODE_PRIVATE);
            int sortType = prefs.getInt(BAMConstants.SORT_PREF, 0);

            Settings settings = new Settings();
            settings.setSortBy(SortByType.values()[sortType]);

            setSettings(settings);
        }

        return mSettings;
    }

    private void saveSettings()
    {
        SharedPreferences prefs = BAMApplication.getContext().getSharedPreferences("com.sobremesa.birdwatching", BAMApplication.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(BAMConstants.SORT_PREF, getSettings().getSortBy().ordinal());
        editor.commit();
    }

    public void addSettingsListener(SettingsListener l) {
        mSettingsListeners.add(l);
    }

    public void removeSettingsListener(SettingsListener l) {
        mSettingsListeners.remove(l);
    }

    public void fireSettingsEvent() {

        for (SettingsListener l : mSettingsListeners) {
            if (l != null)
                l.settingsEventReceived();
        }
    }

}
