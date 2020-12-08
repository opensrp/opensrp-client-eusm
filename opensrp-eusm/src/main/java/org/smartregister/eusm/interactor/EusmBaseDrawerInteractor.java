package org.smartregister.eusm.interactor;

import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.interactor.BaseDrawerInteractor;

public class EusmBaseDrawerInteractor extends BaseDrawerInteractor {

    private PlanDefinitionRepository planDefinitionRepository;

    public EusmBaseDrawerInteractor(BaseDrawerContract.Presenter presenter) {
        super(presenter);
    }

//    @Override
//    public void fetchPlans(String jurisdictionName) {
//        appExecutors.diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                if(StringUtils.isBlank(jurisdictionName)){
//                    Set<PlanDefinition> planDefinitionSet = planDefinitionRepository.findAllPlanDefinitions();
//                    appExecutors.mainThread().execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            presenter.onPlansFetched(planDefinitionSet);
//                        }
//                    });
//
//                }
//            }
//        });
//
//    }
}
