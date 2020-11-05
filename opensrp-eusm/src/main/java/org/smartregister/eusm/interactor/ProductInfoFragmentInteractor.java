package org.smartregister.eusm.interactor;

import android.app.Activity;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.model.StructureTaskDetail;
import org.smartregister.eusm.util.TestDataUtils;

import java.util.List;

import timber.log.Timber;

public class ProductInfoFragmentInteractor implements ProductInfoFragmentContract.Interactor {

    private AppExecutors appExecutors;

    private FormUtils formUtils;

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

    @Override
    public void markProductAsGood(StructureTaskDetail structureTaskDetail, ProductInfoFragmentContract.InteractorCallBack callBack) {

    }

    @Override
    public void startFlagProblemForm(String formName, Activity activity, ProductInfoFragmentContract.InteractorCallBack interactorCallBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                JSONObject form = getFormUtils().getFormJson(activity, formName);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        interactorCallBack.onFlagProblemFormFetched(form);
                    }
                });
            }
        });

    }

    private FormUtils getFormUtils() {
        try {
            formUtils = new FormUtils();
        } catch (Exception e) {
            Timber.e(e);
        }

        return formUtils;
    }
}
