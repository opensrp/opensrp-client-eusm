package org.smartregister.eusm.contract;

import android.app.Activity;

import androidx.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.adapter.ProductInfoQuestionsAdapter;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.util.AppJsonFormUtils;

import java.util.List;

public interface ProductInfoFragmentContract {
    interface View {
        void initializeProgressDialog();

        void showProgressDialog(@StringRes int message);

        void hideProgressDialog();

        void initializeAdapter();

        ProductInfoQuestionsAdapter getAdapter();

        void initializePresenter();

        Activity getActivity();

        void startFlagProblemForm(JSONObject form);
    }

    interface Presenter {
        View getView();

        void fetchProductQuestions(TaskDetail taskDetail);

        void startFlagProblemForm(StructureDetail structureDetail, TaskDetail taskDetail, String formName);

        void markProductAsGood(StructureDetail structureDetail, TaskDetail taskDetail);
    }

    interface Interactor {
        void fetchQuestions(TaskDetail taskDetail, InteractorCallBack callBack);

        void markProductAsGood(StructureDetail structureDetail, TaskDetail taskDetail, InteractorCallBack callBack,
                               Activity activity);

        void startFlagProblemForm(StructureDetail structureDetail, TaskDetail taskDetail, String formName, Activity activity, InteractorCallBack interactorCallBack);

        void saveEventAndInitiateProcessing(String encounterType, JSONObject form,
                                            String bindType, InteractorCallBack interactorCallback,
                                            String entityType);

        AppJsonFormUtils getJsonFormUtils();
    }

    interface InteractorCallBack {
        void onQuestionsFetched(List<ProductInfoQuestion> list);

        void onProductMarkedAsGood(boolean isMarked, Event event);

        void onFlagProblemFormFetched(JSONObject jsonForm);
    }
}
