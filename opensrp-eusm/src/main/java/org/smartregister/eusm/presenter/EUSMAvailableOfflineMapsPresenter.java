package org.smartregister.eusm.presenter;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.tasking.contract.AvailableOfflineMapsContract;
import org.smartregister.tasking.presenter.AvailableOfflineMapsPresenter;

import java.util.ArrayList;
import java.util.List;

public class EUSMAvailableOfflineMapsPresenter extends AvailableOfflineMapsPresenter {

    public EUSMAvailableOfflineMapsPresenter(AvailableOfflineMapsContract.View view) {
        super(view);
    }

    @Override
    public void fetchAvailableOAsForMapDownLoad(List<String> locationIds) {
        List<String> regionIds = new ArrayList<>();
        for (String districtId : locationIds) {
            regionIds.add(EusmApplication.getInstance().getAppLocationRepository().getRegionIdForDistrictId(districtId).getId());
        }
        super.fetchAvailableOAsForMapDownLoad(regionIds);
    }
}
