package org.smartregister.eusm.interactor;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.BaseFormFragmentContract;
import org.smartregister.eusm.util.InteractorUtils;
import org.smartregister.repository.EventClientRepository;

public class BaseFormFragmentInteractor implements BaseFormFragmentContract.Interactor {

    protected EventClientRepository eventClientRepository;
    private final BaseFormFragmentContract.Presenter presenter;
    private final CommonRepository commonRepository;
    private final AppExecutors appExecutors;
    private final SQLiteDatabase sqLiteDatabase;
    private final InteractorUtils interactorUtils;

    public BaseFormFragmentInteractor(BaseFormFragmentContract.Presenter presenter) {
        this.presenter = presenter;
        this.commonRepository = EusmApplication.getInstance().getContext().commonrepository("");//metadata().familyMemberRegister.tableName);
        appExecutors = EusmApplication.getInstance().getAppExecutors();
        sqLiteDatabase = EusmApplication.getInstance().getRepository().getReadableDatabase();
        eventClientRepository = EusmApplication.getInstance().getContext().getEventClientRepository();
        interactorUtils = new InteractorUtils();
    }

}
