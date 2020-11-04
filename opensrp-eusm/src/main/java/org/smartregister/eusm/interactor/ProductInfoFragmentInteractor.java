package org.smartregister.eusm.interactor;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.util.TestDataUtils;

import java.util.List;

public class ProductInfoFragmentInteractor implements ProductInfoFragmentContract.Interactor {

    private AppExecutors appExecutors;

    public ProductInfoFragmentInteractor() {
        appExecutors = EusmApplication.getInstance().getAppExecutors();
    }

    @Override
    public void fetchQuestions(ProductInfoFragmentContract.InteractorCallBack callBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<ProductInfoQuestion> productInfoQuestions = TestDataUtils.getProductInfoQuestionLIst();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onQuestionsFetched(productInfoQuestions);
                    }
                });
            }
        });
    }
}
