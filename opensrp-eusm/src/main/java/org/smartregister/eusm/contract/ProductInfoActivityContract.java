package org.smartregister.eusm.contract;

import android.app.ProgressDialog;
import android.content.Intent;

public interface ProductInfoActivityContract {
    interface View {
        String getProductName();

        String getProductSerial();

        String getProductImage();

        void setUpViews();

        void initializeFragment();

        ProductInfoActivityContract.Presenter presenter();

        void showDialog(String message);

        void hideDialog();

        void initializeDialog();

        ProgressDialog getDialog();
    }

    interface Presenter {
        void saveFlagProblemTask(String encounterType, Intent data);

        View getView();
    }

    interface Interactor {
        void saveFlagProblemTask(String encounterType, Intent data, InteractorCallback interactorCallback);
    }

    interface InteractorCallback {
        void onSavedFlagProblemTask(boolean isSaved);
    }
}
