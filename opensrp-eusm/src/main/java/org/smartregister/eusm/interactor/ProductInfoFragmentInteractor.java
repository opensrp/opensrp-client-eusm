package org.smartregister.eusm.interactor;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.model.ProductInfoFragmentModel;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.util.AppExecutors;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class ProductInfoFragmentInteractor implements ProductInfoFragmentContract.Interactor {

    private final AppExecutors appExecutors;

    private AppJsonFormUtils jsonFormUtils;

    private ProductInfoFragmentModel productInfoFragmentModel;

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
        JSONObject jsonObject = getJsonFormUtils().getFormObjectWithDetails(activity, AppConstants.JsonForm.LOOKS_GOOD, structureDetail, taskDetail);
        saveEventAndInitiateProcessing(AppConstants.EncounterType.LOOKS_GOOD, jsonObject,
                "", callBack, AppConstants.EventEntityType.PRODUCT);
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
                interactorCallback.onProductMarkedAsGood(true, event);
            } catch (Exception e) {
                Timber.e(e);
                interactorCallback.onProductMarkedAsGood(false, null);
            }
        } catch (JSONException e) {
            Timber.e(e);
            interactorCallback.onProductMarkedAsGood(false, null);
        }
    }

    @Override
    public AppJsonFormUtils getJsonFormUtils() {
        if (jsonFormUtils == null) {
            jsonFormUtils = new AppJsonFormUtils();
        }
        return jsonFormUtils;
    }
}
