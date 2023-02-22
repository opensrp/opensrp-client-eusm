package org.smartregister.eusm.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.ProductInfoActivity;
import org.smartregister.eusm.adapter.EusmTaskRegisterAdapter;
import org.smartregister.eusm.contract.TaskRegisterFragmentContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.presenter.TaskRegisterFragmentPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class EusmTasksRegisterFragment extends BaseRegisterFragment implements TaskRegisterFragmentContract.View, View.OnClickListener {

    private EusmTaskRegisterAdapter eusmTaskRegisterAdapter;

    private TaskRegisterFragmentContract.Presenter presenter;

    private StructureDetail structureDetail;

    public static EusmTasksRegisterFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        EusmTasksRegisterFragment fragment = new EusmTasksRegisterFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    protected void retrieveStructureDetail() {
        structureDetail = (StructureDetail) getArguments().getSerializable(AppConstants.IntentData.STRUCTURE_DETAIL);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveStructureDetail();
        initializePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_task_register, container, false);
        clientsView = fragmentView.findViewById(R.id.recycler_view);
        clientsProgressView = fragmentView.findViewById(R.id.client_list_progress);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        clientsView.setLayoutManager(linearLayoutManager);

        eusmTaskRegisterAdapter = new EusmTaskRegisterAdapter(getContext(), this);
        clientsView.setAdapter(eusmTaskRegisterAdapter);
        return fragmentView;
    }

    @Override
    public void showProgressView() {
        if (clientsProgressView != null) {
            clientsProgressView.setVisibility(!clientsProgressView.isShown() ? VISIBLE : INVISIBLE);
        }
    }

    @Override
    public void initializePresenter() {
        presenter = new TaskRegisterFragmentPresenter(this);
    }

    @Override
    public void setUniqueID(String s) {
        //do nothing
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //do nothing
    }

    @Override
    protected void renderView() {
        //do nothing
    }

    @Override
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.task_register_row) {
            TaskDetail taskDetail = (TaskDetail) view.getTag(R.id.task_detail);
            if (taskDetail.isChecked()) {
                openUndoDialog(taskDetail);
            } else {
                if (!taskDetail.isNonProductTask()) {
                    if (AppConstants.EncounterType.FIX_PROBLEM.equals(taskDetail.getTaskCode())) {
                        presenter.startForm(structureDetail, taskDetail, getFixProblemForm());
                    } else {
                        Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
                        intent.putExtra(AppConstants.IntentData.TASK_DETAIL, taskDetail);
                        intent.putExtra(AppConstants.IntentData.STRUCTURE_DETAIL, structureDetail);
                        getActivity().startActivity(intent);
                    }
                } else {
                    if (AppConstants.NonProductTasks.SERVICE_POINT_CHECK.equalsIgnoreCase(taskDetail.getEntityName().trim())) {
                        presenter.startForm(structureDetail, taskDetail, getServicePointCheckForm());
                    } else if (AppConstants.NonProductTasks.RECORD_GPS.equalsIgnoreCase(taskDetail.getEntityName().trim())) {
                        presenter.startForm(structureDetail, taskDetail, getRecordGpsForm());
                    } else if (AppConstants.NonProductTasks.CONSULT_BENEFICIARIES.equalsIgnoreCase(taskDetail.getEntityName().trim())) {
                        presenter.startForm(structureDetail, taskDetail, getBeneficiaryConsultationForm());
                    } else if (AppConstants.NonProductTasks.WAREHOUSE_CHECK.equalsIgnoreCase(taskDetail.getEntityName().trim())) {
                        presenter.startForm(structureDetail, taskDetail, getWarehouseCheckForm());
                    } else if (AppConstants.TaskCode.FIX_PROBLEM_CONSULT_BENEFICIARIES.equalsIgnoreCase(taskDetail.getTaskCode())) {
                        presenter.startForm(structureDetail, taskDetail, getFixProblemForm());
                    }
                }
            }
        }
    }

    public String getFixProblemForm() {
        return AppConstants.JsonForm.FIX_PROBLEM_FORM;
    }

    public String getServicePointCheckForm() {
        return AppConstants.JsonForm.SERVICE_POINT_CHECK_FORM;
    }

    public String getRecordGpsForm() {
        return AppConstants.JsonForm.RECORD_GPS_FORM;
    }

    public String getBeneficiaryConsultationForm() {
        return AppConstants.JsonForm.BENEFICIARY_CONSULTATION_FORM;
    }

   public String getWarehouseCheckForm() {
        return AppConstants.JsonForm.WAREHOUSE_CHECK_FORM;
    }

    @VisibleForTesting
    protected void openUndoDialog(TaskDetail taskDetail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String alertMessage;
        alertMessage = taskDetail.isNonProductTask() ? String.format(getString(R.string.undo_task_title), taskDetail.getEntityName())
                : AppConstants.BusinessStatus.HAS_PROBLEM.equals(taskDetail.getBusinessStatus()) ? String.format(getString(R.string.undo_flag_problem_title), taskDetail.getEntityName())
                : String.format(getString(R.string.undo_looks_good_title), taskDetail.getEntityName());
        builder.setTitle(alertMessage);
        builder.setMessage(R.string.undo_dialog_message);
        builder.setPositiveButton(R.string.undo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.undoTask(taskDetail);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });
        builder.show();
    }

    @Override
    public void onResumption() {
        if (presenter() != null) {
            presenter().fetchData();
        }
    }

    @Override
    protected String getMainCondition() {
        return null;
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {
        //Do nothing
    }

    protected TaskRegisterFragmentPresenter presenter() {
        return (TaskRegisterFragmentPresenter) presenter;
    }

    @Override
    public EusmTaskRegisterAdapter getAdapter() {
        return eusmTaskRegisterAdapter;
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Form form = new Form();
        form.setWizard(false);
        form.setName("");
        form.setBackIcon(R.drawable.ic_action_close);
        form.setSaveLabel(getString(R.string.save));
        form.setActionBarBackground(R.color.primaryDark);
        form.setNavigationBackground(R.color.primaryDark);

        Intent intent = new Intent(getActivity(), JsonWizardFormActivity.class);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm.toString());
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION, true);
        getActivity().startActivityForResult(intent, AppConstants.RequestCode.REQUEST_CODE_GET_JSON);
    }

    @Override
    public StructureDetail getStructureDetail() {
        return structureDetail;
    }

    @Override
    public void onClick(View v) {
        onViewClicked(v);
    }

    @Override
    public void showNotFoundPopup(String s) {
        //do nothing
    }
}
