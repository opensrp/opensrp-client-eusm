package org.smartregister.eusm.presenter;

import android.content.Intent;

import androidx.core.util.Pair;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Location;
import org.smartregister.domain.PlanDefinitionSearch;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppConstants;
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

import com.google.gson.reflect.TypeToken;

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
        super.onOperationalAreaSelectorClicked(getDistrictsForSelectedRegions(names));
    }


    /*
     * Extract districts from selected regions
     */
    private ArrayList<String> getDistrictsForSelectedRegions(ArrayList<String> names) {
        ArrayList<String> districts = new ArrayList<>();
        try {
            JSONArray locationHierarchy = new JSONArray(extractLocationHierarchy().first);
            JSONObject country = locationHierarchy.optJSONObject(0);
            if (country != null) {
                JSONArray regions = country.getJSONArray(AppConstants.JsonForm.NODES);
                for (int i = 0; i < regions.length(); i++) {
                    JSONObject location = regions.getJSONObject(i);
                    if (location != null) {
                        String locName = location.getString(TaskingConstants.CONFIGURATION.KEY);
                        if (names.contains(locName)) {
                            names.remove(locName);
                            if (location.has(AppConstants.JsonForm.NODES)) {
                                JSONArray nodes = location.getJSONArray(AppConstants.JsonForm.NODES);
                                for (int j = 0; j < nodes.length(); j++) {
                                    districts.add(nodes.getJSONObject(j).getString(TaskingConstants.CONFIGURATION.KEY));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return districts;
    }

    @Override
    public String getEntireTree(List<FormLocation> entireTree) {
        PlanDefinitionSearchRepository planDefinitionRepository = EusmApplication.getInstance().getPlanDefinitionSearchRepository();
        List<PlanDefinitionSearch> planDefinitionSearches = planDefinitionRepository.findPlanDefinitionSearchByPlanId(prefsUtil.getCurrentPlanId());
        List<String> jurisdictionList = planDefinitionSearches.stream().map(PlanDefinitionSearch::getJurisdictionId).collect(Collectors.toList());
        LocationRepository locationRepository = drishtiApplication.getContext().getLocationRepository();
        List<Location> locationList = locationRepository.getLocationsByIds(jurisdictionList);
        List<String> jurisdictionListByName = locationList.stream().map(location -> location.getProperties().getName()).collect(Collectors.toList());

        List<FormLocation> formLocations = filterLocations(jurisdictionListByName, entireTree);
        return AssetHandler.javaToJsonString(formLocations,
                new TypeToken<List<FormLocation>>() {
                }.getType());
    }


    /**
     * Filter out regions not in the plan
     *
     * @param jurisdictionList
     * @param entireTree
     * @return locations assigned to the selected plan
     */
    private List<FormLocation> filterLocations(List<String> jurisdictionList, List<FormLocation> entireTree) {
        if (entireTree != null && !entireTree.isEmpty()) {
            List<FormLocation> country = entireTree.get(0).nodes;
            Iterator<FormLocation> locationIterable = country.iterator();
            while (locationIterable.hasNext()) {
                FormLocation formLocation = locationIterable.next();
                if (formLocation != null) {
                    String key = formLocation.key;
                    if (StringUtils.isNotBlank(key) && !jurisdictionList.contains(key)) {
                        locationIterable.remove();
                    }
                }
            }
        }
        return entireTree;
    }

}

