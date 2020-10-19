package org.smartregister.eusm.presenter;

import org.smartregister.eusm.contract.AvailableOfflineMapsContract;
import org.smartregister.eusm.interactor.AvailableOfflineMapsInteractor;
import org.smartregister.eusm.model.OfflineMapModel;

import java.util.List;

public class AvailableOfflineMapsPresenter implements AvailableOfflineMapsContract.Presenter {

    private final AvailableOfflineMapsContract.Interactor interactor;
    private final AvailableOfflineMapsContract.View view;

    public AvailableOfflineMapsPresenter(AvailableOfflineMapsContract.View view) {
        this.view = view;
        this.interactor = new AvailableOfflineMapsInteractor(this);
    }

    @Override
    public void fetchAvailableOAsForMapDownLoad(List<String> locationIds) {
        interactor.fetchAvailableOAsForMapDownLoad(locationIds);
    }

    @Override
    public void onFetchAvailableOAsForMapDownLoad(List<OfflineMapModel> offlineMapModels) {
        view.setOfflineMapModelList(offlineMapModels);
    }

    @Override
    public void onDownloadStarted(String operationalAreaId) {
        view.disableCheckBox(operationalAreaId);

    }

    @Override
    public void onDownloadComplete(String operationalAreaId) {
        view.moveDownloadedOAToDownloadedList(operationalAreaId);
    }

    @Override
    public void onDownloadStopped(String operationalAreaId) {
        view.removeOperationalAreaToDownload(operationalAreaId);
        view.enableCheckBox(operationalAreaId);
    }

}
