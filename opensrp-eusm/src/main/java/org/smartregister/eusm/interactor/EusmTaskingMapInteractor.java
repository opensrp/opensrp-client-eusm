package org.smartregister.eusm.interactor;

import com.mapbox.geojson.Feature;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Location;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.eusm.util.GeoJsonUtils;
import org.smartregister.tasking.contract.TaskingMapActivityContract;
import org.smartregister.tasking.interactor.TaskingMapInteractor;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.tasking.util.Utils;

import java.util.List;

import timber.log.Timber;

public class EusmTaskingMapInteractor extends TaskingMapInteractor {

    private final AppStructureRepository appStructureRepository;
    private final GeoJsonUtils geoJsonUtils;

    public EusmTaskingMapInteractor(TaskingMapActivityContract.Presenter presenter) {
        super(presenter);
        geoJsonUtils = new GeoJsonUtils();
        appStructureRepository = EusmApplication.getInstance().getStructureRepository();
    }

    @Override
    public void fetchLocations(String plan, String operationalArea, String point, Boolean locationComponentActive) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Location operationalAreaLocation = getOperationalAreaLocation(operationalArea);
                JSONObject featureCollection = null;
                try {
                    featureCollection = createFeatureCollection();
                    if (operationalAreaLocation != null) {
                        List<StructureDetail> structureDetails = appStructureRepository
                                .fetchStructureDetails(null, operationalAreaLocation.getId(), null, true, plan);

                        if (structureDetails != null && !structureDetails.isEmpty()) {
                            String features = geoJsonUtils.getGeoJsonFromStructureDetail(structureDetails);
                            featureCollection.put(TaskingConstants.GeoJSON.FEATURES, new JSONArray(features));
                        }
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
                JSONObject finalFeatureCollection = featureCollection;
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (operationalAreaLocation != null) {
                            operationalAreaId = operationalAreaLocation.getId();
                            Feature operationalAreaFeature = Feature.fromJson(gson.toJson(operationalAreaLocation));
                            if (locationComponentActive != null) {
                                getPresenter().onStructuresFetched(finalFeatureCollection, operationalAreaFeature, null, point, locationComponentActive);
                            } else {
                                getPresenter().onStructuresFetched(finalFeatureCollection, operationalAreaFeature, null);
                            }
                        } else {
                            getPresenter().onStructuresFetched(finalFeatureCollection, null, null);
                        }
                    }
                });
            }
        });
    }

    protected Location getOperationalAreaLocation(String operationalArea) {
        return Utils.getOperationalAreaLocation(operationalArea);
    }
}
