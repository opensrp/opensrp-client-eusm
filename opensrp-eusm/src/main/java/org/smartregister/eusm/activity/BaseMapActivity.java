package org.smartregister.eusm.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.pluginscalebar.ScaleBarOptions;
import com.mapbox.pluginscalebar.ScaleBarPlugin;

import org.json.JSONException;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.BaseMapActivityContract;
import org.smartregister.eusm.util.AppConstants;
import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.eusm.util.AppMapHelper;
import org.smartregister.eusm.util.AppUtils;
import org.smartregister.eusm.view.AppMapView;
import org.smartregister.view.activity.MultiLanguageActivity;

import io.ona.kujaku.callbacks.OnLocationComponentInitializedCallback;
import io.ona.kujaku.layers.BoundaryLayer;
import timber.log.Timber;

import static org.smartregister.eusm.util.AppConstants.ANIMATE_TO_LOCATION_DURATION;
import static org.smartregister.eusm.util.AppConstants.VERTICAL_OFFSET;
import static org.smartregister.eusm.util.AppUtils.getPixelsPerDPI;

public abstract class BaseMapActivity extends MultiLanguageActivity implements
        OnLocationComponentInitializedCallback,
        MapboxMap.OnMapClickListener,
        MapboxMap.OnMapLongClickListener,
        OnMapReadyCallback, BaseMapActivityContract.BaseMapActivityView, MapboxMap.OnMoveListener {

    private AppMapView mapView;

    private MapboxMap mapboxMap;

    private ImageButton myLocationButton;

    private ImageButton layerSwitcherFab;

    private GeoJsonSource geoJsonSource;

    private GeoJsonSource selectedGeoJsonSource;

    private CardView potentialAreaOfTransmissionCardView;

    private CardView indicatorsCardView;

    private AppMapHelper appMapHelper;

    private AppJsonFormUtils jsonFormUtils;

    private BoundaryLayer boundaryLayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setUpViews();
        initializeMapView(savedInstanceState);
        appMapHelper = new AppMapHelper();
        jsonFormUtils = new AppJsonFormUtils();
    }

    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    protected void setUpViews() {
        mapView = findViewById(R.id.kujakuMapView);

        myLocationButton = findViewById(R.id.ib_mapview_focusOnMyLocationIcon);

        layerSwitcherFab = findViewById(R.id.fab_mapview_layerSwitcher);
    }

    protected void initializeMapView(Bundle savedInstanceState) {

        mapView.getMapboxLocationComponentWrapper()
                .setOnLocationComponentInitializedCallback(this);

        mapView.onCreate(savedInstanceState);

        mapView.showCurrentLocationBtn(isCurrentLocationButtonEnabled());

        mapView.setDisableMyLocationOnMapMove(isDisableMyLocationOnMapMove());

        Float locationBufferRadius = getLocationBuffer();

        mapView.setLocationBufferRadius(locationBufferRadius / getPixelsPerDPI(getResources()));

        mapView.getMapAsync(this);
    }

    public AppMapView getMapView() {
        return mapView;
    }

    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }

    public void setMapboxMap(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
    }

    protected boolean isCurrentLocationButtonEnabled() {
        return true;
    }

    protected boolean isDisableMyLocationOnMapMove() {
        return true;
    }

    protected boolean shouldEnableCompass() {
        return true;
    }

    protected Float getLocationBuffer() {
        return AppUtils.getLocationBuffer();
    }

    protected void enableCompass(MapboxMap mapboxMap) {
        UiSettings uiSettings = mapboxMap.getUiSettings();
        uiSettings.setCompassGravity(Gravity.START | Gravity.TOP);
        uiSettings.setCompassMargins(getResources().getDimensionPixelSize(R.dimen.compass_left_margin),
                getResources().getDimensionPixelSize(R.dimen.compass_top_margin), 0, 0);
        uiSettings.setCompassFadeFacingNorth(false);
        uiSettings.setCompassEnabled(true);
    }

    private void positionMyLocationAndLayerSwitcher(FrameLayout.LayoutParams myLocationButtonParams, int bottomMargin) {
        if (myLocationButton != null) {
            myLocationButtonParams.gravity = Gravity.BOTTOM | Gravity.END;
            myLocationButtonParams.bottomMargin = bottomMargin;
            myLocationButtonParams.topMargin = 0;
            myLocationButton.setLayoutParams(myLocationButtonParams);
        }
    }

    private BoundaryLayer createBoundaryLayer(Feature operationalArea) {
        return new BoundaryLayer.Builder(FeatureCollection.fromFeature(operationalArea))
                .setLabelProperty(AppConstants.Map.NAME_PROPERTY)
                .setLabelTextSize(getResources().getDimension(R.dimen.operational_area_boundary_text_size))
                .setLabelColorInt(Color.WHITE)
                .setBoundaryColor(Color.WHITE)
                .setBoundaryWidth(getResources().getDimension(R.dimen.operational_area_boundary_width)).build();
    }

    @Override
    public void focusOnUserLocation(boolean focusOnUserLocation) {
        getMapView().focusOnUserLocation(focusOnUserLocation, RenderMode.COMPASS);
    }

    @Override
    public boolean isMyLocationComponentActive() {
        return getAppMapHelper().isMyLocationComponentActive(this, myLocationButton);
    }

    public void positionMyLocationAndLayerSwitcher() {
        FrameLayout.LayoutParams myLocationButtonParams = (FrameLayout.LayoutParams) myLocationButton.getLayoutParams();
        positionMyLocationAndLayerSwitcher(myLocationButtonParams, myLocationButtonParams.topMargin);
    }

    protected void initializeScaleBarPlugin(MapboxMap mapboxMap) {
        if (shouldDisplayDistanceScale()) {
            ScaleBarPlugin scaleBarPlugin = new ScaleBarPlugin(getMapView(), mapboxMap);
            // Create a ScaleBarOptions object to use custom styling
            ScaleBarOptions scaleBarOptions = new ScaleBarOptions(getApplicationContext());
            scaleBarOptions.setTextColor(R.color.distance_scale_text);
            scaleBarOptions.setTextSize(R.dimen.distance_scale_text_size);
            scaleBarPlugin.create(scaleBarOptions);
        }
    }

    protected boolean shouldDisplayDistanceScale() {
        return AppUtils.displayDistanceScale();
    }

    @Override
    public void onLocationComponentInitialized() {
        //Do nothing
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {
        return false;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        mapboxMap.addOnMapClickListener(this);

        mapboxMap.addOnMapLongClickListener(this);

        mapboxMap.addOnMoveListener(this);

        Style.Builder builder = new Style.Builder().fromUri(getContext().getString(R.string.reveal_satellite_style));

        mapboxMap.setStyle(builder, style -> {
            GeoJsonSource geoJsonSource = style.getSourceAs(getString(R.string.reveal_datasource_name));

            // selectedGeoJsonSource = style.getSourceAs(getString(R.string.selected_datasource_name));

            AppMapHelper.addCustomLayers(style, BaseMapActivity.this);

            AppMapHelper.addBaseLayers(mapView, style, BaseMapActivity.this);

            //initializeScaleBarPlugin(mapboxMap);
        });

        if (shouldEnableCompass()) {
            enableCompass(mapboxMap);
        }
        setMapboxMap(mapboxMap);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setGeoJsonSource(@NonNull FeatureCollection featureCollection, Feature operationalArea, boolean isChangeMapPosition) {
//        if (geoJsonSource != null) {
//            geoJsonSource.setGeoJson(featureCollection);
//            if (operationalArea != null) {
//                CameraPosition cameraPosition = getMapboxMap().getCameraForGeometry(operationalArea.geometry());
//
//                if (cameraPosition != null && (boundaryLayer == null || isChangeMapPosition)) {
//                    getMapboxMap().setCameraPosition(cameraPosition);
//                }
//
//                Boolean drawOperationalAreaBoundaryAndLabel = getDrawOperationalAreaBoundaryAndLabel();
//                if (drawOperationalAreaBoundaryAndLabel) {
//                    if (boundaryLayer == null) {
//                        boundaryLayer = createBoundaryLayer(operationalArea);
//                        getMapView().addLayer(boundaryLayer);
//
//                        getMapView().setOnFeatureLongClickListener(new OnFeatureLongClickListener() {
//                            @Override
//                            public void onFeatureLongClick(List<Feature> features) {
////                                homeActivityPresenter.onFociBoundaryLongClicked();
//                            }
//                        }, boundaryLayer.getLayerIds());
//
//                    } else {
//                        boundaryLayer.updateFeatures(FeatureCollection.fromFeature(operationalArea));
//                    }
//                }
//
//                appMapHelper.updateIndexCaseLayers(getMapboxMap(), featureCollection, this);
//            }
//
//        }
    }

    @Override
    public AppJsonFormUtils getJsonFormUtils() {
        return jsonFormUtils;
    }

    @Override
    public void displaySelectedFeature(Feature feature, LatLng point) {
        displaySelectedFeature(feature, point, getMapboxMap().getCameraPosition().zoom);
    }

    @Override
    public void displaySelectedFeature(Feature feature, LatLng clickedPoint, double zoomLevel) {
        adjustFocusPoint(clickedPoint);
        getMapView().centerMap(clickedPoint, ANIMATE_TO_LOCATION_DURATION, zoomLevel);
        if (selectedGeoJsonSource != null) {
            selectedGeoJsonSource.setGeoJson(FeatureCollection.fromFeature(feature));
        }
    }

    protected void adjustFocusPoint(LatLng point) {
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL || screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            point.setLatitude(point.getLatitude() + VERTICAL_OFFSET);
        }
    }

    @Override
    public void clearSelectedFeature() {
        if (selectedGeoJsonSource != null) {
            try {
                selectedGeoJsonSource.setGeoJson(new com.cocoahero.android.geojson.FeatureCollection().toJSON().toString());
            } catch (JSONException e) {
                Timber.e(e, "Error clearing selected feature");
            }
        }
    }

    protected Location getUserCurrentLocation() {
        return getMapView().getLocationClient() == null ? null : getMapView().getLocationClient().getLastLocation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getMapView() != null) {
            getMapView().onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getMapView() != null)
            getMapView().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getMapView() != null)
            getMapView().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getMapView() != null)
            getMapView().onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getMapView() != null)
            getMapView().onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (getMapView() != null)
            getMapView().onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getMapView() != null)
            getMapView().onDestroy();
    }

    @Override
    protected void attachBaseContext(Context base) {
//        LangUtils.saveLanguage(base.getApplicationContext(), "en");
        super.attachBaseContext(base);
    }

    public AppMapHelper getAppMapHelper() {
        return appMapHelper;
    }

    @Override
    public void onMoveBegin(@NonNull MoveGestureDetector detector) {

    }

    @Override
    public void onMove(@NonNull MoveGestureDetector detector) {

    }

    @Override
    public void onMoveEnd(@NonNull MoveGestureDetector detector) {

    }
}
