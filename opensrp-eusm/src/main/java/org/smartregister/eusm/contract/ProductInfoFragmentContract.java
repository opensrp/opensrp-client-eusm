package org.smartregister.eusm.contract;

import android.app.Activity;

import org.json.JSONObject;
import org.smartregister.eusm.adapter.ProductInfoQuestionsAdapter;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.model.StructureTaskDetail;

import java.util.List;

public interface ProductInfoFragmentContract {
    interface View {
        void initializeAdapter();

        ProductInfoQuestionsAdapter getAdapter();

        void initializePresenter();

        Activity getActivity();

        void startFlagProblemForm(JSONObject form);
    }

    interface Presenter {
        View getView();

        void fetchProductQuestions();

        void startFlagProblemForm(String formName);

        void markProductAsGood(StructureTaskDetail structureTaskDetail);
    }

    interface Interactor {
        void fetchQuestions(InteractorCallBack callBack);

        void markProductAsGood(StructureTaskDetail structureTaskDetail, InteractorCallBack callBack);

        void startFlagProblemForm(String formName, Activity activity, InteractorCallBack interactorCallBack);
    }

    interface InteractorCallBack {
        void onQuestionsFetched(List<ProductInfoQuestion> list);

        void onProductMarkedAsGood(boolean isMarked);

        void onFlagProblemFormFetched(JSONObject jsonForm);
    }
}
