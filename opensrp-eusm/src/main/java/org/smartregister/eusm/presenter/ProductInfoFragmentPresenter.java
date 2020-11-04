package org.smartregister.eusm.presenter;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.interactor.ProductInfoFragmentInteractor;
import org.smartregister.eusm.model.ProductInfoQuestion;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

public class ProductInfoFragmentPresenter implements ProductInfoFragmentContract.Presenter, ProductInfoFragmentContract.InteractorCallBack {

    private WeakReference<ProductInfoFragmentContract.View> viewWeakReference;

    private ProductInfoFragmentInteractor interactor;

    private FormUtils formUtils;

    public ProductInfoFragmentPresenter(ProductInfoFragmentContract.View view) {
        viewWeakReference = new WeakReference<>(view);
        interactor = new ProductInfoFragmentInteractor();
    }

    @Override
    public ProductInfoFragmentContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        }
        return null;
    }

    @Override
    public void fetchProductQuestions() {
        interactor.fetchQuestions(this);
    }

    @Override
    public void startFlagProblemForm(String formName) {
        JSONObject form = getFormUtils().getFormJson(getView().getActivity(), formName);
        getView().startFlagProblemForm(form);
    }

    @Override
    public void onQuestionsFetched(List<ProductInfoQuestion> productInfoQuestions) {
        if (getView().getAdapter() != null) {
            getView().getAdapter().setData(productInfoQuestions);
        }
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
