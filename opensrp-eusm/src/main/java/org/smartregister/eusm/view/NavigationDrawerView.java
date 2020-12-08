package org.smartregister.eusm.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.vijay.jsonwizard.customviews.TreeViewDialog;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.view.DrawerMenuView;
import org.smartregister.util.LangUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class NavigationDrawerView extends DrawerMenuView {

    private TextView languageChooserTextView;

    public NavigationDrawerView(BaseDrawerContract.DrawerActivity activity) {
        super(activity);
    }
//
//    @Override
//    public BaseDrawerContract.Interactor getInteractor() {
//        return interactor = new EusmBaseDrawerInteractor(presenter);
//    }

    @Override
    public void setUpViews(NavigationView navigationView) {
        super.setUpViews(navigationView);
        View headerView = navigationView.getHeaderView(0);

        String buildDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(BuildConfig.BUILD_TIMESTAMP));

        ((TextView) headerView.findViewById(R.id.application_updated)).setText(getContext().getString(R.string.app_updated, buildDate));

        languageChooserTextView = headerView.findViewById(R.id.btn_navMenu_language_chooser);

        String language = LangUtils.getLanguage(getContext());

        languageChooserTextView.setText(StringUtils.capitalize(String.format(getString(R.string.language_s), StringUtils.isNotBlank(language) ? getLanguageMap().get(language) : getString(R.string.english_lang))));

        languageChooserTextView.setOnClickListener(this);

        getContext().findViewById(R.id.logout_button).setOnClickListener(this);

        TextView poweredByTextView = getContext().findViewById(R.id.txt_powered_by);
        poweredByTextView.setText(String.format("%s OpenSRP", getString(R.string.powered_by)));
    }

    private Map<String, String> getLanguageMap() {
        Map<String, String> langMap = new HashMap<>();
        langMap.put("en", getString(R.string.english_lang));
        langMap.put("fr", getString(R.string.french_lang));
        return langMap;
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
                    getPresenter().onOperationalAreaSelectorClicked(treeViewDialog.getName());
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
                    getPresenter().onPlanSelectorClicked(treeViewDialog.getValue(), treeViewDialog.getName());
                }
            });
            treeViewDialog.show();
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public void closeDrawerLayout() {
        if (getPresenter().isPlanAndOperationalAreaSelected()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.operational_area_selector)
            getPresenter().onShowOperationalAreaSelector();
        else if (v.getId() == R.id.plan_selector)
            getPresenter().onShowPlanSelector();
        else if (v.getId() == R.id.logout_button)
            EusmApplication.getInstance().logoutCurrentUser();
        else if (v.getId() == R.id.btn_navMenu_offline_maps)
            getPresenter().onShowOfflineMaps();
        else if (v.getId() == R.id.btn_navMenu_language_chooser)
            showLanguageChooser();
        else if (v.getId() == R.id.sync_button) {
            toggleProgressBarView(true);
            AppUtils.startImmediateSync();
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
    public void lockNavigationDrawerForSelection() {

    }
}
