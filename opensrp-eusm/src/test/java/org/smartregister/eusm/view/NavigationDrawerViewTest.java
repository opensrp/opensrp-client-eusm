package org.smartregister.eusm.view;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.contract.BaseDrawerContract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Richard Kareko on 4/9/20.
 */

public class NavigationDrawerViewTest extends BaseUnitTest {

    private final Context context = RuntimeEnvironment.application;
    private final AppCompatActivity mockActivity = mock(AppCompatActivity.class);
    private final ProgressBar progress = new ProgressBar(context);
    private final TextView progressLabel = new TextView(context);
    private final TextView syncButton = new TextView(context);
    private final TextView syncLabel = new TextView(context);
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    private DrawerLayout mDrawerLayout;
    @Mock
    private BaseDrawerContract.DrawerActivity activity;
    @Mock
    private TextView planTextView;
    @Mock
    private TextView operationalAreaTextView;
    @Mock
    private BaseDrawerContract.Presenter presenter;
    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    private NavigationDrawerView navigationDrawerView;

    @Before
    public void setUp() {
        navigationDrawerView = new NavigationDrawerView(activity);
    }

    @Test
    public void testDrawerMenuViewIsCreated() {
        assertNotNull(navigationDrawerView);
        assertNotNull(Whitebox.getInternalState(navigationDrawerView, "activity"));
        assertNotNull(Whitebox.getInternalState(navigationDrawerView, "presenter"));
    }

    @Test
    public void testSetPlan() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "planTextView", planTextView);

        navigationDrawerView.setPlan("FI plan");
        verify(planTextView).setText(stringArgumentCaptor.capture());
        assertEquals("FI plan", stringArgumentCaptor.getValue());
    }

    @Test
    public void testGetPlan() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "planTextView", planTextView);
        when(planTextView.getText()).thenReturn("FI plan");

        String actualPlan = navigationDrawerView.getPlan();
        assertEquals("FI plan", actualPlan);
    }

    @Test
    public void testSetOperationalArea() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "operationalAreaTextView", operationalAreaTextView);

        navigationDrawerView.setOperationalArea("Akros_1");
        verify(operationalAreaTextView).setText(stringArgumentCaptor.capture());
        assertEquals("Akros_1", stringArgumentCaptor.getValue());
    }

    @Test
    public void testGetOperationalArea() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "operationalAreaTextView", operationalAreaTextView);
        when(operationalAreaTextView.getText()).thenReturn("Akros_1");

        String actualOperationalArea = navigationDrawerView.getOperationalArea();
        assertEquals("Akros_1", actualOperationalArea);
    }

    @Test
    public void testLockNavigationDrawerForSelection() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "mDrawerLayout", mDrawerLayout);

        navigationDrawerView.lockNavigationDrawerForSelection();
        verify(mDrawerLayout).openDrawer(GravityCompat.START);
        verify(mDrawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }

    @Test
    public void testUnlockNavigationDrawer() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "mDrawerLayout", mDrawerLayout);
        when(mDrawerLayout.getDrawerLockMode(GravityCompat.START)).thenReturn(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

        navigationDrawerView.unlockNavigationDrawer();
        verify(mDrawerLayout).closeDrawer(GravityCompat.START);
        verify(mDrawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Test
    public void testOpenDrawerLayout() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "mDrawerLayout", mDrawerLayout);

        navigationDrawerView.openDrawerLayout();
        verify(mDrawerLayout).openDrawer(GravityCompat.START);
    }

    @Test
    public void testCloseDrawerLayout() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "mDrawerLayout", mDrawerLayout);
        Whitebox.setInternalState(navigationDrawerView, "presenter", presenter);
        when(presenter.isPlanAndOperationalAreaSelected()).thenReturn(true);

        navigationDrawerView.closeDrawerLayout();
        verify(mDrawerLayout).closeDrawer(GravityCompat.START);
    }

    @Test
    public void testOpAreaSelectorOnClick() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "presenter", presenter);
        View opAreaSelectoreView = new View(context);
        opAreaSelectoreView.setId(R.id.operational_area_selector);

        navigationDrawerView.onClick(opAreaSelectoreView);
        verify(presenter).onShowOperationalAreaSelector();
    }

    @Test
    public void testPlanSelectorOnClick() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "presenter", presenter);
        View planSelectoreView = new View(context);
        planSelectoreView.setId(R.id.plan_selector);

        navigationDrawerView.onClick(planSelectoreView);
        verify(presenter).onShowPlanSelector();
    }

    @Test
    public void testOfflineMapsButtonOnclick() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "presenter", presenter);
        View offlineMapsButton = new View(context);
        offlineMapsButton.setId(R.id.btn_navMenu_offline_maps);

        navigationDrawerView.onClick(offlineMapsButton);
        verify(presenter).onShowOfflineMaps();
    }

    @Test
    public void testOnResume() {
        navigationDrawerView = spy(navigationDrawerView);
        Whitebox.setInternalState(navigationDrawerView, "presenter", presenter);

        navigationDrawerView.onResume();
        verify(presenter).onViewResumed();
    }

    @Test
    public void testToggleProgressBarViewTrue() {
        doReturn(mockActivity).when(activity).getActivity();
        doReturn(progress).when(mockActivity).findViewById(eq(R.id.sync_progress_bar));
        doReturn(progressLabel).when(mockActivity).findViewById(eq(R.id.sync_progress_bar_label));
        doReturn(syncButton).when(mockActivity).findViewById(eq(R.id.sync_button));
        doReturn(syncLabel).when(mockActivity).findViewById(eq(R.id.sync_label));

        navigationDrawerView.toggleProgressBarView(true);

        assertEquals(progress.getVisibility(), View.VISIBLE);
        assertEquals(progressLabel.getVisibility(), View.VISIBLE);
        assertEquals(syncButton.getVisibility(), View.INVISIBLE);
        assertEquals(syncLabel.getVisibility(), View.INVISIBLE);
    }

    @Test
    public void testToggleProgressBarViewFalse() {
        doReturn(mockActivity).when(activity).getActivity();
        doReturn(progress).when(mockActivity).findViewById(eq(R.id.sync_progress_bar));
        doReturn(progressLabel).when(mockActivity).findViewById(eq(R.id.sync_progress_bar_label));
        doReturn(syncButton).when(mockActivity).findViewById(eq(R.id.sync_button));
        doReturn(syncLabel).when(mockActivity).findViewById(eq(R.id.sync_label));

        navigationDrawerView.toggleProgressBarView(false);

        assertEquals(progress.getVisibility(), View.INVISIBLE);
        assertEquals(progressLabel.getVisibility(), View.INVISIBLE);
        assertEquals(syncButton.getVisibility(), View.VISIBLE);
        assertEquals(syncLabel.getVisibility(), View.VISIBLE);
    }

//    @Test
//    public void testLockNavigationDrawerForSelectionShouldDisplayAlertDialog() {
//        navigationDrawerView = spy(navigationDrawerView);
//        AppCompatActivity appCompatActivity = Robolectric.buildActivity(AppCompatActivity.class).create().get();
//        when(navigationDrawerView.getContext()).thenReturn(appCompatActivity);
//        Whitebox.setInternalState(navigationDrawerView, "mDrawerLayout", mDrawerLayout);
//
//        navigationDrawerView.lockNavigationDrawerForSelection(R.string.select_mission_operational_area_title, R.string.select_mission_operational_area_title);
//
//        verify(mDrawerLayout).openDrawer(GravityCompat.START);
//        verify(mDrawerLayout).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
//    }

}
