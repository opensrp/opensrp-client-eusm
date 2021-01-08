package org.smartregister.eusm.util;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.ProfileImage;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.repository.ImageRepository;
import org.smartregister.stock.util.Constants;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

import static org.smartregister.client.utils.constants.JsonFormConstants.Properties.DETAILS;
import static org.smartregister.tasking.util.Constants.METADATA;
import static org.smartregister.util.JsonFormUtils.ENCOUNTER_LOCATION;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.getString;


public class AppJsonFormUtils {


    public AppJsonFormUtils() {
    }

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
            map.put(AppConstants.EventDetailKey.LOCATION_NAME, structureDetail.getEntityName());
            map.put(AppConstants.EventDetailKey.LOCATION_ID, structureDetail.getStructureId());
            map.put(AppConstants.Properties.TASK_IDENTIFIER, taskDetail.getTaskId());
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
