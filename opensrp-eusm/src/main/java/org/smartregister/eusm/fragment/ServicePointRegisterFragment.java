package org.smartregister.eusm.fragment;

import android.location.Location;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.adapter.ServicePointRegisterAdapter;
import org.smartregister.eusm.contract.BaseDrawerContract;
import org.smartregister.eusm.contract.ServicePointRegisterFragmentContract;
import org.smartregister.eusm.model.TaskDetails;
import org.smartregister.eusm.model.TaskFilterParams;
import org.smartregister.eusm.util.LocationUtils;

import java.util.List;
import java.util.Set;

public class ServicePointRegisterFragment extends BaseDrawerRegisterFragment implements ServicePointRegisterFragmentContract.View, BaseDrawerContract.DrawerActivity {

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected void onViewClicked(View view) {

    }

    @Override
    public void onDrawerClosed() {

    }

    @Override
    public Location getLastLocation() {
        return null;
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

    }

    @Override
    public void setTotalServicePoints(int structuresWithinBuffer) {

    }

    @Override
    public void setServicePointDetails(List<TaskDetails> tasks) {

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
    public ServicePointRegisterAdapter getAdapter() {
        return null;
    }

    @Override
    public void openFilterActivity(TaskFilterParams filterParams) {

    }

    @Override
    public void setSearchPhrase(String searchPhrase) {

    }

    @Override
    public void startMapActivity(TaskFilterParams taskFilterParams) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_service_point_register;
    }


    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        RecyclerView servicePointsRecyclerView = view.findViewById(R.id.service_points_recyclerview);

//        ServicePointRegisterAdapter servicePointRegisterAdapter = new ServicePointRegisterAdapter();

    }
}