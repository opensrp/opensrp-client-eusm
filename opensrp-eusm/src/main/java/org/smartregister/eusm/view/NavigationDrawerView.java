package org.smartregister.eusm.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.eusm.BuildConfig;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.presenter.EusmBaseDrawerPresenter;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.tasking.view.DrawerMenuView;
import org.smartregister.util.LangUtils;
import org.smartregister.util.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import timber.log.Timber;

public class NavigationDrawerView extends DrawerMenuView {

    private TextView languageChooserTextView;

    public NavigationDrawerView(BaseDrawerContract.DrawerActivity activity) {
        super(activity);
    }

    @Override
    public void setUpViews() {
        super.setUpViews();

        String buildDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(BuildConfig.BUILD_TIMESTAMP));

        ((TextView) getContext().findViewById(R.id.application_updated)).setText(getContext().getString(R.string.app_updated, buildDate));

        languageChooserTextView = getContext().findViewById(R.id.btn_navMenu_language_chooser);

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
            startImmediateSync();
            closeDrawerLayout();
        }
    }

    protected void startImmediateSync() {
        AppUtils.startImmediateSync();
    }

    protected void showLanguageChooser() {
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
        if (presenter == null) {
            presenter = new EusmBaseDrawerPresenter(this, this.activity);
        }
        return presenter;
    }

    @Override
    public void showOperationalAreaSelector(Pair<String, ArrayList<String>> locationHierarchy) {
        try {

            EusmTreeViewDialog treeViewDialog = new EusmTreeViewDialog(getContext(),
                    R.style.AppTheme_WideDialog,
                    new JSONArray(locationHierarchy.first), locationHierarchy.second, new ArrayList<>(), true);
            treeViewDialog.setCancelable(true);
            treeViewDialog.setCanceledOnTouchOutside(true);
            treeViewDialog.setShouldDisableOnClickListener(true);


            LinearLayout linearLayout = treeViewDialog.getCanvas();

            LinearLayout buttonLayout = new LinearLayout(getContext());
            buttonLayout.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams treeViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            LinearLayout treeViewLayout = new LinearLayout(getContext());
            treeViewLayout.setOrientation(LinearLayout.VERTICAL);
            treeViewLayout.setGravity(Gravity.CENTER);
            treeViewLayout.setLayoutParams(treeViewLayoutParams);


            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Button okButton = new Button(getContext());
            okButton.setLayoutParams(buttonLayoutParams);
            okButton.setText(R.string.ok_text);
            okButton.setTypeface(Typeface.DEFAULT_BOLD);
            okButton.setTextColor(getContext().getResources().getColor(R.color.white));
            okButton.setBackgroundResource(com.vijay.jsonwizard.R.drawable.btn_bg);
            okButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources()
                    .getDimension(com.vijay.jsonwizard.R.dimen.button_text_size));
            okButton.setHeight(getContext().getResources().getDimensionPixelSize(com.vijay.jsonwizard.R.dimen.button_height));
            buttonLayout.addView(okButton);

            ScrollView scrollView = (ScrollView) linearLayout.getChildAt(0);
            View androidTreeView = scrollView.getChildAt(0);
            scrollView.removeView(androidTreeView);
            treeViewLayout.addView(androidTreeView);
            treeViewLayout.addView(buttonLayout);
            scrollView.addView(treeViewLayout);
            linearLayout.removeView(scrollView);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(scrollView);

            okButton.setOnClickListener(v -> {
                List<String> selectedValues = treeViewDialog.getTreeView().getSelectedValues(String.class);
                getPresenter().onOperationalAreaSelectorClicked(new ArrayList<>(selectedValues));
                treeViewDialog.cancel();
            });
            treeViewDialog.show();
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        super.onDrawerOpened(drawerView);
        if (operationalAreaTextView != null) {
            String opAreas = Optional.of(operationalAreaTextView.getText().toString()).orElse("");
            String setOpAres = PreferencesUtil.getInstance().getCurrentOperationalArea();
            if (!StringUtils.isBlank(setOpAres) && !opAreas.equals(setOpAres)) {
                getPresenter().onOperationalAreaSelectorClicked(new ArrayList<>(Arrays.asList(setOpAres.split(PreferencesUtil.OPERATIONAL_AREA_SEPARATOR))));
            }
        }
    }

    @Override
    public void toggleProgressBarView(boolean syncing) {
        super.toggleProgressBarView(syncing);
        View flexBox = this.activity.getActivity().findViewById(R.id.flex_box);
        flexBox.setVisibility((syncing && NetworkUtils.isNetworkAvailable()) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setOperationalArea(String operationalArea) {
        try {
            super.setOperationalArea(getRegionsFromDistricts(operationalArea));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getRegionsFromDistricts(String districts) throws Exception{
        JSONArray locationHierarchy = new JSONArray(((EusmBaseDrawerPresenter) getPresenter()).extractLocationHierarchy().first);
        Set<String> operationalAreas  = new HashSet<>(Arrays.asList(districts.split(",")));
        ArrayList<String> opRegions = AppUtils.getRegionsForDistricts(
                locationHierarchy,
                operationalAreas,
                false
        );
        return opRegions.toString();
    }
}
