package org.smartregister.eusm.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.vijay.jsonwizard.customviews.TreeViewDialog;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.CoreLibrary;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.OfflineMapsActivity;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.contract.BaseDrawerContract;
import org.smartregister.eusm.helper.GenericDrawerLayoutListener;
import org.smartregister.eusm.interactor.BaseDrawerInteractor;
import org.smartregister.eusm.presenter.BaseNavigationDrawerPresenter;
import org.smartregister.eusm.util.AlertDialogUtils;
import org.smartregister.eusm.util.Utils;
import org.smartregister.util.LangUtils;
import org.smartregister.util.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class NavigationDrawerView implements View.OnClickListener, BaseDrawerContract.View {

    private final BaseDrawerContract.Presenter presenter;
    private final BaseDrawerContract.DrawerActivity activity;
    private final BaseDrawerContract.Interactor interactor;
    private TextView planTextView;
    private TextView operationalAreaTextView;
    private TextView operatorTextView;
    private TextView languageChooserTextView;
    private DrawerLayout mDrawerLayout;

    public NavigationDrawerView(BaseDrawerContract.DrawerActivity activity) {
        this.activity = activity;
        presenter = new BaseNavigationDrawerPresenter(this);
        interactor = new BaseDrawerInteractor(presenter);
    }

    @Override
    public void initializeDrawerLayout() {
        setUpViews();
        checkSynced();
    }

    protected void setUpViews() {
        mDrawerLayout = getContext().findViewById(R.id.drawer_layout);

        mDrawerLayout.addDrawerListener(new GenericDrawerLayoutListener() {
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                presenter.onDrawerClosed();
            }
        });

        NavigationView navigationView = getContext().findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        try {
//            String manifestVersion = getManifestVersion();
            String appVersion = getContext().getString(R.string.app_version_without_build_number, Utils.getVersion(getContext()));
//            String appVersionText = appVersion + (manifestVersion == null ? "" : getContext().getString(R.string.manifest_version_parenthesis_placeholder, manifestVersion));
            ((TextView) headerView.findViewById(R.id.application_version))
                    .setText(appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }


        String buildDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(BuildConfig.BUILD_TIMESTAMP));

        ((TextView) headerView.findViewById(R.id.application_updated)).setText(getContext().getString(R.string.app_updated, buildDate));

        planTextView = headerView.findViewById(R.id.plan_selector);

        operationalAreaTextView = headerView.findViewById(R.id.operational_area_selector);

        operatorTextView = getContext().findViewById(R.id.operator_label);

        TextView offlineMapTextView = headerView.findViewById(R.id.btn_navMenu_offline_maps);

        languageChooserTextView = headerView.findViewById(R.id.btn_navMenu_language_chooser);

        String language = LangUtils.getLanguage(getContext());

        languageChooserTextView.setText(StringUtils.capitalize(String.format(getString(R.string.language_s), StringUtils.isNotBlank(language) ? getLanguageMap().get(language) : getString(R.string.english_lang))));

        languageChooserTextView.setOnClickListener(this);

        operationalAreaTextView.setOnClickListener(this);

        planTextView.setOnClickListener(this);

        offlineMapTextView.setVisibility(View.VISIBLE);

        offlineMapTextView.setOnClickListener(this);

        getContext().findViewById(R.id.logout_button).setOnClickListener(this);

        headerView.findViewById(R.id.sync_button).setOnClickListener(this);

        headerView.findViewById(R.id.btn_navMenu_offline_maps).setOnClickListener(this);
    }

    private Map<String, String> getLanguageMap() {
        Map<String, String> langMap = new HashMap<>();
        langMap.put("en", getString(R.string.english_lang));
        langMap.put("fr", getString(R.string.french_lang));
        return langMap;
    }

    @Override
    public String getPlan() {
        return planTextView.getText().toString();
    }

    @Override
    public void setPlan(String campaign) {
        planTextView.setText(campaign);
    }

    @Override
    public String getOperationalArea() {
        return operationalAreaTextView.getText().toString();
    }

    @Override
    public void setOperationalArea(String operationalArea) {
        operationalAreaTextView.setText(operationalArea);
    }

    private String getString(int resId) {
        return getContext().getString(resId);
    }

    @Override
    public void setDistrict(String district) {
        //do nothing
    }

    @Override
    public void setFacility(String facility, String facilityLevel) {
        //do nothing
    }

    @Override
    public void setOperator() {
        operatorTextView.setText(
                String.format(getContext().getString(R.string.operator_with_middle_dot),
                        EusmApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM()));
    }

    @Override
    public void lockNavigationDrawerForSelection() {
        mDrawerLayout.openDrawer(GravityCompat.START);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }

    @Override
    public void lockNavigationDrawerForSelection(int title, int message) {
        AlertDialogUtils.displayNotification(getContext(), title, message);
        mDrawerLayout.openDrawer(GravityCompat.START);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }

    @Override
    public void unlockNavigationDrawer() {
        if (mDrawerLayout.getDrawerLockMode(GravityCompat.START) == DrawerLayout.LOCK_MODE_LOCKED_OPEN) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void showOperationalAreaSelector(Pair<String, ArrayList<String>> locationHierarchy) {
        try {
            TreeViewDialog treeViewDialog = new TreeViewDialog(getContext(),
                    R.style.AppTheme_WideDialog,
                    new JSONArray(locationHierarchy.first), locationHierarchy.second, locationHierarchy.second);
            treeViewDialog.setCancelable(true);
            treeViewDialog.setCanceledOnTouchOutside(true);
            treeViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    presenter.onOperationalAreaSelectorClicked(treeViewDialog.getName());
                }
            });
            treeViewDialog.show();
        } catch (JSONException e) {
            Timber.e(e);
        }

    }

    @Override
    public void showPlanSelector(List<String> campaigns, String entireTreeString) {
        if (StringUtils.isBlank(entireTreeString)) {
            displayNotification(R.string.plans_download_on_progress_title, R.string.plans_download_on_progress);
            return;
        }
        try {
            TreeViewDialog treeViewDialog = new TreeViewDialog(getContext(),
                    R.style.AppTheme_WideDialog,
                    new JSONArray(entireTreeString), new ArrayList<>(campaigns), new ArrayList<>(campaigns));
            treeViewDialog.show();
            treeViewDialog.setCanceledOnTouchOutside(true);
            treeViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    presenter.onPlanSelectorClicked(treeViewDialog.getValue(), treeViewDialog.getName());
                }
            });
            treeViewDialog.show();
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public void displayNotification(int title, int message, Object... formatArgs) {
        AlertDialogUtils.displayNotification(getContext(), title, message, formatArgs);
    }

    @Override
    public Activity getContext() {
        return activity.getActivity();
    }

    @Override
    public void openDrawerLayout() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawerLayout() {
        if (presenter.isPlanAndOperationalAreaSelected()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public BaseDrawerContract.DrawerActivity getActivity() {
        return activity;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.operational_area_selector)
            presenter.onShowOperationalAreaSelector();
        else if (v.getId() == R.id.plan_selector)
            presenter.onShowPlanSelector();
        else if (v.getId() == R.id.logout_button)
            EusmApplication.getInstance().logoutCurrentUser();
        else if (v.getId() == R.id.btn_navMenu_offline_maps)
            presenter.onShowOfflineMaps();
        else if (v.getId() == R.id.btn_navMenu_language_chooser)
            showLanguageChooser();
        else if (v.getId() == R.id.sync_button) {
            toggleProgressBarView(true);
            org.smartregister.eusm.util.Utils.startImmediateSync();
            closeDrawerLayout();
        }
    }

    private void showLanguageChooser() {
        PopupMenu popupMenu = new PopupMenu(getContext(), languageChooserTextView, Gravity.TOP);
        popupMenu.inflate(R.menu.menu_language_chooser);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            Locale LOCALE = Locale.ENGLISH;
            if (itemId == R.id.menu_lang_chooser_french) {
                LOCALE = Locale.FRENCH;
            }
            LangUtils.saveLanguage(getContext(), LOCALE.getLanguage());
            Intent intent = new Intent(getContext(), getContext().getClass());
            getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            return false;
        });
    }

    @Override
    public BaseDrawerContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void onResume() {
        presenter.onViewResumed();
    }

    @Override
    public void openOfflineMapsView() {
        Intent intent = new Intent(getContext(), OfflineMapsActivity.class);
        getContext().startActivity(intent);
    }

    @Override
    public void checkSynced() {
        interactor.checkSynced();
    }

    @Override
    public void toggleProgressBarView(boolean syncing) {
        ProgressBar progressBar = getContext().findViewById(R.id.sync_progress_bar);
        TextView progressLabel = this.activity.getActivity().findViewById(R.id.sync_progress_bar_label);
        TextView syncButton = this.activity.getActivity().findViewById(R.id.sync_button);
        TextView syncBadge = this.activity.getActivity().findViewById(R.id.sync_label);
        if (progressBar == null || syncBadge == null)
            return;
        //only hide the sync button when there is internet connection
        if (syncing && NetworkUtils.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            progressLabel.setVisibility(View.VISIBLE);
            syncButton.setVisibility(View.INVISIBLE);
            syncBadge.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            progressLabel.setVisibility(View.INVISIBLE);
            syncButton.setVisibility(View.VISIBLE);
            syncBadge.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public String getManifestVersion() {
        return CoreLibrary.getInstance().context().allSharedPreferences().fetchManifestVersion();
    }
}
