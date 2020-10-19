package org.smartregister.eusm.interactor;

import android.content.Context;

import androidx.core.util.Pair;

import com.mapbox.mapboxsdk.offline.OfflineRegion;

import org.smartregister.domain.Location;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.DownloadedOfflineMapsContract;
import org.smartregister.eusm.model.OfflineMapModel;
import org.smartregister.eusm.util.OfflineMapHelper;
import org.smartregister.repository.LocationRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ona.kujaku.data.realm.RealmDatabase;
import io.ona.kujaku.data.realm.objects.MapBoxOfflineQueueTask;

public class DownloadedOfflineMapsInteractor implements DownloadedOfflineMapsContract.Interactor {

    private final AppExecutors appExecutors;

    private final LocationRepository locationRepository;

    private final DownloadedOfflineMapsContract.Presenter presenter;

    private final RealmDatabase realmDatabase;

    private Map<String, MapBoxOfflineQueueTask> offlineQueueTaskMap;

    public DownloadedOfflineMapsInteractor(DownloadedOfflineMapsContract.Presenter presenter, Context context) {
        this.presenter = presenter;
        appExecutors = EusmApplication.getInstance().getAppExecutors();
        locationRepository = EusmApplication.getInstance().getLocationRepository();
        realmDatabase = EusmApplication.getInstance().getRealmDatabase(context);
        offlineQueueTaskMap = new HashMap<>();
    }

    @Override
    public void fetchLocationsWithOfflineMapDownloads(final Pair<List<String>, Map<String, OfflineRegion>> offlineRegionInfo) {

        Runnable runnable = new Runnable() {
            public void run() {
                if (offlineRegionInfo == null || offlineRegionInfo.first == null) {
                    presenter.onOAsWithOfflineDownloadsFetched(null);
                    return;
                }

                List<Location> operationalAreas = locationRepository.getLocationsByIds(offlineRegionInfo.first);

                setOfflineQueueTaskMap(OfflineMapHelper.populateOfflineQueueTaskMap(realmDatabase));

                List<OfflineMapModel> offlineMapModels = populateOfflineMapModelList(operationalAreas, offlineRegionInfo.second);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        presenter.onOAsWithOfflineDownloadsFetched(offlineMapModels);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);

    }

    public List<OfflineMapModel> populateOfflineMapModelList(List<Location> locations, Map<String, OfflineRegion> offlineRegionMap) {

        List<OfflineMapModel> offlineMapModels = new ArrayList<>();
        for (Location location : locations) {
            OfflineMapModel offlineMapModel = new OfflineMapModel();
            offlineMapModel.setLocation(location);
            offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.DOWNLOADED);
            offlineMapModel.setOfflineRegion(offlineRegionMap.get(location.getId()));

            if (offlineQueueTaskMap.get(location.getId()) != null) {
                offlineMapModel.setDateCreated(offlineQueueTaskMap.get(location.getId()).getDateCreated());
            }

            offlineMapModels.add(offlineMapModel);
        }

        return offlineMapModels;
    }

    public void setOfflineQueueTaskMap(Map<String, MapBoxOfflineQueueTask> offlineQueueTaskMap) {
        this.offlineQueueTaskMap = offlineQueueTaskMap;
    }

}
