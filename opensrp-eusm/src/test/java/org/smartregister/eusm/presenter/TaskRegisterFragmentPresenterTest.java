package org.smartregister.eusm.presenter;

import android.app.Activity;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.annotation.LooperMode;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.util.AppConstants;

import java.util.UUID;

import static android.os.Looper.getMainLooper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
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
    public void testUndoTask() throws InterruptedException {
        TaskDetail taskDetail = new TaskDetail();
        taskRegisterFragmentPresenter.undoTask(taskDetail);
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(taskRegisterFragmentPresenter).onTaskUndone(eq(false), eq(taskDetail));
    }
}