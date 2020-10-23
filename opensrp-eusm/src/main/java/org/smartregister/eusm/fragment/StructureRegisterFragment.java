package org.smartregister.eusm.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.UiThread;

import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.HomeActivity;
import org.smartregister.eusm.adapter.StructureRegisterAdapter;
import org.smartregister.eusm.contract.BaseDrawerContract;
import org.smartregister.eusm.contract.StructureRegisterFragmentContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskFilterParams;
import org.smartregister.eusm.presenter.StructureRegisterFragmentPresenter;
import org.smartregister.eusm.util.LocationUtils;
import org.smartregister.eusm.view.NavigationDrawerView;

import java.util.List;

public class StructureRegisterFragment extends BaseDrawerRegisterFragment implements StructureRegisterFragmentContract.View, BaseDrawerContract.DrawerActivity, View.OnClickListener {

    private StructureRegisterAdapter structureRegisterAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawerView = new NavigationDrawerView(this);
    }

    @Override
    protected void initializePresenter() {
        presenter = new StructureRegisterFragmentPresenter(this);
    }

    @Override
    protected void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.service_points_btn_register) {
            startMapActivity(null);
        } else if (id == R.id.drawerMenu) {
            drawerView.openDrawerLayout();
        }
    }

    @Override
    public void onDrawerClosed() {

    }

    @Override
    public Location getLastLocation() {
        return null;
    }

    @Override
    public void initializeAdapter() {
        structureRegisterAdapter = new StructureRegisterAdapter(getContext());
        clientsView.setAdapter(structureRegisterAdapter);
    }

    @Override
    public void setTotalServicePoints(int structuresWithinBuffer) {

    }

    @UiThread
    @Override
    public void setStructureDetails(List<StructureDetail> structureDetails) {
        structureRegisterAdapter.setData(structureDetails);
    }

    @Override
    public void displayNotification(int title, int message, Object... formatArgs) {

    }

    @Override
    public void showProgressDialog(int title, int message) {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public LocationUtils getLocationUtils() {
        return null;
    }

    @Override
    public void displayIndexCaseDetails(JSONObject indexCase) {

    }

    @Override
    public void setNumberOfFilters(int numberOfFilters) {

    }

    @Override
    public void clearFilter() {

    }

    @Override
    public StructureRegisterAdapter getAdapter() {
        return structureRegisterAdapter;
    }

    @Override
    public void openFilterActivity(TaskFilterParams filterParams) {

    }

    @Override
    public void setSearchPhrase(String searchPhrase) {

    }

    @Override
    public void startMapActivity(TaskFilterParams taskFilterParams) {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_service_point_register;
    }

    @Override
    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        if (getAdapter() != null) {
            getAdapter().getFilter().filter(filterString);
        }
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        TextView servicePointBtnRegisterTextView = view.findViewById(R.id.service_points_btn_register);
        servicePointBtnRegisterTextView.setText(getString(R.string.map));
        servicePointBtnRegisterTextView.setOnClickListener(this);

        drawerView.initializeDrawerLayout();

        drawerView.onResume();

        view.findViewById(R.id.drawerMenu)
                .setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onViewClicked(v);
    }
}