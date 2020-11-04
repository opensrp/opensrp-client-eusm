package org.smartregister.eusm.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.eusm.R;
import org.smartregister.eusm.adapter.ViewPagerAdapter;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.ServicePointType;
import org.smartregister.eusm.contract.TaskRegisterActivityContract;
import org.smartregister.eusm.fragment.TasksRegisterFragment;
import org.smartregister.eusm.model.StructureDetail;
import org.smartregister.eusm.presenter.TaskRegisterActivityPresenter;
import org.smartregister.eusm.util.AppConstants;

public class TaskRegisterActivity extends BaseAppProfileActivity implements TaskRegisterActivityContract.View {

    private StructureDetail structureDetail = new StructureDetail();

    private View gpsUnknownView;

    private TextView txtProfileBack;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_task_register;
    }

    @Override
    protected void initializePresenter() {
        presenter = new TaskRegisterActivityPresenter(this);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        gpsUnknownView = findViewById(R.id.gps_unknown_view);

        fetchProfileData();

        TextView txtStructureName = findViewById(R.id.txt_service_point_name);
        txtStructureName.setText(getStructureName());

        TextView txtStructureType = findViewById(R.id.txt_service_point_type);
        txtStructureType.setText(getStructureType());

        TextView txtStructureCommune = findViewById(R.id.txt_service_point_commune);
        txtStructureCommune.setText(getCommune());

        ImageView imgProfileBack = findViewById(R.id.img_profile_back);
        imgProfileBack.setOnClickListener(this);

        txtProfileBack = findViewById(R.id.txt_profile_back);
        txtProfileBack.setOnClickListener(this);

        ImageView imgServicePointType = findViewById(R.id.img_service_point_type);

        setServicePointIcon(imgServicePointType);
    }

    protected void setServicePointIcon(ImageView imgServicePointType) {
        if (getStructureDetail() != null && StringUtils.isNotBlank(getStructureDetail().getStructureType())) {
            ServicePointType servicePointType = EusmApplication.getInstance().getServicePointKeyToType().get(getStructureDetail().getStructureType().toLowerCase().replaceAll(" ", "_"));
            if (servicePointType != null) {
                imgServicePointType.setImageDrawable(ResourcesCompat.getDrawable(getResources(), servicePointType.drawableId, getBaseContext().getTheme()));
                imgServicePointType.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
                imgServicePointType.setAlpha(0.6F);
            }
        }
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        TasksRegisterFragment tasksRegisterFragment = getStructureTasksRegisterFragment();
        adapter.addFragment(tasksRegisterFragment, "dummy");
        viewPager.setAdapter(adapter);
        return viewPager;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.img_profile_back) {
            finish();
        } else if (id == R.id.txt_profile_back) {
            finish();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        if (appBarTitleIsShown) {
            txtProfileBack.setVisibility(View.GONE);
        } else {
            txtProfileBack.setVisibility(View.VISIBLE);
        }
    }

    public TasksRegisterFragment getStructureTasksRegisterFragment() {
        return TasksRegisterFragment.newInstance(getIntent().getExtras());
    }

    public StructureDetail getStructureDetail() {
        return structureDetail;
    }


    @Override
    protected void fetchProfileData() {
        structureDetail = (StructureDetail) getIntent().getSerializableExtra(AppConstants.IntentData.STRUCTURE_DETAIL);
    }

    @Override
    public String getStructureIcon() {
        // TODO: condition for image
        return structureDetail.getStructureType();
    }

    @Override
    public String getStructureName() {
        return structureDetail.getStructureName();
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
        // TODO: update commune
        return "Alarobia Ambatomanga";
    }

    @Override
    public TaskRegisterActivityContract.Presenter presenter() {
        return (TaskRegisterActivityContract.Presenter) presenter;
    }

    @Override
    protected String getToolBarLayoutTitleAfterCollapse() {
        return getStructureName();
    }

    @Override
    protected boolean shouldEnableDisplayHomeAsUpEnabled() {
        return false;
    }
}