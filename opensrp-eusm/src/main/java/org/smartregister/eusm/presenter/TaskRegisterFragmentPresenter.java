package org.smartregister.eusm.presenter;

import org.json.JSONObject;
import org.smartregister.eusm.adapter.EusmTaskRegisterAdapter;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.interactor.TaskRegisterFragmentInteractor;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;

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
        taskRegisterFragmentInteractor.startForm(structureDetail, taskDetail,
                getView().getActivity(),
                this,
                formName);
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
        getView().startFormActivity(jsonForm);
    }
}
