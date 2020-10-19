package org.smartregister.eusm.service;

import org.smartregister.sync.intent.SyncIntentService;

/**
 * Created by samuelgithengi on 10/17/19.
 */
public class AppSyncIntentService extends SyncIntentService {

    @Override
    public int getEventPullLimit() {
        return 500;
    }
}
