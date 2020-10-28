package org.smartregister.eusm.contract;

import androidx.annotation.NonNull;

import org.smartregister.eusm.adapter.TaskRegisterAdapter;
import org.smartregister.eusm.model.StructureTaskDetail;

import java.util.List;

public interface TaskRegisterFragmentContract {

    interface View {

        void initializePresenter();

        void onViewClicked(android.view.View view);

        void onResumption();

        TaskRegisterAdapter getAdapter();
    }

    interface Presenter  {
        void fetchData();

        TaskRegisterFragmentContract.View getView();

//        StructureTaskRegisterFragmentContract.View getView();
    }

    interface Interactor {
        void fetchData(@NonNull TaskRegisterFragmentContract.InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onFetchedData(List<StructureTaskDetail> structureTaskDetailList);
    }
}
