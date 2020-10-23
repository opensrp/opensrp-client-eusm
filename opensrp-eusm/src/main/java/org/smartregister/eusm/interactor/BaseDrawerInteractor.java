package org.smartregister.eusm.interactor;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Location;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.BaseDrawerContract;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.PlanDefinitionSearchRepository;

import java.util.Set;

public class BaseDrawerInteractor implements BaseDrawerContract.Interactor {

    private final AppExecutors appExecutors;

    private final BaseDrawerContract.Presenter presenter;

    private final PlanDefinitionRepository planDefinitionRepository;

    private final PlanDefinitionSearchRepository planDefinitionSearchRepository;

    private final EusmApplication eusmApplication;


    public BaseDrawerInteractor(BaseDrawerContract.Presenter presenter) {
        this.presenter = presenter;
        eusmApplication = EusmApplication.getInstance();
        appExecutors = EusmApplication.getInstance().getAppExecutors();
        planDefinitionRepository = EusmApplication.getInstance().getPlanDefinitionRepository();
        planDefinitionSearchRepository = EusmApplication.getInstance().getPlanDefinitionSearchRepository();
    }

    @Override
    public void fetchPlans(String jurisdictionName) {
        Runnable runnable = () -> {
            Set<PlanDefinition> planDefinitionSet;
            if (StringUtils.isNotBlank(jurisdictionName)) {
                Location operationalArea = AppUtils.getOperationalAreaLocation(jurisdictionName);
                String jurisdictionIdentifier = operationalArea != null ? operationalArea.getId() : null;
                planDefinitionSet = planDefinitionSearchRepository.findActivePlansByJurisdiction(jurisdictionIdentifier);
            } else {
                planDefinitionSet = planDefinitionRepository.findAllPlanDefinitions();
            }
            appExecutors.mainThread().execute(() -> presenter.onPlansFetched(planDefinitionSet));
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void validateCurrentPlan(String selectedOperationalArea, String currentPlanId) {
        Runnable runnable = () -> {
            Location operationalArea = AppUtils.getOperationalAreaLocation(selectedOperationalArea);
            String jurisdictionIdentifier = operationalArea != null ? operationalArea.getId() : null;
            boolean isValid = planDefinitionSearchRepository.planExists(currentPlanId, jurisdictionIdentifier);
            appExecutors.mainThread().execute(() -> presenter.onPlanValidated(isValid));
        };

        appExecutors.diskIO().execute(runnable);

    }

    @Override
    public void checkSynced() {
        Runnable runnable = () -> {
            boolean isSynced = EusmApplication.getInstance().getAppRepository().checkSynced();
            eusmApplication.setSynced(isSynced);
            appExecutors.mainThread().execute(() -> (presenter).updateSyncStatusDisplay(isSynced));
        };
        appExecutors.diskIO().execute(runnable);
    }
}
