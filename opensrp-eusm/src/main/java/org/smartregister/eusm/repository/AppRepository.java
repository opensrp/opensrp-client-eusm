package org.smartregister.eusm.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.repository.BaseRepository;
import org.smartregister.tasking.util.InteractorUtils;

import java.util.List;

public class AppRepository extends BaseRepository {

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
//            try {
//                AppUtils.initiateEventProcessing(formSubmissionIds);
//            } catch (Exception e) {
//                Timber.e(e);
//            }
        }
    }
}
