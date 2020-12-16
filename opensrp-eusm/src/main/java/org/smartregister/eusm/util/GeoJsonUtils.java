package org.smartregister.eusm.util;

import org.smartregister.domain.Location;
import org.smartregister.eusm.domain.StructureDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.smartregister.tasking.interactor.BaseInteractor.gson;
import static org.smartregister.tasking.util.Constants.JsonForm.STRUCTURE_NAME;

/**
 * Created by samuelgithengi on 1/7/19.
 */
public class GeoJsonUtils extends org.smartregister.tasking.util.GeoJsonUtils {

    public String getGeoJsonFromStructureDetail(List<StructureDetail> structureDetails) {
        List<Location> locations = new ArrayList<>();
        for (StructureDetail structureDetail : structureDetails) {
            Location location = structureDetail.getGeojson();
            if (location != null) {
                String taskStatus = structureDetail.getTaskStatus();
                Map<String, String> map = location.getProperties().getCustomProperties();
                map.put(AppConstants.CardDetailKeys.TASK_STATUS, taskStatus);
                map.put(STRUCTURE_NAME, structureDetail.getStructureName());
                map.put(AppConstants.CardDetailKeys.COMMUNE, structureDetail.getCommune());
                map.put(AppConstants.CardDetailKeys.DISTANCE_META, structureDetail.getDistanceMeta());
                map.put(AppConstants.CardDetailKeys.STRUCTURE_ID, structureDetail.getStructureId());
                locations.add(location);
            }
        }
        return gson.toJson(locations);
    }

}
