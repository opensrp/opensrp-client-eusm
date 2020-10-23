package org.smartregister.eusm.contract;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.eusm.contract.UserLocationContract.UserLocationView;
import org.smartregister.eusm.model.CardDetails;
import org.smartregister.eusm.model.TaskDetails;
import org.smartregister.eusm.model.TaskFilterParams;

import java.util.List;

/**
 * Created by samuelgithengi on 11/27/18.
 */
public interface HomeActivityContract {

    interface HomeActivityView extends UserLocationView, BaseMapActivityContract.BaseMapActivityView, BaseDrawerContract.DrawerActivity {

        void showProgressDialog(@StringRes int title, @StringRes int message, Object... formatArgs);

        void hideProgressDialog();

        void openServicePointsActivity(TaskFilterParams filterParams);

        void openServicePointRegister(TaskFilterParams filterParams);

        void setGeoJsonSource(@NonNull FeatureCollection featureCollection, Feature operationalArea, boolean changeMapPosition);

        void displayNotification(int title, @StringRes int message, Object... formatArgs);

        void displayNotification(String message);

        void openCardView(CardDetails cardDetails);

        void startJsonForm(JSONObject form);

        void displayToast(@StringRes int resourceId);

        void displayMarkStructureInactiveDialog();

        void setSearchPhrase(String searchPhrase);

        void toggleProgressBarView(boolean syncing);

        void setOperationalArea(String operationalArea);
    }

    interface Presenter extends BaseMapActivityContract.Presenter {

        void onStructuresFetched(JSONObject structuresGeoJson, Feature operationalArea, List<TaskDetails> taskDetailsList);

        void onStructuresFetched(JSONObject structuresGeoJson, Feature operationalArea, List<TaskDetails> taskDetailsList, String point, Boolean locationComponentActive);

        void onDrawerClosed();

        void resetFeatureTasks(String structureId, Task task);

        void onStructureAdded(Feature feature, JSONArray featureCoordinates, double zoomLevel);

        void onFormSaveFailure(String eventType);

        void onCardDetailsFetched(CardDetails cardDetails);

        void onInterventionFormDetailsFetched(CardDetails finalCardDetails);

        void onMarkStructureInactiveConfirmed();

        void onStructureMarkedInactive();

        void onMarkStructureIneligibleConfirmed();

        void onStructureMarkedIneligible();

        void onOpenServicePointRegister();

        void setTaskFilterParams(TaskFilterParams filterParams);

        void onEventFound(Event event);

        void findLastEvent(String featureId, String eventType);
    }
}
