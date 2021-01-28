package org.smartregister.eusm.presenter;

import android.app.Activity;

import androidx.core.util.Pair;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.annotation.LooperMode;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Location;
import org.smartregister.domain.PlanDefinitionSearch;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.eusm.BaseUnitTest;
import org.smartregister.eusm.R;
import org.smartregister.eusm.application.EusmApplication;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionSearchRepository;
import org.smartregister.tasking.contract.BaseDrawerContract;
import org.smartregister.tasking.util.PreferencesUtil;
import org.smartregister.tasking.util.TaskingConstants;
import org.smartregister.util.AppProperties;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@LooperMode(PAUSED)
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

    @Test
    public void testOnShowOperationalAreaSelectorShouldInvokeShowIfLocationHierarchyIsPresent() throws InterruptedException {
        String planId = UUID.randomUUID().toString();
        PreferencesUtil preferencesUtil = mock(PreferencesUtil.class);
        doReturn(planId).when(preferencesUtil).getCurrentPlanId();

        AppProperties mockAppProperties = mock(AppProperties.class);
        doReturn(true).when(mockAppProperties).getPropertyBoolean(eq(TaskingConstants.CONFIGURATION.SELECT_PLAN_THEN_AREA));
        Context spyOpensrpContext = spy(CoreLibrary.getInstance().context());
        doReturn(mockAppProperties).when(spyOpensrpContext).getAppProperties();

        ReflectionHelpers.setField(eusmBaseDrawerPresenter, "prefsUtil", preferencesUtil);

        ReflectionHelpers.setField(CoreLibrary.getInstance(), "context", spyOpensrpContext);

        Pair<String, ArrayList<String>> locationHierarchy = Pair.create("", new ArrayList<String>());
        doReturn(Pair.create("", new ArrayList<String>())).when(eusmBaseDrawerPresenter).extractLocationHierarchy();
        eusmBaseDrawerPresenter.onShowOperationalAreaSelector();
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(view).showOperationalAreaSelector(eq(locationHierarchy));
    }

    @Test
    public void testOnShowOperationalAreaSelectorShouldDisplayErrorIfLocationHierarchyIsNull() throws InterruptedException {
        String planId = UUID.randomUUID().toString();
        PreferencesUtil preferencesUtil = mock(PreferencesUtil.class);
        doReturn(planId).when(preferencesUtil).getCurrentPlanId();

        AppProperties mockAppProperties = mock(AppProperties.class);
        doReturn(true).when(mockAppProperties).getPropertyBoolean(eq(TaskingConstants.CONFIGURATION.SELECT_PLAN_THEN_AREA));
        Context spyOpensrpContext = spy(CoreLibrary.getInstance().context());
        doReturn(mockAppProperties).when(spyOpensrpContext).getAppProperties();

        ReflectionHelpers.setField(eusmBaseDrawerPresenter, "prefsUtil", preferencesUtil);

        ReflectionHelpers.setField(CoreLibrary.getInstance(), "context", spyOpensrpContext);

        doReturn(null).when(eusmBaseDrawerPresenter).extractLocationHierarchy();
        eusmBaseDrawerPresenter.onShowOperationalAreaSelector();
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(view).displayNotification(eq(R.string.error_fetching_location_hierarchy_title), eq(R.string.error_fetching_location_hierarchy));
    }

    @Test
    public void testOnShowOperationalAreaSelectorShouldDisplayErrorIfMissionIsNotSelected() throws InterruptedException {
        PreferencesUtil preferencesUtil = mock(PreferencesUtil.class);
        doReturn("").when(preferencesUtil).getCurrentPlanId();

        AppProperties mockAppProperties = mock(AppProperties.class);
        doReturn(true).when(mockAppProperties).isTrue(eq(TaskingConstants.CONFIGURATION.SELECT_PLAN_THEN_AREA));
        Context spyOpensrpContext = spy(CoreLibrary.getInstance().context());
        doReturn(mockAppProperties).when(spyOpensrpContext).getAppProperties();

        ReflectionHelpers.setField(eusmBaseDrawerPresenter, "prefsUtil", preferencesUtil);

        ReflectionHelpers.setField(CoreLibrary.getInstance(), "context", spyOpensrpContext);

        doReturn(null).when(eusmBaseDrawerPresenter).extractLocationHierarchy();
        eusmBaseDrawerPresenter.onShowOperationalAreaSelector();
        shadowOf(getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(view).displayNotification(eq(R.string.campaign), eq(R.string.plan_not_selected));
    }

    @Test
    public void testGetEntireTreeShouldFilterOutDistrictNotInPlan() {
        String jurisdictionId = "34324";
        String planId = UUID.randomUUID().toString();
        PreferencesUtil preferencesUtil = mock(PreferencesUtil.class);
        doReturn(planId).when(preferencesUtil).getCurrentPlanId();

        ReflectionHelpers.setField(eusmBaseDrawerPresenter, "prefsUtil", preferencesUtil);

        String strLocation = "[{\"type\":\"Feature\",\"id\":\"f3199af5-2eaf-46df-87c9-40d59606a2fb\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.54933,-16.08306]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"8e74d042-4a71-4694-a652-bc3ba6369101\",\"name\":\"SOANIERANA IVONGO\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18479},{\"type\":\"Feature\",\"id\":\"b8a7998c-5df6-49eb-98e6-f0675db71848\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[49.58436,-16.433]},\"properties\":{\"type\":\"Water Point\",\"status\":\"Active\",\"parentId\":\"663d7935-35e7-4ccf-aaf5-6e16f2042570\",\"name\":\"FENERIVE EST\",\"geographicLevel\":0,\"version\":0,\"AdminLevelTag\":\"Commune\"},\"serverVersion\":18480}]";
        List<Location> locations = JsonFormUtils.gson.fromJson(strLocation, new TypeToken<List<Location>>() {
        }.getType());
        LocationRepository spyLocationRepository = spy(DrishtiApplication.getInstance().getContext().getLocationRepository());
        doReturn(locations).when(spyLocationRepository).getLocationsByIds(Collections.singletonList(jurisdictionId));

        Context spyOpensrpContext = spy(CoreLibrary.getInstance().context());
        doReturn(spyLocationRepository).when(spyOpensrpContext).getLocationRepository();

        ReflectionHelpers.setField(DrishtiApplication.getInstance(), "context", spyOpensrpContext);


        String strFormLocation = "[{\"key\":\"Madagascar\",\"level\":\"\",\"name\":\"Madagascar\",\"nodes\":[{\"key\":\"ANALANJIROFO\",\"level\":\"\",\"name\":\"ANALANJIROFO\",\"nodes\":[{\"key\":\"FENERIVE EST\",\"level\":\"\",\"name\":\"FENERIVE EST\",\"nodes\":[]},{\"key\":\"MANANARA AVARATRA\",\"level\":\"\",\"name\":\"MANANARA AVARATRA\",\"nodes\":[]},{\"key\":\"SOANIERANA IVONGO\",\"level\":\"\",\"name\":\"SOANIERANA IVONGO\",\"nodes\":[]},{\"key\":\"TRINIDAD IVONGO\",\"level\":\"\",\"name\":\"TRINIDAD IVONGO\",\"nodes\":[]}]}]}]";
        List<FormLocation> entireTree = JsonFormUtils.gson.fromJson(strFormLocation, new TypeToken<List<FormLocation>>() {
        }.getType());

        PlanDefinitionSearch planDefinitionSearch = new PlanDefinitionSearch();
        planDefinitionSearch.setJurisdictionId(jurisdictionId);
        PlanDefinitionSearchRepository mockPlanDefinitionSearchRepository = mock(PlanDefinitionSearchRepository.class);
        doReturn(Collections.singletonList(planDefinitionSearch)).when(mockPlanDefinitionSearchRepository).findPlanDefinitionSearchByPlanId(eq(planId));

        ReflectionHelpers.setField(EusmApplication.getInstance(), "planDefinitionSearchRepository", mockPlanDefinitionSearchRepository);
        String result = eusmBaseDrawerPresenter.getEntireTree(entireTree);
        List<FormLocation> entireTreeResult = JsonFormUtils.gson.fromJson(result, new TypeToken<List<FormLocation>>() {
        }.getType());

        assertNotNull(result);
        assertNotNull(entireTreeResult);

        assertEquals(2, entireTreeResult.get(0).nodes.get(0).nodes.size());
        assertEquals("FENERIVE EST", entireTreeResult.get(0).nodes.get(0).nodes.get(0).name);
        assertEquals("SOANIERANA IVONGO", entireTreeResult.get(0).nodes.get(0).nodes.get(1).name);
    }
}