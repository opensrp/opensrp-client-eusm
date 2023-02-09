package org.smartregister.eusm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Geometry;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.repository.AppStructureRepository;
import org.smartregister.repository.BaseRepository;
import org.smartregister.tasking.util.Utils;
import org.smartregister.util.Cache;
import org.smartregister.util.JsonFormUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import timber.log.Timber;

import static org.smartregister.client.utils.constants.JsonFormConstants.Properties.DETAILS;
import static org.smartregister.eusm.util.AppConstants.PreferenceKey.HAS_UPGRADED;
import static org.smartregister.eusm.util.AppConstants.STRUCTURE_IDS;
import static org.smartregister.tasking.interactor.BaseInteractor.gson;
import static org.smartregister.tasking.util.Constants.METADATA;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.getString;

public class AppUtils extends Utils {

    private static Cache<org.smartregister.domain.Location> cache = new Cache<>();

    public static Float distanceFromUserLocation(@NonNull android.location.Location location) {
        android.location.Location userLocation = EusmApplication.getInstance().getUserLocation();
        if (userLocation != null && location != null) {
            return userLocation.distanceTo(location);
        }
        Timber.e("UserLocation is null");
        return null;
    }

    public static org.smartregister.domain.Event createEventFromJsonForm(@NonNull JSONObject jsonForm,
                                                                         @NonNull String encounterType,
                                                                         @NonNull String bindType,
                                                                         @NonNull String entityType) throws JSONException {
        String entityId = getString(jsonForm, ENTITY_ID);
        JSONArray fields = JsonFormUtils.fields(jsonForm);
        JSONObject metadata = JsonFormUtils.getJSONObject(jsonForm, METADATA);
        FormTag formTag = Utils.getFormTag();
        formTag.locationId = getAllSharedPreferences().getPreference(AppConstants.PreferenceKey.COMMUNE_ID);
        Event event = JsonFormUtils.createEvent(fields, metadata, formTag, entityId, encounterType, bindType)
                .withEntityType(entityType);
        JSONObject eventJson = new JSONObject(gson.toJson(event));
        eventJson.put(DETAILS, JsonFormUtils.getJSONObject(jsonForm, DETAILS));
        EusmApplication.getInstance().getEcSyncHelper().addEvent(entityId, eventJson);
        return gson.fromJson(eventJson.toString(), org.smartregister.domain.Event.class);
    }


    public static void initiateEventProcessing(@NonNull List<String> formSubmissionIds) throws Exception {
        long lastSyncTimeStamp = Utils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        EusmApplication.getInstance().getClientProcessor()
                .processClient(
                        EusmApplication.getInstance()
                                .getEcSyncHelper()
                                .getEvents(formSubmissionIds), true);

        Utils.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
    }

    public static JSONObject getHiddenFieldTemplate(@NonNull String key,
                                                    @NonNull String value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.HIDDEN);
            jsonObject.put(JsonFormConstants.VALUE, value);
            jsonObject.put(JsonFormConstants.KEY, key);
        } catch (JSONException e) {
            Timber.e(e);
        }
        return jsonObject;
    }

    public static void saveImageAndCloseOutputStream(Bitmap image, File outputFile) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outputFile);
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        image.compress(compressFormat, 100, os);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @NotNull
    public static String formatTaskStatus(String taskStatus_, Context context) {
        String taskStatus = "";
        if (AppConstants.TaskStatus.IN_PROGRESS.equals(taskStatus_)) {
            taskStatus = context.getString(R.string.tasks_in_progress);
        } else if (AppConstants.TaskStatus.COMPLETED.equals(taskStatus_)) {
            taskStatus = context.getString(R.string.tasks_completed);
        } else {
            taskStatus = String.format(context.getString(R.string.no_of_items), taskStatus_);
        }
        return taskStatus;
    }

    public static int getColorByTaskStatus(@NonNull String taskStatus) {
        int colorId = R.color.text_gray;
        if (AppConstants.TaskStatus.COMPLETED.equals(taskStatus)) {
            colorId = R.color.task_completed;
        } else if (AppConstants.TaskStatus.IN_PROGRESS.equals(taskStatus)) {
            colorId = R.color.task_in_progress;
        }
        return colorId;
    }

    public static void saveStructureIds(List<String> structureIds) {
        if (structureIds != null && !structureIds.isEmpty()) {
            Set<String> savedStructureIds = fetchStructureIds();
            if (savedStructureIds == null) {
                savedStructureIds = new HashSet<>();
            }
            savedStructureIds.addAll(structureIds);
            getAllSharedPreferences().savePreference(STRUCTURE_IDS, android.text.TextUtils.join(",", savedStructureIds));
        }
    }

    public static Set<String> fetchStructureIds() {
        String structureIds = getAllSharedPreferences().getPreference(STRUCTURE_IDS);
        return Arrays.stream(StringUtils.split(structureIds, ",")).collect(Collectors.toSet());
    }

    public static Set<String> getDistrictsFromLocationHierarchy() {
        org.smartregister.domain.jsonmapping.util.LocationTree locationTree = gson.fromJson(CoreLibrary.getInstance().context().allSettings().fetchANMLocation(), org.smartregister.domain.jsonmapping.util.LocationTree.class);
        Set<String> districtIds = new HashSet<>();
        if (locationTree != null) {
            Set<String> parentLocations = new HashSet<>();
            LinkedHashMap<String, LinkedHashSet<String>> hashMap = locationTree.getChildParent();
            for (Map.Entry<String, LinkedHashSet<String>> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                parentLocations.add(key);
            }

            for (String id : parentLocations) {
                Location location = locationTree.findLocation(id);
                if (location != null && StringUtils.isNotBlank(location.getLocationId())
                        && location.hasTag(AppConstants.LocationLevels.DISTRICT_TAG)) {
                    districtIds.add(location.getLocationId());
                }
            }
        }
        return districtIds;
    }

    public static String getStringFromJsonElement(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return (element != null) ? element.getAsString() : AppConstants.CardDetailKeys.DISTANCE_META.equals(key) ? "-" : "";
    }

    public static org.smartregister.domain.Location getOperationalAreaLocation(String operationalArea) {
        return cache.get(operationalArea, () -> {
            return EusmApplication.getInstance().getAppLocationRepository()
                    .getLocationByNameAndGeoLevel(operationalArea, AppConstants.LocationGeographicLevel.DISTRICT);//restrict to district geographic level
        });
    }

    public static Set<org.smartregister.domain.Location> getOperationalAreaLocations(Set<String> operationalAreas) {
        return EusmApplication.getInstance().getAppLocationRepository()
                .getLocationByNameAndGeoLevel(operationalAreas, AppConstants.LocationGeographicLevel.DISTRICT);//restrict to district geographic level
    }


    public static Pair<Float, Float> getLatLongFromForm(@NonNull JSONObject form) {
        JSONArray formFields = JsonFormUtils.getMultiStepFormFields(form);
        for (int i = 0; i < formFields.length(); i++) {
            JSONObject fieldObject = formFields.optJSONObject(i);
            if (fieldObject != null) {
                String key = fieldObject.optString(JsonFormConstants.KEY);
                if (AppConstants.JsonFormKey.GPS.equals(key)) {
                    String value = fieldObject.optString(JsonFormConstants.VALUE);
                    String[] values = value.split(" ");
                    if (values.length >= 2) {
                        float latitude = Float.parseFloat(values[0]);
                        float longitude = Float.parseFloat(values[1]);
                        return Pair.create(latitude, longitude);
                    }
                }
            }
        }
        return null;
    }

    public static void updateLocationCoordinatesFromForm(StructureDetail structureDetail, JSONObject form) {
        Pair<Float, Float> latLngPair = AppUtils.getLatLongFromForm(form);
        if (latLngPair != null) {
            AppStructureRepository structureRepository = EusmApplication.getInstance().getStructureRepository();
            org.smartregister.domain.Location location = structureRepository.getLocationById(structureDetail.getStructureId());
            location.setSyncStatus(BaseRepository.TYPE_Created);

            JsonArray jsonArray = new JsonArray(2);
            jsonArray.add(latLngPair.second);
            jsonArray.add(latLngPair.first);

            Geometry geometry = new Geometry();
            geometry.setCoordinates(jsonArray);
            geometry.setType(Geometry.GeometryType.POINT);

            location.setGeometry(geometry);

            structureRepository.addOrUpdate(location);
        }
    }

    /*
     * Return false when the app is launched or upgraded and
     * previous periodically scheduled jobs are not cancelled
     */
    public static boolean hasDisabledScheduledJobs() {
        SharedPreferences sharedPreferences = EusmApplication.getInstance().context().allSharedPreferences().getPreferences();
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(HAS_UPGRADED, false);
        }
        return false;
    }

    /*
     * Save preference for cancelling scheduled job
     */
    public static void saveHasDisabledScheduledJobs() {
        SharedPreferences sharedPreferences = EusmApplication.getInstance().context().allSharedPreferences().getPreferences();
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean(HAS_UPGRADED, true).apply();
        }
    }

}
