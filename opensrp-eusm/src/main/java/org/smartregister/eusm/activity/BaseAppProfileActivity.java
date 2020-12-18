package org.smartregister.eusm.activity;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.smartregister.eusm.R;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;

public abstract class BaseAppProfileActivity extends BaseProfileActivity {

    protected boolean appBarTitleIsShown = true;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private int appBarLayoutScrollRange = -1;

    @Override
    protected void onCreation() {
        setContentView(getLayoutId());

        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(shouldEnableDisplayHomeAsUpEnabled());
        }

        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);

        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsing_toolbar_layout);

        appBarLayout.addOnOffsetChangedListener(this);

        imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();

        setupViews();
    }

    protected boolean shouldEnableDisplayHomeAsUpEnabled() {
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (appBarLayoutScrollRange == -1) {
            appBarLayoutScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (appBarLayoutScrollRange + verticalOffset == 0) {
            collapsingToolbarLayout.setTitle(getToolBarLayoutTitleAfterCollapse());
            appBarTitleIsShown = true;
        } else if (appBarTitleIsShown) {
            collapsingToolbarLayout.setTitle(" ");
            appBarTitleIsShown = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void initializePresenter() {
        //Do nothing
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        //Do nothing
    }

    protected int getLayoutId() {
        return R.layout.activity_base_profile;
    }

    protected String getToolBarLayoutTitleAfterCollapse() {
        return " ";
    }
}
