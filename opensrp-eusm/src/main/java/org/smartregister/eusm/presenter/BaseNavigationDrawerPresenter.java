package org.smartregister.eusm.presenter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.PlanDefinition.PlanStatus;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.DefaultLocationUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.interactor.BaseDrawerInteractor;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class BaseNavigationDrawerPresenter implements BaseDrawerContract.Presenter {

    private final BaseDrawerContract.View view;

    private final BaseDrawerContract.DrawerActivity drawerActivity;

    private final PreferencesUtil prefsUtil;

    private final LocationHelper locationHelper;
    private final BaseDrawerContract.Interactor interactor;
    private final EusmApplication eusmApplication;
    private boolean changedCurrentSelection;
    private boolean viewInitialized = false;

    public BaseNavigationDrawerPresenter(BaseDrawerContract.View view) {
        this.view = view;
        this.drawerActivity = getView().getActivity();
        this.prefsUtil = PreferencesUtil.getInstance();
        this.locationHelper = LocationHelper.getInstance();
        interactor = new BaseDrawerInteractor(this);
        eusmApplication = EusmApplication.getInstance();
    }


    private void initializeDrawerLayout() {

        getView().setOperator();

        if (StringUtils.isBlank(prefsUtil.getCurrentOperationalArea())) {

            List<String> defaultLocation = locationHelper.generateDefaultLocationHierarchy(DefaultLocationUtils.getLocationLevels());

            if (defaultLocation != null) {
                try {
                    prefsUtil.setCurrentDistrict(defaultLocation.get(2));
                }catch (IndexOutOfBoundsException w){
                    Timber.e(w);
                }
            }
        } else {
            populateLocationsFromPreferences();
        }

        getView().setPlan(prefsUtil.getCurrentPlan());

    }


    @Override
    public void onPlansFetched(Set<PlanDefinition> planDefinitions) {
        List<String> ids = new ArrayList<>();
        List<FormLocation> formLocations = new ArrayList<>();
        for (PlanDefinition planDefinition : planDefinitions) {
            if (!planDefinition.getStatus().equals(PlanStatus.ACTIVE)) {
                continue;
            }
            ids.add(planDefinition.getIdentifier());
            FormLocation formLocation = new FormLocation();
            formLocation.name = planDefinition.getTitle();
            formLocation.key = planDefinition.getIdentifier();
            formLocation.level = "";
            formLocations.add(formLocation);

            // get intervention type for plan
            for (PlanDefinition.UseContext useContext : planDefinition.getUseContext()) {
                if (useContext.getCode().equals(AppConstants.UseContextCode.INTERVENTION_TYPE)) {
                    prefsUtil.setInterventionTypeForPlan(planDefinition.getTitle(), useContext.getValueCodableConcept());
                    break;
                }
            }

        }

        String entireTreeString = "";
        if (formLocations != null && !formLocations.isEmpty()) {
            entireTreeString = AssetHandler.javaToJsonString(formLocations,
                    new TypeToken<List<FormLocation>>() {
                    }.getType());
        }

        getView().showPlanSelector(ids, entireTreeString);
    }

    private void populateLocationsFromPreferences() {
        getView().setDistrict(prefsUtil.getCurrentDistrict());
        getView().setFacility(prefsUtil.getCurrentFacility(), prefsUtil.getCurrentFacilityLevel());
        getView().setOperationalArea(prefsUtil.getCurrentOperationalArea());
    }

    @Override
    public void onShowOperationalAreaSelector() {
        Pair<String, ArrayList<String>> locationHierarchy = extractLocationHierarchy();
        if (locationHierarchy == null) {//try to evict location hierachy in cache
            eusmApplication.getContext().anmLocationController().evict();
            locationHierarchy = extractLocationHierarchy();
        }

        getView().showOperationalAreaSelector(extractLocationHierarchy());

        if (StringUtils.isNotBlank(prefsUtil.getCurrentPlan())
                && Utils.getProperties(getView().getContext()).getPropertyBoolean(AppConstants.AppProperties.CHOOSE_OPERATIONAL_AREA_FIRST)) {
            if (locationHierarchy != null) {
                getView().showOperationalAreaSelector(extractLocationHierarchy());
            } else {
                getView().displayNotification(R.string.error_fetching_location_hierarchy_title, R.string.error_fetching_location_hierarchy);
                eusmApplication.getContext().userService().forceRemoteLogin(eusmApplication.getContext().allSharedPreferences().fetchRegisteredANM());
            }
        } else {
            getView().displayNotification(R.string.plan, R.string.plan_not_selected);
        }

    }

    @Override
    public void onShowPlanSelector() {
        if (Utils.getProperties(getView().getContext()).getPropertyBoolean(AppConstants.AppProperties.CHOOSE_OPERATIONAL_AREA_FIRST)
                && StringUtils.isBlank(prefsUtil.getCurrentOperationalArea())) {
            getView().displayNotification(R.string.operational_area, R.string.operational_area_not_selected);
        } else {
            interactor.fetchPlans(prefsUtil.getCurrentOperationalArea());
        }
    }

    private Pair<String, ArrayList<String>> extractLocationHierarchy() {
        List<String> defaultLocation = locationHelper.generateDefaultLocationHierarchy(DefaultLocationUtils.getFacilityLevels());

        if (defaultLocation != null) {
            List<FormLocation> entireTree = locationHelper.generateLocationHierarchyTree(false, DefaultLocationUtils.getFacilityLevels());

            String entireTreeString = AssetHandler.javaToJsonString(entireTree,
                    new TypeToken<List<FormLocation>>() {
                    }.getType());

            return new Pair<>(entireTreeString, new ArrayList<>(defaultLocation));
        } else {
            return null;
        }
    }


    public void onOperationalAreaSelectorClicked(ArrayList<String> name) {
        Timber.d("Selected Location Hierarchy: %s", TextUtils.join(",", name));
        if (name.size() <= 2)//no operational area was selected, dialog was dismissed
            return;
        try {
            //prefsUtil.setCurrentRegion(name.get(1));
            String operationalArea = name.get(name.size() - 1);
            prefsUtil.setCurrentDistrict(operationalArea);
            prefsUtil.setCurrentOperationalArea(operationalArea);
            validateSelectedPlan(operationalArea);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        changedCurrentSelection = true;
        populateLocationsFromPreferences();
        unlockDrawerLayout();
    }

    private Pair<String, String> getFacilityFromOperationalArea(String district, String operationalArea, List<FormLocation> entireTree) {
        for (FormLocation districtLocation : entireTree) {
            if (!districtLocation.name.equals(district))
                continue;
            for (FormLocation facilityLocation : districtLocation.nodes) {
                if (facilityLocation.nodes == null)
                    continue;
                for (FormLocation operationalAreaLocation : facilityLocation.nodes) {
                    if (operationalAreaLocation.name.equals(operationalArea)) {
                        return new Pair<>(facilityLocation.level, facilityLocation.name);
                    }
                }
            }
        }
        return null;
    }

    public void onPlanSelectorClicked(ArrayList<String> value, ArrayList<String> name) {
        if (Utils.isEmptyCollection(name) || (name.size() > 1))
            return;

        Timber.d("Selected Plan : %s", TextUtils.join(",", name));
        Timber.d("Selected Plan Ids: %s", TextUtils.join(",", value));

        prefsUtil.setCurrentPlan(name.get(0));
        prefsUtil.setCurrentPlanId(value.get(0));
        getView().setPlan(name.get(0));
        changedCurrentSelection = true;
        unlockDrawerLayout();

    }

    public void onDrawerClosed() {
        drawerActivity.onDrawerClosed();
    }

    @Override
    public void unlockDrawerLayout() {
        if (isPlanAndOperationalAreaSelected()) {
            getView().unlockNavigationDrawer();
        }
    }

    @Override
    public boolean isChangedCurrentSelection() {
        return changedCurrentSelection;
    }

    @Override
    public void setChangedCurrentSelection(boolean changedCurrentSelection) {
        this.changedCurrentSelection = changedCurrentSelection;
    }

    @Override
    public BaseDrawerContract.View getView() {
        return view;
    }

    @Override
    public void onViewResumed() {
        if (viewInitialized) {
            if ((StringUtils.isBlank(prefsUtil.getCurrentPlan()) || StringUtils.isBlank(prefsUtil.getCurrentOperationalArea())) &&
                    (StringUtils.isNotBlank(getView().getPlan()) || StringUtils.isNotBlank(getView().getOperationalArea()))) {
                getView().setOperationalArea(prefsUtil.getCurrentOperationalArea());
                getView().setPlan(prefsUtil.getCurrentPlan());
//                getView().lockNavigationDrawerForSelection(R.string.select_mission_operational_area_title, R.string.revoked_plan_operational_area);
            } else if (!prefsUtil.getCurrentPlan().equals(getView().getPlan())
                    || !prefsUtil.getCurrentOperationalArea().equals(getView().getOperationalArea())) {
                changedCurrentSelection = true;
                onDrawerClosed();
            }
        } else {
            initializeDrawerLayout();
            viewInitialized = true;
        }
        if (eusmApplication.getSynced() && eusmApplication.isRefreshMapOnEventSaved()) {
            getView().checkSynced();
        } else {
            updateSyncStatusDisplay(eusmApplication.getSynced());
        }

    }


    @Override
    public boolean isPlanAndOperationalAreaSelected() {
        String planId = prefsUtil.getCurrentPlanId();
        String operationalArea = prefsUtil.getCurrentOperationalArea();

        return StringUtils.isNotBlank(planId) && StringUtils.isNotBlank(operationalArea);

    }

    @Override
    public void onShowOfflineMaps() {
        getView().openOfflineMapsView();
    }

    private void validateSelectedPlan(String operationalArea) {
        if (!prefsUtil.getCurrentPlanId().isEmpty()) {
            interactor.validateCurrentPlan(operationalArea, prefsUtil.getCurrentPlanId());
        }
    }

    @Override
    public void onPlanValidated(boolean isValid) {
        if (!isValid) {
            prefsUtil.setCurrentPlanId("");
            prefsUtil.setCurrentPlan("");
            getView().setPlan("");
            getView().lockNavigationDrawerForSelection();
        }
    }

    /**
     * Updates the Hamburger menu of the navigation drawer to display the sync status of the application
     * Updates also the Text view next to the sync button with the sync status of the application
     *
     * @param synced Sync status of the application
     */
    @Override
    public void updateSyncStatusDisplay(boolean synced) {
        Activity activity = getView().getContext();
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView syncLabel = headerView.findViewById(R.id.sync_label);
        TextView syncBadge = activity.findViewById(R.id.sync_badge);
        if (syncBadge != null && syncLabel != null) {
            if (synced) {
                syncBadge.setBackground(ContextCompat.getDrawable(activity, R.drawable.badge_green_oval));
                syncLabel.setText(getView().getContext().getString(R.string.device_data_synced));
                syncLabel.setTextColor(ContextCompat.getColor(activity, R.color.alert_complete_green));
                syncLabel.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_border_alert_green));
            } else {
                syncBadge.setBackground(ContextCompat.getDrawable(activity, R.drawable.badge_oval));
                syncLabel.setText(getView().getContext().getString(R.string.device_data_not_synced));
                syncLabel.setTextColor(ContextCompat.getColor(activity, R.color.alert_urgent_red));
                syncLabel.setBackground(ContextCompat.getDrawable(activity, R.drawable.rounded_border_alert_red));
            }
        }
    }

    @Override
    public void startOtherFormsActivity() {

    }

    @Override
    public void onShowFilledForms() {

    }
}
