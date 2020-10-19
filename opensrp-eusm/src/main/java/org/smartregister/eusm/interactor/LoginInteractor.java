package org.smartregister.eusm.interactor;

import org.smartregister.CoreLibrary;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.job.LocationTaskServiceJob;
import org.smartregister.eusm.util.Utils;
import org.smartregister.job.DocumentConfigurationServiceJob;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;

public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        LocationTaskServiceJob.scheduleJob(LocationTaskServiceJob.TAG,
                BuildConfig.SYNC_INTERVAL_IN_MINUTES, getFlexValue((int) BuildConfig.SYNC_INTERVAL_IN_MINUTES));

//        PullUniqueIdsServiceJob.scheduleJob(SyncServiceJob.TAG,
//                BuildConfig.PULL_UNIQUE_IDS_MINUTES, getFlexValue((int) BuildConfig.PULL_UNIQUE_IDS_MINUTES));

        DocumentConfigurationServiceJob
                .scheduleJob(DocumentConfigurationServiceJob.TAG, BuildConfig.SYNC_INTERVAL_IN_MINUTES,
                        getFlexValue((int) BuildConfig.SYNC_INTERVAL_IN_MINUTES));
    }

    @Override
    protected void scheduleJobsImmediately() {
        Utils.startImmediateSync();
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
