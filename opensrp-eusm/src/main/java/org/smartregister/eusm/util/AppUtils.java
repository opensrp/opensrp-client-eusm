package org.smartregister.eusm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.tasking.util.Utils;
import org.smartregister.util.JsonFormUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import timber.log.Timber;

import static org.smartregister.client.utils.constants.JsonFormConstants.Properties.DETAILS;
import static org.smartregister.eusm.util.AppConstants.STRUCTURE_IDS;
import static org.smartregister.tasking.interactor.BaseInteractor.gson;
import static org.smartregister.tasking.util.Constants.METADATA;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.getString;

public class AppUtils extends Utils {

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
        Event event = JsonFormUtils.createEvent(fields, metadata, Utils.getFormTag(), entityId, encounterType, bindType)
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
            Utils.getAllSharedPreferences().savePreference(STRUCTURE_IDS, android.text.TextUtils.join(",", savedStructureIds));
        }
    }

    public static Set<String> fetchStructureIds() {
        String structureIds = Utils.getAllSharedPreferences().getPreference(STRUCTURE_IDS);
        return Arrays.stream(StringUtils.split(structureIds, ",")).collect(Collectors.toSet());
    }
}
