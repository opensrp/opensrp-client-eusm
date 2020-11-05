package org.smartregister.eusm.presenter;

import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.interactor.ProductInfoFragmentInteractor;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.model.StructureTaskDetail;

import java.lang.ref.WeakReference;
import java.util.List;

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
        interactor.startFlagProblemForm(formName, getView().getActivity(), this);
    }

    @Override
    public void markProductAsGood(StructureTaskDetail structureTaskDetail) {
        interactor.markProductAsGood(structureTaskDetail, this);
    }

    @Override
    public void onQuestionsFetched(List<ProductInfoQuestion> productInfoQuestions) {
        if (getView().getAdapter() != null) {
            getView().getAdapter().setData(productInfoQuestions);
        }
    }

    @Override
    public void onProductMarkedAsGood(boolean isMarked) {
    }

    @Override
    public void onFlagProblemFormFetched(JSONObject jsonForm) {
        getView().startFlagProblemForm(jsonForm);
    }

}
