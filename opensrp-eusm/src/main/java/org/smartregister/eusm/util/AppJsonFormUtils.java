package org.smartregister.eusm.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.core.util.Pair;

import com.mapbox.geojson.Feature;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.domain.Event;
import org.smartregister.domain.Location;
import org.smartregister.domain.Obs;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.activity.AppJsonFormActivity;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.model.BaseTaskDetails;
import org.smartregister.eusm.model.TaskDetails;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.JsonFormUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

import static com.vijay.jsonwizard.constants.JsonFormConstants.CHECK_BOX;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEYS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.TYPE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUES;
import static org.smartregister.AllConstants.JSON_FILE_EXTENSION;
import static org.smartregister.AllConstants.OPTIONS;
import static org.smartregister.AllConstants.TEXT;
import static org.smartregister.eusm.util.AppUtils.getPropertyValue;


/**
 * Created by samuelgithengi on 3/22/19.
 */
public class AppJsonFormUtils {

    private final Set<String> nonEditableFields;

    private final LocationHelper locationHelper = LocationHelper.getInstance();

    public AppJsonFormUtils() {
        nonEditableFields = new HashSet<>(Arrays.asList(AppConstants.JsonForm.HOUSEHOLD_ACCESSIBLE,
                AppConstants.JsonForm.ABLE_TO_SPRAY_FIRST, AppConstants.JsonForm.MOP_UP_VISIT));
    }

    public static org.smartregister.clientandeventmodel.Event createTaskEvent(String baseEntityId, String locationId, Map<String, String> details, String eventType, String entityType) {
        org.smartregister.clientandeventmodel.Event taskEvent = (org.smartregister.clientandeventmodel.Event) new org.smartregister.clientandeventmodel.Event().withBaseEntityId(baseEntityId).withEventDate(new Date()).withEventType(eventType)
                .withLocationId(locationId).withEntityType(entityType).withFormSubmissionId(UUID.randomUUID().toString()).withDateCreated(new Date());
        return taskEvent;
    }

    public JSONObject getFormJSON(Context context, String formName, Feature feature, String sprayStatus, String familyHead) {

        String taskBusinessStatus = getPropertyValue(feature, AppConstants.Properties.TASK_BUSINESS_STATUS);
        String taskIdentifier = getPropertyValue(feature, AppConstants.Properties.TASK_IDENTIFIER);
        String taskStatus = getPropertyValue(feature, AppConstants.Properties.TASK_STATUS);

        String structureId = feature.id();
        String structureUUID = getPropertyValue(feature, AppConstants.Properties.LOCATION_UUID);
        String structureVersion = getPropertyValue(feature, AppConstants.Properties.LOCATION_VERSION);
        String structureType = getPropertyValue(feature, AppConstants.Properties.LOCATION_TYPE);

        String formString = getFormString(context, formName, structureType);
        try {
            JSONObject formJson = populateFormDetails(formString, structureId, structureId, taskIdentifier,
                    taskBusinessStatus, taskStatus, structureUUID,
                    structureVersion == null ? null : Integer.valueOf(structureVersion));

            populateFormFields(formJson, structureType, sprayStatus, familyHead);
            return formJson;
        } catch (Exception e) {
            Timber.e(e, "error launching form%s", formName);
        }
        return null;
    }

    public JSONObject getFormJSON(Context context, String formName, BaseTaskDetails task, Location structure) {

        String taskBusinessStatus = "";
        String taskIdentifier = "";
        String taskStatus = "";
        String entityId = "";
        if (task != null) {
            taskBusinessStatus = task.getBusinessStatus();
            taskIdentifier = task.getTaskId();
            taskStatus = task.getTaskStatus();

            entityId = task.getTaskEntity();
        }

        String structureId = "";
        String structureUUID = "";
        int structureVersion = 0;
        String structureType = "";
        if (structure != null) {
            structureId = structure.getId();
            structureUUID = structure.getProperties().getUid();
            structureVersion = structure.getProperties().getVersion();
            structureType = structure.getProperties().getType();
        }

        String sprayStatus = null;
        String familyHead = null;

        if (task instanceof TaskDetails) {
            sprayStatus = ((TaskDetails) task).getSprayStatus();
            familyHead = ((TaskDetails) task).getFamilyName();
        }

        String formString = getFormString(context, formName, structureType);
        try {
            JSONObject formJson = populateFormDetails(formString, entityId, structureId, taskIdentifier,
                    taskBusinessStatus, taskStatus, structureUUID, structureVersion);
            populateFormFields(formJson, structureType, sprayStatus, familyHead);
            return formJson;
        } catch (JSONException e) {
            Timber.e(e, "error launching form%s", formName);
        }
        return null;
    }

    public String getFormString(Context context, String formName, String structureType) {
        String formString = null;
        try {
            FormUtils formUtils = new FormUtils();
            String formattedFormName = formName.replace(AppConstants.JsonForm.JSON_FORM_FOLDER, "").replace(JSON_FILE_EXTENSION, "");
            JSONObject formStringObj = formUtils.getFormJsonFromRepositoryOrAssets(context, formattedFormName);
            if (formStringObj == null) {
                return null;
            }
            formString = formStringObj.toString();
            if ((AppConstants.JsonForm.SPRAY_FORM.equals(formName) || AppConstants.JsonForm.SPRAY_FORM_BOTSWANA.equals(formName)
                    || AppConstants.JsonForm.SPRAY_FORM_NAMIBIA.equals(formName))) {
                String structType = structureType;
                if (StringUtils.isBlank(structureType)) {
                    structType = AppConstants.StructureType.NON_RESIDENTIAL;
                }
                formString = formString.replace(AppConstants.JsonForm.STRUCTURE_PROPERTIES_TYPE, structType);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return formString;
    }

    public JSONObject populateFormDetails(String formString, String entityId, String structureId, String taskIdentifier,
                                          String taskBusinessStatus, String taskStatus, String structureUUID,
                                          Integer structureVersion) throws JSONException {

        JSONObject formJson = new JSONObject(formString);
        formJson.put(AppConstants.ENTITY_ID, entityId);
        JSONObject formData = new JSONObject();
        formData.put(AppConstants.Properties.TASK_IDENTIFIER, taskIdentifier);
        formData.put(AppConstants.Properties.TASK_BUSINESS_STATUS, taskBusinessStatus);
        formData.put(AppConstants.Properties.TASK_STATUS, taskStatus);
        formData.put(AppConstants.Properties.LOCATION_ID, structureId);
        formData.put(AppConstants.Properties.LOCATION_UUID, structureUUID);
        formData.put(AppConstants.Properties.LOCATION_VERSION, structureVersion);
        formData.put(AppConstants.Properties.APP_VERSION_NAME, BuildConfig.VERSION_NAME);
        formData.put(AppConstants.Properties.FORM_VERSION, formJson.optString("form_version"));
        String planIdentifier = PreferencesUtil.getInstance().getCurrentPlanId();
        formData.put(AppConstants.Properties.PLAN_IDENTIFIER, planIdentifier);
        formJson.put(AppConstants.DETAILS, formData);
        return formJson;
    }

    private void populateFormFields(JSONObject formJson, String structureType, String sprayStatus, String familyHead) throws JSONException {

        JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJson);
        if (StringUtils.isNotBlank(structureType) || StringUtils.isNotBlank(sprayStatus) || StringUtils.isNotBlank(familyHead)) {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                String key = field.getString(KEY);
                if (key.equalsIgnoreCase(AppConstants.JsonForm.STRUCTURE_TYPE))
                    field.put(org.smartregister.util.JsonFormUtils.VALUE, structureType);
                else if (key.equalsIgnoreCase(AppConstants.JsonForm.SPRAY_STATUS))
                    field.put(org.smartregister.util.JsonFormUtils.VALUE, sprayStatus);
                else if (key.equalsIgnoreCase(AppConstants.JsonForm.HEAD_OF_HOUSEHOLD))
                    field.put(org.smartregister.util.JsonFormUtils.VALUE, familyHead);
            }
        }

    }

    public void startJsonForm(JSONObject form, Activity context) {
        startJsonForm(form, context, AppConstants.RequestCode.REQUEST_CODE_GET_JSON);
    }

    public void startJsonForm(JSONObject form, Activity context, int requestCode) {
        Intent intent = new Intent(context, AppJsonFormActivity.class);
        try {
            intent.putExtra(AppConstants.JSON_FORM_PARAM_JSON, form.toString());
            context.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public String getFormName(String encounterType, String taskCode) {
        String formName = null;
        return formName;
    }

//    public void populatePAOTForm(MosquitoHarvestCardDetails cardDetails, JSONObject formJson) {
//        if (formJson == null)
//            return;
//        try {
//            populateField(formJson, Constants.JsonForm.PAOT_STATUS, cardDetails.getStatus(), VALUE);
//            populateField(formJson, Constants.JsonForm.PAOT_COMMENTS, cardDetails.getComments(), VALUE);
//            populateField(formJson, Constants.JsonForm.LAST_UPDATED_DATE, cardDetails.getStartDate(), VALUE);
//        } catch (JSONException e) {
//            Timber.e(e);
//        }
//    }

    public String getFormName(String encounterType) {
        return getFormName(encounterType, null);
    }

    public void populateField(JSONObject formJson, String key, String value, String fieldToPopulate) throws JSONException {
        JSONObject field = JsonFormUtils.getFieldJSONObject(JsonFormUtils.getMultiStepFormFields(formJson), key);
        if (field != null) {
            field.put(fieldToPopulate, value);
        }
    }

    public void populateSprayForm(CommonPersonObject commonPersonObject, JSONObject formJson) {
        if (commonPersonObject == null || commonPersonObject.getDetails() == null)
            return;
        JSONArray fields = JsonFormUtils.fields(formJson);
        for (int i = 0; i < fields.length(); i++) {
            try {
                JSONObject field = fields.getJSONObject(i);
                String key = field.getString(KEY);
                if (commonPersonObject.getDetails().containsKey(key)) {
                    String value = commonPersonObject.getDetails().get(key);
                    if (StringUtils.isNotBlank(value))
                        field.put(VALUE, value);
                    if (nonEditableFields.contains(key) && "Yes".equalsIgnoreCase(value)) {
                        field.put(JsonFormConstants.READ_ONLY, true);
                        field.remove(JsonFormConstants.RELEVANCE);
                    }
                }
            } catch (JSONException e) {
                Timber.e(e);
            }

        }
    }

    public void populateForm(Event event, JSONObject formJSON) {
        if (event == null)
            return;
        JSONArray fields = JsonFormUtils.fields(formJSON);
        for (int i = 0; i < fields.length(); i++) {
            try {
                JSONObject field = fields.getJSONObject(i);
                String key = field.getString(KEY);
                Obs obs = event.findObs(null, false, key);
                if (obs != null && obs.getValues() != null) {
                    if (CHECK_BOX.equals(field.getString(TYPE))) {
                        JSONArray options = field.getJSONArray(OPTIONS);
                        Map<String, String> optionsKeyValue = new HashMap<>();
                        for (int j = 0; j < options.length(); j++) {
                            JSONObject option = options.getJSONObject(j);
                            optionsKeyValue.put(option.getString(TEXT), option.getString(KEY));
                        }
                        JSONArray keys = new JSONArray();
                        for (Object value : obs.getValues()) {
                            keys.put(optionsKeyValue.get(value.toString()));
                        }
                        field.put(VALUE, keys);
                    } else {
                        if (!JsonFormConstants.REPEATING_GROUP.equals(field.optString(TYPE))) {
                            field.put(VALUE, obs.getValue());
                        }
                    }
                }

                if (JsonFormConstants.REPEATING_GROUP.equals(field.optString(TYPE))) {
                    generateRepeatingGroupFields(field, event.getObs(), formJSON);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    private void generateRepeatingGroupFields(JSONObject field, List<Obs> obs, JSONObject formJSON) {
        try {
            LinkedHashMap<String, HashMap<String, String>> repeatingGroupMap = AppUtils.buildRepeatingGroup(field, obs);
            List<HashMap<String, String>> repeatingGroupMapList = AppUtils.generateListMapOfRepeatingGrp(repeatingGroupMap);
            new RepeatingGroupGenerator(formJSON.optJSONObject(JsonFormConstants.STEP1),
                    JsonFormConstants.STEP1,
                    field.optString(KEY),
                    new HashMap<>(),
                    AppConstants.JsonForm.REPEATING_GROUP_UNIQUE_ID,
                    repeatingGroupMapList).init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Pair<JSONArray, JSONArray> populateServerOptions(Map<String, Object> serverConfigs, String settingsConfigKey, JSONObject field, String filterKey) {
        if (serverConfigs == null || field == null)
            return null;
        JSONArray serverConfig = (JSONArray) serverConfigs.get(settingsConfigKey);
        if (serverConfig != null && !serverConfig.isNull(0)) {
            JSONArray options = serverConfig.optJSONObject(0).optJSONArray(filterKey);
            if (options == null)
                return null;
            JSONArray codes = new JSONArray();
            JSONArray values = new JSONArray();
            for (int i = 0; i < options.length(); i++) {
                JSONObject operator = options.optJSONObject(i);
                if (operator == null)
                    continue;
                String code = operator.optString(AppConstants.CONFIGURATION.CODE, null);
                String name = operator.optString(AppConstants.CONFIGURATION.NAME);
                if (StringUtils.isBlank(code) || code.equalsIgnoreCase(name)) {
                    codes.put(name);
                    values.put(name);
                } else {
                    codes.put(code + ":" + name);
                    values.put(code + " - " + name);
                }
            }
            try {
                field.put(KEYS, codes);
                field.put(VALUES, values);
            } catch (JSONException e) {
                Timber.e(e, "Error populating %s Operators ", filterKey);
            }
            return new Pair<>(codes, values);
        }
        return null;
    }

    public Map<String, JSONObject> getFields(JSONObject formJSON) {
        JSONArray fields = JsonFormUtils.fields(formJSON);
        Map<String, JSONObject> fieldsMap = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.optJSONObject(i);
            fieldsMap.put(field.optString(JsonFormUtils.KEY), field);
        }
        return fieldsMap;
    }

    public void populateFormWithServerOptions(String formName, JSONObject formJSON) {

        Map<String, JSONObject> fieldsMap = getFields(formJSON);
        switch (formName) {

            case AppConstants.JsonForm.IRS_SA_DECISION_ZAMBIA:
            case AppConstants.JsonForm.CB_SPRAY_AREA_ZAMBIA:
            case AppConstants.JsonForm.MOBILIZATION_FORM_ZAMBIA:
                populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                        AppConstants.CONFIGURATION.SUPERVISORS, fieldsMap.get(AppConstants.JsonForm.SUPERVISOR),
                        PreferencesUtil.getInstance().getCurrentDistrict());
                break;

            case AppConstants.JsonForm.IRS_FIELD_OFFICER_ZAMBIA:
                populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                        AppConstants.CONFIGURATION.FIELD_OFFICERS, fieldsMap.get(AppConstants.JsonForm.FIELD_OFFICER),
                        PreferencesUtil.getInstance().getCurrentDistrict());
                populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                        AppConstants.CONFIGURATION.HEALTH_FACILITIES, fieldsMap.get(AppConstants.JsonForm.HEALTH_FACILITY),
                        PreferencesUtil.getInstance().getCurrentDistrict());
                break;

            case AppConstants.JsonForm.DAILY_SUMMARY_ZAMBIA:
                populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                        AppConstants.CONFIGURATION.TEAM_LEADERS, fieldsMap.get(AppConstants.JsonForm.TEAM_LEADER),
                        PreferencesUtil.getInstance().getCurrentDistrict());
                String dataCollector = EusmApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
                if (StringUtils.isNotBlank(dataCollector)) {
                    populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                            AppConstants.CONFIGURATION.SPRAY_OPERATORS, fieldsMap.get(AppConstants.JsonForm.SPRAY_OPERATOR_CODE),
                            dataCollector);
                }

                populateUserAssignedLocations(formJSON, AppConstants.JsonForm.ZONE, Arrays.asList(AppConstants.Tags.OPERATIONAL_AREA, AppConstants.Tags.ZONE));
                break;

            case AppConstants.JsonForm.TEAM_LEADER_DOS_ZAMBIA:

                populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                        AppConstants.CONFIGURATION.DATA_COLLECTORS, fieldsMap.get(AppConstants.JsonForm.DATA_COLLECTOR),
                        PreferencesUtil.getInstance().getCurrentDistrict());

                dataCollector = JsonFormUtils.getString(fieldsMap.get(AppConstants.JsonForm.DATA_COLLECTOR), VALUE);
                if (StringUtils.isNotBlank(dataCollector)) {
                    populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                            AppConstants.CONFIGURATION.SPRAY_OPERATORS, fieldsMap.get(AppConstants.JsonForm.SPRAY_OPERATOR_CODE),
                            dataCollector.split(":")[0]);
                }

                populateUserAssignedLocations(formJSON, AppConstants.JsonForm.ZONE, Arrays.asList(AppConstants.Tags.OPERATIONAL_AREA, AppConstants.Tags.ZONE));

                break;

            case AppConstants.JsonForm.VERIFICATION_FORM_ZAMBIA:
                populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                        AppConstants.CONFIGURATION.FIELD_OFFICERS, fieldsMap.get(AppConstants.JsonForm.FIELD_OFFICER),
                        PreferencesUtil.getInstance().getCurrentDistrict());

            case AppConstants.JsonForm.SPRAY_FORM_ZAMBIA:
                populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                        AppConstants.CONFIGURATION.DATA_COLLECTORS, fieldsMap.get(AppConstants.JsonForm.DATA_COLLECTOR),
                        PreferencesUtil.getInstance().getCurrentDistrict());

                dataCollector = EusmApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
                if (StringUtils.isNotBlank(dataCollector)) {
                    populateServerOptions(EusmApplication.getInstance().getServerConfigs(),
                            AppConstants.CONFIGURATION.SPRAY_OPERATORS, fieldsMap.get(AppConstants.JsonForm.SPRAY_OPERATOR_CODE),
                            dataCollector);
                }
                break;
            default:
                break;
        }
    }

    private void populateUserAssignedLocations(JSONObject formJSON, String fieldKey, List<String> allowedTags) {
        JSONArray options = new JSONArray();
        List<String> defaultLocationHierarchy = locationHelper.generateDefaultLocationHierarchy(allowedTags);
        if (defaultLocationHierarchy == null) {
            return;
        }
        defaultLocationHierarchy.stream().forEach(options::put);
        JSONObject field = JsonFormUtils.getFieldJSONObject(JsonFormUtils.fields(formJSON), fieldKey);

        try {
            field.put(KEYS, options);
            field.put(VALUES, options);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }
}
