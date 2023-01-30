package org.smartregister.eusm.helper;

import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.sync.helper.ValidateAssignmentHelper;
import org.smartregister.util.SyncUtils;

import java.util.Set;

public class AppValidateAssignmentHelper extends ValidateAssignmentHelper {

    public AppValidateAssignmentHelper(SyncUtils syncUtils) {
        super(syncUtils);
    }

    /**
     * Filter out previously saved jurisdictions
     * from preferences
     *
     * @return
     */
    @Override
    protected Set<String> getExistingJurisdictions() {
        Set<String> regionIds = AppUtils.getLocationLevelFromLocationHierarchy(AppConstants.LocationLevels.REGION_TAG);
        Set<String> districtIds = AppUtils.getLocationLevelFromLocationHierarchy(AppConstants.LocationLevels.DISTRICT_TAG);
        Set<String> jurisdictionIds = EusmApplication.getInstance().context().userService().fetchJurisdictionIds();

        // Remove district and region ids from assigned communes
        jurisdictionIds.removeAll(districtIds);
        jurisdictionIds.removeAll(regionIds);

        return jurisdictionIds;
    }
}
