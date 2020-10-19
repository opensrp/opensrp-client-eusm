package org.smartregister.eusm.service;

import android.content.Intent;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.smartregister.AllConstants;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.sync.intent.SettingsSyncIntentService;

public class AppSettingsSyncIntentService extends SettingsSyncIntentService {
    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        Bundle data = intent.getExtras();
        if (data != null && data.getInt(AllConstants.INTENT_KEY.SYNC_TOTAL_RECORDS, 0) > 0) {
            EusmApplication.getInstance().processServerConfigs();
            // broadcast sync event
            Intent refreshGeoWidgetIntent = new Intent(AppConstants.Action.STRUCTURE_TASK_SYNCED);
            refreshGeoWidgetIntent.putExtra(AppConstants.CONFIGURATION.UPDATE_LOCATION_BUFFER_RADIUS, true);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(refreshGeoWidgetIntent);
        }
    }
}
