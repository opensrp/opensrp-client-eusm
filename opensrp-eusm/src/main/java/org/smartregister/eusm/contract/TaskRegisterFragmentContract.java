package org.smartregister.eusm.contract;

import android.app.Activity;

import androidx.annotation.NonNull;

import org.json.JSONObject;
import org.smartregister.eusm.adapter.TaskRegisterAdapter;
import org.smartregister.eusm.model.StructureTaskDetail;

import java.util.List;

public interface TaskRegisterFragmentContract {

    interface View {

        void initializePresenter();

        void onViewClicked(android.view.View view);

        void onResumption();

        TaskRegisterAdapter getAdapter();

        void startFixProblemForm(JSONObject form);

        Activity getActivity();
    }

    interface Presenter {
        void fetchData();

        View getView();

        void startFixProblemForm(StructureTaskDetail structureTaskDetail);

    }

    interface Interactor {
        void fetchData(@NonNull TaskRegisterFragmentContract.InteractorCallBack callBack);

        String getFixProblemForm();

        void startFixProblemForm(StructureTaskDetail structureTaskDetail, Activity activity, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onFetchedData(List<StructureTaskDetail> structureTaskDetailList);

        void onFixProblemFormFetched(JSONObject jsonForm);
    }
}
