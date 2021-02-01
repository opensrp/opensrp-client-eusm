package org.smartregister.eusm.util;

import org.smartregister.tasking.util.TaskingConstants;

public interface AppConstants extends TaskingConstants {

    int STRUCTURE_REGISTER_PAGE_SIZE = 10;

    Integer NEARBY_DISTANCE_IN_METRES = 500;

    String LOCATION_ID = "location_id";

    String STRUCTURE_IDS = "STRUCTURE_IDS";

    interface PreferenceKey {
        String LOADED_DISTRICTS_FROM_HIERARCHY = "LOADED_DISTRICTS_FROM_HIERARCHY";
        String COMMUNE_ID = "COMMUNE_ID";
    }

    interface Column {
        interface Structure {
            String TYPE = "type";
        }

        interface Task {
            String FOR = "for";
            String NAME = "name";
            String BUSINESS_STATUS = "business_status";
            String STATUS = "status";
            String ID = "_ID";
            String LOCATION = "location";
            String STRUCTURE_ID = "structure_id";
            String FOCUS = "focus";
            String CODE = "code";
            String PLAN_ID = "plan_id";
            String GROUP_ID = "group_id";
        }
    }

    interface EventEntityType {
        String PRODUCT = "product";
        String SERVICE_POINT = "service_point";
    }

    interface JsonForm {

        String FLAG_PROBLEM_FORM = "flag_problem";

        String FIX_PROBLEM_FORM = "fix_problem";

        String SERVICE_POINT_CHECK_FORM = "service_point_check";

        String RECORD_GPS_FORM = "record_gps";

        String LOOKS_GOOD = "looks_good";
    }

    interface EventDetailKey {
        String PRODUCT_NAME = "productName";
        String LOCATION_NAME = "locationName";
        String PLAN_IDENTIFIER = "planIdentifier";
        String PRODUCT_ID = "productId";
        String MISSION = "mission";
        String LOCATION_ID = "locationId";
    }

    interface EncounterType {
        String FLAG_PROBLEM = "flag_problem";
        String FIX_PROBLEM = "fix_problem";
        String SERVICE_POINT_CHECK = "service_point_check";
        String RECORD_GPS = "record_gps";
        String LOOKS_GOOD = "looks_good";
    }

    interface ServicePointType {
        String EPP = "epp";
        String CEG = "ceg";
        String CHRD1 = "chrd1";
        String CHRD2 = "chrd2";
        String CHRR = "chrr";
        String SDSP = "sdsp";
        String DRSP = "drsp";
        String MSP = "msp";
        String CSB1 = "csb1";
        String CSB2 = "csb2";
        String BSD = "bsd";
        String WAREHOUSE = "warehouse";
        String WATERPOINT = "waterpoint";
        String PRESCO = "presco";
        String MEAH = "meah";
        String DREAH = "dreah";
        String MEN = "men";
        String DREN = "dren";
        String MPPSPF = "mppspf";
        String DRPPSPF = "drpppspf";
        String NGO_PARTNER = "ngo_partner";
        String SITE_COMMUNAUTAIRE = "site_communautaire";
        String DRJS = "drjs";
        String INSTAT = "instat";
    }

    interface IntentData {
        String STRUCTURE_DETAIL = "structure_detail";
        String TASK_DETAIL = "structure_task_detail";
    }

    interface NonProductTasks {
        String SERVICE_POINT_CHECK = "Service Point Check";
        String RECORD_GPS = "Record Gps";
    }

    interface TaskCode {
        String RECORD_GPS = "Record GPS";

        String SERVICE_POINT_CHECK = "Service Point Check";
    }

    interface TaskStatus {
        String COMPLETED = "completed";
        String IN_PROGRESS = "in_progress";
        String NOT_FINISHED = "not_finished";
        String OTHER = "other";
    }

    interface BusinessStatus {
        String HAS_PROBLEM = "has_problem";
        String NOT_VISITED = "Not Visited";
    }

    interface JsonFormKey {
        String PRODUCT_PICTURE = "product_picture";
    }

    interface CardDetailKeys {
        String COMMUNE = "commune";
        String STRUCTURE_ID = "structureId";
        String DISTANCE_META = "distanceMeta";
        String TASK_STATUS = "taskStatus";
        String STATUS = "status";
        String NAME = "name";
        String TYPE = "type";
        String COMMUNE_ID = "communeId";
        String TYPE_TEXT = "typeText";
    }

    interface LocationLevels {
        String DISTRICT = "district";
        String COMMUNE = "commune";
        String DISTRICT_TAG = "District";
    }
}
