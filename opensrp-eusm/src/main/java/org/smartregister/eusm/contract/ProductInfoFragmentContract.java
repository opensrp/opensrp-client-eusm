package org.smartregister.eusm.contract;

import android.app.Activity;

import org.json.JSONObject;
import org.smartregister.eusm.adapter.ProductInfoQuestionsAdapter;
import org.smartregister.eusm.model.ProductInfoQuestion;

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
    }

    interface Interactor {
        void fetchQuestions(InteractorCallBack callBack);

    }

    interface InteractorCallBack {
        void onQuestionsFetched(List<ProductInfoQuestion> list);
    }
}
