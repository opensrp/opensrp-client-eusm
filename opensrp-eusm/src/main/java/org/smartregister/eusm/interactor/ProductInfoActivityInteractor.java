package org.smartregister.eusm.interactor;

import android.content.Intent;

import org.smartregister.eusm.contract.ProductInfoActivityContract;

public class ProductInfoActivityInteractor implements ProductInfoActivityContract.Interactor {

    @Override
    public void saveFlagProblemTask(String encounterType, Intent data, ProductInfoActivityContract.InteractorCallback interactorCallback) {
        //TODO submitting this form completes the task for that product and also triggers the "Fix problem" task, which appears in the task list for that service point.
    }
}
