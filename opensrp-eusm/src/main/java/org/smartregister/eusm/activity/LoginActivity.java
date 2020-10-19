package org.smartregister.eusm.activity;

import android.content.Intent;

import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.presenter.LoginPresenter;
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
        EusmApplication.getInstance().getContext().anmLocationController().evict();
        Intent intent = new Intent(this, HomeActivity.class);
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
