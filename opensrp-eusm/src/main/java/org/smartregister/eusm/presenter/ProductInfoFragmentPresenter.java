package org.smartregister.eusm.presenter;

import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.domain.ProductInfoQuestion;
import org.smartregister.eusm.domain.StructureDetail;
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
        if (getView() != null) {
            getView().initializeProgressDialog();
        }
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
    public void startFlagProblemForm(StructureDetail structureDetail, TaskDetail taskDetail, String formName) {
        interactor.startFlagProblemForm(structureDetail, taskDetail, formName, getView().getActivity(), this);
    }

    @Override
    public void markProductAsGood(StructureDetail structureDetail, TaskDetail taskDetail) {
        if (getView() != null) {
            getView().showProgressDialog(R.string.looks_good_save_dialog);
        }
        interactor.markProductAsGood(structureDetail, taskDetail, this, getView().getActivity());
    }

    @Override
    public void onQuestionsFetched(List<ProductInfoQuestion> productInfoQuestions) {
        if (getView() != null && getView().getAdapter() != null) {
            getView().getAdapter().setData(productInfoQuestions);
        }
    }

    @Override
    public void onProductMarkedAsGood(boolean isMarked, Event event) {
        if (getView() != null) {
            getView().hideProgressDialog();
            getView().getActivity().finish();
        }
    }

    @Override
    public void onFlagProblemFormFetched(JSONObject jsonForm) {
        getView().startFlagProblemForm(jsonForm);
    }
}
