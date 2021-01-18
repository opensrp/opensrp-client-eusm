package org.smartregister.eusm.presenter;

import org.smartregister.eusm.interactor.EusmTaskingMapInteractor;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.contract.TaskingMapActivityContract;
import org.smartregister.tasking.interactor.TaskingMapInteractor;
import org.smartregister.tasking.presenter.TaskingMapPresenter;

public class EusmTaskingMapPresenter extends TaskingMapPresenter {

    private TaskingMapInteractor taskingMapInteractor;

    public EusmTaskingMapPresenter(TaskingMapActivityContract.View view, BaseDrawerContract.Presenter drawerPresenter) {
        super(view, drawerPresenter);
    }

    @Override
    public TaskingMapInteractor getTaskingMapInteractor() {
        if (taskingMapInteractor == null) {
            taskingMapInteractor = new EusmTaskingMapInteractor(this);
        }
        return taskingMapInteractor;
    }

    @Override
    public void requestUserPassword() {
        //Do nothing
    }
}
