package org.smartregister.eusm.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Location;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.PlanDefinitionSearch;
import org.smartregister.domain.Task;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.activity.EusmOfflineMapsActivity;
import org.smartregister.eusm.activity.EusmTaskingMapActivity;
import org.smartregister.eusm.activity.StructureRegisterActivity;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.EusmCardDetail;
import org.smartregister.eusm.helper.EusmTaskingMapHelper;
import org.smartregister.eusm.job.LocationTaskServiceJob;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.DefaultLocationUtils;
import org.smartregister.eusm.view.NavigationDrawerView;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.PlanDefinitionSearchRepository;
import org.smartregister.tasking.activity.TaskingMapActivity;
import org.smartregister.tasking.adapter.TaskRegisterAdapter;
import org.smartregister.tasking.contract.BaseContract;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.contract.BaseFormFragmentContract;
import org.smartregister.tasking.contract.TaskingMapActivityContract;
import org.smartregister.tasking.layer.DigitalGlobeLayer;
import org.smartregister.tasking.model.BaseTaskDetails;
import org.smartregister.tasking.model.CardDetails;
import org.smartregister.tasking.model.TaskDetails;
import org.smartregister.tasking.model.TaskFilterParams;
import org.smartregister.tasking.repository.TaskingMappingHelper;
import org.smartregister.tasking.util.ActivityConfiguration;
import org.smartregister.tasking.util.GeoJsonUtils;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.tasking.util.TaskingJsonFormUtils;
import org.smartregister.tasking.util.TaskingLibraryConfiguration;
import org.smartregister.tasking.util.TaskingMapHelper;
import org.smartregister.util.AppExecutors;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import timber.log.Timber;

import static org.smartregister.tasking.util.Utils.getGlobalConfig;

public class AppTaskingLibraryConfiguration extends TaskingLibraryConfiguration {

    private boolean isSynced;

    @NonNull
    @Override
    public Pair<Drawable, String> getActionDrawable(Context context, TaskDetails task) {
        return null;
    }

    @Override
    public int getInterventionLabel() {
        return 0;
    }

    @NonNull
    @Override
    public Float getLocationBuffer() {
        return Float.valueOf(getGlobalConfig(TaskingConstants.CONFIGURATION.LOCATION_BUFFER_RADIUS_IN_METRES, "25"));
    }

    @Override
    public void startImmediateSync() {
        LocationTaskServiceJob.scheduleJobImmediately(LocationTaskServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        //SyncStockServiceJob.scheduleJobImmediately(SyncStockServiceJob.TAG);
    }

    @Override
    public boolean validateFarStructures() {
        return false;
    }

    @Override
    public int getResolveLocationTimeoutInSeconds() {
        return 0;
    }

    @Override
    public String getAdminPasswordNotNearStructures() {
        return null;
    }

    @Override
    public boolean isFocusInvestigation() {
        return false;
    }

    @Override
    public boolean isMDA() {
        return false;
    }

    @Override
    public String getCurrentLocationId() {
        return null;
    }

    @Override
    public String getCurrentOperationalAreaId() {
        return PreferencesUtil.getInstance().getCurrentOperationalAreaId();
    }

    @Override
    public Integer getDatabaseVersion() {
        return BuildConfig.DATABASE_VERSION;
    }

    @Override
    public void tagEventTaskDetails(List<Event> events, SQLiteDatabase sqLiteDatabase) {
        //do nothing
    }

    @Override
    public Boolean displayDistanceScale() {
        return Boolean.valueOf(getGlobalConfig(TaskingConstants.CONFIGURATION.DISPLAY_DISTANCE_SCALE, "false"));
    }

    @Override
    public String getFormName(@NonNull String encounterType, @Nullable String taskCode) {
        return null;
    }

    @Override
    public boolean resetTaskInfo(@NonNull SQLiteDatabase db, @NonNull BaseTaskDetails taskDetails) {
        return false;
    }

    @Override
    public boolean archiveClient(String baseEntityId, boolean isFamily) {
        return false;
    }

    @Override
    public String getTranslatedIRSVerificationStatus(String status) {
        return null;
    }

    @Override
    public String getTranslatedBusinessStatus(String businessStatus) {
        return null;
    }

    @Override
    public void formatCardDetails(CardDetails cardDetails) {
        //do nothing
    }

    @Override
    public void processServerConfigs() {
        EusmApplication.getInstance().processServerConfigs();
    }

    @Override
    public Map<String, Integer> populateLabels() {
        return null;
    }

    @Override
    public void showBasicForm(BaseFormFragmentContract.View view, Context context, String formName) {
        //do nothing
    }

    @Override
    public void onLocationValidated(@NonNull Context context, @NonNull BaseFormFragmentContract.View view, @NonNull BaseFormFragmentContract.Interactor interactor, @NonNull BaseTaskDetails baseTaskDetails, @NonNull Location structure) {
        //do nothing
    }

    @Override
    public String mainSelect(String mainCondition) {
        return null;
    }

    @Override
    public String nonRegisteredStructureTasksSelect(String mainCondition) {
        return null;
    }

    @Override
    public String groupedRegisteredStructureTasksSelect(String mainCondition) {
        return null;
    }

    @Override
    public String[] taskRegisterMainColumns(String tableName) {
        return new String[0];
    }

    @Override
    public String familyRegisterTableName() {
        return null;
    }

    @Override
    public void saveCaseConfirmation(BaseContract.BaseInteractor baseInteractor, BaseContract.BasePresenter presenterCallBack, JSONObject jsonForm, String eventType) {
        //do nothing
    }

    @Override
    public String calculateBusinessStatus(@NonNull org.smartregister.domain.Event event) {
        return null;
    }

    @Override
    public String getCurrentPlanId() {
        return null;
    }

    @Override
    public boolean getSynced() {
        return isSynced;
    }

    @Override
    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    @Override
    public boolean isMyLocationComponentEnabled() {
        return true;
    }

    @Override
    public void setMyLocationComponentEnabled(boolean myLocationComponentEnabled) {
        //do nothing
    }

    @Override
    public Task generateTaskFromStructureType(@NonNull Context context, @NonNull String structureId, @NonNull String structureType) {
        return null;
    }

    @Override
    public void saveLocationInterventionForm(BaseContract.BaseInteractor baseInteractor, BaseContract.BasePresenter presenterCallBack, JSONObject jsonForm) {
        //do nothing
    }

    @Override
    public void saveJsonForm(BaseContract.BaseInteractor baseInteractor, String json) {
        //do nothing
    }

    @Override
    public void openFilterActivity(Activity activity, TaskFilterParams filterParams) {
        //do nothing
    }

    @Override
    public void openFamilyProfile(Activity activity, CommonPersonObjectClient family, BaseTaskDetails taskDetails) {
        //do nothing
    }

    @Override
    public void setTaskDetails(Activity activity, TaskRegisterAdapter taskAdapter, List<TaskDetails> tasks) {
        //do nothing
    }

    @Override
    public void showNotFoundPopup(Activity activity, String opensrpId) {
        //do nothing
    }

    @Override
    public void startMapActivity(Activity activity, String searchViewText, TaskFilterParams taskFilterParams) {
        Intent intent = new Intent(activity, EusmTaskingMapActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onTaskRegisterBindViewHolder(@NonNull Context context, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull View.OnClickListener registerActionHandler, @NonNull TaskDetails taskDetails, int position) {
        //do nothing
    }

    @NonNull
    @Override
    public AppExecutors getAppExecutors() {
        return EusmApplication.getInstance().getAppExecutors();
    }

    @Override
    public BaseDrawerContract.View getDrawerMenuView(BaseDrawerContract.DrawerActivity activity) {
        return new NavigationDrawerView(activity);
    }

    @Override
    public void showTasksCompleteActionView(TextView actionView) {
        //do nothing
    }

    @Override
    public Map<String, Object> getServerConfigs() {
        return null;
    }

    @Override
    public TaskingJsonFormUtils getJsonFormUtils() {
        return new TaskingJsonFormUtils();
    }

    @Override
    public TaskingMappingHelper getMappingHelper() {
        return new TaskingMappingHelper();
    }

    @Override
    public TaskingMapHelper getMapHelper() {
        return new EusmTaskingMapHelper();
    }

    @Override
    public boolean isRefreshMapOnEventSaved() {
        return true;
    }

    @Override
    public void setRefreshMapOnEventSaved(boolean isRefreshMapOnEventSaved) {
        //do nothing
    }

    @Override
    public void setFeatureCollection(FeatureCollection featureCollection) {
        //do nothing
    }

    @Override
    public DigitalGlobeLayer getDigitalGlobeLayer() {
        return new DigitalGlobeLayer();
    }

    @Override
    public List<String> getFacilityLevels() {
        return DefaultLocationUtils.getFacilityLevels();
    }

    @Override
    public List<String> getLocationLevels() {
        return DefaultLocationUtils.getLocationLevels();
    }

    @Override
    public ActivityConfiguration getActivityConfiguration() {
        return ActivityConfiguration.builder().offlineMapsActivity(EusmOfflineMapsActivity.class).build();
    }

    @Override
    public void registerFamily(Feature selectedFeature) {
        //do nothing
    }

    @Override
    public void openTaskRegister(TaskFilterParams filterParams, TaskingMapActivity activity) {
        Intent intent = new Intent(activity, StructureRegisterActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public boolean isCompassEnabled() {
        return false;
    }

    @Override
    public boolean showCurrentLocationButton() {
        return false;
    }

    @Override
    public boolean disableMyLocationOnMapMove() {
        return false;
    }

    @Override
    public Boolean getDrawOperationalAreaBoundaryAndLabel() {
        return false;
    }

    @Override
    public GeoJsonUtils getGeoJsonUtils() {
        return new org.smartregister.eusm.util.GeoJsonUtils();
    }

    @Override
    public String getProvinceFromTreeDialogValue(List<String> arrayList) {
        return "";
    }

    @Override
    public String getDistrictFromTreeDialogValue(List<String> arrayList) {
        try {
            return arrayList.get(2);
        } catch (IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return "";
    }

    @Override
    public void onShowFilledForms() {
        //do nothing
    }

    @Override
    public void onFeatureSelectedByLongClick(Feature feature, TaskingMapActivityContract.Presenter presenter) {
        //do nothing
    }

    @Override
    public void onFeatureSelectedByClick(Feature feature, TaskingMapActivityContract.Presenter presenter) {
        getAppExecutors().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                JsonObject jsonObject = feature.properties();
                if (jsonObject != null) {
                    EusmCardDetail eusmCardDetail = new EusmCardDetail(jsonObject.get(AppConstants.CardDetailKeys.STATUS).getAsString());
                    eusmCardDetail.setCommune(jsonObject.get(AppConstants.CardDetailKeys.COMMUNE).getAsString());
                    eusmCardDetail.setDistanceMeta(jsonObject.get(AppConstants.CardDetailKeys.DISTANCE_META).getAsString());
                    eusmCardDetail.setStructureId(jsonObject.get(AppConstants.CardDetailKeys.STRUCTURE_ID).getAsString());
                    eusmCardDetail.setTaskStatus(jsonObject.get(AppConstants.CardDetailKeys.TASK_STATUS).getAsString());
                    eusmCardDetail.setStructureName(jsonObject.get(AppConstants.CardDetailKeys.NAME).getAsString());
                    eusmCardDetail.setStructureType(jsonObject.get(AppConstants.CardDetailKeys.TYPE).getAsString());
                    getAppExecutors().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            presenter.onCardDetailsFetched(eusmCardDetail);
                        }
                    });
                }
            }
        });
    }

    @Override
    public double getOnClickMaxZoomLevel() {
        return 6;
    }

    @Override
    public void fetchPlans(String jurisdictionName, BaseDrawerContract.Presenter presenter) {
        PlanDefinitionRepository planDefinitionRepository = EusmApplication.getInstance().getPlanDefinitionRepository();
        Set<PlanDefinition> planDefinitionSet = planDefinitionRepository.findAllPlanDefinitions();
        getAppExecutors().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                presenter.onPlansFetched(planDefinitionSet);
            }
        });
    }

    @Override
    public void validateCurrentPlan(String s, String s1, BaseDrawerContract.Presenter presenter) {
        //do nothing
    }

    @Override
    public void setFacility(List<String> list, BaseDrawerContract.View view) {
        //do nothing
    }

    @Override
    public void openFilterTaskActivity(TaskFilterParams taskFilterParams, TaskingMapActivity TaskingMapActivity) {
        //do nothing
    }

    @Override
    public List<Location> getLocationsIdsForDownload(List<String> downloadedLocations) {
        PlanDefinitionSearchRepository planDefinitionRepository = EusmApplication.getInstance().getPlanDefinitionSearchRepository();
        List<String> jurisdictionIds = planDefinitionRepository
                .findPlanDefinitionSearchByPlanStatus(PlanDefinition.PlanStatus.ACTIVE)
                .stream().map(PlanDefinitionSearch::getJurisdictionId).collect(Collectors.toList());

        if (downloadedLocations != null) {
            jurisdictionIds.removeAll(downloadedLocations);
        }
//        List<Location> locationList =  EusmApplication.getInstance().getStructureRepository().getLocationByDistrictIds(jurisdictionIds);
//
//        if (downloadedLocations != null) {
//            locationList = locationList.stream().filter(location -> !downloadedLocations.contains(location.getId())).collect(Collectors.toList());
//        }
        return EusmApplication.getInstance().getLocationRepository().getLocationsByIds(jurisdictionIds);
    }

    @Override
    public Pair<Double, Double> getMinMaxZoomMapDownloadPair() {
        return Pair.create(5d, 11d);
    }
}
