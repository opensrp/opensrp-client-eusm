package org.smartregister.eusm.interactor;

import android.app.Activity;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.tasking.receiver.TaskGenerationReceiver;
import org.smartregister.util.AppExecutors;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static org.smartregister.AllConstants.INTENT_KEY.TASK_GENERATED_EVENT;

public class ProductInfoActivityInteractor implements ProductInfoActivityContract.Interactor {

    private final AppExecutors appExecutors;
    private AppJsonFormUtils jsonFormUtils;

    public ProductInfoActivityInteractor() {
        appExecutors = EusmApplication.getInstance().getAppExecutors();
    }

    @Override
    public void saveFlagProblemForm(TaskDetail taskDetail,
                                    String encounterType, JSONObject jsonForm,
                                    StructureDetail structureDetail, ProductInfoActivityContract.InteractorCallback interactorCallback) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                getJsonFormUtils().saveImage(jsonForm, AppConstants.EventEntityType.PRODUCT);
                saveEventAndInitiateProcessing(encounterType, jsonForm, "", interactorCallback,
                        AppConstants.EventEntityType.PRODUCT);
            }
        });
    }

    @Override
    public void saveEventAndInitiateProcessing(String encounterType, JSONObject form,
                                               String bindType, ProductInfoActivityContract.InteractorCallback interactorCallback,
                                               String entityId) {
        try {
            Event event = AppUtils.createEventFromJsonForm(form, encounterType, bindType, entityId);
            try {
                AtomicInteger count = new AtomicInteger(1);
                IntentFilter filter = new IntentFilter(TASK_GENERATED_EVENT);
                TaskGenerationReceiver taskGenerationReceiver = new TaskGenerationReceiver(task ->
                        appExecutors.mainThread().execute(() -> returnResponse(interactorCallback, event, true, count.getAndIncrement())),
                        AppConstants.EncounterType.FLAG_PROBLEM.equals(encounterType) ? 2 : 1);
                LocalBroadcastManager.getInstance(EusmApplication.getInstance().getApplicationContext()).registerReceiver(taskGenerationReceiver, filter);

                AppUtils.initiateEventProcessing(Collections.singletonList(event.getFormSubmissionId()));
            } catch (Exception e) {
                Timber.e(e);
                returnResponse(interactorCallback, event, false, 0);
            }
        } catch (JSONException e) {
            Timber.e(e);
            returnResponse(interactorCallback, null, false, 0);
        }
    }

    @Override
    public void markProductAsGood(StructureDetail structureDetail, TaskDetail taskDetail, ProductInfoActivityContract.InteractorCallback callBack,
                                  Activity activity) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = getJsonFormUtils()
                        .getFormObjectWithDetails(activity, AppConstants.JsonForm.LOOKS_GOOD, structureDetail, taskDetail);
                saveEventAndInitiateProcessing(AppConstants.EncounterType.LOOKS_GOOD, jsonObject,
                        "", callBack, AppConstants.EventEntityType.PRODUCT);
            }
        });
    }

    @Override
    public void startFlagProblemForm(StructureDetail structureDetail, TaskDetail taskDetail, String formName, Activity activity,
                                     ProductInfoActivityContract.InteractorCallback interactorCallBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject form = getJsonFormUtils().getFormObjectWithDetails(activity, formName, structureDetail, taskDetail);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        interactorCallBack.onFlagProblemFormFetched(form);
                    }
                });
            }
        });
    }

    private void returnResponse(ProductInfoActivityContract.InteractorCallback interactorCallback,
                                Event event, boolean status, int callCount) {
        if (event != null && event.getDetails() != null) {
            String encounterType = event.getEventType();
            if (AppConstants.EncounterType.FLAG_PROBLEM.equals(encounterType)) {
                if (status && callCount < 2) {
                    return;
                }
                interactorCallback.onSavedFlagProblemTask(status, event);
            } else if (AppConstants.EncounterType.LOOKS_GOOD.equals(encounterType)) {
                interactorCallback.onProductMarkedAsGood(status, event);
            }
        }
    }

    public AppJsonFormUtils getJsonFormUtils() {
        if (jsonFormUtils == null) {
            jsonFormUtils = new AppJsonFormUtils();
        }
        return jsonFormUtils;
    }
}
