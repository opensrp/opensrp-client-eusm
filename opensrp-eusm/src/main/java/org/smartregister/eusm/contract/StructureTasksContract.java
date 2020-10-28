package org.smartregister.eusm.contract;

import android.content.Context;

import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.eusm.model.StructureTaskDetail;

import java.util.List;
import java.util.Set;

/**
 * Created by samuelgithengi on 4/12/19.
 */
public interface StructureTasksContract {

    interface Presenter extends BaseContract.BasePresenter, BaseFormFragmentContract.Presenter {

        void findTasks(String structureId);

        void refreshTasks();

        void onTasksFound(List<StructureTaskDetail> taskDetailsList, StructureTaskDetail incompleteIndexCase);

        void onTaskSelected(StructureTaskDetail details, boolean isEdit, boolean isUndo);

        void saveJsonForm(String json);

        void onDetectCase();

        void onIndexConfirmationFormSaved(String taskID, Task.TaskStatus taskStatus, String businessStatus, Set<Task> removedTasks);

        void onEventFound(Event event);

        void resetTaskInfo(StructureTaskDetail taskDetails);

        void onTaskInfoReset(String structureId);
    }

    interface Interactor extends BaseContract.BaseInteractor {

        void findTasks(String structureId, String currentPlanId, String operationalAreaId);

        void getStructure(StructureTaskDetail details);

        void findLastEvent(StructureTaskDetail taskDetails);

        void resetTaskInfo(Context context, StructureTaskDetail taskDetails);
    }

    interface View extends UserLocationContract.UserLocationView, BaseFormFragmentContract.View {

        void setStructure(String structureId);

        void showProgressDialog(int title, int message);

        void hideProgressDialog();

        android.location.Location getUserCurrentLocation();

        Context getContext();

        void setTaskDetailsList(List<StructureTaskDetail> taskDetailsList);

        void updateTask(String taskID, Task.TaskStatus taskStatus, String businessStatus);

        void displayDetectCaseButton();

        void hideDetectCaseButton();

        void updateNumberOfTasks();

        void updateTasks(String taskID, Task.TaskStatus taskStatus, String businessStatus, Set<Task> removedTasks);

        void displayResetTaskInfoDialog(StructureTaskDetail taskDetails);
    }
}
