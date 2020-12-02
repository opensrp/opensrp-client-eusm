package org.smartregister.eusm.interactor;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.contract.TaskRegisterActivityContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;

import java.util.Collections;

import timber.log.Timber;

public class TaskRegisterActivityInteractor implements TaskRegisterActivityContract.Interactor {

    @Override
    public void saveForm(String encounterType, JSONObject form, StructureDetail structureDetail,
                         TaskRegisterActivityContract.InteractorCallBack interactorCallBack) {
        if (AppConstants.EncounterType.FIX_PROBLEM.equals(encounterType)) {
            saveFixProblemForm(form, interactorCallBack, structureDetail);
        } else if (AppConstants.EncounterType.RECORD_GPS.equals(encounterType)) {
            saveRecordGps(form, interactorCallBack, structureDetail);
        } else if (AppConstants.EncounterType.SERVICE_POINT_CHECK.equals(encounterType)) {
            saveServicePointCheck(form, interactorCallBack, structureDetail);
        }
    }

    @Override
    public void saveFixProblemForm(JSONObject form,
                                   TaskRegisterActivityContract.InteractorCallBack interactorCallBack,
                                   StructureDetail structureDetail) {
        saveEventAndInitiateProcessing(AppConstants.EncounterType.FIX_PROBLEM,
                form, "", interactorCallBack, AppConstants.EventEntityType.PRODUCT);
        //updates the task
    }

    @Override
    public void saveRecordGps(JSONObject form,
                              TaskRegisterActivityContract.InteractorCallBack interactorCallBack, StructureDetail structureDetail) {
        saveEventAndInitiateProcessing(AppConstants.EncounterType.RECORD_GPS,
                form, "", interactorCallBack, AppConstants.EventEntityType.SERVICE_POINT);
        //updates the task
    }

    @Override
    public void saveServicePointCheck(JSONObject form,
                                      TaskRegisterActivityContract.InteractorCallBack interactorCallBack, StructureDetail structureDetail) {
        saveEventAndInitiateProcessing(AppConstants.EncounterType.SERVICE_POINT_CHECK,
                form, "", interactorCallBack, AppConstants.EventEntityType.SERVICE_POINT);
        //updates the task
    }

    @Override
    public void saveEventAndInitiateProcessing(String encounterType, JSONObject form, String bindType,
                                               TaskRegisterActivityContract.InteractorCallBack interactorCallBack,
                                               String entityType) {
        try {
            Event event = AppUtils.createEventFromJsonForm(form, encounterType, bindType, entityType);
            try {
                AppUtils.initiateEventProcessing(Collections.singletonList(event.getFormSubmissionId()));
                interactorCallBack.onFormSaved(encounterType, true);
            } catch (Exception e) {
                Timber.e(e);
                interactorCallBack.onFormSaved(encounterType, false);
            }
        } catch (JSONException e) {
            Timber.e(e);
            interactorCallBack.onFormSaved(encounterType, false);
        }
    }
}
