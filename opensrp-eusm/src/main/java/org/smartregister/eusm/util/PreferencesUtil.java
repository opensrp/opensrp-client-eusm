package org.smartregister.eusm.util;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.account.AccountHelper;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.repository.AllSharedPreferences;

import static org.smartregister.eusm.util.AppConstants.Preferences.CURRENT_DISTRICT;
import static org.smartregister.eusm.util.AppConstants.Preferences.CURRENT_FACILITY;
import static org.smartregister.eusm.util.AppConstants.Preferences.CURRENT_OPERATIONAL_AREA;
import static org.smartregister.eusm.util.AppConstants.Preferences.CURRENT_OPERATIONAL_AREA_ID;
import static org.smartregister.eusm.util.AppConstants.Preferences.CURRENT_PLAN;
import static org.smartregister.eusm.util.AppConstants.Preferences.CURRENT_PLAN_ID;
import static org.smartregister.eusm.util.AppConstants.Preferences.CURRENT_REGION;
import static org.smartregister.eusm.util.AppConstants.Preferences.FACILITY_LEVEL;

/**
 * Created by samuelgithengi on 11/29/18.
 */
public class PreferencesUtil {

    private static PreferencesUtil instance;
    private final AllSharedPreferences allSharedPreferences;

    private PreferencesUtil(AllSharedPreferences allSharedPreferences) {
        this.allSharedPreferences = allSharedPreferences;
    }


    public static PreferencesUtil getInstance() {
        if (instance == null) {
            instance = new PreferencesUtil(EusmApplication.getInstance().getContext().allSharedPreferences());
        }
        return instance;
    }

    public String getCurrentFacility() {
        return allSharedPreferences.getPreference(CURRENT_FACILITY);
    }

    public void setCurrentFacility(String facility) {
        allSharedPreferences.savePreference(CURRENT_FACILITY, facility);
    }

    public String getCurrentOperationalArea() {
        return allSharedPreferences.getPreference(CURRENT_OPERATIONAL_AREA);
    }

    public void setCurrentOperationalArea(String operationalArea) {
        allSharedPreferences.savePreference(CURRENT_OPERATIONAL_AREA, operationalArea);
        if (StringUtils.isNotBlank(operationalArea)) {
            allSharedPreferences.savePreference(CURRENT_OPERATIONAL_AREA_ID, AppUtils.getCurrentLocationId());
        } else {
            allSharedPreferences.savePreference(CURRENT_OPERATIONAL_AREA_ID, null);
        }
    }

    public String getCurrentOperationalAreaId() {
        return allSharedPreferences.getPreference(CURRENT_OPERATIONAL_AREA_ID);
    }

    public String getCurrentDistrict() {
        return allSharedPreferences.getPreference(CURRENT_DISTRICT);
    }

    public void setCurrentDistrict(String district) {
        allSharedPreferences.savePreference(CURRENT_DISTRICT, district);
    }

    public String getCurrentRegion() {
        return allSharedPreferences.getPreference(CURRENT_REGION);
    }

    public void setCurrentRegion(String currentRegion) {
        allSharedPreferences.savePreference(CURRENT_REGION, currentRegion);
    }

    public String getCurrentPlan() {
        return allSharedPreferences.getPreference(CURRENT_PLAN);
    }

    public void setCurrentPlan(String campaign) {
        allSharedPreferences.savePreference(CURRENT_PLAN, campaign);
    }

    public String getCurrentPlanId() {
        return allSharedPreferences.getPreference(CURRENT_PLAN_ID);
    }

    public void setCurrentPlanId(String campaignId) {
        allSharedPreferences.savePreference(CURRENT_PLAN_ID, campaignId);
    }

    public String getPreferenceValue(String key) {
        return allSharedPreferences.getPreference(key);
    }

    public String getCurrentFacilityLevel() {
        return allSharedPreferences.getPreference(FACILITY_LEVEL);
    }

    public void setCurrentFacilityLevel(String facilityLevel) {
        allSharedPreferences.savePreference(FACILITY_LEVEL, facilityLevel);
    }

    public void setInterventionTypeForPlan(String planId, String interventionType) {
        allSharedPreferences.savePreference(planId, interventionType);
    }

    public String getInterventionTypeForPlan(String planId) {
        return allSharedPreferences.getPreference(planId);
    }

    public boolean isKeycloakConfigured() {
        return allSharedPreferences.getPreferences().getBoolean(AccountHelper.CONFIGURATION_CONSTANTS.IS_KEYCLOAK_CONFIGURED, false);
    }

}
