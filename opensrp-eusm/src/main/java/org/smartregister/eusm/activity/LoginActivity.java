package org.smartregister.eusm.activity;

import android.content.Intent;

import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.presenter.LoginPresenter;
import org.smartregister.eusm.util.TestDataUtils;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }

        TestDataUtils testDataUtils = new TestDataUtils();
        testDataUtils.populateTestData();
        EusmApplication.getInstance().getContext().anmLocationController().evict();

//        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance();

        //fetchJurisdictionIds
        //        preferencesUtil.setCurrentPlanId("335ef7a3-7f35-58aa-8263-4419464946d8");
//        preferencesUtil.setCurrentOperationalArea("AMBODIAMPANA");

//        preferencesUtil.setCurrentDistrict("ad56bb3b-66c5-4a29-8003-0a60582540a6");

        Intent intent = new Intent(this, EusmHomeActivity.class);
        startActivity(intent);
        finish();

        EusmApplication.getInstance().processServerConfigs();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }
}
