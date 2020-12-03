package org.smartregister.eusm.util;

import org.smartregister.tasking.util.TaskingConstants;

public interface AppConstants extends TaskingConstants {

    int STRUCTURE_REGISTER_PAGE_SIZE = 10;

    Integer NEARBY_DISTANCE_IN_METRES = 382992;

    String PLAN_IDENTIFIER = "335ef7a3-7f35-58aa-8263-4419464946d8";

    String PLAN_NAME = "SS";

    String LOCATION_ID = "location_id";

    interface Table {
        String STRUCTURE_TABLE = "structure";
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
            String STRUCTURE_ID = "business_status";
            String FOCUS = "focus";
            String CODE = "code";
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
        String STOCK_ID = "stockId";
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

    interface AppProperties {
        String CHOOSE_OPERATIONAL_AREA_FIRST = "CHOOSE_OPERATIONAL_AREA_FIRST";
    }

    interface TaskStatus {
        String COMPLETED = "completed";
        String IN_PROGRESS = "in_progress";
        String NOT_FINISHED = "not_finished";
        String OTHER = "other";
    }

    interface BusinessStatus {
        String HAS_PROBLEM = "has_problem";
    }

    interface JsonFormKey {
        String PRODUCT_PICTURE = "product_picture";
    }
}
