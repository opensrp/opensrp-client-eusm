package org.smartregister.eusm.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.presenter.LoginPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.service.UserService;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.Set;

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

//        TestDataUtils testDataUtils = new TestDataUtils();
//        testDataUtils.populateTestData();

        SharedPreferences allSharedPreferences = Utils.getAllSharedPreferences().getPreferences();
        if (allSharedPreferences != null) {
            boolean hasLoadedDistrictsFromHierarchy = allSharedPreferences.getBoolean(AppConstants.PreferenceKey.LOADED_DISTRICTS_FROM_HIERARCHY, false);
            if (!hasLoadedDistrictsFromHierarchy) {
                Set<String> districtsIds = AppUtils.getDistrictsFromLocationHierarchy();
                UserService userService = EusmApplication.getInstance().context().userService();
                Set<String> strings = userService.fetchJurisdictionIds();
                strings.addAll(districtsIds);
                userService.saveJurisdictionIds(strings);
                allSharedPreferences.edit().putBoolean(AppConstants.PreferenceKey.LOADED_DISTRICTS_FROM_HIERARCHY, true).commit();
            }
        }

        EusmApplication.getInstance().getContext().anmLocationController().evict();

        Intent intent = new Intent(this, EusmTaskingMapActivity.class);
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
