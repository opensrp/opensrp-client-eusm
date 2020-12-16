package org.smartregister.eusm.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import org.smartregister.eusm.service.AppLocationTaskIntentService;
import org.smartregister.eusm.service.AppSyncIntentService;
import org.smartregister.job.DocumentConfigurationServiceJob;
import org.smartregister.job.ExtendedSyncServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.ValidateSyncDataServiceJob;
import org.smartregister.stock.job.SyncStockServiceJob;
import org.smartregister.stock.job.SyncStockTypeServiceJob;
import org.smartregister.sync.intent.DocumentConfigurationIntentService;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 11/21/18.
 */
public class AppJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SyncServiceJob.TAG:
                return new SyncServiceJob(AppSyncIntentService.class);
            case LocationTaskServiceJob.TAG:
                return new LocationTaskServiceJob(AppLocationTaskIntentService.class);
            case AppSyncSettingsServiceJob.TAG:
                return new AppSyncSettingsServiceJob();
            case ExtendedSyncServiceJob.TAG:
                return new ExtendedSyncServiceJob();
            case ValidateSyncDataServiceJob.TAG:
                return new ValidateSyncDataServiceJob();
            case DocumentConfigurationServiceJob.TAG:
                return new DocumentConfigurationServiceJob(DocumentConfigurationIntentService.class);
            case SyncStockTypeServiceJob.TAG:
                return new SyncStockTypeServiceJob();
            case SyncStockServiceJob.TAG:
                return new SyncStockServiceJob();
            case ImageUploadServiceJob.TAG:
                return new ImageUploadServiceJob();
            default:
                Timber.w("%s is not declared in RevealJobCreator Job Creator", tag);
                return null;
        }
    }
}