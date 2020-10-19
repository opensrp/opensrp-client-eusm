package org.smartregister.eusm.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.eusm.R;
import org.smartregister.eusm.interactor.AppJsonFormInteractor;
import org.smartregister.eusm.presenter.AppJsonFormFragmentPresenter;
import org.smartregister.eusm.util.AppConstants;

/**
 * Created by samuelgithengi on 12/13/18.
 */
public class AppJsonFormFragment extends JsonFormFragment {

    private AppJsonFormFragmentPresenter presenter;

    public static AppJsonFormFragment getFormFragment(String stepName) {
        AppJsonFormFragment jsonFormFragment = new AppJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        presenter = new AppJsonFormFragmentPresenter(this, new AppJsonFormInteractor());
        return presenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMargins(view);
    }

    private void setupMargins(View view) {
        if (getArguments() != null) {
            String stepName = getArguments().getString(JsonFormConstants.STEPNAME);
            if (getStep(stepName).optBoolean(AppConstants.JsonForm.NO_PADDING)) {
                view.findViewById(R.id.main_layout).setPadding(0, 0, 0, 0);
            }
        }
    }

    public AppJsonFormFragmentPresenter getPresenter() {
        return presenter;
    }

}
