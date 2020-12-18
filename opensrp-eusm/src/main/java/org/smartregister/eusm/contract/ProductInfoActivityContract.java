package org.smartregister.eusm.contract;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;

public interface ProductInfoActivityContract {
    interface View {
        String getProductName();

        String getProductSerial();

        String getProductImage();

        void setUpViews();

        void initializeFragment();

        ProductInfoActivityContract.Presenter presenter();

        void showProgressDialog(@StringRes int message);

        void hideDialog();

        void initializeDialog();

        ProgressDialog getDialog();

        TaskDetail getTaskDetail();

        StructureDetail getStructureDetail();

        Activity getActivity();

        void startForm(JSONObject jsonForm);
    }

    interface Presenter {
        void saveFlagProblemForm(TaskDetail taskDetail, String encounterType,
                                 JSONObject jsonForm, StructureDetail structureDetail);

        View getView();

        void startFlagProblemForm(StructureDetail structureDetail, TaskDetail taskDetail, String flagProblemForm);

        void markProductAsGood(StructureDetail structureDetail, TaskDetail taskDetail);
    }

    interface Interactor {
        void saveFlagProblemForm(TaskDetail taskDetail, String encounterType, JSONObject jsonForm,
                                 StructureDetail structureDetail, InteractorCallback interactorCallback);

        void saveEventAndInitiateProcessing(String encounterType, JSONObject form,
                                            String bindType, InteractorCallback interactorCallback,
                                            String entityId);

        void startFlagProblemForm(StructureDetail structureDetail, TaskDetail taskDetail, String formName, Activity activity, InteractorCallback interactorCallback);

        void markProductAsGood(StructureDetail structureDetail, TaskDetail taskDetail, InteractorCallback interactorCallback, Activity activity);
    }

    interface InteractorCallback {
        void onSavedFlagProblemTask(boolean isSaved, Event event);

        void onProductMarkedAsGood(boolean isMarked, Event event);

        void onFlagProblemFormFetched(JSONObject jsonForm);
    }
}
