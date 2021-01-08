package org.smartregister.eusm.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.smartregister.eusm.R;
import org.smartregister.eusm.fragment.StructureRegisterFragment;
import org.smartregister.eusm.presenter.StructureRegisterActivityPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;

public class StructureRegisterActivity extends BaseAppRegisterActivity {

    @Override
    protected void initializePresenter() {
        presenter = new StructureRegisterActivityPresenter(this);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        StructureRegisterFragment structureRegisterFragment = new StructureRegisterFragment();
        Bundle bundle = new Bundle();
        structureRegisterFragment.setArguments(bundle);
        return structureRegisterFragment;
    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {
        //do nothing
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    protected void registerBottomNavigation() {
        findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
    }
}