package org.smartregister.eusm.repository;

import org.smartregister.repository.BaseRepository;

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
}
