package org.smartregister.eusm.presenter;

import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.interactor.ProductInfoActivityInteractor;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;

import java.lang.ref.WeakReference;

public class ProductInfoActivityPresenter implements ProductInfoActivityContract.Presenter, ProductInfoActivityContract.InteractorCallback {

    private ProductInfoActivityInteractor productInfoActivityInteractor;

    private WeakReference<ProductInfoActivityContract.View> viewWeakReference;

    public ProductInfoActivityPresenter(ProductInfoActivityContract.View view) {
        viewWeakReference = new WeakReference<>(view);
        productInfoActivityInteractor = new ProductInfoActivityInteractor();
        if (getView() != null) {
            getView().initializeDialog();
        }
    }

    @Override
    public void saveFlagProblemForm(TaskDetail taskDetail, String encounterType,
                                    JSONObject jsonForm, StructureDetail structureDetail) {
        if (getView() != null) {
            getView().showProgressDialog(R.string.saving_message);
        }
        productInfoActivityInteractor.saveFlagProblemForm(taskDetail,
                encounterType,
                jsonForm,
                structureDetail,
                this);
    }

    @Override
    public ProductInfoActivityContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        }
        return null;
    }

    @Override
    public void onSavedFlagProblemTask(boolean isSaved, Event event) {
        if (getView() != null) {
            getView().hideDialog();
            getView().getActivity().finish();
        }
    }
}
