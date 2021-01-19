package org.smartregister.eusm.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.tasking.util.InteractorUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class AppRepositoryTest extends BaseUnitTest {

    private AppRepository appRepository;

    @Before
    public void setUp() {
        appRepository = spy(new AppRepository());
    }

    @Test
    public void testArchiveEventsForTaskShouldInvokeRequiredMethod() {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTaskId("2322");
        InteractorUtils interactorUtils = mock(InteractorUtils.class);
        ReflectionHelpers.setField(appRepository, "interactorUtils", interactorUtils);
        appRepository.archiveEventsForTask(taskDetail);
        verify(interactorUtils).archiveEventsForTask(any(SQLiteDatabase.class), any(TaskDetail.class));
    }
}