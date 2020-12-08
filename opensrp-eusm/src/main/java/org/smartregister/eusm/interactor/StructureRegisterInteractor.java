package org.smartregister.eusm.interactor;

import android.content.Context;

import androidx.annotation.NonNull;

import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.comparator.StructureDetailNameComparator;
import org.smartregister.eusm.contract.StructureRegisterFragmentContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.model.StructureRegisterFragmentModel;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.tasking.TaskingLibrary;
import org.smartregister.util.AppExecutors;

import java.util.Collections;
import java.util.List;

public class StructureRegisterInteractor implements StructureRegisterFragmentContract.Interactor {

    private final StructureRegisterFragmentModel model;

    private final AppExecutors appExecutors;

    private final Context context;

    public StructureRegisterInteractor() {
        model = new StructureRegisterFragmentModel();
        appExecutors = TaskingLibrary.getInstance().getAppExecutors();
        context = EusmApplication.getInstance().getBaseContext();
    }

    @Override
    public void fetchStructures(StructureRegisterFragmentContract.InteractorCallback callback,
                                int currentPageNo, String nameFilter) {
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
        StructureDetail structureDetailNearby = new StructureDetail();
        structureDetailNearby.setHeader(true);
        structureDetailNearby.setStructureName(
                String.format(context.getString(R.string.nearby_within_n_m), AppConstants.NEARBY_DISTANCE_IN_METRES.toString()));

        StructureDetail structureDetailOther = new StructureDetail();
        structureDetailOther.setHeader(true);
        structureDetailOther.setStructureName(context.getString(R.string.other_service_points_a_to_z));

        boolean hasOtherPoints = false;
        boolean hasNearbyPoints = false;

        for (int i = 0; i < structureDetails.size(); i++) {
            StructureDetail structureDetail = structureDetails.get(i);
            if (!structureDetail.isHeader()) {
                if (hasNearbyPoints && hasOtherPoints) {
                    break;
                }
                if (structureDetail.isNearby()) {
                    if (!hasNearbyPoints) {
                        structureDetails.add(i, structureDetailNearby);
                        hasNearbyPoints = true;
                    }
                } else {
                    if (!hasOtherPoints) {
                        Collections.sort(structureDetails.subList(i, structureDetails.size()), new StructureDetailNameComparator());
                        structureDetails.add(i, structureDetailOther);
                        hasOtherPoints = true;
                    }
                }
            }
        }
        return structureDetails;
    }
}
