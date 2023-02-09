package org.smartregister.eusm.interactor;

import org.smartregister.CoreLibrary;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.tasking.TaskingLibrary;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;

public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        // Do Nothing
    }

    @Override
    protected void scheduleJobsImmediately() {
        TaskingLibrary.getInstance().getTaskingLibraryConfiguration().startImmediateSync();
    }

    @Override
    public void loginWithLocalFlag(WeakReference<BaseLoginContract.View> view, boolean localLogin, String userName, char[] password) {
        if (!localLogin) {
            EusmApplication.getInstance().getContext().getHttpAgent().setConnectTimeout(CoreLibrary.getInstance().getSyncConfiguration().getConnectTimeout());
            EusmApplication.getInstance().getContext().getHttpAgent().setReadTimeout(CoreLibrary.getInstance().getSyncConfiguration().getReadTimeout());
        }
        super.loginWithLocalFlag(view, localLogin, userName, password);
    }
}
