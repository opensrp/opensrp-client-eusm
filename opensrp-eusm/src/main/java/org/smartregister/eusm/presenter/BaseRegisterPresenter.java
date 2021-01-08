package org.smartregister.eusm.presenter;

import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

public class BaseRegisterPresenter implements BaseRegisterContract.Presenter {

    public BaseRegisterPresenter(BaseRegisterContract.View view) {
        //do nothing
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        //do nothing
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        //do nothing
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //do nothing
    }

    @Override
    public void updateInitials() {//do nothing
    }
}
