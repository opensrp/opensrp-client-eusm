package org.smartregister.eusm.presenter;

import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.domain.ProductInfoQuestion;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.interactor.ProductInfoFragmentInteractor;

import java.lang.ref.WeakReference;
import java.util.List;

public class ProductInfoFragmentPresenter implements ProductInfoFragmentContract.Presenter, ProductInfoFragmentContract.InteractorCallBack {

    private final WeakReference<ProductInfoFragmentContract.View> viewWeakReference;

    private final ProductInfoFragmentInteractor interactor;

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
    public void fetchProductQuestions(TaskDetail taskDetail) {
        interactor.fetchQuestions(taskDetail, this);
    }

    @Override
    public void onQuestionsFetched(List<ProductInfoQuestion> productInfoQuestions) {
        if (getView() != null && getView().getAdapter() != null) {
            getView().getAdapter().setData(productInfoQuestions);
        }
    }
}
