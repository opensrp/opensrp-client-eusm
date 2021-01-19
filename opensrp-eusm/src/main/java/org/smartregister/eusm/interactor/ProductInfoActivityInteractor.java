package org.smartregister.eusm.interactor;

import android.app.Activity;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.repository.AppTaskRepository;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.util.AppExecutors;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

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
                AppUtils.initiateEventProcessing(Collections.singletonList(event.getFormSubmissionId()));
                returnResponse(interactorCallback, event, true);
            } catch (Exception e) {
                Timber.e(e);
                returnResponse(interactorCallback, event, false);
            }
        } catch (JSONException e) {
            Timber.e(e);
            returnResponse(interactorCallback, null, false);
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

    private void returnResponse(ProductInfoActivityContract.InteractorCallback interactorCallback, Event event, boolean status) {
        if (event != null && event.getDetails() != null) {
            String encounterType = event.getEventType();

            if (AppConstants.EncounterType.FLAG_PROBLEM.equals(encounterType)) {
                AppTaskRepository taskRepository = EusmApplication.getInstance().getAppTaskRepository();
                String taskId = event.getDetails().get(AppConstants.Properties.TASK_IDENTIFIER);
                if (StringUtils.isNotBlank(taskId)) {
                    //TODO to be replaced by event submission
                    taskRepository.updateTaskStatus(taskId, Task.TaskStatus.COMPLETED, AppConstants.BusinessStatus.HAS_PROBLEM);
                }

                Task task = new Task();
                task.setIdentifier(UUID.randomUUID().toString());
                task.setPlanIdentifier(AppConstants.PLAN_IDENTIFIER);
                task.setStatus(Task.TaskStatus.READY);
                task.setPriority(Task.TaskPriority.ROUTINE);
                task.setBusinessStatus("NOT VISITED");
                task.setFocus("Fix Problem");
                task.setAuthoredOn(new DateTime());
                task.setOwner("demo");
                task.setLastModified(new DateTime());
                task.setGroupIdentifier(UUID.randomUUID().toString());
                task.setForEntity(event.getBaseEntityId());
                task.setLocation(event.getLocationId());
                task.setCode(AppConstants.EncounterType.FIX_PROBLEM);
                taskRepository.addOrUpdate(task, false);
            } else if (AppConstants.EncounterType.LOOKS_GOOD.equals(encounterType)) {
                Map<String, String> map = event.getDetails();
                if (map != null) {
                    String taskId = map.get(AppConstants.Properties.TASK_IDENTIFIER);
                    if (StringUtils.isNotBlank(taskId)) {
                        //TODO to be replaced by event submission
                        AppTaskRepository taskRepository = EusmApplication.getInstance().getAppTaskRepository();
                        taskRepository.updateTaskStatus(taskId, Task.TaskStatus.COMPLETED, "VISITED");
                    }
                }
            }
        }
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (event != null) {
                    String encounterType = event.getEventType();
                    if (AppConstants.EncounterType.FLAG_PROBLEM.equals(encounterType)) {
                        interactorCallback.onSavedFlagProblemTask(status, event);
                    } else if (AppConstants.EncounterType.LOOKS_GOOD.equals(encounterType)) {
                        interactorCallback.onProductMarkedAsGood(status, event);
                    }
                }
            }
        });
    }

    public AppJsonFormUtils getJsonFormUtils() {
        if (jsonFormUtils == null) {
            jsonFormUtils = new AppJsonFormUtils();
        }
        return jsonFormUtils;
    }
}
