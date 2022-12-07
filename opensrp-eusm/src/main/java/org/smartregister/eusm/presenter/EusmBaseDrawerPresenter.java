package org.smartregister.eusm.presenter;

import android.content.Intent;

import androidx.core.util.Pair;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.presenter.BaseDrawerPresenter;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.Utils;

import java.util.ArrayList;

import static org.smartregister.tasking.util.TaskingConstants.Action.STRUCTURE_TASK_SYNCED;
import static org.smartregister.tasking.util.TaskingConstants.CONFIGURATION.UPDATE_LOCATION_BUFFER_RADIUS;

import timber.log.Timber;

public class EusmBaseDrawerPresenter extends BaseDrawerPresenter {

    private final AppExecutors appExecutors;

    public EusmBaseDrawerPresenter(BaseDrawerContract.View view, BaseDrawerContract.DrawerActivity drawerActivity) {
        super(view, drawerActivity);
        appExecutors = taskingLibrary.getAppExecutors();
    }

    @Override
    public void onPlanSelectorClicked(ArrayList<String> value, ArrayList<String> name) {
        super.onPlanSelectorClicked(value, name);
        prefsUtil.setCurrentOperationalArea("");
        prefsUtil.setCurrentDistrict("");
        prefsUtil.setCurrentProvince("");
        getView().setOperationalArea(null);

        getView().lockNavigationDrawerForSelection();

        Intent refreshGeoWidgetIntent = new Intent(STRUCTURE_TASK_SYNCED);
        refreshGeoWidgetIntent.putExtra(UPDATE_LOCATION_BUFFER_RADIUS, true);
        LocalBroadcastManager.getInstance(getView().getContext()).sendBroadcast(refreshGeoWidgetIntent);
    }

    @Override
    public void onShowOperationalAreaSelector() {
        if (getView() != null) {
            if (!Utils.getBooleanProperty(TaskingConstants.CONFIGURATION.SELECT_PLAN_THEN_AREA) || StringUtils.isNotBlank(prefsUtil.getCurrentPlanId())) {
                appExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Pair<String, ArrayList<String>> locationHierarchy = extractLocationHierarchy();
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (locationHierarchy != null) {
                                    getView().showOperationalAreaSelector(locationHierarchy);
                                } else {
                                    getView().displayNotification(R.string.error_fetching_location_hierarchy_title, R.string.error_fetching_location_hierarchy);
                                    drishtiApplication.getContext().userService().forceRemoteLogin(Utils.getAllSharedPreferences().fetchRegisteredANM());
                                }
                            }
                        });
                    }
                });
            } else {
                getView().displayNotification(R.string.campaign, R.string.plan_not_selected);
            }
        }
    }

    @Override
    public void onOperationalAreaSelectorClicked(ArrayList<String> names) {
        // Extract districts from selcted regions
        ArrayList<String> districts = new ArrayList<>();
        try {
            JSONArray locationHierarchy = new JSONArray(extractLocationHierarchy().first);
            JSONArray regions = locationHierarchy.getJSONObject(0).getJSONArray(AppConstants.JsonForm.NODES);
            for (int i=0; i<regions.length(); i++) {
                JSONObject location = regions.getJSONObject(i);
                String locName = location.getString(TaskingConstants.CONFIGURATION.KEY);
                if (names.contains(locName)) {
                    names.remove(locName);
                    if (location.has(AppConstants.JsonForm.NODES)) {
                        JSONArray nodes = location.getJSONArray(AppConstants.JsonForm.NODES);
                        for (int j=0; j <nodes.length(); j++) {
                            districts.add(nodes.getJSONObject(j).getString(TaskingConstants.CONFIGURATION.KEY));
                        }
                    }
                }

            }
        } catch (Exception e) {
            Timber.e(e);
        }
        super.onOperationalAreaSelectorClicked(districts);
    }
}

