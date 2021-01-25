package org.smartregister.eusm.presenter;

import android.app.Activity;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.domain.Task;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.repository.AppRepository;
import org.smartregister.eusm.repository.AppTaskRepository;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Collections;
import java.util.UUID;

import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
public class TaskRegisterFragmentPresenterTest extends BaseUnitTest {

    private TaskRegisterFragmentPresenter taskRegisterFragmentPresenter;

    @Mock
    private TaskRegisterFragmentContract.View view;

    @Before
    public void setUp() {
        taskRegisterFragmentPresenter = spy(new TaskRegisterFragmentPresenter(view));
    }

    @Test
    public void testFetchDataShouldInvokeCallbackMethods() throws InterruptedException {
        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setStructureId(UUID.randomUUID().toString());
        doReturn(structureDetail).when(view).getStructureDetail();
        taskRegisterFragmentPresenter.fetchData();
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(taskRegisterFragmentPresenter).onFetchedData(anyList());
    }

    @Test
    public void testStartFormShouldInvokeCallbackMethod() throws InterruptedException {
        StructureDetail structureDetail = new StructureDetail();
        structureDetail.setStructureId(UUID.randomUUID().toString());
        doReturn(structureDetail).when(view).getStructureDetail();
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        doReturn(activity)
                .when(view).getActivity();
        taskRegisterFragmentPresenter.startForm(structureDetail, new TaskDetail(), AppConstants.JsonForm.RECORD_GPS_FORM);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(taskRegisterFragmentPresenter).onFormFetched(any(JSONObject.class));
    }

    @Test
    public void testUndoTaskShouldInvokeOnTaskDoneWithFalseStatus() throws InterruptedException {
        TaskDetail taskDetail = new TaskDetail();
        taskRegisterFragmentPresenter.undoTask(taskDetail);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(taskRegisterFragmentPresenter).onTaskUndone(eq(false), eq(taskDetail));
    }

    @Test
    public void testUndoTaskShouldInvokeOnTaskDoneWithTrueStatus() throws InterruptedException {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTaskId("123");
        taskDetail.setPlanId("321");
        taskDetail.setGroupId("111");
        taskDetail.setForEntity("222");
        taskDetail.setBusinessStatus(AppConstants.BusinessStatus.HAS_PROBLEM);

        Task task = new Task();
        task.setIdentifier("123");
        task.setPriority(Task.TaskPriority.ROUTINE);
        task.setStatus(Task.TaskStatus.READY);

        AppRepository mockAppRepository = mock(AppRepository.class);
        AppTaskRepository mockAppTaskRepository = mock(AppTaskRepository.class);
        EusmApplication mockApplication = mock(EusmApplication.class);

        doReturn(Collections.singleton(task)).when(mockAppTaskRepository).getTasksByEntityAndCode(eq(taskDetail.getPlanId()), eq(taskDetail.getGroupId()), eq(taskDetail.getForEntity()), eq(AppConstants.EncounterType.FIX_PROBLEM));
        doNothing().when(mockAppTaskRepository).updateTaskStatus(eq(taskDetail.getTaskId()), eq(Task.TaskStatus.READY), eq(AppConstants.BusinessStatus.NOT_VISITED));
        doNothing().when(mockAppRepository).archiveEventsForTask(any(TaskDetail.class));
        doReturn(mockAppRepository).when(mockApplication).getAppRepository();
        doReturn(mockAppTaskRepository).when(mockApplication).getAppTaskRepository();

        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", mockApplication);

        taskRegisterFragmentPresenter.undoTask(taskDetail);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);

        verify(mockAppTaskRepository).updateTaskStatus(eq(taskDetail.getTaskId()), eq(Task.TaskStatus.READY), eq(AppConstants.BusinessStatus.NOT_VISITED));

        verify(mockAppTaskRepository).updateTaskStatus(eq(task.getIdentifier()), eq(Task.TaskStatus.READY), eq(AppConstants.BusinessStatus.NOT_VISITED));

        verify(mockAppRepository).archiveEventsForTask(eq(taskDetail));

        verify(taskRegisterFragmentPresenter).onTaskUndone(eq(true), eq(taskDetail));
    }
}