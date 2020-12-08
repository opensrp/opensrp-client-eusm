package org.smartregister.eusm.interactor;

import android.app.Activity;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.domain.ProductInfoQuestion;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.model.ProductInfoFragmentModel;
import org.smartregister.eusm.repository.AppTaskRepository;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.util.AppExecutors;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ProductInfoFragmentInteractor implements ProductInfoFragmentContract.Interactor {

    private final AppExecutors appExecutors;

    private AppJsonFormUtils jsonFormUtils;

    private final ProductInfoFragmentModel productInfoFragmentModel;

    public ProductInfoFragmentInteractor() {
        appExecutors = EusmApplication.getInstance().getAppExecutors();
        productInfoFragmentModel = new ProductInfoFragmentModel();
    }

    @Override
    public void fetchQuestions(TaskDetail taskDetail, ProductInfoFragmentContract.InteractorCallBack callBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<ProductInfoQuestion> productInfoQuestions = productInfoFragmentModel.getProductInfoQuestions(taskDetail);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onQuestionsFetched(productInfoQuestions);
                    }
                });
            }
        });
    }

    @Override
    public void markProductAsGood(StructureDetail structureDetail, TaskDetail taskDetail, ProductInfoFragmentContract.InteractorCallBack callBack,
                                  Activity activity) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = getJsonFormUtils().getFormObjectWithDetails(activity, AppConstants.JsonForm.LOOKS_GOOD, structureDetail, taskDetail);
                saveEventAndInitiateProcessing(AppConstants.EncounterType.LOOKS_GOOD, jsonObject,
                        "", callBack, AppConstants.EventEntityType.PRODUCT);
            }
        });
    }

    @Override
    public void startFlagProblemForm(StructureDetail structureDetail, TaskDetail taskDetail, String formName, Activity activity,
                                     ProductInfoFragmentContract.InteractorCallBack interactorCallBack) {
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

    @Override
    public void saveEventAndInitiateProcessing(String encounterType, JSONObject form,
                                               String bindType, ProductInfoFragmentContract.InteractorCallBack interactorCallback,
                                               String entityType) {
        try {
            Event event = AppUtils.createEventFromJsonForm(form, encounterType, bindType,
                    entityType);
            try {
                AppUtils.initiateEventProcessing(Collections.singletonList(event.getFormSubmissionId()));
                returnResponse(interactorCallback, event, true);
            } catch (Exception e) {
                Timber.e(e);
                returnResponse(interactorCallback, null, false);
            }
        } catch (JSONException e) {
            Timber.e(e);
            returnResponse(interactorCallback, null, false);
        }
    }

    private void returnResponse(ProductInfoFragmentContract.InteractorCallBack interactorCallback, Event event, boolean status) {
        if (status && event != null) {
            String encounterType = event.getEventType();
            if (AppConstants.EncounterType.LOOKS_GOOD.equals(encounterType)) {
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
                interactorCallback.onProductMarkedAsGood(status, event);
            }
        });
    }

    @Override
    public AppJsonFormUtils getJsonFormUtils() {
        if (jsonFormUtils == null) {
            jsonFormUtils = new AppJsonFormUtils();
        }
        return jsonFormUtils;
    }
}
