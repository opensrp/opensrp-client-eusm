package org.smartregister.eusm.job;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.eusm.service.AppSettingsSyncIntentService;
import org.smartregister.job.SyncSettingsServiceJob;

/**
 * @author Vincent Karuri
 */
public class AppSyncSettingsServiceJob extends SyncSettingsServiceJob {

    public static final String TAG = "RevealSyncSettingsServiceJob";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        Intent intent = new Intent(getApplicationContext(), AppSettingsSyncIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE, false) ? Result.RESCHEDULE : Result.SUCCESS;
    }
}
