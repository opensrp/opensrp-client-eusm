package org.smartregister.eusm.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.repository.BaseRepository;
import org.smartregister.tasking.util.InteractorUtils;

public class AppRepository extends BaseRepository {

    public void archiveEventsForTask(TaskDetail taskDetail) {
        SQLiteDatabase db = getReadableDatabase();
        EusmApplication eusmApplication = EusmApplication.getInstance();
        InteractorUtils interactorUtils = new InteractorUtils(eusmApplication.getTaskRepository(),
                eusmApplication.getEventClientRepository(),
                eusmApplication.getClientProcessor());
        interactorUtils.archiveEventsForTask(db, taskDetail);
        //invoke sync here
    }
}
