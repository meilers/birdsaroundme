package com.sobremesa.birdwatching.listeners;


import com.sobremesa.birdwatching.models.SettingType;

public interface SettingsListener {
	void settingsEventReceived(SettingType type);
}
