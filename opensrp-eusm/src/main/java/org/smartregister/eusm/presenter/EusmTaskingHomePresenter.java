package org.smartregister.eusm.presenter;

import org.smartregister.eusm.interactor.EusmTaskingHomeInteractor;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.contract.TaskingHomeActivityContract;
import org.smartregister.tasking.interactor.TaskingHomeInteractor;
import org.smartregister.tasking.presenter.TaskingHomePresenter;

public class EusmTaskingHomePresenter extends TaskingHomePresenter {

    public EusmTaskingHomePresenter(TaskingHomeActivityContract.View view, BaseDrawerContract.Presenter drawerPresenter) {
        super(view, drawerPresenter);
    }

    @Override
    public TaskingHomeInteractor getTaskingHomeInteractor() {
        return new EusmTaskingHomeInteractor(this);
    }
}
