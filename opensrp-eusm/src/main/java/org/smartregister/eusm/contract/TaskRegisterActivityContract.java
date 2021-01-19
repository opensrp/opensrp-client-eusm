package org.smartregister.eusm.contract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.view.contract.BaseRegisterContract;

public interface TaskRegisterActivityContract {

    interface View extends BaseRegisterContract.View {

        int getLayoutId();

        String getStructureIcon();

        String getStructureName();

        String getStructureType();

        String getDistance();

        String getCommune();

        TaskRegisterActivityContract.Presenter presenter();
    }

    interface Presenter extends BaseRegisterContract.Presenter {

        TaskRegisterActivityContract.View getView();

        void saveForm(@NonNull String encounterType, @Nullable JSONObject form,
                      @NonNull StructureDetail structureDetail);
    }

    interface Interactor {
        void saveForm(String encounterType, JSONObject form, StructureDetail structureDetail, InteractorCallBack interactorCallBack);

        void saveFixProblemForm(JSONObject form, InteractorCallBack interactorCallBack, StructureDetail structureDetail);

        void saveRecordGps(JSONObject form, InteractorCallBack interactorCallBack, StructureDetail structureDetail);

        void saveServicePointCheck(JSONObject form, InteractorCallBack interactorCallBack, StructureDetail structureDetail);

        void saveEventAndInitiateProcessing(String encounterType, JSONObject form, String bindType,
                                            InteractorCallBack interactorCallBack,
                                            String entityType);
    }

    interface InteractorCallBack {
        void onFormSaved(String encounterType, boolean isSuccessful, Event event);
    }
}
