package org.smartregister.eusm.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.tasking.util.InteractorUtils;

import java.util.List;

import timber.log.Timber;

public class AppRepository extends BaseRepository {

    public boolean checkSynced() {
        boolean synced = false;

//        String syncQuery = String.format("SELECT %s FROM %s WHERE %s <> ?\n", AppConstants.DatabaseKeys.SYNC_STATUS, EventClientRepository.Table.client.name(), AppConstants.DatabaseKeys.SYNC_STATUS) +
//                "UNION ALL\n" +
//                String.format("SELECT %s FROM %s WHERE %s <> ? AND %s <> ?\n", AppConstants.DatabaseKeys.SYNC_STATUS, AppConstants.Tables.EVENT_TABLE, AppConstants.DatabaseKeys.SYNC_STATUS, AppConstants.DatabaseKeys.SYNC_STATUS) +
//                "UNION ALL\n" +
//                String.format("SELECT %s FROM %s WHERE %s <> ?\n", AppConstants.DatabaseKeys.TASK_SYNC_STATUS, AppConstants.Tables.TASK_TABLE, AppConstants.DatabaseKeys.TASK_SYNC_STATUS) +
//                "UNION ALL\n" +
//                String.format("SELECT %s FROM %s WHERE %s <> ?\n", AppConstants.DatabaseKeys.STRUCTURE_SYNC_STATUS, AppConstants.Tables.STRUCTURE_TABLE, AppConstants.DatabaseKeys.STRUCTURE_SYNC_STATUS);
//
//        try (Cursor syncCursor = getReadableDatabase()
//                .rawQuery(syncQuery, new String[]{TYPE_Synced, TYPE_Synced, TYPE_Task_Unprocessed, TYPE_Synced, TYPE_Synced})) {
//            int count = syncCursor.getCount();
//            synced = count == 0;
//        } catch (Exception e) {
//            Timber.e(e, "EXCEPTION %s", e.toString());
//        }
        return synced;
    }

    public void archiveEventsForTask(TaskDetail taskDetail) {
        SQLiteDatabase db = getReadableDatabase();
        EusmApplication eusmApplication = EusmApplication.getInstance();
        InteractorUtils interactorUtils = new InteractorUtils(eusmApplication.getTaskRepository(),
                eusmApplication.getEventClientRepository(),
                eusmApplication.getClientProcessor());
        boolean archived = interactorUtils.archiveEventsForTask(db, taskDetail);
        if (archived) {
            List<String> formSubmissionIds = interactorUtils
                    .getFormSubmissionIdsFromEventTask(db, taskDetail);
            try {
                AppUtils.initiateEventProcessing(formSubmissionIds);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
