package org.smartregister.eusm.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import androidx.core.util.Pair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.tasking.contract.BaseDrawerContract;

import java.util.ArrayList;

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

    @Test
    public void testShowOperationalAreaSelectorShouldShowTreeViewDialog() {
        String locationHierarchy = "[{\"key\":\"Madagascar\",\"level\":\"\",\"name\":\"Madagascar\",\"nodes\":[{\"key\":\"AMORON\\u0027I MANIA\",\"level\":\"\",\"name\":\"AMORON\\u0027I MANIA\",\"nodes\":[{\"key\":\"Ambositra\",\"level\":\"\",\"name\":\"Ambositra\",\"nodes\":[]}]},{\"key\":\"ANDROY\",\"level\":\"\",\"name\":\"ANDROY\",\"nodes\":[{\"key\":\"Bekily\",\"level\":\"\",\"name\":\"Bekily\",\"nodes\":[]}]},{\"key\":\"HAUTE MATSIATRA\",\"level\":\"\",\"name\":\"HAUTE MATSIATRA\",\"nodes\":[{\"key\":\"Ambohimahasoa\",\"level\":\"\",\"name\":\"Ambohimahasoa\",\"nodes\":[]}]},{\"key\":\"SOFIA\",\"level\":\"\",\"name\":\"SOFIA\",\"nodes\":[{\"key\":\"Analalava\",\"level\":\"\",\"name\":\"Analalava\",\"nodes\":[]}]},{\"key\":\"VAKINANKARATRA\",\"level\":\"\",\"name\":\"VAKINANKARATRA\",\"nodes\":[{\"key\":\"Ambatolampy\",\"level\":\"\",\"name\":\"Ambatolampy\",\"nodes\":[]}]}]}]";

        ArrayList<String> levels = new ArrayList<>();
        levels.add("Bekily");
        levels.add("ANDROY");
        levels.add("Madagascar");

        Activity activity = Robolectric.buildActivity(Activity.class).get();
        doReturn(activity).when(navigationDrawerView).getContext();

        navigationDrawerView.showOperationalAreaSelector(Pair.create(locationHierarchy, levels));

        Dialog dialog = ShadowAlertDialog.getLatestDialog();
        Assert.assertNotNull(dialog);
        Assert.assertTrue(dialog instanceof EusmTreeViewDialog);
    }
}