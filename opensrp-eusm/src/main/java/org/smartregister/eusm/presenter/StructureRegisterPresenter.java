package org.smartregister.eusm.presenter;

import androidx.annotation.NonNull;

import org.smartregister.domain.Task;
import org.smartregister.eusm.contract.BaseContract;
import org.smartregister.view.contract.BaseRegisterContract;

public class StructureRegisterPresenter extends BaseRegisterPresenter implements BaseContract.BasePresenter {

    private BaseRegisterContract.View view;

    public StructureRegisterPresenter(BaseRegisterContract.View view) {
        super(view);
    }


    @Override
    public void onFormSaved(@NonNull String structureId, String taskID, @NonNull Task.TaskStatus taskStatus, @NonNull String businessStatus, String interventionType) {

    }

    @Override
    public void onFormSaveFailure(String eventType) {

    }
}
