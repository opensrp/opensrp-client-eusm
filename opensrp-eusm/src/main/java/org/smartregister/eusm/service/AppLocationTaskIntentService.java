package org.smartregister.eusm.service;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jetbrains.annotations.NotNull;
import org.smartregister.domain.Location;
import org.smartregister.domain.Task;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.repository.TaskRepository;
import org.smartregister.stock.job.SyncStockServiceJob;
import org.smartregister.stock.job.SyncStockTypeServiceJob;
import org.smartregister.sync.helper.LocationServiceHelper;
import org.smartregister.tasking.sync.LocationTaskIntentService;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.tasking.util.Constants.Action.STRUCTURE_TASK_SYNCED;

public class AppLocationTaskIntentService extends LocationTaskIntentService {

    @Override
    protected @NotNull Set<String> getEventBaseEntityIds(List<Location> syncedStructures, List<Task> syncedTasks) {
        TaskRepository taskRepository = DrishtiApplication.getInstance().getContext().getTaskRepository();
        taskRepository.updateTaskStructureIdFromStructure(syncedStructures);
        taskRepository.updateTaskStructureIdsFromExistingStructures();
        if (hasChangesInCurrentOperationalArea(syncedStructures, syncedTasks)) {
            Intent intent = new Intent(STRUCTURE_TASK_SYNCED);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
        return extractStructureIds(syncedStructures, syncedTasks);
    }

    @Override
    protected List<Location> syncStructures(LocationServiceHelper locationServiceHelper) {
        List<Location> locationList = super.syncStructures(locationServiceHelper);

        List<String> locations = EusmApplication.getInstance().getStructureRepository().getAllLocationIds();

        if (locations != null && !locations.isEmpty()) {
            AppUtils.saveStructureIds(locations);

            //initiate Stock And StockType sync after structures have been fetched
            SyncStockServiceJob.scheduleJobImmediately(SyncStockServiceJob.TAG);
            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE))
                    == PackageManager.PERMISSION_GRANTED) {
                SyncStockTypeServiceJob.scheduleJobImmediately(SyncStockTypeServiceJob.TAG);
            } else {
                Timber.e("Read and Write Permission Not Granted");
            }
        }
        return locationList;
    }
}
