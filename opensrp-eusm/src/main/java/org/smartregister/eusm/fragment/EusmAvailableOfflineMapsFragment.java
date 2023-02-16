package org.smartregister.eusm.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.mapbox.geojson.FeatureCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Location;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.tasking.fragment.AvailableOfflineMapsFragment;
import org.smartregister.tasking.model.OfflineMapModel;
import org.smartregister.tasking.presenter.AvailableOfflineMapsPresenter;
import org.smartregister.tasking.util.OfflineMapHelper;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class EusmAvailableOfflineMapsFragment extends AvailableOfflineMapsFragment {

    private AppStructureRepository appStructureRepository;

    private AppExecutors appExecutors;

    private List<OfflineMapModel> offlineMapModelList = new ArrayList<>();

    private List<Location> operationalAreasToDownload = new ArrayList<>();

    private Snackbar displayBar = null;

    public static EusmAvailableOfflineMapsFragment newInstance(Bundle bundle, @NonNull String mapStyleAssetPath) {
        EusmAvailableOfflineMapsFragment fragment = new EusmAvailableOfflineMapsFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        fragment.setPresenter(new AvailableOfflineMapsPresenter(fragment));
        fragment.setMapStyleAssetPath(mapStyleAssetPath);
        return fragment;
    }

    @Override
    protected void downloadLocation(@NonNull Location location) {
        downloadDistrictMap(location.getId());
    }

    protected void downloadDistrictMap(String districtId) {
        getAppExecutors().diskIO().execute(() -> {
            List<Location> locationList = getAppStructureRepository().getStructuresByDistrictId(districtId);
            if (locationList == null || locationList.isEmpty()) {
                getAppExecutors().mainThread().execute(() -> displayToast(getString(R.string.location_has_no_structures)));
                // Revert download map status
                for (OfflineMapModel model : offlineMapModelList) {
                    if (model.getOfflineMapStatus() == OfflineMapModel.OfflineMapStatus.DOWNLOAD_STARTED) {
                        model.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.READY);
                        return;
                    }
                }
            } else {
                JSONObject featureCollection = new JSONObject();
                try {
                    featureCollection.put(TaskingConstants.GeoJSON.TYPE, TaskingConstants.GeoJSON.FEATURE_COLLECTION);
                    featureCollection.put(TaskingConstants.GeoJSON.FEATURES, new JSONArray(gson.toJson(locationList)));
                    getAppExecutors().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            displayToast(getString(R.string.download_starting));
                            downloadMap(FeatureCollection.fromJson(featureCollection.toString()), districtId);
                        }
                    });
                } catch (JSONException e) {
                    Timber.e(e);
                }
            }
        });
    }

    protected void downloadMap(FeatureCollection operationalAreaFeature, String mapName) {
        OfflineMapHelper.downloadMap(operationalAreaFeature, mapName, getContext());
    }

    public AppStructureRepository getAppStructureRepository() {
        if (appStructureRepository == null) {
            appStructureRepository = EusmApplication.getInstance().getStructureRepository();
        }
        return appStructureRepository;
    }

    public AppExecutors getAppExecutors() {
        if (appExecutors == null) {
            appExecutors = EusmApplication.getInstance().getAppExecutors();
        }
        return appExecutors;
    }

    @Override
    public void moveDownloadedOAToDownloadedList(String operationalAreaId) {
        List<OfflineMapModel> toRemoveFromAvailableList = new ArrayList<>();
        List<Location> toRemoveFromDownloadList = new ArrayList<>();
        for (OfflineMapModel offlineMapModel : offlineMapModelList) {
            if (offlineMapModel.getDownloadAreaId().equals(operationalAreaId)) {
                offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.DOWNLOADED);
                callback.onMapDownloaded(offlineMapModel);
                toRemoveFromAvailableList.add(offlineMapModel);
                toRemoveFromDownloadList.add(offlineMapModel.getLocation());
                setOfflineMapModelList(offlineMapModelList);
                break;
            }
        }
        operationalAreasToDownload.removeAll(toRemoveFromDownloadList);
    }

    @Override
    public void updateOperationalAreasToDownload(View view) {
        CheckBox checkBox = (CheckBox) view;
        OfflineMapModel offlineMapModel = (OfflineMapModel) view.getTag(R.id.offline_map_checkbox);

        if (checkBox.isChecked()) {
            offlineMapModel.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD);
            operationalAreasToDownload.clear();
            operationalAreasToDownload.add(offlineMapModel.getLocation());

            for (OfflineMapModel model : offlineMapModelList) {
                if (!model.getDownloadAreaId().equals(offlineMapModel.getDownloadAreaId())
                        && model.getOfflineMapStatus() == OfflineMapModel.OfflineMapStatus.SELECTED_FOR_DOWNLOAD) {
                    model.setOfflineMapStatus(OfflineMapModel.OfflineMapStatus.READY);
                }
            }
            setOfflineMapModelList(offlineMapModelList);
        } else {
            operationalAreasToDownload.remove(offlineMapModel.getLocation());
        }
    }

    @Override
    public void setOfflineMapModelList(List<OfflineMapModel> offlineMapModelList) {
        this.offlineMapModelList = offlineMapModelList;
        super.setOfflineMapModelList(offlineMapModelList);
    }

    @Override
    public void initiateMapDownload() {
        if (this.operationalAreasToDownload == null || this.operationalAreasToDownload.isEmpty()) {
            displayToast(getString(R.string.select_offline_map_to_download));
            return;
        }

        for (OfflineMapModel model : offlineMapModelList) {
            if (model.getOfflineMapStatus() == OfflineMapModel.OfflineMapStatus.DOWNLOAD_STARTED) {
                displayToast(getString(R.string.another_map_in_download));
                Timber.e("Error: A map download is already in progress");
                return;
            }
        }

        for (Location location : this.operationalAreasToDownload) {
            downloadLocation(location);
        }
    }

    @Override
    public void displayToast(String message) {
        if (displayBar == null) {
            displayBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG);
            displayBar.show();
        } else {
            displayBar.setText(message);
            displayBar.setDuration(Snackbar.LENGTH_LONG);
            if (!displayBar.isShown())
                displayBar.show();
        }
    }

    @Override
    public void displayError(int title, String message) {
        displayToast(message);
    }

    @Override
    protected void mapDeletedSuccessfully(String mapUniqueName) {
        super.mapDeletedSuccessfully(mapUniqueName);
        displayToast(getString(R.string.map_deleted, mapUniqueName));
    }
}
