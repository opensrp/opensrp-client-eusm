package org.smartregister.eusm.interactor;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import androidx.core.util.Pair;

import com.google.common.annotations.VisibleForTesting;

import net.sqlcipher.Cursor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Event;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.ServicePointRegisterFragmentContract;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.model.TaskDetails;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.InteractorUtils;
import org.smartregister.eusm.util.Utils;
import org.smartregister.repository.EventClientRepository.event_column;
import org.smartregister.repository.LocationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.domain.Task.INACTIVE_TASK_STATUS;
import static org.smartregister.repository.EventClientRepository.Table.event;

/**
 * Created by samuelgithengi on 3/18/19.
 */
public class TaskRegisterFragmentInteractor extends BaseInteractor implements TaskRegisterFragmentContract.Interactor {

    private final LocationRepository locationRepository;
    private final Float locationBuffer;
    private final InteractorUtils interactorUtils;


    private int structuresWithinBuffer = 0;

    public TaskRegisterFragmentInteractor(TaskRegisterFragmentContract.Presenter presenter) {
        this(presenter, Utils.getLocationBuffer());
    }

    @VisibleForTesting
    public TaskRegisterFragmentInteractor(TaskRegisterFragmentContract.Presenter presenter,
                                          Float locationBuffer) {
        super(presenter);
        this.locationBuffer = locationBuffer;
        locationRepository = EusmApplication.getInstance().getLocationRepository();
        interactorUtils = new InteractorUtils(EusmApplication.getInstance().getTaskRepository(), eventClientRepository, clientProcessor);
    }

    private String mainSelect(String mainCondition) {
        String tableName = AppConstants.DatabaseKeys.TASK_TABLE;
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, mainColumns(tableName), AppConstants.DatabaseKeys.ID);
        queryBuilder.customJoin(String.format(" JOIN %s ON %s.%s = %s.%s ",
                AppConstants.DatabaseKeys.STRUCTURES_TABLE, tableName, AppConstants.DatabaseKeys.FOR, AppConstants.DatabaseKeys.STRUCTURES_TABLE, AppConstants.DatabaseKeys.ID));
        return queryBuilder.mainCondition(mainCondition);
    }

    private String bccSelect() {
        return String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s ='%s' AND %s NOT IN (%s)",
                AppConstants.DatabaseKeys.TASK_TABLE, AppConstants.DatabaseKeys.FOR, AppConstants.DatabaseKeys.PLAN_ID, AppConstants.DatabaseKeys.CODE, AppConstants.Intervention.BCC, AppConstants.DatabaseKeys.STATUS,
                TextUtils.join(",", Collections.nCopies(INACTIVE_TASK_STATUS.length, "?")));
    }

    private String indexCaseSelect() {
        return String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? AND %s NOT IN (%s) AND %s = ? ",
                AppConstants.DatabaseKeys.TASK_TABLE, AppConstants.DatabaseKeys.GROUPID, AppConstants.DatabaseKeys.PLAN_ID, AppConstants.DatabaseKeys.STATUS,
                TextUtils.join(",", Collections.nCopies(INACTIVE_TASK_STATUS.length, "?")),
                AppConstants.DatabaseKeys.CODE);
    }


    private String[] mainColumns(String tableName) {
        return new String[]{
                tableName + "." + AppConstants.DatabaseKeys.ID,
                tableName + "." + AppConstants.DatabaseKeys.CODE,
                tableName + "." + AppConstants.DatabaseKeys.FOR,
                tableName + "." + AppConstants.DatabaseKeys.BUSINESS_STATUS,
                tableName + "." + AppConstants.DatabaseKeys.STATUS,
                tableName + "." + AppConstants.DatabaseKeys.REFERENCE_REASON,
                AppConstants.DatabaseKeys.STRUCTURES_TABLE + "." + AppConstants.DatabaseKeys.LATITUDE,
                AppConstants.DatabaseKeys.STRUCTURES_TABLE + "." + AppConstants.DatabaseKeys.LONGITUDE,
                AppConstants.DatabaseKeys.STRUCTURES_TABLE + "." + AppConstants.DatabaseKeys.NAME,
                AppConstants.DatabaseKeys.SPRAYED_STRUCTURES + "." + AppConstants.DatabaseKeys.STRUCTURE_NAME,
                AppConstants.DatabaseKeys.SPRAYED_STRUCTURES + "." + AppConstants.DatabaseKeys.FAMILY_NAME,
                AppConstants.DatabaseKeys.SPRAYED_STRUCTURES + "." + AppConstants.DatabaseKeys.SPRAY_STATUS,
                AppConstants.DatabaseKeys.SPRAYED_STRUCTURES + "." + AppConstants.DatabaseKeys.NOT_SRAYED_REASON,
                AppConstants.DatabaseKeys.SPRAYED_STRUCTURES + "." + AppConstants.DatabaseKeys.NOT_SRAYED_OTHER_REASON,
                AppConstants.DatabaseKeys.STRUCTURES_TABLE + "." + AppConstants.DatabaseKeys.ID + " AS " + AppConstants.DatabaseKeys.STRUCTURE_ID
        };
    }


    public void findTasks(Pair<String, String[]> mainCondition, Location lastLocation, Location operationalAreaCenter, String houseLabel) {
        if (mainCondition == null || mainCondition.second == null || mainCondition.second.length < 3 || mainCondition.second[0] == null) {
//            getPresenter().onTasksFound(null, 0);
            return;
        }
        // Fetch grouped tasks
        List<TaskDetails> tasks = new ArrayList<>();
        appExecutors.diskIO().execute(() -> {
            structuresWithinBuffer = 0;
//            if (!Utils.isFocusInvestigationOrMDA()) {
//
//                tasks.addAll(queryTaskDetails(mainSelect(mainCondition.first), mainCondition.second,
//                        lastLocation, operationalAreaCenter, houseLabel, false));
//
//            } else { // perform task grouping
////
////                tasks.addAll(queryTaskDetails(groupedRegisteredStructureTasksSelect(mainCondition.first),
////                        mainCondition.second, lastLocation, operationalAreaCenter, houseLabel, true));
////
////
////                tasks.addAll(queryTaskDetails(nonRegisteredStructureTasksSelect(mainCondition.first),
////                        mainCondition.second, lastLocation, operationalAreaCenter, houseLabel, false));
//
//            }

            // Query BCC task
            tasks.addAll(queryTaskDetails(bccSelect(), mainCondition.second, lastLocation,
                    operationalAreaCenter, houseLabel, false));


            // Query Case Confirmation task
            String[] params = ArrayUtils.add(mainCondition.second, AppConstants.Intervention.CASE_CONFIRMATION);
            tasks.addAll(queryTaskDetails(indexCaseSelect(), params, lastLocation,
                    operationalAreaCenter, houseLabel, false));

            Collections.sort(tasks);
            appExecutors.mainThread().execute(() -> {
//                getPresenter().onTasksFound(tasks, structuresWithinBuffer);
            });

        });

    }

    private List<TaskDetails> queryTaskDetails(String query, String[] params, Location lastLocation,
                                               Location operationalAreaCenter, String houseLabel, boolean groupedTasks) {
        List<TaskDetails> tasks = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getDatabase().rawQuery(query, params);
            while (cursor != null && cursor.moveToNext()) {
                TaskDetails taskDetails = readTaskDetails(cursor, lastLocation, operationalAreaCenter, houseLabel, groupedTasks);
                //skip BCC and Case confirmation tasks in tracking tasks within buffer
                if (taskDetails.getDistanceFromUser() <= locationBuffer && taskDetails.getDistanceFromUser() >= 0) {
                    structuresWithinBuffer += 1;
                }
                tasks.add(taskDetails);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tasks;
    }


    private TaskDetails readTaskDetails(Cursor cursor, Location lastLocation, Location operationalAreaCenter, String houseLabel, boolean isGroupedTasks) {
        TaskDetails task = new TaskDetails(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.ID)));
//        task.setTaskCode(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.CODE)));
//        task.setTaskEntity(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.FOR)));
//        task.setBusinessStatus(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.BUSINESS_STATUS)));
//        task.setTaskStatus(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.STATUS)));
//        if (isGroupedTasks) {
//            task.setTaskCount(cursor.getInt(cursor.getColumnIndex(AppConstants.DatabaseKeys.TASK_COUNT)));
//            task.setCompleteTaskCount(cursor.getInt(cursor.getColumnIndex(AppConstants.DatabaseKeys.COMPLETED_TASK_COUNT)));
//            task.setGroupedTaskCodeStatus(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.GROUPED_STRUCTURE_TASK_CODE_AND_STATUS)));
//            task.setHouseNumber(cursor.getString(cursor.getColumnIndex(FamilyConstants.DatabaseKeys.HOUSE_NUMBER)));
//            task.setFamilyMemberNames(cursor.getString(cursor.getColumnIndex(AppConstants.Properties.FAMILY_MEMBER_NAMES)));
//        }
//        Location location = new Location((String) null);
//
//        if (AppConstants.Intervention.CASE_CONFIRMATION.equals(task.getTaskCode())) {
//            task.setReasonReference(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.REFERENCE_REASON)));
//        }
//        task.setStructureId(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.STRUCTURE_ID)));
//
//        calculateDistance(task, location, lastLocation, operationalAreaCenter);

        return task;
    }

    private void calculateDistance(TaskDetails task, Location location, Location lastLocation, Location operationalAreaCenter) {
        if (AppConstants.Intervention.BCC.equals(task.getTaskCode())) {
            //set distance to -2 to always display on top of register
            task.setDistanceFromUser(-2);
        } else if (AppConstants.Intervention.CASE_CONFIRMATION.equals(task.getTaskCode()) && task.getTaskCount() == null) {
            //set distance to -1 to always display on top of register and below BCC
            task.setDistanceFromUser(-1);
        } else if (lastLocation != null) {
            task.setDistanceFromUser(location.distanceTo(lastLocation));
        } else {
            task.setDistanceFromUser(location.distanceTo(operationalAreaCenter));
            task.setDistanceFromCenter(true);
        }
    }


    public void calculateDistanceFromUser(List<TaskDetails> tasks, Location location) {
        if (tasks == null)
            return;
        appExecutors.diskIO().execute(() -> {
            int structuresWithinBuffer = 0;
            for (TaskDetails taskDetails : tasks) {
                if (!AppConstants.Intervention.BCC.equals(taskDetails.getTaskCode()) && !AppConstants.Intervention.CASE_CONFIRMATION.equals(taskDetails.getTaskCode())) {
                    taskDetails.setDistanceFromUser(taskDetails.getLocation().distanceTo(location));
                    taskDetails.setDistanceFromCenter(false);
                }
                if (taskDetails.getDistanceFromUser() <= locationBuffer) {
                    structuresWithinBuffer += 1;
                }
            }
            Collections.sort(tasks);
            int finalStructuresWithinBuffer = structuresWithinBuffer;
            appExecutors.mainThread().execute(() -> {
//                getPresenter().onTasksFound(tasks, finalStructuresWithinBuffer);
            });
        });

    }


    public void getStructure(TaskDetails taskDetails) {
        appExecutors.diskIO().execute(() -> {
            org.smartregister.domain.Location structure;
            if (AppConstants.Intervention.BCC.equals(taskDetails.getTaskCode()))
                structure = locationRepository.getLocationById(taskDetails.getTaskEntity());
            else
                structure = structureRepository.getLocationById(taskDetails.getStructureId());
            appExecutors.mainThread().execute(() -> {
//                getPresenter().onStructureFound(structure, taskDetails);
            });
        });
    }

    private ServicePointRegisterFragmentContract.Presenter getPresenter() {
        return (ServicePointRegisterFragmentContract.Presenter) presenterCallBack;
    }

    public void getIndexCaseDetails(String structureId, String operationalArea, String indexCaseEventId) {
        appExecutors.diskIO().execute(() -> {
            JSONObject jsonEvent = null;
            if (StringUtils.isNotBlank(structureId) || StringUtils.isNotBlank(operationalArea)) {

                Cursor cursor = null;
                try {
                    String[] params;
                    if (structureId == null) {
                        params = new String[]{operationalArea, AppConstants.EventType.CASE_DETAILS_EVENT};
                    } else {
                        params = new String[]{structureId, operationalArea, AppConstants.EventType.CASE_DETAILS_EVENT};
                    }
                    String query = String.format("SELECT %s FROM %s WHERE %s IN (%s) AND %s = ?", event_column.json.name(), event.name(), event_column.baseEntityId.name(),
                            structureId == null ? "?" : "?,?", event_column.eventType.name());
                    cursor = getDatabase().rawQuery(query, params);
                    while (cursor.moveToNext()) {
                        String jsonEventStr = cursor.getString(0);

                        jsonEventStr = jsonEventStr.replaceAll("'", "");
                        JSONObject localJsonEvent = new JSONObject(jsonEventStr);

                        if (cursor.getCount() == 1 || localJsonEvent.optString(AppConstants.DatabaseKeys.ID).equals(indexCaseEventId)) {
                            jsonEvent = new JSONObject(jsonEventStr);
                            break;
                        }
                    }
                } catch (Exception e) {
                    Timber.e(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }
            JSONObject finalJsonEvent = jsonEvent;
            appExecutors.mainThread().execute(() -> {
                getPresenter().onIndexCaseFound(finalJsonEvent, finalJsonEvent != null
                        && operationalArea.equals(finalJsonEvent.optString(AppConstants.Properties.BASE_ENTITY_ID)));
            });
        });

    }

    @Override
    public void resetTaskInfo(Context context, TaskDetails taskDetails) {
        appExecutors.diskIO().execute(() -> {
            interactorUtils.resetTaskInfo(getDatabase(), taskDetails);
        });

        appExecutors.mainThread().execute(() -> {
//            getPresenter().onTaskInfoReset();
        });
    }

    @Override
    public void handleLastEventFound(Event event) {
        getPresenter().onEventFound(event);
    }
}
