package org.smartregister.eusm.presenter;

import android.content.Intent;

import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.interactor.ProductInfoActivityInteractor;

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
    public void saveFlagProblemTask(String encounterType, Intent data) {
        if (getView() != null) {
            getView().showDialog("Saving Flag Problem");
        }
        productInfoActivityInteractor.saveFlagProblemTask(encounterType, data, this::onSavedFlagProblemTask);
    }

    @Override
    public ProductInfoActivityContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        }
        return null;
    }

    @Override
    public void onSavedFlagProblemTask(boolean isSaved) {
        if (getView() != null) {
            getView().hideDialog();
        }
    }
}
