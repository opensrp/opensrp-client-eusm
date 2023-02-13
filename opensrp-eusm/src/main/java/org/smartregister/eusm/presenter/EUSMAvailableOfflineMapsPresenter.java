package org.smartregister.eusm.presenter;

import org.smartregister.domain.Location;
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
        if (locationIds != null) {
            for (String districtId : locationIds) {
                Location region = EusmApplication.getInstance().getAppLocationRepository().getRegionIdForDistrictId(districtId);
                if (region != null)
                    regionIds.add(region.getId());
            }
        }
        super.fetchAvailableOAsForMapDownLoad(regionIds);
    }

    @Override
    public void onDownloadStarted(String operationalAreaId) {
        // DO nothing
    }
}
