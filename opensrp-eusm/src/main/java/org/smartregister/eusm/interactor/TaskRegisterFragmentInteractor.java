package org.smartregister.eusm.interactor;

import android.content.Context;

import androidx.annotation.NonNull;

import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.AppExecutors;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.model.StructureTaskDetail;
import org.smartregister.eusm.util.TestDataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskRegisterFragmentInteractor implements TaskRegisterFragmentContract.Interactor {

    private AppExecutors appExecutors;

    private Context context = EusmApplication.getInstance().getBaseContext();

    public TaskRegisterFragmentInteractor() {
        appExecutors = EusmApplication.getInstance().getAppExecutors();
    }

    @Override
    public void fetchData(@NonNull TaskRegisterFragmentContract.InteractorCallBack callBack) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //fetchData

                List<StructureTaskDetail> structureTaskDetails = TestDataUtils.getStructureDetail();

                List<StructureTaskDetail> sortStructureTaskDetails = sortTaskDetails(structureTaskDetails);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFetchedData(sortStructureTaskDetails);
                    }
                });
            }
        });
    }

    private List<StructureTaskDetail> sortTaskDetails(List<StructureTaskDetail> structureTaskDetails) {
        StructureTaskDetail sItemHeader = new StructureTaskDetail();
        sItemHeader.setChecked(true);
        sItemHeader.setProductName(context.getString(R.string.task_item_text));
        sItemHeader.setHeader(true);
        sItemHeader.setNonProductTask(false);

        StructureTaskDetail sChecked = new StructureTaskDetail();
        sChecked.setChecked(true);
        sChecked.setProductName(context.getString(R.string.task_checked_text));
        sChecked.setHeader(true);
        sChecked.setNonProductTask(false);

        StructureTaskDetail sEmpty = new StructureTaskDetail();
        sEmpty.setChecked(false);
        sEmpty.setProductName(context.getString(R.string.no_items_to_check));
        sEmpty.setHeader(false);
        sEmpty.setEmptyView(true);
        sChecked.setNonProductTask(false);
        List<StructureTaskDetail> checkedStructureTaskDetails = new ArrayList<>();

        if (!structureTaskDetails.isEmpty()) {
            checkedStructureTaskDetails = structureTaskDetails.stream().filter(structureTaskDetail -> structureTaskDetail.isChecked()).collect(Collectors.toList());
        }

        List<StructureTaskDetail> structureTaskDetailList = new ArrayList<>();
        structureTaskDetailList.add(sItemHeader);
        structureTaskDetails.removeAll(checkedStructureTaskDetails);

        if (structureTaskDetails.isEmpty()) {
            structureTaskDetailList.add(sEmpty);
        } else {
            structureTaskDetailList.addAll(structureTaskDetails);
        }
        structureTaskDetailList.add(sChecked);
        if (checkedStructureTaskDetails.isEmpty()) {
            sEmpty.setProductName(context.getString(R.string.no_items_checked_yet));
            structureTaskDetailList.add(sEmpty);
        } else {
            structureTaskDetailList.addAll(checkedStructureTaskDetails);
        }
        return structureTaskDetailList;
    }
}
