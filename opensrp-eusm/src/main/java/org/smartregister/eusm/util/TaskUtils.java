package org.smartregister.eusm.util;

import android.content.Context;

import androidx.annotation.StringRes;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Action;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Task;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.model.BaseTaskDetails;
import org.smartregister.eusm.util.AppConstants.BusinessStatus;
import org.smartregister.eusm.util.AppConstants.Intervention;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.TaskRepository;

import java.util.List;
import java.util.UUID;

import timber.log.Timber;

import static org.smartregister.domain.Task.TaskStatus.READY;
import static org.smartregister.eusm.util.AppConstants.DatabaseKeys.CODE;
import static org.smartregister.eusm.util.AppConstants.DatabaseKeys.FOR;
import static org.smartregister.eusm.util.AppConstants.DatabaseKeys.STATUS;
import static org.smartregister.eusm.util.AppConstants.DatabaseKeys.TASK_TABLE;

/**
 * Created by samuelgithengi on 4/14/19.
 */
public class TaskUtils {

    private static TaskUtils instance;
    private final TaskRepository taskRepository;
    private final PlanDefinitionRepository planRepository;
    private final AllSharedPreferences sharedPreferences;
    private final PreferencesUtil prefsUtil;
    private final EusmApplication eusmApplication;

    private TaskUtils() {
        taskRepository = EusmApplication.getInstance().getTaskRepository();
        sharedPreferences = EusmApplication.getInstance().getContext().allSharedPreferences();
        prefsUtil = PreferencesUtil.getInstance();
        planRepository = EusmApplication.getInstance().getPlanDefinitionRepository();
        eusmApplication = EusmApplication.getInstance();
    }

    public static TaskUtils getInstance() {
        if (instance == null) {
            instance = new TaskUtils();
        }
        return instance;
    }

    public Task generateTask(Context context, String entityId, String structureId, String businessStatus, String intervention, @StringRes int description) {
        Task task = new Task();
        DateTime now = new DateTime();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPlanIdentifier(prefsUtil.getCurrentPlanId());
        task.setGroupIdentifier(Utils.getOperationalAreaLocation(prefsUtil.getCurrentOperationalArea()).getId());
        task.setStatus(READY);
        task.setBusinessStatus(businessStatus);
        task.setPriority(3);
        task.setCode(intervention);
        task.setDescription(context.getString(description));

        PlanDefinition currentPlan = planRepository.findPlanDefinitionById(prefsUtil.getCurrentPlanId());
        if (currentPlan != null && currentPlan.getActions() != null) {
            for (Action action : currentPlan.getActions()) {
                if (intervention.equals(action.getCode())) {
                    task.setFocus(action.getIdentifier());
                }
            }
        }
        task.setForEntity(entityId);
        task.setStructureId(structureId);
        task.setExecutionStartDate(now);
        task.setAuthoredOn(now);
        task.setLastModified(now);
        task.setOwner(sharedPreferences.fetchRegisteredANM());
        task.setSyncStatus(BaseRepository.TYPE_Created);
        taskRepository.addOrUpdate(task);
        eusmApplication.setSynced(false);
        return task;
    }

    public void tagEventTaskDetails(List<Event> events, SQLiteDatabase sqLiteDatabase) {
        for (Event event : events) {
            Cursor cursor = null;
            try {
                cursor = sqLiteDatabase.rawQuery(String.format("select * from %s where %s =? and %s =? and %s =? limit 1", TASK_TABLE, FOR, STATUS, CODE),
                        new String[]{event.getBaseEntityId(), Task.TaskStatus.COMPLETED.name(), Intervention.IRS});
                while (cursor.moveToNext()) {
                    Task task = taskRepository.readCursor(cursor);
                    event.addDetails(AppConstants.Properties.TASK_IDENTIFIER, task.getIdentifier());
                    event.addDetails(AppConstants.Properties.TASK_BUSINESS_STATUS, task.getBusinessStatus());
                    event.addDetails(AppConstants.Properties.TASK_STATUS, task.getStatus().name());
                    event.addDetails(AppConstants.Properties.LOCATION_ID, task.getForEntity());
                    event.addDetails(AppConstants.Properties.APP_VERSION_NAME, BuildConfig.VERSION_NAME);
                    event.setLocationId(task.getGroupIdentifier());
                }

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }
    }

    public boolean resetTask(BaseTaskDetails taskDetails) {

        boolean taskResetSuccessful = false;
        try {
            Task task = taskRepository.getTaskByIdentifier(taskDetails.getTaskId());
            String operationalAreaId = Utils.getOperationalAreaLocation(prefsUtil.getCurrentOperationalArea()).getId();

            if (Intervention.CASE_CONFIRMATION.equals(taskDetails.getTaskCode())) {
                task.setForEntity(operationalAreaId);
            }

            task.setBusinessStatus(BusinessStatus.NOT_VISITED);
            task.setStatus(READY);
            task.setLastModified(new DateTime());
            task.setSyncStatus(BaseRepository.TYPE_Unsynced);
            taskRepository.addOrUpdate(task);
            eusmApplication.setSynced(false);

            eusmApplication.setRefreshMapOnEventSaved(true);

            taskResetSuccessful = true;
        } catch (Exception e) {
            Timber.e(e);
        }

        return taskResetSuccessful;

    }


}
