package org.smartregister.eusm.activity;

import android.app.Activity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.eusm.BaseActivityUnitTest;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

public class EusmOfflineMapsActivityTest extends BaseActivityUnitTest {

    private EusmOfflineMapsActivity eusmOfflineMapsActivity;

    private ActivityController<EusmOfflineMapsActivity> controller;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(EusmOfflineMapsActivity.class);
        eusmOfflineMapsActivity = spy(controller.get());
    }

    @Test
    public void testGetMapStyleAssetPathShouldNotBeNull() {
        assertNotNull(eusmOfflineMapsActivity.getMapStyleAssetPath());
    }

    @Test
    public void testGetAvailableOfflineMapsFragmentShouldNotBeNull() {
        assertNotNull(eusmOfflineMapsActivity.getAvailableOfflineMapsFragment());
    }

    @Override
    protected Activity getActivity() {
        return eusmOfflineMapsActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @After
    public void tearDown(){
        destroyController();
    }
}