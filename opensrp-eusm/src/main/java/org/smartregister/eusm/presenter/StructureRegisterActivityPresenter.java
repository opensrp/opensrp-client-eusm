package org.smartregister.eusm.presenter;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Feature;

import org.json.JSONArray;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.tasking.contract.BaseContract;
import org.smartregister.tasking.contract.UserLocationContract;
import org.smartregister.tasking.model.TaskFilterParams;
import org.smartregister.view.contract.BaseRegisterContract;

public class StructureRegisterActivityPresenter extends BaseRegisterPresenter implements BaseContract.BasePresenter {

    public StructureRegisterActivityPresenter(BaseRegisterContract.View view) {
        super(view);
    }

    @Override
    public void onFormSaved(@NonNull String structureId, String taskID, @NonNull Task.TaskStatus taskStatus, @NonNull String businessStatus, String interventionType) {
        //do nothing
    }

    @Override
    public void onStructureAdded(Feature feature, JSONArray jsonArray, double v) {
        //do nothing
    }

    @Override
    public void onFormSaveFailure(String eventType) {
        //do nothing
    }

    @Override
    public void onFamilyFound(CommonPersonObjectClient commonPersonObjectClient) {
        //do nothing
    }

    @Override
    public void filterTasks(TaskFilterParams taskFilterParams) {
        //do nothing
    }

    @Override
    public UserLocationContract.UserLocationPresenter getLocationPresenter() {
        return null;
    }
}
