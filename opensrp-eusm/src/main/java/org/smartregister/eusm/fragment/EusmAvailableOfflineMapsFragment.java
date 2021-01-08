package org.smartregister.eusm.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.mapbox.geojson.FeatureCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Location;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.tasking.fragment.AvailableOfflineMapsFragment;
import org.smartregister.tasking.presenter.AvailableOfflineMapsPresenter;
import org.smartregister.tasking.util.OfflineMapHelper;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.Utils;

import java.util.List;

import timber.log.Timber;

public class EusmAvailableOfflineMapsFragment extends AvailableOfflineMapsFragment {

    private AppStructureRepository appStructureRepository;

    private AppExecutors appExecutors;

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
        Utils.showToast(getContext(), getString(R.string.download_starting));
        getAppExecutors().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String name = location.getId();
                List<Location> locationList = getAppStructureRepository().getStructuresByDistrictId(name);
                JSONObject featureCollection = new JSONObject();
                try {
                    featureCollection.put(TaskingConstants.GeoJSON.TYPE, TaskingConstants.GeoJSON.FEATURE_COLLECTION);
                    featureCollection.put(TaskingConstants.GeoJSON.FEATURES, new JSONArray(gson.toJson(locationList)));
                    getAppExecutors().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            downloadMap(FeatureCollection.fromJson(featureCollection.toString()), name);
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
}
