package org.smartregister.eusm.repository;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Task;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.TaskNotesRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.stock.repository.StockRepository;
import org.smartregister.stock.repository.StockTypeRepository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.domain.Task.INACTIVE_TASK_STATUS;

public class AppTaskRepository extends TaskRepository {


    public AppTaskRepository(TaskNotesRepository taskNotesRepository) {
        super(taskNotesRepository);
    }

    public List<TaskDetail> getTasksByStructureId(String structureId, String planId, String groupId) {
        List<TaskDetail> taskDetails = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String[] columns = new String[]{
                TASK_TABLE + "." + AppConstants.Column.Task.FOR,
                TASK_TABLE + "." + AppConstants.Column.Task.BUSINESS_STATUS,
                TASK_TABLE + "." + AppConstants.Column.Task.STATUS,
                TASK_TABLE + "." + AppConstants.Column.Task.ID + " as taskId",
                TASK_TABLE + "." + AppConstants.Column.Task.LOCATION + " as taskLocation",
                TASK_TABLE + "." + AppConstants.Column.Task.STRUCTURE_ID,
                TASK_TABLE + "." + AppConstants.Column.Task.CODE,
                TASK_TABLE + "." + AppConstants.Column.Task.PLAN_ID,
                TASK_TABLE + "." + AppConstants.Column.Task.GROUP_ID,

                StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.UNIQUE_ID,
                StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.NAME,
                StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.QUANTITY,
                StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.MATERIAL_NUMBER,
                StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.PHOTO_FILE_LOCATION,
                StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.CONDITION,
                StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.AVAILABILITY,
                StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.APPROPRIATE_USAGE,

                StockRepository.STOCK_TABLE_NAME + "." + StockRepository.STOCK_ID
        };

        String query = "SELECT " + StringUtils.join(columns, ",")
                + " FROM " + TASK_TABLE +
                " LEFT JOIN " + StockRepository.STOCK_TABLE_NAME + " ON " + StockRepository.STOCK_TABLE_NAME + "." + StockRepository.STOCK_ID + " = " + TASK_TABLE + "." + AppConstants.Column.Task.FOR +
                " LEFT JOIN " + StockTypeRepository.STOCK_TYPE_TABLE_NAME + " ON " + StockTypeRepository.STOCK_TYPE_TABLE_NAME + "." + StockTypeRepository.UNIQUE_ID + " = " + StockRepository.STOCK_TABLE_NAME + "." + StockRepository.IDENTIFIER +
                " WHERE task.structure_id = ? AND task.plan_id = ? AND task.group_id = ? AND task.status NOT IN  (" + StringUtils.repeat("?", ",", INACTIVE_TASK_STATUS.length) + ") group by taskId";
        try (Cursor cursor = sqLiteDatabase.rawQuery(query, ArrayUtils.addAll(new String[]{structureId, planId, groupId}, INACTIVE_TASK_STATUS))) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    taskDetails.add(readStructureTaskDetailCursor(cursor));
                }
            }
        } catch (SQLiteException e) {
            Timber.e(e);
        }
        return taskDetails;
    }

    private TaskDetail readStructureTaskDetailCursor(Cursor cursor) {
//        String taskName = cursor.getString(cursor.getColumnIndex("taskName"));
        String taskGroupId = cursor.getString(cursor.getColumnIndex(AppConstants.Column.Task.GROUP_ID));

        String taskFor = cursor.getString(cursor.getColumnIndex(AppConstants.Column.Task.FOR));
        String taskBusinessStatus = cursor.getString(cursor.getColumnIndex(AppConstants.Column.Task.BUSINESS_STATUS));
        String taskStatus = cursor.getString(cursor.getColumnIndex(AppConstants.Column.Task.STATUS));
        String taskId = cursor.getString(cursor.getColumnIndex("taskId"));
//        String taskLocation = cursor.getString(cursor.getColumnIndex("taskLocation"));
        String productName = cursor.getString(cursor.getColumnIndex(StockTypeRepository.NAME));
        String quantity = cursor.getString(cursor.getColumnIndex(StockTypeRepository.QUANTITY));
        String productSerial = cursor.getString(cursor.getColumnIndex(StockTypeRepository.MATERIAL_NUMBER));
        String productImage = cursor.getString(cursor.getColumnIndex(StockTypeRepository.PHOTO_FILE_LOCATION));
        String productId = cursor.getString(cursor.getColumnIndex(StockTypeRepository.UNIQUE_ID));

        String condition = cursor.getString(cursor.getColumnIndex(StockTypeRepository.CONDITION));
        String appropriateUsage = cursor.getString(cursor.getColumnIndex(StockTypeRepository.APPROPRIATE_USAGE));
        String availability = cursor.getString(cursor.getColumnIndex(StockTypeRepository.AVAILABILITY));
        String stockId = cursor.getString(cursor.getColumnIndex(StockRepository.STOCK_ID));
        String taskCode = cursor.getString(cursor.getColumnIndex(AppConstants.Column.Task.CODE));
        String planId = cursor.getString(cursor.getColumnIndex(AppConstants.Column.Task.PLAN_ID));

        TaskDetail taskDetail = new TaskDetail(taskId);
        if (productName != null)
            taskDetail.setEntityName(productName);
        else {
            String name = taskCode;
            if (AppConstants.EncounterType.RECORD_GPS.equals(taskCode)) {
                name = AppConstants.TaskCode.RECORD_GPS;
            } else if (AppConstants.EncounterType.SERVICE_POINT_CHECK.equals(taskCode)) {
                name = AppConstants.TaskCode.SERVICE_POINT_CHECK;
            }
            taskDetail.setEntityName(name);
        }
        taskDetail.setQuantity(quantity);
        taskDetail.setTaskId(taskId);
        taskDetail.setProductId(productId);
        taskDetail.setProductImage(productImage);
        taskDetail.setProductSerial(productSerial);
        taskDetail.setNonProductTask(productName == null);
        taskDetail.setCondition(condition);
        taskDetail.setAppropriateUsage(appropriateUsage);
        taskDetail.setAvailability(availability);
        taskDetail.setChecked(Task.TaskStatus.COMPLETED.name().equalsIgnoreCase(taskStatus));
        taskDetail.setStockId(stockId);
        taskDetail.setTaskCode(taskCode);
        taskDetail.setHasProblem(AppConstants.BusinessStatus.HAS_PROBLEM.equals(taskBusinessStatus));
        taskDetail.setBusinessStatus(taskBusinessStatus);
        taskDetail.setPlanId(planId);
        taskDetail.setForEntity(taskFor);
        taskDetail.setGroupId(taskGroupId);
        return taskDetail;
    }

    public void updateTaskStatus(String taskId,
                                 Task.TaskStatus taskStatus,
                                 String businessStatus) {
        Task task = getTaskByIdentifier(taskId);
        task.setBusinessStatus(businessStatus);
        task.setStatus(taskStatus);
        task.setSyncStatus(BaseRepository.TYPE_Unsynced);
        addOrUpdate(task);
    }
}
