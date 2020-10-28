package org.smartregister.eusm.presenter;

import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.interactor.TaskRegisterFragmentInteractor;
import org.smartregister.eusm.model.StructureTaskDetail;

import java.lang.ref.WeakReference;
import java.util.List;

public class TaskRegisterFragmentPresenter implements TaskRegisterFragmentContract.Presenter, TaskRegisterFragmentContract.InteractorCallBack {

    private TaskRegisterFragmentInteractor taskRegisterFragmentInteractor;

    private WeakReference<TaskRegisterFragmentContract.View> viewWeakReference;

    public TaskRegisterFragmentPresenter(TaskRegisterFragmentContract.View view) {
        taskRegisterFragmentInteractor = new TaskRegisterFragmentInteractor();
        viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void fetchData() {
        taskRegisterFragmentInteractor.fetchData(this);
    }

    @Override
    public TaskRegisterFragmentContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        }
        return null;
    }

    @Override
    public void onFetchedData(List<StructureTaskDetail> structureTaskDetailList) {
        if (getView().getAdapter() != null) {
            getView().getAdapter().setData(structureTaskDetailList);
        }
    }
}
