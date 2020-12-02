package org.smartregister.eusm.presenter;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.domain.Event;
import org.smartregister.domain.Task;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.interactor.ProductInfoFragmentInteractor;
import org.smartregister.eusm.model.ProductInfoQuestion;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.repository.AppTaskRepository;
import org.smartregister.eusm.util.AppConstants;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class ProductInfoFragmentPresenter implements ProductInfoFragmentContract.Presenter, ProductInfoFragmentContract.InteractorCallBack {

    private WeakReference<ProductInfoFragmentContract.View> viewWeakReference;

    private ProductInfoFragmentInteractor interactor;

    private FormUtils formUtils;

    public ProductInfoFragmentPresenter(ProductInfoFragmentContract.View view) {
        viewWeakReference = new WeakReference<>(view);
        interactor = new ProductInfoFragmentInteractor();
    }

    @Override
    public ProductInfoFragmentContract.View getView() {
        if (viewWeakReference != null) {
            return viewWeakReference.get();
        }
        return null;
    }

    @Override
    public void fetchProductQuestions(TaskDetail taskDetail) {
        interactor.fetchQuestions(taskDetail, this);
    }

    @Override
    public void startFlagProblemForm(StructureDetail structureDetail, TaskDetail taskDetail, String formName) {
        interactor.startFlagProblemForm(structureDetail, taskDetail, formName, getView().getActivity(), this);
    }

    @Override
    public void markProductAsGood(StructureDetail structureDetail, TaskDetail taskDetail) {
        interactor.markProductAsGood(structureDetail, taskDetail, this, getView().getActivity());
    }

    @Override
    public void onQuestionsFetched(List<ProductInfoQuestion> productInfoQuestions) {
        if (getView().getAdapter() != null) {
            getView().getAdapter().setData(productInfoQuestions);
        }
    }

    @Override
    public void onProductMarkedAsGood(boolean isMarked, Event event) {
        if (getView() != null) {
            getView().getActivity().finish();
            if (isMarked && event != null) {
                Map<String, String> map = event.getDetails();
                if (map != null) {
                    String taskId = map.get(AppConstants.EventDetailKey.TASK_ID);
                    if (StringUtils.isNotBlank(taskId)) {
                        //TODO to be replaced by event submission
                        AppTaskRepository taskRepository = EusmApplication.getInstance().getAppTaskRepository();
                        taskRepository.updateTaskStatus(taskId, Task.TaskStatus.COMPLETED, "VISITED");
                    }
                }
            }
        }
    }

    @Override
    public void onFlagProblemFormFetched(JSONObject jsonForm) {
        getView().startFlagProblemForm(jsonForm);
    }

}
