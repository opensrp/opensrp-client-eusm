package org.smartregister.eusm.contract;

public interface ProductInfoActivityContract {
    interface View {
        String getProductName();

        String getProductSerial();

        String getProductImage();

        void setUpViews();

        void initializeFragment();
    }

    interface Presenter {
        void startForm(String formType);
    }
}
