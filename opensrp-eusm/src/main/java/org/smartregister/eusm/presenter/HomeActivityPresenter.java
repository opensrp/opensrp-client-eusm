package org.smartregister.eusm.presenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.domain.Task.TaskStatus;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.BaseDrawerContract;
import org.smartregister.eusm.contract.HomeActivityContract;
import org.smartregister.eusm.contract.UserLocationContract.UserLocationCallback;
import org.smartregister.eusm.interactor.ListTaskInteractor;
import org.smartregister.eusm.model.CardDetails;
import org.smartregister.eusm.model.TaskDetails;
import org.smartregister.eusm.model.TaskFilterParams;
import org.smartregister.eusm.repository.AppMappingHelper;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.PreferencesUtil;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.TEXT;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static org.smartregister.domain.LocationProperty.PropertyStatus.INACTIVE;
import static org.smartregister.eusm.contract.HomeActivityContract.HomeActivityView;

public class HomeActivityPresenter implements HomeActivityContract.Presenter,
        UserLocationCallback {

    private final HomeActivityView homeActivityView;

    private final ListTaskInteractor listTaskInteractor;

    private final PreferencesUtil prefsUtil;

    private FeatureCollection featureCollection;

    private List<Feature> filterFeatureCollection;

    private List<Feature> searchFeatureCollection;

    private Feature operationalArea;

    private Feature selectedFeature;

    private String selectedFeatureInterventionType;

    private LatLng clickedPoint;

    private AlertDialog passwordDialog;

    private CardDetails cardDetails;

    private boolean changeInterventionStatus;

    private final BaseDrawerContract.Presenter drawerPresenter;

    private final AppJsonFormUtils jsonFormUtils;

    private boolean changeMapPosition;

    private final EusmApplication eusmApplication;

    private final AppMappingHelper mappingHelper;

    private boolean markStructureIneligibleConfirmed;

    private String reasonUnEligible;

    private boolean isTasksFiltered;

    private String searchPhrase;

    private TaskFilterParams filterParams;

    public HomeActivityPresenter(HomeActivityView homeActivityView, BaseDrawerContract.Presenter drawerPresenter) {
        this.homeActivityView = homeActivityView;
        this.drawerPresenter = drawerPresenter;
        listTaskInteractor = new ListTaskInteractor(this);
        prefsUtil = PreferencesUtil.getInstance();
        jsonFormUtils = homeActivityView.getJsonFormUtils();
        setChangeMapPosition(true);
        eusmApplication = EusmApplication.getInstance();
        mappingHelper = new AppMappingHelper();
    }

    @Override
    public void onDrawerClosed() {
        if (drawerPresenter.isChangedCurrentSelection()) {
//            listTaskView.showProgressDialog(R.string.fetching_structures_title, R.string.fetching_structures_message);
            listTaskInteractor.fetchLocations(prefsUtil.getCurrentPlanId(), prefsUtil.getCurrentOperationalArea());
        }
    }

    public void refreshStructures(boolean localSyncDone) {
        setChangeMapPosition(!localSyncDone);
//        listTaskView.showProgressDialog(R.string.fetching_structures_title, R.string.fetching_structures_message);
        listTaskInteractor.fetchLocations(prefsUtil.getCurrentPlanId(), prefsUtil.getCurrentOperationalArea());
    }

    @Override
    public void onStructuresFetched(JSONObject structuresGeoJson, Feature operationalArea, List<TaskDetails> taskDetailsList, String point, Boolean locationComponentActive) {
        prefsUtil.setCurrentOperationalArea(operationalArea.getStringProperty(AppConstants.Properties.LOCATION_NAME));
        homeActivityView.setOperationalArea(prefsUtil.getCurrentOperationalArea());
        onStructuresFetched(structuresGeoJson, operationalArea, taskDetailsList);
        onAddStructureClicked(locationComponentActive, point);
    }

    @Override
    public void onStructuresFetched(JSONObject structuresGeoJson, Feature operationalArea, List<TaskDetails> taskDetailsList) {
//        listTaskView.hideProgressDialog();
//        setChangeMapPosition(drawerPresenter.isChangedCurrentSelection() || (drawerPresenter.isChangedCurrentSelection() && changeMapPosition));
//        drawerPresenter.setChangedCurrentSelection(false);
//        if (structuresGeoJson.has(AppConstants.GeoJSON.FEATURES)) {
//            featureCollection = FeatureCollection.fromJson(structuresGeoJson.toString());
//            isTasksFiltered = false;
//            if (filterParams != null && !filterParams.getCheckedFilters().isEmpty() && StringUtils.isBlank(searchPhrase)) {
//                filterFeatureCollection = null;
//                filterTasks(filterParams);
//            } else if (filterParams != null && !filterParams.getCheckedFilters().isEmpty()) {
//                searchFeatureCollection = null;
//                searchTasks(searchPhrase);
//            } else {
//                listTaskView.setGeoJsonSource(getFeatureCollection(), operationalArea, isChangeMapPosition());
//            }
//            this.operationalArea = operationalArea;
//            if (Utils.isEmptyCollection(getFeatureCollection().features())) {
//                listTaskView.displayNotification(R.string.fetching_structures_title, R.string.no_structures_found);
//            }
//        } else {
//            listTaskView.displayNotification(R.string.fetching_structures_title,
//                    R.string.fetch_location_and_structures_failed, prefsUtil.getCurrentOperationalArea());
//            try {
//                structuresGeoJson.put(AppConstants.GeoJSON.FEATURES, new JSONArray());
//                listTaskView.setGeoJsonSource(FeatureCollection.fromJson(structuresGeoJson.toString()), operationalArea, isChangeMapPosition());
//                listTaskView.clearSelectedFeature();
////                listTaskView.closeCardView(R.id.btn_collapse_spray_card_view);
//            } catch (JSONException e) {
//                Timber.e("error resetting structures");
//            }
//        }
    }

    public void onMapReady() {
//        String planId = PreferencesUtil.getInstance().getCurrentPlanId();
//        String operationalArea = PreferencesUtil.getInstance().getCurrentOperationalArea();
//        if (StringUtils.isNotBlank(planId) &&
//                StringUtils.isNotBlank(operationalArea)) {
//            listTaskInteractor.fetchLocations(planId, operationalArea);
//        } else {
//            listTaskView.displayNotification(R.string.select_mission_operational_area_title, R.string.select_mission_operational_area);
//            drawerPresenter.getView().lockNavigationDrawerForSelection();
//        }
    }

    public void onMapClicked(MapboxMap mapboxMap, LatLng point, boolean isLongclick) {
//        double currentZoom = mapboxMap.getCameraPosition().zoom;
//        if (currentZoom < AppConstants.Map.MAX_SELECT_ZOOM_LEVEL) {
//            Timber.w("onMapClicked Current Zoom level" + currentZoom);
//            listTaskView.displayToast(R.string.zoom_in_to_select);
//            return;
//        }
//        clickedPoint = point;
//        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);
//        Context context = listTaskView.getContext();
//        List<Feature> features = mapboxMap.queryRenderedFeatures(pixel,
//                context.getString(R.string.reveal_layer_polygons), context.getString(R.string.reveal_layer_points));
//        if (features.isEmpty()) {//try to increase the click area
//            RectF clickArea = new RectF(pixel.x - AppConstants.Map.CLICK_SELECT_RADIUS,
//                    pixel.y + AppConstants.Map.CLICK_SELECT_RADIUS, pixel.x + AppConstants.Map.CLICK_SELECT_RADIUS,
//                    pixel.y - AppConstants.Map.CLICK_SELECT_RADIUS);
//            features = mapboxMap.queryRenderedFeatures(clickArea,
//                    context.getString(R.string.reveal_layer_polygons), context.getString(R.string.reveal_layer_points));
//            Timber.d("Selected structure after increasing click area: " + features.size());
//            if (features.size() == 1) {
//                onFeatureSelected(features.get(0), isLongclick);
//            } else {
//                Timber.d("Not Selected structure after increasing click area: " + features.size());
//            }
//        } else {
//            onFeatureSelected(features.get(0), isLongclick);
//            if (features.size() > 1) {
//                Timber.w("Selected more than 1 structure: " + features.size());
//            }
//        }

    }

    private void onFeatureSelected(Feature feature, boolean isLongclick) {
        this.selectedFeature = feature;
        this.changeInterventionStatus = false;
        cardDetails = null;
//
//        homeActivityView.closeAllCardViews();
//        homeActivityView.displaySelectedFeature(feature, clickedPoint);

        onFeatureSelectedByNormalClick(feature);

    }

    private void onFeatureSelectedByNormalClick(Feature feature) {
//        if (!feature.hasProperty(AppConstants.Properties.TASK_IDENTIFIER)) {
//            listTaskView.displayNotification(listTaskView.getContext().getString(R.string.task_not_found, prefsUtil.getCurrentOperationalArea()));
//            return;
//        }
//
//        String businessStatus = org.smartregister.eusm.util.Utils.getPropertyValue(feature, AppConstants.Properties.FEATURE_SELECT_TASK_BUSINESS_STATUS);
//        String code = org.smartregister.eusm.util.Utils.getPropertyValue(feature, AppConstants.Properties.TASK_CODE);
//        selectedFeatureInterventionType = code;
//        if ((AppConstants.Intervention.IRS.equals(code) || AppConstants.Intervention.MOSQUITO_COLLECTION.equals(code) || AppConstants.Intervention.LARVAL_DIPPING.equals(code) || AppConstants.Intervention.PAOT.equals(code) || AppConstants.Intervention.IRS_VERIFICATION.equals(code) || AppConstants.Intervention.REGISTER_FAMILY.equals(code))
//                && (AppConstants.BusinessStatus.NOT_VISITED.equals(businessStatus) || businessStatus == null)) {
//            if (org.smartregister.eusm.util.Utils.validateFarStructures()) {
//                validateUserLocation();
//            } else {
//                onLocationValidated();
//            }
//        } else if (AppConstants.Intervention.IRS.equals(code) &&
//                (AppConstants.BusinessStatus.NOT_SPRAYED.equals(businessStatus) || AppConstants.BusinessStatus.SPRAYED.equals(businessStatus) || AppConstants.BusinessStatus.NOT_SPRAYABLE.equals(businessStatus) || AppConstants.BusinessStatus.PARTIALLY_SPRAYED.equals(businessStatus)
//                        || AppConstants.BusinessStatus.COMPLETE.equals(businessStatus) || AppConstants.BusinessStatus.NOT_ELIGIBLE.equals(businessStatus) || AppConstants.BusinessStatus.NOT_VISITED.equals(businessStatus))) {
//
//            listTaskInteractor.fetchInterventionDetails(AppConstants.Intervention.IRS, feature.id(), false);
//        } else if ((AppConstants.Intervention.MOSQUITO_COLLECTION.equals(code) || AppConstants.Intervention.LARVAL_DIPPING.equals(code))
//                && (AppConstants.BusinessStatus.INCOMPLETE.equals(businessStatus) || AppConstants.BusinessStatus.IN_PROGRESS.equals(businessStatus)
//                || AppConstants.BusinessStatus.NOT_ELIGIBLE.equals(businessStatus) || AppConstants.BusinessStatus.COMPLETE.equals(businessStatus))) {
//            listTaskInteractor.fetchInterventionDetails(code, feature.id(), false);
//        } else if (AppConstants.Intervention.REGISTER_FAMILY.equals(code) && AppConstants.BusinessStatus.NOT_ELIGIBLE.equals(businessStatus)) {
//            listTaskInteractor.fetchInterventionDetails(code, feature.id(), false);
//        } else if (AppConstants.Intervention.PAOT.equals(code)) {
//            listTaskInteractor.fetchInterventionDetails(code, feature.id(), false);
//        } else if (org.smartregister.eusm.util.Utils.isFocusInvestigationOrMDA()) {
//            listTaskInteractor.fetchFamilyDetails(selectedFeature.id());
//        } else if (AppConstants.Intervention.IRS_VERIFICATION.equals(code) && AppConstants.BusinessStatus.COMPLETE.equals(businessStatus)) {
//            listTaskInteractor.fetchInterventionDetails(AppConstants.Intervention.IRS_VERIFICATION, feature.id(), false);
//        }
    }

    private void onFeatureSelectedByLongClick(Feature feature) {
        String businessStatus = org.smartregister.eusm.util.Utils.getPropertyValue(feature, AppConstants.Properties.TASK_BUSINESS_STATUS);
        String code = org.smartregister.eusm.util.Utils.getPropertyValue(feature, AppConstants.Properties.TASK_CODE);
        String status = org.smartregister.eusm.util.Utils.getPropertyValue(feature, AppConstants.Properties.LOCATION_STATUS);

        selectedFeatureInterventionType = code;
        if (INACTIVE.name().equals(status)) {
//            listTaskView.displayToast(R.string.structure_is_inactive);
        } else if (AppConstants.BusinessStatus.NOT_VISITED.equals(businessStatus) || !feature.hasProperty(AppConstants.Properties.TASK_IDENTIFIER)) {
            homeActivityView.displayMarkStructureInactiveDialog();
        } else {
//            listTaskView.displayToast(R.string.cannot_make_structure_inactive);
        }
    }
//
//    @Override
//    public void onFilterTasksClicked() {
//        listTaskView.openFilterTaskActivity(filterParams);
//    }

    @Override
    public void onOpenTaskRegisterClicked() {
        homeActivityView.openTaskRegister(filterParams);
    }

    @Override
    public void setTaskFilterParams(TaskFilterParams filterParams) {

    }

    @Override
    public void onEventFound(Event event) {
        startForm(selectedFeature, cardDetails, selectedFeatureInterventionType, event);
    }

    @Override
    public void findLastEvent(String featureId, String eventType) {
        listTaskInteractor.findLastEvent(featureId, eventType);
    }

    @Override
    public void onInterventionFormDetailsFetched(CardDetails cardDetails) {
//        this.cardDetails = cardDetails;
//        this.changeInterventionStatus = true;
//        listTaskView.hideProgressDialog();
//        if (org.smartregister.eusm.util.Utils.validateFarStructures()) {
//            validateUserLocation();
//        } else {
//            onLocationValidated();
//        }
    }

    @Override
    public void onCardDetailsFetched(CardDetails cardDetails) {
//        if (cardDetails instanceof SprayCardDetails) {
//            if (cardDetails == null) {
//                return;
//            }
//            formatSprayCardDetails((SprayCardDetails) cardDetails);
//            listTaskView.openCardView(cardDetails);
//        } else if (cardDetails instanceof MosquitoHarvestCardDetails) {
//            listTaskView.openCardView(cardDetails);
//        } else if (cardDetails instanceof IRSVerificationCardDetails) {
//            listTaskView.openCardView(cardDetails);
//        } else if (cardDetails instanceof FamilyCardDetails) {
//            formatFamilyCardDetails((FamilyCardDetails) cardDetails);
//            listTaskView.openCardView(cardDetails);
//        }
    }

    public void startForm(Feature feature, CardDetails cardDetails, String interventionType) {
        startForm(feature, cardDetails, interventionType, null);
    }

    public void startForm(Feature feature, CardDetails cardDetails, String interventionType, Event event) {
        String formName = jsonFormUtils.getFormName(null, interventionType);
        String sprayStatus = cardDetails == null ? null : cardDetails.getStatus();
        String familyHead = null;
//        if (cardDetails instanceof SprayCardDetails) {
//            familyHead = ((SprayCardDetails) cardDetails).getFamilyHead();
//        }
        startForm(formName, feature, sprayStatus, familyHead, event);
    }

    private void startForm(String formName, Feature feature, String sprayStatus, String familyHead, Event event) {
        JSONObject formJson = jsonFormUtils.getFormJSON(homeActivityView.getContext()
                , formName, feature, sprayStatus, familyHead);
        if (AppConstants.JsonForm.SPRAY_FORM_ZAMBIA.equals(formName)) {
            try {
                jsonFormUtils.populateField(formJson, AppConstants.JsonForm.DISTRICT_NAME, prefsUtil.getCurrentDistrict().trim(), VALUE);
                jsonFormUtils.populateField(formJson, AppConstants.JsonForm.PROVINCE_NAME, prefsUtil.getCurrentProvince().trim(), VALUE);
            } catch (JSONException e) {
                Timber.e(e);
            }
            Map<String, JSONObject> fields = jsonFormUtils.getFields(formJson);
            jsonFormUtils.populateServerOptions(EusmApplication.getInstance().getServerConfigs(), AppConstants.CONFIGURATION.HEALTH_FACILITIES, fields.get(AppConstants.JsonForm.HFC_SEEK), prefsUtil.getCurrentDistrict());
            jsonFormUtils.populateServerOptions(EusmApplication.getInstance().getServerConfigs(), AppConstants.CONFIGURATION.HEALTH_FACILITIES, fields.get(AppConstants.JsonForm.HFC_BELONG), prefsUtil.getCurrentDistrict());
            jsonFormUtils.populateForm(event, formJson);
            String dataCollector = EusmApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
            if (StringUtils.isNotBlank(dataCollector)) {
                jsonFormUtils.populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                        AppConstants.CONFIGURATION.SPRAY_OPERATORS, fields.get(AppConstants.JsonForm.SPRAY_OPERATOR_CODE),
                        dataCollector);
            }

        } else if (AppConstants.JsonForm.SPRAY_FORM_REFAPP.equals(formName)) {
            jsonFormUtils.populateServerOptions(EusmApplication.getInstance().getServerConfigs(), AppConstants.CONFIGURATION.DATA_COLLECTORS, jsonFormUtils.getFields(formJson).get(AppConstants.JsonForm.DATA_COLLECTOR), prefsUtil.getCurrentDistrict());
        }
        homeActivityView.startJsonForm(formJson);
    }

    public void saveJsonForm(String json) {
//        try {
//            JSONObject jsonForm = new JSONObject(json);
//            String encounterType = jsonForm.getString(AppConstants.JsonForm.ENCOUNTER_TYPE);
//            JSONArray fields = JsonFormUtils.getMultiStepFormFields(jsonForm);
//            String validOperationalArea = JsonFormUtils.getFieldValue(fields, AppConstants.JsonForm.VALID_OPERATIONAL_AREA);
//            if (AppConstants.REGISTER_STRUCTURE_EVENT.equals(encounterType) && StringUtils.isNotBlank(validOperationalArea)) {
//                listTaskView.showProgressDialog(R.string.opening_form_title, R.string.add_structure_form_redirecting, validOperationalArea);
//                Boolean locationComponentActive = Boolean.valueOf(JsonFormUtils.getFieldValue(fields, AppConstants.JsonForm.LOCATION_COMPONENT_ACTIVE));
//                String point = JsonFormUtils.getFieldValue(fields, AppConstants.JsonForm.STRUCTURE);
//                listTaskInteractor.fetchLocations(prefsUtil.getCurrentPlanId(), validOperationalArea, point, locationComponentActive);
//            } else {
//                listTaskView.showProgressDialog(R.string.saving_title, R.string.saving_message);
//                listTaskInteractor.saveJsonForm(json);
//            }
//        } catch (JSONException e) {
//            Timber.e(e);
//            listTaskView.displayToast(R.string.error_occurred_saving_form);
//        }
    }

    @Override
    public void onFormSaved(@NonNull String structureId, String taskID, @NonNull TaskStatus taskStatus, @NonNull String businessStatus, String interventionType) {
        homeActivityView.hideProgressDialog();
        setChangeMapPosition(false);
        for (Feature feature : getFeatureCollection().features()) {
            if (structureId.equals(feature.id())) {
                feature.addStringProperty(AppConstants.Properties.TASK_BUSINESS_STATUS, businessStatus);
                feature.addStringProperty(AppConstants.Properties.TASK_STATUS, taskStatus.name());
                break;
            }
        }
        homeActivityView.setGeoJsonSource(getFeatureCollection(), null, isChangeMapPosition());
        listTaskInteractor.fetchInterventionDetails(interventionType, structureId, false);
    }

    @Override
    public void resetFeatureTasks(String structureId, Task task) {
        setChangeMapPosition(false);
        for (Feature feature : getFeatureCollection().features()) {
            if (structureId.equals(feature.id())) {
                feature.addStringProperty(AppConstants.Properties.TASK_IDENTIFIER, task.getIdentifier());
                feature.addStringProperty(AppConstants.Properties.TASK_CODE, task.getCode());
                feature.addStringProperty(AppConstants.Properties.TASK_BUSINESS_STATUS, task.getBusinessStatus());
                feature.addStringProperty(AppConstants.Properties.TASK_STATUS, task.getStatus().name());
                feature.addStringProperty(AppConstants.Properties.FEATURE_SELECT_TASK_BUSINESS_STATUS, task.getBusinessStatus());
                feature.removeProperty(AppConstants.Properties.STRUCTURE_NAME);
                break;
            }
        }
        homeActivityView.setGeoJsonSource(getFeatureCollection(), null, isChangeMapPosition());
    }

    @Override
    public void onStructureAdded(Feature feature, JSONArray featureCoordinates, double zoomLevel) {
        homeActivityView.hideProgressDialog();
        getFeatureCollection().features().add(feature);
        setChangeMapPosition(false);
        homeActivityView.setGeoJsonSource(getFeatureCollection(), null, isChangeMapPosition());
        try {
            clickedPoint = new LatLng(featureCoordinates.getDouble(1), featureCoordinates.getDouble(0));
            homeActivityView.displaySelectedFeature(feature, clickedPoint, zoomLevel);

        } catch (JSONException e) {
            Timber.e(e, "error extracting coordinates of added structure");
        }
    }

    @Override
    public void onFormSaveFailure(String eventType) {

    }

    public void onAddStructureClicked(boolean myLocationComponentActive) {
        onAddStructureClicked(myLocationComponentActive, null);
    }

    public void onAddStructureClicked(boolean myLocationComponentActive, String point) {
        String formName = jsonFormUtils.getFormName(AppConstants.REGISTER_STRUCTURE_EVENT);
        try {
            JSONObject formJson = new JSONObject(jsonFormUtils.getFormString(homeActivityView.getContext(), formName, null));
            formJson.put(AppConstants.JsonForm.OPERATIONAL_AREA_TAG, operationalArea.toJson());
            eusmApplication.setFeatureCollection(featureCollection);
            jsonFormUtils.populateField(formJson, AppConstants.JsonForm.SELECTED_OPERATIONAL_AREA_NAME, prefsUtil.getCurrentOperationalArea(), TEXT);
            if (StringUtils.isNotBlank(point)) {
                jsonFormUtils.populateField(formJson, AppConstants.JsonForm.STRUCTURE, point, VALUE);
            }
            formJson.put(AppConstants.JsonForm.LOCATION_COMPONENT_ACTIVE, myLocationComponentActive);
            homeActivityView.startJsonForm(formJson);
        } catch (Exception e) {
            Timber.e(e, "error launching add structure form");
        }

    }

    @Override
    public void onLocationValidated() {
        if (markStructureIneligibleConfirmed) {
            onMarkStructureIneligibleConfirmed();
            markStructureIneligibleConfirmed = false;
        } else if (AppConstants.Intervention.REGISTER_FAMILY.equals(selectedFeatureInterventionType)) {
            //listTaskView.registerFamily();
        } else if (cardDetails == null || !changeInterventionStatus) {
            startForm(selectedFeature, null, selectedFeatureInterventionType);
        } else {
            if (AppConstants.Intervention.IRS.equals(cardDetails.getInterventionType())) {
                findLastEvent(selectedFeature.id(), AppConstants.SPRAY_EVENT);
            } else {
                startForm(selectedFeature, cardDetails, selectedFeatureInterventionType);
            }
        }
    }

    @Override
    public LatLng getTargetCoordinates() {
        android.location.Location center = mappingHelper.getCenter(selectedFeature.geometry().toJson());
        return new LatLng(center.getLatitude(), center.getLongitude());
    }

    @Override
    public void requestUserPassword() {
        if (passwordDialog != null) {
            passwordDialog.show();
        }
    }

    @Override
    public Feature getSelectedFeature() {
        return selectedFeature;
    }

    @Override
    public void onMarkStructureInactiveConfirmed() {
        listTaskInteractor.markStructureAsInactive(selectedFeature);

    }

    @Override
    public void onStructureMarkedInactive() {
        for (Feature feature : getFeatureCollection().features()) {
            if (selectedFeature.id().equals(feature.id())) {
                feature.removeProperty(AppConstants.Properties.TASK_BUSINESS_STATUS);
                feature.removeProperty(AppConstants.Properties.TASK_IDENTIFIER);
                feature.addStringProperty(AppConstants.Properties.LOCATION_STATUS, INACTIVE.name());
                break;
            }
        }

        homeActivityView.setGeoJsonSource(getFeatureCollection(), operationalArea, false);
    }

    @Override
    public void onMarkStructureIneligibleConfirmed() {
        listTaskInteractor.markStructureAsIneligible(selectedFeature, reasonUnEligible);
    }

    @Override
    public void onStructureMarkedIneligible() {
        for (Feature feature : getFeatureCollection().features()) {
            if (selectedFeature.id().equals(feature.id())) {
                feature.addStringProperty(AppConstants.Properties.TASK_BUSINESS_STATUS, AppConstants.BusinessStatus.NOT_ELIGIBLE);
                feature.addStringProperty(AppConstants.Properties.FEATURE_SELECT_TASK_BUSINESS_STATUS, AppConstants.BusinessStatus.NOT_ELIGIBLE);
                break;
            }
        }

        homeActivityView.setGeoJsonSource(getFeatureCollection(), operationalArea, false);
    }

//    @Override
//    public void onFamilyFound(CommonPersonObjectClient finalFamily) {
//        if (finalFamily == null)
//            listTaskView.displayNotification(R.string.fetch_family_failed, R.string.failed_to_find_family);
//        else
//            listTaskView.openStructureProfile(finalFamily);
//    }


    public void onResume() {
        if (eusmApplication.isRefreshMapOnEventSaved()) {
            refreshStructures(true);
            homeActivityView.clearSelectedFeature();
            eusmApplication.setRefreshMapOnEventSaved(false);
        }
        updateLocationComponentState();
    }

    private void updateLocationComponentState() {
        if (eusmApplication.isMyLocationComponentEnabled() && !homeActivityView.isMyLocationComponentActive()) {
            homeActivityView.focusOnUserLocation(true);
        } else if (!eusmApplication.isMyLocationComponentEnabled() && homeActivityView.isMyLocationComponentActive()
                || !homeActivityView.isMyLocationComponentActive()) {
            homeActivityView.focusOnUserLocation(false);
            if (!isTasksFiltered && StringUtils.isBlank(searchPhrase)) {
                homeActivityView.setGeoJsonSource(getFeatureCollection(), operationalArea, false);
            }
        }
    }

    public void displayMarkStructureIneligibleDialog() {

//        AlertDialogUtils.displayNotificationWithCallback(listTaskView.getContext(), R.string.mark_location_ineligible,
//                R.string.is_structure_eligible_for_fam_reg, R.string.eligible, R.string.not_eligible_unoccupied, R.string.not_eligible_other, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == BUTTON_NEGATIVE || which == BUTTON_NEUTRAL) {
//                            markStructureIneligibleConfirmed = true;
//                            reasonUnEligible = which == BUTTON_NEGATIVE ? listTaskView.getContext().getString(R.string.not_eligible_unoccupied) : listTaskView.getContext().getString(R.string.not_eligible_other);
//                        }
//                        if (org.smartregister.eusm.util.Utils.validateFarStructures()) {
//                            validateUserLocation();
//                        } else {
//                            onLocationValidated();
//                        }
//                        dialog.dismiss();
//                    }
//                });
    }


    public boolean isChangeMapPosition() {
        return changeMapPosition;
    }

    public void setChangeMapPosition(boolean changeMapPosition) {
        this.changeMapPosition = changeMapPosition;
    }

    public void filterTasks(TaskFilterParams filterParams) {
//        this.filterParams = filterParams;
//        if (filterParams.getCheckedFilters() == null || filterParams.getCheckedFilters().isEmpty()) {
//            isTasksFiltered = false;
//            listTaskView.setNumberOfFilters(0);
//            return;
//        }
//        filterFeatureCollection = new ArrayList<>();
//        Set<String> filterStatus = filterParams.getCheckedFilters().get(AppConstants.Filter.STATUS);
//        Set<String> filterTaskCode = filterParams.getCheckedFilters().get(AppConstants.Filter.CODE);
//        Set<String> filterInterventionUnitTasks = org.smartregister.eusm.util.Utils.getInterventionUnitCodes(filterParams.getCheckedFilters().get(AppConstants.Filter.INTERVENTION_UNIT));
//        Pattern pattern = Pattern.compile("~");
//        for (Feature feature : featureCollection.features()) {
//            boolean matches = true;
//            if (filterStatus != null) {
//                matches = feature.hasProperty(AppConstants.Properties.TASK_BUSINESS_STATUS) && filterStatus.contains(feature.getStringProperty(AppConstants.Properties.TASK_BUSINESS_STATUS));
//            }
//            if (matches && filterTaskCode != null) {
//                matches = matchesTaskCodeFilterList(feature, filterTaskCode, pattern);
//            }
//            if (matches && filterInterventionUnitTasks != null) {
//                matches = matchesTaskCodeFilterList(feature, filterInterventionUnitTasks, pattern);
//            }
//            if (matches) {
//                filterFeatureCollection.add(feature);
//            }
//        }
//        listTaskView.setGeoJsonSource(FeatureCollection.fromFeatures(filterFeatureCollection), operationalArea, false);
//        listTaskView.setNumberOfFilters(filterParams.getCheckedFilters().size());
//        listTaskView.setSearchPhrase("");
//        isTasksFiltered = true;
    }

    private boolean matchesTaskCodeFilterList(Feature feature, Set<String> filterList, Pattern pattern) {
        boolean matches = false;
        JsonElement taskCodes = feature.getProperty(AppConstants.Properties.TASK_CODE_LIST);
        if (taskCodes != null) {
            String[] array = pattern.split(taskCodes.getAsString());
            matches = CollectionUtils.containsAny(Arrays.asList(array), filterList);
        }
        return matches;

    }

    public void searchTasks(String searchPhrase) {
        if (searchPhrase.isEmpty()) {
            searchFeatureCollection = null;
            homeActivityView.setGeoJsonSource(filterFeatureCollection == null ? getFeatureCollection() : FeatureCollection.fromFeatures(filterFeatureCollection), operationalArea, false);
        } else {
            if (getFeatureCollection() != null) {
                List<Feature> features = new ArrayList<>();
                for (Feature feature : !Utils.isEmptyCollection(searchFeatureCollection) && searchPhrase.length() > this.searchPhrase.length() ? searchFeatureCollection : Utils.isEmptyCollection(filterFeatureCollection) ? getFeatureCollection().features() : filterFeatureCollection) {
                    String structureName = feature.getStringProperty(AppConstants.Properties.STRUCTURE_NAME);
                    String familyMemberNames = feature.getStringProperty(AppConstants.Properties.FAMILY_MEMBER_NAMES);
                    if (org.smartregister.eusm.util.Utils.matchesSearchPhrase(structureName, searchPhrase) ||
                            org.smartregister.eusm.util.Utils.matchesSearchPhrase(familyMemberNames, searchPhrase))
                        features.add(feature);
                }
                searchFeatureCollection = features;
                homeActivityView.setGeoJsonSource(FeatureCollection.fromFeatures(searchFeatureCollection), operationalArea, false);
            }
        }
        this.searchPhrase = searchPhrase;
    }

    private FeatureCollection getFeatureCollection() {
        return isTasksFiltered && filterFeatureCollection != null ? FeatureCollection.fromFeatures(filterFeatureCollection) : featureCollection;
    }
}
