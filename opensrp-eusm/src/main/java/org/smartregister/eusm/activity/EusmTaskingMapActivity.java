package org.smartregister.eusm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.geometry.LatLng;

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

public class EusmTaskingMapActivity extends TaskingMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.filter_tasks_fab).setVisibility(View.GONE);
    }

    @Override
    public TaskingMapActivityContract.Presenter getPresenter() {
        return new EusmTaskingMapPresenter(this, drawerView.getPresenter());
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
            CardView cardView = findViewById(R.id.card_view);
            TextView structureNameView = cardView.findViewById(R.id.txt_structure_name);
            TextView structureDistanceView = cardView.findViewById(R.id.txt_distance);
            TextView structureCommuneView = cardView.findViewById(R.id.txt_commune);
            TextView structureTaskStatusView = cardView.findViewById(R.id.txt_task_status);
            ImageView imgServicePointType = cardView.findViewById(R.id.img_service_point_type);

            ServicePointType servicePointType = EusmApplication.getInstance().getServicePointKeyToType().get(((EusmCardDetail) cardDetails).getStructureType()
                    .toLowerCase().replaceAll(" ", ""));
            if (servicePointType != null) {
                imgServicePointType.setImageDrawable(ResourcesCompat.getDrawable(getResources(), servicePointType.drawableId, getBaseContext().getTheme()));
            }

            imgServicePointType.setAlpha(0.4F);

            Button viewInventoryView = cardView.findViewById(R.id.btn_view_inventory);
            viewInventoryView.setTag(R.id.card_detail, eusmCardDetail);
            viewInventoryView.setOnClickListener(this);
            structureNameView.setText(eusmCardDetail.getStructureName());
            structureDistanceView.setText(String.format(getString(R.string.distance_from_structure), eusmCardDetail.getStructureType(), eusmCardDetail.getDistanceMeta()));
            structureCommuneView.setText(eusmCardDetail.getCommune());
            structureTaskStatusView.setText(AppUtils.formatTaskStatus(eusmCardDetail.getTaskStatus(), getApplicationContext()));

            View view = (View) cardView.getParent();
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 25;
            layoutParams.rightMargin = 25;
            layoutParams.bottomMargin = 20;
            layoutParams.gravity = Gravity.BOTTOM;
            view.setLayoutParams(layoutParams);
            cardView.setVisibility(View.VISIBLE);
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
                structureDetail.setStructureName(eusmCardDetail.getStructureName());

                Intent intent = new Intent(getActivity(), EusmTaskRegisterActivity.class);
                intent.putExtra(AppConstants.IntentData.STRUCTURE_DETAIL, structureDetail);
                getActivity().startActivity(intent);
            }
        }
    }

    @Override
    public void clearSelectedFeature() {
        super.clearSelectedFeature();
        findViewById(R.id.card_view).setVisibility(View.GONE);
    }

    @Override
    public void displaySelectedFeature(Feature feature, LatLng clickedPoint, double zoomLevel) {
        //TODO get a zoom level on selection
        adjustFocusPoint(clickedPoint);
        if (selectedGeoJsonSource != null) {
            selectedGeoJsonSource.setGeoJson(FeatureCollection.fromFeature(feature));
        }
    }
}
