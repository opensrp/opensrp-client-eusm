package org.smartregister.eusm.interactor;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Task;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.repository.AppRepository;
import org.smartregister.eusm.repository.AppTaskRepository;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.util.AppExecutors;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskRegisterFragmentInteractor implements TaskRegisterFragmentContract.Interactor {

    private final AppExecutors appExecutors;

    private Context context;

    private AppJsonFormUtils jsonFormUtils;

    public TaskRegisterFragmentInteractor() {
        appExecutors = EusmApplication.getInstance().getAppExecutors();
    }

    @Override
    public void fetchData(@NonNull StructureDetail structureDetail, @NonNull TaskRegisterFragmentContract.InteractorCallBack callBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<TaskDetail> taskDetails = EusmApplication.getInstance().getAppTaskRepository()
                        .getTasksByStructureId(structureDetail.getStructureId());

                List<TaskDetail> sortTaskDetails = sortTaskDetails(taskDetails);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFetchedData(sortTaskDetails);
                    }
                });
            }
        });
    }


    @Override
    public void startForm(StructureDetail structureDetail, TaskDetail taskDetail, Activity activity,
                          String formName, TaskRegisterFragmentContract.InteractorCallBack callBack) {
        appExecutors.diskIO().execute(() -> {
            JSONObject form = getJsonFormUtils().getFormObjectWithDetails(activity, formName, structureDetail, taskDetail);
            if (form != null) {
                injectAdditionalFields(form, formName, structureDetail, taskDetail);
            }
            appExecutors.mainThread().execute(() -> callBack.onFormFetched(form));
        });

    }

    @Override
    public void injectAdditionalFields(@NonNull JSONObject jsonForm,
                                       @NonNull String formName,
                                       @NonNull StructureDetail structureDetail,
                                       @NonNull TaskDetail taskDetail) {
        Map<String, String> injectedFields = new HashMap<>();
        JSONObject step = jsonForm.optJSONObject(JsonFormConstants.STEP1);
        JSONArray jsonArray = step.optJSONArray(JsonFormConstants.FIELDS);
        for (Map.Entry<String, String> entry : injectedFields.entrySet()) {
            JSONObject jsonObject1 = AppUtils.getHiddenFieldTemplate(entry.getKey(), entry.getValue());
            jsonArray.put(jsonObject1);
        }
    }

    @Override
    public void undoTask(TaskDetail taskDetail, TaskRegisterFragmentContract.InteractorCallBack callBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (taskDetail != null) {

                    String taskId = taskDetail.getTaskId();
                    if (StringUtils.isNotBlank(taskId)) {
                        //TODO to be replaced by event submission

                        AppRepository appRepository = EusmApplication.getInstance().getAppRepository();
                        appRepository.archiveEventsForTask(taskDetail);

                        AppTaskRepository taskRepository = EusmApplication.getInstance().getAppTaskRepository();
                        taskRepository.updateTaskStatus(taskId, Task.TaskStatus.READY, "NOT VISITED");
                    }
                    returnResponse(callBack, taskDetail, true);
                } else {
                    returnResponse(callBack, taskDetail, false);
                }
            }
        });
    }

    private void returnResponse(TaskRegisterFragmentContract.InteractorCallBack callBack, TaskDetail taskDetail, boolean status) {
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                callBack.onTaskUndone(status, taskDetail);
            }
        });
    }

    private List<TaskDetail> sortTaskDetails(List<TaskDetail> taskDetails_) {

        List<TaskDetail> taskDetails = taskDetails_.stream()
                .sorted(Comparator
                        .comparing(TaskDetail::isChecked).thenComparing(TaskDetail::isNonProductTask))
                .collect(Collectors.toList());

        TaskDetail sItemHeader = new TaskDetail();
        sItemHeader.setEntityName(getContext().getString(R.string.task_item_text));
        sItemHeader.setHeader(true);

        taskDetails.add(0, sItemHeader);

        TaskDetail sChecked = new TaskDetail();
        sChecked.setEntityName(getContext().getString(R.string.task_checked_text));
        sChecked.setHeader(true);

        TaskDetail sEmpty = new TaskDetail();
        sEmpty.setEntityName(getContext().getString(R.string.no_items_to_check));
        sEmpty.setEmptyView(true);

        boolean hasUnCheckedItems = false;
        boolean hasCheckedItems = false;
        for (int i = 0; i < taskDetails.size(); i++) {
            TaskDetail taskDetail = taskDetails.get(i);
            if (!taskDetail.isHeader()) {
                if (!hasUnCheckedItems && !taskDetail.isChecked()) {
                    hasUnCheckedItems = true;
                } else if (taskDetail.isChecked() && !hasCheckedItems) {
                    taskDetails.add(i, sChecked);
                    hasCheckedItems = true;
                }
            }
        }

        if (!hasUnCheckedItems) {
            taskDetails.add(1, sEmpty);
        }

        if (!hasCheckedItems) {
            taskDetails.add(sChecked);
            taskDetails.add(sEmpty);
        }

        return taskDetails;
    }

    public AppJsonFormUtils getJsonFormUtils() {
        if (jsonFormUtils == null) {
            jsonFormUtils = new AppJsonFormUtils();
        }
        return jsonFormUtils;
    }

    public Context getContext() {
        if (context == null) {
            context = EusmApplication.getInstance().getBaseContext();
        }
        return context;
    }
}
