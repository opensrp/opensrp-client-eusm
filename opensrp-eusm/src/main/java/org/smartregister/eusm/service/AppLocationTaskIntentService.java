package org.smartregister.eusm.service;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.jetbrains.annotations.NotNull;
import org.smartregister.domain.Location;
import org.smartregister.domain.Task;
import org.smartregister.repository.TaskRepository;
import org.smartregister.tasking.sync.LocationTaskIntentService;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;
import java.util.Set;

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
}
