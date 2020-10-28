package org.smartregister.eusm.presenter;

import org.smartregister.eusm.contract.TaskRegisterActivityContract;
import org.smartregister.eusm.interactor.TaskRegisterFragmentInteractor;
import org.smartregister.view.contract.BaseProfileContract;

import java.lang.ref.WeakReference;

public class TaskRegisterActivityPresenter implements TaskRegisterActivityContract.Presenter, BaseProfileContract.Presenter {

    private TaskRegisterFragmentInteractor taskRegisterFragmentInteractor;

    private WeakReference<TaskRegisterActivityContract.View> viewWeakReference;

    public TaskRegisterActivityPresenter(TaskRegisterActivityContract.View view) {
        taskRegisterFragmentInteractor = new TaskRegisterFragmentInteractor();
        viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public TaskRegisterActivityContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        }
        return null;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

    }
}
