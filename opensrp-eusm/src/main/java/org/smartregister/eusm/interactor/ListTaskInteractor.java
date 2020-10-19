package org.smartregister.eusm.interactor;

import com.mapbox.geojson.Feature;

import net.sqlcipher.Cursor;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Location;
import org.smartregister.domain.Task;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.HomeActivityContract;
import org.smartregister.eusm.model.CardDetails;
import org.smartregister.eusm.model.StructureDetails;
import org.smartregister.eusm.model.StructureTaskDetails;
import org.smartregister.eusm.model.TaskDetails;
import org.smartregister.eusm.presenter.HomeActivityPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.GeoJsonUtils;
import org.smartregister.eusm.util.InteractorUtils;
import org.smartregister.eusm.util.Utils;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.domain.LocationProperty.PropertyStatus.INACTIVE;

/**
 * Created by samuelgithengi on 11/27/18.
 */
public class ListTaskInteractor extends BaseInteractor {

    private final CommonRepository commonRepository;
    private final InteractorUtils interactorUtils;
    private final StructureRepository structureRepository;
    private final TaskRepository taskRepository;
    private final EusmApplication eusmApplication;

    public ListTaskInteractor(HomeActivityContract.Presenter presenter) {
        super(presenter);
        commonRepository = EusmApplication.getInstance().getContext().commonrepository(AppConstants.DatabaseKeys.SPRAYED_STRUCTURES);
        structureRepository = EusmApplication.getInstance().getContext().getStructureRepository();
        taskRepository = EusmApplication.getInstance().getTaskRepository();
        interactorUtils = new InteractorUtils(taskRepository, eventClientRepository, clientProcessor);
        eusmApplication = EusmApplication.getInstance();
    }

    public static List<TaskDetails> processTaskDetails(Map<String, Set<Task>> map) {

        List<TaskDetails> taskDetailsList = new ArrayList<>();

        for (Map.Entry<String, Set<Task>> entry : map.entrySet()) {

            for (Task task : entry.getValue()) {

                taskDetailsList.add(convertToTaskDetails(task));
            }

        }

        return taskDetailsList;

    }

    /**
     * Convert task to task details object
     *
     * @param task the task
     * @return TaskDetails object
     */

    public static TaskDetails convertToTaskDetails(Task task) {

        TaskDetails taskDetails = new TaskDetails(task.getIdentifier());

        taskDetails.setTaskCode(task.getCode());
        taskDetails.setTaskEntity(task.getForEntity());
        taskDetails.setBusinessStatus(task.getBusinessStatus());
        taskDetails.setTaskStatus(task.getStatus().name());
        taskDetails.setStructureId(task.getStructureId());

        return taskDetails;

    }

    public void fetchInterventionDetails(String interventionType, String featureId, boolean isForForm) {
        String sql = "SELECT status, start_date, end_date FROM %s WHERE id=?";
        if (AppConstants.Intervention.IRS.equals(interventionType)) {
            sql = "SELECT spray_status, not_sprayed_reason, not_sprayed_other_reason, property_type, spray_date," +
                    " spray_operator, family_head_name FROM sprayed_structures WHERE id=?";
        } else if (AppConstants.Intervention.MOSQUITO_COLLECTION.equals(interventionType)) {
            sql = String.format(sql, AppConstants.Tables.MOSQUITO_COLLECTIONS_TABLE);
        } else if (AppConstants.Intervention.LARVAL_DIPPING.equals(interventionType)) {
            sql = String.format(sql, AppConstants.Tables.LARVAL_DIPPINGS_TABLE);
        } else if (AppConstants.Intervention.PAOT.equals(interventionType)) {
            sql = String.format("SELECT %s, %s, %s  FROM %s WHERE %s=? ", AppConstants.DatabaseKeys.PAOT_STATUS,
                    AppConstants.DatabaseKeys.PAOT_COMMENTS, AppConstants.DatabaseKeys.LAST_UPDATED_DATE, AppConstants.Tables.PAOT_TABLE, AppConstants.DatabaseKeys.BASE_ENTITY_ID);
        } else if (AppConstants.Intervention.IRS_VERIFICATION.equals(interventionType)) {
            sql = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s WHERE id= ?",
                    AppConstants.DatabaseKeys.TRUE_STRUCTURE, AppConstants.DatabaseKeys.ELIGIBLE_STRUCTURE, AppConstants.DatabaseKeys.REPORT_SPRAY, AppConstants.DatabaseKeys.CHALK_SPRAY, AppConstants.DatabaseKeys.STICKER_SPRAY, AppConstants.DatabaseKeys.CARD_SPRAY, AppConstants.Tables.IRS_VERIFICATION_TABLE);
        } else if (AppConstants.Intervention.REGISTER_FAMILY.equals(interventionType)) {
            sql = String.format("SELECT %s, %s, %s FROM %s WHERE %s = ?",
                    AppConstants.DatabaseKeys.BUSINESS_STATUS, AppConstants.DatabaseKeys.AUTHORED_ON, AppConstants.DatabaseKeys.OWNER, AppConstants.DatabaseKeys.TASK_TABLE, AppConstants.DatabaseKeys.FOR);
        }

        final String SQL = sql;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getDatabase().rawQuery(SQL, new String[]{featureId});

                CardDetails cardDetails = null;
                try {
                    if (cursor.moveToFirst()) {
                        cardDetails = createCardDetails(cursor, interventionType);
                        cardDetails.setInterventionType(interventionType);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                // run on ui thread
                final CardDetails CARD_DETAILS = cardDetails;
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isForForm) {
                            getSprayDetails(interventionType, featureId, CARD_DETAILS);
                            ((HomeActivityPresenter) presenterCallBack).onInterventionFormDetailsFetched(CARD_DETAILS);
                        } else {
                            ((HomeActivityPresenter) presenterCallBack).onCardDetailsFetched(CARD_DETAILS);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void getSprayDetails(String interventionType, String structureId, CardDetails cardDetails) {
        if (!AppConstants.Intervention.IRS.equals(interventionType))
            return;
        CommonPersonObject commonPersonObject = interactorUtils.fetchSprayDetails(interventionType, structureId,
                eventClientRepository, commonRepository);
//        ((SprayCardDetails) cardDetails).setCommonPersonObject(commonPersonObject);
    }

    private CardDetails createCardDetails(Cursor cursor, String interventionType) {
        CardDetails cardDetails = null;
//        if (Constants.Intervention.MOSQUITO_COLLECTION.equals(interventionType) || Constants.Intervention.LARVAL_DIPPING.equals(interventionType)) {
//            cardDetails = createMosquitoHarvestCardDetails(cursor, interventionType);
//        } else if (Constants.Intervention.IRS.equals(interventionType)) {
//            cardDetails = createSprayCardDetails(cursor);
//        } else if (Constants.Intervention.PAOT.equals(interventionType)) {
//            cardDetails = createPaotCardDetails(cursor, interventionType);
//        } else if (Constants.Intervention.IRS_VERIFICATION.equals(interventionType)) {
//            cardDetails = createIRSverificationCardDetails(cursor);
//        } else if (Constants.Intervention.REGISTER_FAMILY.equals(interventionType)) {
//            cardDetails = createFamilyCardDetails(cursor);
//        }

        return cardDetails;
    }

    public void fetchLocations(String plan, String operationalArea) {
        fetchLocations(plan, operationalArea, null, null);
    }

    public void fetchLocations(String plan, String operationalArea, String point, Boolean locationComponentActive) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                JSONObject featureCollection = null;

                Location operationalAreaLocation = Utils.getOperationalAreaLocation(operationalArea);
                List<TaskDetails> taskDetailsList = null;

                try {
                    featureCollection = createFeatureCollection();
                    if (operationalAreaLocation != null) {
                        Map<String, Set<Task>> tasks = taskRepository.getTasksByPlanAndGroup(plan, operationalAreaLocation.getId());
                        List<Location> structures = structureRepository.getLocationsByParentId(operationalAreaLocation.getId());
                        Map<String, StructureDetails> structureNames = new HashMap<>();//getStructureName(operationalAreaLocation.getId());
                        taskDetailsList = processTaskDetails(tasks);
                        String indexCase = null;
//                        if (Utils.getInterventionLabel() == R.string.focus_investigation)
//                            indexCase = getIndexCaseStructure(plan);
                        String features = GeoJsonUtils.getGeoJsonFromStructuresAndTasks(structures, tasks, indexCase, structureNames);
                        featureCollection.put(AppConstants.GeoJSON.FEATURES, new JSONArray(features));

                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
                JSONObject finalFeatureCollection = featureCollection;
                List<TaskDetails> finalTaskDetailsList = taskDetailsList;
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (operationalAreaLocation != null) {
                            operationalAreaId = operationalAreaLocation.getId();
                            Feature operationalAreaFeature = Feature.fromJson(gson.toJson(operationalAreaLocation));
                            if (locationComponentActive != null) {
                                getPresenter().onStructuresFetched(finalFeatureCollection, operationalAreaFeature, finalTaskDetailsList, point, locationComponentActive);
                            } else {
                                getPresenter().onStructuresFetched(finalFeatureCollection, operationalAreaFeature, finalTaskDetailsList);
                            }
                        } else {
                            getPresenter().onStructuresFetched(finalFeatureCollection, null, null);
                        }
                    }
                });

            }

        };


        appExecutors.diskIO().execute(runnable);
    }

//    protected String getStructureNamesSelect(String mainCondition) {
//        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
//        queryBuilder.selectInitiateMainTable(Constants.DatabaseKeys.STRUCTURES_TABLE, new String[]{
//                String.format("COALESCE(%s.%s,%s,%s,%s)", FamilyConstants.TABLE_NAME.FAMILY, Constants.DatabaseKeys.FIRST_NAME, Constants.DatabaseKeys.STRUCTURE_NAME, Constants.DatabaseKeys.NAME, FAMILY_HEAD_NAME),
//                String.format("group_concat(%s.%s||' '||%s.%s)", FamilyConstants.TABLE_NAME.FAMILY_MEMBER, Constants.DatabaseKeys.FIRST_NAME, FamilyConstants.TABLE_NAME.FAMILY_MEMBER, Constants.DatabaseKeys.LAST_NAME)}, Constants.DatabaseKeys.ID);
//        queryBuilder.customJoin(String.format("LEFT JOIN %s ON %s.%s = %s.%s AND %s.%s IS NULL collate nocase ",
//                FamilyConstants.TABLE_NAME.FAMILY, Constants.DatabaseKeys.STRUCTURES_TABLE, Constants.DatabaseKeys.ID, FamilyConstants.TABLE_NAME.FAMILY, Constants.DatabaseKeys.STRUCTURE_ID, FamilyConstants.TABLE_NAME.FAMILY, DATE_REMOVED));
//        return queryBuilder.mainCondition(mainCondition);
//    }
//
//    private Map<String, StructureDetails> getStructureName(String parentId) {
//        Cursor cursor = null;
//        Map<String, StructureDetails> structureNames = new HashMap<>();
//        try {
//            String query = getStructureNamesSelect(String.format("%s=?",
//                    Constants.DatabaseKeys.PARENT_ID)).concat(String.format(" GROUP BY %s.%s", Constants.DatabaseKeys.STRUCTURES_TABLE, Constants.DatabaseKeys.ID));
//            Timber.d(query);
//            cursor = getDatabase().rawQuery(query, new String[]{parentId});
//            while (cursor.moveToNext()) {
//                structureNames.put(cursor.getString(0), new StructureDetails(cursor.getString(1), cursor.getString(2)));
//            }
//        } catch (Exception e) {
//            Timber.e(e);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//        return structureNames;
//    }

    private JSONObject createFeatureCollection() throws JSONException {
        JSONObject featureCollection = new JSONObject();
        featureCollection.put(AppConstants.GeoJSON.TYPE, AppConstants.GeoJSON.FEATURE_COLLECTION);
        return featureCollection;
    }

    private HomeActivityContract.Presenter getPresenter() {
        return (HomeActivityContract.Presenter) presenterCallBack;
    }

    public void markStructureAsInactive(Feature feature) {

        try {
            Location structure = structureRepository.getLocationById(feature.id());
            structure.getProperties().setStatus(INACTIVE);
            structureRepository.addOrUpdate(structure);


            taskRepository.cancelTasksForEntity(feature.id());

            eusmApplication.setSynced(false);
        } catch (Exception e) {
            Timber.e(e);
        }

        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                ((HomeActivityPresenter) presenterCallBack).onStructureMarkedInactive();
            }
        });

    }

    public void markStructureAsIneligible(Feature feature, String reasonUnligible) {

        String taskIdentifier = Utils.getPropertyValue(feature, AppConstants.Properties.TASK_IDENTIFIER);
        String code = Utils.getPropertyValue(feature, AppConstants.Properties.TASK_CODE);

        if (AppConstants.Intervention.REGISTER_FAMILY.equals(code)) {

            Task task = taskRepository.getTaskByIdentifier(taskIdentifier);
            Map<String, String> details = new HashMap<>();
            details.put(AppConstants.Properties.TASK_IDENTIFIER, taskIdentifier);
            details.put(AppConstants.Properties.TASK_BUSINESS_STATUS, task.getBusinessStatus());
            details.put(AppConstants.Properties.TASK_STATUS, task.getStatus().name());
            details.put(AppConstants.Properties.LOCATION_ID, feature.id());
            details.put(AppConstants.Properties.APP_VERSION_NAME, BuildConfig.VERSION_NAME);
            task.setBusinessStatus(AppConstants.BusinessStatus.NOT_ELIGIBLE);
            task.setStatus(Task.TaskStatus.COMPLETED);
            task.setLastModified(new DateTime());
            taskRepository.addOrUpdate(task);
            eusmApplication.setSynced(false);
//            Event event = FamilyJsonFormUtils.createFamilyEvent(task.getForEntity(), feature.id(), details, FamilyConstants.EventType.FAMILY_REGISTRATION_INELIGIBLE);
//            event.addObs(new Obs().withValue(reasonUnligible).withFieldCode("eligible").withFieldType("formsubmissionField"));
//            event.addObs(new Obs().withValue(task.getBusinessStatus()).withFieldCode("whyNotEligible").withFieldType("formsubmissionField"));
//            try {
//                eventClientRepository.addEvent(feature.id(), new JSONObject(gson.toJson(event)));
//            } catch (JSONException e) {
//                Timber.e(e);
//            }

        }

        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                ((HomeActivityPresenter) presenterCallBack).onStructureMarkedIneligible();
            }
        });
    }

    private String[] getStructureColumns() {
        return new String[]{
                AppConstants.DatabaseKeys.TASK_TABLE + "." + AppConstants.DatabaseKeys.ID,
                AppConstants.DatabaseKeys.TASK_TABLE + "." + AppConstants.DatabaseKeys.CODE,
                AppConstants.DatabaseKeys.TASK_TABLE + "." + AppConstants.DatabaseKeys.FOR,
                AppConstants.DatabaseKeys.TASK_TABLE + "." + AppConstants.DatabaseKeys.BUSINESS_STATUS,
                AppConstants.DatabaseKeys.TASK_TABLE + "." + AppConstants.DatabaseKeys.STATUS,
                AppConstants.DatabaseKeys.TASK_TABLE + "." + AppConstants.DatabaseKeys.STRUCTURE_ID
        };
    }

    public String getTaskSelect(String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(AppConstants.DatabaseKeys.TASK_TABLE, getStructureColumns(), AppConstants.DatabaseKeys.ID);
        return queryBuilder.mainCondition(mainCondition);
    }

    public void resetInterventionTaskInfo(String interventionType, String featureId) {
        String sql = String.format(getTaskSelect("%s = ? and %s = ?"),
                AppConstants.DatabaseKeys.FOR, AppConstants.DatabaseKeys.CODE);

        final String SQL = sql;

        appExecutors.diskIO().execute(() -> {
            StructureTaskDetails taskDetails = null;
            Cursor cursor = null;
            boolean taskInfoResetSuccessful = false;
            try {
                cursor = getDatabase().rawQuery(SQL, new String[]{featureId, interventionType});
                if (cursor.moveToNext()) {
                    taskDetails = readTaskDetails(cursor);
                }

            } catch (Exception e) {
                Timber.e(e, "Error querying tasks details for %s", featureId);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            // Reset task info
            taskInfoResetSuccessful = interactorUtils.resetTaskInfo(getDatabase(), taskDetails);


            boolean finalTaskInfoResetSuccessful = taskInfoResetSuccessful;
//            appExecutors.mainThread().execute(() -> {
//                getPresenter().onInterventionTaskInfoReset(finalTaskInfoResetSuccessful);
//            });
        });


    }

    public StructureTaskDetails readTaskDetails(Cursor cursor) {
        StructureTaskDetails task = new StructureTaskDetails(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.ID)));
        task.setTaskCode(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.CODE)));
        task.setTaskEntity(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.FOR)));
        task.setBusinessStatus(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.BUSINESS_STATUS)));
        task.setTaskStatus(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.STATUS)));
        task.setStructureId(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.STRUCTURE_ID)));
        return task;
    }

    @Override
    public void handleLastEventFound(org.smartregister.domain.Event event) {
        getPresenter().onEventFound(event);
    }

}
