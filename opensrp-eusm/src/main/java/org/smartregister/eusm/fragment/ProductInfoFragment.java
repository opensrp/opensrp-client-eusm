package org.smartregister.eusm.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.adapter.ProductInfoQuestionsAdapter;
import org.smartregister.eusm.contract.ProductInfoFragmentContract;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.model.TaskDetail;
import org.smartregister.eusm.presenter.ProductInfoFragmentPresenter;
import org.smartregister.eusm.util.AppConstants;

public class ProductInfoFragment extends Fragment implements ProductInfoFragmentContract.View, View.OnClickListener {

    private TaskDetail taskDetail;

    private StructureDetail structureDetail;

    private ProductInfoQuestionsAdapter productInfoQuestionsAdapter;

    private ProductInfoFragmentContract.Presenter presenter;

    private ProgressDialog progressDialog;

    public static ProductInfoFragment newInstance(Bundle bundle) {
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskDetail = (TaskDetail) getArguments().getSerializable(AppConstants.IntentData.TASK_DETAIL);
        structureDetail = (StructureDetail) getArguments().getSerializable(AppConstants.IntentData.STRUCTURE_DETAIL);
        initializeAdapter();
        initializePresenter();
    }

    @Override
    public void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);
    }

    @Override
    public void showProgressDialog(@StringRes int message) {
        if (progressDialog != null && !getActivity().isFinishing()) {
            progressDialog.setTitle(getString(message));
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        Button btnLooksGood = view.findViewById(R.id.btn_product_looks_good);
        btnLooksGood.setOnClickListener(this);

        View flagProblemView = view.findViewById(R.id.layout_flag_problem);
        flagProblemView.setOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(productInfoQuestionsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.fetchProductQuestions(taskDetail);
    }

    protected int getLayoutId() {
        return R.layout.fragment_product_info;
    }

    @Override
    public void initializeAdapter() {
        productInfoQuestionsAdapter = new ProductInfoQuestionsAdapter(this);
    }

    @Override
    public ProductInfoQuestionsAdapter getAdapter() {
        return productInfoQuestionsAdapter;
    }

    @Override
    public void initializePresenter() {
        presenter = new ProductInfoFragmentPresenter(this);
    }

    @Override
    public void startFlagProblemForm(JSONObject jsonForm) {
        Form form = new Form();
        form.setWizard(true);
        form.setName("Flag Problem Form");
        form.setBackIcon(R.drawable.ic_action_close);
        form.setSaveLabel(getString(R.string.save));
        form.setActionBarBackground(R.color.primaryDark);
        form.setNavigationBackground(R.color.primaryDark);

        Intent intent = new Intent(getActivity(), JsonWizardFormActivity.class);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm.toString());
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        getActivity().startActivityForResult(intent, AppConstants.RequestCode.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_product_looks_good) {
            openLooksGoodConfirmationDialog();
        } else if (id == R.id.layout_flag_problem) {
            startFlagProblemForm();
        }
    }

    protected void startFlagProblemForm() {
        presenter.startFlagProblemForm(structureDetail,
                taskDetail,
                AppConstants.JsonForm.FLAG_PROBLEM_FORM);
    }

    protected void openLooksGoodConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.mark_as_looks_good_title);
        builder.setMessage(R.string.looks_good_dialog_message);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing
            }
        });

        builder.setPositiveButton("Looks Good!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO add logic to update
                presenter.markProductAsGood(structureDetail, taskDetail);

            }
        });

        builder.show();
    }
}