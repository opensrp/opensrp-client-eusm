package org.smartregister.eusm.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.TaskRegisterActivityContract;
import org.smartregister.eusm.interactor.TaskRegisterActivityInteractor;
import org.smartregister.eusm.model.StructureDetail;

import java.lang.ref.WeakReference;
import java.util.List;

public class TaskRegisterActivityPresenter implements TaskRegisterActivityContract.Presenter, TaskRegisterActivityContract.InteractorCallBack {

    private TaskRegisterActivityInteractor taskRegisterActivityInteractor;

    private WeakReference<TaskRegisterActivityContract.View> viewWeakReference;

    public TaskRegisterActivityPresenter(TaskRegisterActivityContract.View view) {
        taskRegisterActivityInteractor = new TaskRegisterActivityInteractor();
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
    public void saveForm(@NonNull String encounterType, @Nullable JSONObject form,
                         @NonNull StructureDetail structureDetail) {
        taskRegisterActivityInteractor.saveForm(encounterType, form, structureDetail, this);
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        //DO NOTHING
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        //DO NOTHING
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //DO NOTHING
    }

    @Override
    public void updateInitials() {
        //DO NOTHING
    }

    @Override
    public void onFormSaved(String encounterType, boolean isSuccessful, Event event) {
        if (getView() != null) {
            getView().hideProgressDialog();
            if (!isSuccessful) {
                getView().displayToast(R.string.error_occurred_saving_form);
            }
        }
    }
}
