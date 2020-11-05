package org.smartregister.eusm.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.ProductInfoActivityContract;
import org.smartregister.eusm.fragment.ProductInfoFragment;
import org.smartregister.eusm.model.StructureTaskDetail;
import org.smartregister.eusm.presenter.ProductInfoActivityPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.view.activity.MultiLanguageActivity;

import timber.log.Timber;

public class ProductInfoActivity extends MultiLanguageActivity implements ProductInfoActivityContract.View, View.OnClickListener {

    private StructureTaskDetail structureTaskDetail;

    private ProductInfoActivityPresenter presenter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        structureTaskDetail = (StructureTaskDetail) getIntent().getSerializableExtra(AppConstants.IntentData.STRUCTURE_TASK_DETAIL);
        presenter = new ProductInfoActivityPresenter(this);
        initializeFragment();
        setUpViews();
    }

    protected int getLayoutId() {
        return R.layout.activity_product_info;
    }

    @Override
    public String getProductName() {
        return structureTaskDetail.getProductName();
    }

    @Override
    public String getProductSerial() {
        if (StringUtils.isNotBlank(structureTaskDetail.getProductSerial())) {
            return String.format(getString(R.string.product_serial), structureTaskDetail.getProductSerial());
        } else {
            return " ";
        }
    }

    @Override
    public String getProductImage() {
        return "";
    }

    @Override
    public void setUpViews() {
        TextView txtProductName = findViewById(R.id.txt_product_name);
        txtProductName.setText(getProductName());

        TextView txtProductSerial = findViewById(R.id.txt_product_serial);
        txtProductSerial.setText(getProductSerial());

        ImageView imgProductImage = findViewById(R.id.img_product_image);

        ImageView imgBackButton = findViewById(R.id.img_profile_back);
        imgBackButton.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.customAppThemeBlue));

        View layoutBackButton = findViewById(R.id.layout_back_button);
        layoutBackButton.setOnClickListener(this);
    }

    protected ProductInfoFragment getProductInfoFragment() {
        return ProductInfoFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
                Timber.d("JSON Result : %s", jsonString);

                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormConstants.ENCOUNTER_TYPE);

                if (encounterType.equals(AppConstants.JsonForm.FLAG_PROBLEM)) {
                    //showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveFlagProblemTask(encounterType, data);
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
        transaction.commit();
    }

    @Override
    public ProductInfoActivityContract.Presenter presenter() {
        return presenter;
    }

    @Override
    public void showDialog(String message) {
        if (progressDialog != null && !progressDialog.isShowing() && !this.isFinishing()) {
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
        }
    }
}