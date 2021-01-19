package org.smartregister.eusm.presenter;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.tasking.contract.BaseDrawerContract;

import java.util.ArrayList;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class EusmBaseDrawerPresenterTest extends BaseUnitTest {

    private EusmBaseDrawerPresenter eusmBaseDrawerPresenter;
    @Mock
    private BaseDrawerContract.View view;

    @Mock
    private BaseDrawerContract.DrawerActivity drawerActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Activity activity = Robolectric.buildActivity(Activity.class).get();
        doReturn(activity).when(view).getContext();

        eusmBaseDrawerPresenter = spy(new EusmBaseDrawerPresenter(view, drawerActivity));
    }

    @Test
    public void testOnPlanSelectorClickedShouldLockNavigationDrawer() {
        eusmBaseDrawerPresenter.onPlanSelectorClicked(new ArrayList<>(), new ArrayList<>());
        verify(view).lockNavigationDrawerForSelection();
    }
}