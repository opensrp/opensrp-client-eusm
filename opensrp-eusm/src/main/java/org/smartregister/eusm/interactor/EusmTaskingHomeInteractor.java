package org.smartregister.eusm.interactor;

import com.mapbox.geojson.Feature;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Location;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.eusm.util.GeoJsonUtils;
import org.smartregister.repository.LocationRepository;
import org.smartregister.tasking.contract.TaskingHomeActivityContract;
import org.smartregister.tasking.interactor.TaskingHomeInteractor;
import org.smartregister.tasking.model.TaskDetails;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.tasking.util.Utils;

import java.util.List;

import timber.log.Timber;

public class EusmTaskingHomeInteractor extends TaskingHomeInteractor {

    private LocationRepository locationRepository;

    private final PreferencesUtil preferencesUtil;

    private final AppStructureRepository appStructureRepository;

    private final GeoJsonUtils geoJsonUtils;

    public EusmTaskingHomeInteractor(TaskingHomeActivityContract.Presenter presenter) {
        super(presenter);
        geoJsonUtils = new GeoJsonUtils();
        appStructureRepository = EusmApplication.getInstance().getStructureRepository();
        preferencesUtil = PreferencesUtil.getInstance();
    }


    @Override
    public void fetchLocations(String plan, String operationalArea, String point, Boolean locationComponentActive) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Location operationalAreaLocation = Utils.getOperationalAreaLocation(operationalArea);

                List<StructureDetail> structureDetails = appStructureRepository.fetchStructureDetails(null, operationalAreaLocation.getId(), null, true);
                JSONObject featureCollection = null;
                try {
                    if (structureDetails != null && !structureDetails.isEmpty()) {
                        featureCollection = createFeatureCollection();
                        String features = geoJsonUtils.getGeoJsonFromStructureDetail(structureDetails);
                        featureCollection.put(TaskingConstants.GeoJSON.FEATURES, new JSONArray(features));
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
                JSONObject finalFeatureCollection = featureCollection;
                List<TaskDetails> finalTaskDetailsList = null;
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (operationalAreaLocation != null) {
                            operationalAreaId = operationalAreaLocation.getId();
                            Feature operationalAreaFeature = Feature.fromJson(gson.toJson(operationalAreaLocation));
                            if (locationComponentActive != null) {
                                getPresenter().onStructuresFetched(finalFeatureCollection, operationalAreaFeature, finalTaskDetailsList, point, locationComponentActive);
                            } else {
                                getPresenter().onStructuresFetched(finalFeatureCollection, operationalAreaFeature, finalTaskDetailsList);
                            }
                        } else {
                            getPresenter().onStructuresFetched(finalFeatureCollection, null, null);
                        }
                    }
                });
            }
        });
    }
}
