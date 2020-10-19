package org.smartregister.eusm.interactor;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.Task;
import org.smartregister.domain.db.EventClient;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.BaseContract;
import org.smartregister.eusm.contract.BaseContract.BasePresenter;
import org.smartregister.eusm.contract.StructureTasksContract;
import org.smartregister.eusm.processor.AppClientProcessor;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.PreferencesUtil;
import org.smartregister.eusm.util.TaskUtils;
import org.smartregister.eusm.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.PropertiesConverter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.getString;


/**
 * Created by samuelgithengi on 3/25/19.
 */
public class BaseInteractor implements BaseContract.BaseInteractor {

    public static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
    protected TaskRepository taskRepository;
    protected StructureRepository structureRepository;
    protected BasePresenter presenterCallBack;
    protected String operationalAreaId;
    protected AppExecutors appExecutors;
    protected AllSharedPreferences sharedPreferences;
    protected EventClientRepository eventClientRepository;
    protected AppClientProcessor clientProcessor;
    private final EusmApplication eusmApplication;
    private final TaskUtils taskUtils;

    private final SQLiteDatabase database;

    private CommonRepository commonRepository;

    private final PreferencesUtil prefsUtil;

    public BaseInteractor(BasePresenter presenterCallBack) {
        eusmApplication = EusmApplication.getInstance();
        this.presenterCallBack = presenterCallBack;
        appExecutors = eusmApplication.getAppExecutors();
        taskRepository = eusmApplication.getTaskRepository();
        structureRepository = eusmApplication.getStructureRepository();
        eventClientRepository = eusmApplication.getContext().getEventClientRepository();
        clientProcessor = AppClientProcessor.getInstance(eusmApplication.getApplicationContext());
        sharedPreferences = eusmApplication.getContext().allSharedPreferences();
        taskUtils = TaskUtils.getInstance();
        database = eusmApplication.getRepository().getReadableDatabase();
        prefsUtil = PreferencesUtil.getInstance();
    }

    @VisibleForTesting
    public BaseInteractor(BasePresenter presenterCallBack, CommonRepository commonRepository) {
        this(presenterCallBack);
        this.commonRepository = commonRepository;
    }

    @Override
    public void saveJsonForm(String json) {
        String encounterType = null;
        try {
            JSONObject jsonForm = new JSONObject(json);
            encounterType = jsonForm.optString(AppConstants.JsonForm.ENCOUNTER_TYPE);
            boolean refreshMapOnEventSaved = true;
            switch (encounterType) {
                case AppConstants.REGISTER_STRUCTURE_EVENT:
                    saveRegisterStructureForm(jsonForm);
                    break;
                case AppConstants.BLOOD_SCREENING_EVENT:
                case AppConstants.EventType.MDA_ADHERENCE:
                    saveMemberForm(jsonForm, encounterType, AppConstants.Intervention.BLOOD_SCREENING);
                    break;

                case AppConstants.EventType.CASE_CONFIRMATION_EVENT:
                    saveCaseConfirmation(jsonForm, encounterType);
                    break;
                default:
                    saveLocationInterventionForm(jsonForm);
                    if (!encounterType.equals(AppConstants.BEDNET_DISTRIBUTION_EVENT) && !encounterType.equals(AppConstants.EventType.IRS_VERIFICATION)) {
                        refreshMapOnEventSaved = false;
                    }
                    break;
            }
            eusmApplication.setRefreshMapOnEventSaved(refreshMapOnEventSaved);
        } catch (Exception e) {
            Timber.e(e);
            presenterCallBack.onFormSaveFailure(encounterType);
        }
    }

    @Override
    public void handleLastEventFound(org.smartregister.domain.Event event) {
        // handle in child class
    }

    private org.smartregister.domain.Event saveEvent(JSONObject jsonForm, String encounterType, String bindType) throws JSONException {
        String entityId = getString(jsonForm, ENTITY_ID);
        JSONArray fields = JsonFormUtils.fields(jsonForm);
        JSONObject metadata = JsonFormUtils.getJSONObject(jsonForm, AppConstants.METADATA);
        Event event = JsonFormUtils.createEvent(fields, metadata, Utils.getFormTag(), entityId, encounterType, bindType);
        JSONObject eventJson = new JSONObject(gson.toJson(event));
        eventJson.put(AppConstants.DETAILS, JsonFormUtils.getJSONObject(jsonForm, AppConstants.DETAILS));
        eventClientRepository.addEvent(entityId, eventJson);
        return gson.fromJson(eventJson.toString(), org.smartregister.domain.Event.class);
    }

    private void saveLocationInterventionForm(JSONObject jsonForm) {
        String encounterType = null;
        String interventionType = null;
        try {
            encounterType = jsonForm.getString(AppConstants.JsonForm.ENCOUNTER_TYPE);
            if (encounterType.equals(AppConstants.SPRAY_EVENT)) {
                interventionType = AppConstants.Intervention.IRS;
            } else if (encounterType.equals(AppConstants.MOSQUITO_COLLECTION_EVENT)) {
                interventionType = AppConstants.Intervention.MOSQUITO_COLLECTION;
            } else if (encounterType.equals(AppConstants.LARVAL_DIPPING_EVENT)) {
                interventionType = AppConstants.Intervention.LARVAL_DIPPING;
            } else if (encounterType.equals(AppConstants.BEDNET_DISTRIBUTION_EVENT)) {
                interventionType = AppConstants.Intervention.BEDNET_DISTRIBUTION;
            } else if (encounterType.equals(AppConstants.BEHAVIOUR_CHANGE_COMMUNICATION)) {
                interventionType = AppConstants.Intervention.BCC;
            } else if (encounterType.equals(AppConstants.EventType.PAOT_EVENT)) {
                interventionType = AppConstants.Intervention.PAOT;
            } else if (encounterType.equals(AppConstants.EventType.MDA_DISPENSE)) {
                interventionType = AppConstants.Intervention.MDA_DISPENSE;
            } else if (encounterType.equals(AppConstants.EventType.MDA_ADHERENCE)) {
                interventionType = AppConstants.Intervention.MDA_ADHERENCE;
            } else if (encounterType.equals(AppConstants.EventType.IRS_VERIFICATION)) {
                interventionType = AppConstants.Intervention.IRS_VERIFICATION;
            } else if (encounterType.equals(AppConstants.EventType.DAILY_SUMMARY_EVENT)) {
                jsonForm.put(ENTITY_ID, UUID.randomUUID().toString());
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

        final String finalInterventionType = interventionType;
        final String finalEncounterType = encounterType;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    org.smartregister.domain.Event event = saveEvent(jsonForm, finalEncounterType, AppConstants.STRUCTURE);
                    clientProcessor.processClient(Collections.singletonList(new EventClient(event, null)), true);
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            String businessStatus = clientProcessor.calculateBusinessStatus(event);
                            String taskID = event.getDetails() == null ? null : event.getDetails().get(AppConstants.Properties.TASK_IDENTIFIER);
                            presenterCallBack.onFormSaved(event.getBaseEntityId(), taskID, Task.TaskStatus.COMPLETED, businessStatus, finalInterventionType);
                        }
                    });
                } catch (JSONException e) {
                    Timber.e(e, "Error saving saving Form ");
                    presenterCallBack.onFormSaveFailure(finalEncounterType);
                }
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void saveRegisterStructureForm(JSONObject jsonForm) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    jsonForm.put(ENTITY_ID, UUID.randomUUID().toString());
//                    JSONObject eventDetails = new JSONObject();
//                    eventDetails.put(AppConstants.Properties.APP_VERSION_NAME, BuildConfig.VERSION_NAME);
//                    eventDetails.put(AppConstants.Properties.LOCATION_PARENT, operationalAreaId);
//                    String planIdentifier = PreferencesUtil.getInstance().getCurrentPlanId();
//                    eventDetails.put(AppConstants.Properties.PLAN_IDENTIFIER, planIdentifier);
//                    jsonForm.put(AppConstants.DETAILS, eventDetails);
//
//                    org.smartregister.domain.Event event = saveEvent(jsonForm, AppConstants.REGISTER_STRUCTURE_EVENT, AppConstants.STRUCTURE);
//                    com.cocoahero.android.geojson.Feature feature = new com.cocoahero.android.geojson.Feature(new JSONObject(event.findObs(null, false, "structure").getValue().toString()));
//                    Date now = new Date();
//
//                    Location structure = new Location();
//                    structure.setId(event.getBaseEntityId());
//                    structure.setType(feature.getType());
//                    org.smartregister.domain.Geometry geometry = new org.smartregister.domain.Geometry();
//                    geometry.setType(org.smartregister.domain.Geometry.GeometryType.valueOf(feature.getGeometry().getType().toUpperCase()));
//
//                    JsonArray coordinates = new JsonArray();
//                    JSONArray featureCoordinates = feature.getGeometry().toJSON().getJSONArray(JSON_COORDINATES);
//                    coordinates.add(Double.parseDouble(featureCoordinates.get(0).toString()));
//                    coordinates.add(Double.parseDouble(featureCoordinates.get(1).toString()));
//                    geometry.setCoordinates(coordinates);
//                    structure.setGeometry(geometry);
//
//                    LocationProperty properties = new LocationProperty();
//                    String structureType = event.findObs(null, false, AppConstants.JsonForm.STRUCTURE_TYPE).getValue().toString();
//                    properties.setType(structureType);
//                    properties.setEffectiveStartDate(now);
//                    properties.setParentId(operationalAreaId);
//                    properties.setStatus(LocationProperty.PropertyStatus.PENDING_REVIEW);
//                    properties.setUid(UUID.randomUUID().toString());
//
//                    Obs structureNameObs = event.findObs(null, false, AppConstants.JsonForm.STRUCTURE_NAME);
//                    if (structureNameObs != null && structureNameObs.getValue() != null) {
//                        properties.setName(structureNameObs.getValue().toString());
//                    }
//                    Obs physicalTypeObs = event.findObs(null, false, AppConstants.JsonForm.PHYSICAL_TYPE);
//                    if (physicalTypeObs != null && physicalTypeObs.getValue() != null) {
//                        Map<String, String> customProperties = new HashMap<>();
//                        customProperties.put(AppConstants.JsonForm.PHYSICAL_TYPE, physicalTypeObs.getValue().toString());
//                        properties.setCustomProperties(customProperties);
//                    }
//                    structure.setProperties(properties);
//                    structure.setSyncStatus(BaseRepository.TYPE_Created);
//                    structureRepository.addOrUpdate(structure);
//                    eusmApplication.setSynced(false);
//                    Context applicationContext = eusmApplication.getApplicationContext();
//                    Task task = null;
//                    if (AppConstants.StructureType.RESIDENTIAL.equals(structureType) && Utils.isFocusInvestigationOrMDA()) {
//                        task = taskUtils.generateRegisterFamilyTask(applicationContext, structure.getId());
//                    } else {
//                        if (AppConstants.StructureType.RESIDENTIAL.equals(structureType)) {
//                            task = taskUtils.generateTask(applicationContext, structure.getId(), structure.getId(),
//                                    AppConstants.BusinessStatus.NOT_VISITED, AppConstants.Intervention.IRS, R.string.irs_task_description);
//                        } else if (AppConstants.StructureType.MOSQUITO_COLLECTION_POINT.equals(structureType)) {
//                            task = taskUtils.generateTask(applicationContext, structure.getId(), structure.getId(),
//                                    AppConstants.BusinessStatus.NOT_VISITED, AppConstants.Intervention.MOSQUITO_COLLECTION, R.string.mosquito_collection_task_description);
//                        } else if (AppConstants.StructureType.LARVAL_BREEDING_SITE.equals(structureType)) {
//                            task = taskUtils.generateTask(applicationContext, structure.getId(), structure.getId(),
//                                    AppConstants.BusinessStatus.NOT_VISITED, AppConstants.Intervention.LARVAL_DIPPING, R.string.larval_dipping_task_description);
//                        } else if (AppConstants.StructureType.POTENTIAL_AREA_OF_TRANSMISSION.equals(structureType)) {
//                            task = taskUtils.generateTask(applicationContext, structure.getId(), structure.getId(),
//                                    AppConstants.BusinessStatus.NOT_VISITED, AppConstants.Intervention.PAOT, R.string.poat_task_description);
//                        }
//                    }
//                    clientProcessor.processClient(Collections.singletonList(new EventClient(event, null)), true);
//                    Task finalTask = task;
//                    appExecutors.mainThread().execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            Map<String, String> taskProperties = new HashMap<>();
//                            if (finalTask != null) {
//
//                                taskProperties.put(AppConstants.Properties.TASK_IDENTIFIER, finalTask.getIdentifier());
//                                taskProperties.put(AppConstants.Properties.TASK_BUSINESS_STATUS, finalTask.getBusinessStatus());
//                                taskProperties.put(AppConstants.Properties.TASK_STATUS, finalTask.getStatus().name());
//                                taskProperties.put(AppConstants.Properties.TASK_CODE, finalTask.getCode());
//                            }
//                            taskProperties.put(AppConstants.Properties.LOCATION_UUID, structure.getProperties().getUid());
//                            taskProperties.put(AppConstants.Properties.LOCATION_VERSION, structure.getProperties().getVersion() + "");
//                            taskProperties.put(AppConstants.Properties.LOCATION_TYPE, structure.getProperties().getType());
//                            structure.getProperties().setCustomProperties(taskProperties);
//
//
//                            Obs myLocationActiveObs = event.findObs(null, false, AppConstants.JsonForm.LOCATION_COMPONENT_ACTIVE);
//
//                            boolean myLocationActive = myLocationActiveObs != null && Boolean.valueOf(myLocationActiveObs.getValue().toString());
//                            eusmApplication.setMyLocationComponentEnabled(myLocationActive);
//
//
//                            Obs zoomObs = event.findObs(null, false, GeoWidgetFactory.ZOOM_LEVEL);
//                            double zoomLevel = Double.parseDouble(zoomObs.getValue().toString());
//
//                            presenterCallBack.onStructureAdded(Feature.fromJson(gson.toJson(structure)), featureCoordinates, zoomLevel);
//                        }
//                    });
//                } catch (JSONException e) {
//                    Timber.e(e, "Error saving new Structure");
//                    presenterCallBack.onFormSaveFailure(AppConstants.REGISTER_STRUCTURE_EVENT);
//                }
//            }
//        };
//
//        appExecutors.diskIO().execute(runnable);
    }

    private void saveMemberForm(JSONObject jsonForm, String eventType, String intervention) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    org.smartregister.domain.Event event = saveEvent(jsonForm, eventType, FamilyConstants.TABLE_NAME.FAMILY_MEMBER);
//                    Client client = eventClientRepository.fetchClientByBaseEntityId(event.getBaseEntityId());
//                    clientProcessor.processClient(Collections.singletonList(new EventClient(event, client)), true);
//                    appExecutors.mainThread().execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            String businessStatus = clientProcessor.calculateBusinessStatus(event);
//                            String taskID = event.getDetails().get(AppConstants.Properties.TASK_IDENTIFIER);
//                            presenterCallBack.onFormSaved(event.getBaseEntityId(), taskID, Task.TaskStatus.COMPLETED, businessStatus, intervention);
//                        }
//                    });
//                } catch (Exception e) {
//                    Timber.e("Error saving member event form");
//                }
//            }
//        };
//        appExecutors.diskIO().execute(runnable);
    }

    private void saveCaseConfirmation(JSONObject jsonForm, String eventType) {
        appExecutors.diskIO().execute(() -> {
            try {
                String baseEntityId = JsonFormUtils.getFieldValue(JsonFormUtils.fields(jsonForm), AppConstants.JsonForm.FAMILY_MEMBER);
                jsonForm.put(ENTITY_ID, baseEntityId);
                org.smartregister.domain.Event event = saveEvent(jsonForm, eventType, AppConstants.Intervention.CASE_CONFIRMATION);
                Client client = eventClientRepository.fetchClientByBaseEntityId(event.getBaseEntityId());
                String taskID = event.getDetails().get(AppConstants.Properties.TASK_IDENTIFIER);
                String businessStatus = clientProcessor.calculateBusinessStatus(event);
                Task task = taskRepository.getTaskByIdentifier(taskID);
                task.setForEntity(baseEntityId);
                task.setBusinessStatus(businessStatus);
                task.setStatus(Task.TaskStatus.COMPLETED);
                task.setSyncStatus(BaseRepository.TYPE_Created);
                taskRepository.addOrUpdate(task);
                Set<Task> removedTasks = new HashSet<>();
                for (Task bloodScreeningTask : taskRepository.getTasksByEntityAndCode(prefsUtil.getCurrentPlanId(),
                        Utils.getOperationalAreaLocation(prefsUtil.getCurrentOperationalArea()).getId(), baseEntityId, AppConstants.Intervention.BLOOD_SCREENING)) {
                    bloodScreeningTask.setStatus(Task.TaskStatus.CANCELLED);
                    bloodScreeningTask.setSyncStatus(BaseRepository.TYPE_Created);
                    taskRepository.addOrUpdate(bloodScreeningTask);
                    removedTasks.add(bloodScreeningTask);
                }
                eusmApplication.setSynced(false);
                clientProcessor.processClient(Collections.singletonList(new EventClient(event, client)), true);
                appExecutors.mainThread().execute(() -> {
                    ((StructureTasksContract.Presenter) presenterCallBack).onIndexConfirmationFormSaved(taskID, Task.TaskStatus.COMPLETED, businessStatus, removedTasks);
                });
            } catch (Exception e) {
                Timber.e("Error saving case confirmation data");
            }
        });
    }

//    protected String getMemberTasksSelect(String mainCondition, String[] memberColumns) {
//        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
//        queryBuilder.selectInitiateMainTable(Constants.DatabaseKeys.STRUCTURES_TABLE, memberColumns, Constants.DatabaseKeys.ID);
//        queryBuilder.customJoin(String.format(" JOIN %s ON %s.%s = %s.%s ",
//                FamilyConstants.TABLE_NAME.FAMILY_MEMBER, FamilyConstants.TABLE_NAME.FAMILY_MEMBER, Constants.DatabaseKeys.STRUCTURE_ID, Constants.DatabaseKeys.STRUCTURES_TABLE, Constants.DatabaseKeys.ID));
//        queryBuilder.customJoin(String.format(" JOIN %s ON %s.%s = %s.%s ",
//                Constants.DatabaseKeys.TASK_TABLE, Constants.DatabaseKeys.TASK_TABLE, Constants.DatabaseKeys.FOR, FamilyConstants.TABLE_NAME.FAMILY_MEMBER, BASE_ENTITY_ID));
//        return queryBuilder.mainCondition(mainCondition);
//    }

    public void fetchFamilyDetails(String structureId) {
//        appExecutors.diskIO().execute(() -> {
//            Cursor cursor = null;
//            CommonPersonObjectClient family = null;
//            try {
//                cursor = database.rawQuery(String.format("SELECT %s FROM %S WHERE %s = ? AND %s IS NULL",
//                        INTENT_KEY.BASE_ENTITY_ID, FamilyConstants.TABLE_NAME.FAMILY, Constants.DatabaseKeys.STRUCTURE_ID, DATE_REMOVED), new String[]{structureId});
//                if (cursor.moveToNext()) {
//                    String baseEntityId = cursor.getString(0);
//                    setCommonRepository();
//                    final CommonPersonObject personObject = commonRepository.findByBaseEntityId(baseEntityId);
//                    family = new CommonPersonObjectClient(personObject.getCaseId(),
//                            personObject.getDetails(), "");
//                    family.setColumnmaps(personObject.getColumnmaps());
//                }
//            } catch (Exception e) {
//                Timber.e(e);
//            } finally {
//                if (cursor != null)
//                    cursor.close();
//            }
//
//            CommonPersonObjectClient finalFamily = family;
//            appExecutors.mainThread().execute(() -> {
//                presenterCallBack.onFamilyFound(finalFamily);
//            });
//        });
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setCommonRepository() {
//        if (commonRepository == null) {
//            commonRepository = eusmApplication.getContext().commonrepository(metadata().familyRegister.tableName);
//
//        }
    }

    @Override
    public void findLastEvent(String eventBaseEntityId, String eventType) {
        appExecutors.diskIO().execute(() -> {
            String events = String.format("select %s from %s where %s = ? and %s =? order by %s desc limit 1",
                    EventClientRepository.event_column.json, EventClientRepository.Table.event.name(), EventClientRepository.event_column.baseEntityId, EventClientRepository.event_column.eventType, EventClientRepository.event_column.updatedAt);

            try (Cursor cursor = getDatabase().rawQuery(events, new String[]{eventBaseEntityId, eventType})) {

                if (cursor.moveToFirst()) {
                    String eventJSON = cursor.getString(0);
                    handleLastEventFound(eventClientRepository.convert(eventJSON, org.smartregister.domain.Event.class));

                } else {
                    handleLastEventFound(null);
                }
            } catch (SQLException e) {
                Timber.e(e);
            }
        });

    }
}
