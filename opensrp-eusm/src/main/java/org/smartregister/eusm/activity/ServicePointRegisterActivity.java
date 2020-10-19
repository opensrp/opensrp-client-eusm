package org.smartregister.eusm.activity;


import android.content.Intent;
import android.os.Bundle;

import org.smartregister.eusm.fragment.ServicePointRegisterFragment;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;

public class ServicePointRegisterActivity extends BaseAppRegisterActivity {


    @Override
    protected void initializePresenter() {

    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        ServicePointRegisterFragment servicePointRegisterFragment = new ServicePointRegisterFragment();
        Bundle bundle = new Bundle();
        servicePointRegisterFragment.setArguments(bundle);
        return servicePointRegisterFragment;
    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {

    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }
}