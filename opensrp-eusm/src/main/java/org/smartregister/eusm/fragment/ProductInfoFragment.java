package org.smartregister.eusm.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import org.smartregister.eusm.model.StructureTaskDetail;
import org.smartregister.eusm.presenter.ProductInfoFragmentPresenter;
import org.smartregister.eusm.util.AppConstants;

public class ProductInfoFragment extends Fragment implements ProductInfoFragmentContract.View, View.OnClickListener {

    private StructureTaskDetail structureTaskDetail;

    private ProductInfoQuestionsAdapter productInfoQuestionsAdapter;

    private ProductInfoFragmentContract.Presenter presenter;

    public static ProductInfoFragment newInstance(Bundle bundle) {
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        structureTaskDetail = (StructureTaskDetail) getArguments().getSerializable(AppConstants.IntentData.STRUCTURE_TASK_DETAIL);
        initializeAdapter();
        initializePresenter();
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
        presenter.fetchProductQuestions();
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
        form.setWizard(false);
        form.setName("");
        form.setBackIcon(R.drawable.ic_action_close);
        form.setSaveLabel(getString(R.string.save));
        form.setActionBarBackground(R.color.primaryDark);
        form.setNavigationBackground(R.color.primaryDark);

        Intent intent = new Intent(getActivity(), JsonWizardFormActivity.class);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm.toString());
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, 20);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_product_looks_good) {
            openLooksGoodConfirmationDialog();
        } else if (id == R.id.layout_flag_problem) {
            startFlagProblemForm(structureTaskDetail);
        }
    }

    protected void startFlagProblemForm(StructureTaskDetail structureTaskDetail) {
        presenter.startFlagProblemForm(AppConstants.JsonForm.FLAG_PROBLEM);
    }

    protected void openLooksGoodConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.mark_as_looks_good_title);
        builder.setMessage(R.string.looks_good_dialog_message);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.markProductAsGood(structureTaskDetail);
            }
        });

        builder.setPositiveButton("Looks Good!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO add logic to update
            }
        });

        builder.show();
    }
}