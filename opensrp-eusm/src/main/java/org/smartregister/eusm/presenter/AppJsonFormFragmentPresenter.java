package org.smartregister.eusm.presenter;

import android.content.Intent;
import android.location.Location;

import androidx.appcompat.app.AlertDialog;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.eusm.activity.AppJsonFormActivity;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.tasking.contract.UserLocationContract;
import org.smartregister.tasking.presenter.ValidateUserLocationPresenter;
import org.smartregister.tasking.util.LocationUtils;
import org.smartregister.tasking.view.TaskingMapView;

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

}
