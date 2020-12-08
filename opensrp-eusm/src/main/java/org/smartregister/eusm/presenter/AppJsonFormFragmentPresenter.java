package org.smartregister.eusm.presenter;

import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.eusm.R;
import org.smartregister.eusm.activity.AppJsonFormActivity;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.tasking.contract.UserLocationContract;
import org.smartregister.tasking.presenter.ValidateUserLocationPresenter;
import org.smartregister.tasking.util.LocationUtils;
import org.smartregister.tasking.view.TaskingMapView;
import org.smartregister.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import io.ona.kujaku.listeners.BaseLocationListener;

/**
 * Created by samuelgithengi on 1/30/19.
 */
public class AppJsonFormFragmentPresenter extends JsonFormFragmentPresenter implements UserLocationContract.UserLocationCallback {

    private final JsonFormFragment formFragment;
    private final AppJsonFormActivity jsonFormView;
    private final LocationUtils locationUtils;
    private final BaseLocationListener locationListener;
    private final AppJsonFormUtils jsonFormUtils;
    private AlertDialog passwordDialog;
    private TaskingMapView mapView;
    private Location lastLocation;

    public AppJsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
        this.formFragment = formFragment;
        jsonFormView = (AppJsonFormActivity) formFragment.getActivity();
        locationUtils = new LocationUtils(jsonFormView);
        locationListener = new BaseLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;
            }
        };
        locationUtils.requestLocationUpdates(locationListener);
        jsonFormUtils = new AppJsonFormUtils();
    }

//    @Override
//    public void validateAndWriteValues() {
//        super.validateAndWriteValues();
//        for (View childAt : formFragment.getJsonApi().getFormDataViews()) {
//            if (childAt instanceof AppMapView) {
//                AppMapView mapView = (AppMapView) childAt;
//                ValidationStatus validationStatus = GeoWidgetFactory.validate(formFragment, mapView, this);
//                String key = (String) childAt.getTag(com.vijay.jsonwizard.R.id.key);
//                String mStepName = this.getView().getArguments().getString("stepName");
//                String fieldKey = mStepName + " (" + mStepDetails.optString("title") + ") :" + key;
//                if (!validationStatus.isValid()) {
//                    getInvalidFields().put(fieldKey, validationStatus);
//                } else {
//                    getInvalidFields().remove(fieldKey);
//                    if (isFormValid() && validateFarStructures()) {
//                        validateUserLocation(mapView);
//                        return;
//                    }
//                }
//                this.mapView = mapView;
//                break;//exit loop, assumption; there will be only 1 map per form.
//            } else if (childAt instanceof TextView && !(childAt instanceof MaterialEditText)) {
//                ValidationStatus validationStatus = AppToasterNotesFactory.validate(formFragment, (TextView) childAt);
//                String address = (String) childAt.getTag(com.vijay.jsonwizard.R.id.address);
//                if (!validationStatus.isValid()) {
//                    getInvalidFields().put(address, validationStatus);
//                } else {
//                    getInvalidFields().remove(address);
//                }
//            }
//        }
//        if (isFormValid()) {// if form is valid and did not have a map, if it had a map view it will be handled above
//            onLocationValidated();
//
//        } else {//if form is invalid whether having a map or not
//            if (showErrorsOnSubmit()) {
//                launchErrorDialog();
//                getView().showToast(getView().getContext().getResources().getString(R.string.json_form_error_msg, this.getInvalidFields().size()));
//            } else {
//                getView().showSnackBar(getView().getContext().getResources().getString(R.string.json_form_error_msg, this.getInvalidFields().size()));
//            }
//        }
//    }

    @VisibleForTesting
    protected boolean validateFarStructures() {
        return AppUtils.validateFarStructures();
    }


//    private void validateUserLocation(AppMapView mapView) {
//        this.mapView = mapView;
//        Location location = jsonFormView.getUserCurrentLocation();
//        if (location != null) {
//            locationPresenter.onGetUserLocation(location);
//        } else {
//            locationPresenter.requestUserLocation();
//        }
//    }

    @Override
    public void onSaveClick(LinearLayout mainView) {
        validateAndWriteValues();
        boolean isFormValid = isFormValid();
        if (isFormValid || Boolean.valueOf(mainView.getTag(com.vijay.jsonwizard.R.id.skip_validation).toString())) {
            Utils.removeGeneratedDynamicRules(formFragment);
            Intent returnIntent = new Intent();
            getView().onFormFinish();
            returnIntent.putExtra("json", getView().getCurrentJsonState());
            returnIntent.putExtra(JsonFormConstants.SKIP_VALIDATION,
                    Boolean.valueOf(mainView.getTag(com.vijay.jsonwizard.R.id.skip_validation).toString()));
            getView().finishWithResult(returnIntent);
        } else {
            if (showErrorsOnSubmit()) {
                launchErrorDialog();
                getView().showToast(getView().getContext().getResources()
                        .getString(com.vijay.jsonwizard.R.string.json_form_error_msg, getInvalidFields().size()));
            } else {
                getView().showSnackBar(getView().getContext().getResources()
                        .getString(com.vijay.jsonwizard.R.string.json_form_error_msg, getInvalidFields().size()));

            }
        }
    }

    @Override
    public void onLocationValidated() {
        jsonFormView.hideProgressDialog();
        Intent returnIntent = new Intent();
        getView().onFormFinish();
        returnIntent.putExtra("json", getView().getCurrentJsonState());
        Object skipValidation = formFragment.getMainView().getTag(com.vijay.jsonwizard.R.id.skip_validation);
        returnIntent.putExtra(JsonFormConstants.SKIP_VALIDATION, Boolean.valueOf(skipValidation == null ? Boolean.FALSE.toString() : skipValidation.toString()));
        getView().finishWithResult(returnIntent);

    }

    @Override
    public LatLng getTargetCoordinates() {
        return mapView.getMapboxMap().getCameraPosition().target;
    }

    @Override
    public void requestUserPassword() {
        if (passwordDialog != null) {
            passwordDialog.show();
        }
    }

    @Override
    public ValidateUserLocationPresenter getLocationPresenter() {
        return null;
    }

    public LocationUtils getLocationUtils() {
        return locationUtils;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public BaseLocationListener getLocationListener() {
        return locationListener;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        super.onItemSelected(parent, view, position, id);
        String key = (String) parent.getTag(R.id.key);
        Map<String, JSONObject> fields = jsonFormUtils.getFields(jsonFormView.getmJSONObject());
//        cascadeSelect(key, AppConstants.JsonForm.DATA_COLLECTOR, AppConstants.CONFIGURATION.SPRAY_OPERATORS, fields.get(AppConstants.JsonForm.SPRAY_OPERATOR_CODE));
//        cascadeSelect(key, AppConstants.JsonForm.HFC_BELONG, AppConstants.CONFIGURATION.COMMUNITY_HEALTH_WORKERS, fields.get(AppConstants.JsonForm.CHW_NAME));
//        cascadeSelect(key, AppConstants.JsonForm.CATCHMENT_AREA, AppConstants.CONFIGURATION.MDA_CORDINATORS, fields.get(AppConstants.JsonForm.COORDINATOR_NAME));
//        cascadeSelect(key, AppConstants.JsonForm.CATCHMENT_AREA, AppConstants.CONFIGURATION.MDA_ENUMERATORS, fields.get(AppConstants.JsonForm.DATA_COLLECTOR));
//        cascadeSelect(key, AppConstants.JsonForm.CATCHMENT_AREA, AppConstants.CONFIGURATION.MDA_COMMUNITY_HEALTH_WORKERS, fields.get(AppConstants.JsonForm.CHW_NAME));
//        cascadeSelect(key, AppConstants.JsonForm.CATCHMENT_AREA, AppConstants.CONFIGURATION.MDA_ADHERENCE_OFFICERS, fields.get(AppConstants.JsonForm.ADHERENCE_NAME));
    }

    private void cascadeSelect(String key, String parentWidget, String configurationKey, JSONObject childWidget) {
        if (parentWidget.equals(key)) {
            String value = JsonFormUtils.getFieldValue(getView().getCurrentJsonState(), key);
            if (!TextUtils.isEmpty(value)) {
                Pair<JSONArray, JSONArray> options = jsonFormUtils.populateServerOptions(EusmApplication.getInstance().getServerConfigs()
                        , configurationKey, childWidget, value.split(":")[0]);
                if (options != null) {
                    List<String> newAdapterValues = new Gson().fromJson(options.second.toString(), new TypeToken<List<String>>() {
                    }.getType());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getView().getContext(), R.layout.native_form_simple_list_item_1, newAdapterValues);
                    MaterialSpinner spinner = (MaterialSpinner) jsonFormView.getFormDataView(JsonFormConstants.STEP1 + ":" + childWidget.optString(JsonFormUtils.KEY));
                    if (spinner != null) {
                        Object selected;
                        if (spinner.getAdapter().getCount() == spinner.getSelectedItemPosition()) {
                            selected = spinner.getAdapter().getItem(spinner.getSelectedItemPosition() - 1);
                        } else {
                            selected = spinner.getSelectedItem();
                        }
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(formFragment.getCommonListener());
                        spinner.setTag(R.id.keys, options.first);
                        if (selected != null && newAdapterValues.contains(selected.toString())) {
                            spinner.setSelection(newAdapterValues.indexOf(selected.toString()));
                        }
                    }
                }
            }
        }
    }


}
