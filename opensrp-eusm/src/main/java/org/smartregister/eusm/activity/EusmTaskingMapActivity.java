package org.smartregister.eusm.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.eusm.config.ServicePointType;
import org.smartregister.eusm.domain.EusmCardDetail;
import org.smartregister.eusm.domain.StructureDetail;
import org.smartregister.eusm.presenter.EusmTaskingMapPresenter;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.tasking.activity.TaskingMapActivity;
import org.smartregister.tasking.contract.TaskingMapActivityContract;
import org.smartregister.tasking.model.CardDetails;
import org.smartregister.tasking.model.TaskFilterParams;
import org.smartregister.tasking.presenter.ValidateUserLocationPresenter;
import org.smartregister.util.Utils;

import timber.log.Timber;

public class EusmTaskingMapActivity extends TaskingMapActivity {

    private CardView eusmCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
    }

    public void setUpViews() {
        eusmCardView = findViewById(R.id.card_view);
        findViewById(R.id.filter_tasks_fab).setVisibility(View.GONE);
        ImageButton buttonSearchCancel = findViewById(R.id.btn_search_cancel);
        buttonSearchCancel.setVisibility(View.VISIBLE);
        buttonSearchCancel.setOnClickListener(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        super.onMapReady(mapboxMap);
        if (taskingMapPresenter != null) {
            ValidateUserLocationPresenter userLocationPresenter = taskingMapPresenter.getLocationPresenter();
            if (userLocationPresenter != null) {
                userLocationPresenter.requestUserLocation();
            }
        }
    }

    @Override
    public void positionMyLocationAndLayerSwitcher() {
        // Do nothing
    }

    @Override
    public TaskingMapActivityContract.Presenter getPresenter() {
        if (taskingMapPresenter == null) {
            taskingMapPresenter = new EusmTaskingMapPresenter(this, drawerView.getPresenter());
        }
        return taskingMapPresenter;
    }

    @Override
    public void openTaskRegister(TaskFilterParams filterParams) {
        //check if the location if fetched
        Location location = getUserCurrentLocation();
        if (location != null) {
            EusmApplication.getInstance().setUserLocation(location);
        } else {
            Utils.showToast(getApplicationContext(), getString(R.string.user_location_not_received));
        }
        super.openTaskRegister(filterParams);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        clearSelectedFeature();
        return super.onMapClick(point);
    }

    @Override
    public void openCardView(CardDetails cardDetails) {
        if (cardDetails != null) {
            EusmCardDetail eusmCardDetail = (EusmCardDetail) cardDetails;
            if (StringUtils.isBlank(eusmCardDetail.getCommune())) {
                clearSelectedFeature();
                Utils.showToast(getApplicationContext(), getString(R.string.text_selected_a_cluster_point));
            } else if (eusmCardView != null) {
                TextView structureNameView = eusmCardView.findViewById(R.id.txt_structure_name);
                TextView structureDistanceView = eusmCardView.findViewById(R.id.txt_distance);
                TextView structureCommuneView = eusmCardView.findViewById(R.id.txt_commune);
                TextView structureTaskStatusView = eusmCardView.findViewById(R.id.txt_task_status);
                ImageView imgServicePointType = eusmCardView.findViewById(R.id.img_service_point_type);
                int taskStatusColor = AppUtils.getColorByTaskStatus(eusmCardDetail.getTaskStatus());

                ServicePointType servicePointType = EusmApplication.getInstance()
                        .getServicePointKeyToType().get(eusmCardDetail.getStructureType().toLowerCase().replaceAll(" ", ""));
                if (servicePointType != null) {
                    imgServicePointType.setImageDrawable(ResourcesCompat.getDrawable(getResources(), servicePointType.drawableId, getBaseContext().getTheme()));
                    imgServicePointType.setColorFilter(ContextCompat.getColor(getApplicationContext(), taskStatusColor));
                }

//                imgServicePointType.setAlpha(0.9F);
                Button viewInventoryView = eusmCardView.findViewById(R.id.btn_view_inventory);
                viewInventoryView.setTag(R.id.card_detail, eusmCardDetail);
                viewInventoryView.setOnClickListener(this);
                structureNameView.setText(eusmCardDetail.getStructureName());
                structureDistanceView.setText(String.format(getString(R.string.distance_from_structure), eusmCardDetail.getStructureType(), eusmCardDetail.getDistanceMeta()));
                structureCommuneView.setText(eusmCardDetail.getCommune());
                structureTaskStatusView.setText(AppUtils.formatTaskStatus(eusmCardDetail.getTaskStatus(), this));
                structureTaskStatusView.setTextColor(ContextCompat.getColor(getApplicationContext(), taskStatusColor));

                View view = (View) eusmCardView.getParent();
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin = 25;
                layoutParams.rightMargin = 25;
                layoutParams.bottomMargin = 20;
                layoutParams.gravity = Gravity.BOTTOM;
                view.setLayoutParams(layoutParams);
                eusmCardView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        if (viewId == R.id.btn_view_inventory) {
            EusmCardDetail eusmCardDetail = (EusmCardDetail) v.getTag(R.id.card_detail);
            if (eusmCardDetail != null) {
                StructureDetail structureDetail = new StructureDetail();
                structureDetail.setCommune(eusmCardDetail.getCommune());
                structureDetail.setDistanceMeta(eusmCardDetail.getDistanceMeta());
                structureDetail.setStructureId(eusmCardDetail.getStructureId());
                structureDetail.setStructureType(eusmCardDetail.getStructureType());
                structureDetail.setTaskStatus(eusmCardDetail.getTaskStatus());
                structureDetail.setEntityName(eusmCardDetail.getStructureName());
                structureDetail.setParentId(eusmCardDetail.getCommuneId());

                Intent intent = new Intent(getActivity(), EusmTaskRegisterActivity.class);
                intent.putExtra(AppConstants.IntentData.STRUCTURE_DETAIL, structureDetail);
                startActivity(intent);
            }
        } else if (viewId == R.id.btn_search_cancel) {
            EditText searchView = findViewById(org.smartregister.tasking.R.id.edt_search);
            if (searchView != null) {
                searchView.setText("");
            }
        }
    }

    @Override
    public void clearSelectedFeature() {
        super.clearSelectedFeature();
        if (eusmCardView != null)
            eusmCardView.setVisibility(View.GONE);
    }

    @Override
    public void displaySelectedFeature(Feature feature, LatLng clickedPoint, double zoomLevel) {
        //TODO get a zoom level on selection
        adjustFocusPoint(clickedPoint);
        if (selectedGeoJsonSource != null) {
            selectedGeoJsonSource.setGeoJson(FeatureCollection.fromFeature(feature));
        }
    }

    @Override
    public Location getUserCurrentLocation() {
        try {
            return super.getUserCurrentLocation();
        } catch (NullPointerException e) {
            Timber.e(e);
            return null;
        }
    }

    @Override
    public void requestUserLocation() {
        kujakuMapView.setWarmGps(true, getString(R.string.location_service_disabled), getString(R.string.location_services_disabled_spray), () -> {
            myLocationButton.performClick();
        });
        hasRequestedLocation = true;
    }
}
