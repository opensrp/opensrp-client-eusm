package org.smartregister.eusm.contract;

import org.smartregister.eusm.model.OfflineMapModel;

public interface OfflineMapDownloadCallback {

    void onMapDownloaded(OfflineMapModel offlineMapModel);

    void onOfflineMapDeleted(OfflineMapModel offlineMapModel);
}
