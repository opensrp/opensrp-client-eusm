package org.smartregister.eusm.service;

import android.content.Intent;

import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.sync.intent.SyncIntentService;

import java.util.Date;

public class AppSyncIntentService extends SyncIntentService {
    //TODO remove when no longer using test data
    @Override
    protected void complete(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_COMPLETE_STATUS, true);

        sendBroadcast(intent);
        //sync time not update if sync is fail
        if (!fetchStatus.equals(FetchStatus.noConnection) && !fetchStatus.equals(FetchStatus.fetchedFailed)) {
            ECSyncHelper ecSyncUpdater = ECSyncHelper.getInstance(getBaseContext());
            ecSyncUpdater.updateLastCheckTimeStamp(new Date().getTime());
        }
    }
}
