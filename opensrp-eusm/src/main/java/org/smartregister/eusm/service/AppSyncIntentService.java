package org.smartregister.eusm.service;

import android.content.Context;

import androidx.annotation.NonNull;

import org.smartregister.eusm.helper.AppValidateAssignmentHelper;
import org.smartregister.sync.intent.SyncIntentService;
import org.smartregister.util.SyncUtils;

public class AppSyncIntentService extends SyncIntentService {

    @Override
    protected void init(@NonNull Context context) {
        super.init(context);
        validateAssignmentHelper = new AppValidateAssignmentHelper(new SyncUtils(getBaseContext()));
    }
}
