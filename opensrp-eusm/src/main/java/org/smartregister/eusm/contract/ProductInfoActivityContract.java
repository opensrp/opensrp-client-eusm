package org.smartregister.eusm.contract;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;

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
    }

    interface Presenter {
        void saveFlagProblemForm(TaskDetail taskDetail, String encounterType,
                                 JSONObject jsonForm, StructureDetail structureDetail);

        View getView();
    }

    interface Interactor {
        void saveFlagProblemForm(TaskDetail taskDetail, String encounterType, JSONObject jsonForm,
                                 StructureDetail structureDetail, InteractorCallback interactorCallback);

        void saveEventAndInitiateProcessing(String encounterType, JSONObject form,
                                            String bindType, InteractorCallback interactorCallback,
                                            String entityId);
    }

    interface InteractorCallback {
        void onSavedFlagProblemTask(boolean isSaved, Event event);
    }
}
