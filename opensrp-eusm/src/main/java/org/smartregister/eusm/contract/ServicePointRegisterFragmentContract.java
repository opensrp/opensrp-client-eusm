package org.smartregister.eusm.contract;


import android.content.Context;
import android.location.Location;

import androidx.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.eusm.adapter.ServicePointRegisterAdapter;
import org.smartregister.eusm.model.TaskDetails;
import org.smartregister.eusm.model.TaskFilterParams;
import org.smartregister.eusm.util.LocationUtils;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

public interface ServicePointRegisterFragmentContract {

    interface Presenter extends BaseRegisterFragmentContract.Presenter, BaseContract.BasePresenter {

        void onDestroy();

        void onDrawerClosed();

        void onTaskSelected(TaskDetails details, boolean isActionClicked);

        void onIndexCaseFound(JSONObject indexCase, boolean isLinkedToJurisdiction);

        void searchServicePoints(String searchText);

        void filterServicePoints(TaskFilterParams filterParams);

        void onFilterTasksClicked();

        void setTaskFilterParams(TaskFilterParams filterParams);

        void onOpenMapClicked();

        void resetTaskInfo(TaskDetails taskDetails);

        void onEventFound(Event event);
    }

    interface View extends BaseRegisterFragmentContract.View {

        Location getLastLocation();

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        void setTotalServicePoints(int structuresWithinBuffer);

        void setServicePointDetails(List<TaskDetails> tasks);

        void displayNotification(int title, @StringRes int message, Object... formatArgs);

        void showProgressDialog(@StringRes int title, @StringRes int message);

        void hideProgressDialog();

        LocationUtils getLocationUtils();

        void displayIndexCaseDetails(JSONObject indexCase);

        void setNumberOfFilters(int numberOfFilters);

        void clearFilter();

        ServicePointRegisterAdapter getAdapter();

        void openFilterActivity(TaskFilterParams filterParams);

        void setSearchPhrase(String searchPhrase);

        void startMapActivity(TaskFilterParams taskFilterParams);
    }

    interface Interactor {
        void resetTaskInfo(Context context, TaskDetails taskDetails);

        void findLastEvent(String eventBaseEntityId, String eventType);
    }


}
