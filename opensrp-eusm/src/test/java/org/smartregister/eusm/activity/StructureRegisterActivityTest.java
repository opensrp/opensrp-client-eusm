package org.smartregister.eusm.activity;

import android.app.Activity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.Context;
import org.smartregister.eusm.BaseActivityUnitTest;
import org.smartregister.eusm.R;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.spy;

public class StructureRegisterActivityTest extends BaseActivityUnitTest {

    private StructureRegisterActivity structureRegisterActivity;

    private ActivityController<StructureRegisterActivity> controller;

    @Before
    public void setUp() {
        Context.bindtypes = new ArrayList<>();
        controller = Robolectric.buildActivity(StructureRegisterActivity.class).create().start();
        structureRegisterActivity = spy(controller.get());
    }

    @Test
    public void testRegisterBottomNavigationShouldNotBeVisible() {
        assertFalse(structureRegisterActivity.findViewById(R.id.bottom_navigation).isShown());
    }

    @Override
    protected Activity getActivity() {
        return structureRegisterActivity;
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