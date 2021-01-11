package org.smartregister.eusm;


import com.vijay.jsonwizard.NativeFormLibrary;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppTaskingLibraryConfiguration;
import org.smartregister.receiver.ValidateAssignmentReceiver;
import org.smartregister.repository.Repository;
import org.smartregister.tasking.TaskingLibrary;
import org.smartregister.util.AppExecutors;

import java.util.concurrent.Executors;

import io.ona.kujaku.data.realm.RealmDatabase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestEusmApplication extends EusmApplication {


    @Override
    public void onCreate() {
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context);
        ConfigurableViewsLibrary.init(context);

        TaskingLibrary.init(new AppTaskingLibraryConfiguration());

        setTheme(R.style.Theme_AppCompat); //or just R.style.Theme_AppCompat

        NativeFormLibrary.getInstance().setClientFormDao(CoreLibrary.getInstance().context().getClientFormRepository());
        ValidateAssignmentReceiver.init(getApplicationContext());
    }

    @Override
    public AppExecutors getAppExecutors() {
        return new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor());
    }

    @Override
    public Repository getRepository() {
        repository = mock(Repository.class);
        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);

        return repository;
    }

    @Override
    public RealmDatabase getRealmDatabase(android.content.Context context) {

        return mock(RealmDatabase.class);
    }

    @Override
    public void onTerminate() {
        //Do nothing
    }
}
