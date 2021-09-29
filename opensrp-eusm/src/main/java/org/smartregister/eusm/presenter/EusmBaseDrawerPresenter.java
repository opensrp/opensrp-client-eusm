package org.smartregister.eusm.presenter;

import android.content.Intent;

import androidx.core.util.Pair;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Location;
import org.smartregister.domain.PlanDefinitionSearch;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionSearchRepository;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.presenter.BaseDrawerPresenter;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.smartregister.tasking.util.TaskingConstants.Action.STRUCTURE_TASK_SYNCED;
import static org.smartregister.tasking.util.TaskingConstants.CONFIGURATION.UPDATE_LOCATION_BUFFER_RADIUS;

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
    public String getEntireTree(List<FormLocation> entireTree) {
        PlanDefinitionSearchRepository planDefinitionRepository = EusmApplication.getInstance().getPlanDefinitionSearchRepository();
        List<PlanDefinitionSearch> planDefinitionSearches = planDefinitionRepository.findPlanDefinitionSearchByPlanId(prefsUtil.getCurrentPlanId());
        List<String> jurisdictionList = planDefinitionSearches.stream().map(PlanDefinitionSearch::getJurisdictionId).collect(Collectors.toList());
        LocationRepository locationRepository = drishtiApplication.getContext().getLocationRepository();
        List<Location> locationList = locationRepository.getLocationsByIds(jurisdictionList);

        List<FormLocation> formLocations = filterLocations(locationList.stream().map(location ->
                location.getProperties().getName()
        ).collect(Collectors.toList()), entireTree);
        cleanUpFormLocations(formLocations);
        return AssetHandler.javaToJsonString(formLocations,
                new TypeToken<List<FormLocation>>() {
                }.getType());
    }


    private void cleanUpFormLocations(List<FormLocation> formLocations) {
        if (formLocations != null && !formLocations.isEmpty()) {
            FormLocation root = formLocations.get(0);
            if (root.nodes != null && !root.nodes.isEmpty()) {
                Iterator<FormLocation> locationIterable = root.nodes.iterator();
                while (locationIterable.hasNext()) {
                    FormLocation formLocation = locationIterable.next();
                    if (formLocation.nodes.isEmpty()) {
                        locationIterable.remove();
                    }
                }
            }
        }
    }


    /**
     * Filter out district not in the plan
     *
     * @param filteredIn
     * @param entireTree
     * @return
     */
    private List<FormLocation> filterLocations(List<String> filteredIn, List<FormLocation> entireTree) {
        if (entireTree != null && !entireTree.isEmpty()) {
            List<FormLocation> formLocations;
            Iterator<FormLocation> locationIterable = entireTree.iterator();
            while (locationIterable.hasNext()) {
                FormLocation formLocation = locationIterable.next();
                formLocations = formLocation.nodes;
                String key = formLocation.key;
                if (formLocations != null && formLocations.isEmpty() && StringUtils.isNotBlank(key)) {
                    if (!filteredIn.contains(key)) {
                        locationIterable.remove();
                    }
                } else {
                    filterLocations(filteredIn, formLocations);
                }
            }
        }
        return entireTree;
    }

}

