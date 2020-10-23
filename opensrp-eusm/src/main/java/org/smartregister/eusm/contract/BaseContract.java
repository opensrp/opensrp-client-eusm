package org.smartregister.eusm.contract;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Feature;

import org.json.JSONArray;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;

public interface BaseContract {

    interface BasePresenter {

        void onFormSaved(@NonNull String structureId,
                         String taskID, @NonNull Task.TaskStatus taskStatus, @NonNull String businessStatus, String interventionType);

        void onFormSaveFailure(String eventType);

    }

    interface BaseInteractor {

        void saveJsonForm(String json);

        void handleLastEventFound(Event event);

        void findLastEvent(String eventBaseEntityId, String eventType);
    }
}
