package org.smartregister.eusm.contract;

import android.app.Activity;

import org.smartregister.eusm.adapter.ProductInfoQuestionsAdapter;
import org.smartregister.eusm.domain.ProductInfoQuestion;
import org.smartregister.eusm.domain.TaskDetail;

import java.util.List;

public interface ProductInfoFragmentContract {
    interface View {
        void initializeAdapter();

        ProductInfoQuestionsAdapter getAdapter();

        void initializePresenter();

        Activity getActivity();
    }

    interface Presenter {
        View getView();

        void fetchProductQuestions(TaskDetail taskDetail);
    }

    interface Interactor {
        void fetchQuestions(TaskDetail taskDetail, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onQuestionsFetched(List<ProductInfoQuestion> list);
    }
}
