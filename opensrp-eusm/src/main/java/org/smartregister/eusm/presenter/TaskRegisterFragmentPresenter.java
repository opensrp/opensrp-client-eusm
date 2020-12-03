package org.smartregister.eusm.presenter;

import org.json.JSONObject;
import org.smartregister.eusm.adapter.EusmTaskRegisterAdapter;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.interactor.TaskRegisterFragmentInteractor;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.model.StructureDetail;

import java.lang.ref.WeakReference;
import java.util.List;

public class TaskRegisterFragmentPresenter implements TaskRegisterFragmentContract.Presenter,
        TaskRegisterFragmentContract.InteractorCallBack {

    private TaskRegisterFragmentInteractor taskRegisterFragmentInteractor;

    private WeakReference<TaskRegisterFragmentContract.View> viewWeakReference;

    public TaskRegisterFragmentPresenter(TaskRegisterFragmentContract.View view) {
        taskRegisterFragmentInteractor = new TaskRegisterFragmentInteractor();
        viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void fetchData() {
        if (getView() != null) {
            getView().showProgressView();
            taskRegisterFragmentInteractor.fetchData(getView().getStructureDetail(), this);
        }
    }

    @Override
    public TaskRegisterFragmentContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        }
        return null;
    }

    @Override
    public void startForm(StructureDetail structureDetail, TaskDetail taskDetail, String formName) {
        if (getView() != null) {
            getView().showProgressView();
        }
        taskRegisterFragmentInteractor.startForm(structureDetail, taskDetail,
                getView().getActivity(),
                formName, this);
    }

    @Override
    public void undoTask(TaskDetail taskDetail) {
        if (getView() != null) {
            getView().showProgressView();
        }
        taskRegisterFragmentInteractor.undoTask(taskDetail, this);
    }

    @Override
    public void onFetchedData(List<TaskDetail> taskDetailList) {
        if (getView() != null) {
            getView().hideProgressView();
            if (getView().getAdapter() != null) {
                ((EusmTaskRegisterAdapter) getView().getAdapter()).setData(taskDetailList);
            }
        }
    }

    @Override
    public void onFormFetched(JSONObject jsonForm) {
        if (getView() != null) {
            getView().hideProgressView();
            getView().startFormActivity(jsonForm);
        }
    }

    @Override
    public void onTaskUndone(boolean isSuccessFul, TaskDetail taskDetail) {
        if (getView() != null) {
            getView().hideProgressView();
        }
        if (isSuccessFul) {
            getView().onResumption();
        }
    }
}
