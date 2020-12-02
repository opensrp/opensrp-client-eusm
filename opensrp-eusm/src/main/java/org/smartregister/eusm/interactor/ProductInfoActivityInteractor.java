package org.smartregister.eusm.interactor;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppUtils;

import java.util.Collections;

import timber.log.Timber;

public class ProductInfoActivityInteractor implements ProductInfoActivityContract.Interactor {

    private AppJsonFormUtils jsonFormUtils;

    @Override
    public void saveFlagProblemForm(TaskDetail taskDetail,
                                    String encounterType, JSONObject jsonForm,
                                    StructureDetail structureDetail, ProductInfoActivityContract.InteractorCallback interactorCallback) {

        getJsonFormUtils().saveImage(jsonForm, AppConstants.EventEntityType.PRODUCT);

        saveEventAndInitiateProcessing(encounterType, jsonForm, "", interactorCallback,
                AppConstants.EventEntityType.PRODUCT);
    }

    @Override
    public void saveEventAndInitiateProcessing(String encounterType, JSONObject form,
                                               String bindType, ProductInfoActivityContract.InteractorCallback interactorCallback,
                                               String entityId) {
        try {
            Event event = AppUtils.createEventFromJsonForm(form, encounterType, bindType, entityId);
            try {
                AppUtils.initiateEventProcessing(Collections.singletonList(event.getFormSubmissionId()));
                interactorCallback.onSavedFlagProblemTask(true);
            } catch (Exception e) {
                Timber.e(e);
                interactorCallback.onSavedFlagProblemTask(false);
            }
        } catch (JSONException e) {
            Timber.e(e);
            interactorCallback.onSavedFlagProblemTask(false);
        }
    }

    public AppJsonFormUtils getJsonFormUtils() {
        if (jsonFormUtils == null) {
            jsonFormUtils = new AppJsonFormUtils();
        }
        return jsonFormUtils;
    }
}
