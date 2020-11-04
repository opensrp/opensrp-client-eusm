package org.smartregister.eusm.interactor;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sqlcipher.Cursor;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.LocationProperty;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.BaseContract;
import org.smartregister.eusm.contract.BaseContract.BasePresenter;
import org.smartregister.eusm.processor.AppClientProcessor;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.eusm.util.PreferencesUtil;
import org.smartregister.eusm.util.TaskUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.StructureRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.util.DateTimeTypeConverter;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.PropertiesConverter;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.getString;


/**
 * Created by samuelgithengi on 3/25/19.
 */
public class BaseInteractor implements BaseContract.BaseInteractor {

    public static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
    protected TaskRepository taskRepository;
    protected StructureRepository structureRepository;
    protected BasePresenter presenterCallBack;
    protected String operationalAreaId;
    protected AppExecutors appExecutors;
    protected AllSharedPreferences sharedPreferences;
    protected EventClientRepository eventClientRepository;
    protected AppClientProcessor clientProcessor;
    private final EusmApplication eusmApplication;
    private final TaskUtils taskUtils;

    private final SQLiteDatabase database;

    private CommonRepository commonRepository;

    private final PreferencesUtil prefsUtil;

    public BaseInteractor(BasePresenter presenterCallBack) {
        eusmApplication = EusmApplication.getInstance();
        this.presenterCallBack = presenterCallBack;
        appExecutors = eusmApplication.getAppExecutors();
        taskRepository = eusmApplication.getTaskRepository();
        structureRepository = eusmApplication.getStructureRepository();
        eventClientRepository = eusmApplication.getContext().getEventClientRepository();
        clientProcessor = AppClientProcessor.getInstance(eusmApplication.getApplicationContext());
        sharedPreferences = eusmApplication.getContext().allSharedPreferences();
        taskUtils = TaskUtils.getInstance();
        database = eusmApplication.getRepository().getReadableDatabase();
        prefsUtil = PreferencesUtil.getInstance();
    }

    @VisibleForTesting
    public BaseInteractor(BasePresenter presenterCallBack, CommonRepository commonRepository) {
        this(presenterCallBack);
        this.commonRepository = commonRepository;
    }

    @Override
    public void saveJsonForm(String json) {

    }

    @Override
    public void handleLastEventFound(org.smartregister.domain.Event event) {
        // handle in child class
    }

    private org.smartregister.domain.Event saveEvent(JSONObject jsonForm, String encounterType, String bindType) throws JSONException {
        String entityId = getString(jsonForm, ENTITY_ID);
        JSONArray fields = JsonFormUtils.fields(jsonForm);
        JSONObject metadata = JsonFormUtils.getJSONObject(jsonForm, AppConstants.METADATA);
        Event event = JsonFormUtils.createEvent(fields, metadata, AppUtils.getFormTag(), entityId, encounterType, bindType);
        JSONObject eventJson = new JSONObject(gson.toJson(event));
        eventJson.put(AppConstants.DETAILS, JsonFormUtils.getJSONObject(jsonForm, AppConstants.DETAILS));
        eventClientRepository.addEvent(entityId, eventJson);
        return gson.fromJson(eventJson.toString(), org.smartregister.domain.Event.class);
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    @Override
    public void findLastEvent(String eventBaseEntityId, String eventType) {
        appExecutors.diskIO().execute(() -> {
            String events = String.format("select %s from %s where %s = ? and %s =? order by %s desc limit 1",
                    EventClientRepository.event_column.json, EventClientRepository.Table.event.name(), EventClientRepository.event_column.baseEntityId, EventClientRepository.event_column.eventType, EventClientRepository.event_column.updatedAt);

            try (Cursor cursor = getDatabase().rawQuery(events, new String[]{eventBaseEntityId, eventType})) {

                if (cursor.moveToFirst()) {
                    String eventJSON = cursor.getString(0);
                    handleLastEventFound(eventClientRepository.convert(eventJSON, org.smartregister.domain.Event.class));

                } else {
                    handleLastEventFound(null);
                }
            } catch (SQLException e) {
                Timber.e(e);
            }
        });

    }
}
