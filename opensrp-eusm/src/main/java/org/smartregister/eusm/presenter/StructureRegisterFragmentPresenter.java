package org.smartregister.eusm.presenter;

import androidx.annotation.NonNull;

import org.smartregister.domain.Task;
import org.smartregister.eusm.R;
import org.smartregister.eusm.comparator.StructureDetailDistanceComparator;
import org.smartregister.eusm.comparator.StructureDetailNameComparator;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.BaseContract;
import org.smartregister.eusm.contract.StructureRegisterFragmentContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.StructureRegisterFragmentModel;
import org.smartregister.eusm.util.AppConstants;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructureRegisterFragmentPresenter extends BaseRegisterFragmentPresenter implements BaseContract.BasePresenter {

    private WeakReference<StructureRegisterFragmentContract.View> viewWeakReference;

    private StructureRegisterFragmentModel model;

    private AppExecutors appExecutors;

    public StructureRegisterFragmentPresenter(StructureRegisterFragmentContract.View view) {
        this.viewWeakReference = new WeakReference<>(view);
        this.model = new StructureRegisterFragmentModel();
        appExecutors = new AppExecutors();
    }

    @Override
    public void initializeQueries(String s) {
        if (getView().getAdapter() == null) {
            getView().initializeAdapter();

            appExecutors.diskIO().execute(() -> {
                List<StructureDetail> structureDetails = fetchStructures();
                appExecutors.mainThread().execute(() -> getView().setStructureDetails(structureDetails));
            });
        }
    }

    public List<StructureDetail> fetchStructures() {
        List<StructureDetail> structureDetails = model.fetchStructures();
        return sortStructures(structureDetails);
    }

    private List<StructureDetail> sortStructures(@NonNull List<StructureDetail> structureDetails) {
        //gets nearby sorts by distance then
        //after nearby false sort alphabetically then splice then append to list
        StructureDetail structureDetailNearby = new StructureDetail();
        structureDetailNearby.setHeader(true);
        structureDetailNearby.setStructureName(
                String.format(getView().getContext().getString(R.string.nearby_within_n_m), AppConstants.NEARBY_DISTANCE_IN_METRES.toString()));

        StructureDetail structureDetailOther = new StructureDetail();
        structureDetailOther.setHeader(true);
        structureDetailOther.setStructureName(getView().getContext().getString(R.string.other_service_points_a_to_z));

        //sort by distance
        Collections.sort(structureDetails, new StructureDetailDistanceComparator());

        boolean hasOtherPoints = false;
        for (int i = 0; i < structureDetails.size(); i++) {
            StructureDetail structureDetail = structureDetails.get(i);
            if (!structureDetail.isNearby()) {
                hasOtherPoints = true;
                List<StructureDetail> detailListNearBy = structureDetails.subList(0, i);
                detailListNearBy.add(0, structureDetailNearby);

                List<StructureDetail> detailListFar = structureDetails.subList(i, structureDetails.size() - 1);

                //sort by names
                Collections.sort(detailListFar, new StructureDetailNameComparator());
                detailListFar.add(0, structureDetailOther);
                List<StructureDetail> resultList = new ArrayList<>();//detailListNearBy.addAll(detailListFar);
                resultList.addAll(detailListNearBy);
                resultList.addAll(detailListFar);
                return resultList;
            }
        }

        if (!hasOtherPoints) {
            structureDetails.add(0, structureDetailNearby);
            structureDetails.add(structureDetailOther);
            StructureDetail s = structureDetails.get(1);
            StructureDetail s2 = structureDetails.get(2);
            s2.setTaskStatus("Completed");
            structureDetails.get(3).setTaskStatus("in progress");
            s.setTaskStatus("10 items");
            structureDetails.add(s);
        }

        return structureDetails;
    }

    @Override
    public void onFormSaved(@NonNull String structureId, String taskID, @NonNull Task.TaskStatus taskStatus, @NonNull String businessStatus, String interventionType) {

    }

    @Override
    public void onFormSaveFailure(String eventType) {

    }

    public StructureRegisterFragmentContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        } else {
            return null;
        }
    }
}
