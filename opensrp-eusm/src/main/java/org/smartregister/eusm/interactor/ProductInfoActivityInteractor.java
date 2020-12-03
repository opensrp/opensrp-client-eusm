package org.smartregister.eusm.interactor;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.repository.AppTaskRepository;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.util.AppExecutors;

import java.util.Collections;
import java.util.UUID;

import timber.log.Timber;

public class ProductInfoActivityInteractor implements ProductInfoActivityContract.Interactor {

    private AppJsonFormUtils jsonFormUtils;

    private AppExecutors appExecutors;

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

    private void returnResponse(ProductInfoActivityContract.InteractorCallback interactorCallback, Event event, boolean status) {
        if (status && event != null && event.getDetails() != null) {
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
            }
        }
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                interactorCallback.onSavedFlagProblemTask(status, event);
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
