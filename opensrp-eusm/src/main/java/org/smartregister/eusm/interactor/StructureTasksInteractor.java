package org.smartregister.eusm.interactor;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.VisibleForTesting;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;

import org.apache.commons.lang3.ArrayUtils;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Event;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.StructureTasksContract;
import org.smartregister.eusm.model.StructureTaskDetails;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.InteractorUtils;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.EventClientRepository.event_column;
import org.smartregister.repository.StructureRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.domain.Task.INACTIVE_TASK_STATUS;
import static org.smartregister.domain.Task.TaskStatus.READY;


/**
 * Created by samuelgithengi on 4/12/19.
 */
public class StructureTasksInteractor extends BaseInteractor implements StructureTasksContract.Interactor {

    private final AppExecutors appExecutors;

    private final SQLiteDatabase database;
    private final StructureTasksContract.Presenter presenter;
    private final StructureRepository structureRepository;
    private final InteractorUtils interactorUtils;

    public StructureTasksInteractor(StructureTasksContract.Presenter presenter) {
        this(presenter, EusmApplication.getInstance().getAppExecutors(), EusmApplication.getInstance().getRepository().getReadableDatabase(), EusmApplication.getInstance().getStructureRepository());
    }

    @VisibleForTesting
    protected StructureTasksInteractor(StructureTasksContract.Presenter presenter, AppExecutors appExecutors,
                                       SQLiteDatabase database, StructureRepository structureRepository) {
        super(presenter);
        this.presenter = presenter;
        this.appExecutors = appExecutors;
        this.database = database;
        this.structureRepository = structureRepository;
        this.interactorUtils = new InteractorUtils(EusmApplication.getInstance().getTaskRepository(), eventClientRepository, clientProcessor);
    }

    @Override
    public void findTasks(String structureId, String planId, String operationalAreaId) {
        appExecutors.diskIO().execute(() -> {
            List<StructureTaskDetails> taskDetailsList = new ArrayList<>();
            StructureTaskDetails incompleteIndexCase = null;
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(getTaskSelect(String.format(
                        "%s=? AND %s=? AND %s NOT IN (%s)", AppConstants.DatabaseKeys.FOR, AppConstants.DatabaseKeys.PLAN_ID, AppConstants.DatabaseKeys.STATUS,
                        TextUtils.join(",", Collections.nCopies(INACTIVE_TASK_STATUS.length, "?")))),
                        ArrayUtils.addAll(new String[]{structureId, planId,}, INACTIVE_TASK_STATUS));
                while (cursor.moveToNext()) {
                    taskDetailsList.add(readTaskDetails(cursor));
                }

                cursor.close();
                cursor = database.rawQuery(getTaskSelect(String.format("%s = ? AND %s = ? AND %s = ? AND %s = ?",
                        AppConstants.DatabaseKeys.GROUPID, AppConstants.DatabaseKeys.PLAN_ID, AppConstants.DatabaseKeys.CODE, AppConstants.DatabaseKeys.STATUS)),
                        new String[]{operationalAreaId, planId, AppConstants.Intervention.CASE_CONFIRMATION, READY.name()});
                if (cursor.moveToNext()) {
                    incompleteIndexCase = readTaskDetails(cursor);
                }
            } catch (Exception e) {
                Timber.e(e, "Error querying tasks for %s", structureId);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            StructureTaskDetails finalIndexCase = incompleteIndexCase;
//            populateEventsPerTask(taskDetailsList);
            appExecutors.mainThread().execute(() -> {
                presenter.onTasksFound(taskDetailsList, finalIndexCase);
            });
        });
    }

    @Override
    public void getStructure(StructureTaskDetails details) {
        appExecutors.diskIO().execute(() -> {

            String structureId = details.getTaskEntity();
            if (AppConstants.Intervention.PERSON_INTERVENTIONS.contains(details.getTaskCode())) {
                structureId = details.getStructureId();
            }
            org.smartregister.domain.Location structure =
                    structureRepository.getLocationById(structureId);
            appExecutors.mainThread().execute(() -> {
                presenter.onStructureFound(structure, details);
            });
        });
    }

    @Override
    public void findLastEvent(StructureTaskDetails taskDetails) {

        appExecutors.diskIO().execute(() -> {
            String eventType = taskDetails.getTaskCode().equals(AppConstants.Intervention.BLOOD_SCREENING) ? AppConstants.BLOOD_SCREENING_EVENT : AppConstants.BEDNET_DISTRIBUTION_EVENT;
            String events = String.format("select %s from %s where %s = ? and %s =? order by %s desc limit 1",
                    event_column.json, EventClientRepository.Table.event.name(), event_column.baseEntityId, event_column.eventType, event_column.updatedAt);
            Cursor cursor = null;
            try {
                cursor = database.rawQuery(events, new String[]{taskDetails.getTaskEntity(), eventType});
                if (cursor.moveToFirst()) {
                    String eventJSON = cursor.getString(0);
                    presenter.onEventFound(eventClientRepository.convert(eventJSON, Event.class));

                }
            } catch (SQLException e) {
                Timber.e(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        });

    }

    @Override
    public void resetTaskInfo(Context context, StructureTaskDetails taskDetails) {
        appExecutors.diskIO().execute(() -> {
            interactorUtils.resetTaskInfo(getDatabase(), taskDetails);

            appExecutors.mainThread().execute(() -> {
                presenter.onTaskInfoReset(taskDetails.getStructureId());
            });
        });
    }


//    private void populateEventsPerTask(List<StructureTaskDetails> tasks) {
//        SQLiteStatement eventsPerTask = database.compileStatement("SELECT count(*) as events_per_task FROM event_task WHERE task_id = ?");
//        SQLiteStatement lastEventDate = database.compileStatement("SELECT max(event_date) FROM event_task WHERE task_id = ?");
//
//        try {
//            for (StructureTaskDetails task : tasks) {
//                EventTask eventTask = new EventTask();
//
//                eventsPerTask.bindString(1, task.getTaskId());
//                eventTask.setEventsPerTask(eventsPerTask.simpleQueryForLong());
//
//                if (eventTask.getEventsPerTask() > 1) {
//                    lastEventDate.bindString(1, task.getTaskId());
//                    eventTask.setLastEventDate(lastEventDate.simpleQueryForString());
//                    task.setLastEdited(DateUtil.parseDate(eventTask.getLastEventDate()));
//
//                }
//
//                if (AppConstants.Intervention.BLOOD_SCREENING.equals(task.getTaskCode())) {
//                    setPersonTested(task, eventTask.getEventsPerTask() > 1);
//                }
//
//            }
//
//        } catch (Exception e) {
//            Timber.e(e, "Error querying events counts ");
//        } finally {
//            if (eventsPerTask != null) {
//                eventsPerTask.close();
//            }
//            if (lastEventDate != null) {
//                lastEventDate.close();
//            }
//        }
//
//    }

    private void setPersonTested(StructureTaskDetails task, boolean isEdit) {

        SQLiteStatement personTestWithEdits = null;
        SQLiteStatement personTested = null;

        try {
            if (isEdit) {
                String personTestWithEditsSql = "select person_tested from event_task where id in " +
                        "(select formSubmissionId from event where baseEntityId = ? and eventType = ? " +
                        "order by updatedAt desc limit 1)";
                personTestWithEdits = database.compileStatement(personTestWithEditsSql);
                personTestWithEdits.bindString(1, task.getTaskEntity());
                personTestWithEdits.bindString(2, AppConstants.BLOOD_SCREENING_EVENT);
                task.setPersonTested(personTestWithEdits.simpleQueryForString());
            } else {
                personTested = database.compileStatement("SELECT person_tested FROM event_task WHERE task_id = ?");
                personTested.bindString(1, task.getTaskId());
                task.setPersonTested(personTested.simpleQueryForString());
            }
        } catch (SQLException e) {
            Timber.e(e, "Error querying person tested values ");
        } finally {
            if (personTestWithEdits != null) {
                personTestWithEdits.close();
            }
            if (personTested != null) {
                personTested.close();
            }
        }
    }

    private String getTaskSelect(String mainCondition) {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(AppConstants.DatabaseKeys.TASK_TABLE, getStructureColumns(), AppConstants.DatabaseKeys.ID);
        return queryBuilder.mainCondition(mainCondition);
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

    private StructureTaskDetails readTaskDetails(Cursor cursor) {
        StructureTaskDetails task = new StructureTaskDetails(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.ID)));
        task.setTaskCode(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.CODE)));
        task.setTaskEntity(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.FOR)));
        task.setBusinessStatus(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.BUSINESS_STATUS)));
        task.setTaskStatus(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.STATUS)));
        task.setStructureId(cursor.getString(cursor.getColumnIndex(AppConstants.DatabaseKeys.STRUCTURE_ID)));
        return task;
    }
}
