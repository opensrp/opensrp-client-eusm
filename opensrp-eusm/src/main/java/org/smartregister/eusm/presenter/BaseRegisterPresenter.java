package org.smartregister.eusm.presenter;

import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

public class BaseRegisterPresenter implements BaseRegisterContract.Presenter {

    private final BaseRegisterContract.View view;

    public BaseRegisterPresenter(BaseRegisterContract.View view) {
        this.view = view;
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
    public void updateInitials() {
        //do nothing
    }

    public BaseRegisterContract.View getView() {
        return view;
    }
}
