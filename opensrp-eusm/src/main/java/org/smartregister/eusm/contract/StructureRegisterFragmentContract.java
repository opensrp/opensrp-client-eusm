package org.smartregister.eusm.contract;


import android.location.Location;

import androidx.annotation.StringRes;

import org.json.JSONObject;
import org.smartregister.eusm.adapter.StructureRegisterAdapter;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.tasking.util.LocationUtils;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;

public interface StructureRegisterFragmentContract {

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

        void onDestroy();

        void onDrawerClosed();

        void onNextButtonClick();

        void onPreviousButtonClick();
    }

    interface View extends BaseRegisterFragmentContract.View {

        Location getLastLocation();

        void initializeAdapter();

        void setTotalServicePoints(int structuresWithinBuffer);

        void setStructureDetails(List<StructureDetail> structureDetails);

        void displayNotification(int title, @StringRes int message, Object... formatArgs);

        void showProgressDialog(@StringRes int title, @StringRes int message);

        void hideProgressDialog();

        LocationUtils getLocationUtils();

        void displayIndexCaseDetails(JSONObject indexCase);

        void setNumberOfFilters(int numberOfFilters);

        void clearFilter();

        StructureRegisterAdapter getAdapter();

        void setSearchPhrase(String searchPhrase);

        void startMapActivity();

        void updateSearchBarHint(String searchBarText);

    }

    interface Interactor {

        void fetchStructures(InteractorCallback callback, int currentPageNo, String nameFilter);

        void countOfStructures(InteractorCallback callback, String nameFilter);
    }

    interface InteractorCallback {

        void onFetchedStructures(List<StructureDetail> structureDetails);

        void onCountOfStructuresFetched(int count);
    }
}
