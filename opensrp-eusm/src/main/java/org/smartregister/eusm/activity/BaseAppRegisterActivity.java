package org.smartregister.eusm.activity;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.Map;

public abstract class BaseAppRegisterActivity extends BaseRegisterActivity {

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {//not used
    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {//not used
    }

    @Override
    public void startRegistration() {//not used
    }

}
