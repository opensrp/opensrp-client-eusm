package org.smartregister.eusm.presenter;

import org.smartregister.eusm.interactor.EusmTaskingMapInteractor;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.contract.TaskingMapActivityContract;
import org.smartregister.tasking.interactor.TaskingMapInteractor;
import org.smartregister.tasking.presenter.TaskingMapPresenter;

public class EusmTaskingMapPresenter extends TaskingMapPresenter {

    public EusmTaskingMapPresenter(TaskingMapActivityContract.View view, BaseDrawerContract.Presenter drawerPresenter) {
        super(view, drawerPresenter);
    }

    @Override
    public TaskingMapInteractor getTaskingMapInteractor() {
        return new EusmTaskingMapInteractor(this);
    }
}
