package org.smartregister.eusm.presenter;

import android.content.Context;

import androidx.core.util.Pair;

import com.mapbox.mapboxsdk.offline.OfflineRegion;

import org.smartregister.eusm.contract.DownloadedOfflineMapsContract;
import org.smartregister.eusm.interactor.DownloadedOfflineMapsInteractor;
import org.smartregister.eusm.model.OfflineMapModel;

import java.util.List;
import java.util.Map;

public class DownloadedOfflineMapsPresenter implements DownloadedOfflineMapsContract.Presenter {

    private final DownloadedOfflineMapsContract.View view;
    private final DownloadedOfflineMapsContract.Interactor interactor;

    public DownloadedOfflineMapsPresenter(DownloadedOfflineMapsContract.View view, Context context) {
        this.view = view;
        this.interactor = new DownloadedOfflineMapsInteractor(this, context);
    }

    @Override
    public void onDeleteDownloadMap(List<OfflineMapModel> offlineMapModels) {
        view.deleteDownloadedOfflineMaps();
    }

    @Override
    public void fetchOAsWithOfflineDownloads(Pair<List<String>, Map<String, OfflineRegion>> offlineRegionInfo) {
        interactor.fetchLocationsWithOfflineMapDownloads(offlineRegionInfo);
    }

    @Override
    public void onOAsWithOfflineDownloadsFetched(List<OfflineMapModel> downloadedOfflineMapModelList) {
        view.setDownloadedOfflineMapModelList(downloadedOfflineMapModelList);
    }
}
