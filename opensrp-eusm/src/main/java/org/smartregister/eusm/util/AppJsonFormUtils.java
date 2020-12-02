package org.smartregister.eusm.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.ProfileImage;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.activity.AppJsonFormActivity;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.ImageRepository;
import org.smartregister.stock.util.Constants;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import static org.smartregister.AllConstants.OPTIONS;
import static org.smartregister.AllConstants.TEXT;
import static org.smartregister.client.utils.constants.JsonFormConstants.Properties.DETAILS;
import static org.smartregister.tasking.util.Constants.METADATA;
import static org.smartregister.util.JsonFormUtils.ENCOUNTER_LOCATION;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.getString;


/**
 * Created by samuelgithengi on 3/22/19.
 */
public class AppJsonFormUtils {

    public static final int REQUEST_CODE_GET_JSON = 2244;
    private final Set<String> nonEditableFields;
    private final LocationHelper locationHelper = LocationHelper.getInstance();

    public AppJsonFormUtils() {
        nonEditableFields = new HashSet<>();
    }

    public static org.smartregister.clientandeventmodel.Event createTaskEvent(String baseEntityId, String locationId, Map<String, String> details, String eventType, String entityType) {
        org.smartregister.clientandeventmodel.Event taskEvent = (org.smartregister.clientandeventmodel.Event) new org.smartregister.clientandeventmodel.Event().withBaseEntityId(baseEntityId).withEventDate(new Date()).withEventType(eventType)
                .withLocationId(locationId).withEntityType(entityType).withFormSubmissionId(UUID.randomUUID().toString()).withDateCreated(new Date());
        return taskEvent;
    }

//    public JSONObject getFormJSON(Context context, String formName, Feature feature, String sprayStatus, String familyHead) {
//
//        String taskBusinessStatus = getPropertyValue(feature, AppConstants.Properties.TASK_BUSINESS_STATUS);
//        String taskIdentifier = getPropertyValue(feature, AppConstants.Properties.TASK_IDENTIFIER);
//        String taskStatus = getPropertyValue(feature, AppConstants.Properties.TASK_STATUS);
//
//        String structureId = feature.id();
//        String structureUUID = getPropertyValue(feature, AppConstants.Properties.LOCATION_UUID);
//        String structureVersion = getPropertyValue(feature, AppConstants.Properties.LOCATION_VERSION);
//        String structureType = getPropertyValue(feature, AppConstants.Properties.LOCATION_TYPE);
//
//        String formString = getFormObject(context, formName, structureType);
//        try {
//            JSONObject formJson = populateFormDetails(formString, structureId, structureId, taskIdentifier,
//                    taskBusinessStatus, taskStatus, structureUUID,
//                    structureVersion == null ? null : Integer.valueOf(structureVersion));
//
//            populateFormFields(formJson, structureType, sprayStatus, familyHead);
//            return formJson;
//        } catch (Exception e) {
//            Timber.e(e, "error launching form%s", formName);
//        }
//        return null;
//    }

//    public JSONObject getFormJSON(Context context, String formName, BaseTaskDetails task, Location structure) {
//
//        String taskBusinessStatus = "";
//        String taskIdentifier = "";
//        String taskStatus = "";
//        String entityId = "";
//        if (task != null) {
//            taskBusinessStatus = task.getBusinessStatus();
//            taskIdentifier = task.getTaskId();
//            taskStatus = task.getTaskStatus();
//
//            entityId = task.getTaskEntity();
//        }
//
//        String structureId = "";
//        String structureUUID = "";
//        int structureVersion = 0;
//        String structureType = "";
//        if (structure != null) {
//            structureId = structure.getId();
//            structureUUID = structure.getProperties().getUid();
//            structureVersion = structure.getProperties().getVersion();
//            structureType = structure.getProperties().getType();
//        }
//
//        String sprayStatus = null;
//        String familyHead = null;
//
//        if (task instanceof TaskDetails) {
//            sprayStatus = ((TaskDetails) task).getSprayStatus();
//            familyHead = ((TaskDetails) task).getFamilyName();
//        }
//
//        String formString = getFormObject(context, formName, structureType);
//        try {
//            JSONObject formJson = populateFormDetails(formString, entityId, structureId, taskIdentifier,
//                    taskBusinessStatus, taskStatus, structureUUID, structureVersion);
//            populateFormFields(formJson, structureType, sprayStatus, familyHead);
//            return formJson;
//        } catch (JSONException e) {
//            Timber.e(e, "error launching form%s", formName);
//        }
//        return null;
//    }

    public JSONObject getFormObject(Context context, String formName) {
        try {
            FormUtils formUtils = new FormUtils();
            return formUtils.getFormJsonFromRepositoryOrAssets(context, formName);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    public JSONObject getFormObjectWithDetails(Context context, String formName, StructureDetail structureDetail, TaskDetail taskDetail) {
        try {
            JSONObject jsonObject = getFormObject(context, formName);

            updateFormEncounterLocation(jsonObject, structureDetail.getStructureId());

            Map<String, String> map = new HashMap<>();
            map.put(AppConstants.EventDetailKey.LOCATION_NAME, structureDetail.getStructureName());
            map.put(AppConstants.EventDetailKey.LOCATION_ID, structureDetail.getStructureId());
            map.put(AppConstants.EventDetailKey.TASK_ID, taskDetail.getTaskId());
            map.put(AppConstants.EventDetailKey.PLAN_IDENTIFIER, AppConstants.PLAN_IDENTIFIER);
            map.put(AppConstants.EventDetailKey.MISSION, AppConstants.PLAN_NAME);
            String entityId;
            if (AppConstants.JsonForm.RECORD_GPS_FORM.equals(formName) || AppConstants.JsonForm.SERVICE_POINT_CHECK_FORM.equals(formName)) {
                entityId = structureDetail.getStructureId();
            } else {
                map.put(AppConstants.EventDetailKey.PRODUCT_NAME, taskDetail.getEntityName());
                map.put(AppConstants.EventDetailKey.PRODUCT_ID, taskDetail.getProductId());
                entityId = taskDetail.getStockId();
            }
            populateFormDetails(jsonObject, entityId, map);
            return jsonObject;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    public void updateFormEncounterLocation(JSONObject jsonForm, String locationId) {
        JSONObject metadata = JsonFormUtils.getJSONObject(jsonForm, METADATA);
        try {
            metadata.put(ENCOUNTER_LOCATION, locationId);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public void populateFormDetails(JSONObject formJson, String entityId, Map<String, String> detailsMap) throws JSONException {
        formJson.put(AppConstants.ENTITY_ID, entityId);
        JSONObject formData = new JSONObject();
        for (Map.Entry<String, String> entry : detailsMap.entrySet()) {
            formData.put(entry.getKey(), entry.getValue());
        }
        String planIdentifier = AppConstants.PLAN_IDENTIFIER;//PreferencesUtil.getInstance().getCurrentPlanId();
        formData.put(AppConstants.Properties.PLAN_IDENTIFIER, planIdentifier);
        formJson.put(AppConstants.DETAILS, formData);
    }

    private void populateFormFields(JSONObject formJson, String structureType, String sprayStatus, String familyHead) throws JSONException {

        JSONArray fields = org.smartregister.util.JsonFormUtils.fields(formJson);
        if (StringUtils.isNotBlank(structureType) || StringUtils.isNotBlank(sprayStatus) || StringUtils.isNotBlank(familyHead)) {
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                String key = field.getString(KEY);
//                if (key.equalsIgnoreCase(AppConstants.JsonForm.STRUCTURE_TYPE))
//                    field.put(org.smartregister.util.JsonFormUtils.VALUE, structureType);
//                else if (key.equalsIgnoreCase(AppConstants.JsonForm.SPRAY_STATUS))
//                    field.put(org.smartregister.util.JsonFormUtils.VALUE, sprayStatus);
//                else if (key.equalsIgnoreCase(AppConstants.JsonForm.HEAD_OF_HOUSEHOLD))
//                    field.put(org.smartregister.util.JsonFormUtils.VALUE, familyHead);
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

    public String getFormName(String encounterType) {
        return getFormName(encounterType, null);
    }

    public void populateField(JSONObject formJson, String key, String value, String fieldToPopulate) throws JSONException {
        JSONObject field = JsonFormUtils.getFieldJSONObject(JsonFormUtils.getMultiStepFormFields(formJson), key);
        if (field != null) {
            field.put(fieldToPopulate, value);
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

            } catch (JSONException e) {
                Timber.e(e);
            }
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

    public void saveImage(JSONObject jsonForm, String entityType) {
        String imageLocation = JsonFormUtils.getFieldValue(jsonForm.toString(), AppConstants.JsonFormKey.PRODUCT_PICTURE);
        if (StringUtils.isNotBlank(imageLocation)) {
            JSONObject detailsObject = JsonFormUtils.getJSONObject(jsonForm, DETAILS);
            String provider = EusmApplication.getInstance().context().allSharedPreferences().fetchRegisteredANM();
            String entityId;
            if (AppConstants.EventEntityType.PRODUCT.equals(entityType))
                entityId = String.format("%s_%s", getString(jsonForm, ENTITY_ID), detailsObject.optString(AppConstants.EventDetailKey.PLAN_IDENTIFIER));
            else
                entityId = getString(jsonForm, ENTITY_ID);

            saveImage(provider, entityId, imageLocation);
        }
    }

    public void saveImage(@NonNull String providerId, @NonNull String entityId,
                          @NonNull String imageLocation) {
        File file = new File(imageLocation);
        if (!file.exists()) {
            return;
        }
        Bitmap compressedImageFile = null;
        try {
            compressedImageFile = EusmApplication.getInstance().getCompressor().compressToBitmap(file);
        } catch (IOException e) {
            Timber.e(e);
        }

        saveStaticImageToDisk(compressedImageFile, providerId, entityId);
    }

    private void saveStaticImageToDisk(Bitmap image, String providerId, String entityId) {
        if (image == null || StringUtils.isBlank(providerId) || StringUtils.isBlank(entityId)) {
            return;
        }
        OutputStream os = null;
        try {

            if (!entityId.isEmpty()) {
                String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                File outputFile = new File(absoluteFileName);
                AppUtils.saveImageAndCloseOutputStream(image, outputFile);

                // insert into the db
                ProfileImage profileImage = new ProfileImage();
                profileImage.setImageid(UUID.randomUUID().toString());
                profileImage.setAnmId(providerId);
                profileImage.setEntityID(entityId);
                profileImage.setFilepath(absoluteFileName);
                profileImage.setFilecategory(Constants.PRODUCT_IMAGE);
                profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);
                ImageRepository imageRepo = EusmApplication.getInstance().context().imageRepository();
                imageRepo.add(profileImage);
            }

        } catch (FileNotFoundException e) {
            Timber.e(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }

    }

}
