package org.smartregister.eusm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.ServicePointType;
import org.smartregister.eusm.contract.TaskRegisterActivityContract;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.fragment.EusmTasksRegisterFragment;
import org.smartregister.eusm.presenter.TaskRegisterActivityPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.stock.widget.StockDatePickerFactory;
import org.smartregister.tasking.activity.TaskRegisterActivity;
import org.smartregister.tasking.adapter.ViewPagerAdapter;
import org.smartregister.tasking.model.BaseTaskDetails;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Map;

import timber.log.Timber;

public class EusmTaskRegisterActivity extends TaskRegisterActivity implements TaskRegisterActivityContract.View, View.OnClickListener {

    private StructureDetail structureDetail = new StructureDetail();

    private View gpsUnknownView;

    @Override
    protected void onCreation() {
        //Do nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mPager = findViewById(R.id.base_view_pager);

        retrieveStructureDetail();

        setupViews();

        Fragment[] otherFragments = getOtherFragments();

        mBaseFragment = getRegisterFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(getRegisterFragment(), "");
        mPager.setOffscreenPageLimit(otherFragments.length);
        mPager.setAdapter(mPagerAdapter);

        initializePresenter();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return getTasksRegisterFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_task_register;
    }

    @Override
    protected void initializePresenter() {
        presenter = new TaskRegisterActivityPresenter(this);
    }

    @Override
    public void startFamilyRegistration(BaseTaskDetails taskDetails) {
        //Do nothing
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        gpsUnknownView = findViewById(R.id.gps_unknown_view);

        TextView txtStructureName = findViewById(R.id.txt_service_point_name);
        txtStructureName.setText(getStructureName());

        TextView txtStructureType = findViewById(R.id.txt_service_point_type);
        txtStructureType.setText(getStructureType());

        TextView txtStructureCommune = findViewById(R.id.txt_service_point_commune);
        txtStructureCommune.setText(getCommune());

        ImageView imgProfileBack = findViewById(R.id.img_profile_back);
        imgProfileBack.setOnClickListener(this);

        TextView txtProfileBack = findViewById(R.id.txt_profile_back);
        txtProfileBack.setOnClickListener(this);

        ImageView imgServicePointType = findViewById(R.id.img_service_point_type);

        setServicePointIcon(imgServicePointType);
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        //Do nothing
    }

    protected void setServicePointIcon(ImageView imgServicePointType) {
        if (getStructureDetail() != null && StringUtils.isNotBlank(getStructureDetail().getStructureType())) {
            ServicePointType servicePointType = EusmApplication.getInstance().getServicePointKeyToType().get(getStructureDetail().getStructureType().toLowerCase().replaceAll(" ", ""));
            if (servicePointType != null) {
                imgServicePointType.setImageDrawable(ResourcesCompat.getDrawable(getResources(), servicePointType.drawableId, getBaseContext().getTheme()));
                imgServicePointType.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
                imgServicePointType.setAlpha(0.6F);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.img_profile_back) {
            finish();
        } else if (id == R.id.txt_profile_back) {
            finish();
        }
    }

    public EusmTasksRegisterFragment getTasksRegisterFragment() {
        return EusmTasksRegisterFragment.newInstance(getIntent().getExtras());
    }

    public StructureDetail getStructureDetail() {
        return structureDetail;
    }

    protected void retrieveStructureDetail() {
        structureDetail = (StructureDetail) getIntent().getSerializableExtra(AppConstants.IntentData.STRUCTURE_DETAIL);
    }

    @Override
    public String getStructureIcon() {
        return structureDetail.getStructureType();
    }

    @Override
    public String getStructureName() {
        return structureDetail.getEntityName();
    }

    @Override
    public String getStructureType() {
        if (StringUtils.isNotBlank(getDistance())) {
            gpsUnknownView.setVisibility(View.GONE);
            return String.format(getString(R.string.distance_from_structure), structureDetail.getStructureType(), getDistance());
        } else {
            gpsUnknownView.setVisibility(View.VISIBLE);
            return String.format(getString(R.string.unlisted_distance_from_structure), structureDetail.getStructureType());
        }
    }

    @Override
    public String getDistance() {
        return structureDetail.getDistanceMeta();
    }

    @Override
    public String getCommune() {
        return structureDetail.getCommune();
    }

    @Override
    public TaskRegisterActivityContract.Presenter presenter() {
        return (TaskRegisterActivityContract.Presenter) presenter;
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (requestCode == AppConstants.RequestCode.REQUEST_CODE_GET_JSON)) {
            try {
                String jsonString = data.getStringExtra(JsonFormConstants.JSON_FORM_KEY.JSON);
                Timber.d("JSONResult : %s", jsonString);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.optString(JsonFormConstants.ENCOUNTER_TYPE);
                if (StringUtils.isNotBlank(encounterType)) {
                    showProgressDialog(R.string.saving_message);
                    presenter().saveForm(encounterType, form, structureDetail);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }
}