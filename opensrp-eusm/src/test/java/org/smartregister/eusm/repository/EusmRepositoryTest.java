package org.smartregister.eusm.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.repository.DrishtiRepository;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EusmRepositoryTest extends BaseUnitTest {

    private EusmRepository eusmRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private Context context;

    @Mock
    private org.smartregister.Context opensrpContext;

    @Before
    public void setUp() {
        org.smartregister.Context.bindtypes = new ArrayList<>();
        DrishtiRepository[] drishtiRepositories = new DrishtiRepository[0];
        doReturn(drishtiRepositories).when(opensrpContext).sharedRepositoriesArray();
        eusmRepository = spy(new EusmRepository(context, opensrpContext));
    }

    @Test
    public void testOnCreateShouldExecuteInitializingDbQueries() {
        eusmRepository.onCreate(sqLiteDatabase);
        verify(sqLiteDatabase, times(83)).execSQL(anyString());
    }
}