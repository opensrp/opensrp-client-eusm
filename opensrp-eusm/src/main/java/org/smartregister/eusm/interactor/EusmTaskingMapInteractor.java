package org.smartregister.eusm.interactor;

import com.mapbox.geojson.Feature;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Location;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.eusm.util.GeoJsonUtils;
import org.smartregister.tasking.contract.TaskingMapActivityContract;
import org.smartregister.tasking.interactor.TaskingMapInteractor;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.tasking.util.TaskingConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        appExecutors.diskIO().execute(() -> {
            Set<Location> operationalAreaLocations = getOperationalAreaLocations(new HashSet<>(Arrays.asList(operationalArea.split(PreferencesUtil.OPERATIONAL_AREA_SEPARATOR))));

            JSONObject featureCollection = null;
            try {
                featureCollection = createFeatureCollection();
                if (!operationalAreaLocations.isEmpty()) {
                    List<StructureDetail> structureDetails = appStructureRepository
                            .fetchStructureDetails(null, operationalAreaLocations.stream().filter(location -> location.getId() != null).map(location -> location.getId()).collect(Collectors.toSet()), null, true, plan);

                    if (structureDetails != null && !structureDetails.isEmpty()) {
                        String features = geoJsonUtils.getGeoJsonFromStructureDetail(structureDetails);
                        featureCollection.put(TaskingConstants.GeoJSON.FEATURES, new JSONArray(features));
                    }
                }
            } catch (Exception e) {
                Timber.e(e);
            }
            JSONObject finalFeatureCollection = featureCollection;
            appExecutors.mainThread().execute(() -> {
                if (!operationalAreaLocations.isEmpty()) {
                    Feature operationalAreaFeature = Feature.fromJson(gson.toJson(operationalAreaLocations.stream().findFirst().orElse(null)));
                    if (locationComponentActive != null) {
                        getPresenter().onStructuresFetched(finalFeatureCollection, operationalAreaFeature, null, point, locationComponentActive);
                    } else {
                        getPresenter().onStructuresFetched(finalFeatureCollection, operationalAreaFeature, null);
                    }
                } else {
                    getPresenter().onStructuresFetched(finalFeatureCollection, null, null);
                }
            });
        });
    }

    protected Set<Location> getOperationalAreaLocations(Set<String> operationalAreas) {
        return AppUtils.getOperationalAreaLocations(operationalAreas);
    }
}
