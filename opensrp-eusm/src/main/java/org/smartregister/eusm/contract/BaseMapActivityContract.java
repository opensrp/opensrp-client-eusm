package org.smartregister.eusm.contract;

import android.content.Context;

import androidx.annotation.NonNull;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.smartregister.eusm.util.AppJsonFormUtils;
import org.smartregister.tasking.contract.BaseContract;

public interface BaseMapActivityContract {

    interface BaseMapActivityView {

        Context getContext();

        void setGeoJsonSource(@NonNull FeatureCollection featureCollection, Feature operationalArea, boolean changeMapPosition);

        void displaySelectedFeature(Feature feature, LatLng clickedPoint);

        void displaySelectedFeature(Feature feature, LatLng clickedPoint, double zoomLevel);

        void clearSelectedFeature();

        AppJsonFormUtils getJsonFormUtils();

        void focusOnUserLocation(boolean focusOnUserLocation);

        boolean isMyLocationComponentActive();
    }

    interface Presenter extends BaseContract.BasePresenter {

        Feature getSelectedFeature();

    }
}
