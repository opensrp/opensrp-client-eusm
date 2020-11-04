package org.smartregister.eusm.application;

import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.vijay.jsonwizard.NativeFormLibrary;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.JsonSpecHelper;
import org.smartregister.domain.Setting;
import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.activity.LoginActivity;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.config.AppSyncConfiguration;
import org.smartregister.eusm.config.ServicePointType;
import org.smartregister.eusm.job.AppJobCreator;
import org.smartregister.eusm.processor.AppClientProcessor;
import org.smartregister.eusm.repository.AppRepository;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.eusm.repository.EusmRepository;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.eusm.util.PreferencesUtil;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.receiver.ValidateAssignmentReceiver;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.PlanDefinitionSearchRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import io.ona.kujaku.KujakuLibrary;
import io.ona.kujaku.data.realm.RealmDatabase;
import timber.log.Timber;

public class EusmApplication extends DrishtiApplication implements TimeChangedBroadcastReceiver.OnTimeChangedListener, ValidateAssignmentReceiver.UserAssignmentListener {

    private static CommonFtsObject commonFtsObject;
    private JsonSpecHelper jsonSpecHelper;
    private PlanDefinitionSearchRepository planDefinitionSearchRepository;
    private Map<String, Object> serverConfigs;
    private AppExecutors appExecutors;

    private boolean refreshMapOnEventSaved;

    private boolean myLocationComponentEnabled;

    private FeatureCollection featureCollection;

    private RealmDatabase realmDatabase;

    private Feature operationalArea;

    private boolean synced;

    private AppRepository appRepository;

    private AppStructureRepository appStructureRepository;

    private Map<String, ServicePointType> servicePointKeyToTypeMap;

    private Location userLocation;

    public static synchronized EusmApplication getInstance() {
        return (EusmApplication) mInstance;
    }

    public static JsonSpecHelper getJsonSpecHelper() {
        return getInstance().jsonSpecHelper;
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{AppConstants.EventsRegister.TABLE_NAME, AppStructureRepository.STRUCTURE_TABLE, "ec_structure"};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(AppConstants.EventsRegister.TABLE_NAME)) {
            return new String[]{AppConstants.DatabaseKeys.EVENT_DATE, AppConstants.DatabaseKeys.EVENT_TYPE, AppConstants.DatabaseKeys.SOP,
                    AppConstants.DatabaseKeys.ENTITY, AppConstants.DatabaseKeys.STATUS};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(AppConstants.EventsRegister.TABLE_NAME)) {
            return new String[]{AppConstants.DatabaseKeys.PROVIDER_ID, AppConstants.DatabaseKeys.EVENT_DATE,
                    AppConstants.DatabaseKeys.EVENT_TYPE, AppConstants.DatabaseKeys.STATUS, AppConstants.DatabaseKeys.SOP};
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());
        forceRemoteLoginForInConsistentUsername();
        // Initialize Modules
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        CoreLibrary.init(context, new AppSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP);

        ConfigurableViewsLibrary.init(context);

        LocationHelper.init(AppUtils.ALLOWED_LEVELS, AppUtils.DEFAULT_LOCATION_LEVEL);

        SyncStatusBroadcastReceiver.init(this);

        jsonSpecHelper = new JsonSpecHelper(this);

        serverConfigs = new HashMap<>();

        Mapbox.getInstance(getApplicationContext(), BuildConfig.MAPBOX_SDK_ACCESS_TOKEN);

        KujakuLibrary.init(getApplicationContext());

        //init Job Manager
        JobManager.create(this).addJobCreator(new AppJobCreator());

        NativeFormLibrary.getInstance().setClientFormDao(CoreLibrary.getInstance().context().getClientFormRepository());

        ValidateAssignmentReceiver.init(this);

        ValidateAssignmentReceiver.getInstance().addListener(this);
        Location location = new Location("dest");
        location.setLongitude(32.6454013);
        location.setLatitude(-14.1580617);
        setUserLocation(location);
    }

    /**
     * Removes the username and forces a remote login in case the username did not match the openmrs username
     */
    private void forceRemoteLoginForInConsistentUsername() {
        AllSharedPreferences allSharedPreferences = context.allSharedPreferences();
        String provider = allSharedPreferences.fetchRegisteredANM();
        if (StringUtils.isNotBlank(provider) && StringUtils.isBlank(allSharedPreferences.fetchDefaultTeamId(allSharedPreferences.fetchRegisteredANM()))) {
            allSharedPreferences.updateANMUserName(null);
            allSharedPreferences.saveForceRemoteLogin(true, provider);
        }
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new EusmRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e, "Error on getRepository: ");

        }
        return repository;
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    public Context getContext() {
        return context;
    }

    protected void cleanUpSyncState() {
        try {
            DrishtiSyncScheduler.stop(getApplicationContext());
            context.allSharedPreferences().saveIsSyncInProgress(false);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onTerminate() {
        Timber.e("Application is terminating. Stopping Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        TimeChangedBroadcastReceiver.destroy(this);
        SyncStatusBroadcastReceiver.destroy(this);
        ValidateAssignmentReceiver.destroy(this);
        super.onTerminate();
    }

    @Override
    public void onTimeChanged() {
        context.userService().forceRemoteLogin(context.allSharedPreferences().fetchRegisteredANM());
        logoutCurrentUser();
    }

    @Override
    public void onTimeZoneChanged() {
        context.userService().forceRemoteLogin(context.allSharedPreferences().fetchRegisteredANM());
        logoutCurrentUser();
    }

    public TaskRepository getTaskRepository() {
        return CoreLibrary.getInstance().context().getTaskRepository();
    }

    public LocationRepository getLocationRepository() {
        return CoreLibrary.getInstance().context().getLocationRepository();
    }

    public LocationTagRepository getLocationTagRepository() {
        return CoreLibrary.getInstance().context().getLocationTagRepository();
    }

    public AllSettings getSettingsRepository() {
        return getInstance().getContext().allSettings();
    }

    public void processServerConfigs() {
        populateConfigs(getSettingsRepository().getSetting(AppConstants.CONFIGURATION.GLOBAL_CONFIGS));
        populateConfigs(getSettingsRepository().getSetting(AppConstants.CONFIGURATION.TEAM_CONFIGS));
    }

    private void populateConfigs(@NonNull Setting setting) {
        if (setting == null) {
            return;
        }
        try {
            JSONArray settingsArray = new JSONObject(setting.getValue()).getJSONArray(AppConstants.CONFIGURATION.SETTINGS);
            for (int i = 0; i < settingsArray.length(); i++) {
                JSONObject jsonObject = settingsArray.getJSONObject(i);
                String value = jsonObject.optString(AppConstants.CONFIGURATION.VALUE, null);
                String key = jsonObject.optString(AppConstants.CONFIGURATION.KEY, null);
                JSONArray values = jsonObject.optJSONArray(AppConstants.CONFIGURATION.VALUES);
                if (value != null && key != null) {
                    serverConfigs.put(key, value);
                } else if (values != null && key != null) {
                    serverConfigs.put(key, values);
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public Map<String, Object> getServerConfigs() {
        return serverConfigs;
    }

    public AppExecutors getAppExecutors() {
        if (appExecutors == null) {
            appExecutors = new AppExecutors();
        }
        return appExecutors;
    }

    public PlanDefinitionRepository getPlanDefinitionRepository() {
        return CoreLibrary.getInstance().context().getPlanDefinitionRepository();
    }

    public PlanDefinitionSearchRepository getPlanDefinitionSearchRepository() {
        if (planDefinitionSearchRepository == null) {
            planDefinitionSearchRepository = new PlanDefinitionSearchRepository();
        }
        return planDefinitionSearchRepository;
    }

    public RealmDatabase getRealmDatabase(android.content.Context context) {
        if (realmDatabase == null) {
            realmDatabase = RealmDatabase.init(context);
        }
        return realmDatabase;
    }

    public AppRepository getAppRepository() {
        if (appRepository == null) {
            appRepository = new AppRepository();
        }
        return appRepository;
    }


    public AppStructureRepository getStructureRepository() {
        if (appStructureRepository == null) {
            appStructureRepository = new AppStructureRepository();
        }
        return appStructureRepository;
    }

    @NonNull
    @Override
    public ClientProcessorForJava getClientProcessor() {
        return AppClientProcessor.getInstance(this);
    }

    public boolean isRefreshMapOnEventSaved() {
        return refreshMapOnEventSaved;
    }

    public void setRefreshMapOnEventSaved(boolean refreshMapOnEventSaved) {
        this.refreshMapOnEventSaved = refreshMapOnEventSaved;
    }

    public boolean isMyLocationComponentEnabled() {
        return myLocationComponentEnabled;
    }

    public void setMyLocationComponentEnabled(boolean myLocationComponentEnabled) {
        this.myLocationComponentEnabled = myLocationComponentEnabled;
    }

    public FeatureCollection getFeatureCollection() {
        return featureCollection;
    }

    public void setFeatureCollection(FeatureCollection featureCollection) {
        this.featureCollection = featureCollection;
    }

    public Feature getOperationalArea() {
        return operationalArea;
    }

    public void setOperationalArea(Feature operationalArea) {
        this.operationalArea = operationalArea;
    }

    public boolean getSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    @Override
    public void onUserAssignmentRevoked(UserAssignmentDTO userAssignmentDTO) {
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance();
        if (userAssignmentDTO.getJurisdictions().contains(preferencesUtil.getCurrentOperationalAreaId())) {
            preferencesUtil.setCurrentOperationalArea(null);
        }
        if (userAssignmentDTO.getPlans().contains(preferencesUtil.getCurrentPlanId())) {
            preferencesUtil.setCurrentPlan(null);
            preferencesUtil.setCurrentPlanId(null);
        }
        getContext().anmLocationController().evict();
    }

    public Map<String, ServicePointType> getServicePointKeyToType() {
        if (servicePointKeyToTypeMap == null) {
            Map<String, ServicePointType> map = new HashMap<>();
            map.put(AppConstants.ServicePointType.EPP, ServicePointType.EPP);
            map.put(AppConstants.ServicePointType.CEG, ServicePointType.CEG);
            map.put(AppConstants.ServicePointType.CHRD1, ServicePointType.CHRD1);
            map.put(AppConstants.ServicePointType.CHRD2, ServicePointType.CHRD2);
            map.put(AppConstants.ServicePointType.DRSP, ServicePointType.DRSP);
            map.put(AppConstants.ServicePointType.MSP, ServicePointType.MSP);
            map.put(AppConstants.ServicePointType.SDSP, ServicePointType.SDSP);
            map.put(AppConstants.ServicePointType.CSB1, ServicePointType.CSB1);
            map.put(AppConstants.ServicePointType.CSB2, ServicePointType.CSB2);
            map.put(AppConstants.ServicePointType.CHRR, ServicePointType.CHRR);
            map.put(AppConstants.ServicePointType.WAREHOUSE, ServicePointType.WAREHOUSE);
            map.put(AppConstants.ServicePointType.WATERPOINT, ServicePointType.WATERPOINT);
            map.put(AppConstants.ServicePointType.PRESCO, ServicePointType.PRESCO);
            map.put(AppConstants.ServicePointType.MEAH, ServicePointType.MEAH);
            map.put(AppConstants.ServicePointType.DREAH, ServicePointType.DREAH);
            map.put(AppConstants.ServicePointType.MPPSPF, ServicePointType.MPPSPF);
            map.put(AppConstants.ServicePointType.DRPPSPF, ServicePointType.DRPPSPF);
            map.put(AppConstants.ServicePointType.NGO_PARTNER, ServicePointType.NGOPARTNER);
            map.put(AppConstants.ServicePointType.SITE_COMMUNAUTAIRE, ServicePointType.SITECOMMUNAUTAIRE);
            map.put(AppConstants.ServicePointType.DRJS, ServicePointType.DRJS);
            map.put(AppConstants.ServicePointType.INSTAT, ServicePointType.INSTAT);
            map.put(AppConstants.ServicePointType.BSD, ServicePointType.BSD);
            servicePointKeyToTypeMap = map;
        }

        return servicePointKeyToTypeMap;
    }
}