package org.smartregister.eusm.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.domain.TaskDetail;
import org.smartregister.eusm.fragment.ProductInfoFragment;
import org.smartregister.eusm.presenter.ProductInfoActivityPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.util.FileUtilities;
import org.smartregister.view.activity.MultiLanguageActivity;

import timber.log.Timber;

public class ProductInfoActivity extends MultiLanguageActivity implements ProductInfoActivityContract.View, View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    protected AppBarLayout appBarLayout;
    private TaskDetail taskDetail;
    private StructureDetail structureDetail;
    private ProductInfoActivityPresenter presenter;
    private ProgressDialog progressDialog;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        taskDetail = (TaskDetail) getIntent().getSerializableExtra(AppConstants.IntentData.TASK_DETAIL);
        structureDetail = (StructureDetail) getIntent().getSerializableExtra(AppConstants.IntentData.STRUCTURE_DETAIL);
        presenter = new ProductInfoActivityPresenter(this);
        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);
        collapsingToolbarLayout = appBarLayout.findViewById(org.smartregister.R.id.collapsing_toolbar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        initializeFragment();
        setUpViews();
    }

    protected int getLayoutId() {
        return R.layout.activity_product_info;
    }

    @Override
    public String getProductName() {
        return taskDetail.getEntityName();
    }

    @Override
    public String getProductSerial() {
        if (StringUtils.isNotBlank(taskDetail.getProductSerial())) {
            return String.format(getString(R.string.product_serial), taskDetail.getProductSerial());
        } else {
            return " ";
        }
    }

    @Override
    public String getProductImage() {
        return taskDetail.getProductImage();
    }

    @Override
    public void setUpViews() {
        TextView txtProductName = findViewById(R.id.txt_product_name);
        txtProductName.setText(getProductName());

        TextView txtProductSerial = findViewById(R.id.txt_product_serial);
        txtProductSerial.setText(getProductSerial());

        ImageView imgProductImage = findViewById(R.id.img_product_image);

        if (StringUtils.isNotBlank(getProductImage())) {
            Bitmap bitmap = FileUtilities.retrieveStaticImageFromDisk(getProductImage());
            imgProductImage.setImageBitmap(bitmap);
        } else {
            imgProductImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }


        ImageView imgBackButton = findViewById(R.id.img_profile_back);
        imgBackButton.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.customAppThemeBlue));

        View layoutBackButton = findViewById(R.id.layout_back_button);
        layoutBackButton.setOnClickListener(this);

        Button btnLooksGood = findViewById(R.id.btn_product_looks_good);
        btnLooksGood.setOnClickListener(this);

        View flagProblemView = findViewById(R.id.layout_flag_problem);
        flagProblemView.setOnClickListener(this);
    }

    protected ProductInfoFragment getProductInfoFragment() {
        return ProductInfoFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.RequestCode.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
                Timber.d("JSON Result : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormConstants.ENCOUNTER_TYPE);

                if (AppConstants.EncounterType.FLAG_PROBLEM.equals(encounterType)) {
                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveFlagProblemForm(taskDetail, encounterType, form, getStructureDetail());
                }

            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void initializeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, getProductInfoFragment());
        transaction.commitAllowingStateLoss();
    }

    @Override
    public ProductInfoActivityContract.Presenter presenter() {
        return presenter;
    }

    @Override
    public void showProgressDialog(@StringRes int message) {
        if (progressDialog != null && !progressDialog.isShowing() && !this.isFinishing()) {
            progressDialog.setTitle(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void initializeDialog() {
        if (progressDialog != null) {
            progressDialog = new ProgressDialog(getApplicationContext());
        }
    }

    @Override
    public ProgressDialog getDialog() {
        return progressDialog;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.layout_back_button) {
            finish();
        } else if (id == R.id.btn_product_looks_good) {
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

    @Override
    public TaskDetail getTaskDetail() {
        return taskDetail;
    }

    @Override
    public StructureDetail getStructureDetail() {
        return structureDetail;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (appBarLayoutScrollRange == -1) {
            appBarLayoutScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (appBarLayoutScrollRange + verticalOffset == 0) {

            collapsingToolbarLayout.setTitle("");
            appBarTitleIsShown = true;
        } else if (appBarTitleIsShown) {
            collapsingToolbarLayout.setTitle(" ");
            appBarTitleIsShown = false;
        }
    }

    @Override
    public void startFlagProblemForm(JSONObject jsonForm) {
        Form form = new Form();
        form.setWizard(true);
        form.setName("Flag Problem");
        form.setBackIcon(R.drawable.ic_action_close);
        form.setSaveLabel(getString(R.string.save));
        form.setActionBarBackground(R.color.primaryDark);
        form.setNavigationBackground(R.color.primaryDark);
        form.setHideSaveLabel(true);

        Intent intent = new Intent(getActivity(), AppJsonWizardFormActivity.class);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.JSON, jsonForm.toString());
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, AppConstants.RequestCode.REQUEST_CODE_GET_JSON);
    }

}