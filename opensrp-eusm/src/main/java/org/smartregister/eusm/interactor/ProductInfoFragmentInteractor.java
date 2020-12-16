package org.smartregister.eusm.interactor;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.domain.ProductInfoQuestion;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.model.ProductInfoFragmentModel;
import org.smartregister.util.AppExecutors;

import java.util.List;

public class ProductInfoFragmentInteractor implements ProductInfoFragmentContract.Interactor {

    private final AppExecutors appExecutors;

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

}
