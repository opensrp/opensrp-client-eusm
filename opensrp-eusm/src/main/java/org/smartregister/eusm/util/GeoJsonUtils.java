package org.smartregister.eusm.util;

import org.smartregister.domain.Location;
import org.smartregister.domain.Task;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.tasking.model.StructureDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.smartregister.tasking.interactor.BaseInteractor.gson;
import static org.smartregister.tasking.util.Constants.JsonForm.STRUCTURE_NAME;
import static org.smartregister.tasking.util.TaskingConstants.BusinessStatus.NOT_ELIGIBLE;
import static org.smartregister.tasking.util.TaskingConstants.GeoJSON.IS_INDEX_CASE;
import static org.smartregister.tasking.util.TaskingConstants.Properties.FEATURE_SELECT_TASK_BUSINESS_STATUS;
import static org.smartregister.tasking.util.TaskingConstants.Properties.LOCATION_TYPE;
import static org.smartregister.tasking.util.TaskingConstants.Properties.LOCATION_UUID;
import static org.smartregister.tasking.util.TaskingConstants.Properties.LOCATION_VERSION;
import static org.smartregister.tasking.util.TaskingConstants.Properties.TASK_BUSINESS_STATUS;
import static org.smartregister.tasking.util.TaskingConstants.Properties.TASK_CODE;
import static org.smartregister.tasking.util.TaskingConstants.Properties.TASK_CODE_LIST;
import static org.smartregister.tasking.util.TaskingConstants.Properties.TASK_IDENTIFIER;
import static org.smartregister.tasking.util.TaskingConstants.Properties.TASK_STATUS;

/**
 * Created by samuelgithengi on 1/7/19.
 */
public class GeoJsonUtils extends org.smartregister.tasking.util.GeoJsonUtils {

    private static final String MDA_DISPENSE_TASK_COUNT = "mda_dispense_task_count";

    @Override
    public String getGeoJsonFromStructuresAndTasks(List<Location> structures, Map<String, Set<Task>> tasks,
                                                   String indexCase, Map<String, StructureDetails> structureNames) {
        List<Location> stLocations = new ArrayList<>();
        for (Location structure : structures) {
            if (structure.getGeometry() != null) {
                stLocations.add(structure);
            }
            Set<Task> taskSet = tasks.get(structure.getId());
            HashMap<String, String> taskProperties = new HashMap<>();

            StringBuilder interventionList = new StringBuilder();

            Map<String, Integer> mdaStatusMap = new HashMap<>();
            mdaStatusMap.put(NOT_ELIGIBLE, 0);
            mdaStatusMap.put(MDA_DISPENSE_TASK_COUNT, 0);
            StateWrapper state = new StateWrapper();
            if (taskSet == null)
                continue;
            for (Task task : taskSet) {
                taskProperties = new HashMap<>();
                taskProperties.put(TASK_IDENTIFIER, task.getIdentifier());

                taskProperties.put(TASK_BUSINESS_STATUS, task.getBusinessStatus());

                taskProperties.put(FEATURE_SELECT_TASK_BUSINESS_STATUS, task.getBusinessStatus()); // used to determine action to take when a feature is selected
                taskProperties.put(TASK_STATUS, task.getStatus().name());
                taskProperties.put(TASK_CODE, task.getCode());

                if (indexCase != null && structure.getId().equals(indexCase)) {
                    taskProperties.put(IS_INDEX_CASE, Boolean.TRUE.toString());
                } else {
                    taskProperties.put(IS_INDEX_CASE, Boolean.FALSE.toString());
                }

                taskProperties.put(LOCATION_UUID, structure.getProperties().getUid());
                taskProperties.put(LOCATION_VERSION, structure.getProperties().getVersion() + "");
                taskProperties.put(LOCATION_TYPE, structure.getProperties().getType());
                interventionList.append(task.getCode());
                interventionList.append("~");

            }

            populateBusinessStatus(taskProperties, mdaStatusMap, state);

            taskProperties.put(TASK_CODE_LIST, interventionList.toString());
            if (structureNames.get(structure.getId()) != null) {
                taskProperties.put(STRUCTURE_NAME, structureNames.get(structure.getId()).getStructureName());
            }
            structure.getProperties().setCustomProperties(taskProperties);

        }
        return gson.toJson(stLocations);
    }


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
