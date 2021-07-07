package org.smartregister.eusm.interactor;

import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.TaskRegisterActivityContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.tasking.receiver.TaskGenerationReceiver;
import org.smartregister.util.AppExecutors;

import java.util.Collections;

import timber.log.Timber;

import static org.smartregister.AllConstants.INTENT_KEY.TASK_GENERATED_EVENT;

public class TaskRegisterActivityInteractor implements TaskRegisterActivityContract.Interactor {

    private final AppExecutors appExecutors;

    public TaskRegisterActivityInteractor() {
        appExecutors = EusmApplication.getInstance().getAppExecutors();
    }

    @Override
    public void saveForm(String encounterType, JSONObject form, StructureDetail structureDetail,
                         TaskRegisterActivityContract.InteractorCallBack interactorCallBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (AppConstants.EncounterType.FIX_PROBLEM.equals(encounterType)) {
                    saveFixProblemForm(form, interactorCallBack, structureDetail);
                } else if (AppConstants.EncounterType.RECORD_GPS.equals(encounterType)) {
                    saveRecordGps(form, interactorCallBack, structureDetail);
                } else if (AppConstants.EncounterType.SERVICE_POINT_CHECK.equals(encounterType)) {
                    saveServicePointCheck(form, interactorCallBack, structureDetail);
                }

            }
        });
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

        //updates the structure with coordinates
        AppUtils.updateLocationCoordinatesFromForm(structureDetail, form);
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
                IntentFilter filter = new IntentFilter(TASK_GENERATED_EVENT);
                TaskGenerationReceiver taskGenerationReceiver = new TaskGenerationReceiver(task -> appExecutors.mainThread().execute(() -> returnResponse(encounterType, interactorCallBack, event, true)));
                LocalBroadcastManager.getInstance(EusmApplication.getInstance().getApplicationContext()).registerReceiver(taskGenerationReceiver, filter);
                AppUtils.initiateEventProcessing(Collections.singletonList(event.getFormSubmissionId()));
            } catch (Exception e) {
                Timber.e(e);
                returnResponse(encounterType, interactorCallBack, event, false);
            }
        } catch (JSONException e) {
            Timber.e(e);
            returnResponse(encounterType, interactorCallBack, null, false);
        }
    }

    private void returnResponse(String encounterType, TaskRegisterActivityContract.InteractorCallBack interactorCallBack,
                                Event event, boolean status) {
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                interactorCallBack.onFormSaved(encounterType, status, event);
            }
        });
    }
}
