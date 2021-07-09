package org.smartregister.eusm.util;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
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
                LocationProperty locationProperty = location.getProperties();
                if (structureDetail.getStructureType() != null) {
                    locationProperty.setType(structureDetail.getStructureType().toLowerCase().trim().replace(" ", ""));
                }
                Map<String, String> map = locationProperty.getCustomProperties();
                map.put(AppConstants.CardDetailKeys.TASK_STATUS, taskStatus);
                map.put(AppConstants.CardDetailKeys.TASK_STATUS_TYPE, StringUtils.isNumeric(taskStatus) ? AppConstants.TaskStatus.NOT_STARTED : taskStatus);
                map.put(STRUCTURE_NAME, structureDetail.getEntityName());
                map.put(AppConstants.CardDetailKeys.TYPE_TEXT, structureDetail.getStructureType());
                map.put(AppConstants.CardDetailKeys.COMMUNE, structureDetail.getCommune());
                map.put(AppConstants.CardDetailKeys.COMMUNE_ID, structureDetail.getParentId());
                map.put(AppConstants.CardDetailKeys.DISTANCE_META, structureDetail.getDistanceMeta());
                map.put(AppConstants.CardDetailKeys.STRUCTURE_ID, structureDetail.getStructureId());
                locations.add(location);
            }
        }
        return gson.toJson(locations);
    }

}
