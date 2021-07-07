package org.smartregister.eusm.repository;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smartregister.domain.Task;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.TaskNotesRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class AppTaskRepositoryTest extends BaseUnitTest {

    private AppTaskRepository appTaskRepository;

    @Mock
    private TaskNotesRepository taskNotesRepository;

    @Mock
    private Cursor cursor;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appTaskRepository = spy(new AppTaskRepository(taskNotesRepository));
    }

    @Test
    public void testGetTasksByStructureIdShouldReturnListOfTasks() {
        doReturn(sqLiteDatabase).when(appTaskRepository).getReadableDatabase();

        doReturn(cursor).when(sqLiteDatabase)
                .rawQuery(anyString(), any(String[].class));

        doAnswer(new Answer() {
            int count = -1;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count == 0;
            }
        }).when(cursor).moveToNext();

        List<TaskDetail> taskDetails = appTaskRepository.getTasksByStructureId("23-3", "32", "23");
        assertEquals(1, taskDetails.size());
    }

    @Test
    public void testUpdateTaskStatusShouldInvokeAddOrUpdate() {
        String id = UUID.randomUUID().toString();
        Task task = new Task();
        task.setIdentifier(id);
        task.setStatus(Task.TaskStatus.READY);
        task.setPriority(Task.TaskPriority.ROUTINE);
        task.setForEntity(UUID.randomUUID().toString());

        doReturn(task).when(appTaskRepository).getTaskByIdentifier(id);
        doNothing().when(appTaskRepository).addOrUpdate(any(Task.class));

        appTaskRepository.updateTaskStatus(id, Task.TaskStatus.COMPLETED, AppConstants.BusinessStatus.NOT_VISITED);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);

        verify(appTaskRepository).addOrUpdate(taskArgumentCaptor.capture());

        Task argTask = taskArgumentCaptor.getValue();

        assertEquals(AppConstants.BusinessStatus.NOT_VISITED, argTask.getBusinessStatus());
        assertEquals(Task.TaskStatus.COMPLETED, argTask.getStatus());
        assertEquals(BaseRepository.TYPE_Unsynced, argTask.getSyncStatus());
    }
}