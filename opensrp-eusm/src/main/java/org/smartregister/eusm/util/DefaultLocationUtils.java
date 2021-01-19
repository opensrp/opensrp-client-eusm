package org.smartregister.eusm.util;

import org.smartregister.eusm.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultLocationUtils {

    public static List<String> getFacilityLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.FACILITY_LEVELS));
    }

    public static List<String> getLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
    }
}
