package org.smartregister.eusm.interactor;

import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.ConfigurableViewsHelper;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.BaseContract;
import org.smartregister.eusm.contract.TaskRegisterContract;

import java.util.List;

/**
 * Created by samuelgithengi on 3/14/19.
 */
public class TaskRegisterInteractor extends BaseInteractor implements TaskRegisterContract.Interactor {

    private ConfigurableViewsHelper viewsHelper;

    private AppExecutors appExecutors;

    public TaskRegisterInteractor(BaseContract.BasePresenter presenterCallBack) {
        super(presenterCallBack);
        viewsHelper = ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper();
        appExecutors = EusmApplication.getInstance().getAppExecutors();
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        appExecutors.diskIO().execute(() -> {
            viewsHelper.registerViewConfigurations(viewIdentifiers);
        });
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        viewsHelper.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void cleanupResources() {
        viewsHelper = null;
        appExecutors = null;
    }


}
