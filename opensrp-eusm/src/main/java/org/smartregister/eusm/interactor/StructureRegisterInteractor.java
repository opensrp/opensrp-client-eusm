package org.smartregister.eusm.interactor;

import android.content.Context;

import androidx.annotation.NonNull;

import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.comparator.StructureDetailDistanceComparator;
import org.smartregister.eusm.comparator.StructureDetailNameComparator;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.StructureRegisterFragmentContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.StructureRegisterFragmentModel;
import org.smartregister.eusm.util.AppConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructureRegisterInteractor implements StructureRegisterFragmentContract.Interactor {

    private StructureRegisterFragmentModel model;

    private AppExecutors appExecutors;

    private Context context;

    public StructureRegisterInteractor() {
        model = new StructureRegisterFragmentModel();
        appExecutors = EusmApplication.getInstance().getAppExecutors();
        context = EusmApplication.getInstance().getBaseContext();
    }

    @Override
    public void fetchStructures(StructureRegisterFragmentContract.InteractorCallback callback, int currentPageNo, String nameFilter) {
        List<StructureDetail> structureDetails = model.fetchStructures(currentPageNo, nameFilter);
        List<StructureDetail> sortedStructureDetails = sortStructures(structureDetails);
        appExecutors.mainThread().execute(() -> {
            callback.onFetchedStructures(sortedStructureDetails);
        });
    }

    @Override
    public void countOfStructures(StructureRegisterFragmentContract.InteractorCallback callback, String nameFilter) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int totalCount = model.countOfStructures(nameFilter);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCountOfStructuresFetched(totalCount);
                    }
                });
            }
        });
    }

    private List<StructureDetail> sortStructures(@NonNull List<StructureDetail> structureDetails) {
        //gets nearby sorts by distance then
        //after nearby false sort alphabetically then splice then append to list
        StructureDetail structureDetailNearby = new StructureDetail();
        structureDetailNearby.setHeader(true);
        structureDetailNearby.setStructureName(
                String.format(context.getString(R.string.nearby_within_n_m), AppConstants.NEARBY_DISTANCE_IN_METRES.toString()));

        StructureDetail structureDetailOther = new StructureDetail();
        structureDetailOther.setHeader(true);
        structureDetailOther.setStructureName(context.getString(R.string.other_service_points_a_to_z));

        //sort by distance
        Collections.sort(structureDetails, new StructureDetailDistanceComparator());

        boolean hasOtherPoints = false;
        for (int i = 0; i < structureDetails.size(); i++) {
            StructureDetail structureDetail = structureDetails.get(i);
            if (!structureDetail.isNearby()) {
                hasOtherPoints = true;
                List<StructureDetail> detailListNearBy = new ArrayList<>(structureDetails.subList(0, i));
                detailListNearBy.add(0, structureDetailNearby);

                List<StructureDetail> detailListFar = new ArrayList<>(structureDetails.subList(i, structureDetails.size()));
                //sort by names
                Collections.sort(detailListFar, new StructureDetailNameComparator());
                detailListFar.add(0, structureDetailOther);

                List<StructureDetail> resultList = new ArrayList<>();
                resultList.addAll(detailListNearBy);
                resultList.addAll(detailListFar);
                return resultList;
            }
        }

        if (!hasOtherPoints) {
            structureDetails.add(0, structureDetailNearby);
            structureDetails.add(structureDetailOther);
        }

        return structureDetails;
    }
}
