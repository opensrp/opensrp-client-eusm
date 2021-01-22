package org.smartregister.eusm.application;

import android.content.Intent;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.evernote.android.job.JobManager;
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
import org.smartregister.eusm.config.AppSyncConfiguration;
import org.smartregister.eusm.config.AppTaskingLibraryConfiguration;
import org.smartregister.eusm.config.ServicePointType;
import org.smartregister.eusm.configuration.EusmStockSyncConfiguration;
import org.smartregister.eusm.job.AppJobCreator;
import org.smartregister.eusm.processor.AppClientProcessor;
import org.smartregister.eusm.repository.AppRepository;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.eusm.repository.AppTaskRepository;
import org.smartregister.eusm.repository.EusmRepository;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.receiver.ValidateAssignmentReceiver;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.PlanDefinitionSearchRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.stock.StockLibrary;
import org.smartregister.stock.repository.dao.StockDaoImpl;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.tasking.TaskingLibrary;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.TimeChangedBroadcastReceiver;

import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
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

    private RealmDatabase realmDatabase;

    private AppRepository appRepository;

    private AppStructureRepository appStructureRepository;

    private AppTaskRepository appTaskRepository;

    private Map<String, ServicePointType> servicePointKeyToTypeMap;

    private Location userLocation;

    private ECSyncHelper ecSyncHelper;

    private Compressor compressor;

    private EventClientRepository eventClientRepository;

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
        return new String[]{AppStructureRepository.STRUCTURE_TABLE, "ec_structure"};
    }

    private static String[] getFtsSearchFields(String tableName) {
        Timber.d(tableName);
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        Timber.d(tableName);
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

        TaskingLibrary.init(new AppTaskingLibraryConfiguration(), null, new AppStructureRepository());

        TaskingLibrary.getInstance().setDigitalGlobeConnectId(BuildConfig.DG_CONNECT_ID);

        TaskingLibrary.getInstance().setMapboxAccessToken(BuildConfig.MAPBOX_SDK_ACCESS_TOKEN);

        CoreLibrary.init(context, new AppSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP);

        PathEvaluatorLibrary.getInstance().setStockDao(new StockDaoImpl());

        ConfigurableViewsLibrary.init(context);

        LocationHelper.init(AppUtils.ALLOWED_LEVELS, AppUtils.DEFAULT_LOCATION_LEVEL);

        SyncStatusBroadcastReceiver.init(this);

        jsonSpecHelper = new JsonSpecHelper(this);

        serverConfigs = new HashMap<>();

        Mapbox.getInstance(getApplicationContext(), BuildConfig.MAPBOX_SDK_ACCESS_TOKEN);

        KujakuLibrary.init(getApplicationContext());

        StockLibrary.init(context, getRepository(), null, getAppExecutors(), new EusmStockSyncConfiguration());

        //init Job Manager
        JobManager.create(this).addJobCreator(new AppJobCreator());

        NativeFormLibrary.getInstance().setClientFormDao(CoreLibrary.getInstance().context().getClientFormRepository());

        ValidateAssignmentReceiver.init(this);

        ValidateAssignmentReceiver.getInstance().addListener(this);
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

    public AllSettings getSettingsRepository() {
        return getInstance().getContext().allSettings();
    }

    public void processServerConfigs() {
        populateConfigs(getSettingsRepository().getSetting(AppConstants.CONFIGURATION.GLOBAL_CONFIGS));
        populateConfigs(getSettingsRepository().getSetting(AppConstants.CONFIGURATION.TEAM_CONFIGS));
    }

    private void populateConfigs(@Nullable Setting setting) {
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

    public ECSyncHelper getEcSyncHelper() {
        if (ecSyncHelper == null) {
            ecSyncHelper = ECSyncHelper.getInstance(getApplicationContext());
        }
        return ecSyncHelper;
    }

    public AppTaskRepository getAppTaskRepository() {
        if (appTaskRepository == null) {
            appTaskRepository = new AppTaskRepository(new TaskNotesRepository());
        }
        return appTaskRepository;
    }

    @NonNull
    public Context context() {
        return context;
    }

    @NonNull
    public Compressor getCompressor() {
        if (compressor == null) {
            compressor = new Compressor(context.applicationContext());
        }

        return compressor;
    }

    public EventClientRepository getEventClientRepository() {
        if (eventClientRepository == null) {
            eventClientRepository = new EventClientRepository();
        }
        return eventClientRepository;
    }
}
