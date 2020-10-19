package org.smartregister.eusm.interactor;

import org.smartregister.domain.Location;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.AvailableOfflineMapsContract;
import org.smartregister.eusm.model.OfflineMapModel;
import org.smartregister.repository.LocationRepository;

import java.util.ArrayList;
import java.util.List;

public class AvailableOfflineMapsInteractor implements AvailableOfflineMapsContract.Interactor {

    private final AppExecutors appExecutors;

    private final LocationRepository locationRepository;

    private final AvailableOfflineMapsContract.Presenter presenter;

    public AvailableOfflineMapsInteractor(AvailableOfflineMapsContract.Presenter presenter) {
        this.presenter = presenter;
        appExecutors = EusmApplication.getInstance().getAppExecutors();
        locationRepository = EusmApplication.getInstance().getLocationRepository();
    }


    @Override
    public void fetchAvailableOAsForMapDownLoad(final List<String> locationIds) {

        Runnable runnable = new Runnable() {
            public void run() {
                List<Location> operationalAreas = locationRepository.getLocationsByIds(locationIds, false);

                appExecutors.mainThread().execute(() -> {
                    presenter.onFetchAvailableOAsForMapDownLoad(populateOfflineMapModelList(operationalAreas));
                });
            }
        };

        appExecutors.diskIO().execute(runnable);

    }

    public List<OfflineMapModel> populateOfflineMapModelList(List<Location> locations) {
        List<OfflineMapModel> offlineMapModels = new ArrayList<>();
        for (Location location : locations) {
            OfflineMapModel offlineMapModel = new OfflineMapModel();
            offlineMapModel.setLocation(location);
            offlineMapModels.add(offlineMapModel);
        }

        return offlineMapModels;
    }

}
