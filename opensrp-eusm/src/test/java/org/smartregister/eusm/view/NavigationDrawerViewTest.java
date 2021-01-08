package org.smartregister.eusm.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.tasking.contract.BaseDrawerContract;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class NavigationDrawerViewTest extends BaseUnitTest {

    private NavigationDrawerView navigationDrawerView;

    @Mock
    private BaseDrawerContract.DrawerActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        navigationDrawerView = spy(new NavigationDrawerView(activity));
    }

    @Test
    public void testOnClickOfLanguageChooserShouldOpenLanguageChooserPopUpMenu() {
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.btn_navMenu_language_chooser);
        TextView textView = new TextView(RuntimeEnvironment.application);
        ReflectionHelpers.setField(navigationDrawerView, "languageChooserTextView", textView);
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        doReturn(activity)
                .when(navigationDrawerView).getContext();
        navigationDrawerView.onClick(view);
        verify(navigationDrawerView).showLanguageChooser();
    }


    @Test
    public void testOnClickOfSyncButtonShouldStartSyncAndCloseDrawer() {
        View view = new View(RuntimeEnvironment.application);
        view.setId(R.id.sync_button);
        doNothing().when(navigationDrawerView).startImmediateSync();
        doNothing().when(navigationDrawerView).toggleProgressBarView(eq(true));
        navigationDrawerView.onClick(view);
        verify(navigationDrawerView).closeDrawerLayout();
        verify(navigationDrawerView).startImmediateSync();
        verify(navigationDrawerView).toggleProgressBarView(eq(true));
    }

}