package org.smartregister.eusm.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.repository.BaseRepository;
import org.smartregister.tasking.util.InteractorUtils;

public class AppRepository extends BaseRepository {

    private InteractorUtils interactorUtils;

    public void archiveEventsForTask(TaskDetail taskDetail) {
        SQLiteDatabase db = getReadableDatabase();
        getInteractorUtils().archiveEventsForTask(db, taskDetail);
        //invoke sync here if necessary
    }

    protected InteractorUtils getInteractorUtils() {
        if (interactorUtils == null) {
            EusmApplication eusmApplication = EusmApplication.getInstance();
            interactorUtils = new InteractorUtils(eusmApplication.getTaskRepository(),
                    eusmApplication.getEventClientRepository(),
                    eusmApplication.getClientProcessor());
        }
        return interactorUtils;
    }
}
