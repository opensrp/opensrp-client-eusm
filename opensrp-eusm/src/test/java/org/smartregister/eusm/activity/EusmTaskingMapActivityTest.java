package org.smartregister.eusm.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.cardview.widget.CardView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.eusm.BaseActivityUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.domain.EusmCardDetail;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EusmTaskingMapActivityTest extends BaseActivityUnitTest {

    private EusmTaskingMapActivity eusmTaskingMapActivity;

    private ActivityController<EusmTaskingMapActivity> controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(EusmTaskingMapActivity.class).create().start();
        eusmTaskingMapActivity = spy(controller.get());
    }

    @Test
    public void testOpenCardViewShouldOpenCardView() {
        CardView cardView = spy(eusmTaskingMapActivity.findViewById(R.id.card_view));
        ReflectionHelpers.setField(eusmTaskingMapActivity, "eusmCardView", cardView);
        assertNotNull(cardView);
        EusmCardDetail eusmCardDetail = new EusmCardDetail("");
        eusmCardDetail.setStructureType("Water Point");
        eusmCardDetail.setTaskStatus("in_progress");
        eusmCardDetail.setDistanceMeta("123.0 km");
        eusmCardDetail.setCommune("commune A");
        eusmTaskingMapActivity.openCardView(eusmCardDetail);
        verify(cardView).setVisibility(eq(View.VISIBLE));

    }

    @Test
    public void testOnClickViewInventoryShouldOpenTaskRegister() {
        EusmCardDetail eusmCardDetail = new EusmCardDetail("status");
        View view = spy(new View(RuntimeEnvironment.application));
        view.setTag(R.id.card_detail, eusmCardDetail);
        view.setId(R.id.btn_view_inventory);
        eusmTaskingMapActivity.onClick(view);
        verify(eusmTaskingMapActivity).startActivity(any(Intent.class));
    }

    @Override
    protected Activity getActivity() {
        return eusmTaskingMapActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }
}