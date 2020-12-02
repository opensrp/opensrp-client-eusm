package org.smartregister.eusm.fragment;

import android.view.View;

import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.receiver.ValidateAssignmentReceiver;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

public abstract class BaseDrawerRegisterFragment extends BaseRegisterFragment implements ValidateAssignmentReceiver.UserAssignmentListener {

    protected BaseDrawerContract.View drawerView;

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return null;
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        ValidateAssignmentReceiver.getInstance().addListener(this);
    }

    @Override
    public void onUserAssignmentRevoked(UserAssignmentDTO userAssignmentDTO) {
        drawerView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ValidateAssignmentReceiver.getInstance().removeLister(this);
    }
}
