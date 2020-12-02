package org.smartregister.eusm.util;

import org.smartregister.domain.Location;
import org.smartregister.domain.Task;
import org.smartregister.eusm.model.StructureDetail;

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
public class GeoJsonUtils {

    private static final String MDA_DISPENSE_TASK_COUNT = "mda_dispense_task_count";

    public static String getGeoJsonFromStructuresAndTasks(List<Location> structures, Map<String, Set<Task>> tasks,
                                                          String indexCase, Map<String, StructureDetail> structureNames) {
        for (Location structure : structures) {
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
        return gson.toJson(structures);
    }

    private static void populateBusinessStatus(HashMap<String, String> taskProperties, Map<String, Integer> mdaStatusMap, StateWrapper state) {
        // The assumption is that a register structure task always exists if the structure has
        // atleast one bednet distribution or blood screening task
//        if (AppUtils.isResidentialStructure(taskProperties.get(TASK_CODE))) {
//
//            boolean familyRegTaskMissingOrFamilyRegComplete = state.familyRegistered || !state.familyRegTaskExists;
//
//            if (AppUtils.isFocusInvestigation()) {
//
//                if (familyRegTaskMissingOrFamilyRegComplete &&
//                        state.bednetDistributed && state.bloodScreeningDone) {
//                    taskProperties.put(TASK_BUSINESS_STATUS, COMPLETE);
//                } else if (state.ineligibleForFamReg) {
//                    taskProperties.put(TASK_BUSINESS_STATUS, NOT_ELIGIBLE);
//                } else {
//                    taskProperties.put(TASK_BUSINESS_STATUS, NOT_VISITED);
//                }
//
//            }
//
//        }
    }

    private static class StateWrapper {
        private final boolean familyRegistered = false;
        private final boolean bednetDistributed = false;
        private final boolean bloodScreeningDone = false;
        private final boolean familyRegTaskExists = false;
        private final boolean caseConfirmed = false;
        private boolean fullyReceived;
        private boolean nonReceived;
        private boolean nonEligible;
        private boolean partiallyReceived;
        private final boolean bloodScreeningExists = false;
        private final boolean ineligibleForFamReg = false;
    }
}
